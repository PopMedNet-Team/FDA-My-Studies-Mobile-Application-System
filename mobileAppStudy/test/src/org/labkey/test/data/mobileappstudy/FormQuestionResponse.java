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

import java.util.stream.Collectors;

/**
 * A metadata-less collection of QuestionResponses
 */
public class FormQuestionResponse extends Form implements QuestionResult
{
    /**
     * Returns: Stringified Json array of contained QuestionResponses w/o any additional metadata
     */
    @Override
    public String getJsonString()
    {
        return String.format("[\n" +
                String.join(",\n",
                        responses.stream().map(QuestionResult::getJsonString).collect(Collectors.toList())
                ) + "]\n"
        );
    }

    public Object getResult()
    {
        return responses;
    }
}
