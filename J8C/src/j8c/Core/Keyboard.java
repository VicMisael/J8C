package j8c.Core;

public class Keyboard {

	private static boolean Keys[] = { false, false, false, false, false, false, false, false, false, false, false, false, false,
			false, false, false };
	private static boolean someKeyIsPressed = false;
	private static int lastPressed;

	private Keyboard() {
	}
	
	public static boolean[] getKeyArray() {
		return Keys;
	}
	
	public static boolean someKeyIsPressed() {
		return someKeyIsPressed;
	}
	public static int getLastPressed() {
		
		return lastPressed;
	}
	
	public static boolean getKeyPressedAtIndex(int index) {

		return Keys[index];
	}

	public static void setKeyPressedAtIndex(int index) {
		someKeyIsPressed = true;
		Keys[index] = true;
		lastPressed = index;
	}

	public static void setKeyUnpressedAtIndex(int index) {
		someKeyIsPressed = false;
		Keys[index] = false;
	}

	public static int getLastPressedKey() {
		if (someKeyIsPressed) {
			return lastPressed;
		} else {
			return -1;
		}

	}
}
