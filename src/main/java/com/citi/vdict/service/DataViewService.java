package com.citi.vdict.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.citi.vdict.chart.ChartType;
import com.citi.vdict.entity.DataSourceKey;
import com.citi.vdict.entity.DataView;
import com.citi.vdict.utils.ApplicationEnum;
import com.citi.vdict.utils.CacheMap;
import com.citi.vdict.utils.PropertiesUtils;
import com.citi.vdict.utils.StringUtils;

@Service
public class DataViewService {
	private static Logger logger = LoggerFactory.getLogger(DataViewService.class);
	// dbmstype&version -> {dbaView|userView -> {viewMap -> DataView}}
	private static CacheMap<String, Map<String, Map<String, DataView>>> dataViewCache = new CacheMap<>();
	// dbmstype&version -> {dbmstype&version -> list{viewnames}}
	private static Map<String, List<String>> allViewNames = new HashMap<>();
	// dbmstype&version -> {viewname -> viewClass}
	private static Map<String, Map<String, Object>> viewClassConfigs = new HashMap<>();

	@Autowired
	private AppDaoService daoService;
	@Autowired
	private CommonService commonService;
	@Autowired
	private DatasourceService dsService;

	@Value("${query.all.dataview.prepared.sql}")
	private String queryAllDataViewPreparedSql;

	public List<String> getAllViewNames(String dbmsType) {
		return allViewNames.get(dbmsType);
	}

	public String getViewClassName(String dbmsType, String viewName) {
		return viewClassConfigs.get(dbmsType).get(viewName).toString();
	}

	public DataView getOneView(String dbmsType, boolean idDbaView, String viewName) {
		return dataViewCache.get(dbmsType).get(getViewType(idDbaView)).get(viewName);
	}

	public List<Map<String, Object>> queryViewContent(DataSourceKey dsKey, DataView dataView, boolean filterDefault) {
		logger.info("view: " + dataView.getViewName() + "filterDefault: " + filterDefault);

		JdbcTemplate jdbcTemplate = getJdbcTemplate(dsKey);
		String filterDefaultWhere = dataView.getFilterDefault();
		if (!(filterDefaultWhere == null || "".equals(filterDefaultWhere))) {
			if (filterDefaultWhere.contains(DataView.DEFAULTUSERLIST)) {
				Set<String> defaultUserList = dsService.getDefaultUserList(dsKey);
				dataView.setFilterDefault(filterDefaultWhere.replace(DataView.DEFAULTUSERLIST,
						StringUtils.collectionToWrapperString(defaultUserList, "'", ",")));
			}
		}
		String statement = dataView.getStatement(filterDefault);
		logger.info("statement: " + statement);

		return jdbcTemplate.queryForList(statement);

	}

	@PostConstruct
	public void init() {
		loadAllDataView();
		loadAllViewClassConfig();
	}

	private void loadAllViewClassConfig() {
		for (String dbmsType : commonService.getAllDbmsType()) {
			String viewClassPropFile = System.getProperty(dbmsType + ".view.class.config.file");
			Map<String, Object> properties = PropertiesUtils.loadYamlFile(viewClassPropFile);
			viewClassConfigs.put(dbmsType, properties);
		}
	}

	private void loadAllDataView() {
		String dbaViewType = getViewType(true);
		String userViewType = getViewType(false);
		for (String dbmsType : commonService.getAllDbmsType()) {
			Map<String, Map<String, DataView>> views = new HashMap<>();
			views.put(dbaViewType, loadAllLocalDataView(dbmsType, true));
			views.get(dbaViewType).putAll(loadAllDbDataView(dbmsType, true));
			views.put(userViewType, loadAllLocalDataView(dbmsType, false));
			views.get(userViewType).putAll(loadAllDbDataView(dbmsType, false));
			dataViewCache.put(dbmsType, views);
		}
		for (String dbmsType : dataViewCache.keySet()) {
			List<String> viewNames = new ArrayList<>();
			viewNames.addAll(
					dataViewCache.get(dbmsType).get(dbaViewType).keySet().stream().collect(Collectors.toList()));
			allViewNames.put(dbmsType, viewNames);
		}
	}

	private Map<String, DataView> loadAllLocalDataView(String dbmsType, boolean isDbaView) {
		Map<String, DataView> allLocalViewMap = new HashMap<String, DataView>();
		String viewPropFile = System.getProperty(dbmsType + (isDbaView ? ".dba" : ".user") + ".default.views.file");
		Map<String, Object> viewsMap = PropertiesUtils.loadYamlFile(viewPropFile);
		for (String viewName : viewsMap.keySet()) {
			Object viewObj = viewsMap.get(viewName);
			if (viewObj instanceof Map) {
				@SuppressWarnings("unchecked")
				Map<String, Object> currViewMap = (Map<String, Object>) viewObj;
				Map<String, Object> currViewRow = new HashMap<String, Object>();
				for (String viewPropName : currViewMap.keySet()) {
					currViewRow.put(viewPropName.toUpperCase(), currViewMap.get(viewPropName));
				}
				currViewRow.put(ApplicationEnum.DBMSTYPE.value(), dbmsType);
				currViewRow.put(ApplicationEnum.VIEWNAME.value(), viewName);
				DataView dataView = generateDataView(currViewRow);
				allLocalViewMap.put(dataView.getViewName(), dataView);
			} else {
				logger.error("wrong view yaml format of " + viewPropFile);
			}
		}
		return allLocalViewMap;
	}

	private Map<String, DataView> loadAllDbDataView(String dbmsType, boolean isDbaView) {
		List<Map<String, Object>> viewSet = daoService.jdbcTemplate().queryForList(queryAllDataViewPreparedSql,
				new Object[] { dbmsType, getViewType(isDbaView) });
		Map<String, DataView> allDbViewMap = new HashMap<String, DataView>();
		for (Map<String, Object> viewRow : viewSet) {
			DataView dataView = generateDataView(viewRow);
			allDbViewMap.put(dataView.getViewName(), dataView);
		}
		return allDbViewMap;
	}

	private JdbcTemplate getJdbcTemplate(DataSourceKey dsKey) {
		return new JdbcTemplate(dsService.getDataSource(dsKey));
	}

	private String getViewType(boolean idDbaView) {
		return idDbaView ? "DBA" : "USER";
	}

	private DataView generateDataView(Map<String, Object> viewRow) {
		DataView dataView = new DataView();
		dataView.setDbmsType(viewRow.get(ApplicationEnum.DBMSTYPE.value()).toString());
		dataView.setViewName(viewRow.get(ApplicationEnum.VIEWNAME.value()).toString());
		dataView.setStatement(objToString(viewRow.get(ApplicationEnum.VIEWSTATEMENMT.value())));
		dataView.setFilterDefault(objToString(viewRow.get(ApplicationEnum.VIEFILTERDEFAULT.value())));
		dataView.setChartType(
				ChartType.getChartType(viewRow.get(ApplicationEnum.CHARTTYPE.value().toString()).toString()));
		return dataView;
	}

	private String objToString(Object obj) {
		return obj == null ? null : obj.toString();
	}

}
