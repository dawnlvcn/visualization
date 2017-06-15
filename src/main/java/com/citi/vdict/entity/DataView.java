package com.citi.vdict.entity;

import com.citi.vdict.chart.ChartType;

public class DataView {
	private String viewName;
	private String configClass;
	private ChartType chartType;

	public String getViewName() {
		return viewName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	public String getConfigClass() {
		return configClass;
	}

	public void setConfigClass(String configClass) {
		this.configClass = configClass;
	}

	public ChartType getChartType() {
		return chartType;
	}

	public void setChartType(ChartType chartType) {
		this.chartType = chartType;
	}

	@Override
	public String toString() {
		return "{viewName=" + viewName + "chartType=" + chartType.value() + "}";
	}
	
	public enum Field {
		VIEWNAME("viewname"),
		CONFIGCLASS("configclass"),
		CHARTTYPE("charttype");
		
		private String value;

		private Field(String value) {
			this.value = value;
		}

		public String value() {
			return this.value;
		}

		@Override
		public String toString() {
			return this.value;
		}
	}
}
