package model;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.SwingWorker;

import org.sqlite.SQLiteConfig;

import controller.Controller;

public class Database
{
	private Controller controller;
	private Connection conn;
	private SQLiteConfig config;
	
	public void setController(Controller controller)
	{
		this.controller = controller;
		configureSQLite();
	}
	
	public SQLiteConfig getSQLiteConfig() 
	{
		return config;
	}
	
	public void configureSQLite()
	{
		config = new SQLiteConfig();
		
		config.setSynchronous(SQLiteConfig.SynchronousMode.OFF);
		config.setJournalMode(SQLiteConfig.JournalMode.MEMORY);
		config.setTempStore(SQLiteConfig.TempStore.MEMORY);
		config.setCacheSize(500000);
		config.setLockingMode(SQLiteConfig.LockingMode.EXCLUSIVE);
	}

	public boolean createDatabase(String dbName) 
	{		
		File dir = new File("database");
		File dB = new File("database/" + dbName);
		
		if(dB.exists())
		{
			return true;
		}
		else
		{
			dir.mkdirs();
			return false;
		}
    }
	
	public void createTables(String name)
	{
		try 
		{	
			conn = DriverManager.getConnection("jdbc:sqlite:database/" + name, config.toProperties());
			Statement stmt = conn.createStatement();
			
			String createStmt = "CREATE TABLE IF NOT EXISTS impressions(date DATE, ID TEXT, gender TEXT, age TEXT, income TEXT, context TEXT, cost REAL);"
							  + "CREATE TABLE IF NOT EXISTS clicks(date DATE, ID TEXT, cost REAL);"
							  + "CREATE TABLE IF NOT EXISTS server_log(entry_date DATE, ID TEXT, exit_date DATE, pgs_viewed INTEGER, conversion TEXT);";

			stmt.executeUpdate(createStmt);
			conn.close();
		} 
		catch (SQLException sqle) 
		{
			sqle.printStackTrace();
		}
	}
	
	public void createIndexes(String name) 
	{
		try 
		{
			Connection conn = DriverManager.getConnection("jdbc:sqlite:database/" + name, config.toProperties());
			Statement stmt = conn.createStatement();
			String indexStmt = "CREATE INDEX impressionsID ON impressions(ID, date);"
					         + "CREATE INDEX impressionsGender ON impressions(context, age, income, gender);"
			                 + "CREATE INDEX impressionsCost ON impressions(cost);"	
					         + "CREATE INDEX clicksID ON clicks(ID, date, cost);"
					         + "CREATE INDEX serverID ON server_log(ID, entry_date);";
			
			stmt.executeUpdate(indexStmt);
			conn.close();
		} 
		catch (SQLException sqle) 
		{
			sqle.printStackTrace();
		}
	}
	
	public void importFiles(String path, String name)
	{
		final SwingWorker<Void, Void> myWorker = new SwingWorker<Void, Void>() 
		{
			protected Void doInBackground() throws Exception 
			{
				importCSV(path + "/impression_log.csv", "impressions", name);
				importCSV(path + "/click_log.csv", "clicks", name);
				importCSV(path + "/server_log.csv", "server_log", name);
				createIndexes(name);
				
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
		    		controller.endProgressBar();
		    		
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
	
	public void importCSV(String filePath, String table, String name)
	{
		try
		{
			Connection conn = DriverManager.getConnection("jdbc:sqlite:database/" + name, config.toProperties());
			conn.setAutoCommit(false);
			
			PreparedStatement pstmt1 = null;
			BufferedReader br = new BufferedReader(new FileReader(filePath));
			
			String line;
	        String sql1 = "INSERT INTO impressions(date, ID, gender, age, income, context, cost) VALUES(?,?,?,?,?,?,?);";
	        String sql2 = "INSERT INTO clicks(date, ID, cost) VALUES(?,?,?);";
	        String sql3 = "INSERT INTO server_log(entry_date, ID, exit_date, pgs_viewed, conversion) VALUES(?,?,?,?,?);";
	        
	        int counter = 0;
	        
			switch (table)
			{
				case "impressions":
						pstmt1 = conn.prepareStatement(sql1);
				        br.readLine();
				        
				        while ((line = br.readLine()) != null)
						{
						     String[] values = line.split(",");	
						     pstmt1.setString(1, values[0]);//ID
						     pstmt1.setString(2, values[1]);//ID					     					     
						     pstmt1.setString(3, values[2]);//Gender
						     pstmt1.setString(4, values[3]);//Age
						     pstmt1.setString(5, values[4]);//Income
						     pstmt1.setString(6, values[5]);//Context
						     pstmt1.setString(7, values[6]);//Cost
							     
							 pstmt1.addBatch();
							 counter++;
							     
							 if(counter > 999)
							 {
								 pstmt1.executeBatch();
								 counter = 0;
							 }
						}
				        
				        br.close();
						pstmt1.executeBatch();
						break;
					
				case "clicks":
				        pstmt1 = conn.prepareStatement(sql2); 
				        br.readLine();
				        
				        while ((line = br.readLine()) != null)
						{
						     String[] values = line.split(",");			     
						     pstmt1.setString(1, values[0]);//Date
						     pstmt1.setString(2, values[1]);//ID
						     pstmt1.setString(3, values[2]);//Cost
							     
							 pstmt1.addBatch();
							 counter++;
							     
							 if(counter > 999)
							 {
								 pstmt1.executeBatch();
								 counter = 0;
							 }
						}
				        
				        br.close();
						pstmt1.executeBatch();
						break;
					
				case "server_log":
			        	pstmt1 = conn.prepareStatement(sql3); 
			        	br.readLine();
			        
				        while ((line = br.readLine()) != null)
						{
						     String[] values = line.split(",");			     
						     pstmt1.setString(1, values[0]); //Entry Date
						     pstmt1.setString(2, values[1]); //ID
						     pstmt1.setString(3, values[2]); //Exit Date
						     pstmt1.setString(4, values[3]); //Pages Viewed
						     pstmt1.setString(5, values[4]); //Conversion
							     
							 pstmt1.addBatch();
							 counter++;
							     
							 if(counter > 999)
							 {
								 pstmt1.executeBatch();
								 counter = 0;
							 }
						}
				        
				        br.close();
						pstmt1.executeBatch();
					    break;
			}
			conn.commit();
			conn.close();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
}
