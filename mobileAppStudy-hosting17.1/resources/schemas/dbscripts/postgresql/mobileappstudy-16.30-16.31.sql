/*
 * Copyright (c) 2016-2018 LabKey Corporation. All rights reserved. No portion of this work may be reproduced in
 * any form or by any electronic or mechanical means without written permission from LabKey Corporation.
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