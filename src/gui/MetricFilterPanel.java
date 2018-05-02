package gui;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import gui.custom.CustomButton;
import model.FilterStorage;
import model.Theme;

//panel holding the small filter panels
public class MetricFilterPanel extends JPanel implements BorderInterface
{
	private static final long serialVersionUID = 1L;
	private MyFrame frame;
	private JPanel scrollPaneHolder;
	private JScrollPane scrollPane;
	private JScrollBar vertical;
	private static final int SCROLLPANE_WIDTH = 320;
	private static final int SCROLLPANE_HEIGHT = 680;
	private FilterStorage storage;
	private JPanel buttonPanel, filterPanel;
	private CustomButton add, update;
	
	
	public MetricFilterPanel(MyFrame frame, FilterStorage storage)
	{
		this.frame = frame;
		this.storage = storage;

		init();
		//changeTheme(this,Theme.ACTIVE_BG, Theme.ACTIVE_FG, Color.gray);
		
		//filterPanel.setBorder(BorderFactory.createTitledBorder(new LineBorder(Theme.ACTIVE_FG), "Metric Filters", TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION, frame.getFont().deriveFont(12.0f), Theme.ACTIVE_FG));

	}
	
	public void refreshBorder(Color fg){
		filterPanel.setBorder(BorderFactory.createTitledBorder(new LineBorder(fg), "Metric Filters", TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION, frame.getFont().deriveFont(12.0f), fg));

	}
	
	public JScrollPane getScrollPane()
	{
		return scrollPane;
	}
	
    public void init()
    {
    	filterPanel = new JPanel();
    	//customisePanel(filterPanel, "Metric Filters", Theme.ACTIVE_BG);
    	
    	//filterPanel.setBackground(Color.cyan);
    	filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.PAGE_AXIS));
    	//filterPanel.setBorder(BorderFactory.createTitledBorder(new LineBorder(Theme.ACTIVE_FG), "Metric Filters", TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION, frame.getFont().deriveFont(12.0f)));
    	
    	scrollPaneHolder = new JPanel();
    	scrollPaneHolder.setLayout(new BoxLayout(scrollPaneHolder, BoxLayout.PAGE_AXIS));
		scrollPaneHolder.setBackground(Theme.ACTIVE_BG);
		scrollPaneHolder.setMaximumSize(new Dimension(SCROLLPANE_WIDTH, SCROLLPANE_HEIGHT));;
		
		scrollPane = new JScrollPane(scrollPaneHolder, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		scrollPane.setPreferredSize(new Dimension(SCROLLPANE_WIDTH, SCROLLPANE_HEIGHT));
		scrollPane.setBorder(BorderFactory.createEmptyBorder());	
		
		vertical = scrollPane.getVerticalScrollBar();
		vertical.setUnitIncrement(16);
	
		// SET SIZE AND FONT SIZE OF THE BUTTONS
		add = new CustomButton("Add Metric");
		add.setFontSize(23);
		add.setPreferredSize(new Dimension(100,30));
		add.setBorder(new LineBorder(Theme.ACTIVE_FG, 1));
		
		update = new CustomButton("Update");
		update.setPreferredSize(new Dimension(100,30));
		update.setFontSize(23);
		update.setBorder(new LineBorder(Theme.ACTIVE_FG, 1));
		
		buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
		buttonPanel.setBackground(Theme.ACTIVE_BG);
		buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
	
		buttonPanel.add(Box.createHorizontalStrut(15));
		buttonPanel.add(add);
		buttonPanel.add(Box.createHorizontalStrut(40));
		buttonPanel.add(update);
		buttonPanel.add(Box.createHorizontalStrut(15));
		
		filterPanel.add(scrollPane);
		filterPanel.add(buttonPanel);
		
		setBounds(getBounds());
		setBackground(Theme.ACTIVE_BG);
		setBorder(new EmptyBorder(15, 15, 0, 15));
		setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
		
		add(filterPanel);
		
		add.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{	
				MetricFilter metFilter = new MetricFilter(storage);
				storage.addFilter(metFilter);
				updateMetricPanel();

				vertical.setValue(vertical.getMaximum());

			}
		});
		
		update.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
//				if(validInput()){
					storage.getMetricTablePanel().updateTableRecords();
					storage.getFrame().getMainPanel().getMenuPanel().getGrahpPanel().updateFilterList();
//				} 
			}
		});
	}
	 
	
	
  
	public void updateMetricPanel()
	{
		scrollPaneHolder.removeAll();
		
		for(MetricFilter filter : storage.getFilters().values())
		{
			scrollPaneHolder.add(Box.createVerticalStrut(10));
			scrollPaneHolder.add(filter);
		}
	
		scrollPaneHolder.revalidate();
		scrollPaneHolder.repaint();
		vertical.setValue(vertical.getMaximum());

		frame.revalidate();

	}

	
	public void changeTheme(Container con, Color bg, Color fg, Color hoverColor){
		this.setBackground(bg);
		
		for(Component c : con.getComponents()) {
			
			c.setBackground(bg);
			c.setForeground(fg);
			
			if(c instanceof Container) {
				Container cont = (Container) c;
				changeTheme(cont,bg,fg,hoverColor);
			}	
		}
		
		filterPanel.setBorder(BorderFactory.createTitledBorder(new LineBorder(fg), "Metric Filters", TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION, frame.getFont().deriveFont(12.0f), fg));
		//scrollPane.setBorder(BorderFactory.createEmptyBorder());
//		add.changeTheme(bg, fg, hoverColor);
//		update.changeTheme(bg, fg, hoverColor);
		}
}
