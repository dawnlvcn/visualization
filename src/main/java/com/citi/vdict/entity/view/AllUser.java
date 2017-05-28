package com.citi.vdict.entity.view;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.citi.vdict.entity.data.Aspect;
import com.citi.vdict.entity.data.Force;
import com.citi.vdict.entity.data.Level;
import com.citi.vdict.entity.data.Node;
import com.citi.vdict.utils.MapUtils;

public class AllUser implements BaseView {
	private static Logger logger = LoggerFactory.getLogger(AllUser.class);
	private Object newViewData = new Object();

	@Override
	public Object getViewData() {
		logger.info(newViewData.toString());
		return newViewData;
	}

	@Override
	public void wrapViewData(List<Map<String, Object>> viewData) {
		if (viewData == null)
			return;

		Force force = new Force();
		for (Map<String, Object> dataMap : viewData) {
			Node sourceNode = new Node();
			sourceNode.setName(dataMap.get(UserColumns.USERNAME.value()));
			sourceNode.setLevel(Level.LEVEL1);
			Map<String, Object> otherData = MapUtils.removeKeys(dataMap, new String[] { UserColumns.USERNAME.value() });
			sourceNode.setOtherData(otherData);
			Aspect aspect = new Aspect();
			aspect.setText("name");
			aspect.setColor("name");
			sourceNode.setAspect(aspect);
			force.addNode(sourceNode);
		}
		newViewData = force;
	}

	enum UserColumns {
		USERNAME("USERNAME"), CREATED("CREATED"), ACCOUNTSTATUS("ACCOUNT_STATUS"), DEFAULTTABLESPACE(
				"DEFAULT_TABLESPACE");
		private String value;

		private UserColumns(String value) {
			this.value = value;
		}

		public String value() {
			return this.value;
		}

	}
}
