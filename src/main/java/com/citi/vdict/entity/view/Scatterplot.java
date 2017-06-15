package com.citi.vdict.entity.view;

import java.util.ArrayList;
import java.util.List;

import com.citi.vdict.entity.data.Node;

public class Scatterplot implements BaseView {

	@Override
	public Object getViewData() {
		List<Node> monthAndValue = new ArrayList<>();
		monthAndValue.add(new Node("A", 100.5));
		monthAndValue.add(new Node("B", 90.6));
		monthAndValue.add(new Node("C", 125.3));
		monthAndValue.add(new Node("D", 131.5));
		monthAndValue.add(new Node("E", 147.5));
		monthAndValue.add(new Node("F", 70.0));
		monthAndValue.add(new Node("G", 88.0));
		monthAndValue.add(new Node("H", 78.0));
		monthAndValue.add(new Node("I", 90.0));
		monthAndValue.add(new Node("J", 123.1));
		monthAndValue.add(new Node("K", 133.1));
		monthAndValue.add(new Node("L", 134.2));
		monthAndValue.add(new Node("M", 112.0));
		monthAndValue.add(new Node("N", 152.0));
		monthAndValue.add(new Node("O", 150.));
		monthAndValue.add(new Node("P", 68.9));
		monthAndValue.add(new Node("Q", 70));
		monthAndValue.add(new Node("R", 90));
		monthAndValue.add(new Node("S", 88.9));
		monthAndValue.add(new Node("T", 76.0));
		monthAndValue.add(new Node("U", 140));
		monthAndValue.add(new Node("V", 97.0));
		monthAndValue.add(new Node("W", 100));
		monthAndValue.add(new Node("X", 160));
		monthAndValue.add(new Node("Y", 143.60));
		monthAndValue.add(new Node("Z", 105.6));
		return monthAndValue;
	}

}
