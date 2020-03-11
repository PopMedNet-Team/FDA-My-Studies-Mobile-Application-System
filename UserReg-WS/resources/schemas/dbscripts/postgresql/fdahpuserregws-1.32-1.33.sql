Delete from fdahpUserRegWS.AppPropertiesDetails;
Delete from fdahpUserRegWS.authInfo;
Delete from fdahpUserRegWS.loginAttempts;
Delete from fdahpUserRegWS.ParticipantActivities;
Delete from fdahpUserRegWS.ParticipantStudies;
Delete from fdahpUserRegWS.passwordHistory;
Delete from fdahpUserRegWS.studyConsent;
Delete from fdahpUserRegWS.userAppDetails;
Delete from fdahpUserRegWS.userDetails;
Delete from fdahpUserRegWS.versionInfo;


ALTER TABLE fdahpUserRegWS.AppPropertiesDetails ADD COLUMN Container ENTITYID default null;
ALTER TABLE fdahpUserRegWS.authInfo ADD COLUMN Container ENTITYID default null;
ALTER TABLE fdahpUserRegWS.loginAttempts ADD COLUMN Container ENTITYID default null;
ALTER TABLE fdahpUserRegWS.ParticipantActivities ADD COLUMN Container ENTITYID default null;
ALTER TABLE fdahpUserRegWS.ParticipantStudies ADD COLUMN Container ENTITYID default null;
ALTER TABLE fdahpUserRegWS.passwordHistory ADD COLUMN Container ENTITYID default null;
ALTER TABLE fdahpUserRegWS.studyConsent ADD COLUMN Container ENTITYID default null;
ALTER TABLE fdahpUserRegWS.userAppDetails ADD COLUMN Container ENTITYID default null;
ALTER TABLE fdahpUserRegWS.userDetails ADD COLUMN Container ENTITYID default null;
ALTER TABLE fdahpUserRegWS.versionInfo ADD COLUMN Container ENTITYID default null;
