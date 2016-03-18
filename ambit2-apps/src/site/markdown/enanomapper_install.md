#[eNanoMapper database](./enanomapper.html) 

## Download 

[**Docker image**](http://projects.bigcat.unimaas.nl/download/enanomapper.tar) A ready to use AMBIT-eNanoMapper instance, running on Tomcat + MySQL stack *[hosted by BiGCaT/UM, NL](http://www.bigcat.unimaas.nl/)*

[**eNanoMapper database web application **](https://sourceforge.net/projects/ambit/files/Ambit2/AMBIT%20REST%20web%20services/custom%20releases/enanomapper)

## Install Guide 

###Requirements

* Java Runtime Environment ([JRE](http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html)) . Install Java 7 Standard Edition or newer.

* Web server. Install [Apache Tomcat](http://tomcat.apache.org/download-70.cgi) 7.x or newer.

* Database server. Install [MySQL 5.6.5](https://dev.mysql.com/doc/relnotes/mysql/5.6/en/) or newer

### Web services installation

####Database

- Create the chemical structures database and set up the associated grants. You will need the [create_tables.sql](https://svn.code.sf.net/p/ambit/code/trunk/ambit2-all/ambit2-db/src/main/resources/ambit2/db/sql/create_tables.sql) script.

- Run the following commands in the MySQL console.  [database create and grant access rights script](txt/enmgrants.sql)

- Verify the current version of the enmrelease database. Run the following commands in the MySQL console.  

````
use enmrelease;
select * from version;
````

The version can be also verified at http://localhost:8080/enanomapper/admin/database

####Deployment

- Deploy the [enanomapper.war](https://sourceforge.net/projects/ambit/files/Ambit2/AMBIT%20REST%20web%20services/custom%20releases/enanomapper) in your Tomcat instance.

- Open the following URI in your browser, replacing YOURHOST and YOURPORT with the relevant values from your configuration.

````
http://YOURHOST:YOURPORT/enanomapper/dataset
````

- The browser should then display the dataset page (empty).
	
Please note that modern web browsers (e.g. Firefox 10, Chrome, Internet Explorer 10) that comply more strictly with current web standards are highly recommended and will provide better overall performance. System's usability might be impaired to some degree when using older browsers.

##### Customize the configuration settings for the eNanoMapper web application.

>IMPORTANT: This step is required only if your setup differs from the default values.

- Open and edit the following file, replacing the {tomcat-dir} with the relevant value from your configuration.

````
{tomcat-dir}/webapps/enanomapper/WEB-INF/classes/ambit2/rest/config/ambit2.pref
````

- The following option specifies the MySQL database configuration. Modify only if your setup differs from the default.

>IMPORTANT: The guide assumes the user should have grants set as per database installation step above

````
	#MySQL Database config
	Host=localhost
	Scheme=jdbc\:mysql
	Port=3306
	Database=enmrelease
	User=guest
	Password=guest
````

#####Customize the Web application authentication and authorization

- Open and edit the following file, replacing the {tomcat-dir} with the relevant value from your configuration [Configuration details](./configureaa.html).

````
	{tomcat-dir}/webapps/enanomapper/WEB-INF/classes/ambit2/rest/config/config.prop
````	

>IMPORTANT: You must restart Tomcat to make sure that the configuration	changes you've made become effective.

#User guide

[Quick user guide](./enanomapper_usage.html)	 