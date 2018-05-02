package model;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import gui.MetricFilter;
import gui.MetricFilterPanel;
import gui.MetricTablePanel;
import gui.MyFrame;

//https://www.sqlite.org/lang_datefunc.html
public class FilterStorage 
{
	private MyFrame frame;
	private MetricFilterPanel metricFilterPanel;
	private MetricTablePanel metricTablePanel;
	private Map<Integer, MetricFilter> metricFilters;
	private String startDate, endDate;
	
	public FilterStorage(MyFrame frame)
	{
		this.frame = frame;
		metricFilters = new LinkedHashMap<Integer, MetricFilter>();
		metricFilterPanel = new MetricFilterPanel(frame, this);
		metricTablePanel = new MetricTablePanel(frame, this);
		calculateCampaignPeriod();
	}

	public void calculateCampaignPeriod()
	{
		Statement stmt1;
		Statement stmt2;
		
		try 
		{
			Connection conn = DriverManager.getConnection("jdbc:sqlite:database/campaign.db", frame.getController().getSQLiteConfig().toProperties());
			
			stmt1 = conn.createStatement();
			stmt2 = conn.createStatement();
			
			ResultSet rs1 = stmt1.executeQuery("SELECT MIN(date) FROM impressions;");
			ResultSet rs2 = stmt2.executeQuery("SELECT MAX(date) FROM impressions;");
			String start = rs1.getString(1);
			String end = rs2.getString(1);

			
			startDate = start; //CURRENTLY STORED AS STRINGS (2015-01-01 12:00:02) SO MAY NEED FORMATTING FOR USE WITH CHOOSING DATE
			endDate = end;
		} 
		catch (SQLException sqle) 
		{
			sqle.printStackTrace();
		}
	}
	
	public void reorderFilters()
	{		
		Map<Integer, MetricFilter> temp = new LinkedHashMap<>();
		int recount = 1;
		
		for(MetricFilter filter : metricFilters.values())
		{
			filter.setFilterIndex(recount);
			filter.setLabelText("Metric No. " + recount);
			temp.put(recount, filter);
			recount++;
		}

		metricFilters = temp;
	}
	
	public void addFilter(MetricFilter filter) 
	{
		metricFilters.put(filter.getFilterIndex(), filter);
	}
	
	public void removeFilter(Integer number)
	{
		metricFilters.remove(number);
		reorderFilters();
	}
	
	public ResultSet getClickCostDistribution()
	{
		ResultSet clicks = null;
		
		try 
		{
			Connection conn = DriverManager.getConnection("jdbc:sqlite:database/campaign.db", frame.getController().getSQLiteConfig().toProperties());
		
			Statement stmt3 = conn.createStatement();
			clicks = stmt3.executeQuery("SELECT ROUND(cost)/100, COUNT(cost) FROM clicks GROUP BY ROUND(cost);");
		}
		catch (SQLException sqle) 
		{
			sqle.printStackTrace();
		}
		
		return clicks;
	}
	
	public MyFrame getFrame()
	{
		return frame;
	}
	
	public void setMetricFilterPanel(MetricFilterPanel metricFilterPanel) 
	{
		this.metricFilterPanel = metricFilterPanel;
	}

	public MetricFilterPanel getMetricFilterPanel() 
	{
		return metricFilterPanel;
	}
	
	public void setMetricTablePanel(MetricTablePanel metricTablePanel) 
	{
		this.metricTablePanel = metricTablePanel;
	}
	
	public MetricTablePanel getMetricTablePanel()
	{
		return metricTablePanel;
	}
	
	public Map<Integer, MetricFilter> getFilters()
	{
		return metricFilters;
	}
	
	public void setStartDate(String startDate) 
	{
		this.startDate = startDate;
	}
	
	public String getStartDate() 
	{
		return startDate;
	}
	
	public void setEndDate(String endDate) 
	{
		this.endDate = endDate;
	}
	
	public String getEndDate() 
	{
		return endDate;
	}
	
	public int getFilterCount() 
	{
		return metricFilters.size();
	}
}
