/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team2495.robot;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;

import org.usfirst.frc.team2495.robot.Jack.Position;

import com.ctre.phoenix.motorcontrol.can.BaseMotorController;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */


public class Robot extends IterativeRobot {
	
	// set the following two constants to true if using a proto/second bot with no hinge and/or no elevator
	public static final boolean HINGE_DISABLED = false;
	public static final boolean ELEVATOR_DISABLED = false;	
	
	// choosers (for auton)
	
	public static final String AUTON_DO_NOTHING = "Do Nothing";
	public static final String AUTON_CUSTOM = "My Auto";
	private String autonSelected;
	private SendableChooser<String> autonChooser = new SendableChooser<>();
	
	public static final String START_POSITION_LEFT = "Left";
	public static final String START_POSITION_CENTER = "Center";
	public static final String START_POSITION_RIGHT = "Right";
	private String startPosition;
	private SendableChooser<String> startPositionChooser = new SendableChooser<>();
	
	public static final String CAMERA_OPTION_USE_ALWAYS = "Always";
	public static final String CAMERA_OPTION_USE_NEVER = "Never";
	private String cameraOption;
	private SendableChooser<String> cameraOptionChooser = new SendableChooser<>();
	
	public static final String SONAR_OPTION_USE_ALWAYS = "Always";
	public static final String SONAR_OPTION_USER_NEVER = "Never";
	private String sonarOption;
	private SendableChooser<String> sonarOptionChooser = new SendableChooser<>();
	
	// sensors
	
	HMCamera camera;
	
	ADXRS450_Gyro gyro; // gyro
	boolean hasGyroBeenManuallyCalibratedAtLeastOnce = false;
	
	Sonar sonar;
	
	HMAccelerometer accelerometer;
	
	// motorized devices
	
	Drivetrain drivetrain;

	WPI_TalonSRX frontLeft;
	WPI_TalonSRX frontRight;
	BaseMotorController rearLeft; 
	BaseMotorController rearRight;
	
	MiniDrivetrain miniDrivetrain;
	
	WPI_TalonSRX frontCenter;
	WPI_TalonSRX rearCenter;

	boolean elevatorFlagUp = true;
	Elevator elevatorControl;
	
	WPI_TalonSRX elevator;
	
	Grasper grasper;
	
	BaseMotorController grasperLeft;
	BaseMotorController grasperRight;
	
	boolean hingeFlagUp = false;
	Hinge hingeControl;
	
	WPI_TalonSRX hinge; 
	
	Winch winchControl;
	
	BaseMotorController winch;
	
	// pneumatic devices
	
	Compressor compressor; // the compressor's lifecycle needs to be the same as the robot
	
	Jack jack;
	boolean largeDriveTrainSelected = false; // by default we assume small drivetrain is down
	
	// joysticks and gamepad
	
	ControllerBase control;
	
	Joystick joyLeft, joyRight;
	Joystick gamepad;
	
	//misc. 
	
	GameData gameData;
	
	Auton auton = null; // autonomous stuff
	
