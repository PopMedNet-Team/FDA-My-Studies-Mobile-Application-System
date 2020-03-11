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
package org.labkey.mobileappstudy;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.labkey.api.data.Container;
import org.labkey.api.data.JdbcType;
import org.labkey.api.data.PropertyStorageSpec;
import org.labkey.api.exp.ChangePropertyDescriptorException;
import org.labkey.api.exp.PropertyType;
import org.labkey.api.exp.list.ListDefinition;
import org.labkey.api.exp.list.ListService;
import org.labkey.api.exp.property.Domain;
import org.labkey.api.exp.property.DomainProperty;
import org.labkey.api.exp.property.Lookup;
import org.labkey.api.security.LimitedUser;
import org.labkey.api.security.User;
import org.labkey.api.security.UserManager;
import org.labkey.api.security.roles.RoleManager;
import org.labkey.api.security.roles.SubmitterRole;
import org.labkey.mobileappstudy.data.MobileAppStudy;
import org.labkey.mobileappstudy.data.SurveyResponse;
import org.labkey.mobileappstudy.surveydesign.InvalidDesignException;
import org.labkey.mobileappstudy.surveydesign.SurveyDesign;
import org.labkey.mobileappstudy.surveydesign.SurveyDesignProvider;
import org.labkey.mobileappstudy.surveydesign.SurveyStep;
import org.labkey.mobileappstudy.surveydesign.SurveyStep.StepResultType;

import java.sql.JDBCType;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * Class to process and apply a survey design to the underlying lists
 */
public class SurveyDesignProcessor
{
    private Logger logger;

    /**
     * List properties that are needed for survey relationships
     */
    private enum StandardProperties
    {
        Key("Key", JDBCType.INTEGER),
        ParticipantId("ParticipantId", JDBCType.INTEGER),
        ParentId("ParentId", JDBCType.INTEGER);

        private String key;
        private JDBCType type;

        StandardProperties(String key, JDBCType type)
        {
            this.key = key;
            this.type = type;
        }

        public static void ensureStandardProperties(Container container, Domain domain, String parentListName) throws InvalidDesignException
        {
            if (domain == null)
                throw new InvalidDesignException("Invalid list domain");

            for (StandardProperties val : values())
            {
                //ParentId uses parentListName as the field name
                String key = val.key == ParentId.key ? getParentListKey(parentListName) : val.key;
                DomainProperty prop = domain.getPropertyByName(key);

                if (prop == null)
                {
                    prop = addStandardProperty(container, val, domain, parentListName);
                    if (prop != null)
                        prop.setPropertyURI(domain.getTypeURI() + "#" + key);
                }
            }
        }

        /**
         * Add properties that are common to the list implementation and any special aspects of that property like Lookups
         * @param container hosting the list
         * @param propName name of the property
         * @param listDomain domain property will belong to
         * @param parentListName (Optional) of parent list. Required if ParentId property is needed
         */
        private static DomainProperty addStandardProperty(@NotNull Container container, @NotNull StandardProperties propName, @NotNull Domain listDomain, @Nullable String parentListName)
        {
            DomainProperty prop = null;
            switch (propName)
            {
                case Key:
                    prop = listDomain.addProperty(new PropertyStorageSpec(propName.key, JdbcType.INTEGER));
                    break;
                case ParticipantId:
                    prop = listDomain.addProperty(new PropertyStorageSpec(ParticipantId.key, JdbcType.INTEGER));
                    prop.setLookup(new Lookup(container, MobileAppStudySchema.NAME, MobileAppStudySchema.PARTICIPANT_TABLE));
                    break;
                case ParentId:
                    if (StringUtils.isNotBlank(parentListName))
                    {
                        prop = listDomain.addProperty(new PropertyStorageSpec( getParentListKey(parentListName), JdbcType.INTEGER));
                        prop.setLookup(new Lookup(container, "lists", parentListName));
                    }
                    break;
            }

            return prop;
        }

        private static String getParentListKey(String parentName)
        {
            return parentName + "Id";
        }
    }

    public SurveyDesignProcessor(Logger logger)
    {
        this.logger = logger != null ? logger : Logger.getLogger(MobileAppStudy.class);
    }

