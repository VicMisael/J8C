package j8c;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

public class Debugger extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private static Debugger deb;
	private JTable table;
	private boolean isOpen=false;
	private JTextField sleepTime;
	private int sleepTimeVar = 1;

	/**
	 * Launch the application.
	 */

	/**
	 * Create the frame.
	 */
	private Debugger() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosed(WindowEvent e) {
				isOpen=false;
			}
			@Override
			public void windowOpened(WindowEvent e) {
				isOpen=true;
			}
		});

		setAutoRequestFocus(false);
		setResizable(false);
		setType(Type.UTILITY);
		setTitle("Debugger");

		setBackground(Color.WHITE);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 479, 343);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(0, 0, 157, 292);
		contentPane.add(scrollPane);

		table = new JTable();
		table.setFillsViewportHeight(true);
		table.setEnabled(false);
		scrollPane.setViewportView(table);
		table.setRowSelectionAllowed(false);
		table.setModel(new DefaultTableModel(new Object[][] { { "V[0]", "" }, { "V[1]", null }, { "V[2]", null },
				{ "V[3]", null }, { "V[4]", null }, { "V[5]", null }, { "V[6]", null }, { "V[7]", null },
				{ "V[8]", null }, { "V[9]", null }, { "V[10]", null }, { "V[11]", null }, { "V[12]", null },
				{ "V[13]", null }, { "V[14]", null }, { "V[15]", null }, { "I", null }, },
				new String[] { "Registers", "Values" }));

		sleepTime = new JTextField();
		sleepTime.setText("1");
		sleepTime.setBounds(167, 236, 86, 20);
		contentPane.add(sleepTime);
		sleepTime.setColumns(10);

		JButton updateSleep = new JButton("Update");
		updateSleep.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String timer = sleepTime.getText();
				if (!timer.isEmpty()) {
					if (timer.matches("[0-9]+")) {
						sleepTimeVar = Integer.valueOf(timer);
					}else {
						JOptionPane.showMessageDialog(Debugger.getInstance(), "You should use only numbers", "JC8",
								JOptionPane.ERROR_MESSAGE);
						sleepTime.setText("1");
					}

				} else {
					JOptionPane.showMessageDialog(Debugger.getInstance(), "This field may not be empty", "JC8",
							JOptionPane.ERROR_MESSAGE);
					sleepTime.setText("1");
				}

			}
		});
		updateSleep.setBounds(167, 269, 89, 23);
		contentPane.add(updateSleep);

	}

	public static Debugger getInstance() {
		if (deb == null) {
			deb = new Debugger();
		}
		return deb;
	}

	public static boolean isDebuggerStarted() {
		if (deb == null) {
			return false;

		} else {
			return Debugger.getInstance().isOpen;
		}

	}
	public int getSleepTimer() {
		return this.sleepTimeVar;
	}
	public void updateRegisters(byte[] Registers, int I) {
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		for (int a = 0; a < model.getRowCount(); a++) {
			if (a < 16) {
				model.setValueAt(Byte.toUnsignedInt(Registers[a]), a, 1);
			} else {
				model.setValueAt(I, a, 1);
			}
		}

	}
}
