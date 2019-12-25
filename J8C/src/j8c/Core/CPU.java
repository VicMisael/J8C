/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package j8c.Core;

import java.util.Arrays;
import java.util.Random;

import javax.swing.JOptionPane;

import j8c.Debugger;

/**
 *
 * @author Misael
 */
public class CPU implements Runnable {
	// Static fields
	private static CPU cpu = null;
	private static Thread CPUThread;
	private static boolean pause = false;
	@SuppressWarnings("unused")
	private static boolean controllerQueue = false;
	private static boolean breakTheEmu = false;
	// non-static fields
	private byte[] memory = new byte[4096];
	private int PC = 0x0;
	// private short[] charAddress = new short[16];
	private byte[] regV = new byte[0x10];
	private short I = 0x00;
	private byte screen[] = new byte[64 * 32];
	private byte HRScreen[] = new byte[128 * 64];
	protected int opcode;
	private boolean[] Keys;
	private final byte[] zero64 = new byte[64];
	private final byte[] zero128 = new byte[128];
	private int lastPressed = -1;
	private boolean keyIsPressed = false;
	private String asm = "";
	// private boolean drawFlag = true;

	private int renderMode = 0;

	// private static boolean pauseTheEmu=false;
	// Hexadecimal F == Binary 1111
	// FF= 1 Byte
	private CPU() {

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
				Keys = Keyboard.getKeyArray();
				lastPressed = Keyboard.getLastPressed();
				controllerQueue = false;
				cycle();

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

		short[] HRCharset = { 0xFF, 0xFF, 0xC3, 0xC3, 0xC3, 0xC3, 0xC3, 0xC3, 0xFF, 0xFF, // 0
				0x18, 0x78, 0x78, 0x18, 0x18, 0x18, 0x18, 0x18, 0xFF, 0xFF, // 1
				0xFF, 0xFF, 0x03, 0x03, 0xFF, 0xFF, 0xC0, 0xC0, 0xFF, 0xFF, // 2
				0xFF, 0xFF, 0x03, 0x03, 0xFF, 0xFF, 0x03, 0x03, 0xFF, 0xFF, // 3
				0xC3, 0xC3, 0xC3, 0xC3, 0xFF, 0xFF, 0x03, 0x03, 0x03, 0x03, // 4
				0xFF, 0xFF, 0xC0, 0xC0, 0xFF, 0xFF, 0x03, 0x03, 0xFF, 0xFF, // 5
				0xFF, 0xFF, 0xC0, 0xC0, 0xFF, 0xFF, 0xC3, 0xC3, 0xFF, 0xFF, // 6
				0xFF, 0xFF, 0x03, 0x03, 0x06, 0x0C, 0x18, 0x18, 0x18, 0x18, // 7
				0xFF, 0xFF, 0xC3, 0xC3, 0xFF, 0xFF, 0xC3, 0xC3, 0xFF, 0xFF, // 8
				0xFF, 0xFF, 0xC3, 0xC3, 0xFF, 0xFF, 0x03, 0x03, 0xFF, 0xFF, // 9
				0x7E, 0xFF, 0xC3, 0xC3, 0xC3, 0xFF, 0xFf, 0xC3, 0xC3, 0xC3, // A
				0xFC, 0xFC, 0xC3, 0xC3, 0xFC, 0xFC, 0xC3, 0xC3, 0xFC, 0xFC, // B
				0x3C, 0xFF, 0xC3, 0xC0, 0xC0, 0xC0, 0xC0, 0xC3, 0xFF, 0x3C, // C
				0xFC, 0xFE, 0xC3, 0xC3, 0xC3, 0xC3, 0xC3, 0xC3, 0xFE, 0xFC, // D
				0xFF, 0xFF, 0xC0, 0xC0, 0xFF, 0xFF, 0xC0, 0xC0, 0xFF, 0xFF, // E
				0xFF, 0xFF, 0xC0, 0xC0, 0xFF, 0xFF, 0xC0, 0xC0, 0xC0, 0xC0, // F

		};
		// Copy from a Chip8 Rust project
		// https://github.com/stianeklund/chip8
		Arrays.fill(zero64, (byte) 0);
		Arrays.fill(memory, (byte) 0);
		Arrays.fill(zero128, (byte) 0);
		for (int i = 0; i < 0x50; i++) {
			memory[i] += charset[i];
		}
		for (int i = 0; i < HRCharset.length; i++) {
			memory[i + 0x50] += HRCharset[i];
		}

//		for (short c : charset) {
//			charset[a] = (byte) c;
//			a++;
//			if (a % 5 == 0 || a == 0) {
//				charAddress[b] = (short) a;
//				b++;
//			}
//
//		}

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
		for (int i = 0; i < HRScreen.length; i++) {
			HRScreen[i] = 0;
		}
		Graphics.cleanBl();
		Stack.reset();
		Timers.reset();
		renderMode = 0;

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
		// Timers.setAfter(System.nanoTime());
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
				// Thread.sleep(1000);
				Thread.sleep(1);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public void logToRegisterWatch() {
		Debugger.getInstance().updateRegisters(regV, I, Stack.getData(), Stack.getLastOp(), Stack.getPointer(), PC,
				opcode, asm);
	}

	private void loadMemory() {
		byte[] rom = RomLoader.getInstance().getRom();
//		for (int i = 0; i < rom.length; ++i) {
//			memory[512 + i] = rom[i];
//		}
		System.arraycopy(rom, 0, memory, 512, rom.length);
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
			if (pointer > -1) {
				int value = stack[pointer];
				return value;
			} else {
				System.out.println("Stack pointer is negative, W T F?");
				return 0;
			}

		}

		private static String getLastOp() {
			return lastOp;
		}

		public static int[] getData() {
			return stack;
		}

		public static int getPointer() {
			return pointer;
		}

		public static void reset() {
			pointer = 0;
		}
	}

