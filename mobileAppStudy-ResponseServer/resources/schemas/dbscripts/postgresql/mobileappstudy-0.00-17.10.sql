/*
 * Copyright (c) 2019 LabKey Corporation
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

/* mobileappstudy-0.00-16.30.sql */

DROP SCHEMA IF EXISTS mobileappsurvey CASCADE;

CREATE SCHEMA mobileappstudy;

CREATE TABLE mobileappstudy.Study
(
  RowId SERIAL,
  ShortName VARCHAR(32),

  Created TIMESTAMP NOT NULL,
  CreatedBy USERID NOT NULL,
  Modified TIMESTAMP NULL,
  ModifiedBy USERID NULL,
  Container ENTITYID NOT NULL,

  CONSTRAINT PK_Study PRIMARY KEY (RowId),
  CONSTRAINT UQ_Study_ShortName UNIQUE (ShortName)
);

CREATE TABLE mobileappstudy.Participant
(
  RowId SERIAL,
  AppToken ENTITYID,
  StudyId INTEGER NOT NULL,

  Created TIMESTAMP NOT NULL,
  Container ENTITYID NOT NULL,

  CONSTRAINT PK_Participant PRIMARY KEY (RowId),
  CONSTRAINT FK_Participant_Study FOREIGN KEY (StudyId) REFERENCES mobileappstudy.Study (RowId),
  CONSTRAINT UQ_Participant_AppToken_StudyId UNIQUE (AppToken, StudyId)
);

CREATE TABLE mobileappstudy.EnrollmentTokenBatch
(
  RowId SERIAL,
  Count INTEGER NOT NULL,

  Created TIMESTAMP NOT NULL,
  CreatedBy USERID NOT NULL,
  Container ENTITYID NOT NULL,

  CONSTRAINT PK_EnrollmentTokenBatch PRIMARY KEY (RowId)
);


CREATE TABLE mobileappstudy.EnrollmentToken
(
  RowId SERIAL,
  BatchId INTEGER,
  Token VARCHAR(9) NOT NULL,
  ParticipantId INTEGER,

  Created TIMESTAMP NOT NULL,
  CreatedBy USERID NOT NULL,
  Container ENTITYID NOT NULL,

  CONSTRAINT PK_EnrollmentToken PRIMARY KEY (RowId),
  CONSTRAINT FK_EnrollmentToken_EnrollmentTokenBatch FOREIGN KEY (BatchId) REFERENCES mobileappstudy.EnrollmentTokenBatch (RowId),
  CONSTRAINT FK_EnrollmentToken_Participant FOREIGN KEY (ParticipantId) REFERENCES mobileappstudy.Participant (RowId),
  CONSTRAINT UQ_EnrollmentToken_Token UNIQUE(Token)
);

/* mobileappstudy-16.30-17.10.sql */

-- Add Table: mobileappstudy.Responses
CREATE TABLE mobileappstudy.Response
(
  RowId SERIAL NOT NULL,
  Response TEXT NOT NULL,
  ParticipantId INTEGER NOT NULL,
  AppToken ENTITYID NOT NULL,
  SurveyVersion VARCHAR(5) NOT NULL,
  SurveyId VARCHAR(200) NOT NULL,
  Status INTEGER,
  Processed TIMESTAMP,
  ProcessedBy USERID,
  ErrorMessage VARCHAR(1000),
  Container ENTITYID NOT NULL,
  Created TIMESTAMP NOT NULL,
  CreatedBy USERID,

  CONSTRAINT PK_Response PRIMARY KEY (RowId),
  CONSTRAINT FK_Response_Participant FOREIGN KEY (participantid)
    REFERENCES mobileappstudy.participant (rowid)
);

CREATE INDEX IX_Response_Participant
  ON mobileappstudy.response
  USING btree
  (participantid);


-- Add Column: mobileappstudy.study.CollectionEnabled
ALTER TABLE mobileappstudy.study ADD COLUMN CollectionEnabled BOOLEAN;
UPDATE mobileappstudy.study SET CollectionEnabled = FALSE;
ALTER TABLE mobileappstudy.study ALTER COLUMN CollectionEnabled SET NOT NULL;
ALTER TABLE mobileappstudy.study ALTER COLUMN CollectionEnabled SET DEFAULT FALSE;

--Change the type of Status column to better support using an EnumTableInfo
ALTER TABLE mobileappstudy.response DROP COLUMN status;
ALTER TABLE mobileappstudy.response ADD COLUMN status integer;

--All existing values are 'Pending'
UPDATE mobileappstudy.response SET status = 0;

CREATE TABLE mobileappstudy.ResponseMetadata
(
  RowId SERIAL NOT NULL,
  Container ENTITYID NOT NULL,
  ListName VARCHAR(64) NOT NULL,
  SurveyId INTEGER NOT NULL,
  QuestionId VARCHAR(64),
  StartTime TIMESTAMP,
  EndTime TIMESTAMP,
  Skipped BOOLEAN,
  Created TIMESTAMP NOT NULL,
  CONSTRAINT PK_ResponseMetadata PRIMARY KEY (RowId)
);

ALTER TABLE mobileappstudy.ResponseMetadata RENAME QuestionId To FieldName;

ALTER TABLE mobileappstudy.participant
  ADD COLUMN "status" integer;

UPDATE mobileappstudy.participant set "status" = 0;

ALTER TABLE mobileappstudy.responsemetadata
  ADD COLUMN "participantid" integer;

CREATE INDEX IX_responsemetadata_participantid ON mobileappstudy.responsemetadata ("participantid");

ALTER TABLE mobileappstudy.study DROP CONSTRAINT uq_study_shortname;

ALTER TABLE mobileappstudy.response
  RENAME COLUMN response TO data;

ALTER TABLE mobileappstudy.response
  RENAME COLUMN surveyid TO activityid;

ALTER TABLE mobileappstudy.responsemetadata
  RENAME COLUMN surveyid TO activityid;