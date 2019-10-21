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
create schema fdahpUserRegWS;

create table fdahpUserRegWS.AuthInfo
(
    _ts timestamp not null,
    AuthId serial ,
    ParticipantId  varchar(50) null,
    DeviceToken  varchar(1000) null,
    DeviceType  varchar(50) null,
    CreatedOn timestamp null,
    ModifiedOn timestamp null,
    AuthKey  varchar(50) null,
    IosAppVersion  varchar(50) null,
    AndroidAppVersion  varchar(50) null,

    constraint PK_AuthInfo primary key (AuthId)

);

create table fdahpUserRegWS.ParticipantActivities
(
    _ts timestamp not null,
    Id serial,
    ParticipantId varchar(50) null,
    StudyId varchar(50) null,
    ActivityId varchar(50) null,
    ActivityCompleteId integer null,
    ActivityType  varchar(1000) null,
    Bookmark  boolean  null,
    Status  varchar(50) null,
    ActivityVersion  varchar(50) null,
    ActivityState  varchar(50) null,
    ActivityRunId varchar(50) null,

    constraint PK_ParticipantActivities primary key (Id)

);

create table fdahpUserRegWS.UserDetails
(
    _ts timestamp not null,
    Id serial ,
    FirstName  varchar(100) null,
    LastName  varchar(100) null,
    Email  varchar(100) null,
    UsePasscode  boolean null,
    TouchId boolean null,
    LocalNotificationFlag boolean null,
    RemoteNotificationFlag boolean null,
    Status int null,
    Password varchar(100) null,
    EntityId ENTITYID not null,
    ReminderLeadTime varchar(50) null,
    SecurityToken varchar(100) null,
    UserId varchar(50) null,
    TempPassword boolean null,
    Locale varchar(100) null,
    ResetPassword varchar(100) null,
    VerificationDate timestamp without time zone,
    TempPasswordDate timestamp without time zone,
 constraint PK_UserDetails primary key (Id)

);
create table fdahpUserRegWS.ParticipantStudies
(
    _ts timestamp not null,
    Id serial,
    StudyId varchar(50) null,
    ConsentStatus  boolean  null,
    Status  varchar(50) null,
    Bookmark  boolean  null,
    Eligbibility  boolean  null,
    ParticipantId  varchar(50) null,
    UserId varchar(50) null,
    EnrolledDate varchar(50) null,
    Sharing text null,
    constraint PK_ParticipantStudies primary key (Id)

);

create table fdahpUserRegWS.StudyConsent
(
    _ts timestamp not null,
    Id serial,
    UserId varchar(50) null,
    StudyId varchar(50) null,
    Version  varchar(50) null,
    Status  varchar(50) null,
    Pdf  text null,
    constraint PK_StudyConsent primary key (Id)

);

create table fdahpUserRegWS.PasswordHistory
(
    _ts timestamp not null,
    Id serial,
    UserId  varchar(50) null,
    Password varchar(50) null,
    Created timestamp without time zone,
    constraint PK_PasswordHistory primary key (Id)

);
