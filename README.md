# StocksDatabase-Sql-h2
Sql database manipulation using Java and the h2 database for Indian stocks

# Introduction

I began with the purpose of developing a database for stock trading and analysis. I opted to use the Api provided by Alpha Vantage which is free but has some constraints (calls per minute, etc.). I decided to download CSV files for stock scrips, so they will remain on my local system. <br>

I intended to use a new, lightweight database that met the criteria (ACID, Referential Integrity, and Small Footprint). H2, HSQLDB, and SQLite were among the light and embeddable databases. I elected to use the H2 database. <br>
It enables this project to have: 
- Full ACID-compliant transactions
- Referential Integrity
<br>
The CSV files retrieved via the Alpha Vantage API are sorted in descending order by date.  I used the Eclipse IDE and the Apache commons-CSV jars to programmatically extract a list of CsvRecords.<br>
To insert the CSV Records, I used H2's JDBC driver to connect to the database and perform SQL queries.

I started with the DDL scripts and made two tables for this. One for stock names and one for a daily history of stock prices. The history table contains an ID column with foreign keys to the master table containing stock names.
<br>
> Note:
>  All the SQL queries used are listed [add this link].
> After that, for inserting the daily data, for each stock, the latest date for that stock was taken
> and records in the CSV files after that date were inserted in the history for each stock and were written this way

## Manual DML and DDL examples:

------------------------------------------------------------

Everything for H2 only, but probably work for all databases

------------------------------------------------------------

Saved Settings: Generic H2 (Embedded) <br>
Setting Name: Generic H2 (Embedded) <br>
Driver class: org.h2.Driver <br>
JDBC URL: jdbc:h2:tcp://localhost:8080/D:/EclipseJee2021-09Ws/StockTrading/Database/StocksDB <br>
User Name: sa <br>
Password: <blank> <br>


drop TABLE BseSmaEmaValues;

drop TABLE BseStockHistoricalValues;

drop TABLE BseStockNames;

#### Table for stock symbols --- only names + numerical PK

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

#### populate BseStockNames

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

#### Table for SMA and EMA values for the selected stocks

CREATE TABLE BseSmaEmaValues(

StockID int Not Null,

Period int Not Null,

PeriodDate date Not Null,

Sma decimal(16, 4) Not Null,

Ema decimal(16, 4) Not Null

);

#### ALTER TABLE BseSmaEmaValues ADD FOREIGN KEY(StockID) REFERENCES BseStockNames(StockID);

ALTER TABLE BseSmaEmaValues ADD CONSTRAINT FK_SmaEma_StockNames_StockId FOREIGN KEY(StockID) REFERENCES BseStockNames(StockID);

ALTER TABLE BseSmaEmaValues ADD CONSTRAINT Unique_Stockid_Period_Date UNIQUE (StockID, Period, PeriodDate);

// To get all the records for a given stock name from both StockNames table and historical values table (Combine both tables essentially)

Select StockName, BseStockNames.StockID, DateVal, OpenVal , HighVal, LowVal, CloseVal, VolumeVal  from BseStockNames, BseStockHistoricalValues where BseStockNames.StockName = 'HdfcBank.Bse' And BseStockNames.StockID = BseStockHistoricalValues.StockID;

// Group by , if table multiple rows for the given column group by those.

Select Count(* ), StockID from BseStockHistoricalValues group by StockID;

## Table Diagram

![image](https://github.com/DoesDevStuff/StocksDatabase-Sql-h2/assets/74312830/0c4f1537-51b1-4518-ba4a-0c55d067cf55)

