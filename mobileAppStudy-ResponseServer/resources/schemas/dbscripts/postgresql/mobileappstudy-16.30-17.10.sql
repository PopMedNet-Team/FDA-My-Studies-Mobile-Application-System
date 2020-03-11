/*
 * Copyright (c) 2017-2019 LabKey Corporation
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

/* mobileappstudy-16.30-16.31.sql */

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

/* mobileappstudy-16.31-16.32.sql */

--Change the type of Status column to better support using an EnumTableInfo
ALTER TABLE mobileappstudy.response DROP COLUMN status;
ALTER TABLE mobileappstudy.response ADD COLUMN status integer;

--All existing values are 'Pending'
UPDATE mobileappstudy.response SET status = 0;

/* mobileappstudy-16.32-16.33.sql */

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

/* mobileappstudy-16.33-16.34.sql */

ALTER TABLE mobileappstudy.ResponseMetadata RENAME QuestionId To FieldName;

/* mobileappstudy-16.34-16.35.sql */

ALTER TABLE mobileappstudy.participant
  ADD COLUMN "status" integer;

UPDATE mobileappstudy.participant set "status" = 0;

ALTER TABLE mobileappstudy.responsemetadata
  ADD COLUMN "participantid" integer;

CREATE INDEX IX_responsemetadata_participantid ON mobileappstudy.responsemetadata ("participantid");

/* mobileappstudy-16.35-16.36.sql */

ALTER TABLE mobileappstudy.study DROP CONSTRAINT uq_study_shortname;

/* mobileappstudy-16.36-16.37.sql */

ALTER TABLE mobileappstudy.response
  RENAME COLUMN response TO data;

ALTER TABLE mobileappstudy.response
  RENAME COLUMN surveyid TO activityid;

ALTER TABLE mobileappstudy.responsemetadata
  RENAME COLUMN surveyid TO activityid;