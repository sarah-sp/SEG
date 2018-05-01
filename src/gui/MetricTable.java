package gui;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
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
import java.text.SimpleDateFormat;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import model.Theme;

public class MetricTable extends JTable 
{
	private static final long serialVersionUID = 1L;
	private Object[] data;
	private String[] COLUMNS = {"No.", "Metric", "Start Date", "End Date", "Gender", "Age", "Income", "Context", "Value"};
	private MyFrame frame;
	private Statement stmt;
	private DecimalFormat df2;
	private DefaultTableModel tableModel;
	private DefaultTableCellRenderer cellRenderer;
	private SimpleDateFormat dateFormat;
	private Color hover;

	public MetricTable(MyFrame frame)
	{	
		this.frame = frame;
		data = new Object[9];
		dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		configure();
		hover = Theme.ACTIVE_HOVER;
		
//		JTableHeader header = this.getTableHeader();
//	    header.setBackground(Color.cyan);
//	    header.setForeground(Color.blue);
	}
	
	public void setHoverColor(Color c) {
		hover = c;
	}
	
	@Override
    public boolean isCellEditable(int row, int column) 
	{
		return false;
    }
	
	@Override
	public TableCellRenderer getCellRenderer (int row, int column) 
	{
	    return cellRenderer;
	}
	
	@Override
	public void setRowHeight(int row, int column)
	{
		setRowHeight(row, 50);
	}
	
	public DefaultTableModel getTableModel()
	{
		return tableModel;
	}
	
	public void addMetricRow(MetricFilter filter)
	{			
		String startDate = filter.startDate.getText();
		String endDate = filter.endDate.getText();
		
		System.out.println(startDate);
		
		data[0] = filter.getFilterIndex();
		data[1] = filter.metricsBox.getSelectedItem();
		data[2] = startDate;
		data[3] = endDate;
		data[4] = filter.selectGender.getSelectedItem();
		data[5] = filter.selectAge.getSelectedItem();
		data[6] = filter.selectIncome.getSelectedItem();
		data[7] = filter.selectContext.getSelectedItem();
		data[8] = filter.getValue() == null ? getResult(filter) : filter.getValue();
		//If filter stays the same then value is stored so query doesn't have to be run again
		
		tableModel.addRow(data);
		tableModel.fireTableDataChanged();
	}

	public void configure() 
	{	
		tableModel = (DefaultTableModel) getModel();
		tableModel.setColumnIdentifiers(COLUMNS);
		
		cellRenderer = new DefaultTableCellRenderer();
		cellRenderer.setHorizontalAlignment(JLabel.CENTER);
		
		TableColumn column0 = getColumnModel().getColumn(0);
		TableColumn column1 = getColumnModel().getColumn(1);
		TableColumn column2 = getColumnModel().getColumn(2);
		TableColumn column3 = getColumnModel().getColumn(3);
		TableColumn column4 = getColumnModel().getColumn(4);
		TableColumn column5 = getColumnModel().getColumn(5);
		TableColumn column6 = getColumnModel().getColumn(6);
		TableColumn column7 = getColumnModel().getColumn(7);
		TableColumn column8 = getColumnModel().getColumn(8);

		
		column0.setPreferredWidth(40);
		column1.setPreferredWidth(380);
		column2.setPreferredWidth(120);
		column3.setPreferredWidth(110);
		column4.setPreferredWidth(70);
		column5.setPreferredWidth(70);
		column6.setPreferredWidth(70);
		column7.setPreferredWidth(130);
		column8.setPreferredWidth(100);
		
		column0.setCellRenderer(cellRenderer);
		column1.setCellRenderer(cellRenderer);
		column2.setCellRenderer(cellRenderer);
		column3.setCellRenderer(cellRenderer);
		column4.setCellRenderer(cellRenderer);
		column5.setCellRenderer(cellRenderer);
		column6.setCellRenderer(cellRenderer);
		column7.setCellRenderer(cellRenderer);
		column8.setCellRenderer(cellRenderer);
		
		Font font = new Font("Courier", Font.BOLD, 18);
		
		setFont(font);
		getTableHeader().setFont(font);
		
		setRowHeight(30);
		getTableHeader().setPreferredSize(new Dimension(getTableHeader().getWidth(), 45));
        setFillsViewportHeight(true); //Fill height
        setCellSelectionEnabled(false);
        setRowSelectionAllowed(true);	
        getTableHeader().setReorderingAllowed(false); //Disallow columns to be re ordered
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		addMouseMotionListener(new MouseMotionAdapter()
		{
			public void mouseMoved(MouseEvent e) 
			{
				int row = rowAtPoint(e.getPoint());
				
				if (row > -1)
				{
					clearSelection();
					setRowSelectionInterval(row, row);
					setSelectionBackground(hover);
				
				} 
			}
		});
		
		addMouseListener(new MouseAdapter()
		{
			public void mouseExited(MouseEvent e)
			{
				clearSelection();
			}
		});
		
	}
	 
