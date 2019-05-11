/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j8c.Core;

import java.util.Random;

import j8c.Debugger;

/**
 *
 * @author Misael
 */
public class CPU implements Runnable {
	private static CPU cpu = null;
	private byte[] memory = new byte[4096];
	private int PC = 0x0;
	private short[] charAddress = new short[16];
	private byte[] regV = new byte[0x10];
	private short I = 0x00;
	private byte screen[] = new byte[64 * 32];
	protected int opcode;
	private boolean[] Keys;
	private Random r;
	private int lastPressed = -1;
	private boolean keyIsPressed = false;
	// private boolean drawFlag = true;
	private static Thread CPUThread;

	private static boolean pause = false;
	@SuppressWarnings("unused")
	private static boolean controllerQueue = false;
	private static boolean breakTheEmu = false;

	// private static boolean pauseTheEmu=false;
	// Hexadecimal F == Binary 1111
	// FF= 1 Byte
	private CPU() {
		r = new Random();
	}

	public static void setNull() {
		cpu = null;
		// I need this until StopCPU() works properly
	};

	public static CPU getInstance() {
		if (cpu == null) {
			cpu = new CPU();
		}
		return cpu;

	}

	@Override

	public void run() {

		while (!breakTheEmu) {
			if (!pause) {
				cycle();
				Keys = Keyboard.getKeyArray();
				lastPressed = Keyboard.getLastPressed();
				controllerQueue = false;

			}
		}

	}

	public void init() {
		breakTheEmu = false;
		Keys = Keyboard.getKeyArray();
		lastPressed = Keyboard.getLastPressed();
		// Setting the Charset

		short[] charset = { 0xF0, 0x90, 0x90, 0x90, 0xF0, // 0
				0x20, 0x60, 0x20, 0x20, 0x70, // 1
				0xF0, 0x10, 0xF0, 0x80, 0xF0, // 2
				0xF0, 0x10, 0xF0, 0x10, 0xF0, // 3
				0x90, 0x90, 0xF0, 0x10, 0x10, // 4
				0xF0, 0x80, 0xF0, 0x10, 0xF0, // 5
				0xF0, 0x80, 0xF0, 0x90, 0xF0, // 6
				0xF0, 0x10, 0x20, 0x40, 0x40, // 7
				0xF0, 0x90, 0xF0, 0x90, 0xF0, // 8
				0xF0, 0x90, 0xF0, 0x10, 0xF0, // 9
				0xF0, 0x90, 0xF0, 0x90, 0x90, // A
				0xE0, 0x90, 0xE0, 0x90, 0xE0, // B
				0xF0, 0x80, 0x80, 0x80, 0xF0, // C
				0xE0, 0x90, 0x90, 0x90, 0xE0, // D
				0xF0, 0x80, 0xF0, 0x80, 0xF0, // E
				0xF0, 0x80, 0xF0, 0x80, 0x80, // F
		};

		int a = 0;
		int b = 0;

		for (int i = 0; i < 80; i++) {
			memory[i] = (byte) charset[i];
		}

		for (short c : charset) {
			charset[a] = (byte) c;
			a++;
			if (a % 5 == 0 || a == 0) {
				charAddress[b] = (short) a;
				b++;
			}

		}

		// Charset has been set

		PC = 0x200;
		Stack.reset();
		loadMemory();
		CPUThread = new Thread(this);
		CPUThread.setName("Interpreter");
		CPUThread.start();

	}

	public void stopCPU() {
		if (CPUThread.isAlive()) {
			breakTheEmu = true;
			try {
				CPUThread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				CPUThread.interrupt();
			}
		}
		for (int i = 0; i < memory.length; i++) {
			memory[i] = 0;
		}
		for (int i = 0; i < screen.length; i++) {
			screen[i] = 0;
		}
		Graphics.cleanBl();
		Stack.reset();

	}

	public void pauseCPU() {
		pause = !pause;
	}

	public void PressedKeyInterrupt() {

		controllerQueue = true;
		Keys = Keyboard.getKeyArray();
		keyIsPressed = Keyboard.someKeyIsPressed();
		lastPressed = Keyboard.getLastPressed();
		controllerQueue = true;

	}

	public void unpressedKeyInterrupt() {

		controllerQueue = true;
		Keys = Keyboard.getKeyArray();
		keyIsPressed = false;
		controllerQueue = true;

	}

