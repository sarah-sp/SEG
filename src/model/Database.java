package model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import javax.swing.SwingWorker;

import org.sqlite.SQLiteConfig;

import controller.Controller;
import gui.MetricFilter;

public class Database {
	private Controller controller;
	private Connection conn;
	private SQLiteConfig config;
	private Statement stmt;
	private DecimalFormat decimalPrecision2;
	private SimpleDateFormat dateFormat;

	public void setController(Controller controller) {
		this.controller = controller;
		setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));
		configureSQLite();
	}

	public SQLiteConfig getSQLiteConfig() {
		return config;
	}

	public void configureSQLite() {
		config = new SQLiteConfig();

		config.setSynchronous(SQLiteConfig.SynchronousMode.OFF);
		config.setJournalMode(SQLiteConfig.JournalMode.MEMORY);
		config.setTempStore(SQLiteConfig.TempStore.MEMORY);
		config.setCacheSize(500000);
		config.setLockingMode(SQLiteConfig.LockingMode.EXCLUSIVE);
	}

	public boolean createDatabase(String dbName) {
		File dir = new File("database");
		File dB = new File("database/" + dbName);

		if (dB.exists()) {
			return true;
		} else {
			dir.mkdirs();
			return false;
		}
	}

	public void createTables(String name) {
		try {
			conn = DriverManager.getConnection("jdbc:sqlite:database/" + name, config.toProperties());
			Statement stmt = conn.createStatement();

			String createStmt = "CREATE TABLE IF NOT EXISTS impressions(date DATE, ID TEXT, gender TEXT, age TEXT, income TEXT, context TEXT, cost REAL);"
					+ "CREATE TABLE IF NOT EXISTS clicks(date DATE, ID TEXT, cost REAL);"
					+ "CREATE TABLE IF NOT EXISTS server_log(entry_date DATE, ID TEXT, exit_date DATE, pgs_viewed INTEGER, conversion TEXT);";

			stmt.executeUpdate(createStmt);
			conn.close();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
	}

	public void createIndexes(String name) {
		try {
			Connection conn = DriverManager.getConnection("jdbc:sqlite:database/" + name, config.toProperties());
			Statement stmt = conn.createStatement();
			String indexStmt = "CREATE INDEX impressionsID ON impressions(ID, date);"
					+ "CREATE INDEX impressionsGender ON impressions(context, age, income, gender);"
					+ "CREATE INDEX impressionsCost ON impressions(cost);"
					+ "CREATE INDEX clicksID ON clicks(ID, date, cost);"
					+ "CREATE INDEX serverID ON server_log(ID, entry_date);";

			stmt.executeUpdate(indexStmt);
			conn.close();
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}
	}

	public void importFiles(String path, String name) {
		final SwingWorker<Void, Void> myWorker = new SwingWorker<Void, Void>() {
			protected Void doInBackground() throws Exception {
				importCSV(path + "/impression_log.csv", "impressions", name);
				importCSV(path + "/click_log.csv", "clicks", name);
				importCSV(path + "/server_log.csv", "server_log", name);
				createIndexes(name);

				return null;
			}
		};

		myWorker.addPropertyChangeListener(new PropertyChangeListener() {
			@Override
			public void propertyChange(PropertyChangeEvent cE) {
				if (cE.getNewValue() == SwingWorker.StateValue.DONE) {
					controller.endProgressBar();

					try {
						myWorker.get();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});

		myWorker.execute();
	}

	public void importCSV(String filePath, String table, String name) {
		try {
			Connection conn = DriverManager.getConnection("jdbc:sqlite:database/" + name, config.toProperties());
			conn.setAutoCommit(false);

			PreparedStatement pstmt1 = null;
			BufferedReader br = new BufferedReader(new FileReader(filePath));

			String line;
			String sql1 = "INSERT INTO impressions(date, ID, gender, age, income, context, cost) VALUES(?,?,?,?,?,?,?);";
			String sql2 = "INSERT INTO clicks(date, ID, cost) VALUES(?,?,?);";
			String sql3 = "INSERT INTO server_log(entry_date, ID, exit_date, pgs_viewed, conversion) VALUES(?,?,?,?,?);";

			int counter = 0;

			switch (table) {
			case "impressions":
				pstmt1 = conn.prepareStatement(sql1);
				br.readLine();

				while ((line = br.readLine()) != null) {
					String[] values = line.split(",");
					pstmt1.setString(1, values[0]);// ID
					pstmt1.setString(2, values[1]);// ID
					pstmt1.setString(3, values[2]);// Gender
					pstmt1.setString(4, values[3]);// Age
					pstmt1.setString(5, values[4]);// Income
					pstmt1.setString(6, values[5]);// Context
					pstmt1.setString(7, values[6]);// Cost

					pstmt1.addBatch();
					counter++;

					if (counter > 999) {
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

				while ((line = br.readLine()) != null) {
					String[] values = line.split(",");
					pstmt1.setString(1, values[0]);// Date
					pstmt1.setString(2, values[1]);// ID
					pstmt1.setString(3, values[2]);// Cost

					pstmt1.addBatch();
					counter++;

					if (counter > 999) {
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

				while ((line = br.readLine()) != null) {
					String[] values = line.split(",");
					pstmt1.setString(1, values[0]); // Entry Date
					pstmt1.setString(2, values[1]); // ID
					pstmt1.setString(3, values[2]); // Exit Date
					pstmt1.setString(4, values[3]); // Pages Viewed
					pstmt1.setString(5, values[4]); // Conversion

					pstmt1.addBatch();
					counter++;

					if (counter > 999) {
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
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public String getQueryResult(MetricFilter filter) {
		String noImpressions = "SELECT count() FROM impressions WHERE (1 ";
		String noClicks = "SELECT count() FROM (SELECT DISTINCT clicks.date, clicks.ID, clicks.cost FROM clicks JOIN impressions ON impressions.ID = clicks.ID WHERE 1 ";
		String noUniques = "SELECT count() FROM (SELECT DISTINCT clicks.date AS date, clicks.ID FROM clicks JOIN impressions ON impressions.ID = clicks.ID WHERE 1 ";
		String noConversions = "SELECT count() FROM (SELECT DISTINCT server_log.ID, server_log.entry_date FROM server_log JOIN impressions ON impressions.ID = server_log.ID WHERE conversion = 'Yes' ";
		String noBouncesPage = "SELECT count() FROM (SELECT DISTINCT server_log.ID, server_log.entry_date FROM server_log JOIN impressions ON impressions.ID = server_log.ID WHERE pgs_viewed = 1 AND conversion = 'No' ";
		String noBouncesTime = "SELECT count() FROM (SELECT DISTINCT server_log.ID, server_log.entry_date FROM server_log JOIN impressions ON impressions.ID = server_log.ID WHERE conversion = 'No' AND strftime('%s', server_log.exit_date) - strftime('%s', server_log.entry_date) < 5 ";

		String result = null;
		String query = null;

		decimalPrecision2 = new DecimalFormat("#.##");
		DecimalFormat decimalPrecision3 = new DecimalFormat("#.###");

		decimalPrecision2.setRoundingMode(RoundingMode.HALF_UP);
		decimalPrecision3.setRoundingMode(RoundingMode.HALF_UP);

		try {
			Connection conn = DriverManager.getConnection("jdbc:sqlite:database/campaign.db",
					controller.getSQLiteConfig().toProperties());
			stmt = conn.createStatement();

			switch (filter.metricsBox.getSelectedItem().toString()) {
			case "Number of Impressions":
				query = applyFilterToQuery(noImpressions, filter);
				result = stmt.executeQuery(query).getString(1);
				break;
			case "Number of Clicks":
				query = applyFilterToQuery(noClicks, filter);
				result = stmt.executeQuery(query).getString(1);
				break;
			case "Number of Uniques":
				query = applyFilterToQuery(noUniques, filter);
				result = stmt.executeQuery(query).getString(1);
				break;
			case "Number of Conversions":
				query = applyFilterToQuery(noConversions, filter);
				result = stmt.executeQuery(query).getString(1);
				break;
			case "Number of Bounces":
				query = controller.getView().getBounceDef().equals("pages")
						? query = applyFilterToQuery(noBouncesPage, filter) : applyFilterToQuery(noBouncesTime, filter);
				result = stmt.executeQuery(query).getString(1);
				break;
			case "Total Cost":
				query = result = decimalPrecision2.format(getTotalCost(filter));
				break;
			case "Click-through Rate (CTR)": // Number of Clicks / Number of
												// Impressions
				double ctr = stmt.executeQuery(applyFilterToQuery(noClicks, filter)).getDouble(1)
						/ stmt.executeQuery(applyFilterToQuery(noImpressions, filter)).getDouble(1);
				result = decimalPrecision3.format(ctr);
				break;
			case "Cost-per-acquisition (CPA)": // Total Cost / No of Conversions
				double cpa = getTotalCost(filter)
						/ stmt.executeQuery(applyFilterToQuery(noConversions, filter)).getDouble(1);
				result = decimalPrecision2.format(cpa);
				break;
			case "Cost-per-click (CPC)": // Total Cost / No of Clicks
				double cpc = getTotalCost(filter)
						/ stmt.executeQuery(applyFilterToQuery(noClicks, filter)).getDouble(1);
				result = decimalPrecision2.format(cpc);
				break;
			case "Cost-per-thousand impressions (CPM)": // Total Cost / No of
														// Impressions / 1000
				double cpm = getTotalCost(filter)
						/ (stmt.executeQuery(applyFilterToQuery(noImpressions, filter)).getDouble(1) / 1000);
				result = decimalPrecision2.format(cpm);
				break;
			case "Bounce Rate": // No of bounces / No of clicks
				double bounceDef = controller.getView().getBounceDef().equals("pages")
						? stmt.executeQuery(applyFilterToQuery(noBouncesPage, filter)).getDouble(1)
						: stmt.executeQuery(applyFilterToQuery(noBouncesTime, filter)).getDouble(1);
				double bounceRate = bounceDef / stmt.executeQuery(applyFilterToQuery(noClicks, filter)).getDouble(1);
				result = decimalPrecision3.format(bounceRate);
				break;
			}
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}

		if (filter.metricsBox.getSelectedItem().toString().toLowerCase().contains("cost")) {
			result = "�" + result;
		}

		filter.setValue(result);
		filter.setQuery(query);

		return result;
	}

	public String applyFilterToQuery(String query, MetricFilter filter) throws SQLException {
		String startDate = filter.startDate.getText();
		String endDate = filter.endDate.getText();

		// bounces, conversions
		query += "AND strftime('%Y-%m-%d', impressions.date) >= strftime('%Y-%m-%d', '" + startDate
				+ "') AND strftime('%Y-%m-%d', impressions.date) <= strftime('%Y-%m-%d', '" + endDate + "')";

		if (!(filter.selectGender.getSelectedItem().equals("All") && filter.selectAge.getSelectedItem().equals("All")
				&& filter.selectIncome.getSelectedItem().equals("All")
				&& filter.selectContext.getSelectedItem().equals("All"))) {
			if (!filter.selectGender.getSelectedItem().equals("All")) {
				query += " AND impressions.gender = '" + filter.selectGender.getSelectedItem() + "'";
			}

			if (!filter.selectAge.getSelectedItem().equals("All")) {
				query += " AND impressions.age = '" + filter.selectAge.getSelectedItem() + "'";
			}

			if (!filter.selectIncome.getSelectedItem().equals("All")) {
				query += " AND impressions.income = '" + filter.selectIncome.getSelectedItem() + "'";
			}

			if (!filter.selectContext.getSelectedItem().equals("All")) {
				query += " AND impressions.context = '" + filter.selectContext.getSelectedItem() + "'";
			}
		}

		query += ") GROUP BY strftime();";

		return query;
	}

	public double getTotalCost(MetricFilter filter) throws NumberFormatException, SQLException {
		String imprCostSum = "SELECT sum(cost) FROM impressions WHERE (1 ";
		String clickCostSum = "SELECT sum(cost) FROM (SELECT DISTINCT clicks.date, clicks.ID, clicks.cost FROM clicks JOIN impressions ON impressions.ID = clicks.ID WHERE 1 ";

		double impressionCost = stmt.executeQuery(applyFilterToQuery(imprCostSum, filter)).getDouble(1);
		double clickCost = stmt.executeQuery(applyFilterToQuery(clickCostSum, filter)).getDouble(1);

		return (impressionCost + clickCost) / 100;
	}

	public ResultSet getTimeGranularityResultSet(String query, String timeGranularity) {
		ResultSet resultSet = null;
		String[] tokens = query.split(" ");

		switch (timeGranularity) {
		case "Months":
			tokens[0] = "SELECT strftime('%m', date),";
			tokens[tokens.length - 1] = "strftime('%m', date)";
			break;
		case "Weeks":
			tokens[0] = "SELECT strftime('%W', date),";
			tokens[tokens.length - 1] = "strftime('%W', date)";
			break;
		case "Days":
			tokens[0] = "SELECT strftime('%d', date),";
			tokens[tokens.length - 1] = "strftime('%d', date)";
			break;
		case "Hours":
			tokens[0] = "SELECT strftime('%H:00', date),";
			tokens[tokens.length - 1] = "strftime('%H', date)";
			break;
		}

		if (query.contains("AS")) {
			tokens[tokens.length - 4] = tokens[tokens.length - 4].substring(0, tokens[tokens.length - 4].length() - 1);
			tokens[tokens.length - 3] = "GROUP BY clicks.date) GROUP";
		}

		if (query.contains("entry_date")) {
			tokens[0] = tokens[0].split(",")[0] + ", entry_date),";
			tokens[tokens.length - 1] = tokens[tokens.length - 1].split(",")[0] + ", entry_date)";
		}
		StringBuilder stringBuilder = new StringBuilder();

		for (String token : tokens) {
			stringBuilder.append(token + " ");
		}

		query = stringBuilder.toString();

		try {
			resultSet = stmt.executeQuery(query);
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}

		return resultSet;
	}

	public ResultSet getClickCostDistribution() {
		ResultSet clicks = null;

		try {
			Connection conn = DriverManager.getConnection("jdbc:sqlite:database/campaign.db",
					controller.getSQLiteConfig().toProperties());

			stmt = conn.createStatement();
			clicks = stmt.executeQuery("SELECT ROUND(cost)/100, COUNT(cost) FROM clicks GROUP BY ROUND(cost);");
		} catch (SQLException sqle) {
			sqle.printStackTrace();
		}

		return clicks;
	}

	public SimpleDateFormat getDateFormat() {
		return dateFormat;
	}

	public void setDateFormat(SimpleDateFormat dateFormat) {
		this.dateFormat = dateFormat;
	}
}
