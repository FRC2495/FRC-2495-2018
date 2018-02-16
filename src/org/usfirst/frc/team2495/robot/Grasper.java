/**
 * 
 */
package org.usfirst.frc.team2495.robot;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.Timer;
/**
 * @author Joshua
 *
 */
public class Grasper {

	/**
	 * 
	 */
	static final double MAX_PCT_OUTPUT = 1.0;
	static final int WAIT_MS = 1000;
	
	
	WPI_TalonSRX grasperLeft , grasperRight; 
	
	Timer time=new Timer();
	
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
	
	public void graspAuto () {
		grasp();
		try {
			time.wait(WAIT_MS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		stop();
	}
	
	public void releaseAuto() {
		release();
		try {
			time.wait(WAIT_MS);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		stop();
	}
}










