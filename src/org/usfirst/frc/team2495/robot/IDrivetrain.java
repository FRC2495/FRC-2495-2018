package org.usfirst.frc.team2495.robot;

import edu.wpi.first.wpilibj.Joystick;

public interface IDrivetrain {
	
	// this method needs to be paired with checkTurnAngleUsingPidController()
	public void turnAngleUsingPidController(double angle);
	
	// do not use in teleop - for auton only
	public void waitTurnAngleUsingPidController();
	
	// this method needs to be paired with checkMoveDistance()
	public void moveDistance(double dist);
	
	public boolean tripleCheckMoveDistance();
	
	// do not use in teleop - for auton only
	public void waitMoveDistance();

	// this method needs to be paired with checkMoveDistance()
	public void moveDistanceAlongArc(int angle);
    
	public void setPIDParameters();
	
	// NOTE THAT THIS METHOD WILL IMPACT BOTH OPEN AND CLOSED LOOP MODES
	public void setNominalAndPeakOutputs(double peakOutput);
	
	public void joystickControl(Joystick joyLeft, Joystick joyRight, boolean held);
	
	public int getRightEncoderValue();

	public int getLeftEncoderValue();

	public int getRightValue();

	public int getLeftValue();
	
	public boolean isMoving();
	
	public boolean isTurning();
		
	// MAKE SURE THAT YOU ARE NOT IN A CLOSED LOOP CONTROL MODE BEFORE CALLING THIS METHOD.
	// OTHERWISE THIS IS EQUIVALENT TO MOVING TO THE DISTANCE TO THE CURRENT ZERO IN REVERSE! 
	public void resetEncoders();
}

