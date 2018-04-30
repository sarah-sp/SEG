package gui;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.RootPaneContainer;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.FontUIResource;

import model.Theme;

public class LoadPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	private MyFrame frame;
	private JFrame progressFrame;
	private Component glassPane; 
	private JPanel panel;
	private JPanel centralPanel;
	private JPanel buttonPanel;
	private JButton load;
	private Font font; 
	private boolean dbExists;

	public LoadPanel(MyFrame frame, JPanel panel)
	{
		this.frame = frame;
		this.panel = panel;
		centralPanel = new JPanel();
		dbExists = true;
		init();
	}
	
	public void init()
	{
		font = new Font("Arial", Font.PLAIN, 18);
		
		load = new JButton("Load Campaign");
		load.setBackground(Theme.ACTIVE_BG);
		load.setPreferredSize(new Dimension(200, 50));
		load.setFocusPainted(false);
		load.setFont( new Font("Courier", Font.BOLD, 18));
		
		buttonPanel = new JPanel();
		buttonPanel.setBackground(Theme.ACTIVE_BG);
		buttonPanel.add(load);
		
		centralPanel.setBackground(Theme.ACTIVE_BG);
		centralPanel.add(buttonPanel);

		dbExists = frame.getController().createDatabase("campaign.db");
		
		if(!dbExists) //If a new database
		{
			frame.getController().createTables("campaign.db"); 
		}
		
		setBorder(new EmptyBorder(15, 15, 15, 15));
		setBackground(Theme.ACTIVE_BG);
		add(centralPanel);

		load.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if(dbExists)
				{
					frame.openClient();
				}
				else
				{
					load();
				}
			}
		});
	}
	
	public void load()
	{
		UIManager.put("OptionPane.minimumSize", new Dimension(300,150));
		UIManager.put("OptionPane.messageFont", new FontUIResource(font));
		
		File file = new File("log_files");
		//File file = new File("2_Month/log_files");
		String path = file.getPath();
			
		if (new File(path + "/click_log.csv").exists() && new File(path + "/impression_log.csv").exists() && new File(path + "/server_log.csv").exists())
		{
			startProgressBar();
			frame.getController().importFiles(path, "campaign.db");
		}
		else
		{
			//JOptionPane.showMessageDialog(panel, "Please select a folder containing: click_log.csv, impression_log.csv and server_log.csv");
			File folder = chooseFolder();
			
			while (folder != null) 
			{
				path = folder.getAbsolutePath();
				
				if (new File(path + "/click_log.csv").exists() && new File(path + "/impression_log.csv").exists() && new File(path + "/server_log.csv").exists())
				{
					startProgressBar();
					frame.getController().importFiles(path, "campaign.db");
					break;
				}
				else 
				{
					displayWrongDirectoryDialog(folder.getName());
					folder = chooseFolder();
				}
			}
		}		 
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
	
	
	public void startProgressBar()
	{
		load.setEnabled(false);
		
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
		frame.openClient();
	}
	
	public void endProgressBar2()
	{
		glassPane.setVisible(false);
		progressFrame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
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

	public void displayWrongDirectoryDialog(String dName)
	{
		JLabel label = new JLabel("You tried to import the directory " + dName + " which didn't have the correct log files in it.\n Please select another folder.");
		label.setFont(font);
		JOptionPane.showMessageDialog(this, label, "Incorrect File Selected", JOptionPane.WARNING_MESSAGE);
	}
}