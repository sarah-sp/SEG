package gui;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import gui.custom.CustomOPanel;
import model.Theme;

public class GraphOPanel extends CustomOPanel 
{
	private static final long serialVersionUID = 1L;
	
	public GraphOPanel()
	{
		super("Graphs");

		Font font = new Font("Courier", Font.BOLD,32);
		
		JLabel label = new JLabel("<html><font face=Courier size=17><center>Create Metrics and Enter the</center><br><center>Advanced Graphs Section to View Graphs</center></html>");
		label.setFont(font);
		label.setForeground(Theme.ACTIVE_FG);
		
		JPanel centre = new JPanel();
		JPanel logoPanel = new JPanel();
		centre.setLayout(new GridBagLayout());
		centre.setBackground(Theme.ACTIVE_BG);
		logoPanel.setLayout(new GridBagLayout());
		logoPanel.setBackground(Theme.ACTIVE_BG);
		
		JPanel filler = new JPanel();
		filler.setBackground(Theme.ACTIVE_BG);
		
		GridBagConstraints c = new GridBagConstraints();

		for (int i = 0; i < 2; i++)
		{
			for (int j = 0; j < 2; j++)
			{
				if (i != 1 && j != 1)
				{
					c.gridx = i;
					c.gridy = j;
					c.fill = GridBagConstraints.BOTH;
					centre.add(filler, c);
					logoPanel.add(filler, c);
				}
			}
		}

		c.gridx = 1;
		c.gridy = 1;
		c.fill = GridBagConstraints.BOTH;
		centre.add(label, c);
		
		JLabel logo = new JLabel("Graphs", JLabel.CENTER);
		logo.setFont(new Font("Above DEMO", Font.BOLD,40));
		
		logoPanel.add(logo, c);
		setBorder(new EmptyBorder(15,15,15,15));		
		add(logoPanel, BorderLayout.NORTH);
		add(centre, BorderLayout.CENTER);
	}
}