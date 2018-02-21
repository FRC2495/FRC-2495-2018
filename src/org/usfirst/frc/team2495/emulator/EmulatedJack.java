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
				System.out.println("Jack: set position down (MiniDrivetrain)");
				break;
			}
			case UP:
			{
				System.out.println("Jack: set position up (Drivetrain)");
				break;
			}
			default:
			{
				// do nothing
			}
		}
	}

}
