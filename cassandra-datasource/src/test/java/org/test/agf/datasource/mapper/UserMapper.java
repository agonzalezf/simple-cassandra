package org.test.agf.datasource.mapper;

import org.test.agf.datasource.objects.User;

import com.datastax.driver.core.Row;

public class UserMapper implements RowMapper<User> {

	@Override
	public User mapRow(Row row, int rowNum) throws Exception {
		return new User(row.getString("first_name") + " " + row.getString("last_name"), row.getString("i_login"));
	}

}
