package org.usfirst.frc.team2495.emulator;

import org.usfirst.frc.team2495.robot.*;

import edu.wpi.first.wpilibj.Joystick;


public class EmulatedHinge implements IHinge {
    
	private boolean isRevLimitSwitchClosed = true;
	
	private int encoder = 0;
	
	boolean hasBeenHomed = false;
	
	public EmulatedHinge() {
	}

	// returns the state of the limit switch
	public boolean getLimitSwitchState() {
		return isRevLimitSwitchClosed; // we fake hinge is down
	}

	// homes the hinge
	// This is done in two steps:
	// step 1: if not already at the switch, we go down slowly until we hit the limit switch.
	// step 2: we go back up a little and mark the position as the virtual/logical zero.
	public void home() {
		System.out.println("Hinge: BEGIN home");
		
		isRevLimitSwitchClosed = true; // maybe not if we are at virtual home but whatever
		
		encoder = 0; // by definition
		
		hasBeenHomed = true;
	}
	
	// DO NOT TRY THIS AT HOME
	// This is to fake homing the hinge when we cannot home it for real (e.g. because we have a cube loaded).
	// It might be useful in auton... 
	// And unlike the real home there is no need to wait for this method.
	// THIS ASSUMES THAT THE HINGE IS ALL THE WAY DOWN!
	public void fakeHomeWhenDown() {
		System.out.println("Hinge: fake home");
		
		isRevLimitSwitchClosed = false;
		
		encoder = Hinge.FAKE_HOME_POSITION_TICKS; // by definition
		
		hasBeenHomed = true;
	}

	// this method need to be called to assess the homing progress
	// (and it takes care of going to step 2 if needed)
	public boolean checkHome() {
		return false;
	}
	
	// do not use in teleop - for auton only
	public void waitHome() {		
		System.out.println("Hinge: END home");
	}
	
	// This method should be called to assess the progress of a move
	public boolean tripleCheckMove() {
		return false;
	}

	// do not use in teleop - for auton only
	public void waitMove() {
		System.out.println("Hinge: END move");
	}
	
	public void moveUp() {
		System.out.println("Hinge: BEGIN move up (retracted)");
		
		isRevLimitSwitchClosed = true; // maybe not if we are at virtual home but whatever
		
		encoder = 0; // by definition
	}

	public void moveMidway() {		
		System.out.println("Hinge: BEGIN move midway (ready to release)");
		
		isRevLimitSwitchClosed = false;
		
		encoder = Hinge.ANGLE_TO_TRAVEL_TICKS / 2;
	}
	
	public void moveDown() {
		System.out.println("Hinge: BEGIN move down (ready to grasp)");
		
		isRevLimitSwitchClosed = false;
		
		encoder = Hinge.ANGLE_TO_TRAVEL_TICKS;
	}

	public double getPosition() {
		return encoder * Hinge.GEAR_RATIO / Hinge.TICKS_PER_REVOLUTION;
	}

	public double getEncPosition() {
		return encoder;
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
		return Math.abs(getEncPosition()) < Hinge.ANGLE_TO_TRAVEL_TICKS * 1/3;
	}
	
	public boolean isDown() {
		return Math.abs(getEncPosition()) > Hinge.ANGLE_TO_TRAVEL_TICKS * 2/3;
	}
	
	public boolean isMidway() {
		return !isUp() && !isDown();
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
		return encoder;
	}
	
	public boolean hasBeenHomed()
	{
		return hasBeenHomed;
	}

}
