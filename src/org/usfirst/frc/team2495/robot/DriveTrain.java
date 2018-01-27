package org.usfirst.frc.team2495.robot;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;

public class DriveTrain {
	
	WPI_TalonSRX frontLeft,rearLeft,frontRight,rearRight;
	DifferentialDrive arcadeDrive; 
	
	public DriveTrain(WPI_TalonSRX frontLeft_in ,WPI_TalonSRX frontRight_in , WPI_TalonSRX rearLeft_in ,WPI_TalonSRX rearRight_in) 
	{
		frontLeft = frontLeft_in;
		frontRight = frontRight_in;
		rearLeft = rearLeft_in;
		rearRight = rearRight_in;
		
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
	//arcadeDrive.arcadeDrive(joyLeft.getY(),joyRight.getX());
	arcadeDrive.tankDrive(joyLeft.getY(),joyRight.getY());
	
	}
	
	
	
	
}


