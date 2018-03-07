package org.usfirst.frc.team2495.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.ControlMode;

import java.util.Calendar;

public class Elevator implements IElevator {
	
	// general settings
	
	static final double DIAMETER_PULLEY_INCHES = 1.19; // TODO set proper value
	public static final double PERIMETER_PULLEY_INCHES = DIAMETER_PULLEY_INCHES * Math.PI;
	
	static final int TIMEOUT_MS = 15000;	
	
	public static final double GEAR_RATIO = 1.0; // TODO change if needed
	
	public static final int LENGTH_OF_TRAVEL_INCHES = 37; // TODO set proper value
	
	static final double VIRTUAL_HOME_OFFSET_INCHES = 0.1; // position of virtual home compared to physical home
	
	static final double HOMING_PCT_OUTPUT = 0.4; // ~homing speed
	static final double MAX_PCT_OUTPUT = 1.0; // ~full speed
	
	static final int TALON_TIMEOUT_MS = 10;
	public static final int TICKS_PER_REVOLUTION = 4096;
	
	
	// move settings
	static final int PRIMARY_PID_LOOP = 0;
	
	static final int SLOT_0 = 0;
	
	static final double REDUCED_PCT_OUTPUT = 0.5;
	
	static final double MOVE_PROPORTIONAL_GAIN = 0.4;
	static final double MOVE_INTEGRAL_GAIN = 0.0;
	static final double MOVE_DERIVATIVE_GAIN = 0.0;
	
	static final int TALON_TICK_THRESH = 128;
	static final double TICK_THRESH = 512;	

	
	// variables
	boolean isHomingPart1, isHomingPart2, isMoving, isMovingUp;
	
	WPI_TalonSRX elevator;
	
	double tac;
	boolean hasBeenHomed = false;

	private int onTargetCount; // counter indicating how many times/iterations we were on target
    private final static int ON_TARGET_MINIMUM_COUNT = 25; // number of times/iterations we need to be on target to really be on target

    Robot robot; 
    
    IHinge hinge;
    