	private class Instruction {

		// 00 System Functions
		// 80 Math functions
		// Instruction like functions
		int id = 0;
		int args = 0;

		public void error(String err) {
			JOptionPane.showMessageDialog(null, err, "SC8", JOptionPane.ERROR_MESSAGE);
			CPU.getInstance().stopCPU();
		}

		public void logToDebugger(String logasm) {
			// System.out.println(logasm);
			if (Debugger.isDebuggerStarted()) {
				asm = logasm;
			}
		}

		public int toUnsignedInt(byte value) {
			return ((int) value) & 0xff;
		}

		public Instruction(int id, int args) {
			this.id = id;
			this.args = args;
			// System.out.println(Integer.toHexString(id|args));

		}

		public void processMath() {
			int mathInstId = (args & 0x000f);
			byte Xreg = (byte) ((args & 0x0f00) >> 8);
			byte Yreg = (byte) ((args & 0x00f0) >> 4);
			switch (mathInstId) {
			case (0x0):
				logToDebugger("ld V[" + Xreg + "]," + "V[" + Yreg + "]");
				regV[Xreg] = regV[Yreg];
				break;
			case (0x1):
				logToDebugger("or V[" + Xreg + "],V[" + Yreg + "]");
				regV[Xreg] |= regV[Yreg];
				break;
			case (0x2):
				logToDebugger("and V[" + Xreg + "],V[" + Yreg + "]");
				regV[Xreg] &= regV[Yreg];
				break;
			case (0x3):
				logToDebugger("xor V[" + Xreg + "],V[" + Yreg + "]");
				regV[Xreg] ^= regV[Yreg];
				break;
			case (0x4):
				logToDebugger("addcarry V[" + Xreg + "],V[" + Yreg + "]");
				if ((regV[Xreg] + regV[Yreg]) > 255) {

					regV[0xf] = 1;
					// regV[Xreg] = (byte) 0xFF;

				}
				regV[Xreg] += regV[Yreg];
				break;
			case (0x5):
				logToDebugger("subcarry V[" + Xreg + "],V[" + Yreg + "]");
				if (toUnsignedInt(regV[Xreg]) > toUnsignedInt(regV[Yreg])) {

					regV[0xf] = 1;
				}
				regV[Xreg] -= regV[Yreg];
				break;
			case (0x6):
				logToDebugger("rsftob V[" + Xreg + "]");
				regV[0xf] = (byte) (regV[Xreg] & 0x1);
				regV[Xreg] >>>= 1;
				break;
			case (0x7):
				logToDebugger("subcarry V[" + Xreg + "],V[" + Yreg + "]");
				if ((regV[Xreg] < regV[Yreg])) {

					regV[0xf] = 1;

				} else {
					regV[0xf] = 0;

				}
				regV[Xreg] = (byte) (regV[Yreg] - regV[Xreg]);
				break;
			case (0xe):
				logToDebugger("lsftob V[" + Xreg + "]");
				if(!Options.getInstance().useDifferent8xye()) {
					regV[0xf] = (byte) (regV[Yreg] & 0x80);
					regV[Xreg] = (byte) ((regV[Yreg] << 1) & 0xFF);
				}else {
					regV[0xf] = (byte) (regV[Xreg] & 0x80);
					regV[Xreg] = (byte) ((regV[Xreg] << 1) & 0xFF);
				}
				
				break;
			default:
				System.out.println("Unknown math instruction" + Integer.toHexString(mathInstId));
				break;

			}
			PC += 2;
		}

