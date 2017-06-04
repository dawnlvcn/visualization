package com.citi.vdict.entity.view;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.citi.vdict.entity.data.Hierarchy;
import com.citi.vdict.entity.data.Level;
import com.citi.vdict.entity.data.Node;

public class Pack implements BaseView {

	@Override
	public void wrapViewData(List<Map<String, Object>> viewData) {
		// TODO Auto-generated method stub

	}

	@Override
	public Object getViewData() {
		// TODO Auto-generated method stub
		Hierarchy h1 = new Hierarchy();
		h1.setName("level1");
		List<Hierarchy> h2s = new ArrayList<>();
		h2s.add(new Hierarchy("level2-1", new Node("level2-1-id", Level.LEVEL2)));
		h2s.add(new Hierarchy("level2-2", new Node("level2-2-id", Level.LEVEL2)));
		h2s.add(new Hierarchy("level2-3", new Node("level2-3-id", Level.LEVEL2)));
		Hierarchy h3 = new Hierarchy();
		h3.setName("level2-4");
		List<Hierarchy> h3s = new ArrayList<>();
		h3.setChildren(h3s);
		h3s.add(new Hierarchy("level3-1", new Node("level3-1-id", Level.LEVEL3)));
		h3s.add(new Hierarchy("level3-2", new Node("level3-2-id", Level.LEVEL3)));
		h3s.add(new Hierarchy("level3-3", new Node("level3-3-id", Level.LEVEL3)));
		h2s.add(new Hierarchy("level2-4", h3));
		h1.setChildren(h2s);
		return h1;
	}

}
