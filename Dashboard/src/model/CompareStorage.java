package model;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

import org.jfree.chart.JFreeChart;

public class CompareStorage 
{
	private ArrayList<String> campaigns = new ArrayList<>();
	private String currentCampaign;
	private JFreeChart chart = null;
	
	public CompareStorage()
	{
		setCurrentCampaign(null);
		findCampaigns();
	}
	
	public void findCampaigns()
	{
		campaigns.clear();
		Path dir = Paths.get("database/");//Create path to search
		File file = new File(dir.toFile().toString());
		File[] contents = file.listFiles(); //Get messages in inbox
		
		for (File newFile : contents)
		{
			campaigns.add(newFile.toString().substring(9));
		}
	}

	public ArrayList<String> getCampaigns() 
	{
		return campaigns;
	}

	public String getCurrentCampaign()
	{
		return currentCampaign;
	}

	public void setCurrentCampaign(String currentCampaign) 
	{
		this.currentCampaign = currentCampaign;
	}

	public JFreeChart getChart() 
	{
		return chart;
	}

	public void setChart(JFreeChart chart) 
	{
		this.chart = chart;
	}
}
