package j8c;


import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import j8c.Core.Options;

import javax.swing.JCheckBox;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class OptionsMenu extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	static OptionsMenu optmen;

	public static OptionsMenu getOptionsMenu() {
		if(optmen==null)
			optmen=new OptionsMenu();
		return optmen;
	}

	/**
	 * Create the frame.
	 */
	public OptionsMenu() {
		setResizable(false);
		setAutoRequestFocus(false);
		setTitle("Options");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 171, 222);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JCheckBox chkboxEnYWrap = new JCheckBox("Enable Y Wrapping");
		chkboxEnYWrap.setSelected(Options.getInstance().isYWrappingEnabled());
		chkboxEnYWrap.setBounds(6, 23, 131, 23);
		contentPane.add(chkboxEnYWrap);

		JCheckBox chkboxEnXWrap = new JCheckBox("Enable X Wrapping");
		chkboxEnXWrap.setSelected(Options.getInstance().isXWrappingEnabled());
		chkboxEnXWrap.setBounds(6, 0, 131, 23);
		contentPane.add(chkboxEnXWrap);

		JButton btnSave = new JButton("Save");
		btnSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Options.getInstance().setXWrapping(chkboxEnXWrap.isSelected());
				Options.getInstance().setYWrapping(chkboxEnYWrap.isSelected());
			}
		});
		btnSave.setBounds(34, 159, 89, 23);
		contentPane.add(btnSave);
	}
}
