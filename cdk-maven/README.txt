This is a hack to upload cdk-*.jar (The Chemistry Development Kit) into Maven repository.

The CDK site where all credits should go is http://cdk.sourceforge.net/

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