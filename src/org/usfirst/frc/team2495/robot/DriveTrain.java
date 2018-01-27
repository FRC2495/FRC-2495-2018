package org.usfirst.frc.team2495.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

public class DriveTrain {
	
	static final double PERIMETER_WHEEL_INCHES = 4 * Math.PI;
	static final int PRIMARY_PID_LOOP = 0;
	static final int SLOT_0 = 0;
	static final int TALON_TIMEOUT_MS = 10;
	static final int TICKS_PER_REVOLUTION = 4096;	
	
	WPI_TalonSRX frontLeft,rearLeft,frontRight,rearRight;
	DifferentialDrive arcadeDrive; 
	
	public DriveTrain(WPI_TalonSRX frontLeft_in ,WPI_TalonSRX frontRight_in , WPI_TalonSRX rearLeft_in ,WPI_TalonSRX rearRight_in) 
	{
		frontLeft = frontLeft_in;
		frontRight = frontRight_in;
		rearLeft = rearLeft_in;
		rearRight = rearRight_in;
		
		frontLeft.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative,
				PRIMARY_PID_LOOP, TALON_TIMEOUT_MS);
				
		frontRight.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative,
				PRIMARY_PID_LOOP, TALON_TIMEOUT_MS);	
		
		frontLeft.setInverted(true);
		frontRight.setInverted(true);
		rearLeft.setInverted(true); 
		rearRight.setInverted(true);
		
		// motors will turn in opposite directions if not inverted 
		
		rearLeft.follow(frontLeft);
		rearRight.follow(frontRight);
		
		arcadeDrive = new DifferentialDrive(frontLeft, frontRight); 
	}
    
	public void joystickControl(Joystick joyLeft , Joystick joyRight)
	
	{
	 
	//frontLeft.set(ControlMode.PercentOutput, joyLeft.getY());	
	//frontRight.set(ControlMode.PercentOutput, joyRight.getY());
	// this is tank drive
		arcadeDrive.arcadeDrive(joyLeft.getY(),joyRight.getX());
	//arcadeDrive.tankDrive(joyLeft.getY(),joyRight.getY());
	
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
	
	
	
}


