# StocksDatabase-Sql-h2
Sql database manipulation using Java and the h2 database for Indian stocks

# Introduction
I started with a goal to create a database for stock trading and analysis. A stock API (Alpha Vantage API) is available for free with certain limitations (calls per minute etc.). I chose to download CSV files for stock scrips, so these files will remain on my local machine.

I wanted to use a new lightweight database that supports the the standatds (ACID, Referential Integrity, and small footprint). As I searched on the Web I found light and embeddable databases like H2, HSQLDB, SQLite. I chose to use H2 database

- Full ACID compliant transactions
- Referential Integrity

The CSV files downloaded with Alpha Vantage API are arranged in descending order by date. I use Eclipse IDE, and I used Apache commons-CSV jars to programmatically get a list of CsvRecords, to insert the Csv Records, I used H2's JDBC driver to connect to the database, and execute SQL queries

I started with the DDL scripts, and created 2 tables for this. One for stock names and one for per day history for daily stock values. The history table has a ID column having foreign key into the master table of stock names.

Note: All the SQL queries used are listed below.
After that, for inserting the daily data, for each stock, the latest date for that stock was taken and records in the CSV files after that date were inserted in the history for each stock were written this way

## Manual DML and DDL examples:

------------------------------------------------------------
Everything for H2 only, but probably work for all databases
------------------------------------------------------------
Saved Settings: Generic H2 (Embedded)
Setting Name: Generic H2 (Embedded)
Driver class: org.h2.Driver
JDBC URL: jdbc:h2:tcp://localhost:8080/D:/EclipseJee2021-09Ws/StockTrading/Database/StocksDB
User Name: sa
Password: <blank>

drop TABLE BseSmaEmaValues;
drop TABLE BseStockHistoricalValues;
drop TABLE BseStockNames;
	
-- Table for stock symbols --- only names + numerical PK
CREATE TABLE BseStockNames(
	StockID int Not Null Primary Key,
	StockName nvarchar(32) Not Null
);
ALTER TABLE BseStockNames ADD CONSTRAINT Unique_StockName UNIQUE (StockName);

// not needed , because stock id is a primary key
ALTER TABLE BseStockNames ADD CONSTRAINT Unique_StockID_StockName UNIQUE (StockID, StockName);

// delete is used to remove rows, Drop is used to delete/remove tables and constraints
// to delete all rows from the table
delete from BseStockNames;
delete from BseStockHistoricalValues;

// to delete with some criteria ( string names must be in single quotes)
delete from BseStockNames where STOCKNAME = 'Itc.Bse';
delete from BseStockNames where STOCKNAME = 'Itc.Bse' OR STOCKID = 182826218 ; // combined columns


-- populate BseStockNames
Insert into BseStockNames (StockID, StockName) Values (-637325307, 'Reliance.Bse');
Insert into BseStockNames (StockID, StockName) Values (-1811992229, 'HdfcBank.Bse');
Insert into BseStockNames (StockID, StockName) Values (-517860450, 'Itc.Bse');
Insert into BseStockNames (StockID, StockName) Values (776361237, 'BajFinance.Bse');
Insert into BseStockNames (StockID, StockName) Values (1604891363, 'JswSteel.Bse');
Insert into BseStockNames (StockID, StockName) Values (781185056, 'AsianPaint.Bse');
Insert into BseStockNames (StockID, StockName) Values (250649598, 'Infy.Bse');
Insert into BseStockNames (StockID, StockName) Values (182826218, 'Tcs.Bse');

INSERT INTO BseStockNames (STOCKID, STOCKNAMES) VALUES (-2012840643,'M&Mfin.Bse');

Select * from BseStockNames;
Select * from BseStockHistoricalValues;

CREATE TABLE BseStockHistoricalValues(
	StockID int Not Null,
	DateVal date Not Null,
	OpenVal decimal(16, 4) Not Null,
	HighVal decimal(16, 4) Not Null,
	LowVal decimal(16, 4) Not Null,
	CloseVal decimal(16, 4) Not Null,
	VolumeVal int Not Null
);
ALTER TABLE BseStockHistoricalValues ADD CONSTRAINT FK_Hist_Stock_Names_StockId FOREIGN KEY(StockID) REFERENCES BseStockNames(StockID);
ALTER TABLE BseStockHistoricalValues ADD CONSTRAINT unique_Stockid_Date UNIQUE (StockID, DateVal);


select Max(DateVal) from BseStockHistoricalValues;

-- Table for SMA and EMA values for the selected stocks
CREATE TABLE BseSmaEmaValues(
	StockID int Not Null,
	Period int Not Null,
	PeriodDate date Not Null,
	Sma decimal(16, 4) Not Null,
	Ema decimal(16, 4) Not Null
);
--ALTER TABLE BseSmaEmaValues ADD FOREIGN KEY(StockID) REFERENCES BseStockNames(StockID);
ALTER TABLE BseSmaEmaValues ADD CONSTRAINT FK_SmaEma_StockNames_StockId FOREIGN KEY(StockID) REFERENCES BseStockNames(StockID);
ALTER TABLE BseSmaEmaValues ADD CONSTRAINT Unique_Stockid_Period_Date UNIQUE (StockID, Period, PeriodDate);


// To get all the records for a given stock name from both StockNames table and historical values table (Combine both tables essentially)

Select StockName, BseStockNames.StockID, DateVal, OpenVal , HighVal, LowVal, CloseVal, VolumeVal  from BseStockNames, BseStockHistoricalValues where BseStockNames.StockName = 'HdfcBank.Bse' And BseStockNames.StockID = BseStockHistoricalValues.StockID;

// Group by , if table multiple rows for the given column group by those.
Select Count(* ), StockID from BseStockHistoricalValues group by StockID;
