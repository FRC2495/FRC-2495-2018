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

		gameData.setGameSpecificMessage("LRR");
		test(autoSelected, startPosition, gameData);

		gameData.setGameSpecificMessage("RRR");
		test(autoSelected, startPosition, gameData);
		
		gameData.setGameSpecificMessage("RLR");
		test(autoSelected, startPosition, gameData);

		gameData.setGameSpecificMessage("RLL");
		test(autoSelected, startPosition, gameData);
}
		
	public static void test(String autoSelected, String startPosition, EmulatedGameData gameData) {	

		PositionTracker tracker = new PositionTracker();
		
		IJack jack = new EmulatedJack();
		IDrivetrain drivetrain = new EmulatedDrivetrain(jack, tracker);
		IMiniDrivetrain miniDrivetrain = new EmulatedMiniDrivetrain(jack, tracker);
		
		IHinge hinge = new EmulatedHinge();
		IElevator elevator = new EmulatedElevator(hinge, tracker);
		IGrasper grasper = new EmulatedGrasper(hinge,elevator);
		
		HMCamera camera = null;
		Robot robot = null;
		
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
		
		
		final String CURRENT_DIRECTORY = System.getProperty("user.dir");
        		
		String filename = autoSelected + "_" + startPosition + "_" + gameData.getAssignedPlateAtFirstSwitch()
		+ "_" + gameData.getAssignedPlateAtScale() + "_" + gameData.getAssignedPlateAtSecondSwitch()
		+ ".csv";
		
		String fullPath = CURRENT_DIRECTORY + "\\" + filename; 
		
		System.out.println("\nSaving .csv file as \"" + fullPath + "\"...\n");
		
		//UNCOMMENT THIS LINE TO SAVE HISTORY TO .CSV FILE
		//tracker.saveHistoryAsCsvFile(fullPath);
	}

}
