package org.test.agf.datasource.mapper;

import org.test.agf.datasource.objects.SkuAlias;

import com.datastax.driver.core.Row;

public class SkuAliasMapper implements RowMapper<SkuAlias> {

	@Override
	public SkuAlias mapRow(Row row, int rowNum) throws Exception {
		return new SkuAlias(row.getString("pk0_gln"), row.getString("ck0_sku_code"), row.getString("sku_description_alias"));
	}

}
