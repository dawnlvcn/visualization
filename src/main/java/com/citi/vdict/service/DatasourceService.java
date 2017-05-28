package com.citi.vdict.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.citi.vdict.entity.DataSourceConfig;
import com.citi.vdict.entity.DataSourceKey;
import com.citi.vdict.utils.ApplicationEnum;
import com.citi.vdict.utils.CacheMap;
import com.mchange.v2.c3p0.ComboPooledDataSource;

@Service
public class DatasourceService {
	private static Logger logger = LoggerFactory.getLogger(DatasourceService.class);
	@Autowired
	private Environment env;

	@Autowired
	private CommonService commonService;
	@Autowired
	private AppDaoService daoService;

	@Value("${query.all.datasourceconfig.prepared.sql}")
	private String queryAllDataSourceConfigPreparedSql;
	@Value("${query.datasourceconfig.prepared.sql}")
	private String queryDataSourceConfigPreparedSql;
	@Value("${insert.datasourceconfig.prepared.sql}")
	private String insertDataSourceConfigPreparedSql;
	@Value("${update.datasourceconfig.prepared.sql}")
	private String updateDataSourceConfigPreparedSql;
	@Value("${delete.datasourceconfig.prepared.sql}")
	private String deleteDataSourceConfigPreparedSql;

	private CacheMap<String, DataSource> dataSourceCache = new CacheMap<>(40);
	private CacheMap<String, DataSourceConfig> dsConfigCache = new CacheMap<>(30);
	private CacheMap<String, Set<String>> dsDefaultUsersCache = new CacheMap<>(30);

	public DataSource getDataSource(DataSourceKey dsKey) {
		DataSource dataSource = dataSourceCache.get(dsKey.key());
		if (dataSource != null) {
			return dataSource;
		}
		return createDataSource(getDataSourceConfig(dsKey), dsKey);
	}

	public String getDbmsType(DataSourceKey dsKey) {
		return getDataSourceConfig(dsKey).getDbmsType();
	}

	public boolean isDbaAccount(DataSourceKey dsKey) {
		return "T".equalsIgnoreCase(getDataSourceConfig(dsKey).isDbaAccount());
	}

