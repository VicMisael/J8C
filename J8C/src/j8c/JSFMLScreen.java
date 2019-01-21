package j8c;

import org.jsfml.graphics.Color;
import org.jsfml.graphics.RectangleShape;
import org.jsfml.graphics.RenderWindow;
import org.jsfml.system.Vector2f;
import org.jsfml.window.VideoMode;
import org.jsfml.window.event.Event;
import org.jsfml.window.event.KeyEvent;

import j8c.Core.CPU;
import j8c.Core.Keyboard;
import j8c.Exceptions.ParametersNotSetException;

public class JSFMLScreen {
	private static JSFMLScreen jsfml = null;
	private RenderWindow window;
	private String GameName = "";
	private byte screen[] = new byte[64 * 32];

	public void setScreen(byte screen[]) {

		this.screen = screen;
	}

	public void setParamJSFML(String GameName) {
		this.GameName = GameName;

	}

	public void start() {

		Thread drawer = new Thread() {
			public void run() {
				window = new RenderWindow(new VideoMode(384, 192), GameName);
				window.setFramerateLimit(30);
				window.clear(Color.BLACK);
				while (window.isOpen()) {

					int y, x = 0;
					for (y = 0; y < 32; y++) {
						for (x = 0; x < 64; x++) {
							byte screenByte = screen[x + y * 64];
							if (screenByte == 0) {

							} else {
								RectangleShape rect = new RectangleShape(new Vector2f(20, 20));
								rect.setPosition(x * 6, y * 6);
								rect.setFillColor(Color.WHITE);
								window.draw(rect);
								// graphics.fillRect(x * 6, y * 6, 6, 6);
								// p.addPoint(x * 6, y * 6);
							}
						}
						window.display();

					}

					for (Event event : window.pollEvents()) {
						if (event.type == Event.Type.KEY_PRESSED) {

							KeyEvent key = event.asKeyEvent();
							System.out.println("They key is " + key.key);
							if (getKeyShort(key) > -1) {
								Keyboard.setKeyPressedAtIndex(getKeyShort(key));
								CPU.getInstance().PressedKeyInterrupt();
							}

						}
						if (event.type == Event.Type.KEY_RELEASED) {
							KeyEvent key = event.asKeyEvent();
							System.out.println("They key is " + key.key);
							if (getKeyShort(key) > -1) {
								Keyboard.setKeyUnpressedAtIndex(getKeyShort(key));
								CPU.getInstance().unpressedKeyInterrupt();
							}
						}
						if (event.type == Event.Type.CLOSED) {
							// MainGUI.stopEmulation();
							window.close();
						}
					}
				}
			}
		};
		drawer.start();
	}

	public static short getKeyShort(KeyEvent key) {
		short keyS = -1;
		switch (key.key.toString()) {
		case "NUMPAD0":
			keyS = 0;
			break;
		case "NUMPAD1":
			keyS = 1;
			break;
		case "NUMPAD2":
			keyS = 2;
			break;
		case "NUMPAD3":
			keyS = 3;
			break;
		case "NUMPAD4":
			keyS = 4;
			break;
		case "NUMPAD5":
			keyS = 5;
			break;
		case "NUMPAD6":
			keyS = 6;
			break;
		case "NUMPAD7":
			keyS = 7;
			break;
		case "NUMPAD8":
			keyS = 8;
			break;
		case "NUMPAD9":
			keyS = 9;
			break;
		case "A":
			keyS = 0xa;
			break;
		case "B":
			keyS = 0xb;
			break;
		case "C":
			keyS = 0xC;
			break;
		case "D":
			keyS = 0xD;
			break;
		case "E":
			keyS = 0xd;
			break;
		case "F":
			keyS = 0xF;
			break;

		}
		return keyS;
	}

	public static JSFMLScreen getInstance() {

		if (jsfml == null) {
			jsfml = new JSFMLScreen();
		}
		return jsfml;
	}

}
