package org.usfirst.frc.team2495.robot;

import org.usfirst.frc.team2495.robot.GameData.Plate;

public class Auton {
	
	String autoSelected;
	String startPosition;
	IGameData gameData;
	
	IDrivetrain drivetrain;
	IJack jack;
	IMiniDrivetrain miniDrivetrain;
	
	IHinge hinge;
	IGrasper grasper;
	IElevator elevator;
	
	HMCamera camera;
	Robot robot;

	public Auton(String autoSelected_in, String startPosition_in, IGameData gameData_in,
			IDrivetrain drivetrain_in, IJack jack_in, IMiniDrivetrain miniDrivetrain_in,
			IHinge hinge_in, IGrasper grasper_in, IElevator elevator_in,
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
	
	// this method should be called once from autonomousInit() so that we always start in a known state
	public void initialize() {
		jack.setPosition(Jack.Position.UP); // just in case in was not up
		
		if (!hinge.hasBeenHomed() && !Robot.HINGE_DISABLED) { // just in case somebody forgot to home
			hinge.home(); 
			hinge.waitHome();
		}
		
		if (!elevator.hasBeenHomed() && !Robot.ELEVATOR_DISABLED) { // just in case somebody forgot to home
			elevator.home(); 
			elevator.waitHome();	
		}
		
		hinge.moveDown(); // always moves hinge down first
		hinge.waitMove();
	}
	
	// this method should be called from autonomousPeriodic()... hence it will be executed at up to 50 Hz
	public void execute() {		
		switch (autoSelected) {
		case Robot.kCustomAuto:

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
					
					elevator.moveUp();
					elevator.waitMove();
					
					hinge.moveMidway();
					hinge.waitMove();
					
					grasper.release();
					grasper.waitReleaseUsingSonar();
					
					hinge.moveDown();
					hinge.waitMove();
					
					elevator.moveDown();
					elevator.waitMove();
					
					drivetrain.turnAngleUsingPidController(+135);//turn (+) 90 degrees
					drivetrain.waitTurnAngleUsingPidController();
					
					drivetrain.moveDistance(70); //324
					drivetrain.waitMoveDistance();	
					
					jack.setPosition(Jack.Position.DOWN);
					
					if (gameData.getAssignedPlateAtFirstSwitch() == Plate.LEFT)
					{
						miniDrivetrain.moveDistance(-36);//Move Left ____ in//Move Left ____ in 
						miniDrivetrain.waitMoveDistance();
					}
					else if (gameData.getAssignedPlateAtFirstSwitch() == Plate.RIGHT)
					{
					
						miniDrivetrain.moveDistance(-144);//Move Left ____ in//Move Left ____ in
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
					drivetrain.moveDistance(229);
					drivetrain.waitMoveDistance();
					
					drivetrain.turnAngleUsingPidController(180);//Turn 180 degrees (+)
					drivetrain.waitTurnAngleUsingPidController();
					
					jack.setPosition(Jack.Position.DOWN);
				
					if (gameData.getAssignedPlateAtFirstSwitch() == Plate.RIGHT)
					{
						miniDrivetrain.moveDistance(-144);//Move Left ____ in 
						miniDrivetrain.waitMoveDistance();
					}						
					else if (gameData.getAssignedPlateAtFirstSwitch() == Plate.LEFT)
					{
						miniDrivetrain.moveDistance(-36);//Move Left ____ in
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
					
				//	elevator.moveDown();
				//	elevator.waitMove();
					
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
					
					miniDrivetrain.moveDistance(-120); // double check this measurement
					miniDrivetrain.waitMoveDistance();
					
					jack.setPosition(Jack.Position.UP);
					
					drivetrain.moveDistance(70);
					drivetrain.waitMoveDistance();
				
					elevator.moveMidway();
					elevator.waitMove();
					
					grasper.release();
					grasper.tripleCheckReleaseUsingSonar();
					
				}
				// start position center && switch right
				else if (gameData.getAssignedPlateAtFirstSwitch() == Plate.RIGHT)
				{
					drivetrain.moveDistance(140);
					drivetrain.waitMoveDistance();
					
					elevator.moveMidway();
					elevator.waitMove();
					
					grasper.release();
					grasper.tripleCheckReleaseUsingSonar();
							
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
					
					drivetrain.moveDistance(70);	// Move forward 324 in
					drivetrain.waitMoveDistance();
					
					jack.setPosition(Jack.Position.DOWN);
					
					if (gameData.getAssignedPlateAtFirstSwitch() == Plate.RIGHT)			
					{
						miniDrivetrain.moveDistance(36);//Move Right ____ in 
						miniDrivetrain.waitMoveDistance();
					}
					else if (gameData.getAssignedPlateAtFirstSwitch() == Plate.LEFT)
					{
						miniDrivetrain.moveDistance(144);//Move Right ____ in
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
					drivetrain.moveDistance(229);
					drivetrain.waitMoveDistance();
					
					drivetrain.turnAngleUsingPidController(180);
					drivetrain.waitTurnAngleUsingPidController();
					
					jack.setPosition(Jack.Position.DOWN);
					
					if (gameData.getAssignedPlateAtFirstSwitch() == Plate.LEFT)
					{
						miniDrivetrain.moveDistance(36);//Move Left ____ in 
						miniDrivetrain.waitMoveDistance();
					}
					else if (gameData.getAssignedPlateAtFirstSwitch() == Plate.RIGHT)
					{
						miniDrivetrain.moveDistance(144);//Move Left ____ in 
						miniDrivetrain.waitMoveDistance();
					}
					
					jack.setPosition(Jack.Position.DOWN);
					
					//drivetrain.moveDistance(45); // Move forward 45 in
					//drivetrain.waitMoveDistance();
					
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
			
			autoSelected = "we are done"; // this is ok because we have a default case
			break;
			
		default: // aka "we are done"
			// We do nothing (except looping)
			break;
		} // end switch
	} // end execute()	
	
	public void end() {
		
	}
	
} // end class
