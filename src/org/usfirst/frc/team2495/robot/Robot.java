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
	public static final String kDefaultAuto = "Default";
	public static final String kCustomAuto = "My Auto";
	private String m_autoSelected;
	private SendableChooser<String> m_chooser = new SendableChooser<>();
	
	public static final String START_POSITION_LEFT = "Left";
	public static final String START_POSITION_CENTER = "Center";
	public static final String START_POSITION_RIGHT = "Right";
	private String startPosition;
	private SendableChooser<String> startPositionChooser = new SendableChooser<>();
	
	Drivetrain drivetrain;
	
	MiniDrivetrain miniDrivetrain;
	
	Grasper grasper;
	
	HMCamera camera;
	
	WPI_TalonSRX frontLeft;
	WPI_TalonSRX frontRight;
	BaseMotorController rearLeft; 
	BaseMotorController rearRight;
	
	WPI_TalonSRX frontCenter;
	WPI_TalonSRX rearCenter;

	WPI_TalonSRX elevator;
	
	BaseMotorController grasperLeft;
	BaseMotorController grasperRight;
	
	WPI_TalonSRX hinge; 
	
	Joystick joyLeft, joyRight;
	Joystick gamepad;
	
	ADXRS450_Gyro gyro; // gyro
	
	Sonar sonar;
	
	Compressor compressor; // the compressor's lifecycle needs to be the same as the robot
	
	ControllerBase control;

	boolean hasGyroBeenManuallyCalibratedAtLeastOnce = false;
	
	boolean elevatorFlagUp = true;
	Elevator elevatorControl;
	
	Jack jack;
	boolean largeDriveTrainSelected = false; // by default we assume small drivetrain is down
	
	boolean hingeFlagUp = false;
	Hinge hingeControl;
	
	GameData gameData;
	
	HMAccelerometer accelerometer;
	
	Auton auton = null; // autonomous stuff

	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	
	@Override
	public void robotInit() {
		m_chooser.addDefault("Default Auto", kDefaultAuto);
		m_chooser.addObject("My Auto", kCustomAuto);
		SmartDashboard.putData("Auto choices", m_chooser);
		
		startPositionChooser.addDefault("Left", START_POSITION_LEFT);
		startPositionChooser.addObject("Center", START_POSITION_CENTER);
		startPositionChooser.addObject("Right", START_POSITION_RIGHT);
		SmartDashboard.putData("Start positions", startPositionChooser);
		
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
				
		sonar = new Sonar(Ports.Analog.SONAR); 
		
		gyro = new ADXRS450_Gyro(SPI.Port.kOnboardCS0); // we want to instantiate before we pass to drivetrain	
		
		drivetrain = new Drivetrain( frontLeft, frontRight, rearLeft, rearRight, gyro, this);		
		miniDrivetrain = new MiniDrivetrain(frontCenter, rearCenter, gyro, this);
		
		grasper = new Grasper(grasperLeft, grasperRight, sonar, this);
			
		camera = new HMCamera("GRIP/myContoursReport");
		
		compressor = new Compressor();
		compressor.checkCompressor();
		
		joyLeft = new Joystick(Ports.USB.LEFT); 
		joyRight = new Joystick(Ports.USB.RIGHT);
		
		gamepad = new Joystick(Ports.USB.GAMEPAD);

		control = new ControllerBase(gamepad, joyLeft, joyRight);	
		
		jack = new Jack();
		
		gyro.calibrate(); 
		gyro.reset();
		
		gameData = new GameData();
		
		accelerometer = new HMAccelerometer();
		
		elevatorControl = new Elevator(elevator,hingeControl);
		elevatorControl.home();
		
		hingeControl = new Hinge(hinge, this);
		hingeControl.home();
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
		m_autoSelected = m_chooser.getSelected();
		System.out.println("Auto selected: " + m_autoSelected);
		
		startPosition= startPositionChooser.getSelected();
		System.out.println("Start position: " + startPosition);
		
		gameData.update();
		
		//At this point we should know what auto run, where we started, and where our plates are located.
		//So we are ready for autonomousPeriodic to be called.
		updateToSmartDash();
		
		auton = new Auton(m_autoSelected, startPosition, gameData,
				drivetrain, jack, miniDrivetrain,
				hingeControl, grasper, elevatorControl,
				camera, this);
		
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
		miniDrivetrain.tripleCheckTurnAngleUsingPidController(); // checks if we are done turning if we were turning*/
		
		elevatorControl.checkHome();
		elevatorControl.tripleCheckMove();
		
		hingeControl.checkHome();
		hingeControl.tripleCheckMove();
		
		grasper.tripleCheckGraspUsingSonar(); // only enable if we want to stop automatically
		grasper.tripleCheckReleaseUsingSonar();
		
		// drive train flag JJ-			
		if(control.getPressedDown(ControllerBase.Joysticks.LEFT_STICK,ControllerBase.JoystickButtons.BTN4)
				|| control.getPressedDown(ControllerBase.Joysticks.LEFT_STICK,ControllerBase.JoystickButtons.BTN5))
		{
			if(largeDriveTrainSelected){
				largeDriveTrainSelected = false; 
				System.out.println("jack down");
				jack.setPosition(Jack.Position.DOWN);
			}
			else{
				largeDriveTrainSelected = true; 
				System.out.println("jack up");
				jack.setPosition(Jack.Position.UP);
			}			
		}
						
		if(largeDriveTrainSelected){
			drivetrain.joystickControl(joyLeft, joyRight, (control.getHeld(ControllerBase.Joysticks.LEFT_STICK,ControllerBase.JoystickButtons.BTN1) 
		                || control.getHeld(ControllerBase.Joysticks.RIGHT_STICK, ControllerBase.JoystickButtons.BTN1)));
		}
		else
		{
			miniDrivetrain.joystickControl(joyLeft, joyRight, (control.getHeld(ControllerBase.Joysticks.LEFT_STICK,ControllerBase.JoystickButtons.BTN1) 
	                || control.getHeld(ControllerBase.Joysticks.RIGHT_STICK, ControllerBase.JoystickButtons.BTN1)));
		}
		
		//hingeControl.joystickControl(joyRight);
		//elevatorControl.joystickControl(joyRight);
				
		
		//Stops the robot moving if pressed
		if(control.getPressedDown(ControllerBase.Joysticks.LEFT_STICK, ControllerBase.JoystickButtons.BTN3) || 
		   control.getPressedDown(ControllerBase.Joysticks.RIGHT_STICK, ControllerBase.JoystickButtons.BTN3))
		{
			drivetrain.stop();
			miniDrivetrain.stop();
			elevatorControl.stop();
			hingeControl.stop();
			grasper.stop();
		}
		
		if(control.getPressedDown(ControllerBase.Joysticks.LEFT_STICK, ControllerBase.JoystickButtons.BTN2) || 
				   control.getPressedDown(ControllerBase.Joysticks.RIGHT_STICK, ControllerBase.JoystickButtons.BTN2))
		{
			drivetrain.resetEncoders();
			miniDrivetrain.resetEncoders();
			gyro.reset(); // resets to zero
			elevatorControl.home();
			hingeControl.home();
		}
		else if(control.getPressedDown(ControllerBase.Joysticks.RIGHT_STICK, ControllerBase.JoystickButtons.BTN6))
		{
			drivetrain.moveDistance(50);
		}
		else if (control.getPressedDown(ControllerBase.Joysticks.RIGHT_STICK, ControllerBase.JoystickButtons.BTN4))
		{
			//drivetrain.moveDistanceAlongArc(-90);
			drivetrain.turnAngleUsingPidController(-90);
		}
		else if (control.getPressedDown(ControllerBase.Joysticks.RIGHT_STICK, ControllerBase.JoystickButtons.BTN5))
		{
			//drivetrain.moveDistanceAlongArc(+90);
			drivetrain.turnAngleUsingPidController(+90);
		}
		else if(control.getPressedDown(ControllerBase.Joysticks.LEFT_STICK, ControllerBase.JoystickButtons.BTN8) ||
			control.getPressedDown(ControllerBase.Joysticks.RIGHT_STICK, ControllerBase.JoystickButtons.BTN8))
		{
			turnAngleUsingPidControllerTowardCube();
		}
		else if(control.getPressedDown(ControllerBase.Joysticks.LEFT_STICK, ControllerBase.JoystickButtons.BTN9) ||
			control.getPressedDown(ControllerBase.Joysticks.RIGHT_STICK, ControllerBase.JoystickButtons.BTN9))
		{
			moveDistanceTowardCube();
		}
		
		
		if(control.getPressedDown(ControllerBase.Joysticks.LEFT_STICK, ControllerBase.JoystickButtons.BTN10) ||
			control.getPressedDown(ControllerBase.Joysticks.RIGHT_STICK, ControllerBase.JoystickButtons.BTN10))
		{
			elevatorControl.home();
		}
		
		/*if(control.getPressedDown(ControllerBase.Joysticks.LEFT_STICK, ControllerBase.JoystickButtons.BTN10) ||
				control.getPressedDown(ControllerBase.Joysticks.RIGHT_STICK, ControllerBase.JoystickButtons.BTN10))
		{
			hingeControl.home();
		}*/
		
		//elevator bound to start
		if (control.getPressedDown(ControllerBase.Joysticks.GAMEPAD, ControllerBase.GamepadButtons.START)) {
			System.out.println("Button Pushed");
			if (elevatorFlagUp) {
				if (hingeControl.isDown()) {
					elevatorControl.moveUp();
					System.out.println("Elevator should be moving up");
					elevatorFlagUp = false;
				} else {
					System.out.println("Lower hinge first!");
				}
			} else {
				if (hingeControl.isDown()) {
					elevatorControl.moveDown();
					System.out.println("Elevator should be moving down");
					elevatorFlagUp = true;
				} else {
					System.out.println("Lower hinge first!");
				}
			}
		}
		
		//hinge bound to back
		if (control.getPressedDown(ControllerBase.Joysticks.GAMEPAD, ControllerBase.GamepadButtons.BACK)) {
			System.out.println("Button Pushed");
			if (hingeFlagUp) {
				hingeControl.moveUp();
				System.out.println("Hinge should be moving up");
				hingeFlagUp = false;
			} else {
				hingeControl.moveDown();
				System.out.println("Hinge should be moving downn");
				hingeFlagUp = true;
			}
		}
			
		if (control.getHeld(ControllerBase.Joysticks.GAMEPAD, ControllerBase.GamepadButtons.Y)) { 
			grasper.grasp();
		}
		else if (control.getHeld(ControllerBase.Joysticks.GAMEPAD, ControllerBase.GamepadButtons.X)) {
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
