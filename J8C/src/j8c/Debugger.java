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
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JProgressBar;
import javax.swing.JCheckBox;

public class Debugger extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private static Debugger deb;
	private JTable registerTable;
	private boolean isOpen = false;
	private JTextField sleepTime;
	private int sleepTimeVar = 1;
	private JTable stack;
	private JLabel operation;
	private JProgressBar stackSize;
	private JCheckBox chckbxShowInHex;
	private JLabel lblPc;
	private JLabel programCounter;

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
				isOpen = false;
			}

			@Override
			public void windowOpened(WindowEvent e) {
				isOpen = true;
			}
		});

		setAutoRequestFocus(false);
		setResizable(false);
		setType(Type.UTILITY);
		setTitle("Debugger");

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 479, 384);
		contentPane = new JPanel();

		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(0, 0, 157, 300);
		contentPane.add(scrollPane);

		registerTable = new JTable();
		registerTable.setShowVerticalLines(true);
		registerTable.setShowHorizontalLines(true);
		registerTable.setFillsViewportHeight(true);
		scrollPane.setViewportView(registerTable);
		registerTable.setRowSelectionAllowed(false);
		registerTable.setModel(new DefaultTableModel(new Object[][] { { "V[0]", "" }, { "V[1]", null },
				{ "V[2]", null }, { "V[3]", null }, { "V[4]", null }, { "V[5]", null }, { "V[6]", null },
				{ "V[7]", null }, { "V[8]", null }, { "V[9]", null }, { "V[10]", null }, { "V[11]", null },
				{ "V[12]", null }, { "V[13]", null }, { "V[14]", null }, { "V[15]", null }, { "I", null }, },
				new String[] { "Registers", "Values" }));

		sleepTime = new JTextField();
		sleepTime.setText("1");
		sleepTime.setBounds(169, 31, 54, 20);
		contentPane.add(sleepTime);
		sleepTime.setColumns(10);

		JButton updateSleep = new JButton("Update");
		updateSleep.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String timer = sleepTime.getText();
				if (!timer.isEmpty()) {
					if (timer.matches("[0-9]+")) {
						sleepTimeVar = Integer.valueOf(timer);
					} else {
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
		updateSleep.setBounds(168, 62, 73, 23);
		contentPane.add(updateSleep);

		JLabel lblCpuThreadSleep = new JLabel("CPU Sleep time");
		lblCpuThreadSleep.setFont(new Font("Tahoma", Font.PLAIN, 9));
		lblCpuThreadSleep.setBounds(169, 6, 72, 14);
		contentPane.add(lblCpuThreadSleep);

		JLabel lblMs = new JLabel("ms");
		lblMs.setBounds(227, 34, 19, 14);
		contentPane.add(lblMs);

		JScrollPane scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(349, 6, 118, 219);
		contentPane.add(scrollPane_1);

		stack = new JTable();
		stack.setModel(new DefaultTableModel(
				new Object[][] { { null }, { null }, { null }, { null }, { null }, { null }, { null }, { null },
						{ null }, { null }, { null }, { null }, { null }, { null }, { null }, { null }, },
				new String[] { "Stack" }));
		stack.getColumnModel().getColumn(0).setPreferredWidth(94);
		stack.setShowHorizontalLines(true);
		scrollPane_1.setViewportView(stack);

		JLabel StacklastOp = new JLabel("LastOp:");
		StacklastOp.setBounds(349, 224, 54, 15);
		contentPane.add(StacklastOp);

		operation = new JLabel("null");
		operation.setBounds(408, 224, 53, 15);
		contentPane.add(operation);

		stackSize = new JProgressBar();
		stackSize.setValue(10);
		stackSize.setMaximum(15);
		stackSize.setBounds(349, 237, 118, 20);
		contentPane.add(stackSize);

		chckbxShowInHex = new JCheckBox("Show In HEX");
		chckbxShowInHex.setBounds(10, 307, 97, 23);
		contentPane.add(chckbxShowInHex);

		lblPc = new JLabel("PC:");
		lblPc.setBounds(253, 7, 33, 15);
		contentPane.add(lblPc);

		programCounter = new JLabel("PC");
		programCounter.setBackground(Color.WHITE);
		programCounter.setBounds(275, 7, 53, 15);
		contentPane.add(programCounter);

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

	public void updateRegisters(byte[] Registers, short I, int[] Stack, String lastStackOp, int pointer, int PC) {
		DefaultTableModel model = (DefaultTableModel) registerTable.getModel();

		if (chckbxShowInHex.isSelected()) {
			for (int a = 0; a < model.getRowCount(); a++) {
				if (a < 16) {
					model.setValueAt("0x" + Integer.toHexString(Byte.toUnsignedInt(Registers[a])), a, 1);
				} else {
					model.setValueAt("0x" + Integer.toHexString(I), a, 1);
				}
			}
		} else {
			for (int a = 0; a < model.getRowCount(); a++) {

				if (a < 16) {
					model.setValueAt(Byte.toUnsignedInt(Registers[a]), a, 1);
				} else {
					model.setValueAt(Short.toUnsignedInt(I), a, 1);
				}
			}
		}

		model = (DefaultTableModel) stack.getModel();
		for (int b = 0; b < model.getRowCount(); b++) {
			if (chckbxShowInHex.isSelected()) {
				model.setValueAt("0x" + Integer.toHexString(Stack[b]), b, 0);
			} else {
				model.setValueAt(Stack[b], b, 0);
			}
		}
		programCounter.setText(chckbxShowInHex.isSelected() ? "0x" + Integer.toHexString(PC) : Integer.toString(PC));
		operation.setText(lastStackOp);
		stackSize.setValue(pointer);

	}
}
