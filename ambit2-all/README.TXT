AMBIT2 

This is multi module maven project;

Prerequisites for building AMBIT2
JDK 1.5 or higher
MySQL 5.0 or higher
Maven

1) to build ambit2 libraries:  


ambit2-all>mvn package

2) to build applications (AMbitXT and REST web services):

>cd ambit2-apps
ambit2-all\ambit2-apps>mvn package

NOTE 1: Applications depend on Toxtree jars being in local maven repository http://toxtree.sourceforge.net .
Get the source from https://toxtree.svn.sourceforge.net/svnroot/toxtree/trunk and run mvn install.


NOTE 2: The build process includes mandatory database tests and can take a while. 
To be able to access mysql database, there should be "settings.xml" file in your ".m2" directory with the following configuration:


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
	<ambit.db>ambit2</ambit.db>
	<ambit.db.user.root.password>mysql-root-password</ambit.db.user.root.password>
        <ambit.db.user.test>mysql-user-name</ambit.db.user.test>
        <ambit.db.user.test.password>mysq-user-password</ambit.db.user.test.password>
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