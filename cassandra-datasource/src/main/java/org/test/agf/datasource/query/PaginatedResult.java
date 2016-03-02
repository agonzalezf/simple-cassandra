package org.test.agf.datasource.query;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.test.agf.datasource.mapper.RowMapper;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;

public class PaginatedResult<T> {
	
	private static final Logger log = LoggerFactory.getLogger(PaginatedResult.class);

	protected List<T> results = null;
	protected ResultSet rs = null;
	protected Integer queryFetchSize = null;
	protected RowMapper<T> mapper = null;
	protected boolean fullyFetched = false;
	protected boolean showInfo = true;
	
	public PaginatedResult(ResultSet rs, RowMapper<T> mapper, int queryFetchSize, boolean showInfo) throws Exception {
		super();
		this.mapper = mapper;
		this.rs = rs;
		this.showInfo = showInfo;
		this.queryFetchSize = new Integer(queryFetchSize);
		this.results = iterateResult();
	}
	
	public PaginatedResult(ResultSet rs, RowMapper<T> mapper, int queryFetchSize) throws Exception {
		super();
		this.mapper = mapper;
		this.rs = rs;
		this.showInfo = false;
		this.queryFetchSize = new Integer(queryFetchSize);
		this.results = iterateResult();
	}

	private List<T> iterateResult() throws Exception {
		List<T> ret = new ArrayList<T>();
		long begin = System.currentTimeMillis();
		Iterator<Row> it = rs.iterator();
		int rowNum = 0;
		while(rowNum < queryFetchSize && it.hasNext()){
			ret.add(mapper.mapRow(it.next(), rowNum));
			rowNum++;
		}
		if (this.showInfo) {
			log.info("iterateResult in (" + (System.currentTimeMillis() - begin) + " ms)");
		}
		return ret;
	}

	public List<T> getResults() {
		return results;
	}

	public void setResults(List<T> results) {
		this.results = results;
	}

	public ResultSet getRs() {
		return rs;
	}

	public void setRs(ResultSet rs) {
		this.rs = rs;
	}

	public Integer getQueryFetchSize() {
		return queryFetchSize;
	}

	public boolean isFullyFetched() {
		return fullyFetched;
	}

	public void setQueryFetchSize(Integer queryFetchSize) {
		this.queryFetchSize = queryFetchSize;
	}

	public boolean nextResults() throws Exception {
		this.results = iterateResult();
		this.fullyFetched = rs.isFullyFetched() && rs.isExhausted();
		return this.fullyFetched;
	}
}
