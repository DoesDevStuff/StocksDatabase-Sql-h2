

//StockTrading: jdbc:h2:D:\EclipseJee2021-09Ws\StockTrading\Database\StocksDB

//H2Test: jdbc:h2:D:/EclipseJee2021-09Ws/H2Test/Database/StocksDB
//Delete from BseStockHistoricalValues

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
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
			
			DbUtils dbUtils = new DbUtils();
			
			String[] stockNamesArray = {"HdfcBank.Bse", "BajFinance.Bse"};
			//String[] stockNamesArray = {"Reliance.Bse", "M&Mfin.Bse", "HdfcBank.Bse", "BajFinance.Bse"};
			dbUtils.insertMultipleCsv(stockNamesArray);
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