	PositionTracker tracker;

	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	
	@Override
	public void robotInit() {
		// choosers (for auton)
		
		autonChooser.addDefault("Do Nothing", AUTON_DO_NOTHING);
		autonChooser.addObject("My Auto", AUTON_CUSTOM);
		SmartDashboard.putData("Auto choices", autonChooser);
		
		startPositionChooser.addDefault("Left", START_POSITION_LEFT);
		startPositionChooser.addObject("Center", START_POSITION_CENTER);
		startPositionChooser.addObject("Right", START_POSITION_RIGHT);
		SmartDashboard.putData("Start positions", startPositionChooser);
		
		cameraOptionChooser.addObject("Always", CAMERA_OPTION_USE_ALWAYS);
		cameraOptionChooser.addDefault("Never", CAMERA_OPTION_USE_NEVER);
		SmartDashboard.putData("Camera options", cameraOptionChooser);
		
		sonarOptionChooser.addDefault("Always", CAMERA_OPTION_USE_ALWAYS);
		sonarOptionChooser.addObject("Never", CAMERA_OPTION_USE_NEVER);
		SmartDashboard.putData("Sonar options", sonarOptionChooser);
		
		// sensors
		
		sonar = new Sonar(Ports.Analog.SONAR); 
		
		gyro = new ADXRS450_Gyro(SPI.Port.kOnboardCS0); // we want to instantiate before we pass to drivetrain	
		
		gyro.calibrate(); 
		gyro.reset();
		
		camera = new HMCamera("GRIP/myContoursReport",false);
		
		accelerometer = new HMAccelerometer();
		
		// motorized devices
		
		frontLeft = new WPI_TalonSRX(Ports.CAN.LEFT_FRONT);
		frontRight = new WPI_TalonSRX(Ports.CAN.RIGHT_FRONT);
		rearLeft = new WPI_TalonSRX(Ports.CAN.LEFT_REAR);
		rearRight= new WPI_TalonSRX(Ports.CAN.RIGHT_REAR);
		
		frontCenter= new WPI_TalonSRX(Ports.CAN.FRONT_CENTER);
		rearCenter= new WPI_TalonSRX(Ports.CAN.REAR_CENTER);
		
		elevator = new WPI_TalonSRX(Ports.CAN.ELEVATOR);
			
		grasperLeft = new WPI_TalonSRX(Ports.CAN.GRASPER_LEFT);
		grasperRight = new WPI_TalonSRX(Ports.CAN.GRASPER_RIGHT);
		
		hinge = new WPI_TalonSRX(Ports.CAN.HINGE);
		
		winch = new WPI_TalonSRX(Ports.CAN.WINCH);
		
		drivetrain = new Drivetrain( frontLeft, frontRight, rearLeft, rearRight, gyro, this);		
		miniDrivetrain = new MiniDrivetrain(frontCenter, rearCenter, gyro, this, camera);
		
		grasper = new Grasper(grasperLeft, grasperRight, sonar, this);
								
		elevatorControl = new Elevator(elevator, hingeControl, this);
		
		hingeControl = new Hinge(hinge, this);
		
		winchControl = new Winch(winch, this);
		
		// pneumatic devices
		
		compressor = new Compressor();
		compressor.checkCompressor();
				
		jack = new Jack();
		
		// joysticks and gamepad
		
		joyLeft = new Joystick(Ports.USB.LEFT); 
		joyRight = new Joystick(Ports.USB.RIGHT);
		
		gamepad = new Joystick(Ports.USB.GAMEPAD);

		control = new ControllerBase(gamepad, joyLeft, joyRight);	

		// misc.
		
		gameData = new GameData();
		
		tracker = new PositionTracker();
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable
	 * chooser code works with the Java SmartDashboard. If you prefer the
	 * LabVIEW Dashboard, remove all of the chooser code and uncomment the
	 * getString line to get the auto name from the text box below the Gyro
	 *
	 * <p>You can add additional auto modes by adding additional comparisons to
	 * the switch structure below with additional strings. If using the
	 * SendableChooser make sure to add them to the chooser code above as well.
	 */
	@Override
	public void autonomousInit() {
		autonSelected = autonChooser.getSelected();
		System.out.println("Auton selected: " + autonSelected);
		
		startPosition = startPositionChooser.getSelected();
		System.out.println("Start position: " + startPosition);
		
		cameraOption = cameraOptionChooser.getSelected();
		System.out.println("Camera option: " + cameraOption);
		
		sonarOption = sonarOptionChooser.getSelected();
		System.out.println("Sonar option: " + sonarOption);
		
		gameData.update();
		
		//At this point we should know what auto run, where we started, and where our plates are located.
		//So we are ready for autonomousPeriodic to be called.
		updateToSmartDash();
		
		auton = new Auton(autonSelected, startPosition, cameraOption, sonarOption, gameData,
				drivetrain, jack, miniDrivetrain,
				hingeControl, grasper, elevatorControl,
				camera, this, tracker);
		
		auton.initialize();
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		auton.execute();
	}

	@Override
	public void teleopInit() {
		drivetrain.stop(); // very important!
		miniDrivetrain.stop();
		elevatorControl.stop();
		hingeControl.stop();
		grasper.stop();
		
		gameData.update();
	}
	
	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
		control.update();
		camera.acquireTargets(false);
		
	
		drivetrain.tripleCheckMoveDistance(); // checks if we are done moving if we were moving
		drivetrain.tripleCheckTurnAngleUsingPidController(); // checks if we are done turning if we were turning
		
		miniDrivetrain.tripleCheckMoveDistance(); // checks if we are done moving if we were moving
		miniDrivetrain.tripleCheckMoveUsingCameraPidController(); // checks if we are done moving using camera if we were moving
		
		elevatorControl.checkHome();
		elevatorControl.tripleCheckMove();
		
		hingeControl.checkHome();
		hingeControl.tripleCheckMove();
		
		grasper.tripleCheckGraspUsingSonar(); // - only enable if we want to stop automatically
		grasper.tripleCheckReleaseUsingSonar();
		
		
		//RIGHT JOYSTICK //RIGHT JOYSTICK //RIGHT JOYSTICK //RIGHT JOYSTICK //RIGHT JOYSTICK
		
		//Joystick drive only using right joystick
		if (largeDriveTrainSelected) {
			drivetrain.joystickControl(joyRight, joyRight, true);
		}
		else
		{
			miniDrivetrain.joystickControl(joyRight, joyRight, true);
		}			
		
		// Jack up or down the robot to switch between main or mini drivetrain			
		if (control.getPressedDown(ControllerBase.Joysticks.RIGHT_STICK,ControllerBase.JoystickButtons.BTN2))
		{
			System.out.println("Button RIGHT.BTN2 Pushed");
			
			//if(largeDriveTrainSelected){
				largeDriveTrainSelected = false; 
				System.out.println("jack down");
				jack.setPosition(Jack.Position.DOWN);
			//}
			//else{
			//	largeDriveTrainSelected = true; 
			//	System.out.println("jack up");
			//	jack.setPosition(Jack.Position.UP);
			//}			
		}
		
		if (control.getPressedDown(ControllerBase.Joysticks.RIGHT_STICK,ControllerBase.JoystickButtons.BTN3))
		{
			System.out.println("Button RIGHT.BTN3 Pushed");
			
			//if(largeDriveTrainSelected){
			//	largeDriveTrainSelected = false; 
			//	System.out.println("jack down");
			//	jack.setPosition(Jack.Position.DOWN);
			//}
			//else{
				largeDriveTrainSelected = true; 
				System.out.println("jack up");
				jack.setPosition(Jack.Position.UP);
			//}			
		}
			
		//Turn the Robot towards the cube
		if (control.getPressedDown(ControllerBase.Joysticks.RIGHT_STICK, ControllerBase.JoystickButtons.BTN4))
		{
			System.out.println("Button RIGHT.BTN4 Pushed");
			
			turnAngleUsingPidControllerTowardCube();
		}
		
		//Move straight towards the cube 
		if (control.getPressedDown(ControllerBase.Joysticks.RIGHT_STICK, ControllerBase.JoystickButtons.BTN5))
		{
			System.out.println("Button RIGHT.BTN5 Pushed");
			
			moveDistanceTowardCube();
		}

		//Resets encoders (and gyro) "Reset"
		if (control.getPressedDown(ControllerBase.Joysticks.RIGHT_STICK, ControllerBase.JoystickButtons.BTN6))
		{
			System.out.println("Button RIGHT.BTN6 Pushed");
			
			drivetrain.resetEncoders();
			miniDrivetrain.resetEncoders();
			gyro.reset(); // resets to zero
		}		
		
		//Stops the robot moving if pressed (or any closed loop operation) "Abort"
		if (control.getPressedDown(ControllerBase.Joysticks.RIGHT_STICK, ControllerBase.JoystickButtons.BTN7))
		{
			System.out.println("Button RIGHT.BTN7 Pushed");
			
			drivetrain.stop();
			miniDrivetrain.stop();
			elevatorControl.stop();
			hingeControl.stop();
			grasper.stop();
		}
		
		
		// LEFT JOYSTICK // LEFT JOYSTICK // LEFT JOYSTICK // LEFT JOYSTICK // LEFT JOYSTICK
						
		// todo document new option
		if (control.getPressedDown(ControllerBase.Joysticks.LEFT_STICK, ControllerBase.JoystickButtons.BTN2))
		{
			System.out.println("Button LEFT.BTN2 Pushed");
			
			if (jack != null && (jack.getPosition() != Position.DOWN)) {
				System.out.println("WARNING: moving mini drivetrain when jack is not down!");
			}
			
			miniDrivetrain.moveUsingCameraPidController();
		}
		else if (control.getPressedDown(ControllerBase.Joysticks.LEFT_STICK, ControllerBase.JoystickButtons.BTN3))
		{
			System.out.println("Button LEFT.BTN3 Pushed");
			
			//if (jack != null && (jack.getPosition() != Position.UP)) {
			//	System.out.println("WARNING: moving drivetrain when jack is not up!");
			//}
			
			if (largeDriveTrainSelected) {
				drivetrain.moveDistance(50);
			} else {
				miniDrivetrain.moveDistance(5);
			}
		}
		else if (control.getPressedDown(ControllerBase.Joysticks.LEFT_STICK, ControllerBase.JoystickButtons.BTN4))
		{
			System.out.println("Button LEFT.BTN4 Pushed");
			
			if (jack != null && (jack.getPosition() != Position.UP)) {
				System.out.println("WARNING: turning drivetrain when jack is not up!");
			}
			
			//drivetrain.moveDistanceAlongArc(-90);
			drivetrain.turnAngleUsingPidController(-90);
		}
		else if (control.getPressedDown(ControllerBase.Joysticks.LEFT_STICK, ControllerBase.JoystickButtons.BTN5))
		{
			System.out.println("Button LEFT.BTN5 Pushed");
			
			if (jack != null && (jack.getPosition() != Position.UP)) {
				System.out.println("WARNING: turning drivetrain when jack is not up!");
			}
			
			//drivetrain.moveDistanceAlongArc(+90);
			drivetrain.turnAngleUsingPidController(+90);
		}
		
		if (control.getHeld(ControllerBase.Joysticks.LEFT_STICK,ControllerBase.JoystickButtons.BTN6))
		{
			System.out.println("Button LEFT.BTN6 Pushed");
			
			grasper.joystickControl(joyLeft);
		}
				
		if (control.getHeld(ControllerBase.Joysticks.LEFT_STICK,ControllerBase.JoystickButtons.BTN8))
		{
			System.out.println("Button LEFT.BTN8 Pushed");
			
			elevatorControl.joystickControl(joyLeft);
		}
		
		if (control.getHeld(ControllerBase.Joysticks.LEFT_STICK,ControllerBase.JoystickButtons.BTN9))
		{
			System.out.println("Button LEFT.BTN9 Pushed");
			
			hingeControl.joystickControl(joyLeft);
		}
		
		if (control.getHeld(ControllerBase.Joysticks.LEFT_STICK,ControllerBase.JoystickButtons.BTN10))
		{
			System.out.println("Button LEFT.BTN10 Pushed");
			
			winchControl.joystickControl(joyLeft);
		}
	
		
		// GAMEPAD // GAMEPAD // GAMEPAD // GAMEPAD // GAMEPAD

		//Home Elevator
		if (control.getPressedDown(ControllerBase.Joysticks.GAMEPAD, ControllerBase.GamepadButtons.BACK)) {
			System.out.println("Button BACK Pushed");
			
			if (hingeControl != null && (!hingeControl.hasBeenHomed() || !hingeControl.isDown())) {
				System.out.println("ORDER DECLINED: cannot home elevator up when hinge has not been homed or is not down!");
			} else {
				elevatorControl.home();
			}
		}
		
		//Home Hinge
		if (control.getPressedDown(ControllerBase.Joysticks.GAMEPAD, ControllerBase.GamepadButtons.START)) { 
			System.out.println("Button START Pushed");
			
			hingeControl.home();
		}
		
		//elevator up and down using Left L3 button
		if (control.getPressedDown(ControllerBase.Joysticks.GAMEPAD, ControllerBase.GamepadButtons.L3)) {
			System.out.println("Button L3 Pushed");
			
			if (hingeControl != null && (!hingeControl.hasBeenHomed() || !hingeControl.isDown())) {
				System.out.println("ORDER DECLINED: cannot move elevator up or down when hinge has not been homed or is not down!");
			} else {
				if (elevatorFlagUp) {
					elevatorControl.moveUp();
					System.out.println("Elevator should be moving up");
					elevatorFlagUp = false;
				} else {
					elevatorControl.moveDown();
					System.out.println("Elevator should be moving down");
					elevatorFlagUp = true;
				}
			}
		}
		
		//Use Left bumper to move elevator midway (switch)
		if (control.getPressedDown(ControllerBase.Joysticks.GAMEPAD, ControllerBase.GamepadButtons.LB)) { 
			System.out.println("Button LB Pushed");
			
			if (hingeControl != null && (!hingeControl.hasBeenHomed() || !hingeControl.isDown())) {
				System.out.println("ORDER DECLINED: cannot move elevator midway when hinge has not been homed or is not down!");
			} else {
				elevatorControl.moveMidway();
				System.out.println("Elevator should be moving midway");
				elevatorFlagUp = false;
			}
		}
		
		//Use Right bumper to move hinge midway (throw)
		if (control.getPressedDown(ControllerBase.Joysticks.GAMEPAD, ControllerBase.GamepadButtons.RB)) { 
			System.out.println("Button RB Pushed");
			
			hingeControl.moveMidway();
			System.out.println("Hinge should be moving midway");
			hingeFlagUp = false;
		}

		//hinge bound to X
		if (control.getPressedDown(ControllerBase.Joysticks.GAMEPAD, ControllerBase.GamepadButtons.X)) {
			System.out.println("Button X Pushed");
			
			if (hingeFlagUp) {
				hingeControl.moveUp();
				System.out.println("Hinge should be moving up");
				hingeFlagUp = false;
			} else {
				hingeControl.moveDown();
				System.out.println("Hinge should be moving down");
				hingeFlagUp = true;
			}
		}

		// THIS REQUIRES 2 KEYS (nuclear option)			
		if (control.getHeld(ControllerBase.Joysticks.RIGHT_STICK, ControllerBase.JoystickButtons.BTN10) &&
				control.getHeld(ControllerBase.Joysticks.GAMEPAD, ControllerBase.GamepadButtons.Y)) {
			winchControl.winchUp();
			control.rumble(true); // provides indicator that we are winching up (both keys are held)
		}
		else 
		{
			if (!control.getHeld(ControllerBase.Joysticks.LEFT_STICK,ControllerBase.JoystickButtons.BTN10))
			{
				winchControl.stop();	// for manual mode, remove if auto stop is desired
				control.rumble(false); // provides indicator that we stopped winching (zero or only one key held)
			}
		}

		if (control.getPressedDown(ControllerBase.Joysticks.GAMEPAD, ControllerBase.GamepadButtons.A)) { 
			System.out.println("Button A Pushed");
			
			if (hingeControl != null && (!hingeControl.hasBeenHomed() || !hingeControl.isDown())) {
				System.out.println("WARNING: grasping when hinge has not been homed or is not down!");
			}
			
			if (elevatorControl != null && (!elevatorControl.hasBeenHomed() || !elevatorControl.isDown())) {
				System.out.println("WARNING: grasping when elevator has not been homed or is not down!");
			}
			
			grasper.grasp();
		}
		else if (control.getPressedDown(ControllerBase.Joysticks.GAMEPAD, ControllerBase.GamepadButtons.B)) {
			System.out.println("Button B Pushed");
			
			if (hingeControl != null && (!hingeControl.hasBeenHomed() || hingeControl.isUp())) {
				System.out.println("WARNING: releasing when hinge has not been homed or is up!");
			}
			
			if (elevatorControl != null && (!elevatorControl.hasBeenHomed() || elevatorControl.isDown())) {
				System.out.println("WARNING: releasing when elevator has not been homed or is down!");
			}
			
			grasper.release();
		}
		else 
		{
			//grasper.stop();	// for manual mode, remove if auto stop is desired	
		}
		
		
		camera.acquireTargets(false);
		updateToSmartDash(); 	
	}
	
	

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}
	
	@Override
	public void disabledInit() {
		gameData.update();
	}

	@Override
	public void disabledPeriodic() {	
		control.update();
		camera.acquireTargets(false);
		
		if (control.getPressedDown(ControllerBase.Joysticks.GAMEPAD, ControllerBase.GamepadButtons.A)) {
			gyro.calibrate();
			gyro.reset();
			hasGyroBeenManuallyCalibratedAtLeastOnce = true; // we flag that this was done
		}
		
		camera.acquireTargets(false);
		updateToSmartDash();
	}
	
	public void turnAngleUsingPidControllerTowardCube() {
		drivetrain.turnAngleUsingPidController(camera.getAngleToTurnToTarget());
		/*drivetrain.turnAngleUsingPidController(calculateProperTurnAngle(
				camera.getAngleToTurnToTarget(),camera.getDistanceToTargetUsingHorizontalFov()));*/
	}
	
	public void moveDistanceTowardCube() {
		final int OFFSET_CAMERA_CUBE_INCHES = 10; // we need to leave some space between the camera and the target
		final int MAX_DISTANCE_TO_CUBE_INCHES = 120; // arbitrary very large distance
		
		double distanceToTargetReportedByCamera = camera.getDistanceToTargetUsingHorizontalFov();
		
		if (distanceToTargetReportedByCamera <= MAX_DISTANCE_TO_CUBE_INCHES) {
			if (distanceToTargetReportedByCamera >= OFFSET_CAMERA_CUBE_INCHES) {
				drivetrain.moveDistance((distanceToTargetReportedByCamera - OFFSET_CAMERA_CUBE_INCHES)); // todo: check sign
			} else {
				System.out.println("Already at the cube!");
			}
		} else {
			System.out.println("Cannot move to infinity and beyond!");
		}		
	}
	
	public void updateToSmartDash()
	{
		// Send Gyro val to Dashboard
        SmartDashboard.putNumber("Gyro Value", gyro.getAngle());
        
        SmartDashboard.putNumber("Right Value", drivetrain.getRightValue());
        SmartDashboard.putNumber("Left Value", drivetrain.getLeftValue());
        SmartDashboard.putNumber("Right Enc Value", drivetrain.getRightEncoderValue());
        SmartDashboard.putNumber("Left Enc Value", drivetrain.getLeftEncoderValue());
        SmartDashboard.putBoolean("isMoving?", drivetrain.isMoving());
        SmartDashboard.putBoolean("isTurning?", drivetrain.isTurning());
        
        SmartDashboard.putNumber("Mini Right Value", miniDrivetrain.getRightValue());
        SmartDashboard.putNumber("Mini Left Value", miniDrivetrain.getLeftValue());
        SmartDashboard.putNumber("Mini Right Enc Value", miniDrivetrain.getRightEncoderValue());
        SmartDashboard.putNumber("Mini Left Enc Value", miniDrivetrain.getLeftEncoderValue());
        SmartDashboard.putBoolean("Mini isMoving?", miniDrivetrain.isMoving());
        
        SmartDashboard.putBoolean("isCompromised?", DriverStation.getInstance().isDisabled());
        
        SmartDashboard.putNumber("Distance to Target", camera.getDistanceToTargetUsingVerticalFov());
        SmartDashboard.putNumber("Angle to Target", camera.getAngleToTurnToTarget());
        SmartDashboard.putNumber("Distance to Target Using Horizontal FOV", camera.getDistanceToTargetUsingHorizontalFov());
        
        SmartDashboard.putBoolean("Elevator Limit Switch", elevatorControl.getLimitSwitchState());
        SmartDashboard.putNumber("Elevator Position", elevatorControl.getPosition());
        SmartDashboard.putNumber("Elevator Enc Position", elevatorControl.getEncPosition());
        SmartDashboard.putBoolean("Elevator IsHoming?", elevatorControl.isHoming());
        SmartDashboard.putBoolean("Elevator IsMoving?", elevatorControl.isMoving());
        SmartDashboard.putNumber("Elevator Target", elevatorControl.getTarget());
        SmartDashboard.putBoolean("Elevator Has Been Homed?", elevatorControl.hasBeenHomed());
        SmartDashboard.putBoolean("Elevator isDown", elevatorControl.isDown());
        SmartDashboard.putBoolean("Elevator isMidway", elevatorControl.isMidway());
        SmartDashboard.putBoolean("Elevator isUp", elevatorControl.isUp());
        
        SmartDashboard.putBoolean("Hinge Limit Switch", hingeControl.getLimitSwitchState());
        SmartDashboard.putNumber("Hinge Position", hingeControl.getPosition());
        SmartDashboard.putNumber("Hinge Enc Position", hingeControl.getEncPosition());
        SmartDashboard.putBoolean("Hinge IsHoming?", hingeControl.isHoming());
        SmartDashboard.putBoolean("Hinge IsMoving?", hingeControl.isMoving());
        SmartDashboard.putNumber("Hinge Target", hingeControl.getTarget());
        SmartDashboard.putBoolean("Hinge Has Been Homed?", hingeControl.hasBeenHomed());
        SmartDashboard.putBoolean("Hinge isDown", hingeControl.isDown());
        SmartDashboard.putBoolean("Hinge isMidway", hingeControl.isMidway());
        SmartDashboard.putBoolean("Hinge isUp", hingeControl.isUp());
        
        SmartDashboard.putBoolean("Gyro Manually Calibrated?",hasGyroBeenManuallyCalibratedAtLeastOnce);
        SmartDashboard.putNumber("PID Error", drivetrain.turnPidController.getError());
        SmartDashboard.putNumber("PID Motor Value", drivetrain.turnPidController.get());
        SmartDashboard.putBoolean("PID On Target", drivetrain.turnPidController.onTarget());
        
        SmartDashboard.putNumber("Tilt", accelerometer.getTilt());
        
        SmartDashboard.putString("First Switch", gameData.getAssignedPlateAtFirstSwitch().toString());
        SmartDashboard.putString("Scale", gameData.getAssignedPlateAtScale().toString());
        SmartDashboard.putString("Second Switch", gameData.getAssignedPlateAtSecondSwitch().toString());
        
        SmartDashboard.putNumber("Range to target", sonar.getRangeInInches());
        SmartDashboard.putNumber("Sonar Voltage", sonar.getVoltage()); 
        
        SmartDashboard.putBoolean("Grasper IsGrasping?", grasper.isGrasping());
        SmartDashboard.putBoolean("Grasper IsReleasing?", grasper.isReleasing());
        
        SmartDashboard.putBoolean("Winch IsWinchingUp?", winchControl.isWinchingUp());
	}
	
	public double calculateProperTurnAngle(double cameraTurnAngle, double cameraHorizontalDist) {
		try {
			final double OFFSET_BETWEEN_CAMERA_AND_ROTATION_CENTER_INCHES = 6; // inches - might need adjustment
			double dist = cameraHorizontalDist * Math.cos(Math.toRadians(cameraTurnAngle));
			return Math.toDegrees(Math.atan(Math.tan(Math.toRadians(cameraTurnAngle)) * dist
					/ (dist + OFFSET_BETWEEN_CAMERA_AND_ROTATION_CENTER_INCHES)));
		} catch (Exception e) {
			System.out.println("Exception in proper turn angle calculation" + e.toString());
			return 0;
		}
	}
}
