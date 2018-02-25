package org.usfirst.frc.team2495.robot;

import edu.wpi.first.wpilibj.Joystick;

/**
 * The {@code ControllerBase} class handles all input coming from the 
 * gamepad, left joystick, and right joystick. This has various methods
 * to get input, buttons, etc.
 */
public class ControllerBase {
	private Joystick[] joysticks;
	
	private boolean[][] btn;
	private boolean[][] btnPrev;

	public static final int MAX_NUMBER_CONTROLLERS = 3;
	public static final int MAX_NUMBER_BUTTONS = 11;
		
	private double[] gamepadAxes;
	private double[] gamepadAxesPrev;
	
	public static final int MAX_NUMBER_GAMEPAD_AXES = 7;
	public static final double GAMEPAD_AXIS_PRESSED_AS_BUTTON_THRESHOLD = 0.5;
	
	/**
	 * The {@code GamepadButtons} class contains all the button bindings for the
	 * Gamepad.
	 */
	public static class GamepadButtons {
		public static final int
			A = 1,
			B = 2,
			X = 3,
			Y = 4,
			LB = 5,
			RB = 6,
			BACK = 7,
			START = 8,
			LS = 9,
			RS = 10;
		/**
		 * <pre>
		 * private GamepadButtons()
		 * </pre>
		 * 
		 * Unused constructor.
		 */
		private GamepadButtons() {
			
		}
	}
	
	public static class GamepadAxes {
		public static final int
			LX = 1,
			LY = 2,
			TRIGGER = 3,
			LT = 31, // appears trigger might be shared
			RT = 32,
			RX = 4,
			RY = 5,
			PX = 6,
			PY = 7;
		
		/**
		 * <pre>
		 * private GamepadButtons()
		 * </pre>
		 * 
		 * Unused constructor.
		 */
		private GamepadAxes() {
			
		}
	}
	
	/**
	 * The {@code JoystickButtons} class contains all the button bindings for the
	 * Joysticks.
	 */
	public static class JoystickButtons {
		// Well this defeats the purpose of constants, doesn't it?
		public static final int
			BTN1 = 1,
			BTN2 = 2,
			BTN3 = 3,
			BTN4 = 4,
			BTN5 = 5,
			BTN6 = 6,
			BTN7 = 7,
			BTN8 = 8,
			BTN9 = 9,
			BTN10 = 10,
			BTN11_UNAVAILABLE = 11; // because gamepad has only 10 buttons and that we share an array
		
		/**
		 * <pre>
		 * private JoystickButtons()
		 * </pre>
		 * 
		 * Unused constructor.
		 */
		private JoystickButtons() { }
	}
	
	/**
	 * The {@code Joysticks} enum contains 
	 * namespaces for the gamepad, left joystick, and right joystick
	 */
	public enum Joysticks {
		GAMEPAD,	// 0
		LEFT_STICK,	// 1
		RIGHT_STICK	// 2
	}
	
	/**
	 * <pre>
	 * public ControllerBase(Joystick gamepad,
	 *                       Joystick leftStick,
	 *                       Joystick rightStick)
	 * </pre>
	 * Constructs a new {@code ControllerBase} with the specified {@code Joysticks}
	 * for the gamepad, left joystick, and right joystick.
	 * @param gamepad    the {@code Joystick} to use for the gamepad.
	 * @param leftStick  the {@code Joystick} to use for the left joystick.
	 * @param rightStick the {@code Joystick} to use for the right joystick.
	 */
	public ControllerBase(Joystick gamepad, Joystick leftStick, Joystick rightStick) {		
		btn = new boolean[ControllerBase.MAX_NUMBER_CONTROLLERS][ControllerBase.MAX_NUMBER_BUTTONS/*+1*/];
		btnPrev = new boolean[ControllerBase.MAX_NUMBER_CONTROLLERS][ControllerBase.MAX_NUMBER_BUTTONS/*+1*/];
		
		// CAUTION: joysticks are indexed according to order defined in Joysticks enum
		// Therefore changes in Joysticks enum need to be reflected here...
		joysticks = new Joystick[]{gamepad, leftStick, rightStick};
		
		gamepadAxes = new double[ControllerBase.MAX_NUMBER_GAMEPAD_AXES+1];
		gamepadAxesPrev = new double[ControllerBase.MAX_NUMBER_GAMEPAD_AXES+1];
	}
	
	/**
	 * <pre>
	 * public void update()
	 * </pre>
	 * Updates the {@code btn} and {@code btnPrev} arrays.
	 */
	public void update() {
		//Dealing with buttons on the different joysticks
		for (int i = 0; i < ControllerBase.MAX_NUMBER_CONTROLLERS; i++) {
			for (int j = 1; j < ControllerBase.MAX_NUMBER_BUTTONS/*+1*/; j++) {
				btnPrev[i][j] = btn[i][j];
			}
		}

		for (int i = 0; i < ControllerBase.MAX_NUMBER_CONTROLLERS; i++) {
			for (int j = 1; j < ControllerBase.MAX_NUMBER_BUTTONS/*+1*/; j++) {
				btn[i][j] = joysticks[i].getRawButton(j);
			}
		}
		
		for (int j = 1; j < ControllerBase.MAX_NUMBER_GAMEPAD_AXES+1; j++) {
			gamepadAxesPrev[j] = gamepadAxes[j];
		}
		
		for (int j = 1; j < ControllerBase.MAX_NUMBER_GAMEPAD_AXES+1; j++) {
			gamepadAxes[j] = joysticks[Joysticks.GAMEPAD.ordinal()].getRawAxis(j);
		}
	}	

