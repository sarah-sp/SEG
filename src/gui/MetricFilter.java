package gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.MaskFormatter;

import gui.custom.CustomButton;
import model.FilterStorage;
import model.Theme;

public class MetricFilter extends JPanel implements BorderInterface
{
	private static final long serialVersionUID = 1L;
	private static final int FILTER_WIDTH = 275;
	private static final int FILTER_HEIGHT = 450;
    public JComboBox<String> metricsBox;
    public JComboBox<String> selectGender;
    public JComboBox<String> selectAge;
    public JComboBox<String> selectIncome;
    public JComboBox<String> selectContext;
//    public JTextField startDate;
//    public JTextField endDate;
    private JLabel label, selectMetricLabel, startLabel, endLabel,refineLabel, genderLabel, ageLabel, incomeLabel, contextLabel;
    private FilterStorage storage;
    private String value = null;
    private int filterIndex;
    private CustomButton remove;
    private JPanel topPanel, contentPanel, metricPanel, datePanel, refinePanel;
    private String query;
    public JFormattedDateTextField startDate, endDate;

	public MetricFilter(FilterStorage storage)
	{	
		this.storage = storage;
		filterIndex = storage.getFilterCount() + 1;
		init();	
	}

	public void refreshBorder(Color fg){
		this.setBorder(new CompoundBorder(new EmptyBorder(5, 5, 5, 5), new CompoundBorder(new LineBorder(fg, 1), new EmptyBorder(5, 5, 5, 5))));
	}

	
	public void init()
	{
		this.setBackground(Theme.ACTIVE_BG);
		this.setBorder(new CompoundBorder(new EmptyBorder(5, 5, 5, 5), new CompoundBorder(new LineBorder(Theme.ACTIVE_FG, 1), new EmptyBorder(5, 5, 5, 5))));
		setLayout(new BorderLayout());
		setMaximumSize(new Dimension (FILTER_WIDTH, FILTER_HEIGHT));
		
		String[] metrics = {"None", "Number of Impressions", "Number of Clicks", "Number of Uniques", "Number of Bounces", "Number of Conversions", "Total Cost", "Click-through Rate (CTR)", "Cost-per-acquisition (CPA)", "Cost-per-click (CPC)", "Cost-per-thousand impressions (CPM)", "Bounce Rate"};
		String[] gender = {"All", "Male", "Female"};
		String[] age = {"All", "<25", "25-34", "35-44", "45-54", ">54"};
		String[] income = {"All", "Low", "Medium", "High"};
		String[] contexts = {"All", "Blog", "Hobbies", "News", "Shopping", "Social Media", "Travel"};
	
		topPanel = new JPanel(new BorderLayout());
		contentPanel = new JPanel();
		metricPanel = new JPanel();
		datePanel = new JPanel();
		refinePanel = new JPanel();
		
		selectMetricLabel = new JLabel("<html><u><b>Select Metric:</b></u></html>");
		selectMetricLabel.setForeground(Theme.ACTIVE_FG);
		
		startLabel = new JLabel("<html><u><b>From Date (Year/Month/Date):</b></u></html>");
		startLabel.setForeground(Theme.ACTIVE_FG);
		
		endLabel = new JLabel("<html><u><b>To Date (Year/Month/Date):</b></u></html>");
		endLabel.setForeground(Theme.ACTIVE_FG);
		
		refineLabel = new JLabel("<html><u><b>Refine By:</b></u></html>");
		refineLabel.setForeground(Theme.ACTIVE_FG);
		
		genderLabel = new JLabel("Gender:");
		genderLabel.setForeground(Theme.ACTIVE_FG);
		
		ageLabel = new JLabel("Age Range:");
		ageLabel.setForeground(Theme.ACTIVE_FG);
		
		incomeLabel = new JLabel("Income:");
		incomeLabel.setForeground(Theme.ACTIVE_FG);
		
		contextLabel = new JLabel("Context:");
		contextLabel.setForeground(Theme.ACTIVE_FG);
		
		label = new JLabel("Metric No. " + filterIndex);
		label.setForeground(Theme.ACTIVE_FG);
		
		remove = new CustomButton("Delete");
		remove.setPreferredSize(new Dimension(65,25));
		remove.setFontSize(15);
		remove.setFocusPainted(false);
		remove.setForeground(Theme.ACTIVE_FG);
		
		remove.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				storage.removeFilter(filterIndex);
				storage.reorderFilters();
				storage.getMetricFilterPanel().updateMetricPanel();
				storage.getMetricTablePanel().updateTableRecords();
				storage.getFrame().getMainPanel().getMenuPanel().getGrahpPanel().updateFilterList();
			}	
		});
				
		metricsBox = new JComboBox<String>(metrics);
		metricsBox.setForeground(Theme.ACTIVE_FG);
