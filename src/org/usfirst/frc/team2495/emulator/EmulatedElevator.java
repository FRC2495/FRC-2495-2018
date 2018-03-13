package org.usfirst.frc.team2495.emulator;

import java.text.DecimalFormat;

import org.usfirst.frc.team2495.robot.*;

import edu.wpi.first.wpilibj.Joystick;


public class EmulatedElevator implements IElevator {
    
	private boolean isFwdLimitSwitchClosed = true;
	
	private int encoder = 0;
	
	boolean hasBeenHomed = false;
	
	IHinge hinge;
	
	PositionTracker tracker;
	
	boolean active = false;
	
	
	public EmulatedElevator(IHinge hinge_in, PositionTracker tracker_in) {
		hinge = hinge_in;
		
		tracker = tracker_in;
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
		
		if (active) {
			System.out.println("VIOLATION: forgot to wait for prior action to complete!");
		}
		active = true;
		
		if (hinge != null && (!hinge.hasBeenHomed() || !hinge.isDown())) {
			System.out.println("VIOLATION: cannot home elevator when hinge has not been homed or is not down!");
		}
		
		isFwdLimitSwitchClosed = true;
		
		encoder = 0; // by definition
		
		hasBeenHomed = true;
	}

	// this method need to be called to assess the homing progress
	// (and it takes care of going to step 2 if needed)
	public boolean checkHome() {
		return false;
	}

	public void printState() {
		DecimalFormat df = new DecimalFormat("#.#");
		
		System.out.println("Elevator: STATE homed = " + hasBeenHomed() + ", position = " + df.format(getPosition()) + " inches, " +
				"down = " + isDown() + ", midway = " + isMidway() + ", up = " + isUp() + "\n");
	}
	
	// do not use in teleop - for auton only
	public void waitHome() {
		System.out.println("Elevator: END home");
		active = false;
		
		printState();
		
		if (tracker != null) {
			tracker.printState();
		}
	}
	
	// This method should be called to assess the progress of a move
	public boolean tripleCheckMove() {
		return false;
	}

	// do not use in teleop - for auton only
	public void waitMove() {
		System.out.println("Elevator: END move");
		active = false;
		
		printState();
		
		if (tracker != null) {
			tracker.printState();
		}
	}
	
	public void moveUp() {
		System.out.println("Elevator: BEGIN move up (move up to scale)");
		
		if (active) {
			System.out.println("VIOLATION: forgot to wait for prior action to complete!");
		}
		active = true;
		
		if (hinge != null && (!hinge.hasBeenHomed() || !hinge.isDown())) {
			System.out.println("VIOLATION: cannot move elevator up when hinge has not been homed or is not down!");
		}
		
		isFwdLimitSwitchClosed = false;
		
		encoder = (int)(+convertInchesToRev(Elevator.LENGTH_OF_TRAVEL_INCHES) * Elevator.TICKS_PER_REVOLUTION);
		
		if (tracker != null) {
			tracker.updateAltitude(getPosition());
		}
	}

	public void moveMidway() {		
		System.out.println("Elevator: BEGIN move midway (move up to switch)");
		
		if (active) {
			System.out.println("VIOLATION: forgot to wait for prior action to complete!");
		}
		active = true;
		
		if (hinge != null && (!hinge.hasBeenHomed() || !hinge.isDown())) {
			System.out.println("VIOLATION: cannot move elevator midway when hinge has not been homed or is not down!");
		}
		
		isFwdLimitSwitchClosed = false;
		
		encoder = (int)(+convertInchesToRev(Elevator.LENGTH_OF_TRAVEL_INCHES / 2) * Elevator.TICKS_PER_REVOLUTION);
		
		if (tracker != null) {
			tracker.updateAltitude(getPosition());
		}
	}
	
	public void moveDown() {
		System.out.println("Elevator: BEGIN move down (move down)");
		
		if (active) {
			System.out.println("VIOLATION: forgot to wait for prior action to complete!");
		}
		active = true;
		
		if (hinge != null && (!hinge.hasBeenHomed() || !hinge.isDown())) {
			System.out.println("VIOLATION: cannot move elevator down when hinge has not been homed or is not down!");
		}
		
		isFwdLimitSwitchClosed = true; // or maybe not if at virtual home but whatever
		
		encoder = 0;
		
		if (tracker != null) {
			tracker.updateAltitude(getPosition());
		}
	}

	public double getPosition() {
		return convertRevtoInches(encoder / Elevator.TICKS_PER_REVOLUTION);
	}

	public double getEncoderPosition() {
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

	private double convertInchesToRev(double inches) {
		return inches / Elevator.PERIMETER_PULLEY_INCHES * Elevator.GEAR_RATIO;
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
