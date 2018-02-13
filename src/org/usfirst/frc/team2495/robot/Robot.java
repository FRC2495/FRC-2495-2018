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

import org.usfirst.frc.team2495.robot.GameData.Plate;

/*import com.ctre.phoenix.motorcontrol.can.TalonSRX; */
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.ADXRS450_Gyro;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */


public class Robot extends IterativeRobot {
	private static final String kDefaultAuto = "Default";
	private static final String kCustomAuto = "My Auto";
	private String m_autoSelected;
	private SendableChooser<String> m_chooser = new SendableChooser<>();
	
	private static final String START_POSITION_LEFT = "Left";
	private static final String START_POSITION_CENTER = "Center";
	private static final String START_POSITION_RIGHT = "Right";
	private String startPosition;
	private SendableChooser<String> startPositionChooser = new SendableChooser<>();
	
	Drivetrain drivetrain;
	
	MiniDrivetrain miniDrivetrain;
	
	Grasper grasper;
	
	HMCamera camera;
	
	WPI_TalonSRX frontLeft;
	WPI_TalonSRX frontRight;
	WPI_TalonSRX rearLeft; 
	WPI_TalonSRX rearRight;
	
	WPI_TalonSRX frontCenter;
	WPI_TalonSRX rearCenter;
	
	WPI_TalonSRX grasperLeft;
	WPI_TalonSRX grasperRight;
	
	Joystick joyLeft, joyRight;
	Joystick gamepad;
	
	ADXRS450_Gyro gyro; // gyro
	
	Compressor compressor; // the compressor's lifecycle needs to be the same as the robot
	
	ControllerBase control;

	boolean hasGyroBeenManuallyCalibratedAtLeastOnce = false;
	
	WPI_TalonSRX elevator;
	boolean elevatorFlagUp = true;
	Elevator elevatorControl;
	Jack jack;
	boolean driveTrainSelect = false;
	
	GameData gameData;
	HMAccelerometer accelerometer;

	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	
	@Override
	public void robotInit() {
		m_chooser.addDefault("Default Auto", kDefaultAuto);
		m_chooser.addObject("My Auto", kCustomAuto);
		SmartDashboard.putData("Auto choices", m_chooser);
		
		m_chooser.addDefault("Left", START_POSITION_LEFT);
		m_chooser.addObject("Center", START_POSITION_CENTER);
		m_chooser.addObject("Center", START_POSITION_RIGHT);
		SmartDashboard.putData("Start position", startPositionChooser);
		
		frontLeft = new WPI_TalonSRX(Ports.CAN.LEFT_FRONT);
		frontRight = new WPI_TalonSRX(Ports.CAN.RIGHT_FRONT);
		rearLeft = new WPI_TalonSRX(Ports.CAN.LEFT_REAR);
		rearRight= new WPI_TalonSRX(Ports.CAN.RIGHT_REAR);
		
		frontCenter= new WPI_TalonSRX(Ports.CAN.FRONT_CENTER);
		rearCenter= new WPI_TalonSRX(Ports.CAN.REAR_CENTER);
		
		elevator = new WPI_TalonSRX(Ports.CAN.ELEVATOR);
		
		grasperLeft = new WPI_TalonSRX(Ports.CAN.GRASPER_LEFT);
		grasperRight = new WPI_TalonSRX(Ports.CAN.GRASPER_RIGHT);
		
		
		// TODO 2017 robot is 0, 2018 is 2
		gyro = new ADXRS450_Gyro(SPI.Port.kOnboardCS0); // we want to instantiate before we pass to drivetrain	
		drivetrain = new Drivetrain(frontLeft, frontRight, rearLeft, rearRight, gyro, this);
		
		miniDrivetrain = new MiniDrivetrain(frontCenter, rearCenter, gyro, this);
		
		grasper = new Grasper(grasperLeft,grasperRight);
			
		camera = new HMCamera("GRIP/myContoursReport");
		
		compressor = new Compressor();
		compressor.checkCompressor();
		
		joyLeft = new Joystick ( Ports.USB.LEFT); 
		joyRight = new Joystick (Ports.USB.RIGHT);
		
		gamepad = new Joystick(Ports.USB.GAMEPAD);

		control = new ControllerBase(gamepad, joyLeft, joyRight);	
		
		jack = new Jack();
		
		gyro.calibrate(); 
		gyro.reset();
		
		gameData = new GameData();
		accelerometer = new HMAccelerometer();
		
		elevatorControl = new Elevator(elevator);
		elevatorControl.home();
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
		
		//At this point we should know what auton run, where we started, and where our plates are located.
		//So we are ready for autonomousPeriodic to be called.
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		switch (m_autoSelected) {
			case kCustomAuto:
				// TODO Put custom auto code here
				if (startPosition == START_POSITION_LEFT)
				{
					if (gameData.getAssignedPlateAtScale() == Plate.LEFT)
					{
						// go straight then go back get the closest cube and go to the switch 
					}
					else if (gameData.getAssignedPlateAtScale() == Plate.RIGHT)
					{
						// go straight then go right then back get the closest cube and go to the switch 
					}
				}
				else if (startPosition == START_POSITION_CENTER)
				{
					if (gameData.getAssignedPlateAtFirstSwitch() == Plate.LEFT)
					{

					}
					else if (gameData.getAssignedPlateAtFirstSwitch() == Plate.RIGHT)
					{

					}	
				}
				else if (startPosition == START_POSITION_RIGHT)
				{
					if (gameData.getAssignedPlateAtScale() == Plate.RIGHT)
					{
						// go straight then go back get the closest cube and go to the switch 

					}
					else if (gameData.getAssignedPlateAtScale() == Plate.LEFT)
					{
						// go straight then go right then back get the closest cube and go to the switch 
					}
				}						
						
				break;
			case kDefaultAuto:
			default:
				// We do nothing
				break;
		}
	}

