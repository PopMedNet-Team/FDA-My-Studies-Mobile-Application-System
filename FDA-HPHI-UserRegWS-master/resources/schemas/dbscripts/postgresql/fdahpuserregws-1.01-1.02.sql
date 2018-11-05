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

ALTER TABLE fdahpUserRegWS.participantstudies ADD Completion integer;

ALTER TABLE fdahpUserRegWS.participantstudies ADD Adherence integer;

ALTER TABLE fdahpUserRegWS.participantactivities ADD Total integer;

ALTER TABLE fdahpUserRegWS.participantactivities ADD Completed integer;

ALTER TABLE fdahpUserRegWS.participantactivities ADD Missed integer;

-- Create a temporary TIMESTAMP column
ALTER TABLE fdahpUserRegWS.participantstudies ADD COLUMN EnrolledDateTemp TIMESTAMP without time zone NULL;

-- Copy casted value over to the temporary column
UPDATE fdahpUserRegWS.participantstudies SET EnrolledDateTemp = EnrolledDate::TIMESTAMP;

-- Modify original column using the temporary column
ALTER TABLE fdahpUserRegWS.participantstudies ALTER COLUMN EnrolledDate TYPE TIMESTAMP without time zone USING EnrolledDateTemp;

-- Drop the temporary column (after examining altered column values)
ALTER TABLE fdahpUserRegWS.participantstudies DROP COLUMN EnrolledDateTemp;