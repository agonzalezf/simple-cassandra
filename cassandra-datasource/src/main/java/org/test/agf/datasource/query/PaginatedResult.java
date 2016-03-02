package org.test.agf.datasource.query;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.test.agf.datasource.mapper.RowMapper;

import com.datastax.driver.core.ResultSet;
import com.datastax.driver.core.Row;

public class PaginatedResult {
	
	private static final Logger log = LoggerFactory.getLogger(PaginatedResult.class);

	protected List<Object> results = null;
	protected ResultSet rs = null;
	protected Integer queryFetchSize = null;
	protected RowMapper mapper = null;
	protected boolean fullyFetched = false;
	
	public PaginatedResult(ResultSet rs, RowMapper mapper, int queryFetchSize) throws Exception {
		super();
		this.mapper = mapper;
		this.rs = rs;		
		this.queryFetchSize = new Integer(queryFetchSize);
		this.results = iterateResult(false);
	}
	
	private List<Object> iterateResult(boolean showInfo) throws Exception {
		List<Object> ret = new ArrayList<Object>();
		long begin = System.currentTimeMillis();
		Iterator<Row> it = rs.iterator();
		int rowNum = 0;
		while(rowNum < queryFetchSize && it.hasNext()){
			ret.add(mapper.mapRow(it.next(), rowNum));
			rowNum++;
		}
		if (showInfo) {
			log.info("iterateResult in (" + (System.currentTimeMillis() - begin) + " ms)");
		}
		return ret;
	}

	public List<Object> getResults() {
		return results;
	}

	public void setResults(List<Object> results) {
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
		this.results = iterateResult(true);
		this.fullyFetched = rs.isFullyFetched() && rs.isExhausted();
		return this.fullyFetched;
	}
}
