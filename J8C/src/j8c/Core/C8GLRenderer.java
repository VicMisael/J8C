package j8c.Core;

import org.lwjgl.*;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.*;
import org.lwjgl.system.*;

//import java.awt.image.BufferedImage;
import java.nio.*;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.*;
import static org.lwjgl.system.MemoryUtil.*;

public class C8GLRenderer {

	private long window;
	private static C8GLRenderer c8gl;
	private boolean glInit = false;

	private C8GLRenderer() {

	}

	public void start() {
		System.out.println("Hello LWJGL " + Version.getVersion() + "!");

		init();
		loop();

		// Free the window callbacks and destroy the window
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
	}

	// Boilerplate from LWJGL
	public void init() {
		// Setup an error callback. The default implementation
		// will print the error message in System.err.
		GLFWErrorCallback.createPrint(System.err).set();

		// Initialize GLFW. Most GLFW functions will not work before doing this.
		if (!glfwInit())
			throw new IllegalStateException("Unable to initialize GLFW");

		// Configure GLFW
		glfwDefaultWindowHints(); // optional, the current window hints are already the default
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, GLFW_FALSE); // the window will be resizable

		// Create the window
		window = glfwCreateWindow(640, 320, "Hello World!", NULL, NULL);
		if (window == NULL)
			throw new RuntimeException("Failed to create the GLFW window");

		// HashMap for the keys
		Map<Integer, Integer> map;
		map = new HashMap<>();
		map.put(GLFW_KEY_A, 0xA);
		map.put(GLFW_KEY_B, 0xB);
		map.put(GLFW_KEY_C, 0xC);
		map.put(GLFW_KEY_D, 0xD);
		map.put(GLFW_KEY_E, 0xE);
		map.put(GLFW_KEY_F, 0xF);
		map.put(GLFW_KEY_0, 0);
		map.put(GLFW_KEY_1, 1);
		map.put(GLFW_KEY_2, 2);
		map.put(GLFW_KEY_3, 3);
		map.put(GLFW_KEY_4, 4);
		map.put(GLFW_KEY_5, 5);
		map.put(GLFW_KEY_6, 6);
		map.put(GLFW_KEY_7, 7);
		map.put(GLFW_KEY_8, 8);
		map.put(GLFW_KEY_9, 9);
		//Keypad numbers 
		map.put(GLFW_KEY_KP_1, 1);
		map.put(GLFW_KEY_KP_2, 2);
		map.put(GLFW_KEY_KP_3, 3);
		map.put(GLFW_KEY_KP_4, 4);
		map.put(GLFW_KEY_KP_5, 5);
		map.put(GLFW_KEY_KP_6, 6);
		map.put(GLFW_KEY_KP_7, 7);
		map.put(GLFW_KEY_KP_8, 8);
		map.put(GLFW_KEY_KP_9, 9);
		map.put(GLFW_KEY_KP_0, 0);
		
		glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
			if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
				glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
			if (action == GLFW_PRESS) {
				if(map.containsKey(key))
					Keyboard.setKeyPressedAtIndex(map.get(key));
			}
			if (action == GLFW_RELEASE) {
				if(map.containsKey(key))
					Keyboard.setKeyUnpressedAtIndex(map.get(key));
			}
		});

		// Get the thread stack and push a new frame
		try (MemoryStack stack = stackPush()) {
			IntBuffer pWidth = stack.mallocInt(1); // int*
			IntBuffer pHeight = stack.mallocInt(1); // int*

			// Get the window size passed to glfwCreateWindow
			glfwGetWindowSize(window, pWidth, pHeight);

			// Get the resolution of the primary monitor
			GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

			// Center the window
			glfwSetWindowPos(window, (vidmode.width() - pWidth.get(0)) / 2, (vidmode.height() - pHeight.get(0)) / 2);
		} // the stack frame is popped automatically

		// Make the OpenGL context current
		glfwMakeContextCurrent(window);
		// Enable v-sync
		glfwSwapInterval(0);

		// Make the window visible
		glfwShowWindow(window);
		// Set the clear color
		GL.createCapabilities();
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		// glOrtho(0f, 128, 64, 0f, -1f, 1f);
		glInit = true;

	}

	static int modeC = -1;

	public void changeOrtho(int mode) {

		if (mode == 0) {
			if (modeC != mode) {
				glLoadIdentity();
				glOrtho(0f, 64, 32, 0f, -1f, 1f);
				modeC = mode;
			}
		} else if (mode == 1) {
			if (modeC != mode) {
				glLoadIdentity();
				glOrtho(0f, 128, 64, 0f, -1f, 1f);
				modeC = mode;
			}
		}
	}

	public void draw(byte screen[], int mode) {
		changeOrtho(mode);
		// int value;
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		// int x1 = 0, y1 = 0;
		if (mode == 0) {
			for (int y = 0; y < 32; y++) {
				for (int x = 0; x < 64; x++) {
					if (screen[x + y * 64] == 1) {

						glColor3f(1, 1, 1);
						glRectf(x, y, x + 1, y + 1);

					}
				}
			}
		} else if (mode == 1) {

			for (int y = 0; y < 64; y++) {

				for (int x = 0; x < 128; x++) {
					// Its faster this way, maybe because of branch prediction?
					if (screen[x + y * 128] == 1) {
						glColor3f(1, 1, 1);
						glRectf(x, y, x + 1, y + 1);
					}

				}

			}

		}
		glfwSwapBuffers(window);
		glfwPollEvents();
	}

	private void loop() {
		// This line is critical for LWJGL's interoperation with GLFW's
		// OpenGL context, or any context that is managed externally.
		// LWJGL detects the context that is current in the current thread,
		// creates the GLCapabilities instance and makes the OpenGL
		// bindings available for use.
		GL.createCapabilities();

		// Set the clear color
		glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		// Run the rendering loop until the user has attempted to close
		// the window or has pressed the ESCAPE key.
		glLoadIdentity();
		glOrtho(0f, 64f, 32f, 0f, -1f, 1f);
		while (!glfwWindowShouldClose(window)) {
			// glMatrixMode(GL_PROJECTION);

			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer
//			glColor3f(0.0f, 0.0f, 0.0f);
//			glRectf(-0.75f, 0.75f, 0.75f, -0.75f);
//			glColor3f(0.0f, 0.9f, 0.0f);
//			glRectf(-0.5f, 0.5f, 0.5f, -0.5f);

			glColor3f((float) Math.random() * 2, (float) Math.random() * 2, (float) Math.random() * 2);
			glRectf(0, 0, (float) Math.random() * 64, (float) Math.random() * 32);

			glfwSwapBuffers(window); // swap the color buffers

			// Poll for window events. The key callback above will only be
			// invoked during this call.
			glfwPollEvents();
		}
	}

	public boolean isGlInit() {
		return glInit;
	}

	public void stop() {
		glfwFreeCallbacks(window);
		glfwDestroyWindow(window);

		// Terminate GLFW and free the error callback
		glfwTerminate();
		glfwSetErrorCallback(null).free();
		glInit = false;
	}

	public static void main(String args[]) {
		C8GLRenderer.getInstance().start();
	}

	public static C8GLRenderer getInstance() {
		if (c8gl == null) {
			c8gl = new C8GLRenderer();
		}
		return c8gl;
	}
}
