package com.citi.vdict.service;

import java.util.Map;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.citi.vdict.TestConfiguration;
import com.citi.vdict.utils.StringUtils;

public class DaoServiceTest extends TestConfiguration {

	@Autowired
	private AppDaoService daoService;

	@Test
	public void testDbConnect() {
		String statement = "select * from dual";
		Map<String, Object> res = daoService.selectOneRow(statement);
		print(StringUtils.mapToString(res));
	}

	@Test
	public void testQueryAllSize() {
		String statement = "select * from dual";
		print(daoService.queryAllSize(statement));
	}
}
