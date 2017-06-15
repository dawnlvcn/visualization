package com.citi.vdict.entity.data;

import java.util.Map;

public class Node {
	private String name;
	private double weight;
	private Map<String, Object> otherData;

	public Node() {

	}

	public Node(String name, double weight) {
		setName(name);
		setWeight(weight);
	}

	public Object getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getWeight() {
		return weight;
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public Map<String, Object> getOtherData() {
		return otherData;
	}

	public void setOtherData(Map<String, Object> otherData) {
		this.otherData = otherData;
	}
}
