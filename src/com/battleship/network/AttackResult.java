package com.battleship.network;

import java.io.Serializable;

public class AttackResult implements Serializable {

	private static final long serialVersionUID = 4239505945197070556L;
	public int coordX = 0;
	public int coordY = 0;
	public boolean isHit = false;
	public boolean inflictsDamage = false;

	public AttackResult(int coordX, int coordY, boolean isHit, boolean inflictsDamage) {
		this.coordX = coordX;
		this.coordY = coordY;
		this.isHit = isHit;
		this.inflictsDamage = inflictsDamage;
	}
}
