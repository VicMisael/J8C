package j8c;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

import j8c.Core.CPU;
import j8c.Core.Keyboard;
import j8c.Core.RomLoader;
import javax.swing.event.MenuListener;
import javax.swing.event.MenuEvent;

public class MainGUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private final JMenuBar menuBar = new JMenuBar();
	private final JMenu FileMenu = new JMenu("File");
	private final JMenu mnNewMenu = new JMenu("Emulation");
	private final JMenuItem mntmNewMenuItem_1 = new JMenuItem("Stop");
	private final JMenuItem mntmNewMenuItem_2 = new JMenuItem("Start");
	private final JMenuItem mntmNewMenuItem = new JMenuItem("Load Rom");
	private static final Canvas canvas = new Canvas();
	private final JMenuItem mntmPause = new JMenuItem("Pause");
	private final JMenuItem mntmDebugger = new JMenuItem("Debugger");
	private final JMenuItem mntmOptions = new JMenuItem("Options");
	private final JMenu mnAbout = new JMenu("About");

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

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

	public MainGUI() throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
		setResizable(false);
		
		setTitle("JC8");
		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		initComponents();
		

	}

	private void initComponents() {
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
				setTitle(RomLoader.getInstance().getRomName());
				startEmulation();

			}
		});

		mnNewMenu.add(mntmNewMenuItem_2);
		mntmNewMenuItem_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setTitle("JC8");
				stopEmulation();
			}
		});

		mnNewMenu.add(mntmNewMenuItem_1);
		mntmPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				pauseEmulation();
			}
		});

		mnNewMenu.add(mntmPause);
		mntmDebugger.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Debugger.getInstance().setVisible(true);
				
			}
		});

		mnNewMenu.add(mntmDebugger);
		mntmOptions.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				OptionsMenu.getOptionsMenu().setVisible(true);
			}
		});
		
		mnNewMenu.add(mntmOptions);
		mnAbout.addMenuListener(new MenuListener() {
			public void menuCanceled(MenuEvent e) {
			}
			public void menuDeselected(MenuEvent e) {
			}
			public void menuSelected(MenuEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							AboutPage frame = new AboutPage();
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		mnAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							AboutPage frame = new AboutPage();
							frame.setVisible(true);
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
		
		menuBar.add(mnAbout);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		canvas.setBackground(Color.WHITE);
		canvas.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent arg0) {
				int kpressed = -1;
				if (Character.isDigit(arg0.getKeyChar())) {
					kpressed = Character.getNumericValue(arg0.getKeyChar());

				} else {
					switch (Character.toUpperCase(arg0.getKeyChar())) {
					case 'A':
						kpressed = 0xa;
						break;
					case 'B':
						kpressed = 0xb;
						break;

					case 'C':
						kpressed = 0xC;
						break;
					case 'D':
						kpressed = 0xD;
						break;
					case 'E':
						kpressed = 0xE;
						break;
					case 'F':
						kpressed = 0xF;
						break;

					}

				}
				if (kpressed != -1) {
					CPU.getInstance().PressedKeyInterrupt();
					System.out.println(kpressed);
					Keyboard.setKeyPressedAtIndex(kpressed);
				}

			}

			public void keyReleased(KeyEvent e) {
				int kpressed = -1;
				if (Character.isDigit(e.getKeyChar())) {
					kpressed = Character.getNumericValue(e.getKeyChar());
				} else {
					switch (Character.toUpperCase(e.getKeyChar())) {
					case 'A':
						kpressed = 0xa;
						break;
					case 'B':
						kpressed = 0xb;
						break;
					case 'C':
						kpressed = 0xC;
						break;
					case 'D':
						kpressed = 0xD;
						break;
					case 'E':
						kpressed = 0xE;
						break;
					case 'F':
						kpressed = 0xF;
						break;

					}

				}
				if (kpressed != -1) {
					CPU.getInstance().unpressedKeyInterrupt();
					System.out.println(kpressed);
					Keyboard.setKeyUnpressedAtIndex(kpressed);
				}
			}
		});

		contentPane.add(canvas, BorderLayout.CENTER);
	}

	public static void startEmulation() {

		canvas.setBackground(new Color(0, 0, 0));

		CPU.getInstance().init();

	}

	public static void drawToCanvas(byte[] screen) {
		// y=32Pixels,X=64 pixels, CanvasX=384 canvasY=192
		if (canvas.getBufferStrategy() == null) {
			canvas.createBufferStrategy(2);
		}

		BufferedImage bimage = new BufferedImage(64, 32, BufferedImage.TYPE_INT_RGB);
		int value = 0;
		for (int y = 0; y < 32; y++) {
			for (int x = 0; x < 64; x++) {
				value = screen[x + y * 64] == 1 ? 0xffffff : 0x0;	
			

				bimage.setRGB(x, y, value);
			}
		}

		canvas.getGraphics().drawImage(bimage.getScaledInstance(canvas.getWidth(), canvas.getHeight(), 0), 0, 0, null);

	}

	public static void stopEmulation() {
		CPU.getInstance().stopCPU();

	}

	public static void pauseEmulation() {
		CPU.getInstance().pauseCPU();
	}

	public void loadGame() {
		// System.out.println(canvas.getWidth() + " " + canvas.getHeight());
		RomLoader.getInstance().resetRom();

		JFileChooser fileChooser = new JFileChooser();
		int returnValue = fileChooser.showOpenDialog(fileChooser);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			RomLoader.getInstance().setRom(file);
		}
	}

}
