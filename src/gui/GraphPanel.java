package gui;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;

import gui.custom.CustomButton;
import gui.custom.CustomPanel;
import model.FilterStorage;
import model.Theme;

public class GraphPanel extends JPanel implements BorderInterface
{
	private static final long serialVersionUID = 1L;
	private static final String[] METRIC_NAMES = initMetricNamesArray(); 
	private static final String[] CONTEXT_NAMES = initContextNamesArray();
	private static final String[] AGE_GROUPS = initAgeGroupArray();
	private static final String[] INCOME_GROUPS = initIncomeGroupsArray();
	private static final String[] CHART_TYPES = initChartTypesArray();
	private static final int CHART_PANEL_HEIGHT = 700;
	private MyFrame frame;
	private CustomPanel customPanel;
	private JPanel topPanel;
	private JPanel leftPanel;
	private JPanel chartHolder;
	private JPanel chartButtonsPanel;
	private JComboBox<String> metrics, chartTypeBox, chartGranBox, chartNames; 
	private JList<Integer> filters;
	private List<JCheckBox> checkBoxes; 
	private List<JLabel> imageList;
	private BufferedImage chartImage;
	private JTextField elementsField, fromField, untilField;
	private JRadioButton yes, no;
	private CustomButton add, remove, group, create, compare, save;
	private Font font;	
	private JPanel metricPanel, metricListPanel;
	
	public GraphPanel(MyFrame frame, JPanel panel)
	{
		this.frame = frame;
		customPanel = new CustomPanel(frame, panel, "Graphs");
		init();
		this.setBorder(new EmptyBorder(15,15,15,15));
		
		//changeTheme(this, Color.GREEN, Color.BLUE, Color.CYAN);
	}
	
	public void refreshBorder(Color fg){
		customisePanel(metricListPanel, "Metric Filter ID's", fg);
		customisePanel(metricPanel, "Metric Name", fg);
		chartHolder.setBorder(BorderFactory.createLineBorder(fg, 3));
	}
	
	
	public static final String[] initMetricNamesArray()
	{
		String[] metrics = {"None", "Number of Impressions", "Number of Clicks", "Number of Uniques", "Number of Bounces", "Number of Conversions", "Total Cost", "Click-through Rate (CTR)", "Cost-per-acquisition (CPA)", "Cost-per-click (CPC)", "Cost-per-thousand impressions (CPM)", "Bounce Rate"};
		
		return metrics;
	}
	
	public static final String[] initContextNamesArray()
	{
		String[] contexts = {"Blog", "Hobbies", "News", "Shopping", "Social Media", "Travel"};
				
		return contexts;
	}
	
	public static final String[] initAgeGroupArray()
	{
		String[] groups = {"<25","25-34", "35-44 ", "45-54", ">54"};
		
		return groups;
	}
	
	public static final String[] initIncomeGroupsArray()
	{
		String[] incGroups = {"High", "Medium", "Low"};
		
		return incGroups;
	}
	
	public static final String[] initChartTypesArray()
	{
		String[] chartTypes = {"Area Chart", "Bar Chart", "Histogram", "Pie Chart", "Pie Chart 3D", "Ring Chart", "Stacked Area Chart", "Stacked Bar Chart"};
		
		return chartTypes;
	}
	
	public void customisePanel(JPanel panel, String borderTitle, Color fg)
	{
		panel.setBackground(fg);
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setBorder(BorderFactory.createTitledBorder(new LineBorder(fg), borderTitle, TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION, getFrame().getFont().deriveFont(12.0f), fg));
	}
	
	public MyFrame getFrame()
	{
		return customPanel.getFrame();
	}
	
	public JPanel getTopPanel()
	{
		return customPanel.getPanel();
	}
	
	public void init()
	{
		UIManager.put("CheckBox.disabledText", Theme.ACTIVE_FG);

		setLayout(new BorderLayout());
		setBackground(Theme.ACTIVE_BG);
		
		topPanel = new JPanel();
		initTopPanel();
		
		leftPanel = new JPanel();
		initLeftPanel();
				
		chartHolder = new JPanel();
		chartHolder.setBackground(Theme.ACTIVE_BG);
		chartHolder.setBorder(BorderFactory.createLineBorder(Theme.ACTIVE_FG, 3));

		chartHolder.setMinimumSize(new Dimension (chartHolder.getWidth(), CHART_PANEL_HEIGHT));//THIS IS THE ISSUE I THINK
		
		chartButtonsPanel = new JPanel();
		initChartButtons();
		
		add(customPanel, BorderLayout.NORTH);
		add(leftPanel, BorderLayout.WEST);
		add(chartHolder, BorderLayout.CENTER);
		add(chartButtonsPanel, BorderLayout.SOUTH);
		
	}
	
	public void initTopPanel()
	{
		topPanel.setLayout(new BorderLayout());
		
		ImageIcon title = new ImageIcon("img/graphs.png");
		ImageIcon exitImage = new ImageIcon("img/arrow.jpg");
		
		JLabel exitLabel = new JLabel("", exitImage, JLabel.CENTER);
		JLabel titleLabel = new JLabel("", title, JLabel.CENTER);
		
		topPanel.add(exitLabel, BorderLayout.WEST);
		topPanel.add(titleLabel, BorderLayout.CENTER);
		
		exitLabel.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e) 
			{
				frame.refreshMain();
			}
			
