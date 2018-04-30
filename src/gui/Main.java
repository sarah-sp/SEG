package gui;
import controller.Controller;
import model.Database;

/**
 * Software Engineering Group Project - Ad Auction Dashboard
 * @author Ryan Gregory
 */

/* --- DATABASE LOADING ---
 MyFrame - constructor
 Controller - deleteDB
 LoadPanel - init createDB and tables
 Database - importFiles
*/

/* --- FILTERING IMPROVEMENTS ---
 TOTAL COST, CTR,CPC, CPA, CPM NOT EFFICIENT (~8 seconds on 2 weeks)
 2 WEEK CAMPAIGN LOAD TIME = 00:20
 2 MONTH CAMPAIGN LOAD TIME = 10:20
 INDEXING NEEDS RE-DOING
 https://sqlite.org/queryplanner.html
 https://sqlite.org/optoverview.html
 https://www.sqlite.org/eqp.html
 .timer
 Use Context Index over ID Index
 */

/*

https://www.whoishostingthis.com/compare/sqlite/optimize/

- INDEX after INSERT

- Consider denormalization

/*
try 
{
    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
} catch (Exception e) {}
*/

public class Main  
{
	public static void main(String[] args)
	{		
		MyFrame view = new MyFrame("Dash");
		Database dbModel = new Database();
		Controller controller = new Controller(dbModel, view);
		
		view.setController(controller);
		dbModel.setController(controller);
		controller.updateView();
	}
}