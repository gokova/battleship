package com.battleship.network;

import java.io.Serializable;

public class AttackInfo implements Serializable {

	private static final long serialVersionUID = -6056307115892715525L;
	public int coordX = 0;
	public int coordY = 0;
	public byte radius = 0;
	public boolean inflictsDamage = false;

	public AttackInfo(int coordX, int coordY, byte radius, boolean inflictsDamage) {
		this.coordX = coordX;
		this.coordY = coordY;
		this.radius = radius;
		this.inflictsDamage = inflictsDamage;
	}
}
