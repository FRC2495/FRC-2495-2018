package org.usfirst.frc.team2495.emulator;

import org.usfirst.frc.team2495.robot.*;

import edu.wpi.first.wpilibj.Joystick;


public class EmulatedElevator implements IElevator {
    
	public EmulatedElevator() {
	}

	// returns the state of the limit switch
	public boolean getLimitSwitchState() {
		return true; // we fake elevator is up
	}
	
	// homes the elevator
	// This is done in two steps:
	// step 1: if not already at the switch, we go down slowly until we hit the limit switch.
	// step 2: we go back up a little and mark the position as the virtual/logical zero.
	public void home() {
		System.out.println("Elevator: BEGIN home");
	}

	// this method need to be called to assess the homing progress
	// (and it takes care of going to step 2 if needed)
	public boolean checkHome() {
		return false;
	}

	
	// do not use in teleop - for auton only
	public void waitHome() {
		System.out.println("Elevator: END home");
	}
	
	// This method should be called to assess the progress of a move
	public boolean tripleCheckMove() {
		return false;
	}

	// do not use in teleop - for auton only
	public void waitMove() {
		System.out.println("Elevator: END move");
	}
	
	public void moveUp() {
		System.out.println("Elevator: BEGIN move up (move up to scale)");
	}

	public void moveMidway() {		
		System.out.println("Elevator: BEGIN move midway (move up to switch)");
	}
	
	public void moveDown() {
		System.out.println("Elevator: BEGIN move down (move down)");
	}

	public double getPosition() {
		return Elevator.LENGTH_OF_TRAVEL_INCHES;
	}

	public double getEncPosition() {
		return 123456789;
	}

	public boolean isHoming() {
		return false;
	}
	
	public boolean isHomingPart1() {
		return false;
	}
	
	public boolean isHomingPart2() {
		return false;
	}

	public boolean isMoving() {
		return false;
	}
	
	public boolean isUp() {
		return true;
	}
	
	public boolean isDown() {
		return false;
	}
	
	public boolean isMidway() {
		return false;
	}

	public void stay() {	 		
	}
	
	public void stop() {	 
	}	

	public void setNominalAndPeakOutputs(double peakOutput)
	{
	}
	
	// for debug purpose only
	public void joystickControl(Joystick joystick)
	{
	}	
	
	public double getTarget() {
		return 0.0;
	}
	
	public boolean hasBeenHomed()
	{
		return true;
	}

}
