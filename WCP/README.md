# FDA Health Study Web Configuration Portal
Project description goes here

## Getting Started

These instructions will get you a copy of the project up and running on your local machine for development and testing purposes. See deployment for notes on how to deploy the project on a live system.

### Prerequisites

1. Java 8

    Tomcat requires Java to be installed on the server so that any Java web application code can be executed. Below URL covers basic installation of the Java Development Kit package.
        
    [Java Install](https://www.digitalocean.com/community/tutorials/how-to-install-apache-tomcat-8-on-ubuntu-16-04#step-1-install-java)

2. Tomcat 8

    Tomcat is an open source implementation of the Java Servlet and Java Server Pages technologies, released by the Apache Software Foundation. Below URL covers the basic installation and some configuration of Tomcat 8 on your Ubuntu server.
    
    [Tomcat Install](https://www.digitalocean.com/community/tutorials/how-to-install-apache-tomcat-8-on-ubuntu-16-04#step-3-install-tomcat)

3. MySQL 5.7

    Below URL covers basic installation of MySQL server and securing MySQL
    
    [MySQL Install](https://www.digitalocean.com/community/tutorials/how-to-install-linux-apache-mysql-php-lamp-stack-on-ubuntu-16-04#step-2-install-mysql)
4. Maven 

    Maven is open source build life cycle management tool by Apache Software Foundation. This tool is required to generate server deployable build (war) from the project. Below URL covers the basic installation and some configuration of Tomcat 8 on your Ubuntu server.
    
    [Maven Install](https://maven.apache.org/index.html)

### Installing

#### Project settings configuration
The `messageResource.properties` file can be find at `wcp\fdahpStudyDesigner\src\main\resources\messageResource.properties` path where following configuration can be customized. 

```properties
max.login.attempts=3                        
#Maximum continuous fail login attempts by a user.

password.resetLink.expiration.in.hour=48    
#Reset password link will get expired after the specified hours.

password.expiration.in.day=90               
#User password expiration in days.

lastlogin.expiration.in.day=90              
#User will get locked if he has not logged in for specified days.

password.history.count=10                   
#User cannot reuse the last 10 generated passwords for change password.

user.lock.duration.in.minutes=30            
#User lock duration in minutes after crossed Maximum continuous fail login attempts limit.
```
#### Externalizing common configuration 
An `application.properties` file will be stored into some physical path with following properties settings.

E.g. `c:/fdahphc/application.properties`

```properties
smtp.portvalue=25               
#Should be changed to actual SMTP port

smtp.hostname=xxx.xxx.xxx.xx    
#Should be changed to actual SMTP IP

fda.imgUploadPath=<Tomcat installed path>/webapps/fdaResources/     
#<Tomcat installed path> will be changed to actual path

acceptLinkMail =http://localhost:8080/fdahpStudyDesigner/createPassword.do?securityToken=
login.url=http://localhost:8080/fdahpStudyDesigner/login.do
signUp.url=http://localhost:8080/fdahpStudyDesigner/signUp.do?securityToken=
#For all the above properties “localhost” will be changed to actual IP address or domain name.

db.url=localhost/fda_hphc
db.username=****
db.password=****
#“localhost” will be changed to IP address or domain name, if database is installed on different server. If database is installed on same server, it’s not required to change “db.url”.
#“db.username” value will be changed to actual username of database.
#“db.password” value will be changed to actual password of database.

hibernate.connection.url=jdbc:mysql://localhost/fda_hphc
hibernate.connection.username=****
hibernate.connection.password=****

#“localhost” will be changed to IP address or domain name, if database is installed on different server. If database is on same server, it’s not required to change “hibernate.connection.url”.
#“hibernate.connection.username” value will be changed to actual username of database.
#“hibernate.connection.password” value will be changed to actual password of database.

fda.smd.study.thumbnailPath = http://localhost:8080/fdaResources/studylogo/
fda.smd.study.pagePath = http://localhost:8080/fdaResources/studypages/
fda.smd.resource.pdfPath = http://localhost:8080/fdaResources/studyResources/
fda.smd.questionnaire.image=http://localhost/fdaResources/questionnaire/
#For all the properties “localhost” will be changed to actual IP address or domain name.


#Folder for Audit log files:
#Please create a folder "fdaAuditLogs" inside the server and replace the path "/usr/local/fdaAuditLogs/" with actual path for “fda.logFilePath” property.
#User registration server root URL:

fda.registration.root.url = https://hphci-fdama-te-ur-01.labkey.com/fdahpUserRegWS
#https://hphci-fdama-te-ur-01.labkey.com – Should be replaced with actual URL

```
#### Changes in Tomcat configuration
Context file path will be : <tomcat installed path>/tomcat/conf/context.xml. Add below parameters in context.xml file inside `<context>` tag.

```xml
<Parameter name="property_file_location_prop" value="C://fdahphc/" override="1"/>
<Parameter name="property_file_name" value="application" override="1"/>
<Parameter name="property_file_location_config" value="file:/c:/fdahphc/application.properties" override="1"/>
<Parameter name="property_file_location_path" value="C://fdahphc/application.properties" override="1"/>

```
### Database script execution
`HPHC_My_Studies_DB_Create_Script.sql` file script should be executed in mysql database.

### Build

To build the application the following command should run in project root folder.
```
mvn clean install
```
This command generate a deployable war file in `target` folder named as `fdahpStudyDesigner.war`.

### Deployment
War file deployment
`fdahpStudyDesigner.war` file will deploy to tomcat.
Restart the tomcat

### Test application
After complete your deployment, to verify the application 
Hit the below URL, you should see the landing page of the application for WCP application 
`http://localhost/fdahpStudyDesigner`


## Built With

* [Spring](http://spring.io/) - The web framework used
* [Hibernate](http://hibernate.org/) - The ORM used.
* [Maven](https://maven.apache.org/) - Dependency Management

## Contributing

Please read [CONTRIBUTING.md](https://gist.github.com/PurpleBooth/b24679402957c63ec426) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/your/project/tags). 

## Authors

* **Billie Thompson** - *Initial work* - [PurpleBooth](https://github.com/PurpleBooth)

See also the list of [contributors](https://github.com/your/project/contributors) who participated in this project.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* Hat tip to anyone who's code was used
* Inspiration
* etc

