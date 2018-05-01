package gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import controller.Controller;
import model.CompareStorage;
import model.FilterStorage;
import model.Theme;

public class MyFrame extends JFrame 
{
	private static final long serialVersionUID = 1L;
	private static final int INITIAL_WIDTH = 600;
	private static final int INITIAL_HEIGHT = 650;
	private static final int FRAME_WIDTH = 1675;
	private static final int FRAME_HEIGHT = 1010; //Maybe make fullscreen on open?
	public static boolean DELETE_DB = false;
	private Controller controller;
	private JPanel contentPane;
	private MainPanel mainPanel;
	private LoadPanel loadPanel;
	private String bounceDef;
	private Font font;
	protected FilterStorage fStorage;
	protected CompareStorage cStorage;

	public MyFrame(String s)  
	{		
		super(s);
		font = new Font("Above DEMO", Font.BOLD, 70);
		
		/*
		 * adding the font so the user can see it
		 */
		try {
		     GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		     ge.registerFont(Font.createFont(Font.TRUETYPE_FONT, new File("font/ABOVE  - PERSONAL USE ONLY.ttf")));
		} catch (IOException|FontFormatException e) {
		     e.printStackTrace();
		}
		
//		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
//		this.setLocation(dim.width/2-this.getSize().width/2, dim.height/2-this.getSize().height/2);


	}
	
	public MainPanel getMainPanel()
	{
		return mainPanel;
	}
	
	public void changeTheme(Container c, Color bg, Color fg, Color hover){
		this.setBackground(bg);
		this.setForeground(fg);
		
		for(Component comp : c.getComponents()) {
			
			comp.setBackground(bg);
			comp.setForeground(fg);
			
			if(comp instanceof Container) {
				changeTheme((Container) comp,bg,fg,hover);
			}
			
		}
	}
	
	public void init() 
	{	
		contentPane = new JPanel();
		contentPane.setLayout(new BoxLayout(contentPane,BoxLayout.PAGE_AXIS));
		contentPane.setBackground(Theme.ACTIVE_BG);

		//JScrollPane jsp = new JScrollPane(contentPane, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		setContentPane(contentPane);


		boolean dbExists = new File("database/campaign.db").exists();
		
		if(!dbExists) {
			JPanel logoPanel = new JPanel();
			logoPanel.setLayout(new BorderLayout());
			logoPanel.setBackground(Theme.ACTIVE_BG);
			
			JLabel logo = new JLabel("Dash", JLabel.CENTER);
			logo.setFont(font);
			
			logoPanel.add(logo, BorderLayout.CENTER);
			
			loadPanel = new LoadPanel(this, contentPane);
			
			contentPane.add(logoPanel);
			contentPane.add(loadPanel);
			contentPane.add(Box.createVerticalStrut(50));	
		} else {
			this.setFont(font);
			this.openClient();
		}
		
		setIconImage(new ImageIcon("img/icon.png").getImage());
		setSize(INITIAL_WIDTH, INITIAL_HEIGHT);
		requestFocusInWindow();
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		setVisible(true);
	}
	
	public void openClient() 
	{

		contentPane.removeAll();
		setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
		setMaximumSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
		setMinimumSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
		setSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
		setLocationRelativeTo(null);
		
		bounceDef = "Time";
		fStorage = new FilterStorage(this);
		cStorage = new CompareStorage();
		
		mainPanel = new MainPanel(this, contentPane, fStorage, cStorage);
		contentPane.add(mainPanel);

	}
	
	public LoadPanel getLoadPanel()
	{
		return loadPanel;
	}
	
	public void setController(Controller controller)
	{
		this.controller = controller;
	}
	
	public Controller getController()
	{	
		return controller;
	}
	
	public void setBounceDef(String s)
	{
		bounceDef = s;
	}
	
	public String getBounceDef()
	{
		return bounceDef;
	}
	public static final int getFrameWidth()
	{
		return FRAME_WIDTH;
	}
	public static final int getFrameHeight()
	{
		return FRAME_HEIGHT;
	}
	
	public Font getMainMenuFont()
	{
		return mainPanel.getMenuPanel().getFont();
	}


	public void refreshMain() 
	{
		contentPane.removeAll();
		MainPanel mainPanel = new MainPanel(this, contentPane, fStorage, cStorage);
		this.mainPanel = mainPanel;
		mainPanel.changeTheme(mainPanel, Theme.ACTIVE_BG, Theme.ACTIVE_FG, Theme.ACTIVE_HOVER);
		contentPane.add(mainPanel);
		contentPane.revalidate();
	}
}