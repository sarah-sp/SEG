package gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
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
import java.text.SimpleDateFormat;
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
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.plaf.ColorUIResource;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.DefaultTableXYDataset;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import gui.custom.CustomPanel;
import model.CompareStorage;
import model.FilterStorage;

public class GraphPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	private static final String[] METRIC_NAMES = initMetricNamesArray(); 
	private static final String[] CHART_TYPES = initChartTypesArray();
	private static final int CHART_PANEL_HEIGHT = 700;
	private MyFrame frame;
	private CustomPanel customPanel;
	private JPanel topPanel;
	private JPanel leftPanel;
	private JPanel chartHolder;
	private JPanel chartButtonsPanel;
	private JButton add, remove, group, create, compare, save;
	private JComboBox<String> metrics, chartTypeBox, timeGranularityBox, chartNames; 
	private JList<Integer> filters;
	private JTextPane textPane;
	private SimpleAttributeSet attSet;
	private List<ChartPanel> chartPanelList;
	private BufferedImage chartImage;
	private JTextField elementsField;
	private JRadioButton yes, no;
	private Font font;
	private String[] options = {"Entire", "Months", "Weeks", "Days", "Hours"};
	private FilterStorage fStorage;
	private CompareStorage cStorage;
	
	public GraphPanel(MyFrame frame, JPanel panel, FilterStorage fStorage, CompareStorage cStorage)
	{
		this.frame = frame;
		this.fStorage = fStorage;
		this.cStorage = cStorage;
		customPanel = new CustomPanel(frame, panel, "graphs");
		init();
	}
	
	public static final String[] initMetricNamesArray()
	{
		String[] metrics = {"Campaign Click Cost Distribution", "Number of Impressions", "Number of Clicks", "Number of Uniques", "Number of Bounces", "Number of Conversions", "Total Cost", "Click-through Rate (CTR)", "Cost-per-acquisition (CPA)", "Cost-per-click (CPC)", "Cost-per-thousand impressions (CPM)", "Bounce Rate"};
		
		return metrics;
	}
	
	public static final String[] initChartTypesArray()
	{
		String[] chartTypes = {"Bar Chart", "Bar Chart 3D", "Histogram", "Line Chart", "Line Chart 3D", "Pie Chart", "Pie Chart 3D", "Ring Chart", "Stacked Area Chart", "Stacked Bar Chart", "Stacked Bar Chart 3D", "XYArea Chart", "XYLine Chart"};
		
		return chartTypes;
	}
	
	public void customisePanel(JPanel panel, String borderTitle)
	{
		panel.setBackground(Color.WHITE);
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		panel.setBorder(BorderFactory.createTitledBorder(new LineBorder(Color.BLACK), borderTitle, TitledBorder.LEFT, TitledBorder.DEFAULT_POSITION, getFrame().getFont().deriveFont(12.0f)));
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
		UIManager.put("ToolTip.foreground", new ColorUIResource(Color.BLACK));
		
		setLayout(new BorderLayout());
		setBackground(Color.WHITE);
		setBorder(BorderFactory.createEmptyBorder(15,15,15,15));
		
		topPanel = new JPanel();
		initTopPanel();
		
		leftPanel = new JPanel();
		initLeftPanel();
				
		chartHolder = new JPanel();
		chartHolder.setBackground(Color.WHITE);
		chartHolder.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
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
				JPanel panel = getTopPanel();
				panel.removeAll();
				MainPanel mainPanel = new MainPanel(getFrame(), panel, fStorage, cStorage);
				panel.add(mainPanel);
				panel.revalidate();
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
		leftPanel.setBackground(Color.WHITE);
		leftPanel.setAlignmentX(Component.LEFT_ALIGNMENT);	
		
		font = new Font("Courier", Font.BOLD, 18);
		
		JPanel metricPanel = new JPanel();
		initMetricPanel(metricPanel);
		
		JPanel metricListPanel = new JPanel();
		
		customisePanel(metricListPanel, "Metric Filter ID's");
		metricListPanel.setMaximumSize(new Dimension(265, 100));
		
		filters = new JList<>(new DefaultListModel<Integer>());
		filters.setPrototypeCellValue(99);
		filters.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		
		JScrollPane scrollPaneFilters = new JScrollPane(filters);
		filters.setFont(font);
		
		metricListPanel.add(scrollPaneFilters);
		
		textPane = new JTextPane();
		textPane.setEditable(false);
		textPane.setBorder(BorderFactory.createEmptyBorder(0,5,5,0));
		attSet = new SimpleAttributeSet();
		textPane.setCharacterAttributes(attSet, true);
		
		JScrollPane scrollPaneDetails = new JScrollPane(textPane);
		
		JSplitPane splitPane= new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPaneFilters, scrollPaneDetails);
		splitPane.setAlignmentX(Component.CENTER_ALIGNMENT);
		splitPane.setDividerLocation(40);
		
		JPanel splitPanePanel = new JPanel();
		customisePanel(splitPanePanel, "Metric Filters");
		splitPanePanel.setPreferredSize(new Dimension(265, 210));
		
		splitPanePanel.add(splitPane);
		
		JPanel splitPanePanelHolder = new JPanel();
		splitPanePanelHolder.setBackground(Color.WHITE);
		splitPanePanelHolder.setMaximumSize(new Dimension(265, 180));
		
		splitPanePanelHolder.add(splitPanePanel);
		
		JPanel filterButtonsPanel = new JPanel();
		initFilterButtons(filterButtonsPanel);
		filterButtonsPanel.setMaximumSize(new Dimension(265, 40));
		
		JLabel chartElements = new JLabel(" Chart Dataset Elements");
		chartElements.setBackground(Color.WHITE);
		chartElements.setFont(font);
		
		elementsField = new JTextField();
		elementsField.setBackground(Color.WHITE);
		elementsField.setFont(font);
		elementsField.setEditable(false);
		elementsField.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		elementsField.setPreferredSize(new Dimension(265, 30));
		
		JPanel elementsPanel = new JPanel(new FlowLayout());
		elementsPanel.setBackground(Color.WHITE);
		elementsPanel.add(elementsField);
		
		JPanel chartDatasetPanel = new JPanel();
		chartDatasetPanel.setBackground(Color.WHITE);
		chartDatasetPanel.setLayout(new BoxLayout(chartDatasetPanel, BoxLayout.PAGE_AXIS));
		chartDatasetPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
		chartDatasetPanel.setPreferredSize(new Dimension(250, 275));
		
		chartDatasetPanel.add(chartElements);
		chartDatasetPanel.add(Box.createVerticalStrut(5));
		chartDatasetPanel.add(elementsPanel);
		
		leftPanel.add(metricPanel);
		leftPanel.add(Box.createVerticalStrut(10));
		leftPanel.add(splitPanePanelHolder);
		leftPanel.add(filterButtonsPanel);
		leftPanel.add(Box.createVerticalStrut(10));
		leftPanel.add(chartDatasetPanel);
		leftPanel.add(Box.createVerticalStrut(5));

		leftPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 20));
		
		filters.addListSelectionListener(new ListSelectionListener()
		{
			@Override
			public void valueChanged(ListSelectionEvent e) 
			{
				textPane.setText("");

				int numFilters = filters.getSelectedValuesList().size();
	
				if (numFilters > 1)
				{	
					for (Integer filterID : filters.getSelectedValuesList())
					{
						displayFilterDetails(filterID);
						numFilters--;
							
						if (numFilters > 0)
						{	
							try 
							{
								textPane.getDocument().insertString(textPane.getDocument().getLength(), "\n\n", textPane.getCharacterAttributes());
							} 
							catch (BadLocationException ble) 
							{
								ble.printStackTrace();
							}
						}	
					}
					
					group.setEnabled(true);
				}
				else if (numFilters == 1)
				{
					displayFilterDetails(filters.getSelectedValue());
					add.setEnabled(true);
					group.setEnabled(false);
				}
				
				textPane.setCaretPosition(0);
			}	
		});
	}

	public void initMetricPanel(JPanel metricPanel) 
	{
		customisePanel(metricPanel, "Metric Name");
		metricPanel.setMaximumSize(new Dimension(265, 60));

		JPanel innerMetricPanel = new JPanel(new BorderLayout());
		innerMetricPanel.setBackground(Color.WHITE);
		innerMetricPanel.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		
		metrics = new JComboBox<>(METRIC_NAMES);
		metrics.setPrototypeDisplayValue(METRIC_NAMES[10]);
		
		innerMetricPanel.add(metrics);
		metricPanel.add(innerMetricPanel);
		
		metrics.addItemListener(new ItemListener()
		{

			@Override
			public void itemStateChanged(ItemEvent e) 
			{				
				updateFilterList();
				showAllowedChartTypes();
				showAllowedTimeGranularity();	
				create.setEnabled(metrics.getSelectedIndex() == 0);
			}	
		});
	}
	
	public void initFilterButtons(JPanel filterButtonsPanel)
	{
		filterButtonsPanel.setBackground(Color.WHITE);
		
		add = new JButton("Add");
		group = new JButton("Add As Group");
		remove = new JButton("Remove");
		
		add.setBackground(Color.WHITE);
		group.setBackground(Color.WHITE);
		remove.setBackground(Color.WHITE);
		
		add.setFocusPainted(false);
		group.setFocusPainted(false);
		remove.setFocusPainted(false);
		
		add.setToolTipText("Select a single filter id or press Ctrl and choose any number of fitler ids to be able to add the selection in the chart dataset");
		group.setToolTipText("Press the Ctrl button and click on the filter ids that you want to add as a single chart data element");
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
				String idList = elementsField.getText() + " ";
					
				for (Integer id : filters.getSelectedValuesList())
				{
					idList += id + " ";
				}
				
				elementsField.setText(idList.trim());
				create.setEnabled(true);
				remove.setEnabled(true);
				
				showAllowedTimeGranularity();
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
				
				elementsField.setText((elementsField.getText() + " " + group).trim());
				create.setEnabled(true);
				remove.setEnabled(true);

				//EASY FIX
				remove.setEnabled(elementsField.getText().contains(buildStringForRemoval()));

			}	
		});
		
		remove.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
			{	
				removeFilterSelection();
				
				if (elementsField.getText().split(" ").length <= 1 || elementsField.getText().split("\\)").length <= 1)
				{
					showAllowedTimeGranularity();
				}
			}	
		});
	}
	
	public void removeFilterSelection()
	{		
		if (filters.getSelectedValuesList().size() > 0)
		{	
			String toBeRemoved = buildStringForRemoval();
			
			String updated = elementsField.getText().replaceFirst(toBeRemoved, "").trim();
			
			StringBuilder stringBuilder = new StringBuilder();
					
			for (String id : updated.split("\\s+"))
			{
				stringBuilder.append(id + " ");
			}
			
			elementsField.setText(stringBuilder.toString().trim());
			
			remove.setEnabled(elementsField.getText().contains(toBeRemoved));
		}
	}
	
	public String buildStringForRemoval()
	{
		String toBeRemoved = "";
		
		for (Integer id : filters.getSelectedValuesList())
		{
			toBeRemoved += id + " ";
		}
		
		toBeRemoved = toBeRemoved.trim();
		
		if (filters.getSelectedValuesList().size() > 1)
		{
			toBeRemoved = "\\(" + toBeRemoved + "\\)";
		}
		
		return toBeRemoved;
	}
	
	public String removeSingleElementGroups(String idList)
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
		chartPanelList = new ArrayList<>();
		
		chartButtonsPanel.setLayout(new BoxLayout(chartButtonsPanel, BoxLayout.LINE_AXIS));
		chartButtonsPanel.setPreferredSize(new Dimension(getWidth(), 85));
		chartButtonsPanel.setBackground(Color.WHITE);
		chartButtonsPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));			
		
		JPanel granularityPanel = new JPanel();
		customisePanel(granularityPanel, "Time Granularity");
		granularityPanel.setMaximumSize(new Dimension(1400, 60));
		
		timeGranularityBox = new JComboBox<>();
		timeGranularityBox.setPrototypeDisplayValue("XXXXXX");
		timeGranularityBox.addItem(options[0]);
		
		JPanel granularityBoxPanel = new JPanel();
		granularityBoxPanel.setBackground(Color.WHITE);
		granularityBoxPanel.add(timeGranularityBox);
			
		granularityPanel.add(granularityBoxPanel);
		
		JPanel chartTypePanel = new JPanel();
		customisePanel(chartTypePanel, "Chart Type");
		
		chartTypeBox = new JComboBox<>();
		chartTypeBox.setPrototypeDisplayValue("Stacked Bar Chart 3D");
		chartTypeBox.addItem(CHART_TYPES[0]);
		chartTypeBox.addItem(CHART_TYPES[1]);
		chartTypeBox.addItem(CHART_TYPES[2]);
		chartTypeBox.setSelectedItem(CHART_TYPES[0]);
		
		JPanel chartTypeBoxPanel = new JPanel();
		chartTypeBoxPanel.setBackground(Color.WHITE);
		chartTypeBoxPanel.add(chartTypeBox);
		
		chartTypePanel.add(chartTypeBoxPanel);
		chartTypePanel.setMaximumSize(new Dimension(160, 60));
		
		JPanel legendPanel = new JPanel();
		customisePanel(legendPanel, "Include Legend");
		legendPanel.setPreferredSize(new Dimension(110, legendPanel.getHeight()));
		
		ButtonGroup legendGroup = new ButtonGroup();
		
		yes = new JRadioButton("Yes");
		yes.setBackground(Color.WHITE);
		
		no = new JRadioButton("No");
		no.setSelected(true);
		no.setBackground(Color.WHITE);
		
		legendGroup.add(yes);
		legendGroup.add(no);
		
		legendPanel.add(yes);
		legendPanel.add(no);
		
		JPanel chartsPanel = new JPanel();
		customisePanel(chartsPanel, "Charts Created");
		chartsPanel.setMaximumSize(new Dimension(200, 70)); // FIX
			
		chartNames = new JComboBox<>();
		chartNames.setPreferredSize(new Dimension(150, 30)); // FIX
		
		JPanel chartBoxPanel = new JPanel();
		chartBoxPanel.setBackground(Color.WHITE);
		chartBoxPanel.add(chartNames);
		
		chartsPanel.add(chartBoxPanel);
		
		save = new JButton("Save As");
		create = new JButton("Create Chart");
		compare = new JButton("Compare To");
		JButton reset = new JButton("Default Settings");
		
		create.setEnabled(true);
		compare.setEnabled(false);
		
		create.setFocusPainted(false);
		compare.setFocusable(false);
		save.setFocusPainted(false);
		reset.setFocusPainted(false);

		create.setBackground(Color.WHITE);
		compare.setBackground(Color.WHITE);
		save.setBackground(Color.WHITE);
		reset.setBackground(Color.WHITE);
		
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
		
		
		chartTypeBox.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent ie) 
			{
				showAllowedTimeGranularity();	
			}
		});
		
		timeGranularityBox.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent ie) 
			{	
				if (timeGranularityBox.getSelectedIndex() == -1)
				{	
					showAllowedTimeGranularity();
				}	
			}	
		});
	
		chartNames.addItemListener(new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent ie) 
			{
				chartHolder.removeAll();
				if(chartNames.getSelectedIndex() != - 1)
				{
					chartHolder.add(chartPanelList.get(chartNames.getSelectedIndex()));
				}
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
				
				if (metrics.getSelectedItem().toString().equals(METRIC_NAMES[0]) || elementsField.getText().length() > 0)
				{
					create.setEnabled(true);
				}
			}
		});
		
		compare.addActionListener(new ActionListener()
		{

			@Override
			public void actionPerformed(ActionEvent e) 
			{
				compareCharts();
			}
		});
		
		save.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e) 
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
	
	
	public void showAllowedTimeGranularity()
	{	
		timeGranularityBox.removeAllItems();
		
		if (chartTypeBox.getSelectedItem() != null && timeGranularityBox.getModel().getSize() == 0)
		{	
			timeGranularityBox.removeAllItems();
			
			if (chartTypeBox.getSelectedItem().toString().contains("Area") || chartTypeBox.getSelectedItem().toString().contains("Line"))
			{
				timeGranularityBox.addItem(options[1]);
				timeGranularityBox.addItem(options[2]);
				timeGranularityBox.addItem(options[3]);
				timeGranularityBox.addItem(options[4]);				
			}
			else
			{
				timeGranularityBox.addItem(options[0]);
			
				String[] elements = elementsField.getText().split(" ");
				
				if (elements.length <= 1)
				{
					timeGranularityBox.addItem(options[1]);
					timeGranularityBox.addItem(options[2]);
					timeGranularityBox.addItem(options[3]);
					timeGranularityBox.addItem(options[4]);
				}
			}
		}	

	}
	
	public void displayTitlePrompt()
	{
		Object[] options = {"Confirm", "Create without title", "Cancel"};
		
		JPanel panel = new JPanel();
		panel.setBackground(Color.WHITE);
		panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
		
		JLabel prompt = new JLabel("Please enter a title for the chart", JLabel.CENTER);
		prompt.setBackground(Color.WHITE);
		prompt.setFont(font);
		
		JTextField inputField = new JTextField(10);
		
		panel.add(prompt);
		panel.add(inputField);
		
		try
		{
			int result = JOptionPane.showOptionDialog(null, panel, "Enter chart name",
            JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, new ImageIcon("img/name.png"), options, null);
			
			String title = null;
			
			if (result == 0)
			{
				title = inputField.getText();
				
				if (title.trim().length() == 0)
				{
					JOptionPane.showMessageDialog(null, "A chart title must contain at least one non-whitespace character! Please try again.", "Title Missing", JOptionPane.ERROR_MESSAGE , new ImageIcon("img/error.png"));
				}
				else
				{
					chartNames.addItem(title);
					chartNames.getModel().setSelectedItem(title);
					chartPanelList.get(chartNames.getSelectedIndex()).setName(chartNames.getItemAt(chartNames.getSelectedIndex()));
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
				chartPanelList.get(chartNames.getSelectedIndex()).setName(chartNames.getItemAt(chartNames.getSelectedIndex()));
			}
			
			if ((result == 0 && !title.trim().isEmpty())|| result == 1)
			{	
				filters.clearSelection();
				elementsField.setText("");
				add.setEnabled(false);
				remove.setEnabled(false);
				save.setEnabled(true);
				create.setEnabled(false);
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
		
		switch (chartTypeBox.getSelectedItem().toString())
		{	
			case "Bar Chart":	
				chart = createBarChart(title, true);
				break;
			case "Bar Chart 3D":
				chart = createBarChart(title, false);
				break;
			case "Histogram":
				chart = createClickCostDistributionChart(title);
				break;
			case "Line Chart":
				chart = createLineChart(title, true);
				break;
			case "Line Chart 3D":
				chart = createLineChart(title, false);
				break;
			case "Pie Chart":
				chart = createPieChart(title, true);
				break;
			case "Pie Chart 3D":
				chart = createPieChart(title, false);
				break;
			case "Ring Chart":	
				chart = createRingChart(title);
				break;
			case "Stacked Area Chart":
				chart = createStackedAreaChart(title);
				break;	
			case "Stacked Bar Chart":
				chart = createStackedBarChart(title, true);
				break;
			case "Stacked Bar Chart 3D":
				chart = createStackedBarChart(title, false);
				break;	
			case "XYArea Chart":	
				chart = createXYAreaChart(title);
				break;
			case "XYLine Chart":
				chart = createXYLineChart(title);
		}
		
		chart.getPlot().setBackgroundPaint(new Color(230,230,230));
		chart.getTitle().setFont(font.deriveFont(26.0f));
		
		ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new Dimension(650, 650));
		chartPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));

		chartPanelList.add(chartPanel);	
		
		chartHolder.removeAll();
		chartHolder.add(chartPanel);
		chartHolder.revalidate();
		chartHolder.repaint();
	}
	
	public JFreeChart createBarChart(String title, boolean normal) 
	{
		JFreeChart chart = null;
		DefaultCategoryDataset categorySet = new DefaultCategoryDataset();
		
		String xAxisTitle = "Filter Details";
		String yAxisTitle = metrics.getSelectedItem().toString();
		
		if (metrics.getSelectedIndex() == 0)
		{
			categorySet = createClickCostDistributionDataset();
			xAxisTitle = "Click Cost in Cents";
			yAxisTitle = "Number of Clicks";
		}
		else if (timeGranularityBox.getSelectedItem().toString().equals("Entire"))
		{
			fillCategoryDataset(categorySet, false);
		}
		else
		{
			SymbolAxis xAxis = null;
			fillTimeGranularityCategoryDataset(categorySet, xAxis, xAxisTitle);	
		}
		
		if (normal)
		{	
			chart = ChartFactory.createStackedBarChart(title, xAxisTitle, yAxisTitle, categorySet,  PlotOrientation.VERTICAL, yes.isSelected(), true, false);
		}
		else
		{
			if (metrics.getSelectedIndex() == 0)
			{
				chart = ChartFactory.createStackedBarChart3D(title, xAxisTitle, yAxisTitle, categorySet, PlotOrientation.VERTICAL, yes.isSelected(), true, false);

			}
			else
			{	
				chart = ChartFactory.createBarChart3D(title, xAxisTitle, yAxisTitle, categorySet, PlotOrientation.VERTICAL, yes.isSelected(), true, false);
			}
		}	
		
		customiseCategoryPlot(chart);
		
		BarRenderer br = (BarRenderer) chart.getCategoryPlot().getRenderer();
		br.setMaximumBarWidth(0.15f);
		
		return chart;
	}

	public JFreeChart createClickCostDistributionChart(String title) 
	{
		JFreeChart chart = null;

		ResultSet clicks = frame.getController().getClickCostDistribution();
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
		
		chart = ChartFactory.createHistogram(title, "Click cost in cents", "Number of clicks", histoSet, PlotOrientation.VERTICAL, yes.isSelected(), true, false);
		
		customiseXYPlot(chart);
		
		return chart;
	}
	
	public JFreeChart createStackedBarChart(String title, boolean normal)
	{
		JFreeChart chart = null;
	
		DefaultCategoryDataset categorySet = new DefaultCategoryDataset();
		String xAxisTitle = "Filter Details";
		String yAxisTitle = metrics.getSelectedItem().toString();
		
		fillCategoryDataset(categorySet, true);

		if (normal)
		{	
			chart = ChartFactory.createStackedBarChart(title, "", yAxisTitle, categorySet,  PlotOrientation.VERTICAL, yes.isSelected(), true, false);
		}
		else
		{
			chart = ChartFactory.createStackedBarChart3D(title, "", yAxisTitle, categorySet,  PlotOrientation.VERTICAL, yes.isSelected(), true, false);
		}
		
		customiseCategoryPlot(chart);
		BarRenderer br = (BarRenderer) chart.getCategoryPlot().getRenderer();
		br.setMaximumBarWidth(0.15f);
		
		return chart;
	}
	
	public JFreeChart createLineChart(String title, boolean normal)
	{
		JFreeChart chart = null;
		String xAxisTitle = null;
		String yAxisTitle =  metrics.getSelectedItem().toString();
		SymbolAxis xAxis = null;
		DefaultCategoryDataset categorySet = new DefaultCategoryDataset(); 
		fillTimeGranularityCategoryDataset(categorySet, xAxis, xAxisTitle);
		
		if (normal)
		{
			chart = ChartFactory.createLineChart(title, xAxisTitle, yAxisTitle, categorySet,  PlotOrientation.VERTICAL, yes.isSelected(), true, false);//ChartFactory.createLineChart(title, xAxisTitle, yAxisTitle, categorySet, PlotOrientation.VERTICAL, yes.isSelected(), true, false);
		}
		else
		{
			chart = ChartFactory.createLineChart3D(title, xAxisTitle, yAxisTitle, categorySet, PlotOrientation.VERTICAL, yes.isSelected(), true, false);
		}
		
		customiseCategoryPlot(chart);
		
		return chart;
	}

	public String setXAxisTitle(String xAxisTitle) 
	{
		switch(timeGranularityBox.getSelectedIndex())
		{
			case 1:
				 xAxisTitle = "Month of Campaign";
				 break;
			case 2:
				 xAxisTitle = "Week Number";
				 break;
			case 3:
				xAxisTitle = "Day of campaign";
				break;
			case 4:	
				xAxisTitle = "Hour of the day";
				break;
		}
		
		return xAxisTitle;
	}

	public void fillTimeGranularityCategoryDataset(DefaultCategoryDataset categorySet, SymbolAxis xAxis, String xAxisTitle)
	{
		Map<Integer, MetricFilter> metricFilters = frame.fStorage.getFilters();
		String[] elements = elementsField.getText().split(" ");
		int numLabels = 0;
		
		for (int i=0; i < elements.length; i++)
		{	
			try 
			{
				String query = metricFilters.get(Integer.parseInt(elements[i])).getQuery();
				ResultSet resultSet = frame.getController().getTimeGranularityResultSet(query, timeGranularityBox.getSelectedItem().toString());
				System.out.println(resultSet);
				while (resultSet.next())
				{					
					categorySet.addValue(resultSet.getDouble(2), elements[i], resultSet.getString(1));
					
					if (i == 0)
					{
						numLabels++;
					}
				}
			} 
			catch (SQLException sqle) 
			{
				sqle.printStackTrace();
			}
		}
		
		xAxisTitle = setXAxisTitle(xAxisTitle);
		xAxis= createCustomSymbolAxis(xAxisTitle, numLabels);
	}

	public void customiseCategoryPlot(JFreeChart chart)
	{
		CategoryPlot catPlot = chart.getCategoryPlot();
		catPlot.getDomainAxis().setLabelFont(font);
		catPlot.getRangeAxis().setLabelFont(font);
		catPlot.setDomainGridlinePaint(Color.BLACK);
		catPlot.setRangeGridlinePaint(Color.BLACK);
		catPlot.setBackgroundPaint(new Color(230,230,230));
	}
	
	public void customiseXYPlot(JFreeChart chart) 
	{
		XYPlot xyPlot = chart.getXYPlot();
		xyPlot.getDomainAxis().setLabelFont(font);
		xyPlot.getRangeAxis().setLabelFont(font);
		xyPlot.setDomainGridlinePaint(Color.BLACK);
		xyPlot.setRangeGridlinePaint(Color.BLACK);
		xyPlot.setBackgroundPaint(new Color(230,230,230));
	}
	
	public JFreeChart createPieChart(String title, boolean normal)
	{
		JFreeChart chart = null;
		DefaultPieDataset pieSet =  new DefaultPieDataset();
		
		if (timeGranularityBox.getSelectedItem().toString().equals("Entire"))
		{
			fillPieDataset(pieSet);
		}
		else
		{
			fillTimeGranularityPieDataset(pieSet);	
		}	
		
		if (normal)
		{
			chart = ChartFactory.createPieChart(title, pieSet, yes.isSelected(), true, false);
		}
		else
		{
			chart = ChartFactory.createPieChart3D(title, pieSet, yes.isSelected(), true, false);
		}
		
		return chart;
	}
	
	public JFreeChart createRingChart(String title)
	{
		JFreeChart chart = null;
		
		DefaultPieDataset pieSet = new DefaultPieDataset();
		
		if (timeGranularityBox.getSelectedItem().toString().equals("Entire"))
		{
			fillPieDataset(pieSet);
		}
		else
		{
			fillTimeGranularityPieDataset(pieSet);	
		}	
		
		chart = ChartFactory.createRingChart(title, pieSet, yes.isSelected(), true, false);
		
		return chart;
	}

	public void fillPieDataset(DefaultPieDataset pieSet) 
	{
		Map<Integer, MetricFilter> metricFilters = frame.fStorage.getFilters();
		Pattern pattern = Pattern.compile("\\d+|(\\((\\d+\\s)+\\d+\\))+");
		Matcher matcher = pattern.matcher(elementsField.getText());
		
		while (matcher.find())
		{
			if (!matcher.group().contains("("))
			{
				MetricFilter current = metricFilters.get(Integer.parseInt(matcher.group()));
				String value = current.getValue();
				pieSet.setValue(current.getFilterDetails(), Integer.parseInt(value));
			}
			else
			{
				String[] elements = matcher.group().substring(1, matcher.group().length() - 1).split(" ");
				String labelText = "Group ID's - ";
				int sum = 0;
						
				for(int i=0; i < elements.length; i++)
				{
					MetricFilter current = metricFilters.get(Integer.parseInt(elements[i]));
					sum += Integer.parseInt(current.getValue());
					labelText += current.getFilterIndex() + ", ";
				}
				
				labelText = labelText.substring(0, labelText.length() - 2);
				
				pieSet.setValue(labelText, sum);
			}
		}
	}
	
	public void fillTimeGranularityPieDataset(DefaultPieDataset pieSet)
	{
		Map<Integer, MetricFilter> metricFilters = frame.fStorage.getFilters();
		String[] elements = elementsField.getText().split(" ");
		
		for (int i=0; i < elements.length; i++)
		{	
			try 
			{
				String query = metricFilters.get(Integer.parseInt(elements[i])).getQuery();
				ResultSet resultSet = frame.getController().getTimeGranularityResultSet(query, timeGranularityBox.getSelectedItem().toString());
					
				while (resultSet.next())
				{					
					pieSet.setValue(resultSet.getString(1), resultSet.getInt(2));
				}	
			} 
			catch (SQLException sqle) 
			{
				sqle.printStackTrace();
			}
		}
	}
	
	public void fillCategoryDataset(DefaultCategoryDataset categorySet, boolean stacked)
	{
		Map<Integer, MetricFilter> metricFilters = frame.fStorage.getFilters();
		Pattern pattern = Pattern.compile("\\d+|(\\((\\d+\\s)+\\d+\\))+");
		Matcher matcher = pattern.matcher(elementsField.getText());
		
		int matches = 0;
		while (matcher.find())
		{
			matches++;
			
			if (!matcher.group().contains("("))
			{
				MetricFilter current = metricFilters.get(Integer.parseInt(matcher.group()));
				String value = current.getValue();
				
				categorySet.addValue(Double.parseDouble(value), String.valueOf(matches), stacked == true? "" : current.getFilterDetails());
			}
			else
			{
				String[] elements = matcher.group().substring(1, matcher.group().length() - 1).split(" ");
				String labelText = "Group ID's - ";
				double sum = 0;
						
				for(int i=0; i < elements.length; i++)
				{
					MetricFilter current = metricFilters.get(Integer.parseInt(elements[i]));
					sum += Integer.parseInt(current.getValue());
					labelText += current.getFilterIndex() + ", ";
				}
				
				labelText = labelText.substring(0, labelText.length() - 2);
				
				categorySet.addValue(sum, String.valueOf(matches), stacked == true? "" : labelText);
			}
		}
	}

	public JFreeChart createXYAreaChart(String title)
	{	
		JFreeChart chart = null;
		XYSeriesCollection xySeriesColl = new XYSeriesCollection();
		SymbolAxis xAxis = null;
		String xAxisTitle = null;		
		String yAxisTitle = timeGranularityBox.getSelectedIndex() == 0 ? "Total " + metrics.getSelectedItem().toString() : metrics.getSelectedItem().toString();
		fillTimeGranularityXYSeriesCollection(xySeriesColl, xAxis, xAxisTitle);

		chart = ChartFactory.createXYAreaChart(title, xAxisTitle, yAxisTitle, xySeriesColl, PlotOrientation.VERTICAL, yes.isSelected(), true, false);	
		
		customiseXYPlot(chart);
		
		return chart;
	}
	
	public JFreeChart createXYLineChart(String title)
	{
		JFreeChart chart = null;
		XYSeriesCollection xySeriesColl = new XYSeriesCollection();
		SymbolAxis xAxis = null;
		String xAxisTitle = null;
		String yAxisTitle = metrics.getSelectedItem().toString();
		fillTimeGranularityXYSeriesCollection(xySeriesColl, xAxis, xAxisTitle);
		
		chart = ChartFactory.createXYLineChart(title, xAxisTitle, yAxisTitle, xySeriesColl, PlotOrientation.VERTICAL, yes.isSelected(), true, false);
		customiseXYPlot(chart);
		
		return chart;
	}
	
	public void fillTimeGranularityXYSeriesCollection(XYSeriesCollection xySeriesColl, SymbolAxis xAxis, String xAxisTitle)
	{
		Map<Integer, MetricFilter> metricFilters = frame.fStorage.getFilters();
		String[] elements = elementsField.getText().split(" ");
		int numLabels = 0;
		
		for (int i=0; i < elements.length; i++)
		{
			XYSeries xySeries = new XYSeries(i);
			
			try 
			{
				String query = metricFilters.get(Integer.parseInt(elements[i])).getQuery();
				ResultSet resultSet = frame.getController().getTimeGranularityResultSet(query, timeGranularityBox.getSelectedItem().toString());
					
				while (resultSet.next())
				{					
					xySeries.add(resultSet.getInt(1), resultSet.getInt(2));
					if (i == 0)
					{
						numLabels++;
					}
				}
				
				xySeriesColl.addSeries(xySeries);
			} 
			catch (SQLException sqle) 
			{
				sqle.printStackTrace();
			}
		}
		
		xAxisTitle = setXAxisTitle(xAxisTitle);
		xAxis= createCustomSymbolAxis(xAxisTitle, numLabels);
	}
	
	public SymbolAxis createCustomSymbolAxis(String xAxisTitle, int numLables) 
	{
		SymbolAxis xAxis;
		
		if (timeGranularityBox.getSelectedIndex() == 3)
		{
			numLables++;
		}
		
		String[] xAxisSymbols = new String[numLables];
		
		for(int i=0; i < numLables; i++)
		{
			if (timeGranularityBox.getSelectedIndex() == 4)
			{
				xAxisSymbols[i] = i + ":00";
				
				if (i < 10)
				{
					xAxisSymbols[i] = "0" + xAxisSymbols[i];
				}
				
			}
			else
			{
				xAxisSymbols[i] = timeGranularityBox.getSelectedIndex() == 3? String.valueOf(i) : String.valueOf(i+1);
			}
		}
		
		xAxis = new SymbolAxis(xAxisTitle, xAxisSymbols);
		xAxis.setTickLabelFont(xAxis.getTickLabelFont().deriveFont(14.0f));
		xAxis.setLabelFont(font);
		
		return xAxis;
}
	
	public DefaultCategoryDataset createClickCostDistributionDataset()
	{
		DefaultCategoryDataset clickSet = new DefaultCategoryDataset();
		ResultSet clicks = frame.getController().getClickCostDistribution();
		
		try 
		{
			while (clicks.next())
			{
				String labelText = String.valueOf(clicks.getDouble(1) * 1000);
				labelText = labelText.substring(0, labelText.length() - 3);
				
				clickSet.addValue(clicks.getInt(2), String.valueOf(clicks.getDouble(1) * 100), labelText.equals("") ? "0" : labelText);
			}
		}
		catch (SQLException sqle) 
		{
			sqle.printStackTrace();
		}
		
		return clickSet;
	}
	
	
	public JFreeChart createStackedAreaChart(String title)
	{
		JFreeChart chart = null;
		String xAxisTitle = null;
		String yAxisTitle =  metrics.getSelectedItem().toString();
		SymbolAxis xAxis = null;
		DefaultCategoryDataset categorySet = new DefaultCategoryDataset(); 
		fillTimeGranularityCategoryDataset(categorySet, xAxis, xAxisTitle);
		
		chart = ChartFactory.createStackedAreaChart(title, xAxisTitle, yAxisTitle, categorySet, PlotOrientation.VERTICAL, yes.isSelected(), true, false);
		
		customiseCategoryPlot(chart);
		
		return chart;
	}
	
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
	
	public void compareCharts() 
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
				for (ChartPanel chartPanel : chartPanelList)
				{
					if (chartPanel.getName().equals(options.getItemAt(options.getSelectedIndex())))
					{	
						JLabel close = new JLabel();
						close.setIcon(new ImageIcon("img/x.png"));
						
						JPanel exitPanel = new JPanel();
						exitPanel.setBackground(Color.WHITE);
						exitPanel.setLayout(new BoxLayout(exitPanel, BoxLayout.LINE_AXIS));
						exitPanel.add(Box.createHorizontalGlue());
						exitPanel.add(close);
						
						JPanel comparePanel = new JPanel();
						comparePanel.setAlignmentX(Component.TOP_ALIGNMENT);
						comparePanel.setBackground(Color.WHITE);
						comparePanel.setLayout(new BoxLayout(comparePanel, BoxLayout.PAGE_AXIS));
						comparePanel.add(exitPanel);
						comparePanel.add(chartPanel);
						
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
	
	public void displayFilterDetails(int filterID)
	{
		FilterStorage storage = frame.fStorage;
		MetricFilter selected = storage.getFilters().get(filterID);
		
		textPane.setFont(font);
		Document doc = textPane.getDocument();
		
		try 
		{
			doc.insertString(doc.getLength(), "Filter ID:" + filterID + "\n", attSet);
			doc.insertString(doc.getLength(), "Gender: " + selected.selectGender.getSelectedItem().toString() + "\n" , attSet);
			doc.insertString(doc.getLength(), "Age: " + selected.selectAge.getSelectedItem().toString() + "\n" , attSet);
			doc.insertString(doc.getLength(), "Income: " + selected.selectIncome.getSelectedItem().toString() + "\n" , attSet);
			doc.insertString(doc.getLength(), "Context: " + selected.selectContext.getSelectedItem().toString() + "\n" , attSet);
			doc.insertString(doc.getLength(), "From: " + selected.startDate + "\n" , attSet);
			doc.insertString(doc.getLength(), "Until: " + selected.endDate, attSet);
		} 
		catch (BadLocationException e) 
		{
			e.printStackTrace();
		}
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
	
	public void showAllowedChartTypes()
	{
		chartTypeBox.removeAllItems();	
		
		if (metrics.getSelectedIndex() == 0)
		{
			chartTypeBox.addItem(CHART_TYPES[0]);
			chartTypeBox.addItem(CHART_TYPES[1]);
			chartTypeBox.addItem(CHART_TYPES[2]);
		}
		else
		{
			for (String CHART_TYPE : CHART_TYPES)
			{
				chartTypeBox.addItem(CHART_TYPE);
			}
			
			chartTypeBox.removeItemAt(2);
		}
		
		if (metrics.getSelectedIndex() > 5)
		{
			chartTypeBox.removeItem(CHART_TYPES[3]);
			chartTypeBox.removeItem(CHART_TYPES[4]);
			chartTypeBox.removeItem(CHART_TYPES[8]);
			chartTypeBox.removeItem(CHART_TYPES[11]);
			chartTypeBox.removeItem(CHART_TYPES[12]);
		}
	}
	
	public void setDefaultSettings()
	{	
		metrics.setSelectedIndex(0);
		elementsField.setText("");
		timeGranularityBox.setSelectedIndex(0);
		no.setSelected(true);
		create.setEnabled(true);
	}
	
	public DefaultListModel<Integer> getDefaultListModel()
	{
		return (DefaultListModel<Integer>) filters.getModel();
	}
	
}