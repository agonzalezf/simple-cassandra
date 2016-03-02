package org.test.agf.datasource.core;

import java.util.LinkedHashMap;

import com.datastax.driver.core.Session;

public class LRUCache extends LinkedHashMap<String, Session> {

	private static final long serialVersionUID = 6304141498686670377L;
	
	protected Integer limit = null;
	
	public LRUCache(Integer limit) {
		super();
		this.limit = limit;
	}

	@Override
	protected boolean removeEldestEntry(java.util.Map.Entry<String, Session> eldest) {
		boolean delete = size() > limit;
		if (delete) {
			Session eldestSession = eldest.getValue();
			eldestSession.close();
		}
		return delete;
	}

	@Override
	public Session remove(Object key) {		
		return super.remove(key);
	}
}
