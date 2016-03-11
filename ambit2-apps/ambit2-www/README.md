# AMBIT Web services

## Installation 

	http://ambit.sourceforge.net/install_ambitrest.html

## Usage

	http://ambit.sourceforge.net/usage.html

## Downloads

	http://ambit.sourceforge.net/downloads.html
	
## Build

* See http://ambit.sourceforge.net/txt/dbgrants.sql for database grants

Build Web archive (.war) file, configured to run under http, with OpenTox AA

````
>mvn clean buildnumber:create package -P http -P ambit-release -P aa-enabled -P aa-admin-disabled -P license-in-text-column
````

Build Web archive (.war) file, configured to run under https, with OpenTox AA

````
>mvn clean buildnumber:create package -P https -P ambit-release -P aa-enabled -P aa-admin-disabled -P license-in-text-column
````

Build Web archive (.war) file, configured to run under http, without any AA

````
>mvn clean buildnumber:create package -P http -P ambit-release -P aa-disabled -P aa-admin-disabled -P license-in-text-column
````

* Note these settings are configurable after compilation and deployment, see 

http://ambit.sourceforge.net/configureaa.html 
http://ambit.sourceforge.net/configure.html

### Maven profiles

* -P http
The war is configured to run under http

* -P https
The war is configured to run under https

* -P aa-enabled
switches on OpenTox AA authentication and authorisation

* -P aa-disabled
No authentication and authorisation

* -P aadb
Local database users

-P license-in-text-column
Will include license column in text files (CSV , TXT). 

* -P no-jni
Excludes jni-inchi*.jar and jnat-*.jar from ambit2.war 
The excluded files (and dependencies) should be added into {tomcat_home}/shared/lib folder
jnati-core-0.4.jar
jnati-deploy-0.4.jar
jni-inchi-0.8.jar
log4j-1.2.14.jar

Don't forget to enable the shared class loader via {tomcat_home}/conf/catalina.properties 
This enables usage of more than one Ambit instance under the same Tomcat.
Solves http://sourceforge.net/tracker/index.php?func=detail&aid=3215942&group_id=191756&atid=938657