	private Object getResult(MetricFilter filter) 
	{
//			String noImpressions = "SELECT COUNT() FROM (SELECT DISTINCT impressions.ID, impressions.date FROM impressions JOIN users ON impressions.ID = users.ID WHERE 1";
//			String noClicks = "SELECT COUNT() FROM (SELECT DISTINCT clicks.ID, clicks.date FROM clicks JOIN users ON clicks.ID = users.ID JOIN impressions ON impressions.ID = users.ID WHERE 1";
//			String noUniques = "SELECT COUNT() FROM (SELECT DISTINCT clicks.ID FROM clicks JOIN users ON clicks.ID = users.ID JOIN impressions ON impressions.ID = users.ID WHERE 1";
//			String noBouncesPage = "SELECT COUNT() FROM (SELECT DISTINCT server_log.ID, server_log.entry_date FROM server_log JOIN users ON server_log.ID = users.ID JOIN impressions ON impressions.ID = users.ID WHERE pgs_viewed = 1 AND conversion = 'No'";
//			String noBouncesTime = "SELECT COUNT() FROM (SELECT DISTINCT server_log.ID, server_log.entry_date FROM server_log JOIN users ON server_log.ID = users.ID JOIN impressions ON impressions.ID = users.ID WHERE conversion = 'No' AND strftime('%s', server_log.exit_date) - strftime('%s', server_log.entry_date) < 5";
//			String noConversions = "SELECT COUNT() FROM (SELECT DISTINCT server_log.ID, server_log.entry_date FROM server_log JOIN users ON server_log.ID = users.ID JOIN impressions ON impressions.ID = users.ID WHERE conversion == 'Yes'";
			
			String result = null;
			String noImpressions = "SELECT COUNT() FROM impressions WHERE (1";
			String noClicks      = "SELECT COUNT() FROM (SELECT DISTINCT clicks.date, clicks.ID, clicks.cost FROM clicks JOIN impressions ON impressions.ID = clicks.ID WHERE 1";
			String noUniques     = "SELECT COUNT() FROM (SELECT DISTINCT clicks.ID FROM clicks JOIN impressions ON impressions.ID = clicks.ID WHERE 1";
			String noBouncesPage = "SELECT COUNT() FROM (SELECT DISTINCT server_log.ID, server_log.entry_date FROM server_log JOIN impressions ON impressions.ID = server_log.ID WHERE pgs_viewed = 1 AND conversion = 'No'";
			String noBouncesTime = "SELECT COUNT() FROM (SELECT DISTINCT server_log.ID, server_log.entry_date FROM server_log JOIN impressions ON impressions.ID = server_log.ID WHERE conversion = 'No' AND strftime('%s', server_log.exit_date) - strftime('%s', server_log.entry_date) < 5";
			String noConversions = "SELECT COUNT() FROM (SELECT DISTINCT server_log.ID, server_log.entry_date FROM server_log JOIN impressions ON impressions.ID = server_log.ID WHERE conversion = 'Yes'";
			
			DecimalFormat df = new DecimalFormat("#.###");
			df2 = new DecimalFormat("#.##");

			df.setRoundingMode(RoundingMode.HALF_UP);
			df2.setRoundingMode(RoundingMode.HALF_UP);
			
			try
			{
				Connection conn = DriverManager.getConnection("jdbc:sqlite:database/campaign.db", frame.getController().getSQLiteConfig().toProperties());
		        stmt = conn.createStatement();
				
				switch(filter.metricsBox.getSelectedItem().toString())
				{
					case "Number of Impressions":
						result = filterQuery(noImpressions, filter);
						break;
					case "Number of Clicks":
						result = filterQuery(noClicks, filter);
						break;
					case "Number of Uniques":
						result = filterQuery(noUniques, filter);
						break;
					case "Number of Bounces":
						result = frame.getBounceDef().equals("Pages") ? filterQuery(noBouncesPage, filter) : filterQuery(noBouncesTime, filter);
						break;
					case "Number of Conversions":
						result = filterQuery(noConversions, filter);
						break;
					case "Total Cost":
				        result = "\u00A3"+ getTotalCost(filter);
						break;				
					case "Click-through Rate (CTR)": //Number of Clicks / Number of Impressions
						double ctr = Double.parseDouble(filterQuery(noClicks, filter)) / Double.parseDouble(filterQuery(noImpressions, filter));
				        result = df.format(ctr);
						break;
					case "Cost-per-acquisition (CPA)": //Total Cost / No of Conversions
						double cpa = Double.parseDouble(getTotalCost(filter))/Double.parseDouble(filterQuery(noConversions, filter));
				        result = "\u00A3"+ df2.format(cpa);
						break;
					case "Cost-per-click (CPC)": //Total Cost / No of Clicks
						double cpc = Double.parseDouble(getTotalCost(filter))/Double.parseDouble(filterQuery(noClicks, filter));
				        result = "\u00A3"+ df2.format(cpc);
						break;
					case "Cost-per-thousand impressions (CPM)": //Total Cost / No of Impressions/1000
						double cpm = Double.parseDouble(getTotalCost(filter))/(Double.parseDouble(filterQuery(noImpressions, filter))/1000);
				        result = "\u00A3"+ df2.format(cpm);
				        break;				
					case "Bounce Rate": //No of bounces / No of clicks
						double bounceDef = frame.getBounceDef().equals("Pages") ? Double.parseDouble(filterQuery(noBouncesPage, filter)) : Double.parseDouble(filterQuery(noBouncesTime, filter));
						double bounceRate = bounceDef /(Double.parseDouble(filterQuery(noClicks, filter)));
				        result = df.format(bounceRate); 
				        break;
				}
			}
			catch(SQLException e)
			{
				e.printStackTrace();
			}
			
			filter.setValue(result);
			
			return result;
	}
		
