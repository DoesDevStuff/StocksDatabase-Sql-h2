
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

import org.apache.commons.csv.CSVRecord;

public class DbUtils {

	public int insertRows(String insertIntoQuery) {
		Connection connection = null;
		int rowsInserted = -1;
		
		try {
			connection = H2TestMain.connection; 
			if (connection == null) {
				System.out.println("DbUtils:insertRows: connection is null");
				return 0;
			}

            Statement statement = connection.createStatement();
            rowsInserted = statement.executeUpdate(insertIntoQuery);
            statement.close();
		}
		catch (Exception ex) {
			System.out.println("DbUtils:insertRows: Exception"); // + ex.getMessage()
		}

		return rowsInserted;
	}

	public int deleteRows() {
		Connection connection = null;
		int rowsDeleted = -1;
		
		try {
			connection = H2TestMain.connection; 
			if (connection == null) {
				System.out.println("DbUtils:deleteRows: connection is null");
				return 0;
			}

            Statement statement = connection.createStatement();
            rowsDeleted = statement.executeUpdate(SqlQueries.DELETE_FROM_HISTORICAL_TABLE);
            statement.close();
		}
		catch (Exception ex) {
			System.out.println("DbUtils:deleteRows: Exception"); // + ex.getMessage()
		}

		return rowsDeleted;
	}

	//generates "insert into" query for the given parameters
	String getInsertIntoHistoricalTableQuery(String stockSymbol, List<CSVRecord> stockCsvRecords, int fromIndex) {
		boolean debug = false;

		//insert query till "values" clause
		String insertQuery = SqlQueries.INSERT_HISTORIAL_VALUES_SQL + "\n";

		for (int csvIndex = fromIndex; csvIndex >= 0; csvIndex--) {
			CSVRecord csvRecord = stockCsvRecords.get(csvIndex); //get CSVRecord at an index
			DbStockRecord dbStockRecord = new DbStockRecord(stockSymbol, csvRecord); //generate a DbStockRecord from CSVRecord
			
			//append values from DbStockRecord 
			insertQuery += "(" + dbStockRecord.stockID + ", " + "'" + dbStockRecord.dateString + "', " +
			               dbStockRecord.open + ", " + dbStockRecord.high + ", " + dbStockRecord.low +
			               ", " + dbStockRecord.close + ", " + dbStockRecord.volume +
			               ")";

			//append ';' if last record, othrwise append ", " 
			insertQuery += (csvIndex == 0) ? ";\n" : ", \n";
			insertQuery += "";
		}

		if (debug) System.out.println(insertQuery);

		return insertQuery;
	}


}
