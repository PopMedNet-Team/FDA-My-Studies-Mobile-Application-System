/*
 * Copyright (c) 2017-2018 LabKey Corporation. All rights reserved. No portion of this work may be reproduced in
 * any form or by any electronic or mechanical means without written permission from LabKey Corporation.
 */
ALTER TABLE mobileappstudy.participant
  ADD COLUMN "status" integer;

UPDATE mobileappstudy.participant set "status" = 0;

ALTER TABLE mobileappstudy.responsemetadata
  ADD COLUMN "participantid" integer;

CREATE INDEX IX_responsemetadata_participantid ON mobileappstudy.responsemetadata ("participantid");