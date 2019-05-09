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
--Change the type of Status column to better support using an EnumTableInfo
ALTER TABLE mobileappstudy.response DROP COLUMN status;
ALTER TABLE mobileappstudy.response ADD COLUMN status integer;

--All existing values are 'Pending'
UPDATE mobileappstudy.response SET status = 0;