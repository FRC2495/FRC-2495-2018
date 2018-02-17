/**
 * 
 */
package org.usfirst.frc.team2495.robot;
import java.util.Calendar;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

import edu.wpi.first.wpilibj.DriverStation;
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

	static final int TALON_TIMEOUT_MS = 10;
	
	WPI_TalonSRX grasperLeft , grasperRight; 
	
	public Grasper( WPI_TalonSRX grasperLeft_in, WPI_TalonSRX grasperRight_in) {
		
		grasperLeft = grasperLeft_in;
		grasperRight = grasperRight_in;
		
		// Mode of operation during Neutral output may be set by using the setNeutralMode() function.
		// As of right now, there are two options when setting the neutral mode of a motor controller,
		// brake and coast.
		grasperLeft.setNeutralMode(NeutralMode.Brake);
		grasperRight.setNeutralMode(NeutralMode.Brake);
		
		// Motor controller output direction can be set by calling the setInverted() function as seen below.
		// Note: Regardless of invert value, the LEDs will blink green when positive output is requested (by robot code or firmware closed loop).
		// Only the motor leads are inverted. This feature ensures that sensor phase and limit switches will properly match the LED pattern
		// (when LEDs are green => forward limit switch and soft limits are being checked).
		//this might me wrong =j
		grasperLeft.setInverted(true);
		grasperRight.setInverted(false);
		
		// Both the Talon SRX and Victor SPX have a follower feature that allows the motor controllers to mimic another motor controller's output.
		// Users will still need to set the motor controller's direction, and neutral mode.
		// The method follow() allows users to create a motor controller follower of not only the same model, but also other models
		// , talon to talon, victor to victor, talon to victor, and victor to talon.
		grasperRight.follow(grasperLeft);
		
		// set peak output to max in case if had been reduced previously
		setNominalAndPeakOutputs(MAX_PCT_OUTPUT);
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
	
	private void waitALittleAndStop() {
		long start = Calendar.getInstance().getTimeInMillis();

		while (true) { 		
			if (!DriverStation.getInstance().isAutonomous()
					|| Calendar.getInstance().getTimeInMillis() - start >= WAIT_MS) {
				System.out.println("Wait is over");
				stop();
				break;
			}

			try {
				Thread.sleep(20); // sleeps a little
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			//robot.updateToSmartDash();
		}
	}
	
	// for autonomous mode only
	public void graspAuto () {
		grasp();
		
		waitALittleAndStop();
		
		//stop();
	}
	
	// for autonomous mode only
	public void releaseAuto() {
		release();
		
		waitALittleAndStop();
		
		//stop();
	}
	
	// NOTE THAT THIS METHOD WILL IMPACT BOTH OPEN AND CLOSED LOOP MODES
	public void setNominalAndPeakOutputs(double peakOutput)
	{
		grasperLeft.configPeakOutputForward(peakOutput, TALON_TIMEOUT_MS);
		grasperLeft.configPeakOutputReverse(-peakOutput, TALON_TIMEOUT_MS);
		grasperRight.configPeakOutputForward(peakOutput, TALON_TIMEOUT_MS);
		grasperRight.configPeakOutputReverse(-peakOutput, TALON_TIMEOUT_MS);
		
		grasperRight.configNominalOutputForward(0, TALON_TIMEOUT_MS);
		grasperLeft.configNominalOutputForward(0, TALON_TIMEOUT_MS);
		grasperRight.configNominalOutputReverse(0, TALON_TIMEOUT_MS);
		grasperLeft.configNominalOutputReverse(0, TALON_TIMEOUT_MS);
	}

}










