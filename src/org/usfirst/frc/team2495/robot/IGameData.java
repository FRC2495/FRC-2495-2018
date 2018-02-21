package org.usfirst.frc.team2495.robot;

import org.usfirst.frc.team2495.robot.GameData.Plate;

public interface IGameData {
	
	// this method needs to be called to retrieve the data once on the transition to Autonomous Enabled
	public void update();
	
	public Plate getAssignedPlateAtFirstSwitch();

	public Plate getAssignedPlateAtScale();

	public Plate getAssignedPlateAtSecondSwitch();

}
