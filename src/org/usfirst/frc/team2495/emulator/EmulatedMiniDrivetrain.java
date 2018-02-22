package org.usfirst.frc.team2495.emulator;

import org.usfirst.frc.team2495.robot.*;


import edu.wpi.first.wpilibj.Joystick;


public class EmulatedMiniDrivetrain implements IMiniDrivetrain {

	int leftEncoder = 0;
	int rightEncoder = 0;
	
	public EmulatedMiniDrivetrain() 
	{	
	}	
	
	// this method needs to be paired with checkMoveDistance()
	public void moveDistance(double dist) // moves the distance in inch given
	{
		System.out.print("MiniDrivetrain: BEGIN move distance: " + dist + " inches ");
		
		if (dist > 0) {
			System.out.println("(slide right " + Math.abs(dist) + " inches)");
		} else if (dist < 0) {
			System.out.println("(slide left " + Math.abs(dist) + " inches)");
		} else {
			System.out.println("(no move)");
		}		
		
		int ltac = (int)(dist / MiniDrivetrain.PERIMETER_WHEEL_INCHES * MiniDrivetrain.TICKS_PER_REVOLUTION);
		int rtac = (int)(dist / MiniDrivetrain.PERIMETER_WHEEL_INCHES * MiniDrivetrain.TICKS_PER_REVOLUTION);
				
		ltac = - ltac;
		rtac = - rtac;
		
		leftEncoder = ltac;
		rightEncoder = rtac;		
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
		return rightEncoder;
	}

	public int getLeftEncoderValue() {
		return leftEncoder;
	}

	public int getRightValue() {
		return (int) (rightEncoder*MiniDrivetrain.PERIMETER_WHEEL_INCHES/MiniDrivetrain.TICKS_PER_REVOLUTION);
	}

	public int getLeftValue() {
		return (int) (leftEncoder*MiniDrivetrain.PERIMETER_WHEEL_INCHES/MiniDrivetrain.TICKS_PER_REVOLUTION);
	}
	
	public boolean isMoving() {
		return false;
	}
		
	// MAKE SURE THAT YOU ARE NOT IN A CLOSED LOOP CONTROL MODE BEFORE CALLING THIS METHOD.
	// OTHERWISE THIS IS EQUIVALENT TO MOVING TO THE DISTANCE TO THE CURRENT ZERO IN REVERSE! 
	public void resetEncoders() {
		leftEncoder = 0;
		rightEncoder = 0;
	}	
}


