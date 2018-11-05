/*
 * Copyright (c) 2016-2018 LabKey Corporation. All rights reserved. No portion of this work may be reproduced in
 * any form or by any electronic or mechanical means without written permission from LabKey Corporation.
 */
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
