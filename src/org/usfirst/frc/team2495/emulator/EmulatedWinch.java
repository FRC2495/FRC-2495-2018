
package org.usfirst.frc.team2495.emulator;

import org.usfirst.frc.team2495.robot.*;

import edu.wpi.first.wpilibj.Joystick;


public class EmulatedWinch implements IWinch {
	
	public EmulatedWinch() {

	}
	
	public synchronized void winchUp() {
		
	}
	
	public synchronized void winchDown() {

	}
	
	public synchronized void winchUpAndStop() {

	}
	
	public synchronized void winchDownAndStop() {

	}
	
	public synchronized void stop() {

	}
	
	
	// NOTE THAT THIS METHOD WILL IMPACT BOTH OPEN AND CLOSED LOOP MODES
	public void setNominalAndPeakOutputs(double peakOutput)
	{

	}
	
	public synchronized boolean isWinchingUp() {
		return false;
	}
	
	public synchronized boolean isWinchingDown(){
		return false;
	}

	// for debug purpose only
	public void joystickControl(Joystick joystick)
	{

	}

}
