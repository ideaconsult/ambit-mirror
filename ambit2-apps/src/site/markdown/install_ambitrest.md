#Installation

## Requirements

###Java Runtime Environment (JRE)

- Install Java 6 or Java 7 Standard Edition.

###Web server

- Install Apache Tomcat 7.x

###Database server

- Install MySQL 5.5.x or 5.6.x

## Web services installation

###Database

- Create the chemical structures database and set up the associated grants. You will need the [create_tables.sql](https://svn.code.sf.net/p/ambit/code/trunk/ambit2-all/ambit2-db/src/main/resources/ambit2/db/sql/create_tables.sql) script.

- Run the following commands in the MySQL console.

````
create database `ambit2` character set utf8;
use `ambit2`;
source create_tables.sql;
GRANT ALL ON `ambit2`.* TO 'guest'@'localhost' IDENTIFIED BY 'guest';
GRANT ALL ON `ambit2`.* TO 'guest'@'127.0.0.1' IDENTIFIED BY 'guest';
GRANT ALL ON `ambit2`.* TO 'guest'@'::1' IDENTIFIED BY 'guest';

GRANT TRIGGER ON `ambit2`.* TO 'guest'@'localhost';
GRANT TRIGGER ON `ambit2`.* TO 'guest'@'127.0.0.1';
GRANT TRIGGER ON `ambit2`.* TO 'guest'@'::1';

GRANT EXECUTE ON PROCEDURE `ambit2`.findByProperty TO 'guest'@'localhost';
GRANT EXECUTE ON PROCEDURE `ambit2`.findByProperty TO 'guest'@'127.0.0.1';
GRANT EXECUTE ON PROCEDURE `ambit2`.findByProperty TO 'guest'@'::1';

GRANT EXECUTE ON PROCEDURE `ambit2`.deleteDataset TO 'guest'@'localhost';
GRANT EXECUTE ON PROCEDURE `ambit2`.deleteDataset TO 'guest'@'127.0.0.1';
GRANT EXECUTE ON PROCEDURE `ambit2`.deleteDataset TO 'guest'@'::1';

GRANT EXECUTE ON PROCEDURE `ambit2`.createBundleCopy TO 'guest'@'localhost';
GRANT EXECUTE ON PROCEDURE `ambit2`.createBundleCopy TO 'guest'@'127.0.0.1';
GRANT EXECUTE ON PROCEDURE `ambit2`.createBundleCopy TO 'guest'@'::1';

GRANT EXECUTE ON PROCEDURE `ambit2`.createBundleVersion TO 'guest'@'localhost';
GRANT EXECUTE ON PROCEDURE `ambit2`.createBundleVersion TO 'guest'@'127.0.0.1';
GRANT EXECUTE ON PROCEDURE `ambit2`.createBundleVersion TO 'guest'@'::1';

````

- Verify the current version of the ambit2 database. Run the following commands in the MySQL console.  

````
use ambit2;
select * from version;
````

The version can be also verified at http://localhost:8080/ambit2/admin/database

###Deployment

- Deploy the [ambit2.war](http://sourceforge.net/projects/ambit/files/Ambit2/AMBIT%20REST%20web%20services/services/ambit-rest-2.5.8/ambit2-www-2.5.8.war/download) in your Tomcat instance.

- Open the following URI in your browser, replacing YOURHOST and YOURPORT with the relevant values from your configuration.

````
http://YOURHOST:YOURPORT/ambit2/dataset
````

- The browser should then display the dataset page (empty).
	
Please note that modern web browsers (e.g. Firefox 10, Chrome, Internet Explorer 10) that comply more strictly with current web standards are highly recommended and will provide better overall performance. System's usability might be impaired to some degree when using older browsers.

#### Customize the configuration settings for the AMBIT2 web application.

>IMPORTANT: This step is required only if your setup differs from the default values.

- Open and edit the following file, replacing the {tomcat-dir} with the relevant value from your configuration.

````
{tomcat-dir}/webapps/ambit2/WEB-INF/classes/config/ambit2.pref
````

- The following option specifies the MySQL database configuration. Modify only if your setup differs from the default.

>IMPORTANT: The guide assumes the user should have grants set as per database installation step above

````
	#MySQL Database config
	Host=localhost
	Scheme=jdbc\:mysql
	Port=3306
	Database=ambit2
	User=guest
	Password=guest
````

####Customize the Web application authentication and authorization

- Open and edit the following file, replacing the {tomcat-dir} with the relevant value from your configuration.

````
	{tomcat-dir}/webapps/ambit2/WEB-INF/classes/config/config.prop
````	

>IMPORTANT: You must restart Tomcat to make sure that the configuration	changes you've made become effective.

#User guide

[Quick user guide](./usage.html)	 