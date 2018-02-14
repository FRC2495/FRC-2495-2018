
package org.usfirst.frc.team2495.robot;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;

public class Hinge {

	static final double DESIRED_OUT_PUT = .5;
	static final int TICKS_PER_REVOLUTION = 4096;
	static final double DESIRED_DESTINATION  = 4096 * .25;
	
	WPI_TalonSRX hinge; 
	
	public Hinge (WPI_TalonSRX hinge_in) {
	
		hinge = hinge_in;
		
		hinge.setNeutralMode(NeutralMode.Brake);
		
	}
	public void raise() {
		hinge.set(ControlMode.PercentOutput, DESIRED_OUT_PUT);
		
	}
	
		public void lower() { 
			hinge.set(ControlMode.PercentOutput, DESIRED_OUT_PUT);
		}
	
}
