package com.citi.vdict.entity.view;

import java.util.ArrayList;
import java.util.List;

import com.citi.vdict.entity.data.Hierarchy;
import com.citi.vdict.entity.data.Level;
import com.citi.vdict.entity.data.Node;

public class Pack implements BaseView {

	@Override
	public Object getViewData() {
		Hierarchy h = new Hierarchy();
		h.setName("level1");
		List<Object> h1s = new ArrayList<>();
		h1s.add(new Node("level1-1-id", Level.LEVEL1.value()));
		h1s.add(new Node("level1-2-id", Level.LEVEL1.value()));
		h1s.add(new Node("level1-3-id", Level.LEVEL1.value()));

		Hierarchy h2 = new Hierarchy();
		h2.setName("level1-4");
		List<Node> h2s = new ArrayList<>();
		h2s.add(new Node("level2-1-id", Level.LEVEL3.value()));
		h2s.add(new Node("level2-2-id", Level.LEVEL3.value()));
		h2s.add(new Node("level2-3-id", Level.LEVEL3.value()));
		h2.setChildren(h2s);
		h1s.add(h2);
		h.setChildren(h1s);
		return h;
	}

}
