Build Web archive (.war) file, configured to run under http, with OpenTox AA

>mvn clean buildnumber:create package -P http -P ambit-release -P aa-enabled -P aa-admin-disabled -P license-in-text-column

Build Web archive (.war) file, configured to run under https, with OpenTox AA

>mvn clean buildnumber:create package -P https -P ambit-release -P aa-enabled -P aa-admin-disabled -P license-in-text-column

Build Web archive (.war) file, configured to run under http, without any AA

>mvn clean buildnumber:create package -P http -P ambit-release -P aa-disabled -P aa-admin-disabled -P license-in-text-column


Maven profiles

-P http
The war is configured to run under http
-P https
The war is configured to run under https

-P aa-enabled 
switches on OpenTox AA authentication and authorisation
-P aa-disabled
No authentication and authorisation

-P aa-admin-enabled
switches on OpenTox AA authentication and authorisation for /admin/* resources
-P aa-admin-disabled
/admin/* resources will be publicly accessible

-P license-in-text-column
Will include license column in text files (CSV , TXT). 

-P no-jni
Excludes jni-inchi*.jar and jnat-*.jar from ambit2.war 
The excluded files (and dependencies) should be added into {tomcat_home}/shared/lib folder
jnati-core-0.4.jar
jnati-deploy-0.4.jar
jni-inchi-0.8.jar
log4j-1.2.14.jar

Don't forget to enable the shared class loader via {tomcat_home}/conf/catalina.properties 
This enables usage of more than one Ambit instance under the same Tomcat.
Solves http://sourceforge.net/tracker/index.php?func=detail&aid=3215942&group_id=191756&atid=938657


GRANT ALL ON `ambit2`.* TO 'guest'@'localhost' IDENTIFIED BY 'guest';
GRANT ALL ON `ambit2`.* TO 'guest'@'127.0.0.1' IDENTIFIED BY 'guest';
GRANT ALL ON `ambit2`.* TO 'guest'@'::1' IDENTIFIED BY 'guest';
GRANT TRIGGER ON `ambit2`.* TO 'guest'@'localhost';
GRANT TRIGGER ON `ambit2`.* TO 'guest'@'127.0.0.1';
GRANT TRIGGER ON `ambit2`.* TO 'guest'@'::1';
GRANT EXECUTE ON PROCEDURE `ambit2`.findByProperty TO 'guest'@'localhost';
GRANT EXECUTE ON PROCEDURE `ambit2`.findByProperty TO 'guest'@'127.0.0.1';
GRANT EXECUTE ON PROCEDURE `ambit2`.findByProperty TO 'guest'@'::1';
