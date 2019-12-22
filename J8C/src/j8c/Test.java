package j8c;

import java.util.Random;

import j8c.Core.Timers;

public class Test {
	private static byte screen[] = new byte[64 * 32];
	private static byte HRScreen[] = new byte[128 * 64];
	private static byte zero128[] = new byte[128];

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		// Timers.setSoundTimer((short)60);
		HRScreen[1]=1;
		for (int y = 0; y < 64; y++) {
			for (int x = 0; x < 128; x++) {
				HRScreen[x + y * 128] = (byte)new Random().nextInt(9);
				System.out.print(HRScreen[x + y * 128]);
			}
			System.out.print(" line " + y + "\n");
		}
		testarScreen(0xf);
		for (int y = 0; y < 64; y++) {
			for (int x = 0; x < 128; x++) {
				System.out.print(HRScreen[x + y * 128]);
			}
			System.out.print(" line " + y + "\n");
		}

	}

	public static void testarScreen(int height) {
		for (int y = 0; y < 64; y++) {
			byte nextLine[] = new byte[128];

			System.arraycopy(HRScreen, (y * 128) + 4, nextLine, 0, 124);
			System.arraycopy(nextLine, 0, HRScreen, y * 128, 128);
//	

		}
	

	}
}
