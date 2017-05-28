package com.citi.vdict.entity.view;

import java.util.List;
import java.util.Map;

public interface BaseView {
	public void wrapViewData(List<Map<String, Object>> viewData);

	public Object getViewData();

}
