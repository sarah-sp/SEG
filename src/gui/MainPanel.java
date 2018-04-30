package gui;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import gui.custom.CustomButton;
import model.CompareStorage;
import model.FilterStorage;
import model.Theme;

public class MainPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	private MyFrame frame;
	private JPanel panel, cards, logoPanel, overviewPanel;
	private MenuPanel menuPanel;
	private MainOPanel mainOPanel;
	private MetricOPanel metricOPanel;
	private GraphOPanel graphOPanel;
	private CompareOPanel compareOPanel;
	private SettingsOPanel settingsOPanel;
	private JLabel logo;
	private TitledBorder border;
	private LineBorder border1;
	private FilterStorage fStorage;
	private CompareStorage cStorage;
	
	public MainPanel(MyFrame frame, JPanel panel, FilterStorage fStorage, CompareStorage cStorage)
	{
		this.frame = frame;
		this.panel = panel;
		this.fStorage = fStorage;
		this.cStorage = cStorage;
		init();
		changeTheme(this, Color.white, Color.black, Color.LIGHT_GRAY);
		//changeTheme(this, new Color(68,82,98), new Color(144,160,178), new Color(171,187,209));
		//changeTheme(this, Color.BLACK, Color.green, Color.blue);
		//changeTheme(this, new Color(220,220,220), new Color(105,105,105), new Color(169,169,169));

	}
	
	public void changeTheme(Container c, Color bg, Color fg, Color hover)
	{
		this.setBackground(bg);
		this.setForeground(fg);
		cards.setBorder(new TitledBorder(new LineBorder(fg, 3), "YOUR OVERVIEWS", TitledBorder.CENTER, TitledBorder.ABOVE_TOP, new Font("Courier", Font.BOLD,40), fg));
		UIManager.put("OptionPane.background", Theme.ACTIVE_BG);
		UIManager.put("OptionPane.messageForeground", Theme.ACTIVE_FG);
		UIManager.put("Panel.background", Theme.ACTIVE_BG);
		
		if(bg.equals(Color.white) && fg.equals(Color.BLACK) && hover.equals(Color.LIGHT_GRAY)){
			Theme.ACTIVE_THEME = "Default";
		} else if(bg.equals(Color.black) && fg.equals(Color.green) && hover.equals(Color.blue)){
			Theme.ACTIVE_THEME = "Pro";
		} else if(bg.equals(Theme.HUSKY_BG) && fg.equals(Theme.HUSKY_FG) && hover.equals(Theme.HUSKY_HOVER)){
			Theme.ACTIVE_THEME = "Husky";
		} else if(bg.equals(Theme.SOFT_BG) && fg.equals(Theme.SOFT_FG) && hover.equals(Theme.SOFT_HOVER)){
			Theme.ACTIVE_THEME = "Soft";
		} else {
			Theme.ACTIVE_THEME = "Custom";
		}

		
//		Theme.ACTIVE_BG = bg;
//		Theme.ACTIVE_FG = fg;
//		Theme.ACTIVE_HOVER = hover;

				
		if(c instanceof CustomButton) {
			if(c.getName() != null) {
				if(c.getName().equals("bg")){
					((CustomButton) c).changeHover(bg, bg);
					c.setBackground(bg);
				} else if(c.getName().equals("fg")){
					((CustomButton) c).changeHover(fg, fg);
					c.setBackground(fg);
				}else if (c.getName().equals("hover")){
					((CustomButton) c).changeHover(hover, hover);
					c.setBackground(hover);
				} else{
					((CustomButton) c).changeHover(bg, hover);
				}
			} else {
				((CustomButton) c).changeHover(bg, hover);
			}
		}
		if(c instanceof BorderInterface) {
			((BorderInterface) c).refreshBorder(fg);
		}
		
		for(Component comp : c.getComponents()) {

			if(comp instanceof BorderInterface) {
				((BorderInterface) comp).refreshBorder(fg);
			}
			
			if(comp instanceof JTable) {
				((JTable) comp).setSelectionBackground(hover);
				((JTable) comp).getTableHeader().setBackground(bg);
				((JTable) comp).getTableHeader().setForeground(fg);
			}
			
			if(comp instanceof CustomButton) {
				if(comp.getName() != null) {
					
					if(comp.getName().equals("bg")){
						((CustomButton) comp).changeHover(bg, bg);
						comp.setBackground(bg);
						
					} else if(comp.getName().equals("fg")){
						((CustomButton) comp).changeHover(fg, fg);
						comp.setBackground(fg);
						
					} else if(comp.getName().equals("hover")){
						((CustomButton) comp).changeHover(hover, hover);
						comp.setBackground(hover);
					} 
					
				} else {
					((CustomButton) comp).changeHover(bg, hover);
				}
				
			} else {
				comp.setBackground(bg);
				comp.setForeground(fg);
			}
			

			if(comp instanceof Container) {
				changeTheme((Container) comp,bg,fg,hover);
			}
			
		}
		
		menuPanel.changeTheme(menuPanel, bg, fg, hover);
		menuPanel.changeTheme(menuPanel.getMetricPanel(), bg, fg, hover);
		menuPanel.changeTheme(menuPanel.getGrahpPanel(), bg, fg, hover);
		menuPanel.changeTheme(menuPanel.getComparePanel(), bg, fg, hover);
		menuPanel.changeTheme(menuPanel.getSettingsPanel(), bg, fg, hover);
		

		this.revalidate();
		this.repaint();

		frame.revalidate();
		frame.repaint();

	}
	
	public MenuPanel getMenuPanel()
	{
		return menuPanel;
	}
	
	public void init()
	{
		setBounds(getBounds());
		setBackground(Theme.ACTIVE_BG);
		setBorder(new EmptyBorder(15, 15, 15, 15));
		setLayout(new GridBagLayout());
				
		cards = new JPanel(new CardLayout());
		
		logoPanel = new JPanel();
		overviewPanel = new JPanel(); 
		menuPanel = new MenuPanel(frame, panel, cards, fStorage, cStorage);
		 
		mainOPanel = new MainOPanel();
		metricOPanel = new MetricOPanel(frame);
		graphOPanel = new GraphOPanel();
		compareOPanel = new CompareOPanel(frame, cStorage);
		settingsOPanel = new SettingsOPanel(frame);
		
		cards.add(mainOPanel, "mainPanel");
		cards.add(metricOPanel, "metricsPanel");
		cards.add(graphOPanel, "graphsPanel");
		cards.add(compareOPanel, "comparePanel");
		cards.add(settingsOPanel, "settingsPanel");
		overviewPanel.add(cards);
		
		cards.setBackground(Theme.ACTIVE_BG);
		logoPanel.setBackground(Color.BLUE);
		menuPanel.setBackground(Theme.ACTIVE_BG);
		overviewPanel.setBackground(Color.ORANGE);
		overviewPanel.setName("overviewPanel");
		
		CardLayout cl = (CardLayout)(cards.getLayout());
		cl.show(cards, "mainPanel");
		
		Font font = new Font("Courier", Font.BOLD,40);
		border1 = new LineBorder(Theme.ACTIVE_FG, 3);
		border = new TitledBorder(border1, "YOUR OVERVIEWS", TitledBorder.CENTER, TitledBorder.ABOVE_TOP, font, Theme.ACTIVE_FG);
		cards.setBorder(border);//add border to overview panel
		
		logoPanel.setBounds(20, 20, 100, 100);
		logoPanel.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseEntered(MouseEvent e) 
			{
				cl.show(cards, "mainPanel"); //change to default overview panel
			}
		});
		
		//ImageIcon image = new ImageIcon("img/dashMenu2.png");
		logo = new JLabel("Dash", JLabel.CENTER);
		logo.setFont(new Font("Above DEMO", Font.BOLD, 60));
		logoPanel.setBackground(Theme.ACTIVE_BG);
		logoPanel.add(logo);
		
		GridBagConstraints c = new GridBagConstraints();

		c.gridx = 0;
		c.gridy = 0;
		c.gridwidth = 1;
		c.gridheight = 1;
		c.weightx = 0;
		c.weighty = 0;
		c.fill = GridBagConstraints.BOTH;
		add(logoPanel, c);
		
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 1;
		c.gridheight = GridBagConstraints.REMAINDER;
		c.weightx = 0;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		add(menuPanel, c);
		
		c.gridx = 1;
		c.gridy = 0;
		c.gridwidth = GridBagConstraints.REMAINDER;
		c.gridheight = GridBagConstraints.REMAINDER;
		c.weightx = 1;
		c.weighty = 1;
		c.fill = GridBagConstraints.BOTH;
		add(cards, c);
		setSize(1600,975);
	}
	
	
//	public void changeTheme(Container con, Color bg, Color fg, Color hoverColor){
//		this.setBackground(bg);
//		
//		for(Component c : con.getComponents()) {
//			
//			c.setBackground(bg);
//			c.setForeground(fg);
//			
//			if(c instanceof Container) {
//				Container cont = (Container) c;
//				changeTheme(cont,bg,fg,hoverColor);
//			}
//			if(c instanceof CustomMenuButton){
//				((CustomMenuButton) c).changeTheme(bg, fg, hoverColor);
//			}
//			
//		}
//		border.setTitleColor(fg);
//		border.setBorder(new LineBorder(fg,3));
//		logo.setForeground(fg);
//	}
}