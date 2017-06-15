package com.citi.vdict.chart;

import com.citi.vdict.utils.StringUtils;

public enum ChartType {
	PIE("PIE"),
	BAR("BAR"),
	LINE("LINE"),
	PACK("PACK"),
	TREEMAP("TREEMAP"),
	FORCE("FORCE"),
	CHORD("CHORD"),
	TREE("TREE"),
	CLUSTER("CLUSTER"),
	SCATTERPLOT("SCATTERPLOT"),
	HISTOGRAM("HISTOGRAM");
	private String value;

	private ChartType(String value) {
		this.value = value;
	}

	public static final ChartType getChartType(String value) {
		if (StringUtils.isBlank(value)) {
			return null;
		}
		for (ChartType item : ChartType.values()) {
			if (StringUtils.equalsIgnoreCase(item.value(), value)) {
				return item;
			}
		}
		return null;
	}

	public String value() {
		return this.value;
	}

	@Override
	public String toString() {
		return this.value;
	}
}
