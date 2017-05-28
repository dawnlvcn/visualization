package com.citi.vdict.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class StringUtilsTest {

	@Test
	public void testMapToString() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("key1", "val1");
		map.put("key2", 123);

		List<String> l = new ArrayList<String>();
		l.add("val3");
		l.add("val4");
		map.put("key3", l);
		System.out.println(StringUtils.mapToString(map, ","));
	}

}