	/**
	 * <pre>
	 * public boolean getPressedDown(Joysticks contNum, 
	 *                               int buttonNum)
	 * </pre>
	 * Gets whether or not a button from the specified {@code Joystick} is pressed.
	 * @param contNum the {@code Joystick} to check the button from
	 * @param buttonNum the index of the button to test
	 * @return true if the button on the specified {@code Joystick} is pressed,
	 *         false otherwise
	 */
	public boolean getPressedDown(Joysticks contNum, int buttonNum) {
		return btn[contNum.ordinal()][buttonNum] && !btnPrev[contNum.ordinal()][buttonNum]; 
	}	
	
	public boolean getHeld(Joysticks contNum, int buttonNum)
	{
		return btn[contNum.ordinal()][buttonNum];
	}
	
	/**
	 * <pre>
	 * public boolean getReleased(Joysticks contNum, 
	 *                               int buttonNum)
	 * </pre>
	 * Gets whether or not a button from the specified {@code Joystick} was released.
	 * @param contNum the {@code Joystick} to check the button from
	 * @param buttonNum the index of the button to test
	 * @return true if the button on the specified {@code Joystick} was released,
	 *         false otherwise
	 */
	public boolean getReleased(Joysticks contNum, int buttonNum){
		return !btn[contNum.ordinal()][buttonNum] && btnPrev[contNum.ordinal()][buttonNum];
	}
	
	
	public boolean getGamepadTriggerDown(int buttonNum) {
		
		if (buttonNum == GamepadAxes.LT)
		{
			return (Math.max(gamepadAxes[GamepadAxes.TRIGGER],0) > GAMEPAD_AXIS_PRESSED_AS_BUTTON_THRESHOLD) &&
					!(Math.max(gamepadAxesPrev[GamepadAxes.TRIGGER],0) > GAMEPAD_AXIS_PRESSED_AS_BUTTON_THRESHOLD);
		}
		else if (buttonNum == GamepadAxes.RT)
		{
			return (-Math.min(gamepadAxes[GamepadAxes.TRIGGER],0) > GAMEPAD_AXIS_PRESSED_AS_BUTTON_THRESHOLD) &&
					!(-Math.min(gamepadAxesPrev[GamepadAxes.TRIGGER],0) > GAMEPAD_AXIS_PRESSED_AS_BUTTON_THRESHOLD);
		}
		else
		{
			return (gamepadAxes[buttonNum] > GAMEPAD_AXIS_PRESSED_AS_BUTTON_THRESHOLD) &&
				!(gamepadAxesPrev[buttonNum] > GAMEPAD_AXIS_PRESSED_AS_BUTTON_THRESHOLD);
		}
	}	
	
	public boolean getGamepadTriggerHeld(int buttonNum)
	{
		if (buttonNum == GamepadAxes.LT)
		{
			return (Math.max(gamepadAxes[GamepadAxes.TRIGGER],0) > GAMEPAD_AXIS_PRESSED_AS_BUTTON_THRESHOLD);
		}
		else if (buttonNum == GamepadAxes.RT)
		{
			return (-Math.min(gamepadAxes[GamepadAxes.TRIGGER],0) > GAMEPAD_AXIS_PRESSED_AS_BUTTON_THRESHOLD);
		}
		else
		{
			return (gamepadAxes[buttonNum] > GAMEPAD_AXIS_PRESSED_AS_BUTTON_THRESHOLD);
		}
	}
	
	public boolean getGamePadTriggerReleased(int buttonNum){
		
		if (buttonNum == GamepadAxes.LT)
		{
			return !(Math.max(gamepadAxes[GamepadAxes.TRIGGER],0) > GAMEPAD_AXIS_PRESSED_AS_BUTTON_THRESHOLD) &&
					(Math.max(gamepadAxesPrev[GamepadAxes.TRIGGER],0) > GAMEPAD_AXIS_PRESSED_AS_BUTTON_THRESHOLD);
		}
		else if (buttonNum == GamepadAxes.RT)
		{
			return !(-Math.min(gamepadAxes[GamepadAxes.TRIGGER],0) > GAMEPAD_AXIS_PRESSED_AS_BUTTON_THRESHOLD) &&
					(-Math.min(gamepadAxesPrev[GamepadAxes.TRIGGER],0) > GAMEPAD_AXIS_PRESSED_AS_BUTTON_THRESHOLD);
		}
		else
		{
			return !(gamepadAxes[buttonNum] > GAMEPAD_AXIS_PRESSED_AS_BUTTON_THRESHOLD) &&
				(gamepadAxesPrev[buttonNum] > GAMEPAD_AXIS_PRESSED_AS_BUTTON_THRESHOLD);
		}
	}
	
	/**
	 * <pre>
	 * public void rumble(boolean rumble)
	 * </pre>
	 * Rumbles the gamepad
	 * 
	 * @param rumble whether or not to set the rumble on
	 */
	public void rumble(boolean rumble) {
		if (rumble) {
			joysticks[Joysticks.GAMEPAD.ordinal()].setRumble(Joystick.RumbleType.kLeftRumble, 1);
			joysticks[Joysticks.GAMEPAD.ordinal()].setRumble(Joystick.RumbleType.kRightRumble, 1);
		} else {
			joysticks[Joysticks.GAMEPAD.ordinal()].setRumble(Joystick.RumbleType.kLeftRumble, 0);
			joysticks[Joysticks.GAMEPAD.ordinal()].setRumble(Joystick.RumbleType.kRightRumble, 0);
		}		
	}
}