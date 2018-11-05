/*
 * Copyright (c) 2017-2018 LabKey Corporation. All rights reserved. No portion of this work may be reproduced in
 * any form or by any electronic or mechanical means without written permission from LabKey Corporation.
 */
package org.labkey.mobileappstudy.surveydesign;

import org.labkey.api.data.Container;

/**
 * Created by iansigmon on 2/2/17.
 */
public interface SurveyDesignProvider
{
    SurveyDesign getSurveyDesign(Container c, String shortName, String activityId, String version) throws Exception;
}
