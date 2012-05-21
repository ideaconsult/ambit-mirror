This is a hack to upload cdk-*.jar (The Chemistry Development Kit) into Maven repository.

The CDK site where all credits should go is http://cdk.sourceforge.net/

CHANGE LOG

May 21, 2012
Updated to cdk 1.4.10 and cdk-jchempaint-26
http://sourceforge.net/projects/cdk/files/cdk/1.4.10/
http://sourceforge.net/projects/cdk/files/CDK-JChemPaint/26/

------

Apr 5, 2012
Updated to cdk 1.4.9
http://sourceforge.net/projects/cdk/files/cdk/1.4.9/
http://sourceforge.net/projects/cdk/files/CDK-JChemPaint/25/
------
Mar 25, 2012
Updated to cdk 1.4.8 & cdk-jchempaint-25
http://sourceforge.net/projects/cdk/files/cdk/1.4.8/
http://sourceforge.net/projects/cdk/files/CDK-JChemPaint/25/

------
Oct 31, 2011
Updated to cdk 1.4.5 
http://sourceforge.net/projects/cdk/files/cdk/1.4.5/
http://sourceforge.net/projects/cdk/files/CDK-JChemPaint/18/
------
Feb 21, 2011  - updated to use cdk 1.3.8.jar as separate jars.  
Includes cdk-jchempaint 18.
http://sourceforge.net/projects/cdk/files/cdk%20%28development%29/1.3.8/

The project was converted to multimodule maven project, each module is for one cdk jar.
The jars are expected in dist subfolders of each module project, in the form of cdk-{module}-{version}.jar


To use in maven projects, use (replace module names as necessary): 

        <dependency>
        	<groupId>org.openscience.cdk</groupId>
        	<artifactId>cdk-standard</artifactId>
        	<version>1.3.8</version>
        </dependency>

and repository:

  	<repository>
        <id>ambit-plovdiv</id>
        <url>http://ambit.uni-plovdiv.bg:8083/nexus/content/repositories/thirdparty</url>
        <snapshots>
  	        <enabled>false</enabled>
  		</snapshots>
        </repository>

Maven dependencies are transitive, so cdk-standard includes cdk-core, cdk-core includes cdk-interfaces and cdk-annotation. 
There should be no need to explicitly include e.g. cdk-interfaces.

August 09, 2010 - repository changed to 

http://ambit.uni-plovdiv.bg:8083/nexus/content/repositories/thirdparty 

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