    public void updateSurveyDesign(@NotNull SurveyResponse surveyResponse, User user) throws Exception
    {
        //get study from response
        MobileAppStudy study = MobileAppStudyManager.get().getStudyFromAppToken(surveyResponse.getAppToken());

        if (study == null)
            throw new Exception("No study associated with app token '" + surveyResponse.getAppToken() + "'");
        logger.info(String.format(LogMessageFormats.START_UPDATE_SURVEY, study.getShortName(), surveyResponse.getActivityId(), surveyResponse.getSurveyVersion()));
        SurveyDesignProvider provider = MobileAppStudyManager.get().getSurveyDesignProvider(study.getContainer());
        if (provider == null)
            throw new InvalidDesignException(LogMessageFormats.PROVIDER_NULL);

        SurveyDesign design = provider.getSurveyDesign(study.getContainer(), study.getShortName(), surveyResponse.getActivityId(), surveyResponse.getSurveyVersion());
        if (design == null)
            throw new InvalidDesignException(LogMessageFormats.DESIGN_NULL);
        else if (!design.isValid())
            throw new InvalidDesignException(LogMessageFormats.MISSING_METADATA);

        // if a user isn't provided, need to create a LimitedUser to use for checking permissions, wrapping the Guest user
        User insertUser = new LimitedUser((user == null)? UserManager.getGuestUser() : user,
                new int[0], Collections.singleton(RoleManager.getRole(SubmitterRole.class)), false);

        ListDefinition listDef = ensureList(study.getContainer(), insertUser, design.getSurveyName(), null);
        applySurveyUpdate(study.getContainer(), insertUser, listDef.getDomain(), design.getSteps(), design.getSurveyName(), "");
    }

    /**
     * Get existing list or create new one
     *
     * @param listName name of list
     * @param container where list resides
     * @param user accessing/creating list
     * @return ListDefinition representing list
     * @throws InvalidDesignException if list is not able to be created, this is a wrapper of any other exception
     */
    private ListDefinition ensureList(Container container, User user, String listName, String parentListName) throws InvalidDesignException
    {
        ListDefinition listDef = ListService.get().getList(container, listName);
        return listDef != null ?
               listDef :
               newSurveyListDefinition(container, user, listName, parentListName);
    }

    private ListDefinition newSurveyListDefinition(Container container, User user, String listName, String parentListName) throws InvalidDesignException
    {
        try
        {
            ListDefinition list = ListService.get().createList(container, listName, ListDefinition.KeyType.AutoIncrementInteger);
            list.setKeyName("Key");
            list.save(user);

            logger.info(String.format(LogMessageFormats.LIST_CREATED, listName));

            //Return a refreshed version of listDefinition
            return ListService.get().getList(container, listName);
        }
        catch (Exception e)
        {
            throw new InvalidDesignException(String.format(LogMessageFormats.UNABLE_CREATE_LIST, listName), e);
        }
    }

    private void applySurveyUpdate(Container container, User user, Domain listDomain, List<SurveyStep> steps, String listName, String parentListName) throws InvalidDesignException
    {
        StandardProperties.ensureStandardProperties(container, listDomain, parentListName);

        try
        {
            //Check for duplicate field keys (sub-lists ok to overlap)
            Set<String> fieldKeys = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
            for (SurveyStep step: steps)
            {
                if (step.getType().equalsIgnoreCase("instruction"))
                    continue;

                if (fieldKeys.contains(step.getKey()))
                    throw new InvalidDesignException(String.format(LogMessageFormats.DUPLICATE_FIELD_KEY, step.getKey()));

                StepResultType resultType = StepResultType.getStepResultType(step.getResultType());
                //need to check for choice/group
                switch(resultType)
                {
                    case TextChoice:
                        updateChoiceList(container, user, listName, step);
                        break;
                    case GroupedResult:
                        updateGroupList(container, user, listName, step);
                        break;
                    case FetalKickCounter:
                    case TowerOfHanoi:
                    case SpatialSpanMemory:
                        step.setSteps(resultType.getDataValues());
                        updateGroupList(container, user, listName, step);
                        break;
                    case UNKNOWN:
                        throw new InvalidDesignException(String.format(LogMessageFormats.INVALID_RESULT_TYPE, step.getKey()));
                    default:
                        ensureStepProperty(listDomain, step);
                        break;
                }

                fieldKeys.add(step.getKey());
            }

            listDomain.save(user);
            logger.info(LogMessageFormats.END_SURVEY_UPDATE);
        }
        catch (InvalidDesignException e)
        {
            //Pass it through
            throw e;
        }
        catch (Exception e)
        {
            //Wrap any other exception
            throw new InvalidDesignException(LogMessageFormats.UNABLE_TO_APPLY_SURVEY, e);
        }
    }


    private void updateGroupList(Container container, User user, String parentListName, SurveyStep step) throws InvalidDesignException
    {
        if (step == null)
            throw new InvalidDesignException(LogMessageFormats.STEP_IS_NULL);
        if (step.getSteps() == null)
            throw new InvalidDesignException(String.format(LogMessageFormats.NO_GROUP_STEPS, step.getKey()));

        String subListName = parentListName + step.getKey();
        ListDefinition listDef = ensureList(container, user, subListName, parentListName);
        applySurveyUpdate(container, user, listDef.getDomain(), step.getSteps(), subListName, parentListName);
    }

