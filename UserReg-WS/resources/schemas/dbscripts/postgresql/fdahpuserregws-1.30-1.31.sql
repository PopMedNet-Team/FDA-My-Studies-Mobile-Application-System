alter TABLE fdahpUserRegWS.AppPropertiesDetails ALTER COLUMN AndroidServerKey TYPE varchar(500);

ALTER TABLE fdahpuserregws.AppPropertiesDetails ADD COLUMN email VARCHAR(50) NULL DEFAULT NULL;

ALTER TABLE fdahpuserregws.AppPropertiesDetails ADD COLUMN emailPassword VARCHAR(50) NULL DEFAULT NULL;

ALTER TABLE fdahpuserregws.AppPropertiesDetails ADD COLUMN regEmailSub VARCHAR(255) NULL DEFAULT NULL;

ALTER TABLE fdahpuserregws.AppPropertiesDetails ADD COLUMN regEmailBody VARCHAR(10000) NULL DEFAULT NULL;

ALTER TABLE fdahpuserregws.AppPropertiesDetails ADD COLUMN forgotEmailSub VARCHAR(255) NULL DEFAULT NULL;

ALTER TABLE fdahpuserregws.AppPropertiesDetails ADD COLUMN forgotEmailBody VARCHAR(10000) NULL DEFAULT NULL;

--ALTER TABLE fdahpUserRegWS.AppPropertiesDetails ADD COLUMN Container ENTITYID not null;


