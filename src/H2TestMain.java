

//StockTrading: jdbc:h2:D:\EclipseJee2021-09Ws\StockTrading\Database\StocksDB

//H2Test: jdbc:h2:D:/EclipseJee2021-09Ws/H2Test/Database/StocksDB
//Delete from BseStockHistoricalValues

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;

import org.apache.commons.csv.CSVRecord;
import org.h2.tools.Server;

public class H2TestMain {
	
	public static Connection connection = null; //java.sql.Connection
	public static Server server = null; //org.h2.tools.Server

	public static boolean runQuery(Connection connection) throws Exception {
		String query = "SELECT * FROM BseStockNames";
		Statement statement = null;
		ResultSet resultSet = null;

		statement = connection.createStatement();
		resultSet = statement.executeQuery(query);
		
		while (resultSet.next()) {
			System.out.printf("%d %s \n", resultSet.getInt(1), resultSet.getString(2));
		}

		resultSet.close();
		statement.close();
		
		return true;
	}
	
	public static void main(String[] args) throws Exception {

		try {
			//there are 2 ways you can connect to H2 server
			//native connection and TCP connection
			boolean nativeOrTcp = true;

			
			GetDatabaseConnection.getDBConnection(nativeOrTcp);
	
			if (connection == null) {
				System.out.println("Unable to get connection, exiting...");
				return;
			}

			
			ReadStockCsvFiles readStockCsvFiles = new ReadStockCsvFiles();
			DbUtils dbUtils = new DbUtils();
			
			//Deleting values in table
			int rowsDeleted = dbUtils.deleteRows();
			System.out.println("Number of records deleted from " + Constants.HISTORICAL_VALUE_TABLE + ": " + rowsDeleted + "\n");
			
			String[] stockeNamesArray = {"Reliance.Bse", "M&Mfin.Bse", "HdfcBank.Bse", "BajFinance.Bse"};
			int stockeNamesArray_length = stockeNamesArray.length;
			
			for (int i = 0; i < stockeNamesArray_length; i++) {
				String stockSymbol = stockeNamesArray[i];
				
				List<CSVRecord> csvRecords = readStockCsvFiles.getCSVRecordsForStock(stockSymbol);
				int numberOfRecords = csvRecords.size();
				System.out.println("Number of records in " + stockSymbol + " CSV file: " + numberOfRecords + "\n");

				//CSV received from AVC API is arranged in descending order, but we want to insert stock rows
				//in ascending order. So the method getInsertIntoHistoricalTableQuery() reads the records from
				//the end of csvRecords so we are passing the last index in csvRecords - means insert all rows  
				int fromIndex = (numberOfRecords - 1);
				
				//get the complete insert query
				String insertQuery = dbUtils.getInsertIntoHistoricalTableQuery(stockSymbol, csvRecords, fromIndex);
				
				if (insertQuery == null) {
					System.out.println("Unable to get insert query, exiting...");
					return;
				}
				
				int rowsInserted = dbUtils.insertRows(insertQuery);
				System.out.println("Number of records inserted for " + stockSymbol + ": " + rowsInserted  + "\n");
			}
			
		}
		catch (Exception ex) {
			System.out.println("Exception: " + ex.getMessage());
			return;
		}
		finally {
			if (connection != null) connection.close();
			if (server != null) server.stop();
		}
	}

}
