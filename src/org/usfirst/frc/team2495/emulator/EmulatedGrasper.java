package org.usfirst.frc.team2495.emulator;

import org.usfirst.frc.team2495.robot.*;
import edu.wpi.first.wpilibj.Joystick;

public class EmulatedGrasper implements IGrasper{

	IHinge hinge;
	IElevator elevator;
	
	public EmulatedGrasper(IHinge hinge_in, IElevator elevator_in) {
		hinge = hinge_in;
		elevator = elevator_in;
	}

	public void grasp() {
		System.out.println("Grasper: BEGIN grasp (grasp cube)");
		
		if (hinge != null && (!hinge.hasBeenHomed() || !hinge.isDown())) {
			System.out.println("VIOLATION: cannot grasp when hinge has not been homed or is not down!");
		}
		
		if (elevator != null && (!elevator.hasBeenHomed() || !elevator.isDown())) {
			System.out.println("VIOLATION: cannot grasp when elevator has not been homed or is not down!");
		}
	}
	
	public void release() {
		System.out.println("Grasper: BEGIN release (release cube)");
		
		if (hinge != null && (!hinge.hasBeenHomed() || hinge.isUp())) {
			System.out.println("VIOLATION: cannot release when hinge has not been homed or is up!");
		}
		
		if (elevator != null && (!elevator.hasBeenHomed() || elevator.isDown())) {
			System.out.println("VIOLATION: cannot release when elevator has not been homed or is down!");
		}
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










