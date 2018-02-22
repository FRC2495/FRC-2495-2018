package org.usfirst.frc.team2495.emulator;

import org.usfirst.frc.team2495.robot.*;

import edu.wpi.first.wpilibj.Joystick;


public class EmulatedElevator implements IElevator {
    
	private boolean isFwdLimitSwitchClosed = true;
	
	private int encoder = 0;
	
	boolean hasBeenHomed = false;
	
	IHinge hinge;
	
	public EmulatedElevator(IHinge hinge_in) {
		hinge = hinge_in;
	}

	// returns the state of the limit switch
	public boolean getLimitSwitchState() {
		return isFwdLimitSwitchClosed;
	}
	
	// homes the elevator
	// This is done in two steps:
	// step 1: if not already at the switch, we go down slowly until we hit the limit switch.
	// step 2: we go back up a little and mark the position as the virtual/logical zero.
	public void home() {
		System.out.println("Elevator: BEGIN home");
		
		isFwdLimitSwitchClosed = true;
		
		encoder = 0; // by definition
		
		hasBeenHomed = true;
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
		
		if (hinge != null && (!hinge.hasBeenHomed() || !hinge.isDown())) {
			System.out.println("VIOLATION: cannot move elevator up when hinge has not been homed or is not down!");
		}
		
		isFwdLimitSwitchClosed = false;
		
		encoder = Elevator.LENGTH_OF_TRAVEL_INCHES;
	}

	public void moveMidway() {		
		System.out.println("Elevator: BEGIN move midway (move up to switch)");
		
		if (hinge != null && (!hinge.hasBeenHomed() || !hinge.isDown())) {
			System.out.println("VIOLATION: cannot move elevator midway when hinge has not been homed or is not down!");
		}
		
		isFwdLimitSwitchClosed = false;
		
		encoder = Elevator.LENGTH_OF_TRAVEL_INCHES / 2;
	}
	
	public void moveDown() {
		System.out.println("Elevator: BEGIN move down (move down)");
		
		if (hinge != null && (!hinge.hasBeenHomed() || !hinge.isDown())) {
			System.out.println("VIOLATION: cannot move elevator down when hinge has not been homed or is not down!");
		}
		
		isFwdLimitSwitchClosed = true; // or maybe not if at virtual home but whatever
		
		encoder = 0;
	}

	public double getPosition() {
		return convertRevtoInches(encoder / Elevator.TICKS_PER_REVOLUTION);
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
		return getPosition() > Elevator.LENGTH_OF_TRAVEL_INCHES * 2/3;
	}
	
	public boolean isDown() {
		return getPosition() < Elevator.LENGTH_OF_TRAVEL_INCHES * 1/3;
	}
	
	public boolean isMidway() {
		return !isUp() && !isDown();
	}

	private double convertRevtoInches(double rev) {
		return rev * Elevator.PERIMETER_PULLEY_INCHES / Elevator.GEAR_RATIO;
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
