# FDA My Studies User Reg WS

This project consists of APIs that required for MyStudies User registration.This project is developed in Spring MVC framework on Labkey environment.

## Getting Started

This project built on Labkey environment and to start this project you need to set up the labkey 
Development machine. Below link will help you to set up the labkey development machine 

https://www.labkey.org/Documentation/wiki-page.view?name=devMachine

Once the Labkey development environment set clone git repositories such as fdahpUserRegWS into the /server/customModules folder.

Clone git repositories such as compliance into the /server/optionalModules folder

git clone https://github.com/LabKey/compliance.git

Switch to the release17.1 branch and git pull

### Build

Once the setup is done you should be able to build the distribution with these two commands 

ant clean

ant dist -Dname=Registration

Once the build is done find the distribution file in the blow folder path

{LABKEY_HOME}/server/dist/HPHC-Reg

LBAKEY_HOME is the root folder where you cloned the labkey code

For ex:C:\dev\labkey

## Deployment

Move the above distribution file into your tomcat webapps folder and unzip the folder and restart the you server and test with below link

## Test URL
http://localhost:8080/labkey/fdahpUserRegWS/ping.api




