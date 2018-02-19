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
			// TODO Put custom auto code here
			if (startPosition == Robot.START_POSITION_LEFT)
			{
				if (gameData.getAssignedPlateAtScale() == Plate.LEFT)
				{
					drivetrain.moveDistance(300); //324
					drivetrain.waitMoveDistance();
					drivetrain.turnAngleUsingPidController(+45);//Turn 90 degrees (+)
					drivetrain.waitTurnAngleUsingPidController();
					grasper.release();
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
					elevator.moveMidway();
					grasper.release();
				} 
				
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
					elevator.isMidway();
					grasper.release();
					elevator.moveDown();
					drivetrain.moveDistance(-10);//move back ___ in.
					drivetrain.waitMoveDistance();
					elevator.moveDown();
					grasper.grasp();
					elevator.moveMidway();
					drivetrain.moveDistance(10);
					drivetrain.waitMoveDistance();
					grasper.release();
					elevator.moveDown();
				}
			}
			else if (startPosition == Robot.START_POSITION_CENTER)
			{
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
				else if (gameData.getAssignedPlateAtFirstSwitch() == Plate.RIGHT)
				{
					drivetrain.moveDistance(140);
					drivetrain.waitMoveDistance();
				}	
			}
			else if (startPosition == Robot.START_POSITION_RIGHT)
			{
				if (gameData.getAssignedPlateAtScale() == Plate.RIGHT)
				{
					drivetrain.moveDistance(300);	// Move forward 324 in
					drivetrain.waitMoveDistance();
					drivetrain.turnAngleUsingPidController(-45);//Turn 90 degrees (-)
					drivetrain.waitTurnAngleUsingPidController();
					elevator.moveUp(); 
					drivetrain.moveDistance(10);
					drivetrain.waitMoveDistance();
					grasper.release(); 
					drivetrain.turnAngleUsingPidController(-135);//turn (-) 90 degrees
					drivetrain.waitTurnAngleUsingPidController();
					elevator.moveDown();
					drivetrain.moveDistance(50);	// Move forward 324 in
					drivetrain.waitMoveDistance();
					jack.setPosition(Jack.Position.DOWN);
					
					if (gameData.getAssignedPlateAtFirstSwitch() == Plate.RIGHT)			
					{
						miniDrivetrain.moveDistance(-30);//Move Right ____ in 
					}
					else if (gameData.getAssignedPlateAtFirstSwitch() == Plate.LEFT)
					{
						miniDrivetrain.moveDistance(-210);//Move Right ____ in 
					}
					jack.setPosition(Jack.Position.UP);
					drivetrain.moveDistance(10); // Move forward 45 in
					drivetrain.waitMoveDistance();
					grasper.grasp();
					elevator.moveMidway();
					drivetrain.moveDistance(10);
					drivetrain.waitMoveDistance();
					grasper.release();
					elevator.moveDown();
				
				}
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
					elevator.moveMidway();
					drivetrain.moveDistance(10);
					grasper.release();
				
				}
			}						
			autoSelected = Robot.kDefaultAuto; // we are done so next we do nothing		
			break;
			
		case Robot.kDefaultAuto:
		default:
			// We do nothing
			break;
		} // end switch
	} // end execute()	
	
	public void end() {
		
	}
	
} // end class
