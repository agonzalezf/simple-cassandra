package org.test.agf.datasource.configuration;

import org.test.agf.configuration.ConfigParam;

public enum DatasourceConfigParam implements ConfigParam {

	// CASSANDRA_NODES("cassandra.nodes", "192.168.1.61,192.168.1.62,192.168.1.63"),
	CASSANDRA_NODES("cassandra.nodes", "127.0.0.1"),
	CASSANDRA_CLUSTER_NAME("cassandra.cluster.name", "TnTCloud"),
	CASSANDRA_PORT("cassandra.port", "9042"),
	CASSANDRA_KEYSPACE("cassandra.keyspace", "epcis_tt"),
	// CASSANDRA_RETRY_POLICY("cassandra.default.retry.policy", "localhost:8000,192.168.1.46:8000"),
	CASSANDRA_CONNECTION_TIMEOUT("cassandra.default.connection.timeout", "1000"),
	CASSANDRA_CONSISTENCY_LEVEL("cassandra.default.consistency.level", "4"),
	CASSANDRA_USE_COMPRESSION("cassandra.default.use.lz4.compression", "false"),
	CASSANDRA_DEFAULT_PARTNER_ID("cassandra.default.partner.id", "71e3ff80-2162-40e9-939f-f658e0238cce");
	
	private String property;
	private String defaultValue;
	
	private DatasourceConfigParam(String property, String defaultValue) {
		this.property = property;
		this.defaultValue = defaultValue;
	}

	@Override
	public String getProperty() {
		return property;
	}

	@Override
	public String getDefaultValue() {
		return defaultValue;
	}

}
