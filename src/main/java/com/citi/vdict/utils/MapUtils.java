package com.citi.vdict.utils;

import java.util.HashMap;
import java.util.Map;

public class MapUtils {
	public static Map<String, Object> removeKeys(Map<String, Object> map, String[] excludeKeys) {
		Map<String, Object> localMap = new HashMap<>(map);
		for (String key : excludeKeys) {
			localMap.remove(key);
		}
		return localMap;
	}

	public static Map<String, Object> onlyIncludeKeys(Map<String, Object> map, String[] includeKeys) {
		Map<String, Object> localMap = new HashMap<>();
		for (String key : includeKeys) {
			localMap.put(key, map.get(key));
		}
		return localMap;
	}
}
