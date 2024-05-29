import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.CSVParser;

public class ReadStockCsvFiles {
	//***hard-coded here for now --- need to put into constants
	final String csvFilePath = "./Csv"; //path of CSV files relative to this project
	final String csvFileExtension = ".csv";

	//reads all CSV lines for a given stock from a CSV file on disk
	String readStockCsvFromFile(String stockSymbol) {
		String stockCsv = null;
		String fileName = csvFilePath + "/" + stockSymbol + csvFileExtension; //CSV file name is stockSymbol.csv 

		System.out.println("Reading " + stockSymbol + " CSV from " + fileName + "...");
		try {
			stockCsv = new String(Files.readAllBytes(Paths.get(fileName)));
		}
		catch (Exception ex) {
			System.out.println("Error Reading " + fileName);
			System.out.println();
			return null;
		}

		return stockCsv;
	}
	
	//Reads all lines for a stock from a stock CSV file, and converts to a list of "CSVRecord" --- org.apache.commons.csv.CSVRecord
	//Calls readStockCsvFromFile() to get all CSV lines for a given stock
	List<CSVRecord> getCSVRecordsForStock(String stockSymbol) {
		boolean debug = true;
		List<CSVRecord> stockRecords;

		String stockCsvString = readStockCsvFromFile(stockSymbol);
		if (stockCsvString == null)
			return null;
		
		if (debug) System.out.println("Processing CSV for " + stockSymbol + "...");
		try {
			@SuppressWarnings("deprecation")
			CSVFormat csvFormat = CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim();
			
			CSVParser csvParser = CSVParser.parse(stockCsvString, csvFormat);
			stockRecords = csvParser.getRecords();
		}
		catch (Exception ex) {
			System.out.println("Error getting CSV records for " + stockSymbol);
			System.out.println();
			return null;
		}
		
		return stockRecords;
	}

}
