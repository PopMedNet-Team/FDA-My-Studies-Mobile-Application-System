/*
 * Copyright (c) 2016-2018 LabKey Corporation. All rights reserved. No portion of this work may be reproduced in
 * any form or by any electronic or mechanical means without written permission from LabKey Corporation.
 */
package org.labkey.test.data.mobileappstudy;

/**
 * Created by iansigmon on 12/1/16.
 */
public class MedForm extends FormQuestionResponse
{
    public enum MedType
    {
        Prescription("rx"),
        OTC("otc"),
        Supplement("supplement");

        MedType(String displayName){
            _displayName = displayName;
        }

        private String _displayName;
        public String getDisplayName()
        {
            return _displayName;
        }
    }

    private static final String
        MED = "medName",
        MED_CONDITION = "medCondition",
        FOLLOWING_INSTRUCTIONS = "FollowingInstructions",
        MED_VS_RX_USE = "medVsRxUse",
        DEVIATION_REASON = "WhyDifferent";

    public QuestionResponse addMed(String...medName)
    {
        return addChoiceResponse(MED, medName);
    }

    public QuestionResponse addMedCondition(String...condition)
    {
        return addChoiceResponse(MED_CONDITION, condition);
    }

    public QuestionResponse addFollowingInstructions(boolean follow)
    {
        return addBooleanResponse(FOLLOWING_INSTRUCTIONS, follow);
    }
    public QuestionResponse addDeviation(String...deviation)
    {
        return addChoiceResponse(MED_VS_RX_USE, deviation);
    }
    public QuestionResponse addDeviationReason(String...reason)
    {
        return addChoiceResponse(DEVIATION_REASON, reason);
    }
}
