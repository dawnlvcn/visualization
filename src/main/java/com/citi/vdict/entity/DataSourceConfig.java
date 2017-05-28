package com.citi.vdict.entity;

public class DataSourceConfig {
	private String dsName;
	private String dbmsType;
	private String dbaAccount;
	private String dburl;
	private String dbUser;
	private String dbPass;

	public String getDsName() {
		return dsName;
	}

	public void setDsName(String dsName) {
		this.dsName = dsName;
	}

	public String getDbmsType() {
		return dbmsType;
	}

	public void setDbmsType(String dbmsType) {
		this.dbmsType = dbmsType;
	}

	public String isDbaAccount() {
		return dbaAccount;
	}

	public void setDbaAccount(String dbaAccount) {
		this.dbaAccount = dbaAccount;
	}

	public String getDbUrl() {
		return dburl;
	}

	public void setDbUrl(String dburl) {
		this.dburl = dburl;
	}

	public String getDbUser() {
		return dbUser;
	}

	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}

	public String getDbPass() {
		return dbPass;
	}

	public void setDbPass(String dbPass) {
		this.dbPass = dbPass;
	}

	public String[] toArray() {
		return new String[] { dsName, dbmsType, dburl, dbUser, dbPass };
	}

	@Override
	public String toString() {
		return "[dsName=" + dsName + ", dbmsType=" + dbmsType + ", dburl=" + dburl + ", dbUser=" + dbUser + "]";
	}
}
