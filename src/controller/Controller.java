package controller;
import javax.swing.SwingUtilities;
import org.sqlite.SQLiteConfig;

import gui.MyFrame;
import model.Database;

public class Controller 
{
	private Database dbModel;
	private MyFrame view;
	
	public Controller(Database dbModel, MyFrame view)
	{
		this.dbModel = dbModel;
		this.view = view;
	}
	
	public void updateView()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run() 
			{
				view.init();
			}
		});
	}
	
	public Database getDbModel() 
	{
		return dbModel;
	}
	
	public MyFrame getView() 
	{
		return view;
	}
	
	
	public boolean createDatabase(String dbName)
	{
		return dbModel.createDatabase(dbName);
	}
	
	public void createTables(String name)
	{	
		dbModel.createTables(name);
	}	
	
	public void importFiles(String path, String name)
	{
		dbModel.importFiles(path, name);
	}
	
	public void endProgressBar()
	{
		view.getLoadPanel().endProgressBar();
	}
	
	public void endProgressBar2()
	{
		view.getLoadPanel().endProgressBar2();
	}
	
	public SQLiteConfig getSQLiteConfig()
	{
		return dbModel.getSQLiteConfig();
	}
	
	
}
