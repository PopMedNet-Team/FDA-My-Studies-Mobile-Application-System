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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

/**
 * Created by iansigmon on 11/30/16.
 */
public abstract class Form
{
    final Collection<QuestionResponse> responses = new ArrayList<>();
    public void addResponse(QuestionResponse response) {
        responses.add(response);
    }

    public QuestionResponse addBooleanResponse(String questionId, boolean value)
    {
        QuestionResponse qr = new QuestionResponse(AbstractQuestionResponse.SupportedResultType.BOOL, questionId, new Date(), new Date(), false, value);
        responses.add(qr);
        return qr;
    }

    public ChoiceQuestionResponse addChoiceResponse(String questionId, String...values)
    {
        ChoiceQuestionResponse cqr = new ChoiceQuestionResponse(questionId, new Date(), new Date(), false, values);
        responses.add(cqr);
        return cqr;
    }

    public QuestionResponse addDateResponse(String questionId, Date value)
    {
        QuestionResponse qr = new QuestionResponse(AbstractQuestionResponse.SupportedResultType.DATE, questionId, new Date(), new Date(), false, value);
        responses.add(qr);
        return qr;
    }

    public GroupedQuestionResponse addGroupedResponse(String questionId, QuestionResult...values)
    {
        GroupedQuestionResponse gqr = new GroupedQuestionResponse(questionId, new Date(), new Date(), false, values);
        responses.add(gqr);
        return gqr;
    }

    public QuestionResponse addNumberResponse(String questionId, double value)
    {
        QuestionResponse qr = new QuestionResponse(AbstractQuestionResponse.SupportedResultType.NUMERIC, questionId, new Date(), new Date(), false, value);
        responses.add(qr);
        return qr;
    }

    public QuestionResponse addScaleResponse(String questionId, int value)
    {
        QuestionResponse qr = new QuestionResponse(AbstractQuestionResponse.SupportedResultType.SCALE, questionId, new Date(), new Date(), false, value);
        responses.add(qr);
        return qr;
    }

    public QuestionResponse addTextResponse(String questionId, String value)
    {
        QuestionResponse qr = new QuestionResponse(AbstractQuestionResponse.SupportedResultType.TEXT, questionId, new Date(), new Date(), false, value);
        responses.add(qr);
        return qr;
    }

}
