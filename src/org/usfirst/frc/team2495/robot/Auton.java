package org.usfirst.frc.team2495.robot;

import org.usfirst.frc.team2495.robot.GameData.Plate;

public class Auton {
	
	String autoSelected;
	String startPosition;
	GameData gameData;
	
	Drivetrain drivetrain;
	Jack jack;
	MiniDrivetrain miniDrivetrain;
	
	Hinge hinge;
	Grasper grasper;
	Elevator elevator;
	
	HMCamera camera;
	Robot robot;

	public Auton(String autoSelected_in, String startPosition_in, GameData gameData_in,
			Drivetrain drivetrain_in, Jack jack_in, MiniDrivetrain miniDrivetrain_in,
			Hinge hinge_in, Grasper grasper_in, Elevator elevator_in,
			HMCamera camera_in, Robot robot_in) {		
		
		autoSelected = autoSelected_in;
		startPosition = startPosition_in;
		gameData = gameData_in;
		
		drivetrain = drivetrain_in;
		jack = jack_in;
		miniDrivetrain = miniDrivetrain_in;
		
		hinge = hinge_in;
		grasper = grasper_in;
		elevator = elevator_in;
		
		camera = camera_in;
		robot = robot_in;
		
	}
	
	public void initialize() {
		
	}
	
