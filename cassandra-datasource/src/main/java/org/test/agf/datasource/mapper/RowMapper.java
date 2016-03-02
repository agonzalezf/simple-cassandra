package org.test.agf.datasource.mapper;

import com.datastax.driver.core.Row;

public interface RowMapper {
	
	public Object mapRow(Row row, int rowNum) throws Exception;

}
