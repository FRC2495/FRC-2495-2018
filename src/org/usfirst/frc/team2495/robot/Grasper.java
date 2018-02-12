/**
 * 
 */
package org.usfirst.frc.team2495.robot;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

/**
 * @author Joshua
 *
 */
public class Grasper {

	/**
	 * 
	 */
	WPI_TalonSRX graspLeft , graspRight; 
	
	public Grasper( WPI_TalonSRX graspLeft_in, WPI_TalonSRX graspRight_in) {
		// TODO Auto-generated constructor stub
		
		graspLeft = graspLeft_in;
		graspRight = graspRight_in;	
	}

	public void grasp() { 
	}
	
	public void release() {
	}
}










