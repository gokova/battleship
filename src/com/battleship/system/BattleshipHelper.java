package com.battleship.system;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import com.battleship.network.AttackInfo;
import com.battleship.network.AttackResult;

public class BattleshipHelper {

	private static BattleshipHelper instance;
	private String[][] playerGrid = new String[15][15];
	private Set<String> destroyedCells = new TreeSet<String>();

	private BattleshipHelper() {
		for (int i = 0; i < playerGrid.length; i++) {
			for (int j = 0; j < playerGrid[0].length; j++) {
				playerGrid[i][j] = "";
			}
		}
	}

	public static BattleshipHelper getInstance() {
		if (instance == null) {
			instance = new BattleshipHelper();
		}
		return instance;
	}

	/**
	 * Checks if it's a valid placement and returns result. If it's valid,
	 * places ship in grid array
	 */
	public boolean PlaceShip(int startX, int startY, byte orientation, String shipName, byte shipSize) {
		int startIndex = (orientation == Orientation.HORIZONTAL.getValue()) ? startX : startY;
		boolean isValid = true;
		if ((startIndex + shipSize) < 16) {
			for (int i = startIndex; i < startIndex + shipSize; i++) {
				if (orientation == Orientation.HORIZONTAL.getValue()) {
					if (!this.playerGrid[i][startY].equals("")) {
						isValid = false;
					}
				} else {
					if (!this.playerGrid[startX][i].equals("")) {
						isValid = false;
					}
				}
			}
			if (isValid) {
				for (int i = startIndex; i < startIndex + shipSize; i++) {
					if (orientation == Orientation.HORIZONTAL.getValue()) {
						this.playerGrid[i][startY] = shipName;
					} else {
						this.playerGrid[startX][i] = shipName;
					}
				}
			}
		} else {
			isValid = false;
		}
		return isValid;
	}

	/**
	 * Get background colour for ship.
	 */
	public Color GetShipColor(String activeShipName) {
		Color result;
		switch (activeShipName) {
		case "Carrier":
			result = new Color(152, 251, 152);
			break;
		case "Cruiser":
			result = new Color(244, 164, 96);
			break;
		case "Battleship":
			result = new Color(127, 255, 212);
			break;
		case "Destroyer":
			result = new Color(245, 222, 179);
			break;
		case "Submarine":
			result = new Color(135, 206, 250);
			break;
		default:
			result = Color.BLACK;
			break;
		}
		return result;
	}

	/**
	 * Determine attack result.
	 */
	public List<AttackResult> ResolveAttackResult(AttackInfo attackInfo) {
		int zeroBasedX = attackInfo.coordX - 1;
		int zeroBasedY = attackInfo.coordY - 1;
		int zeroBasedXPlusI = 0;
		int zeroBasedYPlusJ = 0;
		boolean isHit = false;
		List<AttackResult> result = new ArrayList<AttackResult>();
		for (int i = -attackInfo.radius; i <= attackInfo.radius; i++) {
			for (int j = -attackInfo.radius; j <= attackInfo.radius; j++) {
				if (i == 0 || j == 0) {
					zeroBasedXPlusI = zeroBasedX + i;
					zeroBasedYPlusJ = zeroBasedY + j;
					if (zeroBasedXPlusI >= 0 && zeroBasedXPlusI <= 14 && zeroBasedYPlusJ >= 0
							&& zeroBasedYPlusJ <= 14) {
						if (!destroyedCells.contains(zeroBasedXPlusI + "#" + zeroBasedYPlusJ)) {
							if (!attackInfo.inflictsDamage
									&& playerGrid[zeroBasedXPlusI][zeroBasedYPlusJ] == "Submarine") {
								isHit = false;
								result.add(new AttackResult((zeroBasedXPlusI + 1), (zeroBasedYPlusJ + 1), isHit,
										attackInfo.inflictsDamage));
							} else {
								isHit = playerGrid[zeroBasedXPlusI][zeroBasedYPlusJ] != "";
								result.add(new AttackResult((zeroBasedXPlusI + 1), (zeroBasedYPlusJ + 1), isHit,
										attackInfo.inflictsDamage));
							}
							if (isHit && attackInfo.inflictsDamage) {
								destroyedCells.add(zeroBasedXPlusI + "#" + zeroBasedYPlusJ);
							}
						}
					}
				}
			}
		}
		return result;
	}
}
