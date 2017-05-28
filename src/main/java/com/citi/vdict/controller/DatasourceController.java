package com.citi.vdict.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.citi.vdict.entity.DataSourceConfig;
import com.citi.vdict.entity.DataSourceKey;
import com.citi.vdict.service.CommonService;
import com.citi.vdict.service.DatasourceService;
import com.citi.vdict.utils.JsonUtils;

@RestController
@RequestMapping("/datasource")
public class DatasourceController {

	@Autowired
	private DatasourceService dsService;
	@Autowired
	private CommonService commonService;

	@RequestMapping(value = "/dbms", method = RequestMethod.POST)
	public String getAllDbms(HttpServletRequest httpRequest) {
		return JsonUtils.toJson(commonService.getAllDbmsType());
	}

	@RequestMapping(value = "/all", method = RequestMethod.POST)
	public String getAllDatasourceConfig(HttpServletRequest httpRequest) {
		String userName = httpRequest.getParameter("username");
		return JsonUtils.toJson(dsService.getAllDsConfig(userName));
	}

	@RequestMapping(value = "/save", method = RequestMethod.POST)
	public String addDatasource(HttpServletRequest httpRequest) {
		String updateP = httpRequest.getParameter("update");
		String userName = httpRequest.getParameter("username");
		DataSourceConfig dataSourceConfig = getDataSourceConfig(httpRequest);
		boolean isUpdate = updateP != null && "TRUE".equalsIgnoreCase(updateP.trim());
		boolean result = false;
		if (isUpdate) {
			result = dsService.updateDataSourceConfig(userName, dataSourceConfig);
			return result ? "true" : "false";
		} else {
			result = dsService.insertDataSourceConfig(userName, dataSourceConfig);
			return result ? "true" : "false";
		}
	}

	@RequestMapping(value = "/delete", method = RequestMethod.POST)
	public String deleteDatasource(HttpServletRequest httpRequest) {
		String userName = httpRequest.getParameter("username");
		String dbmsType = httpRequest.getParameter("dbmstype");
		String dsName = httpRequest.getParameter("dsname");
		boolean res = dsService.deleteDataSourceConfig(userName, dbmsType, dsName);
		return res ? "true" : "false";
	}

	@RequestMapping(value = "/test", method = RequestMethod.POST)
	public String testConnection(HttpServletRequest httpRequest) {
		String userName = httpRequest.getParameter("username");
		String dsName = httpRequest.getParameter("dsname");
		DataSourceKey dsKey = new DataSourceKey(userName, dsName);

		DataSourceConfig dsConfig = getDataSourceConfig(httpRequest);
		DataSourceConfig oldDsConfig = dsService.getDataSourceConfig(dsKey);
		if (oldDsConfig != null && "".equals(dsConfig.getDbPass().trim())) {
			dsConfig.setDbPass(oldDsConfig.getDbPass());
		}

		boolean res = dsService.testDataSource(dsConfig);
		return res ? "true" : "false";
	}

	private DataSourceConfig getDataSourceConfig(HttpServletRequest httpRequest) {
		DataSourceConfig dsConfig = new DataSourceConfig();
		String dbmsType = httpRequest.getParameter("dbmstype");
		String dsName = httpRequest.getParameter("dsname");
		String dburl = httpRequest.getParameter("dburl");
		String dbUser = httpRequest.getParameter("dbuser");
		String dbPass = httpRequest.getParameter("dbpass");
		dsConfig.setDsName(dsName);
		dsConfig.setDbmsType(dbmsType);
		dsConfig.setDbUrl(dburl);
		dsConfig.setDbUser(dbUser);
		dsConfig.setDbPass(dbPass);
		return dsConfig;
	}
}
