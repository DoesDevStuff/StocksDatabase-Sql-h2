
import java.text.SimpleDateFormat;
import org.apache.commons.csv.CSVRecord;

public class DbStockRecord {
	public int stockID = -1;
	public String stockName = null;
	public String dateString = null;
	public java.sql.Date sqlDate = null;
	public double open = -1;
	public double high = -1;
	public double low = -1;
	public double close = -1;
	public double volume = -1;
	
/*
	//no-arg constructor not available
	public DbStockRecord() {
	}
*/

	//constructor: converts CSVRecord to DbStockRecord
	public DbStockRecord(String StockName, CSVRecord csvRecord) {
		createDbRecordFromCSVRecord(StockName, csvRecord);
	}

	//helper function to convert CSVRecord to DbStockRecord
	protected void createDbRecordFromCSVRecord(String stockName, CSVRecord csvRecord) {
		try {
			this.stockName = stockName;
			this.stockID = stockName.hashCode(); //stockID is hashcode of the stock name
			dateString = csvRecord.get(Constants.TIMESTAMP); //get date as a string from CSVRecord 

			
			//convert date string from CSVRecord to java.sql.Date format
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat(Constants.DATE_FORMAT);
			java.util.Date javaUtilDate = simpleDateFormat.parse(dateString);
			this.sqlDate = new java.sql.Date(javaUtilDate.getTime());
			

			//convert OHLCV strings from CSVRecord to number 
			this.open = Double.parseDouble(csvRecord.get(Constants.OPEN));
			this.high = Double.parseDouble(csvRecord.get(Constants.HIGN));
			this.low = Double.parseDouble(csvRecord.get(Constants.LOW));
			this.close = Double.parseDouble(csvRecord.get(Constants.CLOSE));
			this.volume = Integer.parseInt(csvRecord.get(Constants.VOLUME)); //*** note
		}
		catch (Exception ex) {
			System.out.println(ex.getMessage());
		}

	}
	
}
