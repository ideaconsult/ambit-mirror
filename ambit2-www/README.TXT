AMBIT2 

This is multi module maven project;

Prerequisites for building AMBIT2
JDK 1.5 or higher
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
        <rdf.writer>jena|stax</rdf.writer> 
	<ambit.db>database name, default ambit2</ambit.db>
        <ambit.db.user.root.password>root password</ambit.db.user.root.password>
        <ambit.db.user.test>normal user name</ambit.db.user.test>
        <ambit.db.user.test.password>normal user password</ambit.db.user.test.password>

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


<rdf.writer> property sets usage of Jena or StAX libraries for writing RDF. Jena is stable, while StAX is lot faster, but may be buggy (it's our implementation of writing RDF/XML directly)
Toggle between jena and stax for application/rdf+xml mime type is controlled via
1)<rdf.writer>jena|stax</rdf.writer> in .m2/settings.xml (compile time)
2)HTTP query parameter ?rdfwriter=jena|stax  (run time, has priority over 1)
3)if none of 1) or 2) is specified, the default is jena
