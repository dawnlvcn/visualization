package com.citi.vdict.entity.data;

import java.util.Map;

public class Edge {
	private String source;
	private String target;
	private double weight;
	private Map<String, Object> otherData;

	public Edge() {

	}

	public Edge(String source, String target, double weight) {
		setSource(source);
		setTarget(target);
		setWeight(weight);
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

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public Map<String, Object> getOtherData() {
		return otherData;
	}

	public void setOtherData(Map<String, Object> otherData) {
		this.otherData = otherData;
	}

}
