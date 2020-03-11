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
