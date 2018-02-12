/**
 * 
 */
package org.usfirst.frc.team2495.robot;
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
	WPI_TalonSRX grasperLeft , grasperRight; 
	
	public Grasper( WPI_TalonSRX grasperLeft_in, WPI_TalonSRX grasperRight_in) {
		// TODO Auto-generated constructor stub
		
		grasperLeft = grasperLeft_in;
		grasperRight = grasperRight_in;
		
		grasperLeft.setNeutralMode(NeutralMode.Brake);
		grasperRight.setNeutralMode(NeutralMode.Brake);
		
		//this might me wrong =j
		grasperLeft.setInverted(true);
		grasperRight.setInverted(false);
	}

	public void grasp() { 
	}
	
	public void release() {
	}
}










