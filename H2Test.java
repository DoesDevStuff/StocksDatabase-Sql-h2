
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.h2.tools.Server;

public class H2Test {

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
	
	public static boolean nativeConnect() throws Exception {
		String url = "jdbc:h2:D:\\EclipseJee2021-09Ws\\StockTrading\\Database\\StocksDB";
		String user = "sa";
		String passwd = "";
		
		Connection connection = null; //java.sql.Connection

	    connection = DriverManager.getConnection(url, user, passwd); //java.sql.DriverManager

		runQuery(connection);

	    connection.close();
	    
		return true;
	}
	
	//starts H2 server with TCP
	public static boolean tcpConnect() throws Exception {
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

		String url = "jdbc:h2:tcp://localhost:" + serverPort + "/D:/EclipseJee2021-09Ws/StockTrading/Database/StocksDB";
		String user = "sa";
		String passwd = "";
	
		server = Server.createTcpServer(args0, args1, args2, args3, args4, args5, args6);
		server.start();
	
	    connection = DriverManager.getConnection(url, user, passwd);
	    
		runQuery(connection);

	    connection.close();
	    server.stop();

		return true;
	}

	public static void main(String[] args) throws Exception {

		//there are 2 ways you can connect to H2 server
		//native connection and TCP connection
		boolean connectWithNative = true;
		
		if (connectWithNative)
			nativeConnect();
		else
			tcpConnect();
	}

}