//	    startDate = new JTextField(storage.getStartDate().substring(0,10));
//	    startDate.setForeground(Theme.ACTIVE_FG);
//	    startDate.setHorizontalAlignment(JTextField.CENTER);
//	    startDate.setFont(metricsBox.getFont());    
//	    endDate = new JTextField(storage.getEndDate().substring(0,10));
//	    endDate.setHorizontalAlignment(JTextField.CENTER);
//	    endDate.setForeground(Theme.ACTIVE_FG);
//	    endDate.setFont(metricsBox.getFont());
	    selectGender = new JComboBox<String>(gender);
	    selectGender.setForeground(Theme.ACTIVE_FG);
	    selectAge = new JComboBox<String>(age);
	    selectAge.setForeground(Theme.ACTIVE_FG);
	    selectIncome = new JComboBox<String>(income);
	    selectIncome.setForeground(Theme.ACTIVE_FG);
	    selectContext = new JComboBox<String>(contexts);
	    selectContext.setForeground(Theme.ACTIVE_FG);
	    
	    startDate = new JFormattedDateTextField();
	    startDate.setForeground(Theme.ACTIVE_FG);

	    startDate.setText(storage.getStartDate().substring(0,10));
	    startDate.setFont(metricsBox.getFont());
	    
	    
	    endDate = new JFormattedDateTextField();
	    endDate.setForeground(Theme.ACTIVE_FG);
	    endDate.setText(storage.getEndDate().substring(0,10));
	    endDate.setFont(metricsBox.getFont());
	    
	    ArrayList<JComboBox<String>> changeListeners = new ArrayList<>();
	    changeListeners.add(metricsBox);
	    changeListeners.add(selectGender);
	    changeListeners.add(selectAge);
	    changeListeners.add(selectIncome);
	    changeListeners.add(selectContext);
	   
	    
	    for (JComboBox<String> obj : changeListeners)
	    {
			obj.addItemListener(new ItemListener()
			{
				@Override
				public void itemStateChanged(ItemEvent arg0) 
				{
					value = null;
				}
			});
	    }
	    
	    startDate.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void changedUpdate(DocumentEvent arg0){value = null;}
			@Override
			public void insertUpdate(DocumentEvent arg0){value = null;}
			@Override
			public void removeUpdate(DocumentEvent arg0){value = null;}
		});
	    
	    endDate.getDocument().addDocumentListener(new DocumentListener()
		{
	    	@Override
			public void changedUpdate(DocumentEvent arg0){value = null;}
			@Override
			public void insertUpdate(DocumentEvent arg0){value = null;}
			@Override
			public void removeUpdate(DocumentEvent arg0){value = null;}
		});
		
	    metricPanel.setLayout(new BoxLayout(metricPanel, BoxLayout.PAGE_AXIS));
	    metricPanel.add(Box.createVerticalStrut(10));
	    metricPanel.add(leftLabel(selectMetricLabel));
	    metricPanel.add(Box.createVerticalStrut(5));
	    metricPanel.add(metricsBox);
	    
	    datePanel.setLayout(new BoxLayout(datePanel, BoxLayout.PAGE_AXIS));
	    datePanel.add(Box.createVerticalStrut(10));
	    datePanel.add(leftLabel(startLabel));
	    datePanel.add(Box.createVerticalStrut(5));
		datePanel.add(startDate);
		datePanel.add(Box.createVerticalStrut(10));
		datePanel.add(leftLabel(endLabel));
		datePanel.add(Box.createVerticalStrut(5));
		datePanel.add(endDate);
		
		refinePanel.setLayout(new BoxLayout(refinePanel, BoxLayout.PAGE_AXIS));
		refinePanel.add(Box.createVerticalStrut(10));
		refinePanel.add(leftLabel(refineLabel));
		refinePanel.add(Box.createVerticalStrut(5));
		refinePanel.add(leftLabel(genderLabel));
		refinePanel.add(selectGender);
		refinePanel.add(Box.createVerticalStrut(5));
		refinePanel.add(leftLabel(ageLabel));
		refinePanel.add(selectAge);
		refinePanel.add(Box.createVerticalStrut(5));
		refinePanel.add(leftLabel(incomeLabel));
		refinePanel.add(selectIncome);
		refinePanel.add(Box.createVerticalStrut(5));
		refinePanel.add(leftLabel(contextLabel));
		refinePanel.add(selectContext);
		
		topPanel.add(label, BorderLayout.WEST);
		topPanel.add(remove, BorderLayout.EAST);
		
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
		contentPanel.add(metricPanel);
		contentPanel.add(datePanel);
		contentPanel.add(refinePanel);
		
		topPanel.setBackground(Theme.ACTIVE_BG);
		contentPanel.setBackground(Theme.ACTIVE_BG);
		metricPanel.setBackground(Theme.ACTIVE_BG);
		datePanel.setBackground(Theme.ACTIVE_BG);
		refinePanel.setBackground(Theme.ACTIVE_BG);
		metricsBox.setBackground(Theme.ACTIVE_BG);
		startDate.setBackground(Theme.ACTIVE_BG);
		endDate.setBackground(Theme.ACTIVE_BG);
		selectGender.setBackground(Theme.ACTIVE_BG);
		selectAge.setBackground(Theme.ACTIVE_BG);
		selectIncome.setBackground(Theme.ACTIVE_BG);
		selectContext.setBackground(Theme.ACTIVE_BG);
		
		add(topPanel, BorderLayout.NORTH);
		add(contentPanel, BorderLayout.CENTER);	
	}
	
	public void changeTheme(Container container, Color bg, Color fg, Color hover) {
		this.setBackground(bg);
		
		for(Component c : container.getComponents()) {
			
			c.setBackground(bg);
			c.setForeground(fg);
			
			if(c instanceof Container) {
				Container con = (Container) c;
				changeTheme(con,bg,fg, hover);
			}
		}	
	}
	
	public void setValue(String value)
	{
		this.value = value;
	}
	
	public String getValue()
	{
		return value;
	}
	
	private Component leftLabel(JLabel label)  
	{
	    Box  b = Box.createHorizontalBox();
	    b.add(label);
	    b.add(Box.createHorizontalGlue());

	    return b;
	}
	
	public void setLabelText(String text)
	{
		label.setText(text);
	}
	
	public void setFilterIndex(int newIndex)
	{
		filterIndex = newIndex;
	}
	
	public int getFilterIndex()
	{
		return filterIndex;
	}
	
	public void setQuery(String query)
	{
		this.query = query;
	}
	
	public String getQuery()
	{
		return query;
	}
	
	public String getFilterDetails()
	{
		String labelText = filterIndex + " - " + selectGender.getSelectedItem().toString() + " " + selectAge.getSelectedItem().toString() + " " + 
						   selectIncome.getSelectedItem().toString() + " " + selectContext.getSelectedItem().toString();
		
		return labelText;
	}
	
	
	 public class JFormattedDateTextField extends JFormattedTextField 
	 {
		 Format format = new SimpleDateFormat("yyyy-MM-dd");
		 boolean invalidInput;
		  
		 public JFormattedDateTextField() 
		 {
			 super();
		     invalidInput = false;
		     MaskFormatter maskFormatter = null;
		     try 
		     {
		    	 maskFormatter = new MaskFormatter("####-##-##");
		     } 
		     catch (ParseException pe) 
		     {
		         pe.printStackTrace();
		     }
		  
		     maskFormatter.setPlaceholderCharacter('_');
		     setFormatterFactory(new DefaultFormatterFactory(maskFormatter));
		     addFocusListener(new FocusAdapter() 
		     {
		         public void focusGained(FocusEvent e) 
		         {
		        	 if (getFocusLostBehavior() == JFormattedTextField.PERSIST)
		        	 {
		        		 setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
		        	 }
		         }   
		      });
		            
		        
		   }
		   
		   public void setValue(Date date) 
		   {
			      super.setValue(toString(date));
		   }
			  
		   private Date toDate(String sDate) 
		   {
			    Date date = null;
			    if (sDate == null) return null;
			      
			    try 
			    {
			        date = (Date) format.parseObject(sDate);
			    }
			    catch (ParseException pe) 
			    {
			        pe.printStackTrace();
			    }
			  
		        return date;
		   }
			  
		   private String toString(Date date) 
		   {
		        try 
			    {
			        return format.format(date);
			    } 
			    catch (Exception e) 
		        {
	
			    	return "";
			    }
	      }
	}
}

