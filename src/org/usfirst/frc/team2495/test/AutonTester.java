package org.usfirst.frc.team2495.test;

import org.usfirst.frc.team2495.robot.*;
import org.usfirst.frc.team2495.emulator.*;

public class AutonTester {

	public static void main(String[] args) {
		
		String autonSelected = Robot.AUTON_CUSTOM;
		
		// CHANGE THE STARTING POSITION HERE (START_POSITION_LEFT or START_POSITION_CENTER or START_POSITION_RIGHT)
		String startPosition = Robot.START_POSITION_LEFT;
		
		String cameraOption = Robot.CAMERA_OPTION_USE_ALWAYS;
		
		String sonarOption = Robot.SONAR_OPTION_USE_ALWAYS;
		
		EmulatedGameData gameData = new EmulatedGameData();
		
		gameData.setGameSpecificMessage("LLL");
		test(autonSelected, startPosition, cameraOption, sonarOption, gameData);

		gameData.setGameSpecificMessage("LRL");
		test(autonSelected, startPosition, cameraOption, sonarOption, gameData);

		gameData.setGameSpecificMessage("RRR");
		test(autonSelected, startPosition, cameraOption, sonarOption, gameData);
		
		gameData.setGameSpecificMessage("RLR");
		test(autonSelected, startPosition, cameraOption, sonarOption, gameData);

		startPosition = Robot.START_POSITION_RIGHT;

		gameData.setGameSpecificMessage("LLL");
		test(autonSelected, startPosition, cameraOption, sonarOption, gameData);

		gameData.setGameSpecificMessage("LRL");
		test(autonSelected, startPosition, cameraOption, sonarOption, gameData);

		gameData.setGameSpecificMessage("RRR");
		test(autonSelected, startPosition, cameraOption, sonarOption, gameData);
		
		gameData.setGameSpecificMessage("RLR");
		test(autonSelected, startPosition, cameraOption, sonarOption, gameData);

		startPosition = Robot.START_POSITION_CENTER;

		gameData.setGameSpecificMessage("LLL");
		test(autonSelected, startPosition, cameraOption, sonarOption, gameData);

		gameData.setGameSpecificMessage("LRL");
		test(autonSelected, startPosition, cameraOption, sonarOption, gameData);

		gameData.setGameSpecificMessage("RRR");
		test(autonSelected, startPosition, cameraOption, sonarOption, gameData);
		
		gameData.setGameSpecificMessage("RLR");
		test(autonSelected, startPosition, cameraOption, sonarOption, gameData);

}
		
	public static void test(String autoSelected, String startPosition, String cameraOption, String sonarOption, EmulatedGameData gameData) {	

		PositionTracker tracker = new PositionTracker();
		
		IHMCamera camera = new EmulatedHMCamera("GRIP/myContoursReport");
		
		IJack jack = new EmulatedJack();
		IDrivetrain drivetrain = new EmulatedDrivetrain(jack, tracker);
		IMiniDrivetrain miniDrivetrain = new EmulatedMiniDrivetrain(jack, tracker);
		
		IHinge hinge = new EmulatedHinge();
		IElevator elevator = new EmulatedElevator(hinge, tracker);
		IGrasper grasper = new EmulatedGrasper(hinge,elevator);
		
		Robot robot = null;
		
		System.out.println("\nAutonTester started with following settings:\n");
		
		System.out.println("Auto selected: " + autoSelected);	
		System.out.println("Start position: " + startPosition);
		System.out.println("Camera option: " + cameraOption);
		System.out.println("Sonar option: " + sonarOption);
		
		System.out.println("First switch: " + gameData.getAssignedPlateAtFirstSwitch());
		System.out.println("Scale: " + gameData.getAssignedPlateAtScale());
		System.out.println("Second switch: " + gameData.getAssignedPlateAtSecondSwitch());

		Auton auton = new Auton(autoSelected, startPosition, cameraOption, sonarOption, gameData, drivetrain, jack, miniDrivetrain, hinge, grasper, elevator, camera, robot, tracker);
		
		System.out.println("\nAuton initalizing\n");
		
		auton.initialize();
		
		System.out.println("\nAuton executing\n");
		
		// todo put in a loop
		auton.execute();
		
		System.out.println("\nAuton end\n");
		
		auton.end();
		
		System.out.println("\nAutonTester finished\n");
		
		
		final String CURRENT_DIRECTORY = System.getProperty("user.dir");
        		
		String filename = autoSelected + "_" + startPosition + "_"
		+ cameraOption + "_" +  sonarOption + "_" + gameData.getAssignedPlateAtFirstSwitch()
		+ "_" + gameData.getAssignedPlateAtScale() + "_" + gameData.getAssignedPlateAtSecondSwitch()
		+ ".csv";
		
		String fullPath = CURRENT_DIRECTORY + "\\" + filename; 
		
		System.out.println("\nSaving .csv file as \"" + fullPath + "\"...\n");
		
		//UNCOMMENT THIS LINE TO SAVE HISTORY TO .CSV FILE
		//tracker.saveHistoryAsCsvFile(fullPath);
	}

}
