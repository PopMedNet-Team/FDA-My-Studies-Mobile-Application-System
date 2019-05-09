/*
 * Copyright (c) 2015 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

-- Create schema, tables, indexes, and constraints used for FdahpUserRegWS module here
-- All SQL VIEW definitions should be created in fdahpuserregws-create.sql and dropped in fdahpuserregws-drop.sql
CREATE SCHEMA fdahpUserRegWS;

CREATE TABLE fdahpUserRegWS.AuthInfo
(
    _ts TIMESTAMP NOT NULL,
    AuthId SERIAL ,
    ParticipantId  VARCHAR(50) NULL,
    DeviceToken  VARCHAR(1000) NULL,
    DeviceType  VARCHAR(50) NULL,
    CreatedOn TIMESTAMP NULL,
    ModifiedOn TIMESTAMP NULL,
    AuthKey  VARCHAR(50) NULL,
    IosAppVersion  VARCHAR(50) NULL,
    AndroidAppVersion  VARCHAR(50) NULL,

    CONSTRAINT PK_AuthInfo PRIMARY KEY (AuthId)

);

CREATE TABLE fdahpUserRegWS.ParticipantActivities
(
    _ts TIMESTAMP NOT NULL,
    Id SERIAL,
    ParticipantId VARCHAR(50) NULL,
    StudyId VARCHAR(50) NULL,
    ActivityId VARCHAR(50) NULL,
    ActivityCompleteId Integer NULL,
    ActivityType  VARCHAR(1000) NULL,
    Bookmark  BOOLEAN  NULL,
    Status  VARCHAR(50) NULL,
    ActivityVersion  VARCHAR(50) NULL,
    ActivityState  VARCHAR(50) NULL,
    ActivityRunId VARCHAR(50) NULL,

    CONSTRAINT PK_ParticipantActivities PRIMARY KEY (Id)

);

CREATE TABLE fdahpUserRegWS.UserDetails
(
    _ts TIMESTAMP NOT NULL,
    Id SERIAL ,
    FirstName  VARCHAR(100) NULL,
    LastName  VARCHAR(100) NULL,
    Email  VARCHAR(100) NULL,
    UsePasscode  BOOLEAN NULL,
    TouchId BOOLEAN NULL,
    LocalNotificationFlag BOOLEAN NULL,
    RemoteNotificationFlag BOOLEAN NULL,
    Status INT NULL,
    Password VARCHAR(100) NULL,
    EntityId ENTITYID NOT NULL,
    ReminderLeadTime VARCHAR(50) NULL,
    SecurityToken VARCHAR(100) NULL,
    UserId VARCHAR(50) NULL,
    TempPassword BOOLEAN NULL,
    Locale VARCHAR(100) NULL,
    ResetPassword VARCHAR(100) NULL,
    VerificationDate TIMESTAMP WITHOUT TIME ZONE,
    TempPasswordDate TIMESTAMP WITHOUT TIME ZONE,
 CONSTRAINT PK_UserDetails PRIMARY KEY (Id)

);
CREATE TABLE fdahpUserRegWS.ParticipantStudies
(
    _ts TIMESTAMP NOT NULL,
    Id SERIAL,
    StudyId VARCHAR(50) NULL,
    ConsentStatus  BOOLEAN  NULL,
    Status  VARCHAR(50) NULL,
    Bookmark  BOOLEAN  NULL,
    Eligbibility  BOOLEAN  NULL,
    ParticipantId  VARCHAR(50) NULL,
    UserId VARCHAR(50) NULL,
    EnrolledDate VARCHAR(50) NULL,
    Sharing TEXT NULL,
    CONSTRAINT PK_ParticipantStudies PRIMARY KEY (Id)

);

CREATE TABLE fdahpUserRegWS.StudyConsent
(
    _ts TIMESTAMP NOT NULL,
    Id SERIAL,
    UserId VARCHAR(50) NULL,
    StudyId VARCHAR(50) NULL,
    Version  VARCHAR(50) NULL,
    Status  VARCHAR(50) NULL,
    Pdf  TEXT NULL,
    CONSTRAINT PK_StudyConsent PRIMARY KEY (Id)

);

CREATE TABLE fdahpUserRegWS.PasswordHistory
(
    _ts TIMESTAMP NOT NULL,
    Id SERIAL,
    UserId  VARCHAR(50) NULL,
    Password VARCHAR(50) NULL,
    Created TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT PK_PasswordHistory PRIMARY KEY (Id)

);

CREATE TABLE fdahpUserRegWS.VersionInfo
(
    Id SERIAL,
    AndroidVersion  VARCHAR(50) NULL,
    IosVersion VARCHAR(50) NULL,
    CONSTRAINT PK_VersionInfo PRIMARY KEY (Id)

);
