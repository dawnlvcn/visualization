package com.citi.vdict;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

	public static void setColumns(String[] columns) {
		List<String> tempList = new ArrayList<>();
		Pattern pattern = Pattern.compile("[0-9a-zA-Z_]*");
		for (int i = 0; i < columns.length; i++) {
			String col = columns[i].toLowerCase();
			if (!pattern.matcher(col).matches()) {
				if (col.contains("as")) {
					col = col.substring(col.lastIndexOf(" as ") + 3).trim();
				} else {
					continue;
				}
			}
			tempList.add(col);
		}
		String[] newColumns = new String[tempList.size()];
		tempList.toArray(newColumns);

		for (String col : newColumns) {
			System.out.println(col);
		}
	}

	public static void main(String[] args) {
		setColumns("username,user_evd".split(","));
		
		Pattern p = Pattern.compile("select.*from");
		Matcher m = p.matcher("  select username,user_evd, test from table_name");
		String colStr = null;
		while (m.find()) {
			colStr = m.group(0);
		}

		System.out.println("colStr: " + colStr.substring(6, colStr.length() - 4).trim());

		String columnStr = "username, to_char(created, 'yyyy-mm-dd') as created";

		Pattern pattern = Pattern.compile("[0-9a-zA-Z_]*");
		String[] columns = columnStr.split(",");
		for (int i = 0; i < columns.length; i++) {
			String col = columns[i].toLowerCase();
			if (!pattern.matcher(col).matches()) {
				if (col.contains("as")) {
					col = col.substring(col.lastIndexOf(" as ") + 3).trim();
				} else {
					continue;
				}
			}

			System.out.println(">>>>" + col);
		}
		String statement = "select    *    from dual";
		if (!statement.contains("count(")) {
			String temp = statement.substring(statement.indexOf("select", 0) + 6, statement.indexOf("from", 0));
			System.out.println("[" + temp + "]");
			statement = statement.replace(temp, " count(*) ");
		}

		System.out.println(statement);
	}

}
