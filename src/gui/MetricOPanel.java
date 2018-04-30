package gui;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import model.Theme;

public class MetricOPanel extends JPanel 
{
	private static final long serialVersionUID = 1L;
	private MyFrame frame;
	private JPanel centre, filler, logoPanel;
	private JTable table;
	private JLabel logo;
	
	public MetricOPanel(MyFrame frame)
	{
		this.frame = frame;
		init();
	}
	
	public void init()
	{
		setBounds(getBounds());
		setBackground(Theme.ACTIVE_BG);
		setLayout(new BorderLayout());
		
		Font font = new Font("Courier", Font.BOLD, 24);
		
		centre = new JPanel();
		filler = new JPanel();
		logoPanel = new JPanel();
		
		centre.setLayout(new GridBagLayout());
		centre.setBackground(Theme.ACTIVE_BG);
		filler.setBackground(Theme.ACTIVE_BG);
		logoPanel.setLayout(new GridBagLayout());
		logoPanel.setBackground(Theme.ACTIVE_BG);
		
		String[] columns = {"Metric", "Value"}; //Column Names
		Object[][] data = new Object[11][2]; //2D Array of data
		data[0][0] = "Number of Impressions:"; 
		data[1][0] = "Number of Clicks:"; 
		data[2][0] = "Number of Uniques:"; 
		data[3][0] = "Number of Bounces:"; 
		data[4][0] = "Number of Conversions:"; 
		data[5][0] = "Total Cost:"; 
		data[6][0] = "Click-through Rate (CTR):"; 
		data[7][0] = "Cost-per-acquisition (CPA):"; 
		data[8][0] = "Cost-per-click (CPC):"; 
		data[9][0] = "Cost-per-thousand Impressions (CPM):"; 
		data[10][0] = "Bounce Rate:"; 
		
		String numberOfImpressions = "SELECT COUNT() FROM impressions;";
		String numberOfClicks = "SELECT COUNT() FROM clicks;";
		String numberOfUniques = "SELECT COUNT(DISTINCT ID) AS no_ids FROM clicks;";
		String numberOfBouncesPages = "SELECT COUNT() FROM server_log WHERE pgs_viewed == 1 AND conversion == 'No';";
		String numberOfBouncesTime = "SELECT COUNT() FROM server_log WHERE conversion = 'No' AND strftime('%s', exit_date) - strftime('%s', entry_date) < 5;";
		String numberOfConversions = "SELECT COUNT() FROM server_log WHERE conversion == 'Yes';";
		String totalImpressionCost = "SELECT SUM(cost) FROM impressions;";
		String totalClickCost = "SELECT SUM(cost) FROM clicks;";
		
		DecimalFormat df = new DecimalFormat("#.###");
		df.setRoundingMode(RoundingMode.HALF_UP);
		
		DecimalFormat df2 = new DecimalFormat("#.##");
		df2.setRoundingMode(RoundingMode.HALF_UP);
        
        try 
        {
        	Connection conn = DriverManager.getConnection("jdbc:sqlite:database/campaign.db", frame.getController().getSQLiteConfig().toProperties());
            Statement stmt  = conn.createStatement();
            ResultSet rs1    = stmt.executeQuery(numberOfImpressions);
            data[0][1] = rs1.getInt(1);
            ResultSet rs2    = stmt.executeQuery(numberOfClicks);
            data[1][1] = rs2.getInt(1);
            ResultSet rs3    = stmt.executeQuery(numberOfUniques);
            data[2][1] = rs3.getInt(1);
            
            switch (frame.getBounceDef())
            {
	            case "pages":
	            	ResultSet rs4a    = stmt.executeQuery(numberOfBouncesPages);
	                data[3][1] = rs4a.getInt(1);
	                break;
	                
	            case "time":
	            	ResultSet rs4b    = stmt.executeQuery(numberOfBouncesTime);
	            	data[3][1] = rs4b.getInt(1);
	            	break;
            }
            
            ResultSet rs5    = stmt.executeQuery(numberOfConversions);
            data[4][1] = rs5.getInt(1);
            ResultSet rs6    = stmt.executeQuery(totalImpressionCost);
            double impressionCost = Double.parseDouble(rs6.getString(1));
            ResultSet rs7    = stmt.executeQuery(totalClickCost);
            double clickCost = Double.parseDouble(rs7.getString(1));
            double totalCost = (impressionCost + clickCost)/100;
            data[5][1] = "\u00A3"+ df2.format(totalCost);
    		double ctr = Double.parseDouble(data[1][1].toString()) / Double.parseDouble(data[0][1].toString());
            data[6][1] = df.format(ctr);
            double cpa = totalCost/Double.parseDouble(data[4][1].toString());
            data[7][1] = "\u00A3"+ df2.format(cpa);
            double cpc = totalCost/Double.parseDouble(data[1][1].toString());
            data[8][1] = "\u00A3"+ df2.format(cpc);
            double cpm = totalCost/(Double.parseDouble(data[0][1].toString())/1000);
            data[9][1] = "\u00A3"+ df2.format(cpm);
            double bounceRate = Double.parseDouble(data[3][1].toString())/(Double.parseDouble(data[1][1].toString()));
            data[10][1] = df.format(bounceRate);           
            conn.close();
           
        } 
        catch (SQLException sqle) 
        {
        	sqle.getMessage();
        }

        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
		rightRenderer.setHorizontalAlignment(JLabel.RIGHT);
        
		table = new JTable(data, columns);
		table.setFillsViewportHeight(true); //Fill height
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);//Select only one at a time
		table.getTableHeader().setReorderingAllowed(false);//Disallow columns to be re ordered
		table.setDefaultEditor(Object.class, null);//Stops editing
		table.setShowGrid(false);
		table.getColumnModel().getColumn(1).setCellRenderer(rightRenderer);
		table.setFont(font);
		table.addMouseMotionListener(new MouseMotionAdapter()
		{
			public void mouseMoved(MouseEvent e) 
			{
				int row = table.rowAtPoint(e.getPoint());
				
				if (row > -1)
				{
					table.clearSelection();
					table.setRowSelectionInterval(row, row);
					table.setSelectionBackground(Theme.ACTIVE_HOVER);
				} else 
				{
					
				}
			}
		});
		
		table.addMouseListener(new MouseAdapter()
		{
			public void mouseExited(MouseEvent e)
			{
				table.clearSelection();
			}
		});
		
		for(int i = 0; i < 11; i++)
		{
			table.setRowHeight(i, 60);
		}
		
		TableColumn column1 = table.getColumnModel().getColumn(0);
		TableColumn column2 = table.getColumnModel().getColumn(1);
		column1.setPreferredWidth(600);
		column2.setPreferredWidth(300);

		GridBagConstraints c = new GridBagConstraints();

		for (int i = 0; i < 2; i++)
		{
			for (int j = 0; j < 2; j++)
			{
				if (i != 1 && j != 1)
				{
					c.gridx = i;
					c.gridy = j;
					c.fill = GridBagConstraints.BOTH;
					centre.add(filler, c);
					logoPanel.add(filler, c);
				}
			}
		}

		c.gridx = 1;
		c.gridy = 1;
		c.fill = GridBagConstraints.BOTH;
		centre.add(table, c);
		
		logo = new JLabel("Metrics", JLabel.CENTER);
		logo.setFont(new Font("Above DEMO", Font.BOLD,40));
		
		logoPanel.add(logo, c);
		setBorder(new EmptyBorder(15,15,15,15));		
		add(logoPanel, BorderLayout.NORTH);
		add(centre, BorderLayout.CENTER);
	}
}