	private void cycle() {
//		int byteOrder = 0;
////		for (byte b : memory) {
////			System.out.print(Integer.toHexString(b));
////
////			byteOrder++;
////			if (byteOrder == 1) {
////				System.out.print("\n");
////				byteOrder = 0;
////			}
////		}
		// Timers.setCurrent(System.nanoTime());
		fetchOpcode();
		decodeExecute();
		if (Debugger.isDebuggerStarted()) {
			logToRegisterWatch();
			int sleepTime = Debugger.getInstance().getSleepTimer();
			try {
				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {

			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// Timers.setAfter(System.nanoTime());
		// Timers.calculate();

	}

	public void logToRegisterWatch() {
		Debugger.getInstance().updateRegisters(regV, I, Stack.getData(), Stack.getLastOp(),Stack.getPointer());
	}

	private void loadMemory() {
		byte[] rom = RomLoader.getInstance().getRom();
		for (int i = 0; i < rom.length; ++i) {
			memory[512 + i] = rom[i];
		}
		// teste de leitura da memoria
//		int i = 0;
//		for (byte a : memory) {
//
//			System.out.print(Integer.toHexString(a));
//			++i;
//			if (i == 2) {
//				System.out.print(" ");
//			}
//			if (i == 4) {
//				System.out.print("\n");
//				i = 0;
//			}
//
//		}
	}

	public void fetchOpcode() {
		byte opcodeFbyte = memory[PC];
		byte opcodeSbyte = memory[PC + 1];

		opcode = Byte.toUnsignedInt(opcodeFbyte);

		opcode <<= 8;

		opcode |= Byte.toUnsignedInt(opcodeSbyte);

	}

	private void decodeExecute() {

		int instructionId = (opcode & 0xf000);
		int instructionArgs = (opcode & 0x0fff);
		new Instruction(instructionId, instructionArgs).execute();
		// System.out.println(Integer.toHexString(opcode));

	}

	private static class Stack {

		private static int stack[] = new int[16];
		private static int pointer = 0;
		private static String lastOp = "null";

		public static void push(int val) {
			stack[pointer] = val;
			pointer++;
			lastOp = "push";
		}

		public static int pop() {
			lastOp = "pop";
			pointer--;
			return stack[pointer];

		}

		private static String getLastOp() {
			return lastOp;
		}

		public static int[] getData() {
			return stack;
		}
		public static int getPointer(){
			return pointer;
		}
		public static void reset() {
			for (int i = 0; i < stack.length; i++) {
				stack[i] = 0;
			}
		}
	}

	private class Instruction {

		// 00 System Functions
		// 80 Math functions
		// Instruction like functions
		int id = 0;
		int args = 0;

		public void logToDebugger(String asm) {

			if (Debugger.isDebuggerStarted()) {
				System.out.println(asm);
			}
		}

		public int toUnsignedInt(byte value) {
			return ((int) value) & 0xff;
		}

		public Instruction(int id, int args) {
			this.id = id;
			this.args = args;

		}

		public void execute() {
			// Do Something
			// System.out.println("The instruction is:" + Integer.toHexString((id & args)));
			if (id == 0x0000) {
				if (args == 0x0E0) {
					logToDebugger("clsc");
					for (int i = 0; i < screen.length; ++i) {
						screen[i] = 0;
					}
					Graphics.Draw(screen, "");
					PC += 2;
				}
				if (args == 0x0ee) {
					logToDebugger("ret");
					PC = Stack.pop();
					PC += 2;
				}
			}
			if (id == 0x1000) {
				logToDebugger("goto " + (args));
				PC = args;

			}
			if (id == 0x2000) {
				logToDebugger("call " + (args));
				Stack.push(PC);
				PC = args;
			}
			if (id == 0x3000) {
				logToDebugger("skpfeq V[" + ((args & 0xF00) >> 8) + "]," + (args & 0x0FF));
				int Xreg = (args & 0xF00) >> 8;
				int value = (args & 0x0FF);
				if (regV[Xreg] == value) {
					PC += 4;
				} else {
					PC += 2;
				}
			}
			if (id == 0x4000) {
				logToDebugger("skpfneq V[" + ((args & 0xF00) >> 8) + "]," + (args & 0x0FF));
				int Xreg = (args & 0xF00) >> 8;
				int value = (args & 0x0FF);
				if (regV[Xreg] != value) {
					PC += 4;
				} else {
					PC += 2;
				}

			}
			if (id == 0x5000) {
				logToDebugger("skpfeq V[" + ((args & 0xF00) >> 8) + "],V[" + ((args & 0x0f0) >> 4) + "]");

				int Xreg = (args & 0xf00) >> 8;
				int Yreg = (args & 0x0f0) >> 4;
				if (regV[Xreg] == regV[Yreg]) {
					PC += 4;
				} else {
					PC += 2;
				}
			}
			if (id == 0x6000) {
				logToDebugger("ld V[" + ((args & 0xF00) >> 8) + "]," + toUnsignedInt((byte) (args & 0x0ff)));
				int Xreg = (args & 0x0f00) >> 8;
				byte value = (byte) toUnsignedInt((byte) (args & 0xff));
				regV[Xreg] = value;
				PC += 2;
			}
			if (id == 0x7000) {
				logToDebugger("add V[" + ((args & 0xF00) >> 8) + "]," + toUnsignedInt((byte) (args & 0x0ff)));
				int Xreg = (args & 0x0f00) >> 8;
				byte value = (byte) (args & 0x0ff);
				regV[Xreg] = (byte) (toUnsignedInt(regV[Xreg]) + toUnsignedInt((byte) value));
				PC += 2;
			}
			if (id == 0x8000) {
				// Math instructions
				int mathInstId = (args & 0x000f);
				byte Xreg = (byte) ((args & 0x0f00) >> 8);
				byte Yreg = (byte) ((args & 0x00f0) >> 4);
				if (mathInstId == 0x0000) {
					logToDebugger("ld V[" + Xreg + "]," + "V[" + Yreg + "]");
					regV[Xreg] = regV[Yreg];
				}
				if (mathInstId == 0x0001) {
					logToDebugger("or V[" + Xreg + "],V[" + Yreg + "]");
					regV[Xreg] = (byte) (regV[Xreg] | regV[Yreg]);
				}
				if (mathInstId == 0x0002) {
					logToDebugger("and V[" + Xreg + "],V[" + Yreg + "]");
					regV[Xreg] = (byte) (regV[Xreg] & regV[Yreg]);
				}
				if (mathInstId == 0x0003) {
					logToDebugger("xor V[" + Xreg + "],V[" + Yreg + "]");
					regV[Xreg] = (byte) (regV[Xreg] ^ regV[Yreg]);
				}
				if (mathInstId == 0x0004) {
					logToDebugger("addcarry V[" + Xreg + "],V[" + Yreg + "]");
					if ((regV[Xreg] + regV[Yreg]) > 255) {

						regV[0xf] = 1;
						regV[Xreg] = (byte) 0xFF;

					} else {
						regV[Xreg] = (byte) (regV[Xreg] + regV[Yreg]);
					}
				}
				if (mathInstId == 0x0005) {
					logToDebugger("subcarry V[" + Xreg + "],V[" + Yreg + "]");
					if (toUnsignedInt(regV[Xreg]) > toUnsignedInt(regV[Yreg])) {

						regV[0xf] = 1;
						regV[Xreg] = (byte) (toUnsignedInt(regV[Yreg]) - toUnsignedInt(regV[Xreg]));

					} else {
						regV[Xreg] = (byte) (toUnsignedInt(regV[Xreg]) - toUnsignedInt(regV[Yreg]));
					}
				}
				if (mathInstId == 0x0006) {
					logToDebugger("rsftob V[" + Xreg + "]");
					regV[0xf] = (byte) (regV[Xreg] & 0x000f);
					regV[Xreg] = (byte) ((regV[Xreg] >> 1) & 0xFF);
				}
				if (mathInstId == 0x0007) {
					logToDebugger("subcarry V[" + Xreg + "],V[" + Yreg + "]");
					if ((regV[Xreg] < regV[Yreg])) {

						regV[0xf] = 1;
						regV[Xreg] = (byte) toUnsignedInt(
								(byte) (toUnsignedInt(regV[Yreg]) - toUnsignedInt(regV[Xreg])));

					} else {
						regV[Xreg] = (byte) (toUnsignedInt(regV[Xreg]) - toUnsignedInt(regV[Yreg]));
					}

				}
				if (mathInstId == 0x000e) {
					logToDebugger("lsftob V[" + Xreg + "]");
					regV[0xf] = (byte) (regV[Xreg] & 0x80);
					regV[Xreg] = (byte) ((regV[Xreg] << 1) & 0xFF);
				}
				PC += 2;
			}
			if (id == 0x9000) {

				int Xreg = ((args & 0x0f00) >> 8);
				int Yreg = ((args & 0x00f0) >> 4);
				logToDebugger("jmpifneq V[" + Xreg + "],V[" + Yreg + "]");
				if (regV[Xreg] != regV[Yreg]) {
					PC += 4;
				} else {
					PC += 2;
				}
			}
			if (id == 0xA000) {

				I = (short) (args & 0x0fff);
				logToDebugger("ld " + I);
				PC += 2;
			}
			if (id == 0xb000) {
				logToDebugger("jmplded" + (args & 0x0fff));
				PC = (args & 0x0fff) + I;
			}
			if (id == 0xc000) {
				logToDebugger("rndand" + ((args & 0xf00) >> 8) + "," + (args & 0xff) + "");
				int Xreg = (args & 0xf00) >> 8;
				int nextInt = r.nextInt(255);
				//
				regV[Xreg] = (byte) ((byte) nextInt & toUnsignedInt((byte) (args & 0xff)));
				PC += 2;
			}
			if (id == 0xd000) {
				logToDebugger(
						"drw V[" + ((args & 0xF00) >> 8) + "],V[" + (byte) ((args & 0x0f0) >> 4) + "]," + (args & 0xf));
				// Draw
				// Tela 64*32
				short valX = (short) toUnsignedInt(regV[(args & 0xf00) >> 8]);
				short valY = (short) toUnsignedInt(regV[(args & 0xf0) >> 4]);
				int height = args & 0xf;
				regV[0xf] = 0;
				for (int Y = 0; Y < height; Y++) {
					int pixel = toUnsignedInt(memory[I + Y]);
					for (int X = 0; X < 8; X++) {
						int num = pixel & (0x80 >> X);
						if (num != 0) {
							int index = (valX + X + ((valY + Y) * 64));
							if (index >= 2048) {
								while (index >= 2048) {
									index = index - 2048;
								}
								// Wrap around the screen
							}
							if (screen[index] == 1) {
								regV[0xf] = 1;
							}
							screen[index] ^= 1;

						}
					}
				}

				Graphics.Draw(screen, "");
				PC += 2;
			}
			if (id == 0xe000) {
				if (0x9e == (args & 0xff)) {
					int Xreg = (args & 0xf00) >> 8;
					if (Keys[regV[Xreg]]) {
						PC += 4;
					} else {
						PC += 2;
					}
					logToDebugger("skp V[" + Xreg + "]");
				}
				if (0xA1 == (args & 0xff)) {
					int Xreg = (args & 0xf00) >> 8;
					logToDebugger("sknp V[" + Xreg + "]");
					if (!Keys[regV[Xreg]]) {
						PC += 4;
					} else {
						PC += 2;
					}
				}

			}
			if (id == 0xf000) {
				if (0x07 == (args & 0xff)) {

					int Xreg = (args & 0xf00) >> 8;
					logToDebugger("unlddt V[" + Xreg + "]");
					regV[Xreg] = (byte) Timers.getDelayTimer();
					PC += 2;
				}
				if (0x0a == (args & 0xff)) {
					// To be implemented
					int Xreg = (args & 0xf00) >> 8;
					logToDebugger("waitkpld V[ " + Xreg + "]");
					if (keyIsPressed || Keyboard.getLastPressed() != -1) {
						regV[Xreg] = (byte) lastPressed;
						PC += 2;
					}
				}
				if (0x15 == (args & 0xff)) {
					int Xreg = (args & 0xf00) >> 8;
					logToDebugger("lddtfreg V[" + Xreg + "]");
					Timers.setDelayTimer(regV[Xreg]);
					PC += 2;
				}
				if (0x18 == (args & 0xff)) {
					int Xreg = (args & 0xf00) >> 8;
					logToDebugger("ldstfreg V[" + Xreg + "]");
					Timers.setSoundTimer(regV[Xreg]);
					PC += 2;
				}
				if (0x1E == (args & 0xff)) {
					int Xreg = (args & 0xf00) >> 8;
					logToDebugger("addi V[" + Xreg + "]");
					I += toUnsignedInt(regV[Xreg]);
					PC += 2;
				}
				if (0x29 == (args & 0xff)) {
					int Xreg = (args & 0xf00) >> 8;
//					I = charAddress[regV[Xreg]];
					logToDebugger("ldspr V[" + Xreg + "]");
					I = (short) toUnsignedInt((byte) (regV[Xreg] * 0x5));
					PC += 2;
				}
				if (0x33 == (args & 0xff)) {

					int Xreg = (args & 0xf00) >> 8;
					short value = (short) toUnsignedInt(regV[Xreg]);
					logToDebugger("BCD V[" + Xreg + "]");
					memory[I] = (byte) (value / 100);
					memory[I + 1] = (byte) ((value / 10) % 10);
					memory[I + 2] = (byte) ((value % 100) % 10);
					PC += 2;

				}
				if (0x55 == (args & 0xff)) {
					int copyIndex = (args & 0xf00) >> 8;
					logToDebugger("rgdump [" + I + "],V[" + copyIndex + "]");

					for (int i = 0; i <= copyIndex; i++) {
						memory[I + i] = regV[i];
					}
					PC += 2;
				}
				if (0x65 == (args & 0xff)) {
					int copyIndex = (args & 0xf00) >> 8;
					logToDebugger("memdump V[" + copyIndex + "]," + copyIndex + "");
					for (int i = 0; i <= copyIndex; i++) {
						regV[i] = memory[I + i];
					}
					PC += 2;

				}
			}

		}

	}

}