	public void execute() {		
		switch (autoSelected) {
		case Robot.kCustomAuto:
			// homing
			jack.setPosition(Jack.Position.UP); // just in case
			
			hinge.fakeHomeWhenDown(); // just in case, no need to wait
			
			elevator.home(); // never an issue if we faked home
			elevator.waitHome();

			// start position left
			if (startPosition == Robot.START_POSITION_LEFT)
			{
				// start position left && scale left
				if (gameData.getAssignedPlateAtScale() == Plate.LEFT)
				{
					drivetrain.moveDistance(300); //324
					drivetrain.waitMoveDistance();
					
					drivetrain.turnAngleUsingPidController(+45);//Turn 90 degrees (+)
					drivetrain.waitTurnAngleUsingPidController();
					
					grasper.release();
					grasper.waitReleaseUsingSonar();
					
					drivetrain.turnAngleUsingPidController(+135);//turn (+) 90 degrees
					drivetrain.waitTurnAngleUsingPidController();
					
					drivetrain.moveDistance(50); //324
					drivetrain.waitMoveDistance();	
					
					jack.setPosition(Jack.Position.DOWN);
					
					if (gameData.getAssignedPlateAtFirstSwitch() == Plate.LEFT)
					{
						miniDrivetrain.moveDistance(30);//Move Left ____ in//Move Left ____ in 
						miniDrivetrain.waitMoveDistance();
					}
					else if (gameData.getAssignedPlateAtFirstSwitch() == Plate.RIGHT)
					{
					
						miniDrivetrain.moveDistance(210);//Move Left ____ in//Move Left ____ in
						miniDrivetrain.waitMoveDistance();
					}
					
					jack.setPosition(Jack.Position.UP);
					
					drivetrain.moveDistance(10);
					drivetrain.waitMoveDistance();
					
					grasper.grasp();
					grasper.waitGraspUsingSonar();
					
					elevator.moveMidway();
					elevator.waitMove();
					
					grasper.release();
					grasper.waitReleaseUsingSonar();
				} 
				// start position left && scale right
				else if (gameData.getAssignedPlateAtScale() == Plate.RIGHT)/// this thing 
				{
					drivetrain.moveDistance(196);
					drivetrain.waitMoveDistance();
					
					drivetrain.turnAngleUsingPidController(180);//Turn 180 degrees (+)
					drivetrain.waitTurnAngleUsingPidController();
					
					jack.setPosition(Jack.Position.DOWN);
				
					if (gameData.getAssignedPlateAtFirstSwitch() == Plate.RIGHT)
					{
						miniDrivetrain.moveDistance(30);//Move Left ____ in 
						miniDrivetrain.waitMoveDistance();
					}						
					else if (gameData.getAssignedPlateAtFirstSwitch() == Plate.LEFT)
					{
						miniDrivetrain.moveDistance(210);//Move Left ____ in
						miniDrivetrain.waitMoveDistance();
					}
					
					jack.setPosition(Jack.Position.UP);
					
					drivetrain.moveDistance(10);
					drivetrain.waitMoveDistance();
					
					elevator.moveMidway();
					elevator.waitMove();
					
					grasper.release();
					grasper.waitReleaseUsingSonar();
					
					elevator.moveDown();
					elevator.waitMove();
					
					drivetrain.moveDistance(-10);//move back ___ in.
					drivetrain.waitMoveDistance();
					
					elevator.moveDown();
					elevator.waitMove();
					
					grasper.grasp();
					grasper.waitGraspUsingSonar();
					
					elevator.moveMidway();
					elevator.waitMove();
					
					drivetrain.moveDistance(10);
					drivetrain.waitMoveDistance();
					
					grasper.release();
					grasper.waitReleaseUsingSonar();
					
					elevator.moveDown();
					elevator.waitMove();
				}
			}
			// start position center
			else if (startPosition == Robot.START_POSITION_CENTER)
			{
				// start position center && switch left
				if (gameData.getAssignedPlateAtFirstSwitch() == Plate.LEFT)
				{
					drivetrain.moveDistance(70);
					drivetrain.waitMoveDistance();
					
					jack.setPosition(Jack.Position.DOWN);
					
					miniDrivetrain.moveDistance(120);
					miniDrivetrain.waitMoveDistance();
					
					jack.setPosition(Jack.Position.UP);
					
					drivetrain.moveDistance(70);
					drivetrain.waitMoveDistance();
					
				}
				// start position center && switch right
				else if (gameData.getAssignedPlateAtFirstSwitch() == Plate.RIGHT)
				{
					drivetrain.moveDistance(140);
					drivetrain.waitMoveDistance();
				}	
			}
			// start position right
			else if (startPosition == Robot.START_POSITION_RIGHT)
			{
				// start position right && scale right
				if (gameData.getAssignedPlateAtScale() == Plate.RIGHT)
				{
					drivetrain.moveDistance(300);	// Move forward 324 in
					drivetrain.waitMoveDistance();
					
					drivetrain.turnAngleUsingPidController(-45);//Turn 90 degrees (-)
					drivetrain.waitTurnAngleUsingPidController();
					
					elevator.moveUp();
					elevator.waitMove();
					
					drivetrain.moveDistance(10);
					drivetrain.waitMoveDistance();
					
					grasper.release();
					grasper.waitReleaseUsingSonar();
					
					drivetrain.turnAngleUsingPidController(-135);//turn (-) 90 degrees
					drivetrain.waitTurnAngleUsingPidController();
					
					elevator.moveDown();
					elevator.waitMove();
					
					drivetrain.moveDistance(50);	// Move forward 324 in
					drivetrain.waitMoveDistance();
					
					jack.setPosition(Jack.Position.DOWN);
					
					if (gameData.getAssignedPlateAtFirstSwitch() == Plate.RIGHT)			
					{
						miniDrivetrain.moveDistance(-30);//Move Right ____ in 
						miniDrivetrain.waitMoveDistance();
					}
					else if (gameData.getAssignedPlateAtFirstSwitch() == Plate.LEFT)
					{
						miniDrivetrain.moveDistance(-210);//Move Right ____ in
						miniDrivetrain.waitMoveDistance();
					}
					
					jack.setPosition(Jack.Position.UP);
					
					drivetrain.moveDistance(10); // Move forward 45 in
					drivetrain.waitMoveDistance();
					
					grasper.grasp();
					grasper.waitGraspUsingSonar();
					
					elevator.moveMidway();
					elevator.waitMove();
					
					drivetrain.moveDistance(10);
					drivetrain.waitMoveDistance();
					
					grasper.release();
					grasper.waitReleaseUsingSonar();
					
					elevator.moveDown();
					elevator.waitMove();
				}
				// start position right && scale left
				else if (gameData.getAssignedPlateAtScale() == Plate.LEFT)
				{
					drivetrain.moveDistance(196);
					drivetrain.waitMoveDistance();
					
					drivetrain.turnAngleUsingPidController(180);
					drivetrain.waitTurnAngleUsingPidController();
					
					jack.setPosition(Jack.Position.DOWN);
					
					if (gameData.getAssignedPlateAtFirstSwitch() == Plate.LEFT)
					{
						miniDrivetrain.moveDistance(30);//Move Left ____ in 
						miniDrivetrain.waitMoveDistance();
					}
					else if (gameData.getAssignedPlateAtFirstSwitch() == Plate.RIGHT)
					{
						miniDrivetrain.moveDistance(0);//Move Left ____ in 
						miniDrivetrain.waitMoveDistance();
					}
					
					jack.setPosition(Jack.Position.DOWN);
					
					drivetrain.moveDistance(45); // Move forward 45 in
					drivetrain.waitMoveDistance();
					
					drivetrain.moveDistance(10);
					drivetrain.waitMoveDistance();
					
					grasper.grasp();
					grasper.waitGraspUsingSonar();
					
					elevator.moveMidway();
					elevator.waitMove();
					
					drivetrain.moveDistance(10);
					drivetrain.waitMoveDistance();
					
					grasper.release();			
					grasper.waitReleaseUsingSonar();
				}
			}						
			autoSelected = "we are done"; // this is ok because we have a default case		
			break;
			
		case Robot.kDefaultAuto:
			// homing only
			jack.setPosition(Jack.Position.UP); // just in case
			
			hinge.fakeHomeWhenDown(); // just in case, no need to wait
			
			elevator.home(); // never an issue if we faked home
			elevator.waitHome();
			
			autoSelected = "we are done"; // this is ok because we have a default case
			break;
			
		default: // aka "we are done"
			// We do nothing
			break;
		} // end switch
	} // end execute()	
	
	public void end() {
		
	}
	
} // end class
