To update the content of your repdose database:

1) connect to the database
2)clean old content 
source repdose_clean.sql
3)import the new content
source repdose_updated.sql

Files description:

repdose.sql

Archive from http://repdose.de/phpmyadmin/  June 15, 2009

repdose_clean.sql

An SQL script to clean up the current content

repdose_updated.sql

An SQL script to import updated content with 581 compounds , June 2009

This sql was created as follows:

1) Download and install Ambit-v.1.21 from http://ambit.acad.bg/downloads/index.php
2)Download and install Ambit-v.1.30 upgrade from  http://ambit.acad.bg/downloads/AmbitDb/v1.31/ambit-patch-for-1.31.zip
3) Start AmbitDatabase. This will start the embedded Mysql server at port 33060
4)Connect to Mysql server at port 33060 and import repdose.sql . This will create a copy of the database from the repdose.de site.
Commands:
create database repdose_old; 
use repdose_old;
source repdose.sql;

5) Clean up current content (this will remove structures, cas numbers and fingerprints):

source repdose_clean_old.sql

6) Connect to repdose_old database via AmbitDatabase application and import compounds from repdose.csv file.
7)Run fingerprints generation

8)Dump repdose_updated.sql via mysqldump command.  This will export data only, without create table statements;

mysqldump --user=root --port=33060 --lock-tables --no-create-db  --no-create-info --databases repdose_old > repdose_updated.s
ql

This will output use repdose_old statement, which needs to be removed/modified to use the name of the new database.

9)Close AmbitDatabase application