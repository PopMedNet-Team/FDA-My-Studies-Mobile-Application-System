/*
 * Copyright (c) 2016-2018 LabKey Corporation. All rights reserved. No portion of this work may be reproduced in
 * any form or by any electronic or mechanical means without written permission from LabKey Corporation.
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