	public String getTotalCost(MetricFilter filter) throws NumberFormatException, SQLException
	{
		String query1 = "SELECT SUM(cost) FROM impressions WHERE (1";
		String query2 = "SELECT SUM(cost) FROM (SELECT DISTINCT clicks.date, clicks.ID, clicks.cost FROM clicks JOIN impressions ON impressions.ID = clicks.ID WHERE 1";
			
		double impressionCost = Double.parseDouble(filterQuery(query1, filter));
		double clickCost = Double.parseDouble(filterQuery(query2, filter));
			
		return df2.format((impressionCost + clickCost)/100);
	}
		
	public String filterQuery(String query, MetricFilter filter) throws SQLException
	{	
		String startDate = filter.startDate.getText();
		String endDate = filter.endDate.getText();
		
		//bounces, conversions
		query += " AND strftime('%Y-%m-%d', impressions.date) >= strftime('%Y-%m-%d', '" + startDate + "') AND strftime('%Y-%m-%d', impressions.date) <= strftime('%Y-%m-%d', '" + endDate + "')";
		ResultSet rs;
		
		if(filter.selectGender.getSelectedItem().equals("All") && filter.selectAge.getSelectedItem().equals("All") && filter.selectIncome.getSelectedItem().equals("All") && filter.selectContext.getSelectedItem().equals("All"))
		{
			query += ");";
	        rs = stmt.executeQuery(query);
		        
	        return rs.getString(1);
		}
		else
		{
			if(! filter.selectGender.getSelectedItem().equals("All"))
			{
				query += " AND impressions.gender = '" + filter.selectGender.getSelectedItem() +"'";
			}
				
			if(!filter.selectAge.getSelectedItem().equals("All"))
			{
				query += " AND impressions.age = '" + filter.selectAge.getSelectedItem() +"'";
			}
				
			if(!filter.selectIncome.getSelectedItem().equals("All"))
			{
				query += " AND impressions.income = '" + filter.selectIncome.getSelectedItem() +"'";
			}
				
			if(!filter.selectContext.getSelectedItem().equals("All"))
			{
				query += " AND impressions.context = '" + filter.selectContext.getSelectedItem() +"'";
			}

			query += ");";
		    rs = stmt.executeQuery(query);
		       
		    return rs.getString(1);
		}
	}
}
