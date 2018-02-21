package org.usfirst.frc.team2495.emulator;

import org.usfirst.frc.team2495.robot.*;
import org.usfirst.frc.team2495.robot.GameData.Plate;


public class EmulatedGameData implements IGameData{
	
	private String gameData = null;
	
	public void setGameSpecificMessage(String gameData_in) {
		gameData = gameData_in;
	}
	
	// this method needs to be called to retrieve the data once on the transition to Autonomous Enabled
	public void update() {
	}
	
	public Plate getAssignedPlateAtFirstSwitch() {
		
		if (gameData != null && gameData.length() >= 1) {
			if (gameData.charAt(0) == 'L') {
				return Plate.LEFT;
			} else if (gameData.charAt(0) == 'R') {
				return Plate.RIGHT;
			}
		}
		
		return Plate.UNKNOWN; // if not left or right
	}

	public Plate getAssignedPlateAtScale() {
		
		if (gameData != null && gameData.length() >= 2) {
			if (gameData.charAt(1) == 'L') {
				return Plate.LEFT;
			} else if (gameData.charAt(1) == 'R') {
				return Plate.RIGHT;
			}
		}
		
		return Plate.UNKNOWN; // if not left or right
	}

	public Plate getAssignedPlateAtSecondSwitch() {
		
		if (gameData != null && gameData.length() >= 3) {
			if (gameData.charAt(2) == 'L') {
				return Plate.LEFT;
			} else if (gameData.charAt(2) == 'R') {
				return Plate.RIGHT;
			}
		}
		
		return Plate.UNKNOWN; // if not left or right
	}

}
