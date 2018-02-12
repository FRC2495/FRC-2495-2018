/**
 * 
 */
package org.usfirst.frc.team2495.robot;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

/**
 * @author Joshua
 *
 */
public class Grasper {

	/**
	 * 
	 */
	static final double MAX_PCT_OUTPUT = 1.0;
	
	WPI_TalonSRX grasperLeft , grasperRight; 
	
	public Grasper( WPI_TalonSRX grasperLeft_in, WPI_TalonSRX grasperRight_in) {
		
		grasperLeft = grasperLeft_in;
		grasperRight = grasperRight_in;
		
		grasperLeft.setNeutralMode(NeutralMode.Brake);
		grasperRight.setNeutralMode(NeutralMode.Brake);
		
		//this might me wrong =j
		grasperLeft.setInverted(true);
		grasperRight.setInverted(false);
		
		grasperRight.follow(grasperLeft);	
	}

	public void grasp() {
		grasperLeft.set(ControlMode.PercentOutput, MAX_PCT_OUTPUT);
	}
	
	public void release() {
		grasperLeft.set(ControlMode.PercentOutput, -MAX_PCT_OUTPUT);
	}
	
	public void stop() {
		grasperLeft.set(ControlMode.PercentOutput, 0);
	}
}










