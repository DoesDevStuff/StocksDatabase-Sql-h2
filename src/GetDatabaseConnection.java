import java.sql.Connection;
import java.sql.DriverManager;
import org.h2.tools.Server;

public class GetDatabaseConnection {

	static int serverPort = 8080;
	static int webPort = 8090;

	static String jdbcURLStart = "jdbc:h2:";

	static String tcpPrefix = "tcp://";
	static String tcpAddress = "localhost:";
		
	static String databaseDirectory = "D:/EclipseNeonEEWs/StockTrading/Database/";

	static String databaseName = "StocksDB";

	
	public static Connection getDBConnection(boolean nativeOrTcp) throws Exception {
		Connection connection = null;

		if (nativeOrTcp)
			connection = nativeConnect();
		else
			connection = tcpConnect();

		return connection;
	}

	public static Connection nativeConnect() throws Exception {
		//"jdbc:h2:D:\\EclipseJee2021-09Ws\\StockTrading\\Database\\StocksDB";
		String url = jdbcURLStart + databaseDirectory + databaseName; //compose the URL for native
		String user = "sa";
		String passwd = "";
		
		Connection connection = DriverManager.getConnection(url, user, passwd); //java.sql.DriverManager
		H2TestMain.connection = connection;

		return connection;
	}
	
	//starts H2 server with TCP
	public static Connection tcpConnect() throws Exception {

		String args0 = "-tcpPort"; 
		String args1 = "" + serverPort; 
		String args2 = "-webPort"; 
		String args3 = "" + webPort; 
		String args4 = "-tcpAllowOthers";
		String args5 = "-webAllowOthers";
		String args6 = "-ifNotExists";

		//"jdbc:h2:tcp://localhost:" + serverPort + "/D:/EclipseJee2021-09Ws/StockTrading/Database/StocksDB";
		String url = jdbcURLStart + tcpPrefix + tcpAddress + serverPort + "/" + databaseDirectory + databaseName; //compose the URL for tcp
		// ***                                                             ^

		String user = "sa";
		String passwd = "";
	
		H2TestMain.server = Server.createTcpServer(args0, args1, args2, args3, args4, args5, args6);
		H2TestMain.server.start();
	
		Connection connection = DriverManager.getConnection(url, user, passwd);
		H2TestMain.connection = connection;

		return connection;
	}
}
