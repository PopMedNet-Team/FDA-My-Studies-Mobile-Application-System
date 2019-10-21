/*
 * Copyright (c) 2017-2019 LabKey Corporation
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
package org.labkey.mobileappstudy.surveydesign;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.labkey.api.collections.CaseInsensitiveHashMap;
import org.labkey.api.data.JdbcType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by iansigmon on 2/2/17.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class SurveyStep
{
    private static final int VARCHAR_TEXT_BOUNDARY = 4000;

    /**
     * Enum describing the various Step Types
     */
    public enum SurveyStepType
    {
        Form("form"),
        Instruction("instruction"),
        Question("question"),
        Task("task"),
        UNKNOWN(null);

        private static final Map<String, SurveyStepType> stepTypeMap;

        static {
            Map<String, SurveyStepType> map = new HashMap<>();
            for (SurveyStepType resultType : values())
                map.put(resultType.designTypeString, resultType);

            stepTypeMap = Collections.unmodifiableMap(map);
        }

        //String representing this in the design json
        private String designTypeString;

        SurveyStepType(String type)
        {
            designTypeString = type;
        }

        static SurveyStepType getStepType(String key)
        {
            return stepTypeMap.getOrDefault(key, UNKNOWN);
        }
    }

    private enum Style
    {
        Integer("Integer", JdbcType.DOUBLE),
        Double("Decimal", JdbcType.DOUBLE),
        Date("Date", JdbcType.TIMESTAMP),
        Date_Time("Date-Time", JdbcType.TIMESTAMP),
        UNKNOWN(null, null);

        private static final Map<String, Style> styleMap;

        static {
            Map<String, Style> map = new HashMap<>();
            for (Style resultType : values())
                map.put(resultType.designStyleString, resultType);

            styleMap = Collections.unmodifiableMap(map);
        }

        //String representing this in the design json
        private String designStyleString;
        private JdbcType propertyType;

        Style(String key, JdbcType type)
        {
            designStyleString = key;
            propertyType = type;
        }

        static Style getStyle(String key)
        {
            return styleMap.getOrDefault(key, UNKNOWN);
        }
        public JdbcType getPropertyType()
        {
            return propertyType;
        }
    }

    /**
     * Enum describing the various possible result types for any given step
     */
    public enum StepResultType
    {
        Scale("scale", true, JdbcType.DOUBLE),
        ContinuousScale("continuousScale", true, JdbcType.DOUBLE),
        TextScale("textScale", true, JdbcType.VARCHAR),
        ValuePicker ("valuePicker", true, JdbcType.VARCHAR),
        ImageChoice ("imageChoice", true, JdbcType.VARCHAR),
        TextChoice ("textChoice", false, JdbcType.VARCHAR),    // Null in ValueType   //Keep type for value field in Choice list
        GroupedResult("grouped", false, null),
        Boolean ("boolean", true, JdbcType.BOOLEAN),
        Numeric ("numeric", true, JdbcType.DOUBLE) {
            @Override
            public @Nullable JdbcType getPropertyType(SurveyStep step)
            {
                return step.getStyle().getPropertyType();
            }
        },   //  Double in ValueType      //TODO: Per result schema value should always be a double
        TimeOfDay("timeOfDay", true, JdbcType.VARCHAR),
        Date("date", true, JdbcType.TIMESTAMP) {
            @Override
            public @Nullable JdbcType getPropertyType(SurveyStep step)
            {
                return step.getStyle().getPropertyType();
            }
        },     // Timestamp in ValueType          //TODO: Per result schema value should always be the same date format
        Text("text", true, JdbcType.VARCHAR),
        Email ("email", true, JdbcType.VARCHAR),
        TimeInterval ("timeInterval", true, JdbcType.DOUBLE),
        Height("height", true, JdbcType.DOUBLE),
        Location("location", true, JdbcType.VARCHAR),
        FetalKickCounter("fetalKickCounter", false, null) {
            @Override
            public List<SurveyStep> getDataValues()
            {
                List<SurveyStep> dataValues = new ArrayList<>();

                SurveyStep duration = new SurveyStep();
                duration.setKey("duration");
                duration.setResultType(Numeric.resultTypeString);
                duration.setType("taskData");
                duration.setFormat(SurveyStepFormat.getIntegerFormat());
                dataValues.add(duration);

                SurveyStep count = new SurveyStep();
                count.setKey("count");
                count.setResultType(Numeric.resultTypeString);
                count.setFormat(SurveyStepFormat.getIntegerFormat());
                count.setType("taskData");
                dataValues.add(count);
                return dataValues;
            }
        },
        SpatialSpanMemory("spatialSpanMemory", false, null) {
            @Override
            public List<SurveyStep> getDataValues()
            {
                List<SurveyStep> dataValues = new ArrayList<>();

                SurveyStep score = new SurveyStep();
                score.setKey("score");
                score.setType("taskData");
                score.setResultType(Numeric.resultTypeString);
                score.setFormat(SurveyStepFormat.getIntegerFormat());
                dataValues.add(score);

                SurveyStep numGames = new SurveyStep();
                numGames.setKey("numberOfGames");
                numGames.setType("taskData");
                numGames.setResultType(Numeric.resultTypeString);
                numGames.setFormat(SurveyStepFormat.getIntegerFormat());
                dataValues.add(numGames);

                SurveyStep numFailures = new SurveyStep();
                numFailures.setKey("numberOfFailures");
                numFailures.setType("taskData");
                numFailures.setResultType(Numeric.resultTypeString);
                numFailures.setFormat(SurveyStepFormat.getIntegerFormat());
                dataValues.add(numFailures);

                return dataValues;
            }
        },
        TowerOfHanoi("towerOfHanoi", false, null) {
            @Override
            public List<SurveyStep> getDataValues()
            {
                List<SurveyStep> dataValues = new ArrayList<>();

                SurveyStep solved = new SurveyStep();
                solved.setKey("puzzleWasSolved");
                solved.setResultType(Boolean.resultTypeString);
                solved.setType("taskData");
                dataValues.add(solved);

                SurveyStep numMoves = new SurveyStep();
                numMoves.setKey("numberOfMoves");
                numMoves.setResultType(Numeric.resultTypeString);
                numMoves.setFormat(SurveyStepFormat.getIntegerFormat());
                numMoves.setType("taskData");
                dataValues.add(numMoves);

                return dataValues;
            }
        }
        ,

        UNKNOWN("Unknown", true, JdbcType.VARCHAR);

        private static final Map<String, StepResultType> resultTypeMap;

        //String representing this in the design json
        private String resultTypeString;
        private Boolean isSingleValued;
        private JdbcType defaultJdbcType;

        static {
            Map<String, StepResultType> map = new CaseInsensitiveHashMap<>();
            for (StepResultType resultType : values())
                map.put(resultType.resultTypeString, resultType);

            resultTypeMap = Collections.unmodifiableMap(map);
        }


        StepResultType(String key, java.lang.Boolean isSingleValued, JdbcType jdbcType)
        {
            this.isSingleValued = isSingleValued;
            this.defaultJdbcType = jdbcType;
            resultTypeString = key;
        }

        public Boolean isSingleValued()
        {
            return isSingleValued;
        }

        public String getResultTypeString()
        {
            return resultTypeString;
        }

        public static StepResultType getStepResultType(String key)
        {
            return resultTypeMap.getOrDefault(key, UNKNOWN);
        }

        @Nullable
        public List<SurveyStep> getDataValues()
        {
            return null;
        }

        public JdbcType getDefaultJdbcType()
        {
            return defaultJdbcType;
        }

        @Nullable
        public JdbcType getPropertyType(SurveyStep step)
        {
            return getDefaultJdbcType();
        }

    }

    public enum PHIClassification
    {
        PHI("PHI"),
        Limited("Limited"),
        NotPHI("NotPHI");

        private static final Map<String, PHIClassification> CONVERTER;

        private String jsonString;
        static {
            Map<String, PHIClassification> map = new HashMap<>();
            for(PHIClassification val : values())
                map.put(val.jsonString, val);

            CONVERTER = Collections.unmodifiableMap(map);
        }

        PHIClassification(String val)
        {
            jsonString = val;
        }

         public static PHIClassification getClassicication(String key)
         {
             if (StringUtils.isNotBlank(key))
                 return CONVERTER.get(key);
             else
                 //TODO: not sure what default should be, so defaulting to most limited
                 return PHIClassification.PHI;
         }

         public String getJsonValue()
         {
             return jsonString;
         }
    }

    private String type;
    private String resultType;
    private String key;
    private String phi;
    private String title;
    private List<SurveyStep> steps = null;

    private SurveyStepFormat format;

    //Currently ignored since we need to make the field
    private boolean skippable = true;

    //Currently ignored. Only applicable to forms and choice, and those are distinguished by result type
    private boolean repeatable = false;

    public boolean getRepeatable()
    {
        return repeatable;
    }

    public void setRepeatable(boolean repeatable)
    {
        this.repeatable = repeatable;
    }

    public boolean isSkippable()
    {
        return skippable;
    }

    public void setSkippable(boolean skippable)
    {
        this.skippable = skippable;
    }

    public String getType()
    {
        return type;
    }

    public String getPhi()
    {
        return phi;
    }

    public void setPhi(String phi)
    {
        this.phi = phi;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public void setSteps(List<SurveyStep> steps)
    {
        this.steps = steps;
    }

    public void setFormat(SurveyStepFormat format)
    {
        this.format = format;
    }

    public boolean isRepeatable()
    {
        return repeatable;
    }

    @JsonIgnore
    public SurveyStepType getStepType()
    {
        return SurveyStepType.getStepType(type);
    }

    public String getKey()
    {
        return key;
    }

    public void setKey(String key)
    {
        this.key = key;
    }

    public String getResultType()
    {
        return this.resultType;
    }

    public void setResultType(String resultType)
    {
        this.resultType = resultType;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public SurveyStepFormat getFormat()
    {
        return format;
    }

    public void setPhiClassification(PHIClassification classification)
    {
        this.phi = classification.getJsonValue();
    }

    @JsonIgnore
    public PHIClassification getPHIClassification()
    {
        return PHIClassification.getClassicication(phi);
    }

    public String getTitle()
    {
        return title;
    }

    @JsonIgnore
    @Nullable
    public Integer getMaxLength()
    {
        if (getFormat() == null || getFormat().getMaxLength() == null)
            return null;

        String val = String.valueOf(getFormat().getMaxLength());
        if (StringUtils.isBlank(val))
            return null;

        //Json MaxLength = 0 indicates Max text size
        Integer intVal = Integer.valueOf(val);
        return intVal == 0 || intVal > VARCHAR_TEXT_BOUNDARY ? Integer.MAX_VALUE : intVal;
    }

    @Nullable
    public List<SurveyStep> getSteps()
    {
        return steps;
    }

    @JsonIgnore
    public Style getStyle()
    {
        if (getFormat() == null)
            return null;

        String val = String.valueOf(getFormat().getStyle());
        if (StringUtils.isBlank(val))
            return null;

        return Style.getStyle(val);
    }

    @JsonIgnore
    public JdbcType getPropertyType()
    {
        return StepResultType.getStepResultType(getResultType()).getPropertyType(this);
    }

    public List<TextChoice> getTextChoices()
    {
        if (getFormat() == null)
            return null;

        return getFormat().getTextChoices();
    }

    public boolean hasOtherOption()
    {
        if (getTextChoices() == null)
            return false;

        return getTextChoices().stream().anyMatch(TextChoice::hasOtherOption);
    }
}
