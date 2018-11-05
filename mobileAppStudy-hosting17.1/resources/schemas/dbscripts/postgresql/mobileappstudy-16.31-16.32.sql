/*
 * Copyright (c) 2016-2018 LabKey Corporation. All rights reserved. No portion of this work may be reproduced in
 * any form or by any electronic or mechanical means without written permission from LabKey Corporation.
 */
--Change the type of Status column to better support using an EnumTableInfo
ALTER TABLE mobileappstudy.response DROP COLUMN status;
ALTER TABLE mobileappstudy.response ADD COLUMN status integer;

--All existing values are 'Pending'
UPDATE mobileappstudy.response SET status = 0;