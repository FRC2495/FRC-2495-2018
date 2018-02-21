package org.usfirst.frc.team2495.robot;

import edu.wpi.first.wpilibj.DoubleSolenoid;

// a class to raise the outer/main drivetrain (by lowering the inner/mini drivetrain)
public class Jack implements IJack{
	
	DoubleSolenoid downup;

	public enum Position {
		UP, // outer drivetrain is down
		DOWN; // outer drivetrain is up
	}

	public Jack() {
		// the double solenoid valve will send compressed air from the tank wherever needed
		downup = new DoubleSolenoid(Ports.CAN.PCM, Ports.PCM.JACK_DOWN, Ports.PCM.JACK_UP); // make sure ports are properly sets in Ports.java	
	}
	
	public void setPosition(Position pos)
	{
		switch(pos)
		{
			case DOWN:
			{
				downup.set(DoubleSolenoid.Value.kReverse); // adjust direction if needed
				break;
			}
			case UP:
			{
				downup.set(DoubleSolenoid.Value.kForward); // adjust direction if needed
				break;
			}
			default:
			{
				// do nothing
			}
		}
	}

}
