
Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors. 
Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction,including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial
portions of the Software.

Funding Source: Food and Drug Administration (“Funding Agency”) effective 18 September 2014 as
Contract no. HHSF22320140030I/HHSF22301006T (the “Prime Contract”).

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED,
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR
PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT
OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
OTHER DEALINGS IN THE SOFTWARE.


# FDA-User Reg WS

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




