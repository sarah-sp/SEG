package gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
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

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;

import model.CompareStorage;
import model.Theme;

public class CompareOPanel extends JPanel 
{
	private static final long serialVersionUID = 1L;
	private CompareStorage cStorage;
	private MyFrame frame;
	private JTable table;
	
	public CompareOPanel(MyFrame frame, CompareStorage cStorage)
	{
		this.frame = frame;
		this.cStorage = cStorage;
		
		setLayout(new BorderLayout());
		
		JPanel notLoaded = new JPanel(new BorderLayout());
		JPanel centre = new JPanel();
		
		Font font = new Font("Courier", Font.BOLD,32);
		
		JLabel label = new JLabel("Secondary Campaign Currently Not Selected For Comparison");
		label.setFont(font);
		
		notLoaded.add(label, BorderLayout.CENTER);
		notLoaded.setBackground(Color.WHITE);
		centre.setBackground(Color.WHITE);

		JPanel fillerPanel = new JPanel();
		fillerPanel.setLayout(new GridBagLayout());
		fillerPanel.setBackground(Color.WHITE);
		
		JPanel filler = new JPanel();
		JPanel logoPanel = new JPanel(new GridBagLayout());
		filler.setBackground(Theme.ACTIVE_BG);
		logoPanel.setBackground(Theme.ACTIVE_BG);
		
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
					fillerPanel.add(filler, c);
					logoPanel.add(filler, c);
				}
			}
		}


		c.gridx = 1;
		c.gridy = 1;
		c.fill = GridBagConstraints.BOTH;
		
		if(this.cStorage.getCurrentCampaign()!=null)
		{
			createTable();
			JScrollPane scrollPane = new JScrollPane(table);
			fillerPanel.add(scrollPane, c);
			add(scrollPane, BorderLayout.CENTER);
		}
		else
		{
			fillerPanel.add(notLoaded, c);
			add(fillerPanel, BorderLayout.CENTER);
		}
		
		JLabel logo1 = new JLabel("Compare Campaigns", JLabel.CENTER);

		logo1.setFont(new Font("Above DEMO", Font.BOLD,40));
		logoPanel.add(logo1, c);

		setBorder(new EmptyBorder(15,15,15,15));
		add(logoPanel, BorderLayout.NORTH);
	}
	
	public void createTable()
	{
		Font font = new Font("Courier", Font.BOLD, 24);

		String[] columns = {"Metric", "Campaign Value", cStorage.getCurrentCampaign() + " Value"}; //Column Names
		Object[][] data = new Object[11][3]; //2D Array of data
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
        	Connection conn2 = DriverManager.getConnection("jdbc:sqlite:database/" + cStorage.getCurrentCampaign(), frame.getController().getSQLiteConfig().toProperties());

            Statement stmt  = conn.createStatement();
            Statement stmt2  = conn2.createStatement();
            
            ResultSet rs1    = stmt.executeQuery(numberOfImpressions);
            ResultSet rs1b    = stmt2.executeQuery(numberOfImpressions);
            data[0][1] = rs1.getInt(1);
            data[0][2] = rs1b.getInt(1);
            
            ResultSet rs2    = stmt.executeQuery(numberOfClicks);
            ResultSet rs2b    = stmt2.executeQuery(numberOfClicks);
            data[1][1] = rs2.getInt(1);
            data[1][2] = rs2b.getInt(1);
            
            ResultSet rs3    = stmt.executeQuery(numberOfUniques);
            ResultSet rs3b    = stmt2.executeQuery(numberOfUniques);
            data[2][1] = rs3.getInt(1);
            data[2][2] = rs3b.getInt(1);
            
            switch (frame.getBounceDef())
            {
	            case "pages":
	            	ResultSet rs4a    = stmt.executeQuery(numberOfBouncesPages);
	            	ResultSet rs4a2    = stmt2.executeQuery(numberOfBouncesPages);
	                data[3][1] = rs4a.getInt(1);
	                data[3][2] = rs4a2.getInt(1);
	                break;
	                
	            case "time":
	            	ResultSet rs4b    = stmt.executeQuery(numberOfBouncesTime);
	            	ResultSet rs4b2    = stmt2.executeQuery(numberOfBouncesTime);
	            	data[3][1] = rs4b.getInt(1);
	            	data[3][2] = rs4b2.getInt(1);
	            	break;
            }
            
            ResultSet rs5    = stmt.executeQuery(numberOfConversions);
            ResultSet rs5b    = stmt2.executeQuery(numberOfConversions);
            data[4][1] = rs5.getInt(1);
            data[4][2] = rs5b.getInt(1);
            
            ResultSet rs6    = stmt.executeQuery(totalImpressionCost);
            ResultSet rs6b    = stmt2.executeQuery(totalImpressionCost);
            double impressionCost = Double.parseDouble(rs6.getString(1));
            double impressionCostb = Double.parseDouble(rs6b.getString(1));

            ResultSet rs7    = stmt.executeQuery(totalClickCost);
            ResultSet rs7b    = stmt2.executeQuery(totalClickCost);

            double clickCost = Double.parseDouble(rs7.getString(1));
            double clickCostb = Double.parseDouble(rs7b.getString(1));

            double totalCost = (impressionCost + clickCost)/100;
            double totalCostb = (impressionCostb + clickCostb)/100;

            data[5][1] = "�"+ df2.format(totalCost);
            data[5][2] = "�"+ df2.format(totalCostb);

    		double ctr = Double.parseDouble(data[1][1].toString()) / Double.parseDouble(data[0][1].toString());
    		double ctrb = Double.parseDouble(data[1][2].toString()) / Double.parseDouble(data[0][2].toString());

            data[6][1] = df.format(ctr);
            data[6][2] = df.format(ctrb);
            
            double cpa = totalCost/Double.parseDouble(data[4][1].toString());
            double cpab = totalCostb/Double.parseDouble(data[4][2].toString());

            data[7][1] = "�"+ df2.format(cpa);
            data[7][2] = "�"+ df2.format(cpab);
            
            double cpc = totalCost/Double.parseDouble(data[1][1].toString());
            double cpcb = totalCostb/Double.parseDouble(data[1][2].toString());
            
            data[8][1] = "�"+ df2.format(cpc);
            data[8][2] = "�"+ df2.format(cpcb);
            
            double cpm = totalCost/(Double.parseDouble(data[0][1].toString())/1000);
            double cpmb = totalCostb/(Double.parseDouble(data[0][2].toString())/1000);

            data[9][1] = "�"+ df2.format(cpm);
            data[9][2] = "�"+ df2.format(cpmb);
            
            double bounceRate = Double.parseDouble(data[3][1].toString())/(Double.parseDouble(data[1][1].toString()));
            double bounceRateb = Double.parseDouble(data[3][2].toString())/(Double.parseDouble(data[1][2].toString()));

            data[10][1] = df.format(bounceRate); 
            data[10][2] = df.format(bounceRateb); 
            
            conn.close();
        } 
        catch (SQLException sqle) 
        {
        	sqle.getMessage();
        }
        
        DefaultTableCellRenderer centreRenderer = new DefaultTableCellRenderer();
        centreRenderer.setHorizontalAlignment(JLabel.CENTER);
        
		table = new JTable(data, columns);
		table.setFillsViewportHeight(true); //Fill height
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);//Select only one at a time
		table.getTableHeader().setReorderingAllowed(false);//Disallow columns to be re ordered
		table.setDefaultEditor(Object.class, null);//Stops editing
		table.setShowGrid(false);
		table.setFont(font);
		table.getTableHeader().setPreferredSize(new Dimension(table.getTableHeader().getWidth(), 45));
		table.getTableHeader().setBorder(BorderFactory.createLineBorder(Color.BLACK));
		table.getTableHeader().setFont(font);
		table.getColumnModel().getColumn(1).setCellRenderer(centreRenderer);
		table.getColumnModel().getColumn(2).setCellRenderer(centreRenderer);
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
		TableColumn column3 = table.getColumnModel().getColumn(2);

		column1.setPreferredWidth(900);
		column2.setPreferredWidth(400);
		column3.setPreferredWidth(400);
		
//		table.getCellRenderer(1, 1).getTableCellRendererComponent(table, data[1][1], false, false, 1, 1).setBackground(Color.RED);
//		
//		DefaultTableCellRenderer defRender = (DefaultTableCellRenderer) table.getCellRenderer(1, 1);
//		Component cellRenderer = defRender.getTableCellRendererComponent(table, data[1][1], false, false, 1, 1);
//		cellRenderer.setBackground(Color.blue);
		
		
	}
}