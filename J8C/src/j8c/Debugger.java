package j8c;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Window.Type;
import javax.swing.JTable;
import javax.swing.JList;
import java.awt.List;
import javax.swing.ListSelectionModel;
import java.awt.Color;
import javax.swing.table.DefaultTableModel;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

public class Debugger extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private static Debugger deb;
	private JTable table;

	/**
	 * Launch the application.
	 */

	/**
	 * Create the frame.
	 */
	private Debugger() {
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
			return deb.isActive();
		}

	}

	public void updateRegisters(byte[] Registers, int I) {
		DefaultTableModel model = (DefaultTableModel) table.getModel();
		for (int a = 0; a < model.getRowCount(); a++) {
			if (a < 16) {
				model.setValueAt(Registers[a], a, 1);
			}
			else {
				model.setValueAt(I, a, 1);
			}
		}

	}

}
