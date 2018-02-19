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

	public Auton(String autoSelected_in, String startPosition_in, GameData gameData_in,
			Drivetrain drivetrain_in, Jack jack_in, MiniDrivetrain miniDrivetrain_in,
			Hinge hinge_in, Grasper grasper_in, Elevator elevator_in) {		
		
		autoSelected = autoSelected_in;
		startPosition = startPosition_in;
		gameData = gameData_in;
		
		drivetrain = drivetrain_in;
		jack = jack_in;
		miniDrivetrain = miniDrivetrain_in;
		
		hinge = hinge_in;
		grasper = grasper_in;
		elevator = elevator_in;
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
					drivetrain.moveDistance(200); //324
					drivetrain.waitMoveDistance();
					drivetrain.turnAngleUsingPidController(+90);//Turn 90 degrees (+)
					drivetrain.waitTurnAngleUsingPidController();
					//Deliver cube at scale 
					drivetrain.turnAngleUsingPidController(+90);//turn (+) 90 degrees
					jack.setPosition(Jack.Position.DOWN);
					if (gameData.getAssignedPlateAtFirstSwitch() == Plate.LEFT)
					{
						miniDrivetrain.moveDistance(0);//Move Left ____ in//Move Left ____ in 
						miniDrivetrain.waitMoveDistance();
					}
					else if (gameData.getAssignedPlateAtFirstSwitch() == Plate.RIGHT)
					{
						
						miniDrivetrain.moveDistance(0);//Move Left ____ in//Move Left ____ in
						miniDrivetrain.waitMoveDistance();
					}
					jack.setPosition(Jack.Position.UP);
					drivetrain.moveDistance(45);
					drivetrain.waitMoveDistance();
					//Pick up cube  
					//Deliver cube 
				} 
				
				else if (gameData.getAssignedPlateAtScale() == Plate.RIGHT)
				{
					drivetrain.moveDistance(196);
					drivetrain.waitMoveDistance();
					drivetrain.turnAngleUsingPidController(180);//Turn 180 degrees (+)
					drivetrain.waitTurnAngleUsingPidController();
					jack.setPosition(Jack.Position.DOWN);
					if (gameData.getAssignedPlateAtFirstSwitch() == Plate.RIGHT)
					{
						miniDrivetrain.moveDistance(0);//Move Left ____ in 
						miniDrivetrain.waitMoveDistance();
					}						
					else if (gameData.getAssignedPlateAtFirstSwitch() == Plate.LEFT)
					{
						miniDrivetrain.moveDistance(0);//Move Left ____ in
						miniDrivetrain.waitMoveDistance();
					}
					jack.setPosition(Jack.Position.UP);
					drivetrain.moveDistance(12);
					drivetrain.waitMoveDistance();
					//Deliver cube 
					drivetrain.moveDistance(0);//move back ___ in.
					drivetrain.waitMoveDistance();
					//Pick up cube  
					//Deliver cube 
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
					drivetrain.moveDistance(324);	// Move forward 324 in
					drivetrain.waitMoveDistance();
					drivetrain.turnAngleUsingPidController(-90);//Turn 90 degrees (-)
					drivetrain.waitTurnAngleUsingPidController();
					//Deliver cube at scale 
					drivetrain.turnAngleUsingPidController(-90);//turn (-) 90 degrees
					jack.setPosition(Jack.Position.DOWN);
					if (gameData.getAssignedPlateAtFirstSwitch() == Plate.RIGHT)
					
					{
						miniDrivetrain.moveDistance(0);//Move Right ____ in 
					}
					else if (gameData.getAssignedPlateAtFirstSwitch() == Plate.LEFT)
					{
						miniDrivetrain.moveDistance(0);//Move Right ____ in 
					}
					jack.setPosition(Jack.Position.UP);
					drivetrain.moveDistance(45); // Move forward 45 in
					drivetrain.waitMoveDistance();
					//Pick up cube  
					//Deliver cube 
				
				}
				else if (gameData.getAssignedPlateAtScale() == Plate.LEFT)
				{
					jack.setPosition(Jack.Position.DOWN);
					// go straight then go right then back get the closest cube and go to the switch 
					if (gameData.getAssignedPlateAtFirstSwitch() == Plate.LEFT)
					{
						miniDrivetrain.moveDistance(0);//Move Left ____ in 
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
					//Pick up cube  
					//Deliver cube 
				}
			}						
			autoSelected = Robot.kDefaultAuto; // we are done so next we do nothing		
			break;
			
		case Robot.kDefaultAuto:
		default:
			// We do nothing
			break;

	}
	
}
