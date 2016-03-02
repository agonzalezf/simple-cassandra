package org.test.agf.datasource;

import java.util.concurrent.Callable;

import org.test.agf.datasource.core.CassandraQueryTemplate;
import org.test.agf.datasource.mapper.RowMapper;

public class QueryThread implements Callable<Boolean> {
	
	protected CassandraQueryTemplate cst = null;
	protected RowMapper mapper = null;
	protected boolean error = false;
	protected String sql = null;

	public QueryThread(CassandraQueryTemplate cst, RowMapper mapper, String sql) {
		super();
		this.cst = cst;
		this.mapper = mapper;
		this.sql = sql;
	}
	
	public boolean getError() {
		return this.error;
	}

	@Override
	public Boolean call() throws Exception {
		try {
			cst.executeQuery(sql, null, mapper);
			Thread.sleep(1500);
		} catch (Exception e) {
			e.printStackTrace();
			this.error = true;
		}
		return this.error;
	}

}
