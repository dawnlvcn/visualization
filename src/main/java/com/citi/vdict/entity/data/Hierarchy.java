package com.citi.vdict.entity.data;

import java.util.List;

public class Hierarchy {
	private String name;
	private List<?> children;

	public Hierarchy() {

	}

	public Hierarchy(String name, List<?> children) {
		setName(name);
		setChildren(children);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Object getChildren() {
		return children;
	}

	public void setChildren(List<?> children) {
		this.children = children;
	}
}
