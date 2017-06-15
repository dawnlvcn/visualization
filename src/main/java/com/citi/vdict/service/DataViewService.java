package com.citi.vdict.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

import com.citi.vdict.chart.ChartType;
import com.citi.vdict.entity.DataView;
import com.citi.vdict.utils.CacheMap;
import com.citi.vdict.utils.PropertiesUtils;

@Service
public class DataViewService {

	private static CacheMap<String, DataView> dataViewCache = new CacheMap<>();

	public List<String> getAllViewNames() {
		return dataViewCache.keySet().stream().collect(Collectors.toList());
	}

	public DataView getView(String viewName) {
		return dataViewCache.get(viewName);
	}

	@PostConstruct
	private void loadAllViewClassConfig() {
		String viewClassPropFile = System.getProperty("view.config.file");
		Map<String, Object> properties = PropertiesUtils.loadYamlFile(viewClassPropFile);
		for (String viewName : properties.keySet()) {
			@SuppressWarnings("unchecked")
			Map<String, Object> viewRow = (Map<String, Object>) properties.get(viewName);
			DataView dataView = new DataView();
			dataView.setViewName(viewName);
			dataView.setConfigClass(viewRow.get(DataView.Field.CONFIGCLASS.value()).toString());
			dataView.setChartType(ChartType.getChartType(viewRow.get(DataView.Field.CHARTTYPE.value()).toString()));
			dataViewCache.put(viewName, dataView);
		}

	}
}