	public Elevator(WPI_TalonSRX elevator_in, Robot robot_in) {
		elevator = elevator_in;
		
		robot = robot_in;
		
		// Mode of operation during Neutral output may be set by using the setNeutralMode() function.
		// As of right now, there are two options when setting the neutral mode of a motor controller,
		// brake and coast.	
		elevator.setNeutralMode(NeutralMode.Brake);
		
		// Sensor phase is the term used to explain sensor direction.
		// In order for limit switches and closed-loop features to function properly the sensor and motor has to be in-phase.
		// This means that the sensor position must move in a positive direction as the motor controller drives positive output.
		elevator.setSensorPhase(true);

		// Enables limit switches
		elevator.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, TALON_TIMEOUT_MS);
		elevator.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector, LimitSwitchNormal.NormallyOpen, TALON_TIMEOUT_MS);
		elevator.overrideLimitSwitchesEnable(true);

		// Motor controller output direction can be set by calling the setInverted() function as seen below.
		// Note: Regardless of invert value, the LEDs will blink green when positive output is requested (by robot code or firmware closed loop).
		// Only the motor leads are inverted. This feature ensures that sensor phase and limit switches will properly match the LED pattern
		// (when LEDs are green => forward limit switch and soft limits are being checked). 	
		elevator.setInverted(false); // invert if required
		
		setPIDParameters();
		
		setNominalAndPeakOutputs(MAX_PCT_OUTPUT);

		// Sensors for motor controllers provide feedback about the position, velocity, and acceleration
		// of the system using that motor controller.
		// Note: With Phoenix framework, position units are in the natural units of the sensor.
		// This ensures the best resolution possible when performing closed-loops in firmware.
		// CTRE Magnetic Encoder (relative/quadrature) =  4096 units per rotation		
		elevator.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative,	PRIMARY_PID_LOOP, TALON_TIMEOUT_MS);
		
		isHomingPart1 = false;
		isHomingPart2 = false;
		isMoving = false;
		isMovingUp = false;
	}
	
	public Elevator(WPI_TalonSRX elevator_in, IHinge hinge_in, Robot robot_in) {
		this(elevator_in, robot_in);
		
		hinge = hinge_in;
	}

	// returns the state of the limit switch
	public boolean getLimitSwitchState() {
		return elevator.getSensorCollection().isFwdLimitSwitchClosed();
	}

	// Private. We move until we reach the limit switch (in open loop). This gives us the physical zero
	private void homePart1() {
		// we assume that the forward limit switch is enabled
		//elevator.configSetParameter(ParamEnum.eClearPositionOnLimitF, 1, 0, 0, TALON_TIMEOUT_MS);
		elevator.set(ControlMode.PercentOutput,+HOMING_PCT_OUTPUT); // we start moving down
		
		isHomingPart1 = true;
	}
	
	// Private. We move back up a little (in closed loop). This gives us the virtual/logical zero.
	// The purpose of this is to avoid hitting the limit switch too hard when we go down at full speed.
	private void homePart2() {
		elevator.set(ControlMode.PercentOutput,0); // we stop AND MAKE SURE WE DO NOT MOVE WHEN SETTING POSITION
		elevator.setSelectedSensorPosition(0, PRIMARY_PID_LOOP, TALON_TIMEOUT_MS); // we set the current position to zero
		
		/*try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
		setNominalAndPeakOutputs(MAX_PCT_OUTPUT);
		setPIDParameters(); // we switch to position mode
		tac = -convertInchesToRev(VIRTUAL_HOME_OFFSET_INCHES) * TICKS_PER_REVOLUTION;
		elevator.set(ControlMode.Position,tac); // we move to virtual zero
		
		isHomingPart2 = true;
		onTargetCount = 0;
	}
	
	// homes the elevator
	// This is done in two steps:
	// step 1: if not already at the switch, we go down slowly until we hit the limit switch.
	// step 2: we go back up a little and mark the position as the virtual/logical zero.
	public void home() {
		hasBeenHomed = false; // flags that it has not been homed
		
		if (!getLimitSwitchState()) { 	// if we are not already at the switch
										// we need to go down to find limit switch					
			homePart1();
			isHomingPart2 = true; // then we need to go to virtual zero later
		} else {
			isHomingPart1 = false; 	// we don't need to go down
									// but we still need to go to virtual zero		
			homePart2(); // we start part 2 directly
		}
	}

	// this method need to be called to assess the homing progress
	// (and it takes care of going to step 2 if needed)
	public boolean checkHome() {
		if (isHomingPart1) {
			isHomingPart1 = !getLimitSwitchState(); // we are not done until we reach the switch

			if (!isHomingPart1) {
				System.out.println("You have reached the home.");
				elevator.set(ControlMode.PercentOutput,0); // turn power off
				
				homePart2(); // we move on to part 2
			}
		} else if (isHomingPart2) {
			isHomingPart2 = isReallyHomingPart2();

			if (!isHomingPart2) {
				System.out.println("You have reached the virtual zero.");

				elevator.set(ControlMode.PercentOutput,0); // we stop AND MAKE SURE WE DO NOT MOVE WHEN SETTING POSITION
				elevator.setSelectedSensorPosition(0, PRIMARY_PID_LOOP, TALON_TIMEOUT_MS); // we mark the virtual zero

				hasBeenHomed = true;
			}
		}

		return isHoming();
	}

	// Private. Checks if homing step 2 is done.
	private boolean isReallyHomingPart2() {
		double error = elevator.getClosedLoopError(PRIMARY_PID_LOOP);
		
		boolean isOnTarget = (Math.abs(error) < TICK_THRESH);
		
		if (isOnTarget) { // if we are on target in this iteration 
			onTargetCount++; // we increase the counter
		} else { // if we are not on target in this iteration
			if (onTargetCount > 0) { // even though we were on target at least once during a previous iteration
				onTargetCount = 0; // we reset the counter as we are not on target anymore
				System.out.println("Triple-check failed (elevator homing part 2).");
			} else {
				// we are definitely homing
				//System.out.println("Elevator homing part 2 error: " + Math.abs(error));
			}
		}
		
        if (onTargetCount > ON_TARGET_MINIMUM_COUNT) { // if we have met the minimum
        	return false;
        }
	        
		return true;
	}
	
	// do not use in teleop - for auton only
	public void waitHome() {
		long start = Calendar.getInstance().getTimeInMillis();
		
		while (checkHome()) {
			if (!DriverStation.getInstance().isAutonomous()
					|| Calendar.getInstance().getTimeInMillis() - start >= TIMEOUT_MS) {
				System.out.println("You went over the time limit (elevator homing)");
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
	
	// This method should be called to assess the progress of a move
	public boolean tripleCheckMove() {
		if (isMoving) {
			
			double error = elevator.getClosedLoopError(PRIMARY_PID_LOOP);
			
			boolean isOnTarget = (Math.abs(error) < TICK_THRESH);
			
			if (isOnTarget) { // if we are on target in this iteration 
				onTargetCount++; // we increase the counter
			} else { // if we are not on target in this iteration
				if (onTargetCount > 0) { // even though we were on target at least once during a previous iteration
					onTargetCount = 0; // we reset the counter as we are not on target anymore
					System.out.println("Triple-check failed (elevator moving).");
				} else {
					// we are definitely moving
				}
			}
			
	        if (onTargetCount > ON_TARGET_MINIMUM_COUNT) { // if we have met the minimum
	        	isMoving = false;
	        }
			
			if (!isMoving) {
				System.out.println("You have reached the target (elevator moving).");
				//elevator.set(ControlMode.PercentOutput,0);
				if (isMovingUp)	{
					stay();
				} else {
					stop();
				}
			}
		}
		return isMoving; 
	}
	
	// do not use in teleop - for auton only
	public void waitMove() {
		long start = Calendar.getInstance().getTimeInMillis();
		
		while (tripleCheckMove()) {
			if (!DriverStation.getInstance().isAutonomous()
					|| Calendar.getInstance().getTimeInMillis() - start >= TIMEOUT_MS) {
				System.out.println("You went over the time limit (elevator moving)");
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

	public void moveUp() {
		
		if (hinge != null && hinge.isUp()) {
			System.out.println("Cannot move when hinge is up");
			return;
		}
		
		if (hasBeenHomed) {
			//setPIDParameters();
			System.out.println("Moving Up");

			tac = -convertInchesToRev(LENGTH_OF_TRAVEL_INCHES) * TICKS_PER_REVOLUTION;
			elevator.set(ControlMode.Position,tac);
			
			isMoving = true;
			isMovingUp = true;
			onTargetCount = 0;
		} else {
			System.out.println("You have not been home, your mother must be worried sick");
		}
	}

	public void moveMidway() {
		
		if (hinge != null && hinge.isUp()) {
			System.out.println("Cannot move when hinge is up");
			return;
		}
		
		if (hasBeenHomed) {
			//setPIDParameters();
			System.out.println("Moving Midway");

			tac = -convertInchesToRev(LENGTH_OF_TRAVEL_INCHES / 2) * TICKS_PER_REVOLUTION;
			elevator.set(ControlMode.Position,tac);
			
			isMoving = true;
			isMovingUp = true;
			onTargetCount = 0;
		} else {
			System.out.println("You have not been home, your mother must be worried sick");
		}
	}	
	
	public void moveDown() {
		
		if (hinge != null && hinge.isUp()) {
			System.out.println("Cannot move when hinge is up");
			return;
		}
		
		if (hasBeenHomed) {
			//setPIDParameters();
			System.out.println("Moving Down");

			tac = +convertInchesToRev(0)* TICKS_PER_REVOLUTION;
			elevator.set(ControlMode.Position,tac);
			
			isMoving = true;
			isMovingUp = false;
			onTargetCount = 0;
		} else {
			System.out.println("You have not been home, your mother must be worried sick");
		}
	}

	public double getPosition() {
		return convertRevtoInches(elevator.getSelectedSensorPosition(PRIMARY_PID_LOOP) / TICKS_PER_REVOLUTION);
	}

	public double getEncPosition() {
		return elevator.getSelectedSensorPosition(PRIMARY_PID_LOOP);
	}

	public boolean isHoming() {
		return isHomingPart1 || isHomingPart2;
	}
	
	public boolean isHomingPart1() {
		return isHomingPart1;
	}
	
	public boolean isHomingPart2() {
		return isHomingPart2;
	}

	public boolean isMoving() {
		return isMoving;
	}
	
	public boolean isUp() {
		return getPosition() > LENGTH_OF_TRAVEL_INCHES * 2/3;
	}
	
	public boolean isDown() {
		return getPosition() < LENGTH_OF_TRAVEL_INCHES * 1/3;
	}
	
	public boolean isMidway() {
		return !isUp() && !isDown();
	}

	private double convertInchesToRev(double inches) {
		return inches / PERIMETER_PULLEY_INCHES * GEAR_RATIO;
	}

	private double convertRevtoInches(double rev) {
		return rev * PERIMETER_PULLEY_INCHES / GEAR_RATIO;
	}

	public void stay() {	 		
		isMoving = false;		
		isHomingPart1 = false;
		isHomingPart2 = false;
	}
	
	public void stop() {	 
		elevator.set(ControlMode.PercentOutput, 0);
		
		isMoving = false;		
		isHomingPart1 = false;
		isHomingPart2 = false;
		
		setNominalAndPeakOutputs(MAX_PCT_OUTPUT); // we undo what me might have changed
	}
	
	private void setPIDParameters() {		
		elevator.configAllowableClosedloopError(SLOT_0, TALON_TICK_THRESH, TALON_TIMEOUT_MS);
		
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
		
		elevator.config_kP(SLOT_0, MOVE_PROPORTIONAL_GAIN, TALON_TIMEOUT_MS);
		elevator.config_kI(SLOT_0, MOVE_INTEGRAL_GAIN, TALON_TIMEOUT_MS);
		elevator.config_kD(SLOT_0, MOVE_DERIVATIVE_GAIN, TALON_TIMEOUT_MS);		
		elevator.config_kF(SLOT_0, 0, TALON_TIMEOUT_MS);
	}

	public void setNominalAndPeakOutputs(double peakOutput)
	{
		elevator.configPeakOutputForward(peakOutput, TALON_TIMEOUT_MS);
		elevator.configPeakOutputReverse(-peakOutput, TALON_TIMEOUT_MS);
		
		elevator.configNominalOutputForward(0, TALON_TIMEOUT_MS);
		elevator.configNominalOutputForward(0, TALON_TIMEOUT_MS);
	}
	
	// for debug purpose only
	public void joystickControl(Joystick joystick)
	{
		if (!isMoving && !isHoming()) // if we are already doing a move we don't take over
		{
			elevator.set(ControlMode.PercentOutput, -joystick.getY());
		}
	}
	
	public double getTarget() {
		return tac;
	}
	
	public boolean hasBeenHomed()
	{
		return hasBeenHomed;
	}
}
