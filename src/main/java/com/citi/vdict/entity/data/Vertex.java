package com.citi.vdict.entity.data;

import java.util.Map;

public class Vertex<T> {
	private T name;
	private String type;
	private double weight;
	private Map<String, Object> otherData;

	public Vertex() {

	}

	public Vertex(T name, String type, double weight) {
		setName(name);
		setType(type);
		setWeight(weight);
	}

	public Object getName() {
		return name;
	}

	public void setName(T name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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