	@Override
	public void teleopInit() {
		drivetrain.stop(); // very important!
		
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
		
		// drive train flag JJ-
		
		
		if(control.getPressedDown(ControllerBase.Joysticks.LEFT_STICK,ControllerBase.JoystickButtons.BTN4)
				|| control.getPressedDown(ControllerBase.Joysticks.LEFT_STICK,ControllerBase.JoystickButtons.BTN5))
		{
			if(driveTrainSelect){
				driveTrainSelect = false; 
				System.out.println("jack down");
				jack.setPosition(Jack.Position.DOWN);
			}
			else{
				driveTrainSelect = true; 
				System.out.println("jack up");
				jack.setPosition(Jack.Position.UP);
			}
				
		}
				
		
		if(driveTrainSelect){
			drivetrain.joystickControl(joyLeft, joyRight, (control.getHeld(ControllerBase.Joysticks.LEFT_STICK,ControllerBase.JoystickButtons.BTN1) 
		                || control.getHeld(ControllerBase.Joysticks.RIGHT_STICK, ControllerBase.JoystickButtons.BTN1)));
		}
		else
		{
			miniDrivetrain.joystickControl(joyLeft, joyRight, (control.getHeld(ControllerBase.Joysticks.LEFT_STICK,ControllerBase.JoystickButtons.BTN1) 
	                || control.getHeld(ControllerBase.Joysticks.RIGHT_STICK, ControllerBase.JoystickButtons.BTN1)));
		}
				
		
		//Stops the robot moving if pressed
		if(control.getPressedDown(ControllerBase.Joysticks.LEFT_STICK, ControllerBase.JoystickButtons.BTN3) || 
		   control.getPressedDown(ControllerBase.Joysticks.RIGHT_STICK, ControllerBase.JoystickButtons.BTN3))
		{
			drivetrain.stop();
			miniDrivetrain.stop();
		}
		
		if(control.getPressedDown(ControllerBase.Joysticks.LEFT_STICK, ControllerBase.JoystickButtons.BTN2) || 
				   control.getPressedDown(ControllerBase.Joysticks.RIGHT_STICK, ControllerBase.JoystickButtons.BTN2))
		{
			drivetrain.resetEncoders();
			miniDrivetrain.resetEncoders();
			gyro.reset(); // resets to zero
			elevatorControl.home();
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
		
		//elevator bound to start
		if (control.getPressedDown(ControllerBase.Joysticks.GAMEPAD, ControllerBase.GamepadButtons.START)) {
			System.out.println("Button Pushed");
			if (elevatorFlagUp) {
				elevatorControl.moveUp();
				System.out.println("Should be Moving");
				elevatorFlagUp = false;
			} else {
				elevatorControl.moveDown();
				System.out.println("Should be Moving");
				elevatorFlagUp = true;
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
			grasper.stop();		
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
	
	private void turnAngleUsingPidControllerTowardCube() {
		drivetrain.turnAngleUsingPidController(camera.getAngleToTurnToTarget());
		/*drivetrain.turnAngleUsingPidController(calculateProperTurnAngle(
				camera.getAngleToTurnToTarget(),camera.getDistanceToTargetUsingHorizontalFov()));*/
	}
	
	private void moveDistanceTowardCube() {
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
        SmartDashboard.putBoolean("Gyro Manually Calibrated?",hasGyroBeenManuallyCalibratedAtLeastOnce);
        SmartDashboard.putNumber("PID Error", drivetrain.turnPidController.getError());
        SmartDashboard.putNumber("PID Motor Value", drivetrain.turnPidController.get());
        SmartDashboard.putBoolean("PID On Target", drivetrain.turnPidController.onTarget());
        
        SmartDashboard.putNumber("Tilt", accelerometer.getTilt());
        SmartDashboard.putString("First Switch", gameData.getAssignedPlateAtFirstSwitch().toString());
        SmartDashboard.putString("Scale", gameData.getAssignedPlateAtScale().toString());
        SmartDashboard.putString("Second Switch", gameData.getAssignedPlateAtSecondSwitch().toString());
        
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
