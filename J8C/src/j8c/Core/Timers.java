package j8c.Core;

public class Timers {
	private static short soundTimer = 0;
	private static short delayTimer = 0;
	private static long currentMilis;
	private static long afterMilis;
	
	public static void setCurrent(long current) {
		currentMilis=current;
	}
	public static void setAfter(long after) {
		afterMilis=after;
		//calculate();
	}
	@SuppressWarnings("unused")
	private static void calculate() {
		System.out.println(afterMilis-currentMilis);
		
	}

	public static short getDelayTimer() {
		return delayTimer;
	}

	public static short getSoundTimer() {
		return soundTimer;
	}

	public static void setDelayTimer(short delTimer) {
		delayTimer = delTimer;
		Thread delayTimerCounter = new Thread(new DelayTimerCounter());
		delayTimerCounter.start();
	}
	public static void setSoundTimer(short sTimer) {
		delayTimer = sTimer;
		Thread soundTimerCounter = new Thread(new SoundTimerCounter());
		soundTimerCounter.start();
	}
	
	
	private static class DelayTimerCounter implements Runnable {
		public void run() {
			while (delayTimer != 0) {
				
				delayTimer--;
				System.out.println("Test");
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
