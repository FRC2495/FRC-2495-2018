package org.usfirst.frc.team2495.emulator;

import org.usfirst.frc.team2495.robot.*;
import edu.wpi.first.wpilibj.PIDSource;
import edu.wpi.first.wpilibj.PIDSourceType;

public class EmulatedHMCamera implements PIDSource, IHMCamera {

	public EmulatedHMCamera(String networktable) {
		// we don't need to care about nt
	}

	public boolean isCoherent() {
		return true;
	}

	public int getNumberOfTargets() {
		return 1;
	}

	public boolean acquireTargets(boolean waitForNewInfo) {
		/*if (waitForNewInfo) {
			Timer.delay(HmCamera.CAMERA_CATCHUP_DELAY_SECS);
		}*/
		
		if (isCoherent() && getNumberOfTargets() > 0) { // if we have targets
			return true;
		} else {
			return false;
		}
	}

	public boolean checkForCube() {
		return getNumberOfTargets() > 0; // cube is at least one target
	}

	public double getDistanceToTargetUsingVerticalFov() {
		return 12.0; // because why not?
	}
	
	public double getDistanceToTargetUsingHorizontalFov()
	{
		return 12.0; // because why not?
	}

	public double getAngleToTurnToTarget() {
		return 0;
	}
	
	public double getPixelDisplacementToCenterToTarget() {
		return 0;
	}

	public double[] getArea() {
		double[] array = new double[1];
		
		array[0] = 160*120;
		
		return array;
	}

	public double[] getWidth() {
		double[] array = new double[1];
		
		array[0] = 160;
		
		return array;
	}

	public double[] getHeight() {
		double[] array = new double[1];
		
		array[0] = 120;
		
		return array;
	}

	public double[] getCenterX() {
		double[] array = new double[1];
		
		array[0] = 0;
		
		return array;
	}

	public double[] getCenterY() {
		double[] array = new double[1];
		
		array[0] = 0;
		
		return array;
	}
	
	public void setPIDSourceType(PIDSourceType pidSource)
	{
		// always displacement!
	}

	public PIDSourceType getPIDSourceType()
	{
		return PIDSourceType.kDisplacement;
	}
	
	public double pidGet()
	{
		acquireTargets(false); // we don't want to wait but the lag might be problematic
		
		return -getPixelDisplacementToCenterToTarget(); // we are located at the opposite or the displacement we need to shift by
	}
}
