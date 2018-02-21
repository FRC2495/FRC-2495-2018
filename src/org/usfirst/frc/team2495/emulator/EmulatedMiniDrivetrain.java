package org.usfirst.frc.team2495.emulator;

import org.usfirst.frc.team2495.robot.*;

import edu.wpi.first.wpilibj.Joystick;


public class EmulatedMiniDrivetrain implements IMiniDrivetrain {

	public EmulatedMiniDrivetrain() 
	{	
	}
	
	
	// this method needs to be paired with checkMoveDistance()
	public void moveDistance(double dist) // moves the distance in inch given
	{
		System.out.println("MiniDrivetrain: BEGIN move distance: " + dist + " inches");
	}
	
	public boolean tripleCheckMoveDistance() {
		return false;
	}
	
	// do not use in teleop - for auton only
	public void waitMoveDistance() {
		System.out.println("MiniDrivetrain: END move distance");
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
	
	public int getRightEncoderValue() {
		return 123456789;
	}

	public int getLeftEncoderValue() {
		return 12345789;
	}

	public int getRightValue() {
		return 12345;
	}

	public int getLeftValue() {
		return 12345;
	}
	
	public boolean isMoving() {
		return false;
	}
	
	public boolean isTurning(){
		return false;
	}
	
	// MAKE SURE THAT YOU ARE NOT IN A CLOSED LOOP CONTROL MODE BEFORE CALLING THIS METHOD.
	// OTHERWISE THIS IS EQUIVALENT TO MOVING TO THE DISTANCE TO THE CURRENT ZERO IN REVERSE! 
	public void resetEncoders() {
	}	
}


