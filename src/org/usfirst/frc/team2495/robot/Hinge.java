
package org.usfirst.frc.team2495.robot;
import com.ctre.phoenix.motorcontrol.NeutralMode;
//import com.ctre.phoenix.motorcontrol.ControlMode;
//import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class Hinge {

	WPI_TalonSRX hinge; 
	
	public Hinge (WPI_TalonSRX hinge_in) {
	
		hinge = hinge_in;
		
		hinge.setNeutralMode(NeutralMode.Brake);
		
	}
	
}
