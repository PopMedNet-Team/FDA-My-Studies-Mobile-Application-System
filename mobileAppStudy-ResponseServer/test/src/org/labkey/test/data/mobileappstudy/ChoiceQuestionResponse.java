/*
 * Copyright (c) 2016-2019 LabKey Corporation
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
package org.labkey.test.data.mobileappstudy;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.stream.Collectors;

/**
 * Created by iansigmon on 11/28/16.
 */

/**
 * Test helper class to handle MobileAppStudy multi-select survey questions
 */
public class ChoiceQuestionResponse extends QuestionResponse
{
    public ChoiceQuestionResponse(String questionId, Date start, Date end, boolean skipped, String...values)
    {
        super(SupportedResultType.CHOICE, questionId, start, end, skipped, values != null ? Arrays.asList(values) : Collections.emptyList());
    }

    @Override
    public String getResultJsonString()
    {
        return String.format(getType().getFormatString(),
            String.join(",\n",
                    ((Collection<String>)getResult()).stream().map((response)-> "\"" + response + "\"").collect(Collectors.toList())
            )
        );
    }
}
