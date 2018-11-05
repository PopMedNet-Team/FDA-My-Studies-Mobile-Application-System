/*
 * Copyright (c) 2016-2018 LabKey Corporation. All rights reserved. No portion of this work may be reproduced in
 * any form or by any electronic or mechanical means without written permission from LabKey Corporation.
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
