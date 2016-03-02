package org.test.agf.datasource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.test.agf.datasource.core.CassandraConnection;
import org.test.agf.datasource.core.CassandraQueryTemplate;
import org.test.agf.datasource.mapper.RowMapper;
import org.test.agf.datasource.mapper.SkuAliasMapper;
import org.test.agf.datasource.mapper.SkuFamilyMapper;
import org.test.agf.datasource.mapper.UserMapper;
import org.test.agf.datasource.query.PaginatedResult;

public class TestQuery {
	
	protected static CassandraQueryTemplate cst = null;
	protected static RowMapper mapper = null;
	
	@BeforeClass
	public static void init() {
		CassandraConnection connectionParams = new CassandraConnection();
		connectionParams.setKeyspace("tnt_orica");
		cst = new CassandraQueryTemplate(connectionParams);		
		mapper = new UserMapper();
	}
	
	@AfterClass
	public static void destroy() {
		// session.close();
	}

	// @Test
	public void testSimpleQuery() {
		try {
			List<Object> ret = cst.executeQuery("select * from masterdata_user_movicatt", null, mapper);
			assertEquals(true, ret.size() > 0);
		} catch (Exception e) {
			fail("Error at execute query " + e);
		}
	}
	
	// @Test
	public void testPaginatedQuery() {
		try {
			PaginatedResult ret = cst.executePaginatedQuery("select * from masterdata_user_movicatt", null, new Integer(10), mapper, null);
			assertEquals(true, ret.getResults().size() == 10);
			
			ret.nextResults();
			assertEquals(true, ret.getResults().size() == 10);
			
			ret.nextResults();
			assertEquals(true, ret.getResults().size() == 10);
			
			ret.nextResults();
			assertEquals(true, ret.getResults().size() == 10);
			
			ret.nextResults();
			assertEquals(true, ret.getResults().size() == 10);
			
			ret.nextResults();
			assertEquals(true, ret.getResults().size() == 0);
			
		} catch (Exception e) {
			fail("Error at execute query " + e);
		}
	}
	
	// @Test
	public void testTimeoutPaginatedQuery() throws Exception {
		PaginatedResult ret = cst.executePaginatedQuery("select * from masterdata_user_movicatt", null, new Integer(10), mapper, null);
		assertEquals(true, ret.getResults().size() == 10);
		
		Thread.sleep(cst.getConnectionParams().getTimeout() * 40);
		
		ret.nextResults();
		assertEquals(true, ret.getResults().size() == 10);
	}
	
	@Test
	public void testConcurrentQueries() throws InterruptedException, ExecutionException {
		QueryThread thread1 = new QueryThread(cst, mapper, "select * from masterdata_user_movicatt");
		QueryThread thread2 = new QueryThread(cst, new SkuFamilyMapper(), "select * from masterdata_sku_families");
		QueryThread thread3 = new QueryThread(cst, new SkuAliasMapper(), "select * from masterdata_sku_alias");
		
		ExecutorService threadPool = Executors.newFixedThreadPool(3);
		Set<Callable<Boolean>> callables = new HashSet<Callable<Boolean>>();
		
		callables.add(thread1);
		callables.add(thread2);
		callables.add(thread3);
		
		List<Future<Boolean>> futures = threadPool.invokeAll(callables);
		
		threadPool.shutdown();
		
		assertEquals(false, futures.get(0).get());
		assertEquals(false, futures.get(1).get());
		assertEquals(false, futures.get(2).get());
	}

}
