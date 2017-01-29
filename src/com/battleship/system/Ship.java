package com.battleship.system;

public enum Ship {

	CARRIER("Carrier"), CRUISER("Cruiser"), BATTLESHIP("Battleship"), DESTROYER("Destroyer"), SUBMARINE("Submarine");
	private String value;

	private Ship(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