    private void ensureStepProperty(Domain listDomain, SurveyStep step) throws InvalidDesignException
    {
        DomainProperty prop = listDomain.getPropertyByName(step.getKey());
        if (prop != null)
        {
            //existing property
            if (prop.getPropertyDescriptor().getJdbcType() != step.getPropertyType())
                throw new InvalidDesignException(String.format(LogMessageFormats.RESULT_TYPE_MISMATCH, step.getKey()));

            //Update a string field's size. Increase only.
            if (prop.getPropertyType() == PropertyType.STRING && step.getMaxLength() != null)
            {
                //Logged in List audit log
                if (step.getMaxLength() > prop.getScale())
                    prop.setScale(step.getMaxLength());
            }
        }
        else
        {
            //New property
            getNewDomainProperty(listDomain, step);
        }
    }

    private void updateChoiceList(Container container, User user, String parentSurveyName, SurveyStep step) throws InvalidDesignException, ChangePropertyDescriptorException
    {
        String listName = parentSurveyName + step.getKey();

        //Get existing list or create new one
        ListDefinition listDef = ensureList(container, user, listName, parentSurveyName);
        Domain domain = listDef.getDomain();

        //Check for key, participantId, and parent survey fields
        StandardProperties.ensureStandardProperties(container, domain, parentSurveyName);

        //Add value property
        ensureStepProperty(domain, step);

        if(step.hasOtherOption())
            ensureOtherOption(domain, step);

        try
        {
            domain.save(user);
        }
        catch (ChangePropertyDescriptorException e)
        {
            throw new InvalidDesignException(String.format(LogMessageFormats.SUBLIST_PROPERTY_ERROR, step.getKey()), e);
        }
    }

    private static final String OTHER_OPTION_BASE_DESCRIPTION = "Optional text provided by respondent";
    private static final int OTHER_OPTION_MAX_LENGTH = 4000;

    private void ensureOtherOption(Domain listDomain, SurveyStep step) throws InvalidDesignException
    {
        String otherTextKey = MobileAppStudyManager.getOtherOptionKey(step.getKey());
        JdbcType propType = JdbcType.VARCHAR;
        DomainProperty prop = listDomain.getPropertyByName(otherTextKey);
        if (prop == null)
        {
            //New property
            getNewDomainProperty(listDomain, otherTextKey, propType, OTHER_OPTION_BASE_DESCRIPTION, OTHER_OPTION_MAX_LENGTH );
        }
        // Else field already exists, no need to generate it...
    }

    private static DomainProperty getNewDomainProperty(Domain domain, SurveyStep step)
    {
        return getNewDomainProperty(domain, step.getKey(), step.getPropertyType(), step.getTitle(), step.getMaxLength());
    }

    private static DomainProperty getNewDomainProperty(Domain domain, String key, JdbcType propertyType, String description, Integer length)
    {
        DomainProperty prop = domain.addProperty(new PropertyStorageSpec(key, propertyType));
        prop.setName(key);
        prop.setDescription(description);
        prop.setPropertyURI(domain.getTypeURI() + "#" + key);
        if (prop.getPropertyType() == PropertyType.STRING && length != null)
            prop.setScale(length);

        prop.setMeasure(false);
        prop.setDimension(false);
        prop.setRequired(false);

        return prop;
    }

    private static class LogMessageFormats
    {
        public static final String UNABLE_TO_APPLY_SURVEY = "Unable to apply survey changes";
        public static final String STEP_IS_NULL = "Step is null";
        public static final String RESULT_TYPE_MISMATCH = "Can not change question result types. Field: %1$s";
        public static final String INVALID_RESULT_TYPE = "Unknown step result type for key: %1$s";
        public static final String PROVIDER_NULL = "No SurveyDesignProvider configured.";
        public static final String DESIGN_NULL = "Unable to parse design metadata";
        public static final String MISSING_METADATA = "Design document does not contain all the required fields (activityId, steps)";
        public static final String START_UPDATE_SURVEY = "Getting new survey version: Study: %1$s, Survey: %2$s, Version: %3$s";
        public static final String END_SURVEY_UPDATE = "Survey update completed";
        public static final String UNABLE_CREATE_LIST = "Unable to create new list. List: %1$s";
        public static final String LIST_CREATED = "Survey list [%1$s] successfully created.";
        public static final String SUBLIST_PROPERTY_ERROR = "Unable to add sub-list property: %1$s";
        public static final String NO_GROUP_STEPS = "Form contains no steps: Step: %1$s";
        public static final String DUPLICATE_FIELD_KEY = "Design schema contains duplicate field keys: %1$s";
    }
}
