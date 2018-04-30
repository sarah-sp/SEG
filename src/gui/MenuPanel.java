package gui;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import gui.custom.CustomButton;
import gui.custom.PictureButton;
import model.CompareStorage;
import model.FilterStorage;
import model.Theme;

public class MenuPanel extends JPanel 
{
	private static final long serialVersionUID = 1L;
	private MyFrame frame;
	private JPanel contentPanel;
	private MetricPanel metricPanel;
	private GraphPanel graphPanel;
	private ComparePanel comparePanel;
	private SettingsPanel settingsPanel;
	private JPanel cards;
	private Font font;
	private JScrollPane scroll;
	
	public MenuPanel(MyFrame frame, JPanel contentPanel, JPanel cards, FilterStorage fStorage, CompareStorage cStorage)
	{
		this.frame = frame;
		this.contentPanel = contentPanel;
		this.cards = cards;
		
		font = new Font("Courier", Font.BOLD, 20);
		
		metricPanel = new MetricPanel(frame, contentPanel, fStorage);
		graphPanel = new GraphPanel(frame, contentPanel);
		comparePanel = new ComparePanel(frame, contentPanel, cStorage);
		settingsPanel = new SettingsPanel(frame, contentPanel);
		
		init();
		
	}

	public void changeTheme(Container c, Color bg, Color fg, Color hover){
		c.setBackground(bg);
		c.setForeground(fg);
		UIManager.put("OptionPane.background", Theme.ACTIVE_BG);
		UIManager.put("OptionPane.messageForeground", Theme.ACTIVE_FG);
		UIManager.put("Panel.background", Theme.ACTIVE_BG);
		
		if(c instanceof CustomButton) {
			if(c.getName() != null) {
				if(c.getName().equals("bg")){
					c.setBackground(bg);
				} else if(c.getName().equals("fg")){
			
					c.setBackground(fg);
				} else if (c.getName().equals("hover")){
					c.setBackground(hover);
				} else{
					((CustomButton) c).changeHover(bg, hover);
				}
			} else {
				((CustomButton) c).changeHover(bg, hover);
			}
		}
		
		if(c instanceof PictureButton){
			try {
				((PictureButton) c).changeTheme(fg, hover);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if(c instanceof BorderInterface) {
			((BorderInterface) c).refreshBorder(fg);
		}
		
		for(Component comp : c.getComponents()) {
			
			comp.setBackground(bg);
			comp.setForeground(fg);

			if(comp instanceof BorderInterface) {
				((BorderInterface) comp).refreshBorder(fg);
			}
			
			if(comp instanceof MetricTable) {
				((MetricTable) comp).setHoverColor(hover);
				((MetricTable) comp).getTableHeader().setBackground(bg);
				((MetricTable) comp).getTableHeader().setForeground(fg);
			}
			
			
			if(comp instanceof CustomButton) {
				if(comp.getName() != null) {
					if(comp.getName().equals("bg")){
						
						comp.setBackground(bg);
						((CustomButton) comp).changeHover(bg, bg);
						((CustomButton) comp).refreshBorder(hover);
					} else if(comp.getName().equals("fg")){
						
						comp.setBackground(fg);
						((CustomButton) comp).changeHover(fg, fg);
						((CustomButton) comp).refreshBorder(hover);
						
					} else if(comp.getName().equals("hover")){
						((CustomButton) comp).changeHover(hover, hover);
						((CustomButton) comp).refreshBorder(hover);
						comp.setBackground(hover);
					} 
				} else {
					((CustomButton) comp).changeHover(bg, hover);
				}
			} 
				
			if(comp instanceof Container) {
				changeTheme((Container) comp,bg,fg,hover);
			}
			comp.revalidate();
			comp.repaint();
		}
	
	}
	
	public MetricPanel getMetricPanel()
	{
		return metricPanel;
	}
	
	public GraphPanel getGrahpPanel()
	{
		return graphPanel;
	}
	
	public void init()
	{	
		UIManager.put("OptionPane.background", Theme.ACTIVE_BG);
		UIManager.put("Panel.background", Theme.ACTIVE_BG);
	    UIManager.put("OptionPane.messageFont", font);
	    UIManager.put("OptionPane.buttonFont", font.deriveFont(14.0f));
	    
		setLayout(new BorderLayout());
		setBounds(getBounds());
		setBackground(Theme.ACTIVE_BG);
			
		JPanel centralPanel = new JPanel();
		JPanel metricsPanel = new JPanel();
		JPanel graphsPanel = new JPanel();
		JPanel comparePanel = new JPanel();
		JPanel settingsPanel = new JPanel();
		JPanel exitPanel = new JPanel();
		
		CustomButton metrics = new CustomButton("Metrics");
		CustomButton graphs = new CustomButton("Graphs");
		CustomButton compare = new CustomButton("Compare Campaigns");
		CustomButton settings = new CustomButton("Settings");
		CustomButton exit = new CustomButton("Exit");
		
		scroll =  new JScrollPane(graphPanel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);	
		
		metrics.setName("metricsPanel");
		graphs.setName("graphsPanel");
		compare.setName("comparePanel");
		settings.setName("settingsPanel");
		
		metrics.setToolTipText("Filter your metrics");
		graphs.setToolTipText("Filter your graphs");
		compare.setToolTipText("Select campaigns to compare");
		settings.setToolTipText("Modify your settings");	
		
		metricsPanel.setName("metricsPanel");
		graphsPanel.setName("graphsPanel");
		comparePanel.setName("comparePanel");
		settingsPanel.setName("settingsPanel");
		
		exit.setPreferredSize(new Dimension(400,50));
		exitPanel.add(exit);
		
		metricsPanel.add(metrics);
		graphsPanel.add(graphs);
		comparePanel.add(compare);
		settingsPanel.add(settings);
		
		centralPanel.setBounds(getBounds());
		centralPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
		centralPanel.setLayout(new BoxLayout(centralPanel, BoxLayout.PAGE_AXIS));

		addCardListener(metrics);
		addCardListener(graphs);
		addCardListener(compare);
		addCardListener(settings);

		centralPanel.add(metricsPanel);
		centralPanel.add(Box.createVerticalStrut(25));
		centralPanel.add(graphsPanel);
		centralPanel.add(Box.createVerticalStrut(25));
		centralPanel.add(comparePanel);
		centralPanel.add(Box.createVerticalStrut(25));
		centralPanel.add(settingsPanel);
		centralPanel.add(Box.createVerticalStrut(25));
		
		add(centralPanel, BorderLayout.NORTH);
		add(exitPanel, BorderLayout.SOUTH);
		
		exit.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e) 
			{
				int reply = JOptionPane.showConfirmDialog(null, "<html>Do you really want to exit?</html>", "Exit Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE ,new ImageIcon("img/exit.png"));
						
				
				if (reply == 0)
				{	
					if(MyFrame.DELETE_DB){
						System.out.println("cc");
						File file = new File("database/campaign.db");
						file.deleteOnExit();
					}
					frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
//					if(frame.getToDelete()){
//						File file = new File("database/campaign.db");
//						file.de
//					}

				}
			}
		});
	}

	public void addCardListener(JButton button)
	{
		button.addMouseListener(new MouseAdapter()
		{
			CardLayout cl = (CardLayout) (cards.getLayout());
	
			@Override
			public void mouseEntered(MouseEvent arg0) 
			{
				cl.show(cards, button.getName());
			}
					
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				contentPanel.removeAll();
				
				switch(button.getName())
				{
					case "metricsPanel":
						contentPanel.add(metricPanel);
						break;
					case "graphsPanel":
						contentPanel.add(scroll);
						break;
					case "comparePanel":
						contentPanel.add(comparePanel);
						break;
					case "settingsPanel":
						contentPanel.add(settingsPanel);
					    break;    
				}	
				
				contentPanel.revalidate();
				contentPanel.repaint();
			}
		});
	}

	public ComparePanel getComparePanel() {
		return comparePanel;
	}

	public SettingsPanel getSettingsPanel() {
		return settingsPanel;
	}
	

}