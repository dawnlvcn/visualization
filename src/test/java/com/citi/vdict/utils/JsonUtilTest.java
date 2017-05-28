package com.citi.vdict.utils;

import org.junit.Test;

import com.citi.vdict.entity.DataView;
import com.citi.vdict.entity.ViewStatement;

public class JsonUtilTest {

	@Test
	public void testViewStatementToJson() {
		System.out.println("ViewStatement from set methods:");
		ViewStatement viewStatement = new ViewStatement();
		viewStatement.setColumns(new String[] { "username" });
		viewStatement.setTableName("users");
		viewStatement.addWheres("username=test and userid=123");
		System.out.println(JsonUtils.toJson(viewStatement));
	}

	@Test
	public void testViewStatementToJson2() {
		System.out.println("ViewStatement from string:");
		String viewStatementStr = "select * from tablename where username= liwei";
		ViewStatement viewStatement = new ViewStatement(viewStatementStr);
		System.out.println(JsonUtils.toJson(viewStatement));
	}

	@Test
	public void testDataViewToJson() {
		System.out.println("DataView from set methods:");
		DataView dataView = new DataView();
		dataView.setViewName("view1");

		ViewStatement viewStatement = new ViewStatement();
		viewStatement.setColumns(new String[] { "username" });
		viewStatement.setTableName("users");
		viewStatement.addWheres("username=test and userid=123");

		String jsonStr = JsonUtils.toJson(dataView);
		System.out.println(jsonStr);

		System.out.println();

		System.out.println(JsonUtils.toObject(jsonStr, DataView.class));

	}

}
