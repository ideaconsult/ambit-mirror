Small library to access OpenTox authorization and authentication services.


This is a maven project. The following maven profile might be configured.

<settings xmlns="http://maven.apache.org/POM/4.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="http://maven.apache.org/POM/4.0.0
                      http://maven.apache.org/xsd/settings-1.0.0.xsd">

  <profiles>
   <profile>
      <id>opentox-opensso</id>
      <activation>
        <activeByDefault>true</activeByDefault>
      </activation>
      <properties>
         <!-- Configuration of OpenTox authentication service -->
		<aa.opensso>http://opensso.in-silico.ch/opensso/identity</aa.opensso>
		<!-- Configuration of OpenTox authorization service -->
        <aa.policy>http://opensso.in-silico.ch/Pol/opensso-pol</aa.policy>
		<!-- User credentials for junit tests -->
        <aa.user>YOUR-USER-FOR-TESTING</aa.user>
        <aa.pass>YOUR-PASS-FOR-TESTING</aa.pass>
      </properties>
    </profile>
  </profiles>
</settings>
