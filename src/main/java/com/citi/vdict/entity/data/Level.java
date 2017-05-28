package com.citi.vdict.entity.data;

public enum Level {
	LEVEL1(1), LEVEL2(2), LEVEL3(3), LEVEL4(4), LEVEL5(5);

	private int value;

	private Level(int value) {
		this.value = value;
	}

	public int value() {
		return this.value;
	}

}
