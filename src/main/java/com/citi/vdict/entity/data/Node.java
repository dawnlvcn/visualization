package com.citi.vdict.entity.data;

import java.util.Map;

public class Node {
	private Object name;
	private Level level;
	private Aspect aspect;
	private Map<String, Object> otherData;

	public Node() {

	}

	public Node(Object id, Level level) {
		setName(id);
		setLevel(level);
	}

	public Object getName() {
		return name;
	}

	public void setName(Object name) {
		this.name = name;
	}

	public int getLevel() {
		return level.value();
	}

	public void setLevel(Level level) {
		this.level = level;
	}

	public Map<String, Object> getOtherData() {
		return otherData;
	}

	public Aspect getAspect() {
		return aspect;
	}

	public void setAspect(Aspect aspect) {
		this.aspect = aspect;
	}

	public void setOtherData(Map<String, Object> otherData) {
		this.otherData = otherData;
	}
}
