package com.citi.vdict.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.validation.constraints.NotNull;

import com.citi.vdict.utils.StringUtils;

public class ViewStatement {

	private String[] columns;
	private String colsStr;
	private String tableName;
	private String wheres;
	private String orderCol;

	public ViewStatement() {

	}

	public ViewStatement(String statement) {
		if (statement == null)
			return;
		String tempStatement = statement.toLowerCase().trim();
		if (verify(tempStatement)) {
			colsStr = tempStatement.substring(6, tempStatement.indexOf("from")).trim();
			setColumns(colsStr.split(","));
			int tableIndex = tempStatement.length();
			if (tempStatement.contains("where")) {
				int whereIndex = tempStatement.indexOf("where");
				tableIndex = whereIndex;
				addWheres(tempStatement.substring(whereIndex + 5));
			}
			if (tempStatement.contains("order") && tempStatement.contains("by")) {
				int orderbyIndex = tempStatement.indexOf("by");
				tableIndex = orderbyIndex;
				setOrderCol(tempStatement.substring(orderbyIndex + 2, tempStatement.length()));
			}
			setTableName(tempStatement.substring(tempStatement.indexOf("from") + 4, tableIndex).trim());
		} else {
			tempStatement = null;
		}

	}

	private boolean verify(String statement) {
		statement = statement.toLowerCase().trim();
		if (statement.startsWith("select") && statement.contains("from")) {
			return true;
		}
		return false;
	}

	public String[] getColumns() {
		return columns;
	}

	public void setColumns(String[] columns) {		
		List<String> tempList = new ArrayList<>();
		Pattern pattern = Pattern.compile("[0-9a-zA-Z_]*");
		for (int i = 0; i < columns.length; i++) {
			String col = columns[i].toLowerCase().trim();
			if (!pattern.matcher(col).matches()) {
				if (col.contains("as")) {
					col = col.substring(col.lastIndexOf(" as ") + 3).trim();
				} else {
					continue;
				}
			}
			tempList.add(col);
		}
		this.columns = new String[tempList.size()];
		tempList.toArray(this.columns);
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getWheres() {
		return wheres;
	}

	public void setWheres(String wheres) {
		this.wheres = wheres;
	}

	public void addWheres(@NotNull String wheres) {

		if (this.wheres != null && this.wheres.toLowerCase().contains("where")) {
			this.wheres += " and ";
		}
		this.wheres = wheres;
	}

	public void orWheres(@NotNull String wheres) {
		if (this.wheres != null && this.wheres.toLowerCase().contains("where")) {
			this.wheres += " or ";
		}
		this.wheres += wheres;
	}
	
	public String getOrderCol() {
		return orderCol;
	}

	public void setOrderCol(String orderCol) {
		this.orderCol = orderCol;
	}

	
	public String statement() {
		return statement(false);
	}

	public String statement(boolean order) {
		StringBuilder statementBf = new StringBuilder();
		statementBf.append("select ");
		if (colsStr != null) {
			statementBf.append(colsStr);
		} else {
			statementBf.append("*");
		}
		statementBf.append(" from " + tableName);

		if (wheres != null) {
			statementBf.append(" where ");
			statementBf.append(" ( " + wheres + " ) ");
		}
			
		if (order) {
			statementBf.append(" order by ").append(orderCol);
		}
		return statementBf.toString();
	}


	
	@Override
	public String toString() {
		return "{columns=" + StringUtils.arrayToString(columns) + ",tableName=" + tableName + ",wheres=" + wheres
				+ "}";
	}

}