package com.citi.vdict.entity.data;

import java.util.Map;

public class Link {
	private String source;
	private String target;
	private Level level;
	private Map<String, Object> otherData;

	public Link() {

	}

	public Link(String source, String target, Level level) {
		setSource(source);
		setTarget(target);
		setLevel(level);
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
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

	public void setOtherData(Map<String, Object> otherData) {
		this.otherData = otherData;
	}
}
