package org.usfirst.frc.team2495.test;

import org.usfirst.frc.team2495.robot.*;
import org.usfirst.frc.team2495.emulator.*;

public class AutonTester {

	public static void main(String[] args) {
		
		String autoSelected= Robot.kCustomAuto;
		String startPosition = Robot.START_POSITION_LEFT;
		
		EmulatedGameData gameData = new EmulatedGameData();
		gameData.setGameSpecificMessage("LRL");

		// todo create missing emulated devices
		Drivetrain drivetrain = null;
		IJack jack = new EmulatedJack();
		MiniDrivetrain miniDrivetrain = null;
		IHinge hinge = new EmulatedHinge();
		Grasper grasper = null;
		IElevator elevator = new EmulatedElevator();
		HMCamera camera = null;
		Robot robot = null;

		Auton auton = new Auton(autoSelected, startPosition, gameData, drivetrain, jack, miniDrivetrain, hinge, grasper, elevator, camera, robot);
		
		auton.initialize();
		
		// todo put in a loop
		auton.execute();
	}

}
