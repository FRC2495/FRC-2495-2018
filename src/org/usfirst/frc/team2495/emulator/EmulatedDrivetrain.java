package org.usfirst.frc.team2495.emulator;

import org.usfirst.frc.team2495.robot.*;
import org.usfirst.frc.team2495.robot.Jack.Position;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PIDOutput;


public class EmulatedDrivetrain implements PIDOutput, IDrivetrain {

	int leftEncoder = 0;
	int rightEncoder = 0;
	
	int gyro = 0;
	
	IJack jack;
	
	PositionTracker tracker;
	
	public EmulatedDrivetrain(IJack jack_in, PositionTracker tracker_in)
	{	
		jack = jack_in;
		
		tracker = tracker_in;
	}
	
	private void printState() {
		System.out.println("Drivetrain: STATE left position = " + getLeftValue() +
				" inches, right position = " + getRightValue() + " inches, gyro = " + gyro + " degrees\n");
	}
	
	// this method needs to be paired with checkTurnAngleUsingPidController()
	public void turnAngleUsingPidController(double angle) {
		System.out.print("Drivetrain: BEGIN turn angle using PID controller: " + angle + " degrees ");
			
		if (angle > 0) {
			System.out.println("(turn right " + Math.abs(angle) + " degrees)");
		} else if (angle < 0) {
			System.out.println("(turn left " + Math.abs(angle) + " degrees)");
		} else {
			System.out.println("(no move)");
		}
		
		if (jack != null && (jack.getPosition() != Position.UP)) {
			System.out.println("VIOLATION: cannot move drivetrain when jack is up!");
		}
		
		gyro = (int)angle;
		
		double dist = arclength((int)angle);
		double ldist, rdist;
		
		ldist = dist;
		rdist = -dist;
		
		int rtac = (int)(rdist / Drivetrain.PERIMETER_WHEEL_INCHES * Drivetrain.TICKS_PER_REVOLUTION);
		int ltac = (int)(ldist / Drivetrain.PERIMETER_WHEEL_INCHES * Drivetrain.TICKS_PER_REVOLUTION);

		rightEncoder = +rtac;
		leftEncoder = +ltac;
		
		if (tracker != null) {
			tracker.turnAngle(angle);
		}
	}
		
	// This method checks that we are within target up to ON_TARGET_MINIMUM_COUNT times
	// It relies on its own counter
	public boolean tripleCheckTurnAngleUsingPidController() {	
		return false;
	}
	
	// do not use in teleop - for auton only
	public void waitTurnAngleUsingPidController() {
		System.out.println("Drivetrain: END turn angle using PID controller");
		
		printState();
		
		if (tracker != null) {
			tracker.printState();
		}
	}

	// this method needs to be paired with checkMoveDistance()
	public void moveDistance(double dist) // moves the distance in inch given
	{
		System.out.print("Drivetrain: BEGIN move distance: " + dist + " inches ");
		
		if (dist > 0) {
			System.out.println("(move forward " + Math.abs(dist) + " inches)");
		} else if (dist < 0) {
			System.out.println("(move back " + Math.abs(dist) + " inches)");
		} else {
			System.out.println("(no move)");
		}
		
		if (jack != null && (jack.getPosition() != Position.UP)) {
			System.out.println("VIOLATION: cannot move drivetrain when jack is up!");
		}
		
		int ltac = (int)(dist / Drivetrain.PERIMETER_WHEEL_INCHES * Drivetrain.TICKS_PER_REVOLUTION);
		int rtac = (int)(dist / Drivetrain.PERIMETER_WHEEL_INCHES * Drivetrain.TICKS_PER_REVOLUTION);
				
		ltac = + ltac;
		rtac = + rtac;
		
		leftEncoder = ltac;
		rightEncoder = rtac;
		
		if (tracker != null) {
			tracker.moveDistance(dist);
		}
	}
	
	public boolean tripleCheckMoveDistance() {
		return false;
	}
	
	// do not use in teleop - for auton only
	public void waitMoveDistance() {
		System.out.println("Drivetrain: END move distance");
				
		printState();

		if (tracker != null) {
			tracker.printState();
		}
	}
	
	private double arclength(int angle) // returns the inches needed to be moved
	// to turn the specified angle
	{
		return Math.toRadians(angle) * Drivetrain.RADIUS_DRIVEVETRAIN_INCHES;
	}
	
	// this method needs to be paired with checkMoveDistance()
	public void moveDistanceAlongArc(int angle) {
		System.out.println("Drivetrain: BEGIN move distance along arc: " + angle + " degrees");
		
		if (jack != null && (jack.getPosition() != Position.UP)) {
			System.out.println("VIOLATION: cannot move drivetrain when jack is up!");
		}
		
		gyro = angle;
		
		double dist = arclength(angle);
		double ldist, rdist;
		
		ldist = dist;
		rdist = -dist;
		
		int rtac = (int)(rdist / Drivetrain.PERIMETER_WHEEL_INCHES * Drivetrain.TICKS_PER_REVOLUTION);
		int ltac = (int)(ldist / Drivetrain.PERIMETER_WHEEL_INCHES * Drivetrain.TICKS_PER_REVOLUTION);

		rightEncoder = +rtac;
		leftEncoder = +ltac;
		
		if (tracker != null) {
			tracker.turnAngle(angle);
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
	
	public int getRightEncoderValue() {
		return rightEncoder;
	}

	public int getLeftEncoderValue() {
		return leftEncoder;
	}

	public int getRightValue() {
		return (int) (rightEncoder*Drivetrain.PERIMETER_WHEEL_INCHES/Drivetrain.TICKS_PER_REVOLUTION);
	}

	public int getLeftValue() {
		return (int) (leftEncoder*Drivetrain.PERIMETER_WHEEL_INCHES/Drivetrain.TICKS_PER_REVOLUTION);
	}
	
	public boolean isMoving(){
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
		leftEncoder = 0;
		rightEncoder = 0;
	}	
}


