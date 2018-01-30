package org.usfirst.frc.team2495.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

public class Drivetrain {

	boolean isMoving;  
	
	double ltac, rtac; 
	static final double PERIMETER_WHEEL_INCHES = 4 * Math.PI;
	static final int PRIMARY_PID_LOOP = 0;
	static final double TICK_THRESH = 512;
	static final double MAX_PCT_OUTPUT = 1.0;
	static final double REDUCED_PCT_OUTPUT = 0.5;
	static final double MIN_ROTATE_PCT_OUTPUT = 0.25; 
	static final int SLOT_0 = 0;
	static final int TALON_TIMEOUT_MS = 10;
	static final int TICKS_PER_REVOLUTION = 4096;
	
	private int onTargetCount; // counter indicating how many times/iterations we were on target
	private final static int ON_TARGET_MINIMUM_COUNT = 25; // number of times/iterations we need to be on target to really be on target
	
	WPI_TalonSRX frontLeft,rearLeft,frontRight,rearRight;
	DifferentialDrive differentialDrive; 
	
	public Drivetrain(WPI_TalonSRX frontLeft_in ,WPI_TalonSRX frontRight_in , WPI_TalonSRX rearLeft_in ,WPI_TalonSRX rearRight_in) 
	{
		frontLeft = frontLeft_in;
		frontRight = frontRight_in;
		rearLeft = rearLeft_in;
		rearRight = rearRight_in;
		
		frontLeft.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative,
				PRIMARY_PID_LOOP, TALON_TIMEOUT_MS);
				
