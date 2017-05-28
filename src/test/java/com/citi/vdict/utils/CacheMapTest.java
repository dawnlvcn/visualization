package com.citi.vdict.utils;

import org.junit.Test;

public class CacheMapTest {

	@Test
	public void test() throws InterruptedException {
		CacheMap<String, String> cache = new CacheMap<String, String>(3);
		cache.put("test", "test");
		Thread.sleep(10);
		cache.put("test1", "test1");
		Thread.sleep(10);
		cache.put("test2", "test2");
		System.out.println("after get init: " + cache);
		cache.put("test3", "test3");
		System.out.println("after add test3: " + cache);
		cache.put("test4", "test4");
		Thread.sleep(10);
		cache.get("test2");
		cache.put("test5", "test5");
		System.out.println("after get test1: " + cache);
	}

}
