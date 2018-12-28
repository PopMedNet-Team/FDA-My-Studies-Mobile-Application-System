# FDA MyStudies User Registration Web Services

This project consists of APIs that are required for MyStudies User Registration. 
Developed in Spring MVC, on the Labkey environment.

## Getting Started

### Setting up a local LabKey Environment
This project is intended for deployment to a LabKey environment, and to start this project you need 
to set up a local LabKey Development environment. The following link will help you to set this up.

https://www.labkey.org/Documentation/wiki-page.view?name=devMachine

### Clone Custom Module Dependencies
Once the LabKey development environment set clone git repositories such as "fdahpUserRegWS" into 
the `/server/customModules` folder.

### Clone Optional Module Dependencies
Clone git repositories such as "compliance" into the `/server/optionalModules` folder

`git clone https://github.com/LabKey/compliance.git`

Switch to the `release17.1` branch and git pull


## Build

The distribution can be built by executing the following two commands:

- `ant clean`
- `ant dist -Dname=Registration`

When the execution is complete, you can find the distribution file in the following folder path.
`{LABKEY_HOME}/server/dist/HPHC-Reg`

`LBAKEY_HOME` is the root folder where you cloned the LabKey code.

For example, `C:\dev\labkey`

## Deployment

1) Copy the built distribution file into your Tomcat `webapps` directory 
2) Unzip the folder
3) Restart the you server

Your deployment is now accessible at the following link (Assuming the default Tomcat port is configured). 
`http://localhost:8080/labkey/fdahpUserRegWS/ping.api`
