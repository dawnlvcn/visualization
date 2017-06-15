package com.citi.vdict.entity.view;

import java.util.ArrayList;
import java.util.List;

import com.citi.vdict.entity.data.Node;

public class LineChart implements BaseView {

	@Override
	public Object getViewData() {
		List<Node> monthAndValue = new ArrayList<>();

		monthAndValue.add(new Node("1-May-17", 100.5));
		monthAndValue.add(new Node("2-May-17", 90.6));
		monthAndValue.add(new Node("3-May-17", 125.3));
		monthAndValue.add(new Node("4-May-17", 131.5));
		monthAndValue.add(new Node("5-May-17", 170.5));
		monthAndValue.add(new Node("6-May-17", 70.0));
		monthAndValue.add(new Node("7-May-17", 88.0));
		monthAndValue.add(new Node("8-May-17", 78.0));
		monthAndValue.add(new Node("9-May-17", 90.0));
		monthAndValue.add(new Node("10-May-17", 123.1));
		monthAndValue.add(new Node("11-May-17", 133.1));
		monthAndValue.add(new Node("12-May-17", 134.2));
		monthAndValue.add(new Node("13-May-17", 100.5));
		monthAndValue.add(new Node("14-May-17", 90.6));
		monthAndValue.add(new Node("15-May-17", 125.3));
		monthAndValue.add(new Node("16-May-17", 131.5));
		monthAndValue.add(new Node("17-May-17", 170.5));
		monthAndValue.add(new Node("18-May-17", 70.0));
		monthAndValue.add(new Node("19-May-17", 88.0));
		monthAndValue.add(new Node("20-May-17", 78.0));
		monthAndValue.add(new Node("21-May-17", 90.0));
		monthAndValue.add(new Node("22-May-17", 123.1));
		monthAndValue.add(new Node("23-May-17", 133.1));
		monthAndValue.add(new Node("24-May-17", 134.2));
		monthAndValue.add(new Node("25-May-17", 70.0));
		monthAndValue.add(new Node("26-May-17", 88.0));
		monthAndValue.add(new Node("27-May-17", 78.0));
		monthAndValue.add(new Node("28-May-17", 90.0));
		monthAndValue.add(new Node("29-May-17", 123.1));
		monthAndValue.add(new Node("30-May-17", 133.1));
		monthAndValue.add(new Node("31-May-17", 134.2));

		return monthAndValue;
	}

}
