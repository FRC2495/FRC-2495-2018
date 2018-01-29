package org.usfirst.frc.team2495.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

public class Drivetrain {
	
	static final double PERIMETER_WHEEL_INCHES = 4 * Math.PI;
	static final int PRIMARY_PID_LOOP = 0;
	static final int SLOT_0 = 0;
	static final int TALON_TIMEOUT_MS = 10;
	static final int TICKS_PER_REVOLUTION = 4096;	
	
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
	}
    
	public void joystickControl(Joystick joyLeft, Joystick joyRight, boolean held) // sets talons to
	// joystick control
	{
		//frontRight.set(ControlMode.PercentOutput, joyRight.getY());
		//frontLeft.set(ControlMode.PercentOutput, joyLeft.getY());
		
		//differentialDrive.tankDrive(joyLeft.getY(), -joyRight.getY()); // right needs to be reversed
		
		differentialDrive.arcadeDrive(-joyRight.getX(), joyLeft.getY()); // right needs to be reversed
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
	
	public void resetEncoders() {
		frontRight.set(ControlMode.PercentOutput, 0); // we switch to open loop to be safe.
		frontLeft.set(ControlMode.PercentOutput, 0);			
		
		frontRight.setSelectedSensorPosition(0, PRIMARY_PID_LOOP, TALON_TIMEOUT_MS);
		frontLeft.setSelectedSensorPosition(0, PRIMARY_PID_LOOP, TALON_TIMEOUT_MS);
	}	
}


