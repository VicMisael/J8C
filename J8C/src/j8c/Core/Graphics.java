package j8c.Core;

import j8c.MainGUI;

public class Graphics {
	static Thread gl;
	static byte[] screenT;
	static int modeT = 0;
	static boolean stop = false;
	static boolean isPrepared = false;
	private Graphics() {
	}

	public static void Draw(byte[] screen, int mode) {

		if (!Options.getInstance().useOpenGl()) {
			MainGUI.drawToCanvas(screen, mode);
			stop = true;
		} else {
			if (stop) {
				stop = false;
			}
			prepareGlThread();
			screenT = screen;
			modeT = mode;

		}

	}

	public static void stopGL() {
		stop = true;
		try {
			gl.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		isPrepared = false;
		gl=null;
	}

	

	private static void prepareGlThread() {
		if (!isPrepared) {
			gl = new Thread(new GLManager());
			gl.setName("GL RENDERER THREAD");
			isPrepared = true;
			gl.start();
		}
	
	}

	public static void cleanBl() {
		byte[] screen = new byte[64 * 32];
		for (int a = 0; a < screen.length; a++) {
			screen[a] = 0;
		}
		MainGUI.drawToCanvas(screen, -1);
	}

	public static class GLManager implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub

			while (!stop) {
				if (!C8GLRenderer.getInstance().isGlInit()) {
					C8GLRenderer.getInstance().init();
				}
				C8GLRenderer.getInstance().draw(screenT, modeT);
				try {
					Thread.sleep(12);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			C8GLRenderer.getInstance().stop();

		}

	}

}
