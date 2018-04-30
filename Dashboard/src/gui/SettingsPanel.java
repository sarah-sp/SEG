package gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;

import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import gui.custom.CustomButton;
import gui.custom.CustomPanel;
import model.Theme;

public class SettingsPanel extends JPanel implements BorderInterface
{
	private static final long serialVersionUID = 1L;
	private JRadioButton brTimeSpent, brPageNum, proTheme, defaultTheme, huskyTheme, softTheme;
	private MyFrame frame;
	private JPanel bounceDefPanel, themes, docPanel, aboutPanel, appearancePanel, colours, databasePanel;
	private JPanel leftPanel, centrePanel;
	private CustomPanel topPanel;
	private CustomButton bounceDef, appearance, documentation, about, database;
	private ButtonGroup themeGroup;

	public SettingsPanel(MyFrame frame, JPanel panel)
	{

		this.frame = frame;
		
		this.setLayout(new BorderLayout());
		this.setBounds(getBounds());
		this.setBackground(Theme.ACTIVE_BG);
		this.setBorder(new EmptyBorder(15,15,15,15));
		
		/*
		 * panels that hold the buttons on the left
		 */
		bounceDefPanel = new JPanel();
		themes = new JPanel();
		docPanel = new JPanel();
		aboutPanel = new JPanel();
		appearancePanel = new JPanel();
		colours = new JPanel();
		databasePanel = new JPanel();
		
		appearancePanel.setLayout(new BoxLayout(appearancePanel, BoxLayout.Y_AXIS));
		appearancePanel.setBorder(new EmptyBorder(15,15,15,15));
		
		bounceDefPanel.setBackground(Theme.ACTIVE_BG);
		themes.setBackground(Theme.ACTIVE_BG);
		docPanel.setBackground(Theme.ACTIVE_BG);
		aboutPanel.setBackground(Theme.ACTIVE_BG);
		
		leftPanel = new JPanel(new BorderLayout());
		centrePanel = new JPanel(new BorderLayout());
		topPanel = new CustomPanel(frame, panel, "Settings");
		
		leftPanel.setBackground(Theme.ACTIVE_BG);
		leftPanel.setLayout(new BoxLayout(leftPanel,BoxLayout.Y_AXIS));
		leftPanel.setBorder(new EmptyBorder(15, 15, 15, 15));
		
		centrePanel.setBackground(Theme.ACTIVE_BG);
		appearancePanel.setBackground(Theme.ACTIVE_BG);
		

		/*
		 * buttons
		 */

		bounceDef = new CustomButton("Bounce Rate");
		database = new CustomButton("Database");
		appearance = new CustomButton("Appearance");
		documentation = new CustomButton("Documentation");
		about = new CustomButton("About");
		
		database.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				centrePanel.removeAll();
				
				centrePanel.setLayout(new BoxLayout(centrePanel, BoxLayout.X_AXIS));
				centrePanel.setAlignmentX(Component.TOP_ALIGNMENT);
				
				JLabel del = new JLabel("Delete current campaign upon closing the application");
				del.setForeground(Theme.ACTIVE_FG);
				del.setFont(new Font("Courier", Font.BOLD, 20));
					
				JCheckBox delete = new JCheckBox();
				delete.setSelected(MyFrame.DELETE_DB);
				delete.addActionListener(new ActionListener(){
		
					@Override
					public void actionPerformed(ActionEvent e) {
						if(delete.isSelected()){

							MyFrame.DELETE_DB = true;
						} else {
							MyFrame.DELETE_DB = false;
						}
						
					}
					
				});
				
				centrePanel.add(del);
				centrePanel.add(delete);
//				frame.getLoadPanel().load();
//				frame.getLoadPanel().startProgressBar();
//				frame.getController().
				
				centrePanel.revalidate();
				centrePanel.repaint();
				
			}
			
		});
		bounceDef.addActionListener( new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				
				centrePanel.removeAll();
				centrePanel.setLayout(new BorderLayout());
				bounceDef.setBackground(Theme.ACTIVE_HOVER);
				
				JLabel heading = new JLabel("Bounce Rate: ");
				heading.setForeground(Theme.ACTIVE_FG);
				heading.setFont(new Font("Courier", Font.BOLD, 20));
				
				brTimeSpent = new JRadioButton("Time spent on page", frame.getBounceDef().equals("time"));
				brPageNum = new JRadioButton("Number of pages visited", frame.getBounceDef().equals("pages"));
				
				brTimeSpent.setBackground(Theme.ACTIVE_BG);
				brPageNum.setBackground(Theme.ACTIVE_BG);
				brTimeSpent.setForeground(Theme.ACTIVE_FG);
				brPageNum.setForeground(Theme.ACTIVE_FG);
				
				ButtonGroup brGroup = new ButtonGroup();
				brGroup.add(brTimeSpent);
				brGroup.add(brPageNum);
				
				bounceDefPanel = new JPanel(new GridLayout(3,1));
				bounceDefPanel.setBackground(Theme.ACTIVE_BG);
				bounceDefPanel.setLayout(new BoxLayout(bounceDefPanel, BoxLayout.PAGE_AXIS));
				bounceDefPanel.setBorder(new EmptyBorder(15,15,15,15));
				
				bounceDefPanel.add(heading);
				bounceDefPanel.add(Box.createVerticalStrut(25));
				bounceDefPanel.add(brTimeSpent);
				bounceDefPanel.add(Box.createVerticalStrut(15));
			    bounceDefPanel.add(brPageNum);			    

			    centrePanel.add(bounceDefPanel);
				    
			    brPageNum.addActionListener(new ActionListener() 
				{
					@Override
					public void actionPerformed(ActionEvent arg0) 
					{
						frame.setBounceDef("pages");
						frame.revalidate();
						frame.repaint();
					}
				});
				
				brTimeSpent.addActionListener(new ActionListener()
				{
					@Override
					public void actionPerformed(ActionEvent arg0)
					{
						frame.setBounceDef("time");
						frame.revalidate();
						frame.repaint();
					}
				});

				centrePanel.revalidate();
				centrePanel.repaint();
			}
		});
		
		appearance.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				centrePanel.removeAll();
				colours.removeAll();
				appearancePanel.removeAll();
				
				centrePanel.setLayout(new BorderLayout());
				
				proTheme = new JRadioButton("Pro", Theme.ACTIVE_THEME.equals("Pro"));
				defaultTheme = new JRadioButton("Default", Theme.ACTIVE_THEME.equals("Default"));
				huskyTheme = new JRadioButton("Husky", Theme.ACTIVE_THEME.equals("Husky"));
				softTheme = new JRadioButton("Soft", Theme.ACTIVE_THEME.equals("Soft"));
							
				proTheme.setForeground(Theme.ACTIVE_FG);
				defaultTheme.setForeground(Theme.ACTIVE_FG);
				huskyTheme.setForeground(Theme.ACTIVE_FG);
				softTheme.setForeground(Theme.ACTIVE_FG);
				
				proTheme.setBackground(Theme.ACTIVE_BG);
				defaultTheme.setBackground(Theme.ACTIVE_BG);
				huskyTheme.setBackground(Theme.ACTIVE_BG);
				softTheme.setBackground(Theme.ACTIVE_BG);
				
				JLabel heading = new JLabel("Theme:");
				heading.setForeground(Theme.ACTIVE_FG);
				heading.setFont(new Font("Courier", Font.BOLD, 20));
				
				JLabel choose = new JLabel("Make your own:");
				choose.setFont(new Font("Courier", Font.BOLD, 20));
				choose.setForeground(Theme.ACTIVE_FG);
				
				JLabel bgLabel = new JLabel("Background: ");
				JLabel fgLabel = new JLabel("Font colour: ");
				JLabel hoverLabel = new JLabel("Hover colour: ");
				
				bgLabel.setForeground(Theme.ACTIVE_FG);
				fgLabel.setForeground(Theme.ACTIVE_FG);
				hoverLabel.setForeground(Theme.ACTIVE_FG);
				
				colours.setBackground(Theme.ACTIVE_BG);
				colours.setLayout(new BoxLayout(colours, BoxLayout.X_AXIS));
				colours.setAlignmentX(Component.LEFT_ALIGNMENT);
				
				CustomButton bg = new CustomButton("  ");
				bg.setBackground(Theme.ACTIVE_BG);
				bg.changeHover(Theme.ACTIVE_BG, Theme.ACTIVE_BG);
				bg.setBorder(BorderFactory.createLineBorder(Theme.ACTIVE_HOVER, 1));
				bg.setName("bg");
				bg.setToolTipText("Click to change background colour");			
				
				CustomButton fg = new CustomButton("  ");
				fg.setBackground(Theme.ACTIVE_FG);
				fg.changeHover(Theme.ACTIVE_FG, Theme.ACTIVE_FG);
				fg.setBorder(BorderFactory.createLineBorder(Theme.ACTIVE_HOVER, 1));
				fg.setName("fg");
				fg.setToolTipText("Click to change font colour");
				
				CustomButton hover = new CustomButton("  ");
				hover.setBackground(Theme.ACTIVE_HOVER);
				hover.changeHover(Theme.ACTIVE_HOVER, Theme.ACTIVE_HOVER);
				hover.setBorder(BorderFactory.createLineBorder(Theme.ACTIVE_HOVER, 1));
				hover.setName("hover");
				hover.setToolTipText("Click to change hover colour");
				
				addColourButtonListeners(bg, "background");
				addColourButtonListeners(fg, "foreground");
				addColourButtonListeners(hover, "hover");
				
				colours.add(bgLabel);
				colours.add(bg);
				colours.add(fgLabel);
				colours.add(fg);
				colours.add(hoverLabel);
				colours.add(hover);
				
				colours.repaint();
				colours.revalidate();
				
				themeGroup = new ButtonGroup();
				themeGroup.add(defaultTheme);
				themeGroup.add(proTheme);
				themeGroup.add(huskyTheme);
				themeGroup.add(softTheme);

				addRadioListeners();
				
				appearancePanel.add(heading);
				appearancePanel.add(Box.createVerticalStrut(20));
				appearancePanel.add(defaultTheme);
				appearancePanel.add(Box.createVerticalStrut(15));
				appearancePanel.add(proTheme);
				appearancePanel.add(Box.createVerticalStrut(15));
				appearancePanel.add(huskyTheme);
				appearancePanel.add(Box.createVerticalStrut(15));
				appearancePanel.add(softTheme);
				appearancePanel.add(Box.createVerticalStrut(30));
				appearancePanel.add(choose);
				appearancePanel.add(Box.createVerticalStrut(15));
				appearancePanel.add(colours);
				
				centrePanel.add(appearancePanel, BorderLayout.CENTER);
				centrePanel.revalidate();
				centrePanel.repaint();
				centrePanel.setBackground(Theme.ACTIVE_BG);
				appearancePanel.setBackground(Theme.ACTIVE_BG);
				
			}	
		});
		
		documentation.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				centrePanel.removeAll();
				
				centrePanel.setLayout(new BorderLayout());
				
				JLabel aboutLabel = new JLabel();
				try {
					aboutLabel.setText(new String(Files.readAllBytes(Paths.get("guide/userGuide.txt"))));
				} catch (IOException e1) {
					e1.printStackTrace();
					aboutLabel.setText("File missing");
				}
				aboutLabel.setForeground(Theme.ACTIVE_FG);
				
				JPanel textHolder = new JPanel();
				textHolder.setLayout(new GridBagLayout());
				textHolder.setBackground(Theme.ACTIVE_BG);
				
				JPanel filler = new JPanel();
				filler.setBackground(Theme.ACTIVE_BG);
				
				GridBagConstraints c = new GridBagConstraints();

				c.gridx = 1;
				c.gridy = 1;
				c.fill = GridBagConstraints.BOTH;
				textHolder.add(aboutLabel, c);
				
				JScrollPane scroll = new JScrollPane(textHolder, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
	
				centrePanel.add(scroll);
				centrePanel.revalidate();
				centrePanel.repaint();
			}
			
		});
		
		about.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				centrePanel.removeAll();
				
				centrePanel.setLayout(new BorderLayout());
				
				JLabel aboutLabel = new JLabel();
				try {
					aboutLabel.setText(new String(Files.readAllBytes(Paths.get("about/about.txt"))));
				} catch (IOException e1) {
					e1.printStackTrace();
					aboutLabel.setText("File missing");
				}
				aboutLabel.setForeground(Theme.ACTIVE_FG);
				
				JPanel textHolder = new JPanel();
				textHolder.setLayout(new GridBagLayout());
				textHolder.setBackground(Theme.ACTIVE_BG);
				
				
				GridBagConstraints c = new GridBagConstraints();

