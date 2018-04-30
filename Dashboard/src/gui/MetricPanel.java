package gui;
import java.awt.BorderLayout;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import gui.custom.CustomPanel;
import model.FilterStorage;

public class MetricPanel extends CustomPanel
{
	private static final long serialVersionUID = 1L;
	private MetricFilterPanel metFiltPanel;
	private MetricTablePanel metTablePanel;
	private FilterStorage fStorage;
	
	public MetricPanel(MyFrame frame, JPanel panel, FilterStorage fStorage)
	{
		super(frame, panel, "Metrics");
		this.fStorage = fStorage;
		init();
		
		this.setBorder(new EmptyBorder(15,15,15,15));
	}
	
	/*public FilterStorage getFilterStorage()
	{
		return storage;
	}*/
	
	public void init()
	{
		metFiltPanel = fStorage.getMetricFilterPanel();
		metTablePanel = fStorage.getMetricTablePanel();
		
		JPanel centralPanel = new JPanel();
		centralPanel.setLayout(new BoxLayout(centralPanel, BoxLayout.LINE_AXIS));
		
		centralPanel.add(metFiltPanel);
		centralPanel.add(Box.createHorizontalStrut(10));
		centralPanel.add(metTablePanel);
		
		add(centralPanel, BorderLayout.CENTER);
	}
}