			public void mouseEntered(MouseEvent e)
			{
				exitLabel.setToolTipText("Back");
			}
		});
	}
	
	public void initLeftPanel()
	{	
		leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.PAGE_AXIS));
		leftPanel.setBackground(Theme.ACTIVE_BG);
		leftPanel.setAlignmentX(Component.LEFT_ALIGNMENT);	
		
		font = new Font("Courier", Font.BOLD, 18);
		
		checkBoxes = new ArrayList<>();
		
		metricPanel = new JPanel();
		initMetricPanel(metricPanel);
		
		metricListPanel = new JPanel();
		initFilterListPanel(metricListPanel);
		
		JPanel chartDatasetPanel = new JPanel();
		chartDatasetPanel.setLayout(new BoxLayout(chartDatasetPanel, BoxLayout.PAGE_AXIS));
		chartDatasetPanel.setBackground(Theme.ACTIVE_BG);
		
		JLabel chartElements = new JLabel(" Chart Dataset Elements");//, JLabel.CENTER);
		chartElements.setBackground(Theme.ACTIVE_BG);
		chartElements.setFont(font);
		
		elementsField = new JTextField();
		elementsField.setBackground(Theme.ACTIVE_BG);
		elementsField.setFont(font);
		elementsField.setEditable(false);
		
		chartDatasetPanel.add(chartElements);
		chartDatasetPanel.add(Box.createVerticalStrut(5));
		chartDatasetPanel.add(elementsField);
		chartDatasetPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		
		JPanel genderPanel = new JPanel();
		initGenderPanel(genderPanel);
		
		JPanel agePanel = new JPanel();
		customisePanel(agePanel, "Age Group", Theme.ACTIVE_FG);
		agePanel.setMaximumSize(new Dimension(80, 145));
		
		for (String ageGroup : AGE_GROUPS)
		{
			JCheckBox checkBox = new JCheckBox(ageGroup);
			checkBox.setBackground(Theme.ACTIVE_BG);
			agePanel.add(checkBox);
			checkBoxes.add(checkBox);
			checkBox.setEnabled(false);
		}
		
		JPanel incomePanel = new JPanel();
		customisePanel(incomePanel, "Income", Theme.ACTIVE_BG);
				
		for(String income : INCOME_GROUPS)
		{
			JCheckBox checkBox = new JCheckBox(income);
			checkBox.setEnabled(false);
			checkBox.setForeground(Theme.ACTIVE_FG);
			checkBox.setBackground(Theme.ACTIVE_BG);
			incomePanel.add(checkBox);
			checkBoxes.add(checkBox);
		}
		
		JPanel contextPanel = new JPanel();
		customisePanel(contextPanel, "Ad Context", Theme.ACTIVE_BG);
		
		for (String name: CONTEXT_NAMES)
		{	
			JCheckBox checkBox = new JCheckBox(name);
			checkBox.setBackground(Theme.ACTIVE_BG);
			contextPanel.add(checkBox);
			checkBoxes.add(checkBox);
			checkBox.setEnabled(false);
		}
	
		JPanel overviewPanel = new JPanel(new BorderLayout());
		overviewPanel.setBackground(Theme.ACTIVE_BG);
		overviewPanel.setMaximumSize(new Dimension(300, 325));
		
		overviewPanel.add(genderPanel, BorderLayout.WEST);
		overviewPanel.add(agePanel, BorderLayout.CENTER);
		overviewPanel.add(incomePanel, BorderLayout.EAST);
		overviewPanel.add(contextPanel, BorderLayout.SOUTH);
		
		JLabel details = new JLabel("Filter Details");
		details.setFont(font);
		details.setAlignmentX(Component.CENTER_ALIGNMENT);
		
		JLabel from = new JLabel("From:");
		from.setFont(font);
		
		fromField = new JTextField();
		fromField.setFont(font);
		fromField.setBackground(Theme.ACTIVE_BG);
		fromField.setEditable(false);
      
		JPanel fromPanel = new JPanel();
		fromPanel.setLayout(new BoxLayout(fromPanel, BoxLayout.LINE_AXIS));
		fromPanel.setBackground(Theme.ACTIVE_BG);
		
		fromPanel.add(from);
		fromPanel.add(Box.createHorizontalStrut(20));
		fromPanel.add(fromField);
	
		fromPanel.setMaximumSize(new Dimension(280, fromPanel.getPreferredSize().height));
		
		JLabel until = new JLabel("Until:");
		until.setFont(font);
		
		untilField = new JTextField();
		untilField.setFont(font);
		untilField.setBackground(Theme.ACTIVE_BG);
		untilField.setEditable(false);
		
		JPanel untilPanel = new JPanel();
		untilPanel.setBackground(Theme.ACTIVE_BG);
		untilPanel.setLayout(new BoxLayout(untilPanel, BoxLayout.LINE_AXIS));
		
		untilPanel.add(until);
		untilPanel.add(Box.createHorizontalStrut(10));
		untilPanel.add(untilField);
	
		untilPanel.setMaximumSize(new Dimension(280, fromPanel.getPreferredSize().height));
		
		JPanel filterButtonsPanel = new JPanel();
		initFilterButtons(filterButtonsPanel);
		
		leftPanel.add(metricPanel);
		leftPanel.add(Box.createVerticalStrut(5));
		leftPanel.add(metricListPanel);
		leftPanel.add(Box.createVerticalStrut(5));
		leftPanel.add(filterButtonsPanel);
		leftPanel.add(Box.createVerticalStrut(5));
		leftPanel.add(chartDatasetPanel);
		leftPanel.add(Box.createVerticalStrut(10));
		leftPanel.add(details);
		leftPanel.add(Box.createVerticalStrut(5));
		leftPanel.add(overviewPanel);
		leftPanel.add(Box.createVerticalStrut(10));
		leftPanel.add(fromPanel);
		leftPanel.add(Box.createVerticalStrut(10));
		leftPanel.add(untilPanel);
		
		leftPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
		
		filters.addListSelectionListener(new ListSelectionListener()
		{
			@Override
			public void valueChanged(ListSelectionEvent e) 
			{
				clearFilterDetails();
					
				if (filters.getSelectedValuesList().size() == 1)
				{	
					add.setEnabled(true);
					displayFilterDetails();
				}
				else
				{
					add.setEnabled(false);
				}
				
				group.setEnabled(filters.getSelectedValuesList().size() > 1);
			}		
		});
	}

	public void initGenderPanel(JPanel genderPanel) 
	{
		customisePanel(genderPanel, "Gender", Theme.ACTIVE_FG);
		genderPanel.setMaximumSize(new Dimension(80, 70));
		
		JCheckBox male = new JCheckBox("Male");
		male.setBackground(Theme.ACTIVE_FG);
		male.setEnabled(false);
		
		JCheckBox female = new JCheckBox("Female");
		female.setBackground(Theme.ACTIVE_FG);
		female.setEnabled(false);
		
		checkBoxes.add(male);
		checkBoxes.add(female);
		
		genderPanel.add(male);
		genderPanel.add(female);
	}

	public void initFilterListPanel(JPanel metricListPanel) 
	{
		customisePanel(metricListPanel, "Metric Filter ID's", Theme.ACTIVE_FG);
		metricListPanel.setMaximumSize(new Dimension(265, 100));
		
		filters = new JList<>(new DefaultListModel<Integer>());
		filters.setPrototypeCellValue(99);
		filters.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		JScrollPane scrollPane = new JScrollPane(filters);
		
		metricListPanel.add(scrollPane);
	}

	public void initMetricPanel(JPanel metricPanel) 
	{
		customisePanel(metricPanel, "Metric Name", Theme.ACTIVE_FG);
		metricPanel.setMaximumSize(new Dimension(265, 60));

		JPanel innerMetricPanel = new JPanel(new BorderLayout());
		innerMetricPanel.setBackground(Theme.ACTIVE_BG);
		innerMetricPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		
		metrics = new JComboBox<>(METRIC_NAMES);
		metrics.setPrototypeDisplayValue(METRIC_NAMES[10]);
		
		metrics.addItemListener(new ItemListener()
		{

			@Override
			public void itemStateChanged(ItemEvent e) 
			{				
				updateFilterList();
				updateMetricChartTypes();
				
				String[] elements = elementsField.getText().split(" ");
				Map<Integer, MetricFilter> metricFilters = frame.fStorage.getFilters();
				
				MetricFilter first = elements[0].equals("") ? null : metricFilters.get(Integer.parseInt(elements[0]));
				
				if (first == null || !first.getValue().equals(metrics.getSelectedItem().toString()))
				{
					create.setEnabled(false);
				}
				
				if (metrics.getSelectedItem().toString().equals("Campaign Click Cost Distribution") && elementsField.getText().isEmpty())
				{
					create.setEnabled(true);
				}
				
			}	
		});
		
		innerMetricPanel.add(metrics);
		metricPanel.add(innerMetricPanel);
	}
	
	public void initFilterButtons(JPanel filterButtonsPanel)
	{
		filterButtonsPanel.setBackground(Theme.ACTIVE_BG);
		filterButtonsPanel.setPreferredSize(new Dimension(265, 40));
		
		add = new CustomButton("Add");
		add.setPreferredSize(new Dimension(75,25));
		add.setFontSize(12);
		group = new CustomButton("Add Group");
		group.setPreferredSize(new Dimension(75,25));
		group.setFontSize(12);
		remove = new CustomButton("Remove");
		remove.setPreferredSize(new Dimension(75,25));
		remove.setFontSize(12);
		
		add.setFocusPainted(false);
		group.setFocusPainted(false);
		remove.setFocusPainted(false);
		
		add.setToolTipText("Add a single filter to the chart dataset");
		group.setToolTipText("Add multiple filters as a single chart dataset element");
		remove.setToolTipText("Remove the first occurrence of the selected filter/s from the chart dataset");
		
		add.setEnabled(false);
		group.setEnabled(false);
		remove.setEnabled(false);
		
		filterButtonsPanel.add(add);
		filterButtonsPanel.add(group);
		filterButtonsPanel.add(remove);
		
		add.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{	
				String idList = elementsField.getText();
					
				for (Integer id : filters.getSelectedValuesList())
				{
					idList += id + " ";
				}
					
				elementsField.setText(idList);
				create.setEnabled(true);
				remove.setEnabled(true);
			}			
		});
		
		group.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				String group = "(";
				
				for (int i = filters.getMinSelectionIndex(); i <= filters.getMaxSelectionIndex(); i++)
				{
					group += filters.getModel().getElementAt(i);
						
							
					if (i < filters.getMaxSelectionIndex())
					{
						group += " ";
					}
					else
					{
						group += ") ";
					}
				}
				
				elementsField.setText(elementsField.getText() + group);
				create.setEnabled(true);
				remove.setEnabled(true);
			}
		});
		
		remove.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{	
				String idList = elementsField.getText();
				
				for (Integer id : filters.getSelectedValuesList())
				{
					idList = removeIdFromList(idList, id);
					idList = removeSingleGroups(idList);
				}
				
				elementsField.setText(idList.trim());
			
				if (elementsField.getText().equals(""))
				{
					System.out.println("In");
					remove.setEnabled(false);
				}
			}	
		});
	}
	
	public String removeSingleGroups(String idList)
	{
		String[] idArray = idList.split(" ");
		String updated = "";
		
		for (String id : idArray)
		{
			if (id.startsWith("(") && id.endsWith(")"))
			{
				updated += id.substring(1, id.length() - 1) + " ";
			}
			else
			{
				updated += id + " ";
			}
		}	
		
		return updated;
	}
	
	public String removeIdFromList(String idList, int id)
	{
		boolean removed = false;
		
		String updated = "";
		
		for (String element : idList.split(" "))
		{
			
			if (element.startsWith("("))
			{
				if (Integer.parseInt(element.substring(1)) == id && removed == false)
				{
					removed = true;
					updated += "(";
				}
				else
				{
					updated += element + " ";
				}
			}
			else if (element.endsWith(")")  && (!element.equals("")))
			{	
				if (Integer.parseInt(element.substring(0, element.length() - 1)) == id && removed == false)
				{
					removed = true;
					updated = updated.substring(0, updated.length() - 1) + ") ";
				}
				else
				{
					updated += element + " ";
				}
			}
			else if (!element.equals(""))
			{
				if (Integer.parseInt(element) == id && removed == false)
				{
					removed = true;
				}
				else
				{
					updated += element + " ";
				}
			}	
		}
		
		return updated;
	}
	
	public void initChartButtons()
	{
		imageList = new ArrayList<>();
		
		chartButtonsPanel.setLayout(new BoxLayout(chartButtonsPanel, BoxLayout.LINE_AXIS));
		//chartButtonsPanel.setPreferredSize(new Dimension(getWidth(), 85));
		chartButtonsPanel.setBackground(Theme.ACTIVE_BG);
		chartButtonsPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));			
		
		JPanel granularityPanel = new JPanel();
		customisePanel(granularityPanel, "Time Granularity", Theme.ACTIVE_BG);
		granularityPanel.setMaximumSize(new Dimension(1400, 60));
		
		String[] options = {"Weeks", "Days", "Hours"};
		
		chartGranBox = new JComboBox<>(options);
		
		JPanel granularityBoxPanel = new JPanel();
		granularityBoxPanel.setBackground(Theme.ACTIVE_FG);
		granularityBoxPanel.add(chartGranBox);
			
		granularityPanel.add(granularityBoxPanel);
		
		JPanel chartTypePanel = new JPanel();
		customisePanel(chartTypePanel, "Chart Type", Theme.ACTIVE_FG);
		//chartTypePanel.setMaximumSize(new Dimension(100, 60));
		
		chartTypeBox = new JComboBox<>();
		chartTypeBox.setPrototypeDisplayValue("Stacked Area Chart");
		chartTypeBox.addItem(CHART_TYPES[1]);
		chartTypeBox.addItem(CHART_TYPES[2]);
		
		JPanel chartTypeBoxPanel = new JPanel();
		chartTypeBoxPanel.setBackground(Theme.ACTIVE_FG);
		chartTypeBoxPanel.add(chartTypeBox);
		
		chartTypePanel.add(chartTypeBoxPanel);
		chartTypePanel.setMaximumSize(new Dimension(160, 60));
		
		JPanel legendPanel = new JPanel();
		customisePanel(legendPanel, "Include Legend", Theme.ACTIVE_FG);
		legendPanel.setPreferredSize(new Dimension(110, legendPanel.getHeight()));
		
		ButtonGroup legendGroup = new ButtonGroup();
		
		yes = new JRadioButton("Yes");
		yes.setBackground(Theme.ACTIVE_BG);
		
		no = new JRadioButton("No");
		no.setSelected(true);
		no.setBackground(Theme.ACTIVE_BG);
		
		legendGroup.add(yes);
		legendGroup.add(no);
		
		legendPanel.add(yes);
		legendPanel.add(no);
		
		JPanel chartsPanel = new JPanel();
		customisePanel(chartsPanel, "Charts Created", Theme.ACTIVE_BG);
		chartsPanel.setMaximumSize(new Dimension(200, 70)); // FIX
			
		chartNames = new JComboBox<>();
		chartNames.setPreferredSize(new Dimension(150, 30)); // FIX
		
		JPanel chartBoxPanel = new JPanel();
		chartBoxPanel.setBackground(Theme.ACTIVE_BG);
		chartBoxPanel.add(chartNames);
		
		chartsPanel.add(chartBoxPanel);
		
		save = new CustomButton("Save As");
		create = new CustomButton("Create Chart");
		compare = new CustomButton("Compare To");
		CustomButton reset = new CustomButton("Default Settings");
		
		save.setFontSize(12);
		create.setFontSize(12);
		compare.setFontSize(12);
		reset.setFontSize(12);
		
