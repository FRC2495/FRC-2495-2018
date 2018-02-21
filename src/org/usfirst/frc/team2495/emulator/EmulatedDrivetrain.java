package org.usfirst.frc.team2495.emulator;

import org.usfirst.frc.team2495.robot.*;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PIDOutput;


public class EmulatedDrivetrain implements PIDOutput, IDrivetrain {

	public EmulatedDrivetrain() 
	{	
	}
	
	// this method needs to be paired with checkTurnAngleUsingPidController()
	public void turnAngleUsingPidController(double angle) {
		System.out.println("Drivetrain: BEGIN turn angle using PID controller: " + angle + " degrees");
	}
		
	// This method checks that we are within target up to ON_TARGET_MINIMUM_COUNT times
	// It relies on its own counter
	public boolean tripleCheckTurnAngleUsingPidController() {	
		return false;
	}
	
	// do not use in teleop - for auton only
	public void waitTurnAngleUsingPidController() {
		System.out.println("Drivetrain: END turn angle using PID controller");
	}

	// this method needs to be paired with checkMoveDistance()
	public void moveDistance(double dist) // moves the distance in inch given
	{
		System.out.println("Drivetrain: BEGIN move distance: " + dist + " inches");
	}
	
	public boolean tripleCheckMoveDistance() {
		return false;
	}
	
	// do not use in teleop - for auton only
	public void waitMoveDistance() {
		System.out.println("Drivetrain: END move distance");
	}
	
	// this method needs to be paired with checkMoveDistance()
	public void moveDistanceAlongArc(int angle) {
		System.out.println("Drivetrain: BEGIN move distance along arc: " + angle + " degrees");
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

	@Override
	public void pidWrite(double output) {
		
	}	
	
	// MAKE SURE THAT YOU ARE NOT IN A CLOSED LOOP CONTROL MODE BEFORE CALLING THIS METHOD.
	// OTHERWISE THIS IS EQUIVALENT TO MOVING TO THE DISTANCE TO THE CURRENT ZERO IN REVERSE! 
	public void resetEncoders() {
	}	
}


