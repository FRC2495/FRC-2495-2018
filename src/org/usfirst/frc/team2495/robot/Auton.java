package org.usfirst.frc.team2495.robot;

import org.usfirst.frc.team2495.robot.GameData.Plate;

public class Auton {
	
	static final int ROBOT_DEPTH_INCHES = 34; // inches
	static final int HALF_ROBOT_DEPTH_INCHES = ROBOT_DEPTH_INCHES / 2;
	static final int EXTRA_HINGE_DEPTH_INCHES = 14; // inches
	static final int ALLIANCE_STATION_TO_SCALE = 295-ROBOT_DEPTH_INCHES-EXTRA_HINGE_DEPTH_INCHES;
	static final int ALLIANCE_STATION_SCALE_ADJUST = 6; 
	static final int ALLIANCE_STATION_TO_SWITCH = 140-ROBOT_DEPTH_INCHES-EXTRA_HINGE_DEPTH_INCHES-1; //adjusting 1 inches since movedistance didnt complete
	static final int SCALE_TO_SWITCH_1 = 40;
	static final int SCALE_TO_SWITCH_2 = 4;
	static final int SLIDE_TO_NEAR_SWITCH = 34+20; //adjustment as the testbot is off when it reaches scale.
	static final int SLIDE_TO_FAR_SWITCH = 175;
	static final int DISTANCE_BETWEEN_SWITCH_CENTERS = 115;
	
	String autoSelected;
	String startPosition;
	String cameraOption;
	String sonarOption;
	
	IGameData gameData;
	
	IDrivetrain drivetrain;
	IJack jack;
	IMiniDrivetrain miniDrivetrain;
	
	IHinge hinge;
	IGrasper grasper;
	IElevator elevator;
	
	IHMCamera camera;
	Robot robot;
	
	PositionTracker tracker;

	public Auton(String autoSelected_in, String startPosition_in, String cameraOption_in,
			String sonarOption_in, IGameData gameData_in,
			IDrivetrain drivetrain_in, IJack jack_in, IMiniDrivetrain miniDrivetrain_in,
			IHinge hinge_in, IGrasper grasper_in, IElevator elevator_in,
			IHMCamera camera_in, Robot robot_in, PositionTracker tracker_in) {		
		
		autoSelected = autoSelected_in;
		startPosition = startPosition_in;
		cameraOption = cameraOption_in;
		sonarOption = sonarOption_in;
		
		gameData = gameData_in;
		
		drivetrain = drivetrain_in;
		jack = jack_in;
		miniDrivetrain = miniDrivetrain_in;
		
		hinge = hinge_in;
		grasper = grasper_in;
		elevator = elevator_in;
		
		camera = camera_in;
		robot = robot_in;
		
		tracker = tracker_in;
	}
	
	// begin camera support
	// The code below is a copy of code that was already in Robot.java
	// It is copied here to make it easier to run AutonTester with emulated devices (because Robot is set to only use real devices).
	// NOTE THAT BEFORE USING ANY OF THE METHOD HEREUNDER MAKE SURE YOU CALL camera.acquireTargets() TO FORCE THE CAMERA TO REFRESH ITS INFORMATION.
	// AND BECAUSE OF THE LAG IT IS SUGGSESTED TO USE camera.acquireTargets(true)
	// The two methods implemented so far assume that we want to use the large drivetrain since the camera is only useful with the large drivetrain.
	
	// In Auton this method needs to be paired with waitTurnAngleUsingPidController().
	// Regardless this method assumes that the camera is at the center of rotation, which is not true.
	// But since we cannot properly estimate the distance to the target as the target size is not fixed we cannot correct this.
	// In practice this should not be a big issue.
	public void turnAngleUsingPidControllerTowardCube() {
		drivetrain.turnAngleUsingPidController(camera.getAngleToTurnToTarget());
		/*drivetrain.turnAngleUsingPidController(calculateProperTurnAngle(
				camera.getAngleToTurnToTarget(),camera.getDistanceToTargetUsingHorizontalFov()));*/
	}
	
