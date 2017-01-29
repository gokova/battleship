package com.battleship.system;

public enum Orientation {

	HORIZONTAL((byte) 0), VERTICAL((byte) 1);
	private byte value;

	private Orientation(byte value) {
		this.value = value;
	}

	public byte getValue() {
		return value;
	}
}
