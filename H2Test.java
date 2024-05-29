
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;

import org.h2.tools.Server;

// returning two variables
class ConnectionServer {
	public Connection connection = null;
	public Server server = null;
};

public class H2Test {

	//public static Server server = null; // class variable
	
	public static boolean runQuery(ConnectionServer connectionServer) throws Exception {
		String query = "SELECT * FROM BseStockNames";
		Statement statement = null;
		ResultSet resultSet = null;

		statement = connectionServer.connection.createStatement();
		resultSet = statement.executeQuery(query);
		
		while (resultSet.next()) {
			System.out.printf("%d %s \n", resultSet.getInt(1), resultSet.getString(2));
		}

		resultSet.close();
		statement.close();
		
		return true;
	}
	
	// Insert Into table
	public static boolean runInsertQuery(ConnectionServer connectionServer) throws Exception {
		// Adds one query at a time
		String query = "INSERT INTO BseStockNames (StockID, StockName) Values (250649598, 'Infy.Bse')";
		
		// Method 1 : Multiple insertion query, compound the statements like normal
		//String query = "INSERT INTO BseStockNames (StockID, StockName) Values (250649598, 'Infy.Bse'), (182826218, 'Tcs.Bse')";
		
		Statement statement = null;

		statement = connectionServer.connection.createStatement();
		statement.executeUpdate(query);

		statement.close();
		
		return true;
	}
	
	// Method 2 : Multiple query insertion  
	// references : https://docs.oracle.com/javase/tutorial/jdbc/basics/retrieving.html#batch_updates, https://docs.oracle.com/javase/tutorial/jdbc/basics/prepared.html
	public static boolean runMultipleInsertQuery(ConnectionServer connectionServer) throws Exception {
		// Query when using prepared statements
		String query = "INSERT INTO BseStockNames (StockID, StockName) Values (?, ?)";
		int[] updateRows = null;
		
		// multiple Insert statements, we use something called batch inserts and utilise prepared statements for this too
		PreparedStatement preparedStatement = connectionServer.connection.prepareStatement(query);
		
		connectionServer.connection.setAutoCommit(false);
		
		preparedStatement.setInt(1, -637325307);
		preparedStatement.setString(2, "Reliance.Bse");
        preparedStatement.addBatch();
        
		preparedStatement.setInt(1, -1811992229);
		preparedStatement.setString(2, "HdfcBank.Bse");
        preparedStatement.addBatch();
		
		preparedStatement.setInt(1, -517860450);
		preparedStatement.setString(2, "Itc.Bse");
        preparedStatement.addBatch();
		
		preparedStatement.setInt(1, 776361237);
		preparedStatement.setString(2, "BajFinance.Bse");
        preparedStatement.addBatch();
		
		preparedStatement.setInt(1, 1604891363);
		preparedStatement.setString(2, "JswSteel.Bse");
        preparedStatement.addBatch();
        
		preparedStatement.setInt(1, 781185056);
		preparedStatement.setString(2, "AsianPaint.Bse");
        preparedStatement.addBatch();

        updateRows = preparedStatement.executeBatch();
        
        System.out.println(Arrays.toString(updateRows));
        
        connectionServer.connection.commit();
        connectionServer.connection.setAutoCommit(true);
		
        preparedStatement.close();
		
		return true;
	}
	
	// Insert Into table
	public static boolean runDeleteQuery(ConnectionServer connectionServer) throws Exception {
		// Adds one query at a time
		String query = "DELETE FROM BseStockNames WHERE STOCKNAME = 'Itc.Bse' OR STOCKID = 182826218";
		
		Statement statement = null;

		statement = connectionServer.connection.createStatement();
		statement.executeUpdate(query);

		statement.close();
		
		return true;
	}
	
	
	public static ConnectionServer nativeConnect() throws Exception {
		String url = "jdbc:h2:D:\\EclipseNeonEEWs\\StockTrading\\Database\\StocksDB";
		String user = "sa";
		String passwd = "";
		
		Connection connection = null; //java.sql.Connection

	    connection = DriverManager.getConnection(url, user, passwd); //java.sql.DriverManager

		//runQuery(connection);

	    //connection.close();
	    ConnectionServer connectionServer = new ConnectionServer();
	    connectionServer.connection = connection;
	    
		return connectionServer;
	}
	
	//starts H2 server with TCP
	public static ConnectionServer tcpConnect() throws Exception {
		int serverPort = 8080;
		int webPort = 8090;

		Connection connection = null; //java.sql.Connection
		Server server = null; //org.h2.tools.Server

		String args0 = "-tcpPort"; 
		String args1 = "" + serverPort; 
		String args2 = "-webPort"; 
		String args3 = "" + webPort; 
		String args4 = "-tcpAllowOthers";
		String args5 = "-webAllowOthers";
		String args6 = "-ifNotExists";
		//String url = "jdbc:h2:D:\\EclipseNeonEEWs\\StockTrading\\Database\\StocksDB";
		String url = "jdbc:h2:tcp://localhost:" + serverPort + "/D:/EclipseNeonEEWs/StockTrading/Database/StocksDB"; // there needs to be a forward slash before D:, because it concatenates the string
		String user = "sa";
		String passwd = "";
	
		server = Server.createTcpServer(args0, args1, args2, args3, args4, args5, args6);
		server.start();
	
		//H2Test.server = server;
		
	    connection = DriverManager.getConnection(url, user, passwd);
	    
		//runQuery(connection);

	    //connection.close();
	    //server.stop();
	    ConnectionServer connectionServer = new ConnectionServer();
	    connectionServer.connection = connection;
	    connectionServer.server = server;
	    
		return connectionServer;
	}

	public static void main(String[] args) throws Exception {

		//there are 2 ways you can connect to H2 server
		//native connection and TCP connection
		boolean connectWithNative = false;
		ConnectionServer connectionServer = null;
		
		if (connectWithNative)
			connectionServer = nativeConnect();
		else
			connectionServer = tcpConnect();
		
		//runInsertQuery(connectionServer);
		//runMultipleInsertQuery(connectionServer);
		runDeleteQuery(connectionServer);
		
		runQuery(connectionServer);
		
		if (connectionServer.connection != null) connectionServer.connection.close();
		if (connectionServer.server != null) connectionServer.server.stop();
		
	}

}
