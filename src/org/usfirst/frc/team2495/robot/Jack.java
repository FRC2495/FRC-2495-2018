package org.usfirst.frc.team2495.robot;

import java.util.Calendar;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DriverStation;

// a class to raise the outer/main drivetrain (by lowering the inner/mini drivetrain)
public class Jack implements IJack{
	
	static final int WAIT_MS = 1000;
	
	DoubleSolenoid downup;

	public enum Position {
		UP, // outer drivetrain is down
		DOWN, // outer drivetrain is up
		UNKNOWN;
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

	public Position getPosition()
	{
		DoubleSolenoid.Value value = downup.get();
		
		switch(value)
		{
			case kReverse:
			{
				return Position.DOWN;
			}
			case kForward:
			{
				return Position.UP;
			}
			default:
			{
				return Position.UNKNOWN;
			}
		}
	}
	
	public void waitSetPosition() {
		long start = Calendar.getInstance().getTimeInMillis();

		while (true) { 		
			if (!DriverStation.getInstance().isAutonomous()
					|| Calendar.getInstance().getTimeInMillis() - start >= WAIT_MS) {
				System.out.println("Wait is over");
				break;
			}

			try {
				Thread.sleep(20); // sleeps a little
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			//robot.updateToSmartDash();
		}
	}
}
