package org.test.agf.datasource.mapper;

import org.test.agf.datasource.objects.SkuFamily;

import com.datastax.driver.core.Row;

public class SkuFamilyMapper implements RowMapper<SkuFamily> {

	@Override
	public SkuFamily mapRow(Row row, int rowNum) throws Exception {
		return new SkuFamily(row.getUUID("pk0_partner").toString(), row.getString("ck0_name"), row.getBool("show"));
	}

}
