package gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;


import model.FilterStorage;
import model.Theme;

public class MetricTablePanel extends JPanel implements BorderInterface//POTENTIALLY ADD ROW CLICK TO HIGHLIGHT/UN-HIGHLIGHT
{
	private static final long serialVersionUID = 1L;
	private MyFrame frame;
	private JPanel centerPanel;
	private JScrollPane scrollPane;
	private MetricTable metricTable;
	private FilterStorage storage;
	
	public MetricTablePanel(MyFrame frame, FilterStorage storage) 
	{
		this.frame = frame;
		this.storage = storage;
		init();
	}
	
	public MetricTable getMetricTable()
	{
		return metricTable;
	}
	
	public void init()
	{
		setLayout(new BorderLayout());
		
		metricTable = new MetricTable(frame);
		metricTable.setName("spread");
		
		scrollPane = new JScrollPane(metricTable);
		metricTable.setPreferredSize(new Dimension(1100, 200));
		metricTable.setBorder(BorderFactory.createLineBorder(Theme.ACTIVE_FG));
		metricTable.getTableHeader().setBorder(BorderFactory.createLineBorder(Theme.ACTIVE_FG));
		metricTable.getTableHeader().setResizingAllowed(false);
	
		centerPanel = new JPanel();
		centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.PAGE_AXIS));
		
		centerPanel.add(Box.createVerticalStrut(25));
		centerPanel.add(scrollPane);
	
		add(centerPanel, BorderLayout.CENTER);
	
		setBackground(Color.cyan);
	}
	
	public void refreshBorder(Color fg)
	{
		metricTable.setBorder(BorderFactory.createLineBorder(fg));
		metricTable.getTableHeader().setBorder(BorderFactory.createLineBorder(fg));
	}
	
	public void changeTheme(Container con, Color bg, Color fg, Color hoverColor)
	{
		this.setBackground(bg);
		
		for(Component c : con.getComponents()) {
			
			c.setBackground(bg);
			c.setForeground(fg);
			
			if (c instanceof Container) 
			{
				Container cont = (Container) c;
				changeTheme(cont,bg,fg,hoverColor);
			}
			
		}
	}
	
	public void updateTableRecords()
	{	
		metricTable.removeAll();
		metricTable.getTableModel().setRowCount(0);
		
		FilterStorage storage = frame.fStorage;
		
		for (Integer key : storage.getFilters().keySet())
		{
			addMetricToTable(storage.getFilters().get(key));
		}
	}

	public void addMetricToTable(MetricFilter filter) 
	{
		if (!filter.metricsBox.getSelectedItem().equals("None"))
		{
			metricTable.addMetricRow(filter);
		}
	}


}
