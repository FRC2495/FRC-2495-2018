package org.usfirst.frc.team2495.test;

import org.usfirst.frc.team2495.robot.*;
import org.usfirst.frc.team2495.emulator.*;

public class AutonTester {

	public static void main(String[] args) {
		
		String autoSelected = Robot.kCustomAuto;
		
		// CHANGE THE STARTING POSITION HERE (START_POSITION_LEFT or START_POSITION_CENTER or START_POSITION_RIGHT)
		String startPosition = Robot.START_POSITION_LEFT;
		
		EmulatedGameData gameData = new EmulatedGameData();
		
		gameData.setGameSpecificMessage("LLL");
		test(autoSelected, startPosition, gameData);

		gameData.setGameSpecificMessage("LRL");
		test(autoSelected, startPosition, gameData);

		gameData.setGameSpecificMessage("RRR");
		test(autoSelected, startPosition, gameData);
		
		gameData.setGameSpecificMessage("RLR");
		test(autoSelected, startPosition, gameData);
	}
		
	public static void test(String autoSelected, String startPosition, EmulatedGameData gameData) {	

		IJack jack = new EmulatedJack();
		IDrivetrain drivetrain = new EmulatedDrivetrain(jack);
		IMiniDrivetrain miniDrivetrain = new EmulatedMiniDrivetrain(jack);
		
		IHinge hinge = new EmulatedHinge();
		IElevator elevator = new EmulatedElevator(hinge);
		IGrasper grasper = new EmulatedGrasper(hinge,elevator);
		
		HMCamera camera = null;
		Robot robot = null;
		
		PositionTracker tracker = new PositionTracker();
		
		System.out.println("\nAutonTester started with following settings:\n");
		
		System.out.println("Auto selected: " + autoSelected);	
		System.out.println("Start position: " + startPosition);
		
		System.out.println("First switch: " + gameData.getAssignedPlateAtFirstSwitch());
		System.out.println("Scale: " + gameData.getAssignedPlateAtScale());
		System.out.println("Second switch: " + gameData.getAssignedPlateAtSecondSwitch());

		Auton auton = new Auton(autoSelected, startPosition, gameData, drivetrain, jack, miniDrivetrain, hinge, grasper, elevator, camera, robot, tracker);
		
		System.out.println("\nAuton initalizing\n");
		
		auton.initialize();
		
		System.out.println("\nAuton executing\n");
		
		// todo put in a loop
		auton.execute();
		
		System.out.println("\nAuton end\n");
		
		auton.end();
		
		System.out.println("\nAutonTester finished\n");
	}

}
