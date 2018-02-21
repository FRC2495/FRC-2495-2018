package org.usfirst.frc.team2495.test;

import org.usfirst.frc.team2495.robot.*;
import org.usfirst.frc.team2495.emulator.*;

public class AutonTester {

	public static void main(String[] args) {
		
		String autoSelected= Robot.kCustomAuto;
		String startPosition = Robot.START_POSITION_LEFT;
		
		EmulatedGameData gameData = new EmulatedGameData();
		gameData.setGameSpecificMessage("LRL");

		IDrivetrain drivetrain = new EmulatedDrivetrain();
		IJack jack = new EmulatedJack();
		IMiniDrivetrain miniDrivetrain = new EmulatedMiniDrivetrain();
		IHinge hinge = new EmulatedHinge();
		IGrasper grasper = new EmulatedGrasper();
		IElevator elevator = new EmulatedElevator();
		
		HMCamera camera = null;
		Robot robot = null;
		
		System.out.println("\nAutonTester started with following settings:\n");
		
		System.out.println("Auto selected: " + autoSelected);	
		System.out.println("Start position: " + startPosition);
		
		System.out.println("First switch: " + gameData.getAssignedPlateAtFirstSwitch());
		System.out.println("Scale: " + gameData.getAssignedPlateAtScale());
		System.out.println("Second switch: " + gameData.getAssignedPlateAtSecondSwitch());

		Auton auton = new Auton(autoSelected, startPosition, gameData, drivetrain, jack, miniDrivetrain, hinge, grasper, elevator, camera, robot);
		
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