		frontRight.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative,
				PRIMARY_PID_LOOP, TALON_TIMEOUT_MS);
		
		frontLeft.setSensorPhase(true);
		frontRight.setSensorPhase(true);	
		
		frontLeft.setInverted(true);
		frontRight.setInverted(false);
		rearLeft.setInverted(true); 
		rearRight.setInverted(false);
		
		// motors will turn in opposite directions if not inverted 
		
		rearLeft.follow(frontLeft);
		rearRight.follow(frontRight);
		
		differentialDrive = new DifferentialDrive(frontLeft, frontRight);
		differentialDrive.setSafetyEnabled(false); // disables the stupid timeout error when we run in closed loop
	}
	
	public void moveDistance(double dist) // moves the distance in inch given
	{
		resetEncoders();
		setPIDParameters();
		setNominalAndPeakOutputs(REDUCED_PCT_OUTPUT); //this has a global impact, so we reset in stop()
		
		rtac = dist / PERIMETER_WHEEL_INCHES * TICKS_PER_REVOLUTION;
		ltac = dist / PERIMETER_WHEEL_INCHES * TICKS_PER_REVOLUTION;
		
		rtac = - rtac; // account for fact that front of robot is back from sensor's point of view
		ltac = - ltac;
		
		System.out.println("rtac, ltac: " + rtac + ", " + ltac);
		frontRight.set(ControlMode.Position, rtac);
		frontLeft.set(ControlMode.Position, ltac);

		//hi
		
		isMoving = true;
		onTargetCount = 0;
	}

	public boolean tripleCheckMoveDistance() {
		if (isMoving) {
			
			double rerror = frontRight.getClosedLoopError(PRIMARY_PID_LOOP);
			double lerror = frontLeft.getClosedLoopError(PRIMARY_PID_LOOP);
			
			boolean isOnTarget = (Math.abs(rerror) < TICK_THRESH && Math.abs(lerror) < TICK_THRESH);
			
			if (isOnTarget) { // if we are on target in this iteration 
				onTargetCount++; // we increase the counter
			} else { // if we are not on target in this iteration
				if (onTargetCount > 0) { // even though we were on target at least once during a previous iteration
					onTargetCount = 0; // we reset the counter as we are not on target anymore
					System.out.println("Triple-check failed (moving).");
				} else {
					// we are definitely moving
				}
			}
			
	        if (onTargetCount > ON_TARGET_MINIMUM_COUNT) { // if we have met the minimum
	        	isMoving = false;
	        }
			
			if (!isMoving) {
				System.out.println("You have reached the target (moving).");
				stop();				 
			}
		}
		return isMoving;
	}
	
	public void stop() {
		//turnPidController.disable(); // exits PID loop
		 
		frontLeft.set(ControlMode.PercentOutput, 0);
		frontRight.set(ControlMode.PercentOutput, 0);
		
		isMoving = false;
		//isTurning = false;
		
		setNominalAndPeakOutputs(MAX_PCT_OUTPUT); // we undo what me might have changed
	}
    
	public void setPIDParameters()
	{
		frontRight.configAllowableClosedloopError(SLOT_0, 128, TALON_TIMEOUT_MS);
		frontLeft.configAllowableClosedloopError(SLOT_0, 128, TALON_TIMEOUT_MS);
		
		// P is the proportional gain. It modifies the closed-loop output by a proportion (the gain value)
		// of the closed-loop error.
		// P gain is specified in output unit per error unit.
		// When tuning P, it's useful to estimate your starting value.
		// If you want your mechanism to drive 50% output when the error is 4096 (one rotation when using CTRE Mag Encoder),
		// then the calculated Proportional Gain would be (0.50 X 1023) / 4096 = ~0.125.
		
		// I is the integral gain. It modifies the closed-loop output according to the integral error
		// (summation of the closed-loop error each iteration).
		// I gain is specified in output units per integrated error.
		// If your mechanism never quite reaches your target and using integral gain is viable,
		// start with 1/100th of the Proportional Gain.
		
		// D is the derivative gain. It modifies the closed-loop output according to the derivative error
		// (change in closed-loop error each iteration).
		// D gain is specified in output units per derivative error.
		// If your mechanism accelerates too abruptly, Derivative Gain can be used to smooth the motion.
		// Typically start with 10x to 100x of your current Proportional Gain.
		
		frontRight.config_kP(SLOT_0, 0.4, TALON_TIMEOUT_MS);
		frontRight.config_kI(SLOT_0, 0, TALON_TIMEOUT_MS);
		frontRight.config_kD(SLOT_0, 0, TALON_TIMEOUT_MS);
		
		frontLeft.config_kP(SLOT_0, 0.4, TALON_TIMEOUT_MS);
		frontLeft.config_kI(SLOT_0, 0, TALON_TIMEOUT_MS);
		frontLeft.config_kD(SLOT_0, 0, TALON_TIMEOUT_MS);		
	}
	
	// NOTE THAT THIS METHOD WILL IMPACT BOTH OPEN AND CLOSED LOOP MODES
	public void setNominalAndPeakOutputs(double peakOutput)
	{
		frontLeft.configPeakOutputForward(peakOutput, TALON_TIMEOUT_MS);
		frontLeft.configPeakOutputReverse(-peakOutput, TALON_TIMEOUT_MS);
		frontRight.configPeakOutputForward(peakOutput, TALON_TIMEOUT_MS);
		frontRight.configPeakOutputReverse(-peakOutput, TALON_TIMEOUT_MS);
		
		frontRight.configNominalOutputForward(0, TALON_TIMEOUT_MS);
		frontLeft.configNominalOutputForward(0, TALON_TIMEOUT_MS);
		frontRight.configNominalOutputReverse(0, TALON_TIMEOUT_MS);
		frontLeft.configNominalOutputReverse(0, TALON_TIMEOUT_MS);
	}

	
	public void joystickControl(Joystick joyLeft, Joystick joyRight, boolean held) // sets talons to
	// joystick control
	{
		if (!isMoving) // if we are already doing a move or turn we don't take over
		{
			if(!held)
			{

				//frontRight.set(ControlMode.PercentOutput, joyRight.getY() * .75);
				//frontLeft.set(ControlMode.PercentOutput, joyLeft.getY() * .75);
				
				//differentialDrive.tankDrive(joyLeft.getY() * .75, -joyRight.getY() * .75); // right needs to be reversed
				
				differentialDrive.arcadeDrive(-joyRight.getX() * .75, joyLeft.getY() * .75); // right needs to be reversed
			}
			else
			{
				
				//frontRight.set(ControlMode.PercentOutput, joyRight.getY());
				//frontLeft.set(ControlMode.PercentOutput, joyLeft.getY());
				
				//differentialDrive.tankDrive(joyLeft.getY(), -joyRight.getY()); // right needs to be reversed
				
				differentialDrive.arcadeDrive(-joyRight.getX(), joyLeft.getY()); // right needs to be reversed
			}
		}
	}	
	
	public int getRightEncoderValue() {
		return (int) (frontRight.getSelectedSensorPosition(PRIMARY_PID_LOOP));
	}
//
	public int getLeftEncoderValue() {
		return (int) (frontLeft.getSelectedSensorPosition(PRIMARY_PID_LOOP));
	}

	public int getRightValue() {
		return (int) (frontRight.getSelectedSensorPosition(PRIMARY_PID_LOOP)*PERIMETER_WHEEL_INCHES/TICKS_PER_REVOLUTION);
	}

	public int getLeftValue() {
		return (int) (frontLeft.getSelectedSensorPosition(PRIMARY_PID_LOOP)*PERIMETER_WHEEL_INCHES/TICKS_PER_REVOLUTION);
	}
	
	public boolean isMoving() {
		return isMoving;
	}
	
	public void resetEncoders() {
		frontRight.set(ControlMode.PercentOutput, 0); // we switch to open loop to be safe.
		frontLeft.set(ControlMode.PercentOutput, 0);			
		
		frontRight.setSelectedSensorPosition(0, PRIMARY_PID_LOOP, TALON_TIMEOUT_MS);
		frontLeft.setSelectedSensorPosition(0, PRIMARY_PID_LOOP, TALON_TIMEOUT_MS);
	}	
}


