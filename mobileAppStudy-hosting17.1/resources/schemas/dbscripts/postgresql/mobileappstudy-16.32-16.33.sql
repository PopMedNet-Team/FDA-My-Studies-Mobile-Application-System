/*
 * Copyright (c) 2016-2018 LabKey Corporation. All rights reserved. No portion of this work may be reproduced in
 * any form or by any electronic or mechanical means without written permission from LabKey Corporation.
 */
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