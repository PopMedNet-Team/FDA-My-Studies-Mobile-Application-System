/*
 * Copyright (c) 2017-2018 LabKey Corporation. All rights reserved. No portion of this work may be reproduced in
 * any form or by any electronic or mechanical means without written permission from LabKey Corporation.
 */
ALTER TABLE mobileappstudy.response
  RENAME COLUMN response TO data;

ALTER TABLE mobileappstudy.response
  RENAME COLUMN surveyid TO activityid;

ALTER TABLE mobileappstudy.responsemetadata
  RENAME COLUMN surveyid TO activityid;
