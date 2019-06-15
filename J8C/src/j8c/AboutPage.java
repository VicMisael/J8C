package j8c;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Color;

public class AboutPage extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	

	/**
	 * Create the frame.
	 */
	public AboutPage() {
		setBackground(Color.WHITE);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBackground(Color.WHITE);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblNewLabel = new JLabel("Created by an aspiring programmer who loves emulators");
		lblNewLabel.setBounds(10, 31, 414, 24);
		contentPane.add(lblNewLabel);
		
		JLabel label = new JLabel("https://github.com/VicMisael");
		label.setBounds(10, 52, 150, 24);
		contentPane.add(label);
		
		JLabel lblSystemInfo = new JLabel("System info:");
		lblSystemInfo.setBounds(10, 124, 150, 24);
		contentPane.add(lblSystemInfo);
		
		JLabel lblBrazil = new JLabel("Brazil, 2019");
		lblBrazil.setBounds(10, 76, 83, 14);
		contentPane.add(lblBrazil);
		String system="<html><body>"+System.getProperty("os.name")+" "+ System.getProperty("os.version") + "<br>"
		+"Running on JRE: "+System.getProperty("java.version")+"<br>"+System.getProperty("java.vendor")+"<br>"
				+ System.getenv("PROCESSOR_IDENTIFIER") + "<br>"+System.getProperty("os.arch")+"</body></html>";
		
		
		
		JLabel Sysinfo = new JLabel(system);
		Sysinfo.setBounds(10, 140, 378, 99);
		contentPane.add(Sysinfo);
		
		JLabel lblYouCanMake = new JLabel("You can make free use of this code, it has no license");
		lblYouCanMake.setBounds(10, 86, 414, 24);
		contentPane.add(lblYouCanMake);
	}
}
