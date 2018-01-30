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
	
	Drivetrain drivetrain;
	WPI_TalonSRX frontLeft;
	WPI_TalonSRX frontRight;
	WPI_TalonSRX rearLeft; 
	WPI_TalonSRX rearRight;
	Joystick joyLeft, joyRight;
	Joystick gamepad;
	
	ADXRS450_Gyro gyro; // gyro
	
	ControllerBase control;

	boolean hasGyroBeenManuallyCalibratedAtLeastOnce = false;
	
	/**
	 * This function is run when the robot is first started up and should be
	 * used for any initialization code.
	 */
	
	@Override
	public void robotInit() {
		m_chooser.addDefault("Default Auto", kDefaultAuto);
		m_chooser.addObject("My Auto", kCustomAuto);
		SmartDashboard.putData("Auto choices", m_chooser);
		
		frontLeft = new WPI_TalonSRX(Ports.CAN.LEFT_FRONT);
		frontRight = new WPI_TalonSRX(Ports.CAN.RIGHT_FRONT);
		rearLeft = new WPI_TalonSRX(Ports.CAN.LEFT_REAR);
		rearRight= new WPI_TalonSRX(Ports.CAN.RIGHT_REAR);
		
		gyro = new ADXRS450_Gyro(SPI.Port.kOnboardCS0); // we want to instantiate before we pass to drivetrain	
		drivetrain = new Drivetrain(frontLeft, frontRight, rearLeft, rearRight);
		
		joyLeft = new Joystick ( Ports.USB.LEFT); 
		joyRight = new Joystick (Ports.USB.RIGHT);
		
		gamepad = new Joystick(Ports.USB.GAMEPAD);

		control = new ControllerBase(gamepad, joyLeft, joyRight);	
		
		gyro.calibrate(); 
		gyro.reset();
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
		// autoSelected = SmartDashboard.getString("Auto Selector",
		// defaultAuto);
		System.out.println("Auto selected: " + m_autoSelected);
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		switch (m_autoSelected) {
			case kCustomAuto:
				// Put custom auto code here
				break;
			case kDefaultAuto:
			default:
				// Put default auto code here
				break;
		}
	}

	@Override
	public void teleopInit() {
	}
	
	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
		control.update();
		
		drivetrain.tripleCheckMoveDistance(); // checks if we are done moving if we were moving
		
		drivetrain.joystickControl(joyLeft, joyRight, (control.getHeld(ControllerBase.Joysticks.LEFT_STICK,ControllerBase.JoystickButtons.BTN1) 
                || control.getHeld(ControllerBase.Joysticks.RIGHT_STICK, ControllerBase.JoystickButtons.BTN1)));
		
		if(control.getPressedDown(ControllerBase.Joysticks.LEFT_STICK, ControllerBase.JoystickButtons.BTN2) || 
				   control.getPressedDown(ControllerBase.Joysticks.RIGHT_STICK, ControllerBase.JoystickButtons.BTN2))
		{
			drivetrain.resetEncoders();
		}
		else if(control.getPressedDown(ControllerBase.Joysticks.RIGHT_STICK, ControllerBase.JoystickButtons.BTN6))
		{
			drivetrain.moveDistance(50);
		}
		else if (control.getPressedDown(ControllerBase.Joysticks.RIGHT_STICK, ControllerBase.JoystickButtons.BTN4))
		{
			drivetrain.turnDistance(50, ControllerBase.JoystickButtons.BTN4);
		}
		else if (control.getPressedDown(ControllerBase.Joysticks.RIGHT_STICK, ControllerBase.JoystickButtons.BTN5))
		{
			drivetrain.turnDistance(50, ControllerBase.JoystickButtons.BTN5);
		}
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
	}

	@Override
	public void disabledPeriodic() {	
		control.update();
		
		if (control.getPressedDown(ControllerBase.Joysticks.GAMEPAD, ControllerBase.GamepadButtons.A)) {
			gyro.calibrate();
			gyro.reset();
			hasGyroBeenManuallyCalibratedAtLeastOnce = true; // we flag that this was done
		}
		
		updateToSmartDash();		
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

        SmartDashboard.putBoolean("Gyro Manually Calibrated?",hasGyroBeenManuallyCalibratedAtLeastOnce);
	}
}
