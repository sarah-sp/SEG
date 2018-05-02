package gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.RootPaneContainer;
import javax.swing.SwingWorker;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import gui.custom.CustomPanel;
import gui.custom.CustomButton;

import model.CompareStorage;
import model.Theme;


public class ComparePanel extends JPanel implements BorderInterface
{
	private static final long serialVersionUID = 1L;
	private MyFrame frame;
	private JFrame progressFrame;
	private Component glassPane; 
	private Font font;
	private CompareStorage cStorage;
	private JComboBox<String> campaign, metrics, granularity;
	private JPanel centralPanel;
	private JFreeChart lineChart;
	private String dbName;
	
	public ComparePanel(MyFrame frame, JPanel panel, CompareStorage cStorage)
	{
		this.frame = frame;
		this.cStorage = cStorage;
		font = new Font("Arial", Font.PLAIN, 18);
		
		setBounds(getBounds());
		setBackground(Color.WHITE);
		setBorder(new EmptyBorder(15, 15, 15, 15));
		setLayout(new BorderLayout());
		
		CustomPanel topPanel = new CustomPanel(frame, panel,"Compare Campaigns");

		JPanel leftPanel = new JPanel();
		JPanel leftDisplay = new JPanel();
		centralPanel = new JPanel();
		JPanel buttonPanel = new JPanel();
		JPanel updatePanel = new JPanel();
		JPanel label1 = new JPanel();
		JPanel label2 = new JPanel();
		JPanel label3 = new JPanel();
		JLabel labelC = new JLabel("Select Campaign to Compare:");
		JLabel labelM = new JLabel("Select Metric to Compare:");
		JLabel labelG = new JLabel("Select Granularity:");
		CustomButton addCampaign = new CustomButton("Add Campaign");
		CustomButton update = new CustomButton("Update");
		String[] metricChoices = {"None", "Number of Impressions", "Number of Clicks", "Number of Uniques", "Number of Bounces", "Number of Conversions", "Total Cost"};
		String[] granularities = {"Day", "Week", "Month"};
		metrics = new JComboBox<String>(metricChoices);
		granularity = new JComboBox<String>(granularities);
		ImageIcon exitImage = new ImageIcon("img/arrow.jpg");
		JLabel exitLabel = new JLabel("", exitImage, JLabel.CENTER);
		
		addCampaign.setPreferredSize(new Dimension(120,30));
		addCampaign.setFontSize(13);
		
		update.setPreferredSize(new Dimension(120,30));
		update.setFontSize(13);
		
		
		campaign = new JComboBox<>();
		for(String s : cStorage.getCampaigns())
		{
			campaign.addItem(s);
		}
				
		centralPanel.setBackground(Color.WHITE);
		
		label1.add(labelC);
		label2.add(labelM);
		label3.add(labelG);
		buttonPanel.add(addCampaign);
		updatePanel.add(update);
		
		leftDisplay.setLayout(new BoxLayout(leftDisplay, BoxLayout.Y_AXIS));
		leftDisplay.setBackground(Color.WHITE);
		leftDisplay.add(Box.createVerticalStrut(20));
		leftDisplay.add(label1);
		leftDisplay.add(Box.createVerticalStrut(5));
		leftDisplay.add(campaign);
		leftDisplay.add(Box.createVerticalStrut(5));
		leftDisplay.add(buttonPanel);
		leftDisplay.add(Box.createVerticalStrut(20));
		leftDisplay.add(label2);
		leftDisplay.add(Box.createVerticalStrut(5));
		leftDisplay.add(metrics);
		leftDisplay.add(Box.createVerticalStrut(20));
		leftDisplay.add(label3);
		leftDisplay.add(Box.createVerticalStrut(5));
		leftDisplay.add(granularity);
		leftDisplay.add(Box.createVerticalStrut(20));
		leftDisplay.add(updatePanel);
		
		leftPanel.setLayout(new BorderLayout());
		leftPanel.add(leftDisplay, BorderLayout.NORTH);
		leftPanel.setBorder(new EmptyBorder(15,15,15,15));
		
		centralPanel.setBorder(new LineBorder(Color.BLACK, 3));
		
		setBackground(Color.WHITE);
		topPanel.setBackground(Color.WHITE);
		leftPanel.setBackground(Color.WHITE);
		leftDisplay.setBackground(Color.WHITE);
		centralPanel.setBackground(Color.WHITE);
		buttonPanel.setBackground(Color.WHITE);
		updatePanel.setBackground(Color.WHITE);
		label1.setBackground(Color.WHITE);
		label2.setBackground(Color.WHITE);
		label3.setBackground(Color.WHITE);
		labelC.setBackground(Color.WHITE);
		labelM.setBackground(Color.WHITE);
		labelG.setBackground(Color.WHITE);
		add(topPanel, BorderLayout.NORTH);
		add(leftPanel, BorderLayout.WEST);
		add(centralPanel, BorderLayout.CENTER);
		
		if(cStorage.getChart()!=null)
		{
			lineChart = cStorage.getChart();
			updateGraph(lineChart);
		}
		

		
		addCampaign.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				File folder = chooseFolder();
				
				while (folder != null) 
				{
					String path = folder.getAbsolutePath();
					
					if (new File(path + "/click_log.csv").exists() && new File(path + "/impression_log.csv").exists() && new File(path + "/server_log.csv").exists())
					{	
						boolean dbExists = true;
						String dbName = null;
						

						while(dbExists)
						{
							dbName = (String) JOptionPane.showInputDialog(frame, "Enter New Campaign Name:", "Name Campaign", JOptionPane.PLAIN_MESSAGE, null, null, "name");
							
							
							dbExists = frame.getController().createDatabase(dbName + ".db");

							dbExists = frame.getController().createDatabase(dbName + ".db");
							
							

						}
						startProgressBar();
						frame.getController().createTables(dbName + ".db");
						importFiles(path, dbName + ".db");
						ComparePanel.this.dbName = dbName;
						break;
					}
					else 
					{
						displayWrongDirectoryDialog(folder.getName());
						folder = chooseFolder();
					}
				}
			}		
		});
		
		update.addMouseListener(new MouseAdapter() 
		{
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				cStorage.setCurrentCampaign((String) campaign.getSelectedItem());
				if(!metrics.getSelectedItem().equals("None"))
				{
					updateGraph(null);
					cStorage.setChart(lineChart);
					JOptionPane.showMessageDialog(frame, "Updated.");
				}
				else
				{
					JOptionPane.showMessageDialog(frame, "Comparison Campaign Updated But No Graph Generated.");
				}
			}
		});
		
		exitLabel.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseReleased(MouseEvent e) 
			{
				displayMainMenu();
			}
			
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				displayMainMenu();
			}
				
			public void mouseEntered(MouseEvent e)
			{
				exitLabel.setToolTipText("Back");
			}
		
		});
	}
	
	private void updateGraph(JFreeChart lineChart) 
	{
		if(lineChart!=null)
		{
			centralPanel.removeAll();
			centralPanel.setLayout(new BorderLayout());
			ChartPanel chartPanel = new ChartPanel(lineChart);
			centralPanel.add(chartPanel);
			frame.revalidate();
		}
		else
		{
			centralPanel.removeAll();
			centralPanel.setLayout(new BorderLayout());
			this.lineChart = ChartFactory.createXYLineChart((String)metrics.getSelectedItem(), "Value-Per-"+granularity.getSelectedItem(), (String)metrics.getSelectedItem(), getDataset(), PlotOrientation.VERTICAL, true, true, false);
			XYPlot plot = this.lineChart.getXYPlot();
			NumberAxis xAxis = (NumberAxis) plot.getDomainAxis();
			switch ((String) granularity.getSelectedItem()) 
			{
				case "Day":
					xAxis.setTickUnit(new NumberTickUnit(2));
					break;
				case "Week":
					xAxis.setTickUnit(new NumberTickUnit(1));
					break;
				case "Month":
					xAxis.setTickUnit(new NumberTickUnit(1));
					break;
			}
			
			ChartPanel chartPanel = new ChartPanel(this.lineChart);
			centralPanel.add(chartPanel);
			frame.revalidate();
		}
	}

	private XYDataset getDataset() 
	{
		XYSeriesCollection dataset = new XYSeriesCollection();
		XYSeries yourCampaign = new XYSeries("Your Campaign");
		XYSeries otherCampaign = new XYSeries((String)campaign.getSelectedItem());
		
		String numberOfImpressionsD 	= "SELECT strftime('%j', date) AS day, COUNT(*) FROM impressions GROUP BY day";
		String numberOfImpressionsW 	= "SELECT strftime('%W', date) AS week, COUNT(*) FROM impressions GROUP BY week";
		String numberOfImpressionsM 	= "SELECT strftime('%m', date) AS month, COUNT(*) FROM impressions GROUP BY month";
		
		String numberOfClicksD 	    = "SELECT strftime('%j', date) AS day, COUNT(*) FROM clicks GROUP BY day;";
		String numberOfClicksW 	    = "SELECT strftime('%W', date) AS week, COUNT(*) FROM clicks GROUP BY week;";
		String numberOfClicksM 	    = "SELECT strftime('%m', date) AS month, COUNT(*) FROM clicks GROUP BY month;";
		
		String numberOfUniquesD 	= "SELECT strftime('%j', date) AS day, COUNT(DISTINCT ID) AS no_ids FROM clicks GROUP BY day;";
		String numberOfUniquesW 	= "SELECT strftime('%W', date) AS week, COUNT(DISTINCT ID) AS no_ids FROM clicks GROUP BY week;";
		String numberOfUniquesM 	= "SELECT strftime('%m', date) AS month, COUNT(DISTINCT ID) AS no_ids FROM clicks GROUP BY month;";
		
		String numberOfBouncesPagesD = "SELECT strftime('%j', entry_date) AS day, COUNT(*) FROM server_log WHERE pgs_viewed == 1 AND conversion == 'No' GROUP BY day;";
		String numberOfBouncesPagesW = "SELECT strftime('%W', entry_date) AS week, COUNT(*) FROM server_log WHERE pgs_viewed == 1 AND conversion == 'No' GROUP BY week;";
		String numberOfBouncesPagesM = "SELECT strftime('%m', entry_date) AS month, COUNT(*) FROM server_log WHERE pgs_viewed == 1 AND conversion == 'No' GROUP BY month;";

		String numberOfBouncesTimeD 	= "SELECT strftime('%j', entry_date) AS day, COUNT(*) FROM server_log WHERE conversion = 'No' AND strftime('%s', exit_date) - strftime('%s', entry_date) < 5 GROUP BY day;";
		String numberOfBouncesTimeW 	= "SELECT strftime('%W', entry_date) AS week, COUNT(*) FROM server_log WHERE conversion = 'No' AND strftime('%s', exit_date) - strftime('%s', entry_date) < 5 GROUP BY week;";
		String numberOfBouncesTimeM 	= "SELECT strftime('%m', entry_date) AS month, COUNT(*) FROM server_log WHERE conversion = 'No' AND strftime('%s', exit_date) - strftime('%s', entry_date) < 5 GROUP BY month;";

		String numberOfConversionsD 	= "SELECT strftime('%j', entry_date) AS day, COUNT(*) FROM server_log WHERE conversion == 'Yes' GROUP BY day;";
		String numberOfConversionsW 	= "SELECT strftime('%W', entry_date) AS week, COUNT(*) FROM server_log WHERE conversion == 'Yes' GROUP BY week;";
		String numberOfConversionsM 	= "SELECT strftime('%m', entry_date) AS month, COUNT(*) FROM server_log WHERE conversion == 'Yes' GROUP BY month;";

		String totalCostD 				= "SELECT strftime('%j', impressions.date) AS day, SUM(impressions.cost + clicks.cost) FROM impressions JOIN clicks ON impressions.ID = clicks.ID GROUP BY day;";
		String totalCostW 				= "SELECT strftime('%W', impressions.date) AS week, SUM(impressions.cost + clicks.cost) FROM impressions JOIN clicks ON impressions.ID = clicks.ID GROUP BY week;";
		String totalCostM 				= "SELECT strftime('%m', impressions.date) AS month, SUM(impressions.cost + clicks.cost) FROM impressions JOIN clicks ON impressions.ID = clicks.ID GROUP BY month;";

		try 
		{
			Connection conn = DriverManager.getConnection("jdbc:sqlite:database/campaign.db", frame.getController().getSQLiteConfig().toProperties());
	    	Connection conn2 = DriverManager.getConnection("jdbc:sqlite:database/" + cStorage.getCurrentCampaign(), frame.getController().getSQLiteConfig().toProperties());
	    	
	    	Statement stmt  = conn.createStatement();
            Statement stmt2  = conn2.createStatement();
            
            ResultSet rs1 = null, rs2 = null;
            
            switch((String) metrics.getSelectedItem())
			{
				case "Number of Impressions":
					switch((String) granularity.getSelectedItem())
					{
						case "Day":
							rs1    = stmt.executeQuery(numberOfImpressionsD);
				            rs2    = stmt2.executeQuery(numberOfImpressionsD);
							break;
						case "Week":
							rs1    = stmt.executeQuery(numberOfImpressionsW);
				            rs2    = stmt2.executeQuery(numberOfImpressionsW);
							break;
						case "Month":
							rs1    = stmt.executeQuery(numberOfImpressionsM);
				            rs2    = stmt2.executeQuery(numberOfImpressionsM);
							break;
					}
					break;
				case "Number of Clicks":
					switch((String) granularity.getSelectedItem())
					{
						case "Day":
							rs1    = stmt.executeQuery(numberOfClicksD);
				            rs2    = stmt2.executeQuery(numberOfClicksD);
							break;
						case "Week":
							rs1    = stmt.executeQuery(numberOfClicksW);
				            rs2    = stmt2.executeQuery(numberOfClicksW);
							break;
						case "Month":
							rs1    = stmt.executeQuery(numberOfClicksM);
				            rs2    = stmt2.executeQuery(numberOfClicksM);
							break;
					}
					break;
				case "Number of Uniques":
					switch((String) granularity.getSelectedItem())
					{
						case "Day":
							rs1    = stmt.executeQuery(numberOfUniquesD);
				            rs2    = stmt2.executeQuery(numberOfUniquesD);
							break;
						case "Week":
							rs1    = stmt.executeQuery(numberOfUniquesW);
				            rs2    = stmt2.executeQuery(numberOfUniquesW);
							break;
						case "Month":
							rs1    = stmt.executeQuery(numberOfUniquesM);
				            rs2    = stmt2.executeQuery(numberOfUniquesM);
							break;
					}
		            break;
				case "Number of Bounces":
					switch((String) granularity.getSelectedItem())
					{
						case "Day":
							if(frame.getBounceDef().equals("Pages"))
							{
								rs1    = stmt.executeQuery(numberOfBouncesPagesD);
								rs2    = stmt2.executeQuery(numberOfBouncesPagesD);
							}
							else
							{
								rs1    = stmt.executeQuery(numberOfBouncesTimeD);
								rs2    = stmt2.executeQuery(numberOfBouncesTimeD);
							}
							break;
						case "Week":
							if(frame.getBounceDef().equals("Pages"))
							{
								rs1    = stmt.executeQuery(numberOfBouncesPagesW);
								rs2    = stmt2.executeQuery(numberOfBouncesPagesW);
							}
							else
							{
								rs1    = stmt.executeQuery(numberOfBouncesTimeW);
								rs2    = stmt2.executeQuery(numberOfBouncesTimeW);
							}
							break;
						case "Month":
							if(frame.getBounceDef().equals("Pages"))
							{
								rs1    = stmt.executeQuery(numberOfBouncesPagesM);
								rs2    = stmt2.executeQuery(numberOfBouncesPagesM);
							}
							else
							{
								rs1    = stmt.executeQuery(numberOfBouncesTimeM);
								rs2    = stmt2.executeQuery(numberOfBouncesTimeM);
							}
							break;
					}
		            break;
				case "Number of Conversions":
					switch((String) granularity.getSelectedItem())
					{
						case "Day":
							rs1    = stmt.executeQuery(numberOfConversionsD);
				            rs2    = stmt2.executeQuery(numberOfConversionsD);
							break;
						case "Week":
							rs1    = stmt.executeQuery(numberOfConversionsW);
				            rs2    = stmt2.executeQuery(numberOfConversionsW);
							break;
						case "Month":
							rs1    = stmt.executeQuery(numberOfConversionsM);
				            rs2    = stmt2.executeQuery(numberOfConversionsM);
							break;
					}
		            break;
				case "Total Cost":
					switch((String) granularity.getSelectedItem())
					{
						case "Day":
							rs1    = stmt.executeQuery(totalCostD);
				            rs2    = stmt2.executeQuery(totalCostD);
							break;
						case "Week":
							rs1    = stmt.executeQuery(totalCostW);
				            rs2    = stmt2.executeQuery(totalCostW);
							break;
						case "Month":
							rs1    = stmt.executeQuery(totalCostM);
				            rs2    = stmt2.executeQuery(totalCostM);
							break;
					}
					break;				
			}
            
            yourCampaign.add(0, 0);
            otherCampaign.add(0, 0);
            
            while(rs1.next())
            {
    	    	yourCampaign.add(Double.parseDouble(rs1.getString(1)), rs1.getDouble(2));
            }
            
            while(rs2.next())
            {
    	    	otherCampaign.add(Double.parseDouble(rs2.getString(1)), rs2.getDouble(2));
            }
            
            dataset.addSeries(yourCampaign);
            dataset.addSeries(otherCampaign);
		} catch (SQLException e) {e.printStackTrace();}
		return dataset;
	}

	public File chooseFolder()
	{
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.showOpenDialog(null);
		setFileChooserFont(fileChooser.getComponents());
		File folder = fileChooser.getSelectedFile();
		
		return folder;
	}
	
	public void displayWrongDirectoryDialog(String dName)
	{
		JLabel label = new JLabel("You tried to import the directory " + dName + " which didn't have the correct log files in it.\n Please select another folder.");
		label.setFont(font);
		JOptionPane.showMessageDialog(this, label, "Incorrect File Selected", JOptionPane.WARNING_MESSAGE);
	}
	
	public void setFileChooserFont(Component[] comp)
	{
		for(int x = 0; x < comp.length; x++)
	    {
			if (comp[x] instanceof Container) 
			{	
				setFileChooserFont(((Container)comp[x]).getComponents());
			}
			
			try
			{
				comp[x].setFont(font.deriveFont(20.0f));
			}
			catch(Exception e)
			{	
				e.printStackTrace();  
			}
	    }
	}
	
	public String getName()
	{
		return null;
	}
	
	public void displayMainMenu()
	{
		frame.refreshMain();
	}

	private void getCampaigns() 
	{
		campaign.removeAllItems();
		for(String s : cStorage.getCampaigns())
		{
			campaign.addItem(s);
		}
		campaign.setSelectedItem(dbName+".db");
	}
	
	public void importFiles(String path, String name)
	{
		final SwingWorker<Void, Void> myWorker = new SwingWorker<Void, Void>() 
		{
			protected Void doInBackground() throws Exception 
			{
				frame.getController().getDbModel().importCSV(path + "/impression_log.csv", "impressions", name);
				frame.getController().getDbModel().importCSV(path + "/click_log.csv", "clicks", name);
				frame.getController().getDbModel().importCSV(path + "/server_log.csv", "server_log", name);
				frame.getController().getDbModel().createIndexes(name);
				return null;
			}
		};
		
		myWorker.addPropertyChangeListener(new PropertyChangeListener() 
		{
			@Override
			public void propertyChange(PropertyChangeEvent cE) 
			{
		         if (cE.getNewValue() == SwingWorker.StateValue.DONE) 
		         {
		        	endProgressBar();
		    		
		            try 
		            {
		               myWorker.get();
		            }
		            catch (Exception e) 
		            {
		            	e.printStackTrace();
		            }
		         }				
			}
		});
		
		myWorker.execute();
	}
	
	private void startProgressBar()
	{
		progressFrame = new JFrame("Loading data");
		progressFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		progressFrame.setSize(300, 100);
		progressFrame.setLocationRelativeTo(null);
		progressFrame.setVisible(true);
				
		JProgressBar progressBar = new JProgressBar(0, 100);
		Container content = progressFrame.getContentPane();
		content.add(progressBar, BorderLayout.NORTH);
		
		Border border = BorderFactory.createTitledBorder("Importing Log Files...");
		glassPane = ((RootPaneContainer) getTopLevelAncestor()).getGlassPane();
		glassPane.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		glassPane.setVisible(true);

		progressBar.setIndeterminate(true);
		progressBar.setBorder(border);	
	}
	
	public void endProgressBar()
	{
		glassPane.setVisible(false);
		progressFrame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
		cStorage.findCampaigns();
		getCampaigns();
		frame.revalidate();
		frame.repaint();
	}

	@Override
	public void refreshBorder(Color bg) {
		centralPanel.setBorder(new LineBorder(Theme.ACTIVE_FG, 3));
		
	}
}