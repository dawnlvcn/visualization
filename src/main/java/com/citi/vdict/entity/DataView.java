package com.citi.vdict.entity;

import com.citi.vdict.chart.ChartType;

public class DataView {
	public static String FILTERDEFAULTPLACEHOLDER = "##FILTERDEFAULT##";
	public static String DEFAULTUSERLIST = "##DEFAULTUSERLIST##";
	private String viewName;
	private String dbmsType;
	private String statement;
	private String filterDefault;
	private ChartType chartType;

	public String getViewName() {
		return viewName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public String getDbmsType() {
		return dbmsType;
	}

	public void setDbmsType(String dbmsType) {
		this.dbmsType = dbmsType;
	}

	public String getStatement(boolean isFilterDefault) {
		if (statement == null) {
			return "";
		}
		String repalce = isFilterDefault ? filterDefault : "";
		return statement.replace(FILTERDEFAULTPLACEHOLDER, repalce);
	}

	public void setStatement(String statement) {
		this.statement = statement;
	}

	public void setFilterDefault(String filterDefault) {
		this.filterDefault = filterDefault == null ? "" : filterDefault;
	}

	public String getFilterDefault() {
		return filterDefault;
	}

	@Override
	public String toString() {
		return "{viewName=" + viewName + ",statement=" + statement + ",filterDefault=" + filterDefault + "}";
	}

	public ChartType getChartType() {
		return chartType;
	}

	public void setChartType(ChartType chartType) {
		this.chartType = chartType;
	}
}
