package org.usfirst.frc.team2495.robot;

import java.util.Calendar;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

public class MiniDrivetrain implements PIDOutput {

	// general settings
	static final double DIAMETER_WHEEL_INCHES = 4;
	static final double PERIMETER_WHEEL_INCHES = DIAMETER_WHEEL_INCHES * Math.PI;
	
	static final int TIMEOUT_MS = 15000;	
	
	static final double RADIUS_DRIVEVETRAIN_INCHES = 13; // 12.5;
	
	static final double MAX_PCT_OUTPUT = 1.0;
		
	static final int TALON_TIMEOUT_MS = 10;
	static final int TICKS_PER_REVOLUTION = 4096;
	
	static final int MINI_DRIVETRAIN_POLARITY = 1.0; 
	
	// turn settings
	// NOTE: it might make sense to decrease the PID controller period to 0.02 sec (which is the period used by the main loop)
	static final double TURN_PID_CONTROLLER_PERIOD_SECONDS = .02; // 0.02 sec = 20 ms 	
	
	static final double MIN_TURN_PCT_OUTPUT = 0.2;
	static final double MAX_TURN_PCT_OUTPUT = 0.5;
	
	static final double TURN_PROPORTIONAL_GAIN = 0.04;
	static final double TURN_INTEGRAL_GAIN = 0.0;
	static final double TURN_DERIVATIVE_GAIN = 0.0;
	
	static final int DEGREE_THRESHOLD = 1;
	
	
	// move settings
	static final int PRIMARY_PID_LOOP = 0;
	
	static final int SLOT_0 = 0;
	
	static final double REDUCED_PCT_OUTPUT = 0.5;
	
	static final double MOVE_PROPORTIONAL_GAIN = 0.4;
	static final double MOVE_INTEGRAL_GAIN = 0.0;
	static final double MOVE_DERIVATIVE_GAIN = 0.0;
	
	static final int TALON_TICK_THRESH = 128;
	static final double TICK_THRESH = 512;
	
	
	// shared turn and move settings
	private int onTargetCount; // counter indicating how many times/iterations we were on target
	private final static int ON_TARGET_MINIMUM_COUNT = 25; // number of times/iterations we need to be on target to really be on target

	
	// variables
	boolean isMoving; // indicates that the MiniDrivetrain is moving using the PID controllers embedded on the motor controllers 
	boolean isTurning;  // indicates that the MiniDrivetrain is turning using the PID controller hereunder
	
	double ltac, rtac; // target positions 

	WPI_TalonSRX frontCenter, rearCenter; // motor controllers
	ADXRS450_Gyro gyro; // gyroscope
	
	DifferentialDrive differentialDrive; // a class to simplify tank or arcade drive (open loop driving) 
	
	Robot robot; // a reference to the robot
	
	PIDController turnPidController; // the PID controller used to turn
	
	
	public MiniDrivetrain(WPI_TalonSRX frontCenter_in,WPI_TalonSRX rearCenter_in, ADXRS450_Gyro gyro_in, Robot robot_in) 
	{
		
		frontCenter = frontCenter_in;
		rearCenter = rearCenter_in;
		gyro = gyro_in;	
		robot = robot_in;
		
		// Mode of operation during Neutral output may be set by using the setNeutralMode() function.
		// As of right now, there are two options when setting the neutral mode of a motor controller,
		// brake and coast.
		frontCenter.setNeutralMode(NeutralMode.Brake); // sets the talons on brake mode
		rearCenter.setNeutralMode(NeutralMode.Brake);	
		
		// Sensors for motor controllers provide feedback about the position, velocity, and acceleration
		// of the system using that motor controller.
		// Note: With Phoenix framework, position units are in the natural units of the sensor.
		// This ensures the best resolution possible when performing closed-loops in firmware.
		// CTRE Magnetic Encoder (relative/quadrature) =  4096 units per rotation
		frontCenter.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative,
				PRIMARY_PID_LOOP, TALON_TIMEOUT_MS);
				
