package org.usfirst.frc.team2495.test;

import org.usfirst.frc.team2495.robot.*;

public class AutonTester {

	public static void main(String[] args) {
		
		String autoSelected= Robot.kCustomAuto;
		String startPosition = Robot.START_POSITION_LEFT;
		
		// todo create emulated devices and pass them
		GameData gameData = null;
		Drivetrain drivetrain = null;
		Jack jack = null;
		MiniDrivetrain miniDrivetrain = null;
		Hinge hinge = null;
		Grasper grasper = null;
		Elevator elevator = null;
		HMCamera camera = null;
		Robot robot = null;

		Auton auton = new Auton(autoSelected, startPosition, gameData, drivetrain, jack, miniDrivetrain, hinge, grasper, elevator, camera, robot);
		
		auton.initialize();
		
		// todo put in a loop
		auton.execute();
	}

}
