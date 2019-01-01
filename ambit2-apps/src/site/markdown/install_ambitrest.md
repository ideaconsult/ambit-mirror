#AMBIT web application install guide 

[Download](./downloads.html) | [Installation guide](install_ambitrest.html) | [Configuration](configure.html) | [Authentication and authorisation](./configureaa.html) | [For developers](./dev.html)

###Requirements

* Java Runtime Environment ([JRE](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html)). Install Java 7 Standard Edition or newer.

* Web server. Install [Apache Tomcat](http://tomcat.apache.org/download-70.cgi) 7.x or newer.

* Database server. Install [MySQL 5.6.5](https://dev.mysql.com/doc/relnotes/mysql/5.6/en/) or newer

## Web services installation

###Database

- Create the chemical structures database and set up the associated grants. You will need the [create_tables.sql](https://svn.code.sf.net/p/ambit/code/trunk/ambit2-all/ambit2-db/src/main/resources/ambit2/db/sql/create_tables.sql) script.

- Run the following commands in the MySQL console.  [database create and grant access rights script](txt/dbgrants.sql)

- Verify the current version of the ambit2 database. Run the following commands in the MySQL console.  

````
use ambit2;
select * from version;
````

The version can be also verified at http://localhost:8080/ambit2/admin/database

###Deployment

- Deploy the [ambit2.war](http://sourceforge.net/projects/ambit/files/Ambit2/AMBIT%20REST%20web%20services/services/ambit-rest-3.0.1/ambit2-www-3.0.1.war/download) in your Tomcat instance.

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
{tomcat-dir}/webapps/ambit2/WEB-INF/classes/ambit2/rest/config/ambit2.pref
````

- The following option specifies the MySQL database configuration. Modify only if your setup differs from the default.
  [Configuration details](./configure.html)

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
  [Configuration details](./configureaa.html)

````
	{tomcat-dir}/webapps/ambit2/WEB-INF/classes/ambit2/rest/config/config.prop
````	

>IMPORTANT: You must restart Tomcat to make sure that the configuration	changes you've made become effective.

>Note: Importing IUCLID5 files may require adjusting the PermGen setting in Java 7, see [this guide](./images/config/TomcatJVMConfig_win.pdf)


#User guide

[Quick user guide](./usage.html)	 