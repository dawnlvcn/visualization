package com.citi.vdict.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.citi.vdict.entity.DataSourceKey;
import com.citi.vdict.entity.DataView;
import com.citi.vdict.entity.data.Level;
import com.citi.vdict.entity.data.Node;
import com.citi.vdict.entity.data.Hierarchy;
import com.citi.vdict.entity.view.BaseView;
import com.citi.vdict.service.DataViewService;
import com.citi.vdict.service.DatasourceService;

@RestController
@RequestMapping("/view")
public class ViewController {

	private static Logger logger = LoggerFactory.getLogger(ViewController.class);

	@Autowired
	private DatasourceService dsService;
	@Autowired
	private DataViewService viewService;

	@RequestMapping(value = "/data", method = RequestMethod.POST)
	public Object viewData(@RequestParam(value = "username", required = true) String userName,
			@RequestParam(value = "dsname", required = true) String dsName,
			@RequestParam(value = "viewname", required = true) String viewName,
			@RequestParam(value = "filterdefault", required = false) String filterDefaultP) {

		boolean filterDefault = "FALSE".equalsIgnoreCase(filterDefaultP) ? false : true;

		DataSourceKey dsKey = new DataSourceKey(userName, dsName);
		String dbmsType = dsService.getDbmsType(dsKey);
		BaseView targetView = getViewClass(dbmsType, viewName);
		ResponseData responseData = new ResponseData();
		responseData.setViewName(viewName);
		if (targetView != null) {
			DataView dataView = viewService.getOneView(dbmsType, dsService.isDbaAccount(dsKey), viewName);
			List<Map<String, Object>> viewData = null;
			if(!"".equalsIgnoreCase(dataView.getStatement(false))){
				viewData = viewService.queryViewContent(dsKey, dataView, filterDefault);
			}
			targetView.wrapViewData(viewData);
			responseData.setData(targetView.getViewData());
			responseData.setChartType(dataView.getChartType().value());
		}
		return responseData;
	}

	@RequestMapping(value = "/allViewNames", method = RequestMethod.POST)
	public Object allViewNames(@RequestParam(value = "username", required = true) String userName,
			@RequestParam(value = "dsname", required = true) String dsName) {
		DataSourceKey dsKey = new DataSourceKey(userName, dsName);
		String dbmsType = dsService.getDbmsType(dsKey);
		List<String> allViewNames = viewService.getAllViewNames(dbmsType);
		List<Node> viewNameNodes = new ArrayList<>();
		for (String viewName : allViewNames) {
			viewNameNodes.add(new Node(viewName, Level.LEVEL1));
		}
		return new Hierarchy("all view names", viewNameNodes);
	}

	private BaseView getViewClass(String dbmsType, String viewName) {
		String viewClassName = viewService.getViewClassName(dbmsType, viewName);
		Class<?> viewClass = null;
		BaseView view = null;
		try {
			viewClass = Class.forName(viewClassName);
			view = (BaseView) viewClass.newInstance();
		} catch (Exception e) {
			logger.info(e.getMessage());
			view = null;
		}
		return view;
	}

	class ResponseData {
		private String viewName;
		private String chartType;
		private Object data;

		public ResponseData() {

		}

		public ResponseData(String viewName, String chartType, Object data) {
			setViewName(viewName);
			setChartType(chartType);
			setData(data);
		}

		public String getViewName() {
			return viewName;
		}

		public void setViewName(String viewName) {
			this.viewName = viewName;
		}

		public String getChartType() {
			return chartType;
		}

		public void setChartType(String chartType) {
			this.chartType = chartType;
		}

		public Object getData() {
			return data;
		}

		public void setData(Object data) {
			this.data = data;
		}

	}
}
