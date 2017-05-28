package com.citi.vdict.entity.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.citi.vdict.entity.data.Aspect;
import com.citi.vdict.entity.data.Force;
import com.citi.vdict.entity.data.Level;
import com.citi.vdict.entity.data.Link;
import com.citi.vdict.entity.data.Node;
import com.citi.vdict.utils.MapUtils;

public class AllTable implements BaseView {

	private Object newViewData = new Object();

	@Override
	public Object getViewData() {
		return newViewData;
	}

	@Override
	public void wrapViewData(List<Map<String, Object>> viewData) {
		if (viewData == null)
			return;

		Force force = new Force();
		List<String> cacheNode = new ArrayList<>();
		for (Map<String, Object> dataMap : viewData) {
			String sourceNodeName = dataMap.get(Table.OWNER.value()).toString();
			if (!cacheNode.contains(sourceNodeName)) {
				Node sourceNode = new Node();
				sourceNode.setName(dataMap.get(Table.OWNER.value()));
				sourceNode.setLevel(Level.LEVEL1);
				Aspect sourceNodeAspect = new Aspect();
				sourceNodeAspect.setText("name");
				sourceNodeAspect.setColor("level");
				sourceNodeAspect.setSize("level");
				sourceNode.setAspect(sourceNodeAspect);
				force.addNode(sourceNode);
				cacheNode.add(sourceNodeName);
			}

			String targetNodeName = dataMap.get(Table.TABLENAME.value()).toString();
			if (!cacheNode.contains(targetNodeName)) {
				Node targetNode = new Node();
				targetNode.setName(dataMap.get(Table.TABLENAME.value()));
				targetNode.setLevel(Level.LEVEL2);
				Map<String, Object> otherData = MapUtils.onlyIncludeKeys(dataMap, new String[] {
						Table.TABLESPACE_NAME.value(), Table.NUM_ROWS.value(), Table.AVG_ROW_LEN.value() });
				targetNode.setOtherData(otherData);
				Aspect targetNodeAspect = new Aspect();
				targetNodeAspect.setText("name");
				targetNodeAspect.setColor(Table.TABLESPACE_NAME.value());
				targetNodeAspect.setSize("level");
				targetNodeAspect.setTitles(new String[] { Table.NUM_ROWS.value(), Table.AVG_ROW_LEN.value() });
				targetNode.setAspect(targetNodeAspect);
				force.addNode(targetNode);
				cacheNode.add(targetNodeName);
			}

			Link link = new Link();
			link.setSource(sourceNodeName);
			link.setTarget(targetNodeName);
			link.setLevel(Level.LEVEL1);
			force.addLinks(link);
		}
		newViewData = force;
	}

	enum Table {
		OWNER("OWNER"), TABLENAME("TABLE_NAME"), TABLESPACE_NAME("TABLESPACE_NAME"), STATUS("STATUS"), NUM_ROWS(
				"NUM_ROWS"), AVG_ROW_LEN("AVG_ROW_LEN");
		private String value;

		private Table(String value) {
			this.value = value;
		}

		public String value() {
			return this.value;
		}

	}
}
