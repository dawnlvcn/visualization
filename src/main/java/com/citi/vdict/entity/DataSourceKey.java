package com.citi.vdict.entity;

public class DataSourceKey {
	private String username;
	private String dsName;

	public DataSourceKey() {
	}

	public DataSourceKey(String username, String dsName) {
		setUsername(username);
		setDsName(dsName);
	}

	public String getDsName() {
		return dsName;
	}

	public void setDsName(String dsName) {
		this.dsName = dsName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String key() {
		return dsName + "_" + username;
	}

	@Override
	public String toString() {
		return key();
	}
}
