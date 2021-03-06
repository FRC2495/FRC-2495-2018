package org.usfirst.frc.team2495.emulator;


import org.usfirst.frc.team2495.robot.*;
import org.usfirst.frc.team2495.robot.Jack.Position;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PIDOutput;


public class EmulatedMiniDrivetrain implements PIDOutput, IMiniDrivetrain {

	int leftEncoder = 0;
	int rightEncoder = 0;
	
	IJack jack;
	
	PositionTracker tracker;
	
	boolean active = false;
	
	
	public EmulatedMiniDrivetrain(IJack jack_in, PositionTracker tracker_in)
	{	
		jack = jack_in;
		
		tracker = tracker_in;
	}
	
	public void moveUsingCameraPidController()
	{
		System.out.println("MiniDrivetrain: BEGIN move using camera");
		
		if (active) {
			System.out.println("VIOLATION: forgot to wait for prior action to complete!");
		}
		active = true;
		
		if (jack != null && (jack.getPosition() != Position.MINI_DRIVETRAIN)) {
			System.out.println("VIOLATION: cannot move mini drivetrain using camera when jack is not down!");
		}
	}
		
	public boolean tripleCheckMoveUsingCameraPidController()
	{
		return false;
	}
		
	// do not use in teleop - for auton only
	public void waitMoveUsingCameraPidController()
	{
		System.out.println("MiniDrivetrain: END move using camera");
		active = false;
		
		printState();
		
		if (tracker != null) {
			tracker.printState();
		}	
	}
	
	// this method needs to be paired with checkMoveDistance()
	public void moveDistance(double dist) // moves the distance in inch given
	{
		System.out.print("MiniDrivetrain: BEGIN move distance: " + dist + " inches ");
				
		if (dist > 0) {
			System.out.println("(move arthwart right " + Math.abs(dist) + " inches)");
		} else if (dist < 0) {
			System.out.println("(move arthwart left " + Math.abs(dist) + " inches)");
		} else {
			System.out.println("(no move)");
		}
		
		if (active) {
			System.out.println("VIOLATION: forgot to wait for prior action to complete!");
		}
		active = true;
		
		if (jack != null && (jack.getPosition() != Position.MINI_DRIVETRAIN)) {
			System.out.println("VIOLATION: cannot move mini drivetrain when jack is not down!");
		}
		
		int ltac = (int)(dist / MiniDrivetrain.PERIMETER_WHEEL_INCHES * MiniDrivetrain.TICKS_PER_REVOLUTION);
		int rtac = (int)(dist / MiniDrivetrain.PERIMETER_WHEEL_INCHES * MiniDrivetrain.TICKS_PER_REVOLUTION);
				
		ltac = + ltac;
		rtac = + rtac;
		
		leftEncoder = ltac;
		rightEncoder = rtac;	
		
		if (tracker != null) {
			tracker.moveDistanceAthwart(dist);
		}
	}
	
	public boolean tripleCheckMoveDistance() {
		return false;
	}
	
	private void printState() {
		System.out.println("MiniDrivetrain: STATE left position = " + getLeftPosition() +
				" inches, right position = " + getRightPosition() + " inches\n");
	}
	
	// do not use in teleop - for auton only
	public void waitMoveDistance() {
		System.out.println("MiniDrivetrain: END move distance");
		active = false;
		
		printState();
		
		if (tracker != null) {
			tracker.printState();
		}
	}
		
	public void stop() {
	}
    
	public void setPIDParameters()
	{
	}
	
	// NOTE THAT THIS METHOD WILL IMPACT BOTH OPEN AND CLOSED LOOP MODES
	public void setNominalAndPeakOutputs(double peakOutput)
	{
	}

	public void joystickControl(Joystick joyLeft, Joystick joyRight, boolean held) // sets talons to
	{
	}	
	
	public int getRightEncoderPosition() {
		return rightEncoder;
	}

	public int getLeftEncoderPosition() {
		return leftEncoder;
	}

	public int getRightPosition() {
		return (int) (rightEncoder*MiniDrivetrain.PERIMETER_WHEEL_INCHES/MiniDrivetrain.TICKS_PER_REVOLUTION);
	}

	public int getLeftPosition() {
		return (int) (leftEncoder*MiniDrivetrain.PERIMETER_WHEEL_INCHES/MiniDrivetrain.TICKS_PER_REVOLUTION);
	}
	
	public boolean isMoving() {
		return false;
	}
	
	public boolean isMovingUsingCamera() {
		return false;
	}
	
	@Override
	public void pidWrite(double output) {
	}
		
	// MAKE SURE THAT YOU ARE NOT IN A CLOSED LOOP CONTROL MODE BEFORE CALLING THIS METHOD.
	// OTHERWISE THIS IS EQUIVALENT TO MOVING TO THE DISTANCE TO THE CURRENT ZERO IN REVERSE! 
	public void resetEncoders() {
		leftEncoder = 0;
		rightEncoder = 0;
	}	
}


