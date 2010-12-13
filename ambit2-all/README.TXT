http://ambit.sourceforge.net

AMBIT 2.2.0

Changelog: 
cdk 1.3.7
DB schema modified
 
AMBIT 2.1.4

Changelog: cdk 1.3.6 and jchempaint 3.1.3

This is multi module maven project;

Prerequisites for building AMBIT2
JDK 1.6 or higher
MySQL 5.0 or higher
Maven

1) to build ambit2 libraries:  


ambit2-all>mvn package

2) to build applications - AMbitXT (standalone) and REST web services (war):

>cd ambit2-apps
ambit2-all\ambit2-apps>mvn package

NOTE 1: Applications build depend on Toxtree jars. All ambit and Toxtree artifacts are available at

http://ambit.uni-plovdiv.bg:8083/nexus/content/repositories/thirdparty

Optionally, to build Toxtree version on your , get the source from https://toxtree.svn.sourceforge.net/svnroot/toxtree/trunk and run mvn install.


NOTE 2: The build process includes mandatory database tests and can take a while. 
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
	<google.analytics>your-google-analytics-key</google.analytics>
	 <!-- Write RDF via jena or stax Could be altered via ?rdfwriter=jena|stax query paramater -->
         <rdf.writer>stax</rdf.writer>

	<ambit.db>database name, default ambit2</ambit.db>
        <ambit.db.user.root.password>root password</ambit.db.user.root.password>
        <ambit.db.user.test>normal user name</ambit.db.user.test>
        <ambit.db.user.test.password>normal user password</ambit.db.user.test.password>
	<!-- Default ontology service -->
         <!-- AA -->
	<service.ontology>http://ambit.uni-plovdiv.bg:8082/ontology</service.ontology>
	 <aa.opensso>http://opensso.in-silico.ch/opensso/identity</aa.opensso>
        <aa.policy>http://opensso.in-silico.ch/Pol/opensso-pol</aa.policy>

      </properties>
    </profile>
  </profiles>
</settings>


Google analytics for ambit2-www AmbitApplication:
To switch on G.A., insert the google analytics tag into .m2/settings.xml 
<properties>
...
  <google.analytics>UA-XXX</google.analytics> 
...
</properties>

