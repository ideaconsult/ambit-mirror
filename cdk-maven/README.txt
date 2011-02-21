This is a hack to upload cdk-*.jar (The Chemistry Development Kit) into Maven repository.

The CDK site where all credits should go is http://cdk.sourceforge.net/

Feb 21, 2011  - updated to use cdk 1.3.8.jar as separate jars.  
The project was converted to multimodule maven project, each module is for one cdk jar.
The jars are expected in dist subfolders of each module project, in the form of cdk-{module}-{version}.jar

April 02, 2010  - updated to use cdk-1.3.4.jar , released on 2010/03/31

Download  cdk-1.3.4.jar and copy into dist folder, then run mvn deploy


This makes the cdk-1.3.4.jar accessible from  http://ambit.sourceforge.net/jars/org/openscience/cdk/cdk/1.3.4/

To use in maven projects, one need to configure dependencies and repositories in pom.xml

...
<dependencies>
    <dependency>
    	<groupId>org.openscience.cdk</groupId>
    	<artifactId>cdk</artifactId>
    	<version>1.3.4</version>
    </dependency>
...
</dependencies>
...

and repositories

...
<repositories>
  	<repository>
        <id>website_ambit</id>
  		<url>http://ambit.sourceforge.net/jars</url>
  	</repository>
</repositories>


(The repository should be the same as the repository where the CDK jar is deployed) 