package org.usfirst.frc.team2495.test;

import org.usfirst.frc.team2495.robot.*;
import org.usfirst.frc.team2495.emulator.*;

public class AutonTester {

	public static void main(String[] args) {
		
		String autoSelected= Robot.kCustomAuto;
		String startPosition = Robot.START_POSITION_LEFT;
		
		// todo create emulated devices and pass them
		GameData gameData = null;
		Drivetrain drivetrain = null;
		IJack jack = new EmulatedJack();
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
