@echo off
rem take the jar file name, e.g. "cdk-core.jar" or "cdk-core-sources.jar"
set str=%1
rem remove the .jar extension,  result is "cdk-core" or "cdk-core-sources"
set str=%str:.jar=%
rem remove the "-sources" string , e.g. "cdk-core-sources" result is "cdk-core". This is the target folder
set dir=%str:-sources=%
rem construct the target file name, result is e.g. "cdk-core\dist\cdk-core-1.4.16.jar" or "cdk-core\dist\cdk-core-sources-1.4.16.jar"
set str="%dir%\dist\%str%-1.4.16.jar
rem reverse version and "-sources" to match Maven format; e.g. "cdk-core\dist\cdk-core-1.4.16-sources.jar"
set str=%str:-sources-1.4.16.jar=-1.4.16-sources.jar%
rem echo.%str%
rem copy the file
@echo on
del "%str%"
copy "..\cdk-src+libs-1.4.16\dist\jar\%1" "%str%"