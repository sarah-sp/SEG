package gui.custom;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JLabel;
import javax.swing.JPanel;

import gui.HelpFrame;
import gui.MyFrame;
import model.Theme;

public class CustomPanel extends JPanel 
{
	private static final long serialVersionUID = 1L;
	private MyFrame frame;
	private JPanel panel;
	private JPanel topPanel;
	private JLabel logo1;
	
	public CustomPanel(MyFrame frame, JPanel panel, String panelName)
	{
		this.setBackground(Theme.ACTIVE_BG);
		this.frame = frame;
		this.panel = panel;
		
		setBounds(getBounds());
		setBackground(Theme.ACTIVE_BG);
		setLayout(new BorderLayout());
		
		topPanel = new JPanel();
		topPanel.setLayout(new BorderLayout());
		
		logo1 = new JLabel(panelName, JLabel.CENTER);
		logo1.setFont(new Font("Above DEMO", Font.BOLD,60));
		
		PictureButton helpButton = new PictureButton("question", "Help");
		PictureButton backButton = new PictureButton("arrow", "Back");
		
		helpButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				HelpFrame hf = new HelpFrame(panelName);
				hf.init();
				
			}
			
		});
		
		backButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				displayMainMenu();			
			}
			
		});

		
		topPanel.add(logo1, BorderLayout.CENTER);
		topPanel.add(helpButton, BorderLayout.EAST);
		topPanel.add(backButton, BorderLayout.WEST);
		topPanel.setBackground(Theme.ACTIVE_BG);
		
		add(topPanel, BorderLayout.NORTH);
		
		if (panelName.equals("compareCampaigns"))
		{	
			addNotYetImplementedLabel();
		}	
	}

	public MyFrame getFrame()
	{
		return frame;
	}
	
	public JPanel getPanel()
	{
		return panel;
	}
	
	public JPanel getContentPane()
	{
		return this.getContentPane();
	}
	
	public void displayMainMenu()
	{
		frame.refreshMain();
	}
	
	public void addNotYetImplementedLabel()
	{
		Font font = new Font("Courier", Font.BOLD,32);
		
		JLabel notImplemented = new JLabel("Not yet implemented");
		notImplemented.setFont(font);
		notImplemented.setForeground(Color.RED);
		
		JPanel nyi = new JPanel();
		nyi.setLayout(new GridBagLayout());
		nyi.setBackground(Theme.ACTIVE_BG);
		
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
					nyi.add(filler, c);
				}
			}
		}

		c.gridx = 1;
		c.gridy = 1;
		c.fill = GridBagConstraints.BOTH;
		nyi.add(notImplemented, c);
		
		add(nyi, BorderLayout.CENTER);
	}
}