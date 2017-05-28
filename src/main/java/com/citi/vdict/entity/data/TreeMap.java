package com.citi.vdict.entity.data;

public class TreeMap {
	private String name;
	private Object children;

	public TreeMap() {

	}

	public TreeMap(String name, Object children) {
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

	public void setChildren(Object children) {
		this.children = children;
	}
}
