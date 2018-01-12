package org.usfirst.frc.team2495.robot;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

public class DriveTrain {
	
	TalonSRX topLeft,botLeft,topRight,botRight;
	
	public DriveTrain(TalonSRX topLeft_in, TalonSRX botLeft_in, TalonSRX topRight_in, TalonSRX botRight_in){
		
		topLeft = topLeft_in;
		topRight = topRight_in;
		botLeft = botLeft_in;
		botRight = botRight_in;
		
	}

}
