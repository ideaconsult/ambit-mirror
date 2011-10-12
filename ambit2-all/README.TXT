http://ambit.sourceforge.net

This is multi module maven project.

Prerequisites for building AMBIT2
JDK 1.6 or higher
MySQL 5.0 or higher
Maven

1) to build ambit2 libraries:  


ambit2-all>mvn package

2) to build applications - AMbitXT (standalone) and REST web services (war):

>cd ambit2-apps
ambit2-all\ambit2-apps>mvn package

NOTE 1: The build of applications (ambit2-ambitxt and ambit2-www) depends on Toxtree jars. 
All ambit and Toxtree artifacts are available at

http://ambit.uni-plovdiv.bg:8083/nexus/content/repositories/thirdparty

Optionally, to build Toxtree version on your own, get the source 
from https://toxtree.svn.sourceforge.net/svnroot/toxtree/trunk and run mvn install.


NOTE 2: The build process includes mandatory database tests and may take a while. 
To be able to access mysql database, there should be "settings.xml" file in your ".m2" directory with the following configuration:

Root DB password is used only for creating a new database via dbunit tests , and if a web request to create a new database succeeds.
normal user and password are guest/guest by default and are used in database queries/writes.



<settings xmlns="http://maven.apache.org/POM/4.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                      http://maven.apache.org/xsd/settings-1.0.0.xsd">

  <profiles>
   <profile>
      <id>ambit-build</id>
      <activation>
        <activeByDefault>true</activeByDefault>
      </activation>
      <properties>
	 <!-- Write RDF via jena or stax Could be altered via ?rdfwriter=jena|stax query paramater -->
         <rdf.writer>stax</rdf.writer>

	<!-- the database name, default ambit2  -->
	<ambit.db>ambit2</ambit.db>

	<!-- db root password - for creating the database during tests -->
        <ambit.db.user.root.password></ambit.db.user.root.password>

	<!-- database user password and name -->
        <ambit.db.user.test>guest</ambit.db.user.test>
        <ambit.db.user.test.password>guest</ambit.db.user.test.password>

	 <!-- Allow creating the dtabase via POST  -->
	 <ambit.db.create.allow>true</ambit.db.create.allow>

         <!-- OpenTox AA services-->
	 <aa.opensso>http://opensso.in-silico.ch/opensso/identity</aa.opensso>
        <aa.policy>http://opensso.in-silico.ch/Pol/opensso-pol</aa.policy>

	<!-- 
		Google analytics code for AMBIT REST web services (ambit2-www)
	-->
	<google.analytics>UA-...</google.analytics>

    <!-- Include license as a separate column in CSV and TXT files , default is false. 
    Alternatively, use predefined profiles 
    -P no-license-in-text-column
    -P license-in-text-column
    --> 
    <license.intextfiles>false</license.intextfiles>
      </properties>
    </profile>
 	
    <profile>
      <!-- use this profile via mvn -P aa-enabled to have the REST services protected by OpenTox AA -->
      <id>aa-enabled</id>
      <activation>
        <activeByDefault>false</activeByDefault>
      </activation>
	<properties>
	 <!-- OpenTox AA services-->
        <aa.opensso>http://opensso.in-silico.ch/opensso/identity</aa.opensso>
        <aa.policy>http://opensso.in-silico.ch/Pol/opensso-pol</aa.policy>
	<!-- default user for tests only -->
        <aa.user></aa.user>
        <aa.pass></aa.pass>
	<aa.enabled>true</aa.enabled>
	</properties>
    </profile>
  </profiles>
</settings>

----------------------------------------------------------------------
CHANGELOG

AMBIT2 2.4.0-SNAPSHOT
.r3436
 cdk 1.3.8 and cdk-jchempaint-18 
jchempaint 3.1.3 only in ambit2-jchempaint and AmbitXT
MySQL 5.5

Changelog: 

AMBIT 2.2.0
.r2845
 cdk 1.3.7 and jchempaint 3.1.3

AMBIT 2.1.4

Changelog: cdk 1.3.6 and jchempaint 3.1.3

