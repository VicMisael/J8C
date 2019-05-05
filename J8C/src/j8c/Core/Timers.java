package j8c.Core;

public class Timers {
	private static short soundTimer = 0;
	private static short delayTimer = 0;
	private static long currentMilis;
	private static long afterMilis;
	private static Thread delayTimerCounter;
	private static Thread soundTimerCounter;

	public static void setCurrent(long current) {
		currentMilis = current;
	}

	static {
		
		
		

	}

	public static void setAfter(long after) {
		afterMilis = after;
		// calculate();
	}


	public static void calculate() {
		System.out.println(afterMilis - currentMilis);

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
			while (soundTimer != 0) {
				soundTimer--;
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
