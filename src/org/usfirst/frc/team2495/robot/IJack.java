package org.usfirst.frc.team2495.robot;

import org.usfirst.frc.team2495.robot.Jack.Position;


public interface IJack {
	
	public void setPosition(Position pos);	
	
	public Position getPosition();
}
