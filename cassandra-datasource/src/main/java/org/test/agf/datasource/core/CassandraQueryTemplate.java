package org.test.agf.datasource.core;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.test.agf.datasource.mapper.RowMapper;
import org.test.agf.datasource.query.PaginatedResult;

import com.datastax.driver.core.BatchStatement;
import com.datastax.driver.core.BatchStatement.Type;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.ConsistencyLevel;
import com.datastax.driver.core.PreparedStatement;
import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Session;
import com.datastax.driver.core.Statement;

public class CassandraQueryTemplate {
	
	// private static final Logger log = LoggerFactory.getLogger(CassandraQueryTemplate.class);
	
	protected CassandraConnectionManager ccm = null;
	protected ConcurrentHashMap<String, PreparedStatement> psStms = new ConcurrentHashMap<String, PreparedStatement>();
	
	public CassandraQueryTemplate() {
		super();
		this.ccm = new CassandraConnectionManager();
	}
			
	
	public CassandraQueryTemplate(CassandraConnection connectionParams) {
		super();
		this.ccm = new CassandraConnectionManager(connectionParams);
	}
	
	public CassandraConnection getConnectionParams() {
		return this.ccm.getConnectionParams();
	}
	
	protected Session getSession() {
		return this.ccm.getSession();
		
	}

	public CassandraConnectionManager getConnectionManager() {
		return ccm;
	}

	public List<Object> executeQuery(String sql, Object[] args, RowMapper mapper) throws Exception {
		return executeQuery(getSession(), sql, args, mapper, null);
	}
	
	public List<Object> executeQuery(Session session, String sql, Object[] args, RowMapper mapper) throws Exception {
		return executeQuery(session, sql, args, mapper, null);
	}
	
	public List<Object> executeQuery(Session session, String sql, Object[] args, RowMapper mapper, ConsistencyLevel cLevel) throws Exception {
		return executePaginatedQuery(session, sql, args, null, mapper, cLevel).getResults();
	}
	
	public PaginatedResult executePaginatedQuery(String sql, Object[] args, Integer fetchSize, RowMapper mapper, ConsistencyLevel cLevel) throws Exception {
		return executePaginatedQuery(getSession(), sql, args, fetchSize, mapper, cLevel);
	}
	
	public PaginatedResult executePaginatedQuery(Session session, String sql, Object[] args, Integer fetchSize, RowMapper mapper, ConsistencyLevel cLevel) throws Exception {
		PaginatedResult ret = null;
		// long begin = System.currentTimeMillis();		
		BoundStatement bs = generateStatement(session, sql, args, fetchSize,cLevel);
		int queryFetchSize = getFetchSize(session, bs);
		ResultSet rs = session.execute(bs);
		ret = new PaginatedResult(rs, mapper, queryFetchSize);
		// log.debug("Executed query({}) with FetchSize ({}) in ({} ms)", sql, fetchSize, (System.currentTimeMillis() - begin) );
		return ret;
	}
	
	public BoundStatement generateQueryStatement(String sql, Object[] args, ConsistencyLevel cLevel) {
		return generateStatement(getSession(), sql, args, null, cLevel);
	}
	
	public BoundStatement generateStatement(String sql, Object[] args, ConsistencyLevel cLevel) {
		return generateStatement(sql, args, null, cLevel);
	}
	
	public BoundStatement generateStatement(String sql, Object[] args, Integer fetchSize, ConsistencyLevel cLevel) {
		return generateStatement(getSession(), sql, args, fetchSize, cLevel);
	}

	public ResultSet executeBatch(List<Statement> stms, Type type) {
		BatchStatement bstm = new BatchStatement(type);
		for(Statement stm: stms) {
			bstm.add(stm);
		}
		return getSession().execute(bstm);
	}
	
	public ResultSet execute(Statement stm) {
		return getSession().execute(stm);
	}
	
	protected BoundStatement generateStatement(Session session, String sql, Object[] args, Integer fetchSize, ConsistencyLevel cLevel) {
		BoundStatement bs = getStatement(session, sql, fetchSize, cLevel);
		if (args != null && args.length > 0) {
			bs.bind(args);			
		}
		return bs;
	}
	
	private int getFetchSize(Session session, BoundStatement bs) {
		int fs = bs.getFetchSize();
		if (fs == 0) {
			fs = session.getCluster().getConfiguration().getQueryOptions().getFetchSize();
		}
		return fs;
	}
	
	private BoundStatement getStatement(Session session, String sql, Integer fetchSize, ConsistencyLevel cLevel) {
		PreparedStatement ps = getCachedStatement(sql, fetchSize);
		if (ps == null){
			ps = createStatement(session, sql, fetchSize, cLevel);
		}
		
		BoundStatement bs = new BoundStatement(ps);
		if (cLevel != null) {
			bs.setConsistencyLevel(cLevel);
		}
		if (fetchSize != null && fetchSize.longValue() > 0) {
			bs.setFetchSize(fetchSize);
		}
		
		return bs;
	}

	private PreparedStatement getCachedStatement(String sql, Integer fetchSize) {
		PreparedStatement ps = null;
		if (fetchSize != null && fetchSize.longValue() > 0) {
			ps =  psStms.get(sql + "#" + fetchSize);
		} else {
			ps = psStms.get(sql);
		}
		return ps;
	}
	
	private PreparedStatement createStatement(Session session, String sql, Integer fetchSize, ConsistencyLevel cLevel) {
		PreparedStatement ps = session.prepare(sql);
		addCachedStatement(sql, fetchSize, ps);
		return ps;
	}
	
	private void addCachedStatement(String sql, Integer fetchSize, PreparedStatement ps) {
		if (fetchSize != null && fetchSize.longValue() > 0) {
			psStms.put(sql + "#" + fetchSize, ps);
		} else {
			psStms.put(sql, ps);
		}
	}
}