//		save.setPreferredSize(new Dimension(90,90));
		save.setPreferredSize(new Dimension(75,25));
		compare.setPreferredSize(new Dimension(90,90));
		reset.setPreferredSize(new Dimension(100,90));
		create.setPreferredSize(new Dimension(90,90));
		
		create.setEnabled(true);
		compare.setEnabled(false);
		
		create.setFocusPainted(false);
		compare.setFocusable(false);
		save.setFocusPainted(false);
		reset.setFocusPainted(false);

		create.setBackground(Theme.ACTIVE_BG);
		compare.setBackground(Theme.ACTIVE_BG);
		save.setBackground(Theme.ACTIVE_BG);
		reset.setBackground(Theme.ACTIVE_BG);
		
		create.setToolTipText("Create a chart which includes all filters from the chart dataset");
		compare.setToolTipText("Compare current chart to another");
		save.setToolTipText("Save your current chart on your hard disk");
		reset.setToolTipText("Reset the current settings to the default ones");
		
		save.setEnabled(false);
		
		chartButtonsPanel.add(chartTypePanel);
		chartButtonsPanel.add(Box.createHorizontalStrut(20));
		chartButtonsPanel.add(granularityPanel);
		chartButtonsPanel.add(Box.createHorizontalStrut(20));
		chartButtonsPanel.add(legendPanel);
		chartButtonsPanel.add(Box.createHorizontalStrut(20));
		chartButtonsPanel.add(chartsPanel);
		chartButtonsPanel.add(Box.createHorizontalGlue());
		chartButtonsPanel.add(create);
		chartButtonsPanel.add(Box.createHorizontalStrut(20));
		chartButtonsPanel.add(compare);
		chartButtonsPanel.add(Box.createHorizontalStrut(20));
		chartButtonsPanel.add(save);
		chartButtonsPanel.add(Box.createHorizontalStrut(20));
		chartButtonsPanel.add(reset);
		
		chartNames.addItemListener(new ItemListener()
		{

			@Override
			public void itemStateChanged(ItemEvent e) 
			{
				chartHolder.removeAll();
				chartHolder.add(imageList.get(chartNames.getSelectedIndex()));
				chartHolder.revalidate();
				chartHolder.repaint();
			}
			
		});
		
		create.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{
				displayTitlePrompt();
				filters.clearSelection();
				elementsField.setText("");
				save.setEnabled(true);
				if (!metrics.getSelectedItem().toString().equals(METRIC_NAMES[0]))
				{
					create.setEnabled(false);
				}
			}
		});
		
		compare.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e) 
			{
				JPanel panel = new JPanel();
				panel.setLayout(new BoxLayout(panel, BoxLayout.LINE_AXIS));
				
				
				DefaultComboBoxModel<String> model = new DefaultComboBoxModel<>();
				
				for (int i=0; i < chartNames.getItemCount(); i++)
				{
					if (i != chartNames.getSelectedIndex())
					{
						model.addElement(chartNames.getItemAt(i));
					}
				}
								
				JComboBox<String> options = new JComboBox<>(model);
				options.removeItem(chartNames.getSelectedIndex());
				
				JLabel prompt = new JLabel("Please select a chart for comaprison");
				prompt.setFont(font);
				
				panel.add(prompt);
				panel.add(Box.createHorizontalStrut(10));
				panel.add(options);
				
				try
				{
					int result = JOptionPane.showConfirmDialog(null, panel, "Compare Charts", JOptionPane.OK_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE, new ImageIcon("img/chart.png"));
				
					if (result == 0)
					{		
						for (JLabel imgLabel : imageList)
						{
							if (imgLabel.getName().equals(options.getItemAt(options.getSelectedIndex())))
							{	
								JLabel close = new JLabel();
								close.setIcon(new ImageIcon("img/x.png"));
								
								JPanel exitPanel = new JPanel();
								exitPanel.setBackground(Theme.ACTIVE_BG);
								exitPanel.setLayout(new BoxLayout(exitPanel, BoxLayout.LINE_AXIS));
								exitPanel.add(Box.createHorizontalGlue());
								exitPanel.add(close);
								
								JPanel comparePanel = new JPanel();
								comparePanel.setAlignmentX(Component.TOP_ALIGNMENT);
								comparePanel.setBackground(Theme.ACTIVE_BG);
								comparePanel.setLayout(new BoxLayout(comparePanel, BoxLayout.PAGE_AXIS));
								comparePanel.add(exitPanel);
								comparePanel.add(imgLabel);
								
								if (chartHolder.getComponents().length == 2)
								{
									chartHolder.remove(chartHolder.getComponent(1));
								}
	
								chartHolder.add(comparePanel);
								chartHolder.revalidate();
								chartHolder.repaint();
								
								close.addMouseListener(new MouseAdapter()
								{
									
									public void mouseClicked(MouseEvent e)
									{
										frame.setSize(MyFrame.getFrameWidth(), MyFrame.getFrameHeight());
										chartHolder.remove(comparePanel);
										chartHolder.revalidate();
										chartHolder.repaint();
									}	
								});
								
								
								break;
							}	
						}
					}		
				}
				catch (NullPointerException npe)
				{
					
				}
			}
		});
		
		save.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event) 
			{
				saveChartImage(chartImage);
			}
		
		});

		reset.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e)
			{
				setDefaultSettings();
			}
		});
	}
	
	public void displayTitlePrompt()
	{
		Object[] options = {"Confirm", "Create without title", "Cancel"};
		
		JPanel panel = new JPanel();
		panel.setBackground(Theme.ACTIVE_BG);
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		
		JLabel prompt = new JLabel("Please enter a title for the chart", JLabel.CENTER);
		prompt.setBackground(Theme.ACTIVE_BG);
		prompt.setForeground(Theme.ACTIVE_FG);
		prompt.setFont(font);
		
		JTextField inputField = new JTextField(10);
		
		panel.add(prompt);
		panel.add(inputField);
		
		try
		{
			int result = JOptionPane.showOptionDialog(null, panel, "Enter chart name",
            JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, new ImageIcon("img/name.png"), options, null);
			
			if (result == 0)
			{
				String title = inputField.getText();
				
				if (title.trim().length() == 0)
				{
					JOptionPane.showMessageDialog(null, "A chart title must contain at least one non-whitespace character! Please try again.", "Title Missing", JOptionPane.ERROR_MESSAGE);
				}
				else
				{
					displayChart(title);
					chartNames.addItem(title);
					chartNames.getModel().setSelectedItem(title);
					imageList.get(chartNames.getSelectedIndex()).setName(chartNames.getItemAt(chartNames.getSelectedIndex()));
				}
			}	
			else if (result == 1)
			{
				int index = 1;
				
				for(int i=0; i < chartNames.getItemCount(); i++)
				{
					if (chartNames.getItemAt(i).startsWith("Untitled"))
					{
						index++;
					}
				}
				
				displayChart("");
				chartNames.addItem("Untitled" + index);
				chartNames.getModel().setSelectedItem("Untitled" + index);
				imageList.get(chartNames.getSelectedIndex()).setName(chartNames.getItemAt(chartNames.getSelectedIndex()));
			}
				
			if (chartNames.getModel().getSize() > 1)
			{
				compare.setEnabled(true);
			}
		}
		catch(NullPointerException npe)
		{
			
		}
	}
	
	public void displayChart(String title)
	{
		JFreeChart chart = null;
		//chart.setBackgroundPaint(Color.CYAN);
		//chart.setBackgroundImage(new ImageIcon);
		
		switch (chartTypeBox.getSelectedItem().toString())
		{	
			case "Area Chart":	
				DefaultCategoryDataset areaSet = createDefaultCategoryDataset();
				chart = ChartFactory.createAreaChart(title, "Filter Details", "Value", areaSet, PlotOrientation.VERTICAL, yes.isSelected(), true, false);
				break;
			case "Bar Chart":	
				DefaultCategoryDataset barSet = createClickCostDistributionDataset();
				String xAxisTitle = "Click Cost";	
				
				if (!elementsField.getText().isEmpty())
				{
					barSet = createDefaultCategoryDataset();
					xAxisTitle = "Filter Details";
				}
				
				chart = ChartFactory.createBarChart(title, xAxisTitle, "Value", barSet, PlotOrientation.VERTICAL, yes.isSelected(), true, false);
				break;
			case "Histogram":
				FilterStorage storage = frame.fStorage;
				ResultSet clicks = storage.getClickCostDistribution();
				DefaultTableXYDataset histoSet = new DefaultTableXYDataset();
				XYSeries series = new XYSeries("Click cost in cents", true, false);
				
				try
				{
					while (clicks.next())
					{	
						series.add(new XYDataItem(clicks.getDouble(1) * 100, clicks.getInt(2)));
					}
				}	
				catch (SQLException sqle) 
				{
					sqle.printStackTrace();
				}
				
				histoSet.addSeries(series);
				
				chart = ChartFactory.createHistogram(title, "Click cost in cents", "Value", histoSet, PlotOrientation.VERTICAL, yes.isSelected(), true, false);
				break;
				
			case "Line Chart":
				//DefaultCategoryDataset lineSet = createLineChartDefaultCategoryDataset();
				//chart = ChartFactory.createLineChart(title, "Filter Details", "Value", lineSet, PlotOrientation.VERTICAL, yes.isSelected(), true, false);
				break;
			case "Line Chart 3D":
				DefaultCategoryDataset lineSet3D = createDefaultCategoryDataset(); 
				chart = ChartFactory.createLineChart3D(title, "Filter Details", "Value", lineSet3D, PlotOrientation.VERTICAL, yes.isSelected(), true, false);
				break;
			case "Pie Chart":
				DefaultPieDataset pieSet = createDefaultPieDataset();
				chart = ChartFactory.createPieChart(title, pieSet, yes.isSelected(), true, false);
				break;
			case "Pie Chart 3D":
				DefaultPieDataset pieSet3D = createDefaultPieDataset();			
				chart = ChartFactory.createPieChart3D(title, pieSet3D, yes.isSelected(), true, false);
				break;
			case "Ring Chart":	
				DefaultPieDataset ringSet = createDefaultPieDataset();
				chart = ChartFactory.createRingChart(title, ringSet, yes.isSelected(), true, false);
				break;
			case "Stacked Area Chart":
				DefaultCategoryDataset stackedAreaSet = createDefaultCategoryDataset();
				chart = ChartFactory.createStackedAreaChart(title, "Filter Details", "Value", stackedAreaSet, PlotOrientation.VERTICAL, yes.isSelected(), true, false);
				break;	
			case "Stacked Bar Chart":
				DefaultCategoryDataset stackedBarSet = createDefaultCategoryDataset();
				chart = ChartFactory.createStackedBarChart(title, "Filter Details", "Value", stackedBarSet, PlotOrientation.VERTICAL, yes.isSelected(), true, false);
				break;
			case "Time Series Chart" : 
				break;
			case "WaterFall Chart":
				break;
				
		}
		
		chartImage = chart.createBufferedImage(650, 650);

		JLabel imageLabel = new JLabel();
		imageLabel.setIcon(new ImageIcon(chartImage));
		imageLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));

		imageList.add(imageLabel);	
		
		chartHolder.removeAll();
		chartHolder.add(imageLabel);
		chartHolder.revalidate();
		chartHolder.repaint();
	}
	
	public DefaultCategoryDataset createDefaultCategoryDataset()
	{
		Map<Integer, MetricFilter> metricFilters = frame.fStorage.getFilters();
		String[] elements = elementsField.getText().split(" ");
		DefaultCategoryDataset areaSet = new DefaultCategoryDataset();
		boolean isInGroup = false;
		int groupSum = 0;
		int i=0;
		String labelText = "";
		
		while (i < elements.length)
		{
			String filterIndex = elements[i];
			String value = null;
			
			Pattern pattern = Pattern.compile("[0-9]+");
			Matcher matcher = pattern.matcher(filterIndex);
			
			if (matcher.find())
			{
			    value = metricFilters.get(Integer.parseInt(matcher.group())).getValue();
			}
			
			if (value.contains("�"))
			{
				value = value.substring(1);
			}		
			
			if (filterIndex.startsWith("("))
			{
				isInGroup = true;	
			}
			
			if (!isInGroup)
			{	
				labelText = generateLabelText(filterIndex);
				areaSet.addValue(Double.parseDouble(value), "First", labelText);
			}	
			else
			{	
				if (!filterIndex.endsWith(")"))
				{
					if(filterIndex.startsWith("("))
					{	
						filterIndex = filterIndex.substring(1);
						labelText = generateLabelText(filterIndex);
					}
					else
					{
						labelText += "\nand\n" + generateLabelText(filterIndex);
					}
					
					groupSum += Double.parseDouble(value);
				}
				else
				{
					filterIndex = filterIndex.substring(0, filterIndex.length() - 1);	
					labelText += "\nand\n" + generateLabelText(filterIndex);	
					areaSet.addValue(groupSum + Double.parseDouble(value),  "First", labelText);
					
					groupSum = 0;
					isInGroup = false;
					labelText = "";
				}
			}	
			
			i++;
		}
		
		return areaSet;
	}
	public DefaultPieDataset createDefaultPieDataset()
	{		
		Map<Integer, MetricFilter> metricFilters = frame.fStorage.getFilters();
		String[] elements = elementsField.getText().split(" ");
		DefaultPieDataset pieSet = new DefaultPieDataset();
		boolean isInGroup = false;
		double groupSum = 0;
		int i=0;
		String labelText = "";
		
		
		while (i < elements.length)
		{
			String filterIndex = elements[i];
			String value = null;
			
			Pattern pattern = Pattern.compile("[0-9]+");
			Matcher matcher = pattern.matcher(filterIndex);
			
			if (matcher.find())
			{
			    value = metricFilters.get(Integer.parseInt(matcher.group())).getValue();
			}
			
			if (value.contains("�"))
			{
				value = value.substring(1);
			}		
			
			if (filterIndex.startsWith("("))
			{
				isInGroup = true;	
			}
			
			if (!isInGroup)
			{			
				labelText = generateLabelText(filterIndex);
				pieSet.setValue(labelText, Double.parseDouble(value));
			}	
			else
			{
				if (!elements[i].endsWith(")"))
				{
					if (filterIndex.startsWith("("))
					{
						filterIndex = filterIndex.substring(1);
						labelText = generateLabelText(filterIndex);
					}
					else
					{
						labelText += "\nand\n" + generateLabelText(filterIndex);
					}
					
					groupSum += Double.parseDouble(value);
				}
				else
				{
					filterIndex = filterIndex.substring(0, filterIndex.length() - 1);	
					groupSum += Double.parseDouble(value);	
					labelText += "\nand\n" + generateLabelText(filterIndex);
					pieSet.setValue(labelText, groupSum + Double.parseDouble(value));
					
					groupSum = 0;
					isInGroup = false;
					labelText = "";
				}
			}
			
			i++;
		}
		
		return pieSet;
	}
	
	public DefaultCategoryDataset createClickCostDistributionDataset()
	{
		FilterStorage storage = frame.fStorage;
		DefaultCategoryDataset clickSet = new DefaultCategoryDataset();
		
		ResultSet clicks = storage.getClickCostDistribution();
		
		try 
		{
			while (clicks.next())
			{
				clickSet.addValue(clicks.getInt(2), clicks.getString(1), clicks.getString(1));
			}
		}
		catch (SQLException sqle) 
		{
			sqle.printStackTrace();
		}
		
		return clickSet;
	}
	
