
import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
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

	public int deleteRows(String stockSymbol) {
		Connection connection = null;
		int rowsDeleted = -1;
		String deleteQuery = SqlQueries.DELETE_FROM_HISTORICAL_TABLE_WHERE + stockSymbol.hashCode();
		
		try {
			connection = H2TestMain.connection; 
			if (connection == null) {
				System.out.println("DbUtils:deleteRows: connection is null");
				return 0;
			}

            Statement statement = connection.createStatement();
            rowsDeleted = statement.executeUpdate(deleteQuery);
            statement.close();
		}
		catch (Exception ex) {
			System.out.println("DbUtils:deleteRows: Exception"); // + ex.getMessage()
		}

		return rowsDeleted;
	}

	public int checkExistenceOfStock(String stockSymbol) {
		Connection connection = null;
		int count = -1;
		ResultSet resultSet = null;
		String countQuery = SqlQueries.SELECT_COUNT_FROM_BSESTOCKNAMES_TABLE_WHERE + "'" + stockSymbol + "'";
		
		try {
			connection = H2TestMain.connection; 
			if (connection == null) {
				System.out.println("DbUtils:deleteRows: connection is null");
				return 0;
			}

            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(countQuery);
			        
			if (resultSet.next())
			{
				count =  resultSet.getInt(1);    
			}
			
    		resultSet.close();
            statement.close();
		}
		catch (Exception ex) {
			System.out.println("DbUtils:deleteRows: Exception"); // + ex.getMessage()
		}
		
		return count;
	}
	
	/*
	public int checkExistenceOfHistoricalValues() {
		Connection connection = null;
		int count = -1;
		ResultSet resultSet = null;
		String countQuery = SqlQueries.SELECT_COUNT_FROM_HISTORICAL_VALUES_TABLE;
		
		try {
			connection = H2TestMain.connection; 
			if (connection == null) {
				System.out.println("DbUtils:deleteRows: connection is null");
				return 0;
			}

            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(countQuery);
			        
			if (resultSet.next())
			{
				count =  resultSet.getInt(1);    
			}
			
    		resultSet.close();
            statement.close();
		}
		catch (Exception ex) {
			System.out.println("DbUtils:deleteRows: Exception"); // + ex.getMessage()
		}
		
		return count;
	}
	*/
	public Date getMaxDateValFromHistoricalValuesTable(String stockSymbol) {
		Connection connection = null;
		Date maxDate = null;
		ResultSet resultSet = null;
		int StockID = stockSymbol.hashCode();
		
		String countQuery = SqlQueries.SELECT_MAX_DATE_FROM_HISTORICAL_VALUES_TABLE + StockID + ";";
		
		try {
			connection = H2TestMain.connection; 
			if (connection == null) {
				System.out.println("DbUtils:deleteRows: connection is null");
				return null;
			}

            Statement statement = connection.createStatement();
            resultSet = statement.executeQuery(countQuery);
			        
			if (resultSet.next())
			{
				maxDate = resultSet.getDate(1);
			}
			
    		resultSet.close();
            statement.close();
		}
		catch (Exception ex) {
			System.out.println("DbUtils:deleteRows: Exception"); // + ex.getMessage()
		}
		
		return maxDate;
		
	}
	
	public boolean checkIfCsvDateGreaterthanMaxDateInDb(String stockSymbol, Date maxDate, List<CSVRecord> stockCsvRecords, int fromIndex) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
		String maxDateInHistoricalTable = dateFormat.format(maxDate);
		
		if (maxDateInHistoricalTable == null) {
			System.out.println("The table BseStockHistoricalValues is empty...");
			return false;
		}
		
		for (int csvIndex = fromIndex; csvIndex >= 0; csvIndex--) {
		    CSVRecord csvRecord = stockCsvRecords.get(csvIndex);
		    DbStockRecord dbStockRecord = new DbStockRecord(stockSymbol, csvRecord);
		    String  csvDate = dbStockRecord.dateString;
		
		
		    if (csvDate.compareTo(maxDateInHistoricalTable) > 0) {
		    	System.out.println("Found new values inserting...");
		        return true;
		    }
		    else if (csvDate.equals(maxDateInHistoricalTable)) {
	            return false;
	        }
		}
		return false;
	}
	
	//stockCsvRecords list is populated into database in reverse order
	//find an index in stockCsvRecords from which data is to be populated
	//return value -1 means error, -2 means no action - DB already has the latest value
	//Otherwise the index in (in CSV to update records
	int getIndexInAvrecordsToWriteFrom(String stockSymbol, List<CSVRecord> stockCsvRecords) {
		boolean debug = false;
		if (debug) System.out.println("Getting index to write database from for " + stockSymbol + "...");

		int countOfRecordsInCSV = stockCsvRecords.size(); //number of records in the CSV file
			
		//get the most recent date for this stock in the historical table
		Date maxDBDate = getMaxDateValFromHistoricalValuesTable(stockSymbol);
		//if maxDBDate is null, it means there are no records for this stock
		if (maxDBDate == null)
			return countOfRecordsInCSV - 1; //database has no records, start from end

		//maxDBDate is not null, so  search from top to get an index
		//where the date is equal to maxDate, and take an index just above it
		int dateEqualIndex = -1;
		for(int csvIndex = 0; csvIndex < countOfRecordsInCSV; csvIndex++) {
			CSVRecord csvRecord = stockCsvRecords.get(csvIndex);
			DbStockRecord dbStockRecord = new DbStockRecord(stockSymbol, csvRecord);
			
			if (maxDBDate.equals(dbStockRecord.sqlDate)) {
				dateEqualIndex = csvIndex; //index found
				break;
			}
		}

		//if index not found, return -1 (error)
		if (dateEqualIndex == -1)
			return -1;
        
		//if DB date is same for index zero, no action needs to be done, return -2
		if (dateEqualIndex == 0)
			return -2;

		// start from the index above the date as there are more records than in the DB
		return dateEqualIndex - 1;
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
			
			//append ';' if last record, otherwise append ", " 
			insertQuery += (csvIndex == 0) ? ";\n" : ", \n";
			insertQuery += "";
		}
		
		if (debug) System.out.println(insertQuery);

		return insertQuery;
	}
	
	public void insertMultipleCsv(String[] stockNamesArray) {
		int stockeNamesArrayLength = stockNamesArray.length;
		String insertQuery = null;
		ReadStockCsvFiles readStockCsvFiles = new ReadStockCsvFiles();
		
		for (int i = 0; i < stockeNamesArrayLength; i++) {
			String stockSymbol = stockNamesArray[i];
			
			if (checkExistenceOfStock(stockSymbol) == 0) {
				int StockID = stockSymbol.hashCode();
				String StockName = stockSymbol;
				String insertIntoTableQuery = SqlQueries.INSERT_INTO_BSESTOCKNAMES_TABLE  + 
													"(" + StockID + "," + "'" + StockName + "'" + ")" + ";\n";
				insertRows(insertIntoTableQuery);
			}
			
			
			List<CSVRecord> csvRecords = readStockCsvFiles.getCSVRecordsForStock(stockSymbol);
			int numberOfRecords = csvRecords.size();
			System.out.println("Number of records in " + stockSymbol + " CSV file: " + numberOfRecords + "\n");

			//CSV received from AVC API is arranged in descending order, but we want to insert stock rows
			//in ascending order. So the method getInsertIntoHistoricalTableQuery() reads the records from
			//the end of csvRecords so we are passing the last index in csvRecords - means insert all rows  
			//int fromIndex = (numberOfRecords - 1);
			int fromIndex = getIndexInAvrecordsToWriteFrom(stockSymbol, csvRecords);

			if (fromIndex == -1) {
				System.out.println("Error getting the index to write database");
				continue;
			}

			if (fromIndex == -2) {
				System.out.println("nothing to write to Database --- it is already up-to date");
				continue;
			}

			System.out.println("records in stockCsvRecords: " + csvRecords.size());
			System.out.println("populating from index: " + fromIndex);
			
			//get the complete insert query
			insertQuery = getInsertIntoHistoricalTableQuery(stockSymbol, csvRecords, fromIndex);		
			
			if (insertQuery == null) {
				System.out.println("Unable to get insert query, for: " + stockSymbol);
				continue;
			}
			
			int rowsInserted = insertRows(insertQuery);
			System.out.println("Number of records inserted for " + stockSymbol + ": " + rowsInserted  + "\n");		
			
		}
		
	}

}
