package com.citi.vdict.service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import com.citi.vdict.entity.ViewStatement;

@Service
public class AppDaoService {

	private static Logger logger = LoggerFactory.getLogger(AppDaoService.class);
	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public AppDaoService(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public JdbcTemplate jdbcTemplate() {
		return this.jdbcTemplate;
	}

	public long queryAllSize(@NotNull String statement) {
		logger.info("query all row size for: " + statement);
		String newQuerySql = "select count(*) as count from ";
		if (statement.contains("from")) {
			String table = statement.substring(statement.indexOf("from", 0) + 6, statement.length());
			newQuerySql = newQuerySql + table;
		} else {
			newQuerySql = newQuerySql + statement;
		}
		Object resObj = jdbcTemplate.queryForMap(newQuerySql).get("COUNT");
		return resObj == null ? 0L : Long.parseLong(resObj.toString());
	}

	public List<String> queryAllColums(@NotNull String tableName) {
		String statement = "select column_name from all_tab_columns where table_name = '" + tableName + "'";
		logger.info("query all columns for: " + tableName);
		jdbcTemplate.queryForList(statement, new RowMapper<String>() {
			@Override
			public String mapRow(ResultSet rs, int rownumber) throws SQLException {
				return rs.getString("COLUMN_NAME");
			}
		});
		return null;
	}

	public Map<String, Object> selectOneRow(@NotNull String statement) {
		logger.info("query one row for: " + statement);
		return jdbcTemplate.queryForMap(statement);
	}

	public List<Map<String, Object>> selectMultiRow(@NotNull String statement) {
		logger.info("query multi row for: " + statement);
		return jdbcTemplate.queryForList(statement);
	}

	public List<Map<String, Object>> selectRangeRow(@NotNull ViewStatement queryStatement, final int startIndex,
			final int fetchSize) {
		List<String> tempAllColumn = null;
		String[] columns = queryStatement.getColumns();
		if (columns == null || (tempAllColumn = Arrays.asList(columns)).contains("*")) {
			tempAllColumn = queryAllColums(queryStatement.getTableName());
		}
		if (queryStatement.getOrderCol() == null) {
			queryStatement.setOrderCol(tempAllColumn.get(0));
		}
		logger.info("query multi row for: " + queryStatement.statement());
		final List<String> allColumn = new ArrayList<String>(tempAllColumn);
		logger.info("query multi row for: " + allColumn.toString());
		return jdbcTemplate.query(queryStatement.statement(), new RowMapper<Map<String, Object>>() {
			@Override
			public Map<String, Object> mapRow(ResultSet rs, int rowNum) throws SQLException {
				if (rs.next() && rowNum >= startIndex && rowNum <= startIndex + fetchSize) {
					Map<String, Object> resMap = new HashMap<String, Object>();
					for (String colName : allColumn) {
						resMap.put(colName, rs.getString(colName.toUpperCase()));
					}
					return resMap;
				}
				return null;
			}
		});
	}

}
