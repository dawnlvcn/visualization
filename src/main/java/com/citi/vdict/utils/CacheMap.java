package com.citi.vdict.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class CacheMap<K, V> {

	private static final int DEFAULRCACHESIZE = 50;
	private int maxCacheSize = 0;
	private int size = 0;

	private Map<K, V> cacheMap;
	private Map<Object, Long> lastestUsingTime;

	public CacheMap() {
		cacheMap = new HashMap<K, V>(DEFAULRCACHESIZE);
		lastestUsingTime = new HashMap<Object, Long>(DEFAULRCACHESIZE);
		this.maxCacheSize = DEFAULRCACHESIZE;
	}

	public CacheMap(int maxCacheSize) {
		cacheMap = new HashMap<K, V>(maxCacheSize);
		lastestUsingTime = new HashMap<Object, Long>(DEFAULRCACHESIZE);
		this.maxCacheSize = maxCacheSize;
	}

	public V get(Object key) {
		lastestUsingTime.put(key, System.currentTimeMillis());
		return cacheMap.get(key);
	}

	public V put(K key, V value) {
		if (size >= maxCacheSize) {
			remove(getEldestKey());
		}
		++size;
		lastestUsingTime.put(key, System.currentTimeMillis());
		return cacheMap.put(key, value);
	}

	public void putAll(Map<K, V> map) {
		int newMaxCacheSize = cacheMap.size() + map.size();
		Map<K, V> newCacheMap = new HashMap<K, V>(newMaxCacheSize);
		newCacheMap.putAll(cacheMap);
		Map<Object, Long> newAstestUsingTime = new HashMap<Object, Long>(newMaxCacheSize);
		newAstestUsingTime.putAll(lastestUsingTime);
		for (K key : map.keySet()) {
			newCacheMap.put(key, map.get(key));
			newAstestUsingTime.put(key, System.currentTimeMillis());
		}
		cacheMap = newCacheMap;
		lastestUsingTime = newAstestUsingTime;
		maxCacheSize = newMaxCacheSize;
		size = maxCacheSize;
	}

	public V remove(Object key) {
		--size;
		lastestUsingTime.remove(key);

		return cacheMap.remove(key);
	}

	private Object getEldestKey() {
		Long theEldestTime = Collections.min(lastestUsingTime.values());
		for (Object k : lastestUsingTime.keySet()) {
			if (lastestUsingTime.get(k) == theEldestTime) {
				return k;
			}
		}
		// never be null
		return null;
	}

	public Map<K, V> toMap() {
		return cacheMap;
	}

	public Set<K> keySet() {
		return cacheMap.keySet();
	}

	@Override
	public String toString() {
		return StringUtils.mapToString(cacheMap, ",");
	}

}
