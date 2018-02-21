package org.usfirst.frc.team2495.emulator;

import org.usfirst.frc.team2495.robot.*;
import edu.wpi.first.wpilibj.Joystick;

public class EmulatedGrasper implements IGrasper{

	
	public EmulatedGrasper() {
	}
	

	public void grasp() {
		System.out.println("Grasper: BEGIN grasp");
	}
	
	public void release() {
		System.out.println("Grasper: BEGIN release");
	}
	
	public void stop() {
	}
	
	
	public boolean tripleCheckGraspUsingSonar() {
		return false;
	}
	
	// do not use in teleop - for auton only
	public void waitGraspUsingSonar() {
		System.out.println("Grasper: END grasp");
	}
	
	public boolean tripleCheckReleaseUsingSonar() {
		return false;
	}
	
	// do not use in teleop - for auton only
	public void waitReleaseUsingSonar() {
		System.out.println("Grasper: END release");
	}
		
	// NOTE THAT THIS METHOD WILL IMPACT BOTH OPEN AND CLOSED LOOP MODES
	public void setNominalAndPeakOutputs(double peakOutput)
	{
	}
	
	public boolean isGrasping() {
		return false;
	}
	
	public boolean isReleasing(){
		return false;
	}

	// for debug purpose only
	public void joystickControl(Joystick joystick)
	{
	}
}