	// In Auton this method needs to be paired with waitMoveDistance()
	public void moveDistanceTowardCube() {
		final int OFFSET_CAMERA_CUBE_INCHES = 10; // we need to leave some space between the camera and the target
		//final int MAX_DISTANCE_TO_CUBE_INCHES = 120; // arbitrary very large distance
		final int MAX_DISTANCE_TO_CUBE_INCHES = 24; // IN THIS AUTON WE NEVER NEED TO MOVE MORE THAN JUST A FEW INCHES
		// SO WE ARBITRARILY USE 24 INCHES (2 FEET) AS THE SAFE UPPER LIMIT 
	
		double distanceToTargetReportedByCamera = camera.getDistanceToTargetUsingHorizontalFov();
		
		if (distanceToTargetReportedByCamera <= MAX_DISTANCE_TO_CUBE_INCHES) {
			if (distanceToTargetReportedByCamera >= OFFSET_CAMERA_CUBE_INCHES) {
				drivetrain.moveDistance((distanceToTargetReportedByCamera - OFFSET_CAMERA_CUBE_INCHES)); // todo: check sign
			} else {
				System.out.println("WARNING: Already at the cube!");
			}
		} else {
			System.out.println("ERROR: Cannot move to infinity and beyond!");
		}		
	}

	/*private double calculateProperTurnAngle(double cameraTurnAngle, double cameraHorizontalDist) {
		try {
			final double OFFSET_BETWEEN_CAMERA_AND_ROTATION_CENTER_INCHES = 6; // inches - might need adjustment
			double dist = cameraHorizontalDist * Math.cos(Math.toRadians(cameraTurnAngle));
			return Math.toDegrees(Math.atan(Math.tan(Math.toRadians(cameraTurnAngle)) * dist
					/ (dist + OFFSET_BETWEEN_CAMERA_AND_ROTATION_CENTER_INCHES)));
		} catch (Exception e) {
			System.out.println("Exception in proper turn angle calculation" + e.toString());
			return 0;
		}
	}*/
	// end camera support

	// this method should be called once from autonomousInit() so that we always start in a known state
	public void initialize() {
		jack.setPosition(Jack.Position.LARGE_DRIVETRAIN); // just in case in was not up
		// no need to wait because we should already be up
		
		if (!hinge.hasBeenHomed() && !Robot.HINGE_DISABLED) { // just in case somebody forgot to home
			hinge.home(); 
			hinge.waitHome();
		}
		
		hinge.moveDown(); // always moves hinge down first
		hinge.waitMove();
		
		if (!elevator.hasBeenHomed() && !Robot.ELEVATOR_DISABLED) { // just in case somebody forgot to home
			elevator.home(); 
			elevator.waitHome();	
		}
	}
	
	public void align_and_move_to_cube() {
		//If dashboard option to use camera during Auton is selected i.e. we trust camera alignment
		if (cameraOption == Robot.CAMERA_OPTION_USE_ALWAYS  || cameraOption == Robot.CAMERA_OPTION_USE_OPEN_LOOP_ONLY || cameraOption == Robot.CAMERA_OPTION_USE_CLOSED_LOOP_ONLY)
		{	
			if (cameraOption == Robot.CAMERA_OPTION_USE_ALWAYS || cameraOption == Robot.CAMERA_OPTION_USE_CLOSED_LOOP_ONLY)
			{
				// we need to make sure we are down before we move athwart
				if (jack.getPosition() != Jack.Position.MINI_DRIVETRAIN) {
					jack.setPosition(Jack.Position.MINI_DRIVETRAIN);
					jack.waitSetPosition();
				}
			
				miniDrivetrain.moveUsingCameraPidController();
				miniDrivetrain.waitMoveUsingCameraPidController();
			
				jack.setPosition(Jack.Position.LARGE_DRIVETRAIN);
				jack.waitSetPosition();
			}
			else //if (cameraOption == Robot.CAMERA_OPTION_USE_OPEN_LOOP_ONLY)
			{
				if (jack.getPosition() != Jack.Position.LARGE_DRIVETRAIN) {
					jack.setPosition(Jack.Position.LARGE_DRIVETRAIN);
					jack.waitSetPosition();
				}
				
				camera.acquireTargets(true);
			
				this.turnAngleUsingPidControllerTowardCube();
				drivetrain.waitTurnAngleUsingPidController();
			}

			if (cameraOption == Robot.CAMERA_OPTION_USE_ALWAYS || cameraOption == Robot.CAMERA_OPTION_USE_OPEN_LOOP_ONLY) {
				if (jack.getPosition() != Jack.Position.LARGE_DRIVETRAIN) {
					jack.setPosition(Jack.Position.LARGE_DRIVETRAIN);
					jack.waitSetPosition();
				}
				 
				camera.acquireTargets(true);
			
				this.moveDistanceTowardCube();
				drivetrain.waitMoveDistance();
			}
			else //if (cameraOption == Robot.CAMERA_OPTION_USE_CLOSED_LOOP_ONLY)
			{
				// we need to make sure we are up before we move
				if (jack.getPosition() != Jack.Position.LARGE_DRIVETRAIN) {
					jack.setPosition(Jack.Position.LARGE_DRIVETRAIN);
					jack.waitSetPosition();
				}
				
				drivetrain.moveDistance(SCALE_TO_SWITCH_2);
				drivetrain.waitMoveDistance();
			}
		}
		else //if (cameraOption == Robot.CAMERA_OPTION_USE_NEVER)
		{
			// we need to make sure we are up before we move
			if (jack.getPosition() != Jack.Position.LARGE_DRIVETRAIN) {
				jack.setPosition(Jack.Position.LARGE_DRIVETRAIN);
				jack.waitSetPosition();
			}
			
			drivetrain.moveDistance(SCALE_TO_SWITCH_2);
			drivetrain.waitMoveDistance();
		}
	}
	