	public Set<String> getDefaultUserList(DataSourceKey dsKey) {
		Set<String> defaultUserList = dsDefaultUsersCache.get(dsKey.key());
		if (defaultUserList == null) {
			DataSourceConfig currDs = getDataSourceConfig(dsKey);
			defaultUserList = new HashSet<String>();

			Connection conn = null;
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				conn = createDataSource(currDs, null).getConnection();
				String statement = env.getProperty(currDs.getDbmsType() + ".default.user.list.statement");
				logger.info(statement);
				Pattern pattern = Pattern.compile("select.*from");
				Matcher m = pattern.matcher(statement.toLowerCase());
				String columnStr = null;
				while (m.find()) {
					columnStr = m.group(0);
				}
				columnStr = columnStr.substring(6, columnStr.length() - 4).trim();
				String[] columns = updateColumns(columnStr.split(","));

				ps = conn.prepareStatement(statement);
				rs = ps.executeQuery();
				while (rs.next()) {
					for (String col : columns) {
						String schemas = rs.getString(col.toUpperCase());
						if (schemas != null) {
							for (String userSchema : schemas.split(",")) {
								defaultUserList.add(userSchema.trim());
							}
						}
					}
				}
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
			} finally {
				closeConnect(rs, ps, conn);
			}
			dsDefaultUsersCache.put(dsKey.key(), defaultUserList);
		}
		return defaultUserList;
	}

	private String[] updateColumns(String[] columns) {
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
		String[] newColums = new String[tempList.size()];
		tempList.toArray(newColums);
		return newColums;
	}

	public Map<String, DataSourceConfig> getAllDsConfig(String userName) {
		Map<String, DataSourceConfig> allDsConfig = new HashMap<String, DataSourceConfig>();
		List<Map<String, Object>> resultSetList = daoService.jdbcTemplate()
				.queryForList(queryAllDataSourceConfigPreparedSql, new Object[] { userName });

		for (Map<String, Object> resultMap : resultSetList) {
			DataSourceConfig dsConfig = toDataSourceConfig(resultMap);
			dsConfig.setDbPass("");
			allDsConfig.put(dsConfig.getDsName(), dsConfig);
		}
		return allDsConfig;
	}

	private DataSource createDataSource(DataSourceConfig dsConfig, DataSourceKey dsKey) {
		ComboPooledDataSource dataSource = null;
		try {
			dataSource = new ComboPooledDataSource();
			dataSource.setUser(dsConfig.getDbUser());
			dataSource.setPassword(dsConfig.getDbPass());
			dataSource.setJdbcUrl(dsConfig.getDbUrl());
			dataSource.setDriverClass(commonService.getDbmsDriverClass(dsConfig.getDbmsType()));
			dataSource.setInitialPoolSize(1);
			dataSource.setMinPoolSize(1);
			dataSource.setMaxPoolSize(10);
			dataSource.setMaxStatements(50);
			dataSource.setMaxIdleTime(20);
			dataSource.setAcquireRetryAttempts(3);

			if (dataSource.getConnection().isValid(2)) {
				if (dsKey != null) {
					dataSourceCache.put(dsKey.key(), dataSource);
				}
			} 
		} catch (Exception e) {
			logger.error("failed create datasource", e.getMessage());
		}
		return dataSource;
	}

	public DataSourceConfig getDataSourceConfig(DataSourceKey dsKey) {
		DataSourceConfig dataSourceConfig = dsConfigCache.get(dsKey.key());
		if (dataSourceConfig != null) {
			return dataSourceConfig;
		}
		List<Map<String, Object>> resultMapList = daoService.jdbcTemplate().queryForList(
				queryDataSourceConfigPreparedSql, new Object[] { dsKey.getUsername(), dsKey.getDsName() });
		if (resultMapList.size() > 0) {
			Map<String, Object> resultMap = resultMapList.get(0);
			dataSourceConfig = toDataSourceConfig(resultMap);

			DataSourceKey newDsKey = new DataSourceKey();
			newDsKey.setUsername(resultMap.get(ApplicationEnum.USERID.value()).toString());
			newDsKey.setDsName(dataSourceConfig.getDsName());
			dsConfigCache.put(newDsKey.key(), dataSourceConfig);

			return dataSourceConfig;
		} else {
			return null;
		}
	}

	private DataSourceConfig toDataSourceConfig(Map<String, Object> dsMap) {
		DataSourceConfig dataSourceConfig = new DataSourceConfig();
		dataSourceConfig.setDbmsType(dsMap.get(ApplicationEnum.DBMSTYPE.value()).toString());
		dataSourceConfig.setDsName(dsMap.get(ApplicationEnum.DSNAME.value()).toString());
		dataSourceConfig.setDbaAccount(objToString(dsMap.get(ApplicationEnum.DBAACCOUNT.value())));
		dataSourceConfig.setDbUrl(objToString(dsMap.get(ApplicationEnum.DBURL.value())));
		dataSourceConfig.setDbUser(objToString(dsMap.get(ApplicationEnum.DBUSER.value())));
		dataSourceConfig.setDbPass(objToString(dsMap.get(ApplicationEnum.DBPASS.value())));
		return dataSourceConfig;
	}

	private String objToString(Object obj) {
		return obj == null ? null : obj.toString();
	}

	public boolean testDataSource(DataSourceConfig dsConfig) {
		boolean result = false;
		Connection conn = null;
		try {
			Class.forName(commonService.getDbmsDriverClass(dsConfig.getDbmsType()));
			conn = DriverManager.getConnection(dsConfig.getDbUrl(), dsConfig.getDbUser(), dsConfig.getDbPass());
			conn.isValid(1);
			result = true;
		} catch (Exception e) {
			logger.error(e.getMessage());
		} finally {
			closeConnect(null, null, conn);
		}
		return result;
	}

	public boolean insertDataSourceConfig(String userId, DataSourceConfig dataSourceConfig) {
		if (testDataSource(dataSourceConfig)) {
			return false;
		}
		Object[] parameters = concat(new Object[] { userId }, dataSourceConfig.toArray());
		int res = daoService.jdbcTemplate().update(insertDataSourceConfigPreparedSql, parameters);
		if (res > 0) {
			res = updateDbaAccount(userId, dataSourceConfig);
		}
		return res > 0 ? true : false;
	}

	private static <T> T[] concat(T[] first, T[] second) {
		T[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}

	public boolean updateDataSourceConfig(String userId, DataSourceConfig dataSourceConfig) {
		if (testDataSource(dataSourceConfig)) {
			return false;
		}
		Object[] parameters = new Object[] { dataSourceConfig.getDbUrl(), dataSourceConfig.getDbUser(),
				dataSourceConfig.getDbPass(), userId, dataSourceConfig.getDsName() };
		int res = daoService.jdbcTemplate().update(updateDataSourceConfigPreparedSql, parameters);
		if (res > 0 && (res = updateDbaAccount(userId, dataSourceConfig)) > 0) {
			// clear chche to reload the ds after updating
			dsConfigCache.remove((new DataSourceKey(userId, dataSourceConfig.getDsName())).key());
		}
		return res == 1 ? true : false;
	}

	private int updateDbaAccount(String userId, DataSourceConfig currDs) {
		boolean result = false;
		Connection conn = null;
		try {
			Class.forName(commonService.getDbmsDriverClass(currDs.getDbmsType()));
			conn = DriverManager.getConnection(currDs.getDbUrl(), currDs.getDbUser(), currDs.getDbPass());
			if (conn.isValid(2)) {
				conn.prepareStatement("select username from dba_users").executeQuery();
				result = true;
			}
		} catch (SQLException e) {
			logger.info(e.getMessage());
		} catch (Exception e) {
			logger.info(e.getMessage());
		} finally {
			closeConnect(null, null, conn);
		}

		Object[] parameters = new Object[] { (result ? "T" : "F"), userId, currDs.getDsName() };

		int updateRes = daoService.jdbcTemplate().update(env.getProperty("update.datasource.dba.status.prepared.sql"),
				parameters);

		return updateRes;
	}

	private void closeConnect(ResultSet rs, PreparedStatement ps, Connection conn) {
		if (rs != null) {
			try {
				rs.close();
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
			} finally {
				rs = null;
			}
		}
		if (ps != null) {
			try {
				ps.close();
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
			} finally {
				ps = null;
			}
		}
		if (conn != null) {
			try {
				conn.close();
			} catch (SQLException e) {
				logger.error(e.getMessage(), e);
			} finally {
				conn = null;
			}
		}
	}

	public boolean deleteDataSourceConfig(String userId, String dbmsType, String dsName) {
		Object[] parameters = new Object[] { userId, dbmsType, dsName };
		int res = daoService.jdbcTemplate().update(deleteDataSourceConfigPreparedSql, parameters);
		if (res == 1) {
			dsConfigCache.remove((new DataSourceKey(userId, dsName)).key());
		}
		return res == 1 ? true : false;
	}

}
