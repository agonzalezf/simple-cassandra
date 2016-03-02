package org.test.agf.datasource.core;

import org.test.agf.configuration.Configuration;
import org.test.agf.datasource.configuration.DatasourceConfigParam;

import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.QueryOptions;
import com.datastax.driver.core.policies.ConstantReconnectionPolicy;
import com.datastax.driver.core.policies.ReconnectionPolicy;

public class CassandraConnection {
	
	protected String clusterName = null;
	protected String keyspace = null;
	protected String[] nodes = null;
	protected Integer port = null;
	protected Integer consistencyLevel = null;
	protected ConsistencyLevel consistency = null;
	protected ReconnectionPolicy reconnectionPolicy = null;
	protected Long timeout = null;
	protected QueryOptions qOpts = null;
	
	public CassandraConnection() {
		super();
		this.clusterName = Configuration.getValue(DatasourceConfigParam.CASSANDRA_CLUSTER_NAME);
		this.keyspace = Configuration.getValue(DatasourceConfigParam.CASSANDRA_KEYSPACE);
		this.nodes = Configuration.getArrayValues(DatasourceConfigParam.CASSANDRA_NODES);
		this.port = Configuration.getIntValue(DatasourceConfigParam.CASSANDRA_PORT);
		setConsistencyLevel(Configuration.getIntValue(DatasourceConfigParam.CASSANDRA_CONSISTENCY_LEVEL));
		this.timeout = Configuration.getLongValue(DatasourceConfigParam.CASSANDRA_CONNECTION_TIMEOUT);
		this.reconnectionPolicy = new ConstantReconnectionPolicy(this.timeout);
		this.qOpts = new QueryOptions();
		this.qOpts.setConsistencyLevel(getConsistency());
	}

	public String getClusterName() {
		return clusterName;
	}
	
	public String getKeyspace() {
		return keyspace;
	}
	
	public String[] getNodes() {
		return nodes;
	}

	public Integer getPort() {
		return port;
	}

	public Integer getConsistencyLevel() {
		return consistencyLevel;
	}

	public ConsistencyLevel getConsistency() {
		return consistency;
	}

	public ReconnectionPolicy getReconnectionPolicy() {
		return reconnectionPolicy;
	}

	public Long getTimeout() {
		return timeout;
	}

	public void setKeyspace(String keyspace) {
		this.keyspace = keyspace;
	}

	public void setNodes(String nodes) {
		String[] ret = new String[]{};
		if (nodes != null && !nodes.isEmpty()) {
			ret = nodes.split("\\,");
		}
		this.nodes = ret;
	}

	public void setPort(Integer port) {
		this.port = port;
	}

	public void setConsistencyLevel(Integer consistencyLevel) {
		this.consistencyLevel = consistencyLevel;
		switch (consistencyLevel) {
			case 0:
				this.consistency = ConsistencyLevel.ANY;
				break;
			case 1:
				this.consistency = ConsistencyLevel.ONE;
				break;
			case 2:
				this.consistency = ConsistencyLevel.TWO;
				break;
			case 3:
				this.consistency = ConsistencyLevel.THREE;
				break;
			case 4:
				this.consistency = ConsistencyLevel.QUORUM;
				break;
			case 5:
				this.consistency = ConsistencyLevel.ALL;
				break;
			case 6:
				this.consistency = ConsistencyLevel.LOCAL_QUORUM;
				break;
			case 7:
				this.consistency = ConsistencyLevel.EACH_QUORUM;
				break;
			case 8:
				this.consistency = ConsistencyLevel.SERIAL;
				break;
			case 9:
				this.consistency = ConsistencyLevel.LOCAL_SERIAL;
				break;
			case 10:
				this.consistency = ConsistencyLevel.LOCAL_ONE;
				break;
			default:
				break;
		}
	}	

	public void setReconnectionPolicy(ReconnectionPolicy reconnectionPolicy) {
		this.reconnectionPolicy = reconnectionPolicy;
	}

	public void setConsistency(ConsistencyLevel consistency) {
		this.consistency = consistency;
	}

	public QueryOptions getQueryOpts() {
		return qOpts;
	}

	public void setqOpts(QueryOptions qOpts) {
		this.qOpts = qOpts;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}

	public void setNodes(String[] nodes) {
		this.nodes = nodes;
	}

	public void setTimeout(Long timeout) {
		this.timeout = timeout;
	}
}
