package j8c;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.List;
import java.awt.Polygon;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import j8c.Core.CPU;
import j8c.Core.Keyboard;
import j8c.Core.RomLoader;
import j8c.Exceptions.ParametersNotSetException;

import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import java.awt.event.ActionListener;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.UIManager;

public class MainGUI extends JFrame {

	private JPanel contentPane;
	private final JMenuBar menuBar = new JMenuBar();
	private final JMenu FileMenu = new JMenu("File");
	private final JMenu mnNewMenu = new JMenu("Emulation");
	private final JMenuItem mntmNewMenuItem_1 = new JMenuItem("Stop");
	private final JMenuItem mntmNewMenuItem_2 = new JMenuItem("Start");
	private final JMenuItem mntmNewMenuItem = new JMenuItem("Load Rom");
	private static final Canvas canvas = new Canvas();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		try {
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
		} catch (Throwable e) {
			e.printStackTrace();
		}

		try {
			new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
		} catch (InterruptedException | IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainGUI frame = new MainGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public MainGUI() {
		initComponents();

	}

	private void initComponents() {
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 410, 262);

		setJMenuBar(menuBar);

		menuBar.add(FileMenu);
		mntmNewMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loadGame();
			}
		});

		FileMenu.add(mntmNewMenuItem);

		menuBar.add(mnNewMenu);
		mntmNewMenuItem_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				startEmulation();

			}
		});

		mnNewMenu.add(mntmNewMenuItem_2);
		mntmNewMenuItem_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				stopEmulation();
			}
		});

		mnNewMenu.add(mntmNewMenuItem_1);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		canvas.setBackground(Color.WHITE);
		canvas.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent arg0) {
//				int kpressed = -1;
//				if (Character.isDigit(arg0.getKeyChar())) {
//					kpressed = Character.getNumericValue(arg0.getKeyChar());
//
//				} else {
//					switch (Character.toUpperCase(arg0.getKeyChar())) {
//					case 'A':
//						kpressed = 0xa;
//						break;
//					case 'B':
//						kpressed = 0xb;
//						break;
//
//					case 'C':
//						kpressed = 0xC;
//						break;
//					case 'D':
//						kpressed = 0xD;
//						break;
//					case 'E':
//						kpressed = 0xE;
//						break;
//					case 'F':
//						kpressed = 0xF;
//						break;
//
//					}
//
//				}
//				if (kpressed != -1) {
//					System.out.println(kpressed);
//					Keyboard.setKeyPressedAtIndex(kpressed);
//				}

			}

			public void keyReleased(KeyEvent e) {
//				int kpressed = -1;
//				if (Character.isDigit(e.getKeyChar())) {
//					kpressed = Character.getNumericValue(e.getKeyChar());
//				} else {
//					switch (Character.toUpperCase(e.getKeyChar())) {
//					case 'A':
//						kpressed = 0xa;
//						break;
//					case 'B':
//						kpressed = 0xb;
//						break;
//					case 'C':
//						kpressed = 0xC;
//						break;
//					case 'D':
//						kpressed = 0xD;
//						break;
//					case 'E':
//						kpressed = 0xE;
//						break;
//					case 'F':
//						kpressed = 0xF;
//						break;
//
//					}
//
//				}
//				if (kpressed != -1) {
//					System.out.println(kpressed);
//					Keyboard.setKeyUnpressedAtIndex(kpressed);
//				}
			}
		});

		contentPane.add(canvas, BorderLayout.CENTER);
	}

	Thread ScreenThread;

	public static void startEmulation() {

		canvas.setBackground(new Color(0, 0, 0));

		CPU.getInstance().init();
		JSFMLScreen.getInstance().setParamJSFML(RomLoader.getInstance().getRomName());
		JSFMLScreen.getInstance().start();

	}

	public static void drawToCanvas(byte[] screen) {

		Graphics graphics = (Graphics2D) canvas.getGraphics();
		graphics.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
		graphics.setColor(Color.white);
		int width = (int) canvas.getSize().getWidth();
		int height = (int) canvas.getSize().getHeight();
		int y, x = 0;
		for (y = 0; y < 32; y++) {
			for (x = 0; x < 64; x++) {
				byte screenByte = screen[x + y * 64];
				if (screenByte == 0) {

				} else {

					graphics.fillRect(x * 6, y * 6, 6, 6);
					// p.addPoint(x * 6, y * 6);
				}
			}

		}

		canvas.paint(graphics);

	}

	public static void stopEmulation() {
		CPU.getInstance().stopCPU();

	}

	public void loadGame() {
		System.out.println(canvas.getWidth() + " " + canvas.getHeight());
		RomLoader.getInstance().resetRom();
		String url = "";
		JFileChooser fileChooser = new JFileChooser();
		int returnValue = fileChooser.showOpenDialog(fileChooser);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			RomLoader.getInstance().setRom(file);
		}
	}

}
