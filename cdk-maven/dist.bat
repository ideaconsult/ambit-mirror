@echo off
for /f %%a IN ('dir /b ..\cdk-src+libs-1.4.10\dist\jar\*.jar') do call copyjar.bat %%a