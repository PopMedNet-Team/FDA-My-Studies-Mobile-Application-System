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