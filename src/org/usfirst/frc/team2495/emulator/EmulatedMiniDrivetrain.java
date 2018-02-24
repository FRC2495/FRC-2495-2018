package org.usfirst.frc.team2495.emulator;

import java.util.Calendar;

import org.usfirst.frc.team2495.robot.*;
import org.usfirst.frc.team2495.robot.Jack.Position;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;


public class EmulatedMiniDrivetrain implements PIDOutput, IMiniDrivetrain {

	int leftEncoder = 0;
	int rightEncoder = 0;
	
	IJack jack;
	
	PositionTracker tracker;
	
	private int onTargetCount; // counter indicating how many times/iterations we were on target
	boolean isMovingUsingCamera;  // indicates that the drivetrain is moving using the PID controller hereunder
	
	HMCamera camera;
	
	PIDController moveUsingCameraPidController; // the PID controller used to turn
	
	
	public EmulatedMiniDrivetrain(IJack jack_in, PositionTracker tracker_in, HMCamera camera_in)
	{	
		jack = jack_in;
		
		tracker = tracker_in;
		
		camera = camera_in;
	
		//creates a PID controller
		moveUsingCameraPidController = new PIDController(MiniDrivetrain.MOVE_USING_CAMERA_PROPORTIONAL_GAIN, MiniDrivetrain.MOVE_USING_CAMERA_INTEGRAL_GAIN, MiniDrivetrain.MOVE_USING_CAMERA_DERIVATIVE_GAIN, camera, this, MiniDrivetrain.MOVE_USING_CAMERA_PID_CONTROLLER_PERIOD_SECONDS);
	    	
		moveUsingCameraPidController.setInputRange(-HMCamera.HORIZONTAL_CAMERA_RES_PIXELS, HMCamera.HORIZONTAL_CAMERA_RES_PIXELS); // valid input range 
		moveUsingCameraPidController.setOutputRange(-MiniDrivetrain.MAX_MOVE_USING_CAMERA_PCT_OUTPUT, MiniDrivetrain.MAX_MOVE_USING_CAMERA_PCT_OUTPUT); // output range NOTE: might need to change signs
	    	
		moveUsingCameraPidController.setAbsoluteTolerance(MiniDrivetrain.PIXEL_THRESHOLD); // error tolerated
	}
	
	public void moveUsingCameraPidController()
	{
		System.out.print("MiniDrivetrain: BEGIN move using camera");
		
		if (jack != null && (jack.getPosition() != Position.DOWN)) {
			System.out.println("VIOLATION: cannot move mini drivetrain using camera when jack is not down!");
		}
		
		// switches to percentage vbus
		stop(); // resets state 
		
		moveUsingCameraPidController.setSetpoint(0); // we want to end centered
		moveUsingCameraPidController.enable(); // begins running
		
		isMovingUsingCamera = true;
		onTargetCount = 0;
	}
		
	public boolean tripleCheckMoveUsingCameraPidController()
	{
		if (isMovingUsingCamera) {
			boolean isOnTarget = moveUsingCameraPidController.onTarget();
			
			if (isOnTarget) { // if we are on target in this iteration 
				onTargetCount++; // we increase the counter
			} else { // if we are not on target in this iteration
				if (onTargetCount > 0) { // even though we were on target at least once during a previous iteration
					onTargetCount = 0; // we reset the counter as we are not on target anymore
					System.out.println("Triple-check failed (moving using camera).");
				} else {
					// we are definitely turning
				}
			}
			
	        if (onTargetCount > MiniDrivetrain.ON_TARGET_MINIMUM_COUNT) { // if we have met the minimum
	        	isMovingUsingCamera = false;
	        }
			
			if (!isMovingUsingCamera) {
				System.out.println("You have reached the target (moving using camera).");
				stop();				 
			}
		}
		return isMovingUsingCamera;
	}
		
	// do not use in teleop - for auton only
	public void waitMoveUsingCameraPidController()
	{
		long start = Calendar.getInstance().getTimeInMillis();

		while (tripleCheckMoveUsingCameraPidController()) { 		
			if (!DriverStation.getInstance().isAutonomous()
					|| Calendar.getInstance().getTimeInMillis() - start >= MiniDrivetrain.TIMEOUT_MS) {
				System.out.println("You went over the time limit (moving using camera)");
				stop();
				break;
			}

			try {
				Thread.sleep(20); // sleeps a little
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			//robot.updateToSmartDash();
		}		
		stop();
		
		System.out.println("MiniDrivetrain: END move using camera");
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
		
		if (jack != null && (jack.getPosition() != Position.DOWN)) {
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
		System.out.println("MiniDrivetrain: STATE left position = " + getLeftValue() +
				" inches, right position = " + getRightValue() + " inches\n");
	}
	
	// do not use in teleop - for auton only
	public void waitMoveDistance() {
		System.out.println("MiniDrivetrain: END move distance");
		
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
	
	public boolean isMovingUsingCamera() {
		return isMovingUsingCamera;
	}
	
	@Override
	public void pidWrite(double output) {
		if(Math.abs(moveUsingCameraPidController.getError()) < MiniDrivetrain.PIXEL_THRESHOLD)
		{
			output = 0;
		}
		if(output != 0 && Math.abs(output) < MiniDrivetrain.MIN_MOVE_USING_CAMERA_PCT_OUTPUT)
		{
			double sign = output > 0 ? 1.0 : -1.0;
			output = MiniDrivetrain.MIN_MOVE_USING_CAMERA_PCT_OUTPUT * sign;
		}
		
		System.out.println("MiniDrivetrain.pidWrite() output is " + output + "\n");
	}
		
	// MAKE SURE THAT YOU ARE NOT IN A CLOSED LOOP CONTROL MODE BEFORE CALLING THIS METHOD.
	// OTHERWISE THIS IS EQUIVALENT TO MOVING TO THE DISTANCE TO THE CURRENT ZERO IN REVERSE! 
	public void resetEncoders() {
		leftEncoder = 0;
		rightEncoder = 0;
	}	
}