//	public DefaultCategoryDataset createLineChartDefaultCategoryDataset()
//	{
//		DefaultCategoryDataset lineSet = new DefaultCategoryDataset();
//		
//		Map<Integer, MetricFilter> metricFilters = frame.getMainPanel().getMenuPanel().getMetricPanel().getFilterStorage().getFilters();
//		String[] elements = elementsField.getText().split(" ");
//		DefaultCategoryDataset category = new DefaultCategoryDataset();
//		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");		
//		boolean isInGroup = false;
//		int groupSum = 0;
//		int i=0;
//		
//		while (i < elements.length)
//		{
//			String filterIndex = elements[i];
//			
//			if (filterIndex.startsWith("("))
//			{
//				filterIndex = filterIndex.substring(1);
//				isInGroup = true;	
//			}
//			
//			if (isInGroup)
//			{	
//				if (elements[i].endsWith(")"))
//				{
//					filterIndex = filterIndex.substring(0, filterIndex.length() - 1);	
//					category.addValue(400, "Series 1", "Series 1");
//					
//					groupSum = 0;
//					isInGroup = false;
//				}
//				else
//				{
//					groupSum += Integer.parseInt(metricFilters.get(Integer.parseInt(filterIndex)).getValue());
//				}
//			}	
//			else
//			{
//				category.addValue(300,"Series 1", "Series1");
//			}
//			
//			i++;
//		}
//		
//		return lineSet;
//	}
	
	public void saveChartImage(BufferedImage chartImage)
	{
		try
		{
			String fileName = (String) JOptionPane.showInputDialog(null, "Please enter an image name", "Enter image name", JOptionPane.PLAIN_MESSAGE, new ImageIcon("img/name.png"), null, "");
			
			if (fileName.trim().length() > 0)
			{			
				chooseImageFormat(chartImage, fileName);
			}
			else
			{
				JOptionPane.showMessageDialog(null, "A chart title must contain at least one non-whitespace character! Please try again.", "Title Missing", JOptionPane.ERROR_MESSAGE , new ImageIcon("img/error.png"));
			}
		}
		catch(NullPointerException npe)
		{
			
		}
			
	}
	
	public String generateLabelText(String filterIndex)
	{
		Map<Integer, MetricFilter> metricFilters = frame.fStorage.getFilters();
		MetricFilter current = metricFilters.get(Integer.parseInt(filterIndex));
		
		String labelText = current.selectGender.getSelectedItem() + " " + current.selectAge.getSelectedItem() + " "
				+ current.selectContext.getSelectedItem() + " " + current.selectIncome.getSelectedItem() + "\n";
		
		return labelText;
	}
		
	public void chooseImageFormat(BufferedImage bufImage, String fileName)
	{
		Object[] imageFormats = {"PNG", "JPEG", "GIF"};
		
		String userInput = (String) JOptionPane.showInputDialog(null, "Choose file format", "File Format Settings", JOptionPane.PLAIN_MESSAGE, new ImageIcon("img/save.png"), imageFormats, "PNG");
				
		String fileExtension = null;
		switch (userInput)
		{
			case "PNG":
				fileExtension = ".png";
				break;
			case "JPEG":
				fileExtension = ".jpg";
				break;
			case "GIF":
				fileExtension = ".gif";
				break;
		}
		
		File file = new File(fileName + fileExtension);
				
		try 
		{
			ImageIO.write(bufImage, userInput, file);
			JOptionPane.showMessageDialog(this, "Your image was saved successfully at " + file.getAbsolutePath(), "Image saved successfully", JOptionPane.INFORMATION_MESSAGE, new ImageIcon("img/success.png"));
		} 
		catch (IOException e) 
		{
			JOptionPane.showMessageDialog(this, "An error occurred while trying to save image" + file.getName(), "Image not saved", JOptionPane.ERROR_MESSAGE, new ImageIcon("img/error.png"));
		}
	}
	
	public void displayFilterDetails()
	{
		FilterStorage storage = frame.fStorage;
		MetricFilter selected = storage.getFilters().get(filters.getSelectedValue());
		List<String> details = new ArrayList<>();
		
		details.add(selected.selectGender.getSelectedItem().toString());
		details.add(selected.selectAge.getSelectedItem().toString());
		details.add(selected.selectIncome.getSelectedItem().toString());
		details.add(selected.selectContext.getSelectedItem().toString());
		
		for (int i = 0; i < checkBoxes.size(); i ++)
		{
			if (details.get(0).equals("All") && i <= 1)
			{
				checkBoxes.get(i).setSelected(true);
			}
			
			if (details.get(1).equals("All") && i > 1 && i <= 6)
			{
				checkBoxes.get(i).setSelected(true);
			}
			
			if (details.get(2).equals("All") && i > 6 && i <= 9)
			{
				checkBoxes.get(i).setSelected(true);
			}
			
			if (details.get(3).equals("All") && i > 9)
			{
				checkBoxes.get(i).setSelected(true);
			}
			
			if (details.contains(checkBoxes.get(i).getText()))
			{
				checkBoxes.get(i).setSelected(true);
			}
		}
		
		fromField.setText(storage.getMetricTablePanel().getMetricTable().getValueAt(selected.getFilterIndex() - 1, 2).toString());
		untilField.setText(storage.getMetricTablePanel().getMetricTable().getValueAt(selected.getFilterIndex() - 1, 3).toString());
	}
	
	public void setDefaultSettings()
	{	
		metrics.setSelectedIndex(0);
		elementsField.setText("");
		chartGranBox.setSelectedIndex(0);
		no.setSelected(true);
		create.setEnabled(true);
		
		clearFilterDetails();
	}
	
	public void clearFilterDetails()
	{
		for(JCheckBox checkBox : checkBoxes)
		{
			if (checkBox.isSelected())
			{
				checkBox.setSelected(false);
			}
		}

		fromField.setText("");
		untilField.setText("");
	}
	
	public DefaultListModel<Integer> getDefaultListModel()
	{
		return (DefaultListModel<Integer>) filters.getModel();
	}
	
	public void updateFilterList()
	{	
		DefaultListModel<Integer> filterModel = (DefaultListModel<Integer>) filters.getModel();
		filterModel.removeAllElements();
		
		FilterStorage storage = frame.fStorage;		
		String metric = metrics.getSelectedItem().toString();
		
		for (MetricFilter filter : storage.getFilters().values())
		{
			if (filter.metricsBox.getSelectedItem().equals(metric))
			{
				filterModel.addElement(filter.getFilterIndex());
			}
			
		}
	}
	
	public void updateMetricChartTypes()
	{
		chartTypeBox.removeAllItems();	
		
		if (metrics.getSelectedItem().toString().equals(METRIC_NAMES[0]))
		{
			chartTypeBox.addItem(CHART_TYPES[1]);
			chartTypeBox.addItem(CHART_TYPES[2]);
		}
		else
		{
			for (String CHART_TYPE : CHART_TYPES)
			{
				chartTypeBox.addItem(CHART_TYPE);
			}
		}
	}
}