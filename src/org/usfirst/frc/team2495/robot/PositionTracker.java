package org.usfirst.frc.team2495.robot;

public class PositionTracker {
	
	double x = 0.0;
	double y = 0.0;
	double z = 0.0;
	
	double heading = 0.0;
	
	public PositionTracker() {
		 
	}
	
	// distance in inches
	public void moveDistance(double dist) {
		x = x + dist * Math.sin (Math.toRadians(heading)); 		
		y = y + dist * Math.cos (Math.toRadians(heading));
	}
	
	// angle in degrees
	public void turnAngle(double angle) {
		heading = heading + angle; 
	}
	
	// distance in inches, positive distance means slide right
	public void slideDistance(double dist) {
		turnAngle(90); // turn right
		moveDistance(dist);
		turnAngle(-90); // turn back left
	}
	
	public void updateAltitude(double altitude) {
		z = altitude;
	}
	
	public void printState() {
		System.out.println("PositionTracker: STATE x = " + x + ", y = " + y + " , " +
				"z = " + z + ", heading = " + heading + "\n");
	}

}
