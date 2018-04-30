package gui;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import model.Theme;

public class MainOPanel extends JPanel 
{
	private static final long serialVersionUID = 1L;
	
	public MainOPanel()
	{
		Font font = new Font("Above DEMO", Font.BOLD,100);
		
		JLabel label = new JLabel("Dash");
		label.setFont(font);
		label.setForeground(Theme.ACTIVE_FG);
		
		setBackground(Theme.ACTIVE_BG);
		setLayout(new GridBagLayout());
		
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
					add(filler, c);
				}
			}
		}
	
		c.gridx = 1;
		c.gridy = 1;
		c.fill = GridBagConstraints.BOTH;
		
		add(label, c);
	}
}
