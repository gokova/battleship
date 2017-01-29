package com.battleship.network;

import java.io.Serializable;
import java.util.List;

public class AttackResultWrapper implements Serializable {

	private static final long serialVersionUID = 94585516422684449L;
	public List<AttackResult> attackResult = null;

	public AttackResultWrapper(List<AttackResult> attackResult) {
		this.attackResult = attackResult;
	}
}
