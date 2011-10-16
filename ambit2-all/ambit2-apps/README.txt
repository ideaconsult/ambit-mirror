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


