
create table fdahpUserRegWS.VersionInfo
(
    Id serial,
    AndroidVersion  varchar(50) null,
    IosVersion varchar(50) null,
    constraint PK_VersionInfo primary key (Id)

);

create table fdahpUserRegWS.UserAppDetails
(
   UserAppId serial,
   UserId  varchar(50) null,
   ApplicationId varchar(50) null,
   OrgId varchar(50) null,
   CreatedOn timestamp null,
   constraint PK_UserAppDetails primary key (UserAppId)

);

ALTER TABLE fdahpuserregws.authinfo ADD COLUMN applicationId VARCHAR(50) NULL DEFAULT NULL;

ALTER TABLE fdahpuserregws.authinfo ADD COLUMN orgId VARCHAR(50) NULL DEFAULT NULL;

ALTER TABLE fdahpuserregws.participantactivities ADD COLUMN applicationId VARCHAR(50) NULL DEFAULT NULL;

ALTER TABLE fdahpuserregws.participantactivities ADD COLUMN orgId VARCHAR(50) NULL DEFAULT NULL;

ALTER TABLE fdahpuserregws.participantstudies ADD COLUMN applicationId VARCHAR(50) NULL DEFAULT NULL;

ALTER TABLE fdahpuserregws.participantstudies ADD COLUMN orgId VARCHAR(50) NULL DEFAULT NULL;

ALTER TABLE fdahpuserregws.studyconsent ADD COLUMN applicationId VARCHAR(50) NULL DEFAULT NULL;

ALTER TABLE fdahpuserregws.studyconsent ADD COLUMN orgId VARCHAR(50) NULL DEFAULT NULL;




 create table fdahpUserRegWS.AppPropertiesDetails
(
   Id serial,
   AppId varchar(50) null,
   OrgId varchar(50) null,
   IosBundleId varchar(50) null,
   AndroidBundleId varchar(50) null,
   IosCertificate varchar(10000) null,
   IosCertificatePassword varchar(50) null,
   AndroidServerKey varchar(50) null,
   CreatedOn timestamp null,
   constraint PK_AppPropertiesDetails primary key (Id)

);

 alter TABLE fdahpUserRegWS.UserDetails ADD ApplicationId varchar(255);
 update fdahpUserRegWS.UserDetails set ApplicationId = null;


 alter TABLE fdahpUserRegWS.UserDetails ADD OrgId varchar(255);
 update fdahpUserRegWS.UserDetails set OrgId = null;

--alter TABLE fdahpUserRegWS.AppPropertiesDetails ALTER COLUMN IosCertificate TYPE varchar(10000);