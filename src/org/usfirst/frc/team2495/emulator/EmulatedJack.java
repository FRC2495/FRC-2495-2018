package org.usfirst.frc.team2495.emulator;

import org.usfirst.frc.team2495.robot.*;

// a class to raise the outer/main drivetrain (by lowering the inner/mini drivetrain)
public class EmulatedJack implements IJack{
	
	public EmulatedJack() {
		
	}
	
	public void setPosition(Jack.Position pos)
	{
		switch(pos)
		{
			case DOWN:
			{
				System.out.println("Jack's solenoid set to reverse");
				break;
			}
			case UP:
			{
				System.out.println("Jack's solenoid set to forward");
				break;
			}
			default:
			{
				// do nothing
			}
		}
	}

}
