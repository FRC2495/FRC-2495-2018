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
			case MINI_DRIVETRAIN:
			{
				System.out.println("Jack: BEGIN set position down (Mini Drivetrain activated)");
				value = DoubleSolenoid.Value.kReverse;
				System.out.println("Jack: STATE down\n");;
				break;
			}
			case LARGE_DRIVETRAIN:
			{
				System.out.println("Jack: BEGIN set position up (Drivetrain activated)");
				value = DoubleSolenoid.Value.kForward;
				System.out.println("Jack: STATE up\n");;
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
				return Position.MINI_DRIVETRAIN;
			}
			case kForward:
			{
				return Position.LARGE_DRIVETRAIN;
			}
			default:
			{
				return Position.UNKNOWN;
			}
		}
	}
	
	public void waitSetPosition() {
		System.out.println("Jack: END set position up or down");
		
		System.out.println("Jack: STATE up or down\n");
	}
}
