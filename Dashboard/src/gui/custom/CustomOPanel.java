package gui.custom;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import model.Theme;

public class CustomOPanel extends JPanel 
{
	private static final long serialVersionUID = 1L;
	
	private String picName;
	
	public CustomOPanel(String picName)
	{
		this.picName = picName;
		init();
	}
	
	public void init()
	{
		setBounds(getBounds());
		setBackground(Theme.ACTIVE_BG);
		setLayout(new BorderLayout());
		
		JLabel logo1 = new JLabel(picName, JLabel.CENTER);
		logo1.setFont(new Font("Above DEMO", Font.BOLD,40));
		add(logo1, BorderLayout.NORTH);
	}
}
