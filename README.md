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
