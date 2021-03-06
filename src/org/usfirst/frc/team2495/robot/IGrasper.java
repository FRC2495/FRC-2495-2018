package org.usfirst.frc.team2495.robot;

import edu.wpi.first.wpilibj.Joystick;

public interface IGrasper {

	public void grasp();
	
	public void release();
	
	public void stop();
	
	public void waitGraspOrRelease();
		
	public boolean tripleCheckGraspUsingSonar();
	
	public void waitGraspUsingSonar();
	
	public boolean tripleCheckReleaseUsingSonar();
	
	// do not use in teleop - for auton only
	public void waitReleaseUsingSonar();
		
	// NOTE THAT THIS METHOD WILL IMPACT BOTH OPEN AND CLOSED LOOP MODES
	public void setNominalAndPeakOutputs(double peakOutput);
	
	public boolean isGrasping();
	
	public boolean isReleasing();

	// for debug purpose only
	public void joystickControl(Joystick joystick);
}










