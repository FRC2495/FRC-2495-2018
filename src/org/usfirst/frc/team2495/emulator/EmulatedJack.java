package org.usfirst.frc.team2495.emulator;

import org.usfirst.frc.team2495.robot.*;
import org.usfirst.frc.team2495.robot.Jack.Position;

import edu.wpi.first.wpilibj.DoubleSolenoid;

// a class to raise the outer/main drivetrain (by lowering the inner/mini drivetrain)
public class EmulatedJack implements IJack{
	
	private DoubleSolenoid.Value value = DoubleSolenoid.Value.kForward;
	
	public EmulatedJack() {
		
	}
	
	public void setPosition(Jack.Position pos)
	{
		switch(pos)
		{
			case DOWN:
			{
				System.out.println("Jack: set position down (Mini Drivetrain activated)");
				value = DoubleSolenoid.Value.kReverse;
				break;
			}
			case UP:
			{
				System.out.println("Jack: set position up (Drivetrain activated)");
				value = DoubleSolenoid.Value.kForward;
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
}
