

public class SqlQueries {
	public static String GET_HISTORIAL_DATA_COUNT_SQL =
		"Select count(BseStockHistoricalValues.StockID) from BseStockHistoricalValues, BseStockNames" +
		" Where BseStockHistoricalValues.StockID = BseStockNames.StockID" +
		" And BseStockNames.StockName = ?" +
		"";

	public static String GET_HISTORIAL_DATA_MAX_DATE_SQL =
		"Select Max(BseStockHistoricalValues.DateVal) from BseStockHistoricalValues, BseStockNames" +
		" Where BseStockHistoricalValues.StockID = BseStockNames.StockID" +
		" And BseStockNames.StockName = ?" +
		"";

	public static String INSERT_HISTORIAL_VALUES_SQL =
		"Insert into BseStockHistoricalValues (StockID, DateVal, OpenVal, HighVal, LowVal, CloseVal, VolumeVal)" +
		" Values " +
		"";

	public static String GET_HISTORIAL_VALUES_FROM_DATE_SQL =
		"Select BseStockHistoricalValues.StockID, StockName, DateVal, OpenVal, HighVal, LowVal, CloseVal, VolumeVal" +
		" from BseStockHistoricalValues, BseStockNames" +
		" Where BseStockHistoricalValues.StockID = BseStockNames.StockID" +
		" And BseStockNames.StockName = ?" + 
		" And BseStockHistoricalValues.DateVal >= ?" + 
		" ORDER BY DateVal ASC";

	public static String GET_SMA_EMA_COUNT_SQL =
		"Select count(BseSmaEmaValues.StockID) from BseSmaEmaValues, BseStockNames" +
		" Where BseSmaEmaValues.StockID = BseStockNames.StockID" +
		" And BseStockNames.StockName = ?" +
		"";

	public static String GET_SMA_EMA_MAX_DATE_SQL =
		"Select Max(BseSmaEmaValues.PeriodDate) from BseSmaEmaValues, BseStockNames" +
		" Where BseSmaEmaValues.StockID = BseStockNames.StockID" +
		" And BseStockNames.StockName = ?" +
		" And BseSmaEmaValues.Period = ?" +
		"";

	public static String INSERT_SMA_EMA_VALUES_SQL =
		"Insert into BseSmaEmaValues (StockID, Period, PeriodDate, Sma, Ema)" +
		" Values " +
		"";

	public static String GET_SMA_EMA_VALUES_SQL =
		"Select BseSmaEmaValues.StockID, StockName, StockID, Period, PeriodDate, Sma, Ema" +
		" from BseSmaEmaValues, BseStockNames" +
		" Where BseSmaEmaValues.StockID = BseStockNames.StockID" +
		" And BseStockNames.StockName = ?" + 
		" ORDER BY DateVal ASC" +
		"";

	public static String DELETE_FROM_HISTORICAL_TABLE = "DELETE FROM " + Constants.HISTORICAL_VALUE_TABLE;
}
