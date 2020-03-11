# FDA-User Reg WS

This project consists of APIs that required for MyStudies User registration.This project is developed in Spring MVC framework on Labkey environment.

## Getting Started

This project built on Labkey environment and to start this project you need to set up the labkey 
Development machine. Below link will help you to set up the labkey development machine 

https://www.labkey.org/Documentation/wiki-page.view?name=devMachine

Once the Labkey development environment set clone git repositories such as UserReg-WS into the /server/modules folder.

Switch to the release 2019.10 branch and git pull

In your settings.gradle file, find the commented out line with this text:
//include ":server:optionalModules:workflow"
Underneath this line, add these two lines:(might change if folder structure is changed)
include ":server:modules:UserReg-WS"
include ":server:modules:UserReg-WS:distributions:Registration"

### To generate a local build use the below commands:

Once the setup is done you should be able to build the distribution with this command
•	gradlew cleanBuild deployApp

Once it’s build successfully, click the run icon in your IDE

### Test URL
http://localhost:8080/labkey/fdahpUserRegWS/ping.api

### To generate a production build use the below commands:(might change if folder structure is changed)
•	gradlew deployApp -PdeployMode=prod
•	gradlew -PdeployMode=prod :server:modules:UserReg-WS:distributions:Registration:distribution

Once the build is completed, you will find the distribution file at below path:
{LABKEY_HOME}/dist/Registration
LABKEY_HOME is the root folder where you cloned the labkey code