		public void execute() {
			// TODO change the if and else to a switch statement
			// Do Something
			// System.out.println("The instruction is:" + Integer.toHexString((id & args)));
			int Xreg = -1;
			int Yreg = -1;
			byte value = 0;
			switch (id) {

			case 0x0:
				switch (args & 0x0f0) {
				case (0x0c0):
					// error("Not yet Implemented 0x" + Integer.toHexString(opcode));
					int height = args & 0xf;

					logToDebugger("scrdwn " + height);
					if (renderMode == 0) {
//						for (int i = 0; i < height; i++) {
//						}
						while (height > 0) {
							int y = 31;
							while (y > 0) {
								System.arraycopy(screen, (y - 1) * 64, screen, y * 64, 64);
								y--;
							}
							System.arraycopy(zero64, 0, screen, 0, 64);
							height--;
						}

					} else if (renderMode == 1) {
//						for (int i = 0; i < height; i++) {
//						}
						while (height > 0) {
							int y = 63;
							while (y > 0) {
								System.arraycopy(HRScreen, (y - 1) * 128, HRScreen, y * 128, 128);
								y--;
							}
							System.arraycopy(zero128, 0, HRScreen, 0, 128);
							height--;
						}
					}
					PC += 2;
					break;

				}
				switch (args) {
				case (0x0E0):
					logToDebugger("clsc");
					if (renderMode == 0) {
						for (int i = 0; i < screen.length; ++i) {
							screen[i] = 0;
						}
						Graphics.Draw(screen, renderMode);
					} else {
						for (int i = 0; i < HRScreen.length; ++i) {
							HRScreen[i] = 0;
						}
						Graphics.Draw(HRScreen, renderMode);
					}

					PC += 2;
					break;
				case (0x0ee):
					logToDebugger("ret");
					PC = Stack.pop();
					PC += 2;
					break;

				case (0x00FB):
					logToDebugger("scrri");
					// error("Not yet Implemented 0x" + Integer.toHexString(opcode));
					if (renderMode == 0) {
						for (int y = 0; y < 32; y++) {
							byte nextLine[] = new byte[64];
//							for (int b = 0; b < 4; b++) {
//								nextLine[b] = 0;
//							}
							System.arraycopy(screen, y * 64, nextLine, 4, 60);
							System.arraycopy(nextLine, 0, screen, y * 64, 64);
//					
						}

					} else if (renderMode == 1) {

						for (int y = 0; y < 64; y++) {
							byte nextLine[] = new byte[128];
//							for (int b = 0; b < 4; b++) {
//								nextLine[b] = 0;
//							}
							System.arraycopy(HRScreen, y * 128, nextLine, 4, 124);
							System.arraycopy(nextLine, 0, HRScreen, y * 128, 128);
//					
						}
					}
					PC += 2;

					// Scroll display 4 pixels to the right.
					break;
				case (0x00FC):
					logToDebugger("scrle");
					// error("Not yet Implemented 0x" + Integer.toHexString(opcode));
					if (renderMode == 0) {
						for (int y = 0; y < 32; y++) {
							byte nextLine[] = new byte[64];

							System.arraycopy(screen, (y * 64) + 4, nextLine, 0, 60);
							System.arraycopy(nextLine, 0, screen, y * 64, 64);
						}

					} else if (renderMode == 1) {

						for (int y = 0; y < 64; y++) {
							byte nextLine[] = new byte[128];

							System.arraycopy(HRScreen, (y * 128) + 4, nextLine, 0, 124);
							System.arraycopy(nextLine, 0, HRScreen, y * 128, 128);
//					

						}
					}
					PC += 2;
					// Scroll display 4 pixels to the left.
					break;
				case (0x00FD):
					// error("Not yet Implemented 0x" + Integer.toHexString(opcode));
					logToDebugger("quit");
					error("Exit call");
					// Exit the interpreter.
					break;
				case (0x00FE):
					// error("Not yet Implemented 0x"+Integer.toHexString(opcode));
					logToDebugger("lowRes");
					System.out.println("Low resolution mode on");
					// Enable low res (64x32) mode.
					renderMode = 0;
					PC += 2;
					break;
				case (0x00FF):
					// error("Not yet Implemented 0x"+Integer.toHexString(opcode));
					logToDebugger("highres");
					System.out.println("High Resolution Mode On");
					// Enable high res (128x64) mode.
					renderMode = 1;
					PC += 2;
					break;
				default:
					System.out.println("Uknown opcode" + Integer.toHexString(id|args));
					break;
				}
				break;
			case (0x1000):
				logToDebugger("goto " + (args));
				PC = args;
				break;

			case (0x2000):
				logToDebugger("call " + (args));
				Stack.push(PC);
				PC = args;
				break;

			case (0x3000):
				logToDebugger("skpfeq V[" + ((args & 0xF00) >> 8) + "]," + (args & 0x0FF));
				Xreg = (args & 0xF00) >> 8;
				value = (byte) (args & 0x0FF);
				if (regV[Xreg] == value) {
					PC += 4;
				} else {
					PC += 2;
				}
				break;

			case (0x4000):
				logToDebugger("skpfneq V[" + ((args & 0xF00) >> 8) + "]," + (args & 0x0FF));
				Xreg = (args & 0xF00) >> 8;

				value = (byte) (args & 0x0FF);
				if (regV[Xreg] != value) {
					PC += 4;
				} else {
					PC += 2;
				}
				break;

			case (0x5000):
				logToDebugger("skpfeq V[" + ((args & 0xF00) >> 8) + "],V[" + ((args & 0x0f0) >> 4) + "]");

				Xreg = (args & 0xf00) >> 8;
				Yreg = (args & 0x0f0) >> 4;
				if (regV[Xreg] == regV[Yreg]) {
					PC += 4;
				} else {
					PC += 2;
				}
				break;

			case (0x6000):
				logToDebugger("ld V[" + ((args & 0xF00) >> 8) + "]," + toUnsignedInt((byte) (args & 0x0ff)));
				Xreg = (args & 0x0f00) >> 8;
				value = (byte) toUnsignedInt((byte) (args & 0xff));
				regV[Xreg] = value;
				PC += 2;
				break;

			case (0x7000):
				logToDebugger("add V[" + ((args & 0xF00) >> 8) + "]," + toUnsignedInt((byte) (args & 0x0ff)));
				Xreg = (args & 0x0f00) >> 8;
				value = 0;
				value += (args & 0x0ff);
				regV[Xreg] += value;
				PC += 2;
				break;

			case (0x8000):
				processMath();
				break;
			// Math instructions

			case (0x9000):

				Xreg = ((args & 0x0f00) >> 8);
				Yreg = ((args & 0x00f0) >> 4);
				logToDebugger("jmpifneq V[" + Xreg + "],V[" + Yreg + "]");
				if (regV[Xreg] != regV[Yreg]) {
					PC += 4;
				} else {
					PC += 2;
				}
				break;

			case (0xA000):

				I = (short) (args & 0x0fff);
				logToDebugger("ld " + I);
				PC += 2;
				break;

			case (0xb000):
				logToDebugger("jmplded" + (args & 0x0fff));
				PC = (args & 0x0fff) + I;
				break;
			case (0xc000):
				logToDebugger("rnd" + ((args & 0xf00) >> 8) + "," + (args & 0xff) + "");
				Xreg = (args & 0xf00) >> 8;
				regV[Xreg] ^= regV[Xreg];
				regV[Xreg] += ((byte) new Random().nextInt(255)) & args & 0xff;
				PC += 2;
				break;

			case (0xd000):
				logToDebugger(
						"drw V[" + ((args & 0xF00) >> 8) + "],V[" + (byte) ((args & 0x0f0) >> 4) + "]," + (args & 0xf));
				// Draw
				// Tela 64*32
				// Timers.setCurrent(System.currentTimeMillis());

				short valX = (short) toUnsignedInt(regV[(args & 0xf00) >> 8]);
				short valY = (short) toUnsignedInt(regV[(args & 0xf0) >> 4]);
				int height = args & 0xf;
				regV[0xf] = 0;
				if (renderMode == 0) {
					for (int Y = 0; Y < height; Y++) {
						byte pixel = memory[I + Y];
						for (int X = 0; X < 8; X++) {

							byte num = (byte) ((pixel & (0x80 >>> X)));
							if (num != 0) {

								int screenRX = valX + X;
								int screenRY = valY + Y;
								// System.out.println(screenRX);
								// System.out.println(screenRY);
								int a = 0;
								if ((screenRX >= 64 || screenRY >= 32)) {
									if (Options.getInstance().isXWrappingEnabled()) {
										for (a = screenRX / 64; a > 0; a--) {
											screenRX -= 64;

										}
									}
									if (Options.getInstance().isXWrappingEnabled()) {
										for (a = screenRY / 32; a > 0; a--) {
											screenRY -= 32;
										}
									}
								}
								int index = (screenRX + ((screenRY) * 64));
//								if(num==1 && screen[index]==1)
//									regV[0xf] = screen[index];

								screen[index] ^= 1;
								if (screen[index] == 0 && regV[0xf] == 0)
									regV[0xf] = 1;
							}
						}
					}
					Graphics.Draw(screen, renderMode);
				} else if (renderMode == 1) {
					if (height == 0) {
						for (int Y = 0; Y < 16; Y++) {
							byte pixel = (memory[I + Y]);
							// byte pixel2 = (memory[I + Y + 1]);
							for (int X = 0; X < 16; X++) {

								byte num = (byte) ((pixel & (0x80 >>> X)));
								if (num != 0) {

									int screenRX = valX + X;
									int screenRY = valY + Y;
									// System.out.println(screenRX);
									// System.out.println(screenRY);
									int a = 0;
									if ((screenRX >= 128 || screenRY >= 64)) {
										if (Options.getInstance().isXWrappingEnabled()) {
											for (a = screenRX / 128; a > 0; a--) {
												screenRX -= 128;

											}
										}
										if (Options.getInstance().isXWrappingEnabled()) {
											for (a = screenRY / 64; a > 0; a--) {
												screenRY -= 64;
											}
										}
									}
									int index = (screenRX + ((screenRY) * 128));

//									if (HRScreen[index] == 1) {
//										regV[0xf] = 1;
//									}
									// Collision is 1, so its just gonna copy

									// regV[0xf] = HRScreen[index];
									HRScreen[index] ^= 1;
									if (HRScreen[index] == 0 && regV[0xf] == 0)
										regV[0xf] = 1;
								}
							}
						}
					} else {
						for (int Y = 0; Y < height; Y++) {
							byte pixel = memory[I + Y];
							for (int X = 0; X < 8; X++) {

								byte num = (byte) ((pixel & (0x80 >>> X)));
								if (num != 0) {

									int screenRX = valX + X;
									int screenRY = valY + Y;
									// System.out.println(screenRX);
									// System.out.println(screenRY);
									int a = 0;
									if ((screenRX >= 128 || screenRY >= 64)) {
										if (Options.getInstance().isXWrappingEnabled()) {
											for (a = screenRX / 128; a > 0; a--) {
												screenRX -= 128;

											}
										}
										if (Options.getInstance().isXWrappingEnabled()) {
											for (a = screenRY / 64; a > 0; a--) {
												screenRY -= 64;
											}
										}
									}
									int index = (screenRX + ((screenRY) * 128));

//									if (HRScreen[index] == 1) {
//										regV[0xf] = 1;
//									}
									// regV[0xf] = HRScreen[index];
									HRScreen[index] ^= 1;
									if (HRScreen[index] == 0 && regV[0xf] == 0)
										regV[0xf] = 1;

								}
							}
						}

					}

					Graphics.Draw(HRScreen, renderMode);
				}

				// Timers.setAfter(System.currentTimeMillis());
				PC += 2;
				break;

			case (0xe000):
				switch (args & 0xff) {
				case (0x9e):
					Xreg = (args & 0xf00) >> 8;
					if (Keys[regV[Xreg]]) {
						PC += 4;
					} else {
						PC += 2;
					}
					logToDebugger("skp V[" + Xreg + "]");
					break;
				case (0xa1):
					Xreg = (args & 0xf00) >> 8;
					logToDebugger("sknp V[" + Xreg + "]");
					if (!Keys[regV[Xreg]]) {
						PC += 4;
					} else {
						PC += 2;
					}
					break;
				default:
					System.out.println("Uknown opcode" + Integer.toHexString(id|args));
					break;

				}
				break;
			case (0xf000):
				int copyIndex;
				switch (args & 0xff) {
				case (0x07):
					Xreg = (args & 0xf00) >> 8;
					logToDebugger("unlddt V[" + Xreg + "]");
					regV[Xreg] = (byte) Timers.getDelayTimer();
					PC += 2;
					break;
				case (0x0a):
					Xreg = (args & 0xf00) >> 8;
					logToDebugger("waitkpld V[ " + Xreg + "]");
					if (keyIsPressed || Keyboard.getLastPressed() != -1) {
						regV[Xreg] = (byte) lastPressed;
						PC += 2;
					}
					break;
				case (0x15):
					Xreg = (args & 0xf00) >> 8;
					logToDebugger("lddtfreg V[" + Xreg + "]");
					Timers.setDelayTimer(regV[Xreg]);
					PC += 2;
					break;
				case (0x18):
					Xreg = (args & 0xf00) >> 8;
					logToDebugger("ldstfreg V[" + Xreg + "]");
					Timers.setSoundTimer(regV[Xreg]);
					PC += 2;
					break;
				case (0x1e):
					Xreg = (args & 0xf00) >> 8;
					logToDebugger("addi V[" + Xreg + "]");
					I += toUnsignedInt(regV[Xreg]);
					PC += 2;
					break;
				case (0x29):
					Xreg = (args & 0xf00) >> 8;
//				I = charAddress[regV[Xreg]];
					logToDebugger("ldspr V[" + Xreg + "]");
					I = (short) toUnsignedInt((byte) (regV[Xreg] * 0x5));
					PC += 2;
					break;
				case (0x30):
					// error("Not yet Implemented 0x" + Integer.toHexString(opcode));
					Xreg = (args & 0xf00) >> 8;
//				I = charAddress[regV[Xreg]];
					logToDebugger("ldspr V[" + Xreg + "]");
					I = (short) toUnsignedInt((byte) (regV[Xreg] * 0xA));
					PC += 2;
					break;
				case (0x33):
					Xreg = (args & 0xf00) >> 8;
					short valueTobcd = (short) toUnsignedInt(regV[Xreg]);
					logToDebugger("BCD V[" + Xreg + "]");
					memory[I] = (byte) (valueTobcd / 100);
					memory[I + 1] = (byte) ((valueTobcd / 10) % 10);
					memory[I + 2] = (byte) ((valueTobcd % 100) % 10);
					PC += 2;
					break;
				case (0x55):
					copyIndex = (args & 0xf00) >> 8;
					logToDebugger("rgdump [" + I + "],V[" + copyIndex + "]");

//				for (int i = 0; i <= copyIndex; i++) {
//					memory[I + i] = regV[i];
//				}
					System.arraycopy(regV, 0, memory, I, copyIndex + 1);
					PC += 2;
					break;
				case (0x65):
					copyIndex = (args & 0xf00) >> 8;
					logToDebugger("memdump V[" + copyIndex + "]," + copyIndex + "");
//				for (int i = 0; i <= copyIndex; i++) {
//					regV[i] = memory[I + i];
//				}
					System.arraycopy(memory, I + 0, regV, 0, copyIndex + 1);
					PC += 2;
					break;
				default:
					System.out.println("Uknown opcode" + Integer.toHexString(id|args));
					break;
				}

				break;
			default:
				System.out.println("Uknown opcode" + Integer.toHexString(id|args));
				break;

			}
//			if (id == 0xf000) {
//				int copyIndex;
//				switch (args & 0xff) {
//				case (0x07):
//					Xreg = (args & 0xf00) >> 8;
//					logToDebugger("unlddt V[" + Xreg + "]");
//					regV[Xreg] = (byte) Timers.getDelayTimer();
//					PC += 2;
//					break;
//				case (0x0a):
//					Xreg = (args & 0xf00) >> 8;
//					logToDebugger("waitkpld V[ " + Xreg + "]");
//					if (keyIsPressed || Keyboard.getLastPressed() != -1) {
//						regV[Xreg] = (byte) lastPressed;
//						PC += 2;
//					}
//					break;
//				case (0x15):
//					Xreg = (args & 0xf00) >> 8;
//					logToDebugger("lddtfreg V[" + Xreg + "]");
//					Timers.setDelayTimer(regV[Xreg]);
//					PC += 2;
//					break;
//				case (0x18):
//					Xreg = (args & 0xf00) >> 8;
//					logToDebugger("ldstfreg V[" + Xreg + "]");
//					Timers.setSoundTimer(regV[Xreg]);
//					PC += 2;
//					break;
//				case (0x1e):
//					Xreg = (args & 0xf00) >> 8;
//					logToDebugger("addi V[" + Xreg + "]");
//					I += toUnsignedInt(regV[Xreg]);
//					PC += 2;
//					break;
//				case (0x29):
//					Xreg = (args & 0xf00) >> 8;
////					I = charAddress[regV[Xreg]];
//					logToDebugger("ldspr V[" + Xreg + "]");
//					I = (short) toUnsignedInt((byte) (regV[Xreg] * 0x5));
//					PC += 2;
//					break;
//				case (0x30):
//					// error("Not yet Implemented 0x" + Integer.toHexString(opcode));
//					Xreg = (args & 0xf00) >> 8;
////					I = charAddress[regV[Xreg]];
//					logToDebugger("ldspr V[" + Xreg + "]");
//					I = (short) toUnsignedInt((byte) (regV[Xreg] * 0xA));
//					PC += 2;
//					break;
//				case (0x33):
//					Xreg = (args & 0xf00) >> 8;
//					short valueTobcd = (short) toUnsignedInt(regV[Xreg]);
//					logToDebugger("BCD V[" + Xreg + "]");
//					memory[I] = (byte) (valueTobcd / 100);
//					memory[I + 1] = (byte) ((valueTobcd / 10) % 10);
//					memory[I + 2] = (byte) ((valueTobcd % 100) % 10);
//					PC += 2;
//					break;
//				case (0x55):
//					copyIndex = (args & 0xf00) >> 8;
//					logToDebugger("rgdump [" + I + "],V[" + copyIndex + "]");
//
////					for (int i = 0; i <= copyIndex; i++) {
////						memory[I + i] = regV[i];
////					}
//					System.arraycopy(regV, 0, memory, I, copyIndex + 1);
//					PC += 2;
//					break;
//				case (0x65):
//					copyIndex = (args & 0xf00) >> 8;
//					logToDebugger("memdump V[" + copyIndex + "]," + copyIndex + "");
////					for (int i = 0; i <= copyIndex; i++) {
////						regV[i] = memory[I + i];
////					}
//					System.arraycopy(memory, I + 0, regV, 0, copyIndex + 1);
//					PC += 2;
//					break;
//				default:
//					break;
//				}
//			}

		}

	}

}