//				c.gridx = 1;
//				c.gridy = 1;
//				c.fill = GridBagConstraints.BOTH;
				textHolder.add(aboutLabel, c);
				
				centrePanel.add(textHolder);
				centrePanel.revalidate();
				centrePanel.repaint();
			}
			
		});
		
		databasePanel.add(database);
		bounceDefPanel.add(bounceDef);
		themes.add(appearance);
		docPanel.add(documentation);
		aboutPanel.add(about);
		
		leftPanel.add(databasePanel);
		leftPanel.add(bounceDefPanel);
		leftPanel.add(themes);
		leftPanel.add(docPanel);
		leftPanel.add(aboutPanel);
		
		centrePanel.setBorder(new LineBorder(Color.BLACK,3));
		
		this.add(leftPanel, BorderLayout.WEST);
		this.add(centrePanel, BorderLayout.CENTER);
		this.add(topPanel, BorderLayout.NORTH);

	}
	

	
	
	/*
	 * listener for the make your own theme functionality
	 */
//	public void addColourButtonListeners(CustomButton button, String text){
//		button.addActionListener(new ActionListener(){
//
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				Color newColor = JColorChooser.showDialog(null, "Choose a " + text + " color", button.getBackground());
//				if(newColor != null) {
//					MainPanel mp = frame.getMainPanel();
//					if(button.getName().equals("bg")){
//						Theme.ACTIVE_BG = newColor;
//						mp.changeTheme(mp, newColor, Theme.ACTIVE_FG, Theme.ACTIVE_HOVER);
//					} else if(button.getName().equals("fg")){
//						Theme.ACTIVE_FG = newColor;
//						mp.changeTheme(mp, Theme.ACTIVE_BG, newColor, Theme.ACTIVE_HOVER);
//					} else if(button.getName().equals("hover")){

	/*
	 * listener for the make your own theme functionality
	 */
	public void addColourButtonListeners(CustomButton button, String text)
	{
		button.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				Theme.ACTIVE_THEME = "Custom";
				Color newColor = JColorChooser.showDialog(null, "Choose a " + text + " color", button.getBackground());
				if(newColor != null) 
				{
					MainPanel mp = frame.getMainPanel();
					if(button.getName().equals("bg"))
					{
						Theme.ACTIVE_BG = newColor;
						mp.changeTheme(mp, newColor, Theme.ACTIVE_FG, Theme.ACTIVE_HOVER);
					} 
					else if(button.getName().equals("fg"))
					{
						Theme.ACTIVE_FG = newColor;
						mp.changeTheme(mp, Theme.ACTIVE_BG, newColor, Theme.ACTIVE_HOVER);
					} 
					else if(button.getName().equals("hover"))
					{
						Theme.ACTIVE_HOVER = newColor;
						mp.changeTheme(mp, Theme.ACTIVE_BG, Theme.ACTIVE_FG, newColor);
					}
					themeGroup.clearSelection();
				}
			}
			
		});
	}
	
	/*
	 * attaches the listeners to the radio buttons to choose theme
	 */
	public void addRadioListeners(){
		defaultTheme.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				MainPanel mp = frame.getMainPanel();
				Theme.ACTIVE_BG = Color.white;
				Theme.ACTIVE_FG = Color.black;
				Theme.ACTIVE_HOVER = Color.lightGray;
				mp.changeTheme(mp, Color.WHITE, Color.black, Color.LIGHT_GRAY);
				defaultTheme.setSelected(true);

			}
		});
		
		proTheme.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				MainPanel mp = frame.getMainPanel();
				Theme.ACTIVE_BG = Color.BLACK;
				Theme.ACTIVE_FG = Color.GREEN;
				Theme.ACTIVE_HOVER = Color.BLUE;
				mp.changeTheme(mp, Color.BLACK, Color.GREEN, Color.BLUE);
				proTheme.setSelected(true);

				frame.repaint();
				frame.revalidate();

			}
		});
		
		huskyTheme.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				MainPanel mp = frame.getMainPanel();
				Theme.ACTIVE_BG = Theme.HUSKY_BG;
				Theme.ACTIVE_FG = Theme.HUSKY_FG;
				Theme.ACTIVE_HOVER = Theme.HUSKY_HOVER;
				mp.changeTheme(mp, Theme.HUSKY_BG, Theme.HUSKY_FG, Theme.HUSKY_HOVER);
				huskyTheme.setSelected(true);
			}
		});
		
		softTheme.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				MainPanel mp = frame.getMainPanel();
				Theme.ACTIVE_BG = Theme.SOFT_BG;
				Theme.ACTIVE_FG = Theme.SOFT_FG;
				Theme.ACTIVE_HOVER = Theme.SOFT_HOVER;
				mp.changeTheme(mp, Theme.SOFT_BG, Theme.SOFT_FG, Theme.SOFT_HOVER);
				softTheme.setSelected(true);
			}
		});
	}
	

	@Override
	public void refreshBorder(Color bg) {
		centrePanel.setBorder(new LineBorder(bg,3));
	}
}
