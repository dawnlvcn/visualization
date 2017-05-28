package com.citi.vdict.utils;

public enum ApplicationEnum {
	DBMSTYPE("DBMSTYPE"), 
	DBAACCOUNT("DBA"),
	DSNAME("DSNAME"),
	DBURL("DBURL"), 
	DBUSER("DBUSER"), 
	DBPASS("DBPASS"), 
	USERID("USERID"),
	
	VIEWNAME("VIEWNAME"),
	VIEWSTATEMENMT("STATEMENT"),
	VIEFILTERDEFAULT("FILTERDEFAULT"),
	CHARTTYPE("CHARTTYPE");
	
	private String value;

	private ApplicationEnum(String value) {
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
