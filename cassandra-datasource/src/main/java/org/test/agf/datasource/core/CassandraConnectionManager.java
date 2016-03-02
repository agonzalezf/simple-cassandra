package org.test.agf.datasource.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.datastax.driver.core.Cluster;
import com.datastax.driver.core.Host;
import com.datastax.driver.core.Metadata;
import com.datastax.driver.core.ProtocolVersion;
import com.datastax.driver.core.QueryOptions;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.exceptions.DriverException;

public class CassandraConnectionManager {
	
	private static final Logger log = LoggerFactory.getLogger(CassandraConnectionManager.class);
	
	protected final LRUCache activeSessions = new LRUCache(20);	
	protected CassandraConnection connectionParams = null;
	protected Cluster cluster = null;
	
	public CassandraConnectionManager() {
		super();
		// By default, the specific values are loaded from the external config
		// file in the CassandraConnection object constructor
		this.connectionParams = new CassandraConnection();
	}
	
	public CassandraConnectionManager(CassandraConnection conn) {
		super();
		this.connectionParams = conn;
	}
	
	public Session getSession() {
		return getSession(this.connectionParams.getQueryOpts(), this.connectionParams);
	}
	
	public synchronized Session getSession(QueryOptions qOpts, CassandraConnection connectionParams) {
		String connId = getSessionKey(connectionParams);
		Session ref = activeSessions.get(connId);
		if (ref == null) {
			ref = createConnection(qOpts, connectionParams);
			activeSessions.put(connId, ref);
		}
		return ref;
	}
	
	public synchronized void closeSession() {
		String connId = getSessionKey(connectionParams);
		Session ref = activeSessions.get(connId);
		if (ref != null) {
			activeSessions.remove(ref);
			ref.close();
		}
	}
	
	protected Session createConnection(QueryOptions qOpts, CassandraConnection connectionParams) {
		Session ret = null;
		try {
			if (cluster == null) {
				long begin = System.currentTimeMillis();
				cluster = Cluster.builder().withPort(connectionParams.getPort())
					.withProtocolVersion(ProtocolVersion.V2)
					.addContactPoints(connectionParams.getNodes())
		            .withReconnectionPolicy(connectionParams.getReconnectionPolicy())
		            .withQueryOptions(qOpts).build();
				Metadata metadata = cluster.getMetadata();
				log.info("Connected to cluster: " +  metadata.getClusterName());
			    for (Host host : metadata.getAllHosts() ) {
			    	log.info("Datatacenter: " + host.getDatacenter() + " ; Host: " + host.getAddress() + "; Rack: " + host.getRack());
			    }
			    log.info("Create connection in (" + (System.currentTimeMillis() - begin) + " ms)");
			}
			long begin = System.currentTimeMillis();
			ret = cluster.connect(connectionParams.getKeyspace());
			log.info("Create session in (" + (System.currentTimeMillis() - begin) + " ms)");
		} catch (DriverException die) {
			if (ret != null) {
				ret.close();
			}
			if (this.cluster != null) {
				this.cluster.close();
			}
			this.cluster = null;
			throw die;
		}
		return ret;
	}

	private String getSessionKey(CassandraConnection connectionParams) {
		return Thread.currentThread().getId() + "#" + connectionParams.hashCode();
	}

	public CassandraConnection getConnectionParams() {
		return connectionParams;
	}
	
	public void close() {
		activeSessions.clear();
		if (this.cluster != null) this.cluster.closeAsync();
	}
}
