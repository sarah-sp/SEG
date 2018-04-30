package gui;
import controller.Controller;
import model.Database;

/**
 * Software Engineering Group Project - Ad Auction Dashboard
 * @author Ryan Gregory
 */

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