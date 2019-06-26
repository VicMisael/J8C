package j8c.Core;

import java.util.Random;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;
import javax.swing.JOptionPane;

public class Timers {
	private static short soundTimer = 0;
	private static short delayTimer = 0;
	private static long currentMilis;
	private static long afterMilis;
	private static Thread delayTimerCounter;
	private static Thread soundTimerCounter;
	private static SourceDataLine sdline;

	public static void setCurrent(long current) {
		currentMilis = current;
	}

	static {

		try {
			AudioFormat f = new AudioFormat(8000, 8, 1, true, false);
			sdline = AudioSystem.getSourceDataLine(f);
			sdline.open();
			sdline.start();

		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage(), "Error trying to initialize audio \n ",
					JOptionPane.ERROR_MESSAGE);
		}

	}

	public static void setAfter(long after) {
		afterMilis = after;
		calculate();
	}

	public static void calculate() {
		System.err.println((afterMilis - currentMilis) / 1000000d);

	}

	public static short getDelayTimer() {
		return delayTimer;
	}

	public static short getSoundTimer() {
		return soundTimer;
	}

	public static void setDelayTimer(short delTimer) {
		delayTimer = delTimer;
		delayTimerCounter = new Thread(new DelayTimerCounter());
		delayTimerCounter.setName("delay TImer");
		delayTimerCounter.start();

	}

	public static void setSoundTimer(short sTimer) {
		soundTimer = sTimer;
		soundTimerCounter = new Thread(new SoundTimerCounter());
		soundTimerCounter.setName("Sound TImer");
		soundTimerCounter.start();
	}

	private static class DelayTimerCounter implements Runnable {
		public void run() {
			while (delayTimer != 0) {

				delayTimer--;
				try {
					Thread.sleep(16);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}

	private static class SoundTimerCounter implements Runnable {
		public void run() {
			double angle=0;
			byte[] aud = new byte[8000 / 16];
			int d=0;
			for (int i = 0; i < aud.length; i++) {
				angle = (i*160000/(aud.length*1.0d))*3.14d;
				aud[i]=(byte)(Math.sin(angle)*255);
			}
			while (soundTimer != 0) {
				soundTimer--;
				System.out.println("beep");
				sdline.write(aud, 0, aud.length);
				try {
					Thread.sleep(16);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}
}