	public void grasp_cube() {
		if (sonarOption == Robot.SONAR_OPTION_USE_ALWAYS || sonarOption == Robot.SONAR_OPTION_USE_GRASP_ONLY)
		{
			grasper.grasp();
			grasper.waitGraspUsingSonar();
		}
		else
		{
			grasper.grasp();
			grasper.waitGraspOrRelease();
		}
	}
	
	public void release_cube() {
		if (sonarOption == Robot.SONAR_OPTION_USE_ALWAYS || sonarOption == Robot.SONAR_OPTION_USE_RELEASE_ONLY)
		{
			grasper.release();
			grasper.waitReleaseUsingSonar();
		}
		else
		{
			grasper.release();
			grasper.waitGraspOrRelease();
		}
	}
	
	// this method should be called from autonomousPeriodic()... hence it will be executed at up to 50 Hz
	public void execute() {		
		switch (autoSelected) {
		case Robot.AUTON_CUSTOM:

			// start position left
			if (startPosition == Robot.START_POSITION_LEFT)
			{
				// start position left && scale left
				if (gameData.getAssignedPlateAtScale() == Plate.LEFT)
				{
					drivetrain.moveDistance(ALLIANCE_STATION_TO_SCALE); 
					drivetrain.waitMoveDistance();
					
					drivetrain.turnAngleUsingPidController(+45);
					drivetrain.waitTurnAngleUsingPidController();
					
					elevator.moveUp();
					elevator.waitMove();
					
					hinge.moveMidway();
					hinge.waitMove();
					
					drivetrain.moveDistance(ALLIANCE_STATION_SCALE_ADJUST); 
					drivetrain.waitMoveDistance();
					
					release_cube();
					
					drivetrain.moveDistance(-ALLIANCE_STATION_SCALE_ADJUST); 
					drivetrain.waitMoveDistance();
					
					hinge.moveDown();
					hinge.waitMove();
					
					elevator.moveDown();
					elevator.waitMove();
					
					drivetrain.turnAngleUsingPidController(+135);
					drivetrain.waitTurnAngleUsingPidController();
					
					drivetrain.moveDistance(SCALE_TO_SWITCH_1);
					drivetrain.waitMoveDistance();	
					
					jack.setPosition(Jack.Position.MINI_DRIVETRAIN);
					jack.waitSetPosition();
					
					if (gameData.getAssignedPlateAtFirstSwitch() == Plate.LEFT)
					{
						miniDrivetrain.moveDistance(-SLIDE_TO_NEAR_SWITCH); 
						miniDrivetrain.waitMoveDistance();
					}
					else if (gameData.getAssignedPlateAtFirstSwitch() == Plate.RIGHT)
					{				
						miniDrivetrain.moveDistance(-SLIDE_TO_FAR_SWITCH);
						miniDrivetrain.waitMoveDistance();
					}

					align_and_move_to_cube();
										
					grasp_cube();
					
					elevator.moveMidway();
					elevator.waitMove();
					
					release_cube();

					elevator.moveDown();
					elevator.waitMove();
				} 
				// start position left && scale right
				else if (gameData.getAssignedPlateAtScale() == Plate.RIGHT) 
				{
					drivetrain.moveDistance(ALLIANCE_STATION_TO_SCALE-SCALE_TO_SWITCH_1);
					drivetrain.waitMoveDistance();
					
					drivetrain.turnAngleUsingPidController(180);
					drivetrain.waitTurnAngleUsingPidController();
					
					jack.setPosition(Jack.Position.MINI_DRIVETRAIN);
					jack.waitSetPosition();
				
					if (gameData.getAssignedPlateAtFirstSwitch() == Plate.RIGHT)
					{
						miniDrivetrain.moveDistance(-SLIDE_TO_FAR_SWITCH);//Move far Left 
						miniDrivetrain.waitMoveDistance();
					}						
					else if (gameData.getAssignedPlateAtFirstSwitch() == Plate.LEFT)
					{
						miniDrivetrain.moveDistance(-SLIDE_TO_NEAR_SWITCH);//Move near left
						miniDrivetrain.waitMoveDistance();
					}
					
					jack.setPosition(Jack.Position.LARGE_DRIVETRAIN);
					jack.waitSetPosition();

					elevator.moveMidway();
					elevator.waitMove();
					
					//Remember you will be hitting a cube and not the switch perimeter,  So adjust accordingly
					drivetrain.moveDistance(SCALE_TO_SWITCH_2);
					drivetrain.waitMoveDistance();
					
					
					release_cube();

					drivetrain.moveDistance(-SCALE_TO_SWITCH_2);//move back ___ in.
					drivetrain.waitMoveDistance();

					elevator.moveDown();
					elevator.waitMove();
					
					align_and_move_to_cube();

					grasp_cube();
					
					elevator.moveMidway();
					elevator.waitMove();
					
					release_cube();
					
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
					drivetrain.moveDistance(ALLIANCE_STATION_TO_SWITCH/2); //changed the distance so that when we move forward its based off the center of the robot.
					drivetrain.waitMoveDistance();

					jack.setPosition(Jack.Position.MINI_DRIVETRAIN);
					jack.waitSetPosition();
					
					miniDrivetrain.moveDistance(-DISTANCE_BETWEEN_SWITCH_CENTERS); //changed the distance so that when we move left its based off the center of the robot.
					miniDrivetrain.waitMoveDistance();

					jack.setPosition(Jack.Position.LARGE_DRIVETRAIN);
					jack.waitSetPosition();

					elevator.moveMidway();
					drivetrain.moveDistance(ALLIANCE_STATION_TO_SWITCH/2); //changed the distance so that when we move forward its based off the center of the robot.					
					
					//drivetrain.waitMoveDistance();				
					drivetrain.waitMoveDistanceOrStalled(); // TODO TEST THAT IT WORKS
					elevator.waitMove();
					
					release_cube();

					elevator.moveDown();
					elevator.waitMove();				
				}
				// start position center && switch right
				else if (gameData.getAssignedPlateAtFirstSwitch() == Plate.RIGHT)
				{
					elevator.moveMidway();
					drivetrain.moveDistance(ALLIANCE_STATION_TO_SWITCH); //changed the distance so that when we move forward its based off the center of the robot.
					
					//drivetrain.waitMoveDistance();
					drivetrain.waitMoveDistanceOrStalled(); // TODO TEST THAT IT WORKS
					elevator.waitMove();
					
					release_cube();
							
					elevator.moveDown();
					elevator.waitMove();
				}	
				
			}
			// start position right
			else if (startPosition == Robot.START_POSITION_RIGHT)
			{
				// start position right && scale right
				if (gameData.getAssignedPlateAtScale() == Plate.RIGHT)
				{
					drivetrain.moveDistance(ALLIANCE_STATION_TO_SCALE);	// Move forward 324 in
					drivetrain.waitMoveDistance();
					
					drivetrain.turnAngleUsingPidController(-45);//Turn 90 degrees (-)
					drivetrain.waitTurnAngleUsingPidController();
					
					elevator.moveUp();
					elevator.waitMove();
					
					hinge.moveMidway();
					hinge.waitMove();
					
					drivetrain.moveDistance(ALLIANCE_STATION_SCALE_ADJUST); 
					drivetrain.waitMoveDistance();
					
					release_cube();
					
					drivetrain.moveDistance(-ALLIANCE_STATION_SCALE_ADJUST); 
					drivetrain.waitMoveDistance();
					
					hinge.moveDown();
					hinge.waitMove();
					
					elevator.moveDown();
					elevator.waitMove();
					
					drivetrain.turnAngleUsingPidController(-135);
					drivetrain.waitTurnAngleUsingPidController();
					
					drivetrain.moveDistance(SCALE_TO_SWITCH_1);	
					drivetrain.waitMoveDistance();
					
					jack.setPosition(Jack.Position.MINI_DRIVETRAIN);
					jack.waitSetPosition();
					
					if (gameData.getAssignedPlateAtFirstSwitch() == Plate.RIGHT)			
					{
						miniDrivetrain.moveDistance(SLIDE_TO_NEAR_SWITCH);//Move Right ____ in 
						miniDrivetrain.waitMoveDistance();
					}
					else if (gameData.getAssignedPlateAtFirstSwitch() == Plate.LEFT)
					{
						miniDrivetrain.moveDistance(SLIDE_TO_FAR_SWITCH);//Move Right ____ in
						miniDrivetrain.waitMoveDistance();
					}

					align_and_move_to_cube();

					grasp_cube();
					
					elevator.moveMidway();
					elevator.waitMove();
					
					release_cube();
					
					elevator.moveDown();
					elevator.waitMove();
				}
				// start position right && scale left
				else if (gameData.getAssignedPlateAtScale() == Plate.LEFT)
				{
					drivetrain.moveDistance(ALLIANCE_STATION_TO_SCALE-SCALE_TO_SWITCH_1);
					drivetrain.waitMoveDistance();
					
					drivetrain.turnAngleUsingPidController(180);
					drivetrain.waitTurnAngleUsingPidController();
					
					jack.setPosition(Jack.Position.MINI_DRIVETRAIN);
					jack.waitSetPosition();
					
					if (gameData.getAssignedPlateAtFirstSwitch() == Plate.LEFT)
					{
						miniDrivetrain.moveDistance(SLIDE_TO_FAR_SWITCH);//Move Left ____ in 
						miniDrivetrain.waitMoveDistance();
					}
					else if (gameData.getAssignedPlateAtFirstSwitch() == Plate.RIGHT)
					{
						miniDrivetrain.moveDistance(SLIDE_TO_NEAR_SWITCH);//Move Left ____ in 
						miniDrivetrain.waitMoveDistance();
					}
					
					jack.setPosition(Jack.Position.LARGE_DRIVETRAIN);
					jack.waitSetPosition();
					
					elevator.moveMidway();
					elevator.waitMove();
					
					//Remember you will be hitting a cube and not the switch perimeter,  So adjust accordingly
					drivetrain.moveDistance(SCALE_TO_SWITCH_2);
					drivetrain.waitMoveDistance();
					
					release_cube();

					drivetrain.moveDistance(-SCALE_TO_SWITCH_2);//move back ___ in.
					drivetrain.waitMoveDistance();

					elevator.moveDown();
					elevator.waitMove();
										
					align_and_move_to_cube();

					grasp_cube();
					
					elevator.moveMidway();
					elevator.waitMove();
					
					release_cube();
					
					elevator.moveDown();
					elevator.waitMove();
					
				}
			}						
			autoSelected = "we are done"; // this is ok because we have a default case		
			break;
			
		case Robot.AUTON_DO_NOTHING:
			
			autoSelected = "we are done"; // this is ok because we have a default case
			break;
			
		default: // aka "we are done"
			// We do nothing (except looping)
			break;
		} // end switch
	} // end execute()	
	
	public void end() {
		
	}
	
} // end clas