		rearCenter.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative,
				PRIMARY_PID_LOOP, TALON_TIMEOUT_MS);
		
		// Sensor phase is the term used to explain sensor direction.
		// In order for limit switches and closed-loop features to function properly the sensor and motor has to be in-phase.
		// This means that the sensor position must move in a positive direction as the motor controller drives positive output.  
		frontCenter.setSensorPhase(true);
		rearCenter.setSensorPhase(true);	
		
		// Disables limit switches
		frontCenter.overrideLimitSwitchesEnable(false);
		rearCenter.overrideLimitSwitchesEnable(false);
		
		// Motor controller output direction can be set by calling the setInverted() function as seen below.
		// Note: Regardless of invert value, the LEDs will blink green when positive output is requested (by robot code or firmware closed loop).
		// Only the motor leads are inverted. This feature ensures that sensor phase and limit switches will properly match the LED pattern
		// (when LEDs are green => forward limit switch and soft limits are being checked). 
		frontCenter.setInverted(false);
		rearCenter.setInverted(false);
	
		
		// motors will turn in opposite directions if not inverted 
		
		// Both the Talon SRX and Victor SPX have a follower feature that allows the motor controllers to mimic another motor controller's output.
		// Users will still need to set the motor controller's direction, and neutral mode.
		// The method follow() allows users to create a motor controller follower of not only the same model, but also other models
		// , talon to talon, victor to victor, talon to victor, and victor to talon.
		/*
		rearLeft.follow(rearCenter);
		rearRight.follow(frontRight);
		*/
		// set peak output to max in case if had been reduced previously
		setNominalAndPeakOutputs(MAX_PCT_OUTPUT);
		
		//creates a PID controller
		turnPidController = new PIDController(TURN_PROPORTIONAL_GAIN, TURN_INTEGRAL_GAIN, TURN_DERIVATIVE_GAIN, gyro, this, TURN_PID_CONTROLLER_PERIOD_SECONDS);
    	
    	turnPidController.setInputRange(-180, 180); // valid input range 
    	turnPidController.setOutputRange(-MAX_TURN_PCT_OUTPUT, MAX_TURN_PCT_OUTPUT); // output range NOTE: might need to change signs
    	
    	turnPidController.setContinuous(true); // because -180 degrees is the same as 180 degrees (needs input range to be defined first)
    	turnPidController.setAbsoluteTolerance(DEGREE_THRESHOLD); // 1 degree error tolerated
		
		differentialDrive = new DifferentialDrive(frontCenter, rearCenter);
		differentialDrive.setSafetyEnabled(false); // disables the stupid timeout error when we run in closed loop
	}
	
	// this method needs to be paired with checkTurnAngleUsingPidController()
	public void turnAngleUsingPidController(double angle) {
		// switches to percentage vbus
		stop(); // resets state
		
		gyro.reset(); // resets to zero for now
		//double current = gyro.getAngle();
		double heading = angle; //+ current; // calculates new heading
		
		turnPidController.setSetpoint(heading); // sets the heading
		turnPidController.enable(); // begins running
		
		isTurning = true;
		onTargetCount = 0;
	}
		
	// This method checks that we are within target up to ON_TARGET_MINIMUM_COUNT times
	// It relies on its own counter
	public boolean tripleCheckTurnAngleUsingPidController() {	
		if (isTurning) {
			boolean isOnTarget = turnPidController.onTarget();
			
			if (isOnTarget) { // if we are on target in this iteration 
				onTargetCount++; // we increase the counter
			} else { // if we are not on target in this iteration
				if (onTargetCount > 0) { // even though we were on target at least once during a previous iteration
					onTargetCount = 0; // we reset the counter as we are not on target anymore
					System.out.println("Triple-check failed (turning).");
				} else {
					// we are definitely turning
				}
			}
			
	        if (onTargetCount > ON_TARGET_MINIMUM_COUNT) { // if we have met the minimum
	        	isTurning = false;
	        }
			
			if (!isTurning) {
				System.out.println("You have reached the target (turning).");
				stop();				 
			}
		}
		return isTurning;
	}
	
	// do not use in teleop - for auton only
	public void waitTurnAngleUsingPidController() {
		long start = Calendar.getInstance().getTimeInMillis();

		while (tripleCheckTurnAngleUsingPidController()) { 		
			if (!DriverStation.getInstance().isAutonomous()
					|| Calendar.getInstance().getTimeInMillis() - start >= TIMEOUT_MS) {
				System.out.println("You went over the time limit (turning)");
				stop();
				break;
			}

			try {
				Thread.sleep(20); // sleeps a little
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			robot.updateToSmartDash();
		}		
		stop();
	}

	// this method needs to be paired with checkMoveDistance()
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
		frontCenter.set(ControlMode.Position, rtac);
		rearCenter.set(ControlMode.Position, ltac);

		isMoving = true;
		onTargetCount = 0;
	}
	
	public boolean tripleCheckMoveDistance() {
		if (isMoving) {
			
			double rerror = frontCenter.getClosedLoopError(PRIMARY_PID_LOOP);
			double lerror =rearCenter.getClosedLoopError(PRIMARY_PID_LOOP);
			
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
	
	// do not use in teleop - for auton only
	public void waitMoveDistance() {
		long start = Calendar.getInstance().getTimeInMillis();
		
		while (tripleCheckMoveDistance()) {
			if (!DriverStation.getInstance().isAutonomous()
					|| Calendar.getInstance().getTimeInMillis() - start >= TIMEOUT_MS) {
				System.out.println("You went over the time limit (moving)");
				stop();
				break;
			}
			
			try {
				Thread.sleep(20); // sleeps a little
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			robot.updateToSmartDash();
		}
	}
	
	private double arclength(int angle) // returns the inches needed to be moved
	// to turn the specified angle
	{
		return Math.toRadians(angle) * RADIUS_DRIVEVETRAIN_INCHES;
	}

	// this method needs to be paired with checkMoveDistance()
	public void moveDistanceAlongArc(int angle) {
		double dist = arclength(angle);
		double ldist, rdist;
		
		ldist = dist;
		rdist = -dist;
		
		resetEncoders();
		setPIDParameters();
		
		rtac = rdist / PERIMETER_WHEEL_INCHES * TICKS_PER_REVOLUTION;
		ltac = ldist / PERIMETER_WHEEL_INCHES * TICKS_PER_REVOLUTION;
		System.out.println("rtac, ltac: " + rtac + ", " + ltac);
		frontCenter.set(ControlMode.Position, -rtac);
		rearCenter.set(ControlMode.Position, -ltac);
		
		isMoving = true;
		onTargetCount = 0;
	}
	
	public void stop() {
		turnPidController.disable(); // exits PID loop
		 
		rearCenter.set(ControlMode.PercentOutput, 0);
		frontCenter.set(ControlMode.PercentOutput, 0);
		
		isMoving = false;
		isTurning = false;
		
		setNominalAndPeakOutputs(MAX_PCT_OUTPUT); // we undo what me might have changed
	}
    
	public void setPIDParameters()
	{
		frontCenter.configAllowableClosedloopError(SLOT_0, TALON_TICK_THRESH, TALON_TIMEOUT_MS);
		rearCenter.configAllowableClosedloopError(SLOT_0, TALON_TICK_THRESH, TALON_TIMEOUT_MS);
		
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
		
		frontCenter.config_kP(SLOT_0, MOVE_PROPORTIONAL_GAIN, TALON_TIMEOUT_MS);
		frontCenter.config_kI(SLOT_0, MOVE_INTEGRAL_GAIN, TALON_TIMEOUT_MS);
		frontCenter.config_kD(SLOT_0, MOVE_DERIVATIVE_GAIN, TALON_TIMEOUT_MS);
		
		rearCenter.config_kP(SLOT_0, MOVE_PROPORTIONAL_GAIN, TALON_TIMEOUT_MS);
		rearCenter.config_kI(SLOT_0, MOVE_INTEGRAL_GAIN, TALON_TIMEOUT_MS);
		rearCenter.config_kD(SLOT_0, MOVE_DERIVATIVE_GAIN, TALON_TIMEOUT_MS);		
	}
	
	// NOTE THAT THIS METHOD WILL IMPACT BOTH OPEN AND CLOSED LOOP MODES
	public void setNominalAndPeakOutputs(double peakOutput)
	{
		rearCenter.configPeakOutputForward(peakOutput, TALON_TIMEOUT_MS);
		rearCenter.configPeakOutputReverse(-peakOutput, TALON_TIMEOUT_MS);
		frontCenter.configPeakOutputForward(peakOutput, TALON_TIMEOUT_MS);
		frontCenter.configPeakOutputReverse(-peakOutput, TALON_TIMEOUT_MS);
		
		frontCenter.configNominalOutputForward(0, TALON_TIMEOUT_MS);
		rearCenter.configNominalOutputForward(0, TALON_TIMEOUT_MS);
		frontCenter.configNominalOutputReverse(0, TALON_TIMEOUT_MS);
		rearCenter.configNominalOutputReverse(0, TALON_TIMEOUT_MS);
	}

	public void joystickControl(Joystick joyLeft, Joystick joyRight, boolean held) // sets talons to
	// joystick control
	
	{
		if (!isMoving && !isTurning) // if we are already doing a move or turn we don't take over
		{
			if(!held)
			{

				//frontCenter.set(ControlMode.PercentOutput, joyRight.getY() * .75);
				//rearCenter.set(ControlMode.PercentOutput, joyLeft.getY() * .75);
				
				//differentialDrive.tankDrive(joyLeft.getY() * .75, -joyRight.getY() * .75); // right needs to be reversed
				
				//differentialDrive.arcadeDrive(-joyRight.getX() * .75, joyLeft.getY() * .75); // right needs to be reversed
				differentialDrive.arcadeDrive(-joyRight.getY() * .75, joyLeft.getX() * .75); // right needs to be reversed
			}
			else
			{
				
				//frontCenter.set(ControlMode.PercentOutput, joyRight.getY());
				//rearCenter.set(ControlMode.PercentOutput, joyLeft.getY());
				
				//differentialDrive.tankDrive(joyLeft.getY(), -joyRight.getY()); // right needs to be reversed
				
				//differentialDrive.arcadeDrive(-joyRight.getX(), joyLeft.getY()); // right needs to be reversed
				differentialDrive.arcadeDrive(-joyRight.getY(), joyLeft.getX()); // right needs to be reversed
			}
		}
	}	
	
	public int getRightEncoderValue() {
		return (int) (frontCenter.getSelectedSensorPosition(PRIMARY_PID_LOOP));
	}

	public int getLeftEncoderValue() {
		return (int) (rearCenter.getSelectedSensorPosition(PRIMARY_PID_LOOP));
	}

	public int getRightValue() {
		return (int) (frontCenter.getSelectedSensorPosition(PRIMARY_PID_LOOP)*PERIMETER_WHEEL_INCHES/TICKS_PER_REVOLUTION);
	}

	public int getLeftValue() {
		return (int) (rearCenter.getSelectedSensorPosition(PRIMARY_PID_LOOP)*PERIMETER_WHEEL_INCHES/TICKS_PER_REVOLUTION);
	}
	
	public boolean isMoving() {
		return isMoving;
	}
	
	public boolean isTurning(){
		return isTurning;
	}

	@Override
	public void pidWrite(double output) {
		
		if(Math.abs(turnPidController.getError()) < DEGREE_THRESHOLD)
		{
			output = 0;
		}
		if(output != 0 && Math.abs(output) < MIN_TURN_PCT_OUTPUT)
		{
			double sign = output > 0 ? 1.0 : -1.0;
			output = MIN_TURN_PCT_OUTPUT * sign;
		}
		frontCenter.set(ControlMode.PercentOutput, +output);
		rearCenter.set(ControlMode.PercentOutput, -output);		
	}	
	
	// MAKE SURE THAT YOU ARE NOT IN A CLOSED LOOP CONTROL MODE BEFORE CALLING THIS METHOD.
	// OTHERWISE THIS IS EQUIVALENT TO MOVING TO THE DISTANCE TO THE CURRENT ZERO IN REVERSE! 
	public void resetEncoders() {
		frontCenter.set(ControlMode.PercentOutput, 0); // we switch to open loop to be safe.
		rearCenter.set(ControlMode.PercentOutput, 0);			
		
		frontCenter.setSelectedSensorPosition(0, PRIMARY_PID_LOOP, TALON_TIMEOUT_MS);
		rearCenter.setSelectedSensorPosition(0, PRIMARY_PID_LOOP, TALON_TIMEOUT_MS);
	}	
}


