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

package org.labkey.mobileappstudy;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.labkey.api.collections.ArrayListMap;
import org.labkey.api.data.ColumnInfo;
import org.labkey.api.data.CompareType;
import org.labkey.api.data.Container;
import org.labkey.api.data.ContainerManager;
import org.labkey.api.data.DbScope;
import org.labkey.api.data.JdbcType;
import org.labkey.api.data.Results;
import org.labkey.api.data.SQLFragment;
import org.labkey.api.data.SimpleFilter;
import org.labkey.api.data.SqlExecutor;
import org.labkey.api.data.SqlSelector;
import org.labkey.api.data.Table;
import org.labkey.api.data.TableInfo;
import org.labkey.api.data.TableSelector;
import org.labkey.api.exp.list.ListDefinition;
import org.labkey.api.exp.list.ListService;
import org.labkey.api.query.BatchValidationException;
import org.labkey.api.query.FieldKey;
import org.labkey.api.query.QueryUpdateService;
import org.labkey.api.query.RuntimeValidationException;
import org.labkey.api.security.LimitedUser;
import org.labkey.api.security.User;
import org.labkey.api.security.UserManager;
import org.labkey.api.security.roles.EditorRole;
import org.labkey.api.security.roles.RoleManager;
import org.labkey.api.security.roles.SubmitterRole;
import org.labkey.api.util.ChecksumUtil;
import org.labkey.api.util.ContainerUtil;
import org.labkey.api.util.GUID;
import org.labkey.api.util.JobRunner;
import org.labkey.api.util.Pair;
import org.labkey.api.view.NotFoundException;
import org.labkey.mobileappstudy.data.EnrollmentToken;
import org.labkey.mobileappstudy.data.EnrollmentTokenBatch;
import org.labkey.mobileappstudy.data.MobileAppStudy;
import org.labkey.mobileappstudy.data.Participant;
import org.labkey.mobileappstudy.data.Participant.ParticipantStatus;
import org.labkey.mobileappstudy.data.Response;
import org.labkey.mobileappstudy.data.SurveyResponse;
import org.labkey.mobileappstudy.data.SurveyResponse.ResponseStatus;
import org.labkey.mobileappstudy.data.SurveyResult;
import org.labkey.mobileappstudy.data.TextChoiceResult;
import org.labkey.mobileappstudy.forwarder.ForwarderProperties;
import org.labkey.mobileappstudy.forwarder.ForwardingScheduler;
import org.labkey.mobileappstudy.forwarder.ForwardingType;
import org.labkey.mobileappstudy.forwarder.SurveyResponseForwardingJob;
import org.labkey.mobileappstudy.surveydesign.FileSurveyDesignProvider;
import org.labkey.mobileappstudy.surveydesign.InvalidDesignException;
import org.labkey.mobileappstudy.surveydesign.ServiceSurveyDesignProvider;
import org.labkey.mobileappstudy.surveydesign.SurveyDesignProvider;
import org.labkey.mobileappstudy.surveydesign.SurveyStep;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MobileAppStudyManager
{
    private static final String TRUNCATED_MESSAGE_SUFFIX =  "... (message truncated)";
    private static final Integer ERROR_MESSAGE_MAX_SIZE = 1000 - TRUNCATED_MESSAGE_SUFFIX.length();
    private static final Integer TOKEN_SIZE = 8;
    private static final String TOKEN_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final MobileAppStudyManager _instance = new MobileAppStudyManager();
    private static final ChecksumUtil _checksumUtil = new ChecksumUtil(TOKEN_CHARS);

    private static final int THREAD_COUNT = 10; //TODO: Verify this is an appropriate number
    private static JobRunner _shredder;
    private static final Logger logger = Logger.getLogger(MobileAppStudy.class);

    public static final String OTHER_OPTION_TITLE = "_Other_Text";

    private MobileAppStudyManager()
    {
        if (_shredder == null)
            _shredder = new JobRunner("MobileAppResponseShredder", THREAD_COUNT);
    }

    public static MobileAppStudyManager get()
    {
        return _instance;
    }

    void doStartup()
    {
        ForwardingScheduler.get().schedule();

        //Pick up any pending shredder jobs that might have been lost at shutdown/crash/etc
        Collection<Integer> pendingResponses = getPendingResponseIds();
        if (pendingResponses != null)
        {
            pendingResponses.forEach(rowId ->
                enqueueSurveyResponse(() -> shredSurveyResponse(rowId, null))
            );
        }
    }

    /**
     * Create an enrollment token with TOKEN_SIZE characters plus a checksum character
     * @return the generated token.
     */
    public String generateEnrollmentToken()
    {
        String prefix = RandomStringUtils.random(TOKEN_SIZE, TOKEN_CHARS);
        return (prefix + _checksumUtil.getValue(prefix));
    }

    /**
     * Determines if the checksum value included in the token string validates or not
     * @param token string including the checksum character
     * @return true or false to indicate if the checksum validates
     */
    public boolean isChecksumValid(@NotNull String token)
    {
        try
        {
            return _checksumUtil.isValid(token.toUpperCase());
        }
        catch (IllegalArgumentException e)
        {
            return false;
        }
    }

    /**
     * Determines if the token provided is one that is associated with the study short name
     * by the short name.  This does not check the validity of the token itself.
     * @param token the token string
     * @param shortName unique identifier for the study
     * @return true or false to indicate if the given string is associated with the study.
     */
    public boolean isValidStudyToken(@NotNull String token, @NotNull String shortName)
    {
        List<Container> containers = getStudyContainers(shortName);
        if (containers.isEmpty())
            return false;
        MobileAppStudySchema schema = MobileAppStudySchema.getInstance();
        SimpleFilter filter = new SimpleFilter();
        filter.addCondition(FieldKey.fromParts("Container"), containers, CompareType.IN);
        filter.addCondition(FieldKey.fromString("Token"), token.toUpperCase());
        TableSelector selector = new TableSelector(schema.getTableInfoEnrollmentToken(), filter, null);
        return selector.exists();
    }

    /**
     * Determines if the given shortName corresponds to a study that requires an enrollment token to be provided.
     * This will be true if there have been token batches generated for the container that the shortName is associated
     * with.
     * @param shortName unique string identifier for the study
     * @return true if a token is required and false otherwise
     */
    public boolean enrollmentTokenRequired(@NotNull String shortName)
    {
        List<Container> containers = getStudyContainers(shortName);
        if (containers.isEmpty())
            return false;
        MobileAppStudySchema schema = MobileAppStudySchema.getInstance();
        SimpleFilter filter = new SimpleFilter();
        filter.addCondition(FieldKey.fromParts("Container"), containers, CompareType.IN);
        TableSelector selector = new TableSelector(schema.getTableInfoEnrollmentTokenBatch(), filter, null);
        return selector.exists();
    }

    /**
     * Determine if the study identified by the given short name has a participant with the given token
     * @param shortName identifier for the study
     * @param tokenValue identifier for the participant
     * @return true or false depending on existence of participant in the given study
     */
    public boolean hasParticipant(@NotNull String shortName, @NotNull String tokenValue)
    {
        MobileAppStudySchema schema = MobileAppStudySchema.getInstance();
        TableInfo participantTable = schema.getTableInfoParticipant();
        TableInfo tokenTable = schema.getTableInfoEnrollmentToken();
        TableInfo studyTable = schema.getTableInfoStudy();

        SQLFragment sql = new SQLFragment("SELECT p.* from ").append(participantTable, "p");
        sql.append(" JOIN ").append(tokenTable, "t").append(" ON t.participantId = p.rowId");
        sql.append(" JOIN ").append(studyTable, "s").append(" ON p.studyId = s.rowId");
        sql.append(" WHERE t.token = ? AND s.shortName = ?");

        SqlSelector selector = new SqlSelector(schema.getSchema(), sql.getSQL(), tokenValue.toUpperCase(), shortName.toUpperCase());
        return selector.exists();
    }

    /**
     * Given a tokenValue that is associated with a study whose short name is provided, adds a new
     * study participant, generates the application token for that participant, and updates the
     * enrollment tokens table to link the token to the participant.
     * @param shortName identifier for the study
     * @param tokenValue the token string being used for enrollment
     * @return an object representing the participant that was created
     */
    public Participant enrollParticipant(@NotNull String shortName, @Nullable String tokenValue)
    {
        DbScope scope = MobileAppStudySchema.getInstance().getSchema().getScope();

        try (DbScope.Transaction transaction = scope.ensureTransaction())
        {
            MobileAppStudySchema schema = MobileAppStudySchema.getInstance();
            Participant participant = new Participant();
            List<MobileAppStudy> studies = getStudies(shortName);
            MobileAppStudy study;
            if (tokenValue != null)
            {
                Optional<MobileAppStudy> studyOpt = studies.stream().filter(s -> getEnrollmentToken(s.getContainer(), tokenValue) != null).findFirst();
                if (!studyOpt.isPresent())
                    throw new RuntimeValidationException("Invalid token '" + tokenValue + "' for study id '" + shortName + "'. Participant cannot be enrolled.");
                else
                    study = studyOpt.get();
            }
            else if (studies.isEmpty())
                throw new RuntimeValidationException("Invalid study id '" + shortName + "'.  Participant cannot be enrolled.");
            else if (studies.size() > 1)
                throw new RuntimeValidationException("Study id '" + shortName + "' cannot be associated with more than one container when not using enrollment tokens.  Participant cannot be enrolled.");
            else
                study = studies.get(0);
            participant.setStudyId(study.getRowId());
            participant.setAppToken(GUID.makeHash());
            participant.setContainer(study.getContainer());
            participant.setStatus(ParticipantStatus.Enrolled);
            participant = Table.insert(null, schema.getTableInfoParticipant(), participant);
            if (tokenValue != null)
            {
                EnrollmentToken eToken = getEnrollmentToken(study.getContainer(), tokenValue);
                eToken.setParticipantId(participant.getRowId());
                Table.update(null, schema.getTableInfoEnrollmentToken(), eToken, eToken.getRowId());
            }

            transaction.commit();

            return participant;
        }
    }

    /**
     * Given a container and a token value, return the corresponding enrollment token object.
     * @param container the container in which the token is valid
     * @param tokenValue the value of the token
     * @return object representation of the database record for this token.
     */
    private EnrollmentToken getEnrollmentToken(@NotNull Container container, @NotNull String tokenValue)
    {
        MobileAppStudySchema schema = MobileAppStudySchema.getInstance();
        SimpleFilter filter = SimpleFilter.createContainerFilter(container);
        filter.addCondition(FieldKey.fromString("Token"), tokenValue.toUpperCase());
        TableSelector selector = new TableSelector(schema.getTableInfoEnrollmentToken(), filter, null);
        return selector.getObject(EnrollmentToken.class);
    }

    /**
     * Creates a new token batch with a given number of unique tokens
     * @param count number of unique tokens to generate
     * @param user the user making the request for the batch
     * @param container container in which the tokens are being generated
     * @return an enrollment token batch object representing the newly created batch.
     */
    public EnrollmentTokenBatch createTokenBatch(@NotNull Integer count, @NotNull User user, @NotNull Container container)
    {
        DbScope scope = MobileAppStudySchema.getInstance().getSchema().getScope();

        Date createdDate = new Date();
        try (DbScope.Transaction transaction = scope.ensureTransaction())
        {
            EnrollmentTokenBatch batch = new EnrollmentTokenBatch();
            batch.setCount(count);
            batch.setContainer(container);

            // create the batch entry
            TableInfo batchTable = MobileAppStudySchema.getInstance().getTableInfoEnrollmentTokenBatch();
            batch = Table.insert(user, batchTable, batch);

            // Generate the individual tokens, checking for duplicates
            TableInfo tokenTable = MobileAppStudySchema.getInstance().getTableInfoEnrollmentToken();
            MobileAppStudySchema schema = MobileAppStudySchema.getInstance();
            SqlExecutor executor = new SqlExecutor(schema.getSchema());

            int numTokens = 0;
            SQLFragment sql = new SQLFragment("INSERT INTO " + tokenTable);
            sql.append(" (BatchId, Token, ParticipantId, Created, CreatedBy, Container) ");
            sql.append(" SELECT ?, ?, NULL, ?, ?, ? ");
            sql.append(" WHERE NOT EXISTS (SELECT * FROM ").append(tokenTable, "dup").append(" WHERE dup.Token = ?)");
            while (numTokens < count)
            {
                String token = generateEnrollmentToken();
                numTokens += executor.execute(sql.getSQL(), batch.getRowId(), token, createdDate, user.getUserId(), container, token);
            }

            transaction.commit();
            return batch;
        }
    }

    /**
     * Remove data associated with the given container from the tables for this module
     * @param c container that is being removed
     */
    public void purgeContainer(@NotNull Container c)
    {
        MobileAppStudySchema schema = MobileAppStudySchema.getInstance();
        try (DbScope.Transaction transaction = schema.getSchema().getScope().ensureTransaction())
        {
            ContainerUtil.purgeTable(schema.getTableInfoEnrollmentToken(), c, null); //Has a FKs to TokenBatch and Participant tables
            ContainerUtil.purgeTable(schema.getTableInfoResponse(), c, null);   //Has a FK to participant table
            ContainerUtil.purgeTable(schema.getTableInfoParticipant(), c, null); //Has a FK to study table
            ContainerUtil.purgeTable(schema.getTableInfoEnrollmentTokenBatch(), c, null);
            ContainerUtil.purgeTable(schema.getTableInfoStudy(), c, null);
            ContainerUtil.purgeTable(schema.getTableInfoResponseMetadata(), c, null);

            transaction.commit();
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }


    /**
     * Get the studies associated with a given identifier
     * @param shortName identifier for the study
     * @return the associated studies, or an empty list if these are no such studies
     */
    @NotNull
    public List<MobileAppStudy> getStudies(@NotNull String shortName)
    {
        MobileAppStudySchema schema = MobileAppStudySchema.getInstance();
        SimpleFilter filter = new SimpleFilter(FieldKey.fromString("ShortName"), shortName.toUpperCase());
        TableSelector selector = new TableSelector(schema.getTableInfoStudy(), filter, null);
        return selector.getArrayList(MobileAppStudy.class);
    }


    /**
     * Get the study associated with a given container
     * @param c the container in question
     * @return the associated study, or null if there is no such study
     */
    public MobileAppStudy getStudy(@NotNull Container c)
    {
        MobileAppStudySchema schema = MobileAppStudySchema.getInstance();
        SimpleFilter filter = SimpleFilter.createContainerFilter(c);
        TableSelector selector = new TableSelector(schema.getTableInfoStudy(), filter, null);
        return selector.getObject(MobileAppStudy.class);
    }


    /**
     * Get the containers associated with a particular study short name
     * @param shortName identifier for the study
     * @return the associated containers, or an empty list if there is no such container.
     */
    @NotNull
    public List<Container> getStudyContainers(@NotNull String shortName)
    {
        return getStudies(shortName).stream()
                .map(MobileAppStudy::getContainer)
                .collect(Collectors.toList());
    }


    /**
     * Given a container, find the associated study short name
     * @param c container in question
     * @return the short name identifier for the study associated with the container or null if there is no such study.
     */
    public String getStudyShortName(@NotNull Container c)
    {
        MobileAppStudy study = getStudy(c);
        return study == null ? null : study.getShortName();
    }

    /**
     * Determines if the study in the given container has any data about participants or not
     * @param c the container in question
     * @return true if there are any participants associated with the given container, false otherwise.
     */
    public boolean hasStudyParticipants(@NotNull Container c)
    {
        MobileAppStudySchema schema = MobileAppStudySchema.getInstance();
        SimpleFilter filter = SimpleFilter.createContainerFilter(c);
        TableSelector selector = new TableSelector(schema.getTableInfoParticipant(), Collections.singleton("RowId"), filter, null);
        return selector.exists();
    }

    /**
     * Determines if there is a study with the given short name identifier
     * @param shortName identifier for the study
     * @return true or false, depending on whether the study short name already exists in the database.
     */
    public boolean studyExists(@NotNull String shortName)
    {
        MobileAppStudySchema schema = MobileAppStudySchema.getInstance();

        SimpleFilter filter = new SimpleFilter(FieldKey.fromString("ShortName"), shortName.toUpperCase());
        TableSelector selector = new TableSelector(schema.getTableInfoStudy(), Collections.singleton("ShortName"), filter, null);
        return selector.exists();
    }

    private List<Container> getContainers(@NotNull String shortName)
    {
        MobileAppStudySchema schema = MobileAppStudySchema.getInstance();

        SimpleFilter filter = new SimpleFilter(FieldKey.fromString("ShortName"), shortName.toUpperCase());
        TableSelector selector = new TableSelector(schema.getTableInfoStudy(), Collections.singleton("Container"), filter, null);
        List<String> containerIds =  selector.getArrayList(String.class);
        List<Container> containers = new ArrayList<>();
        for (String id : containerIds)
        {
            containers.add(ContainerManager.getForId(id));
        }
        return containers;
    }

    /**
     * Determine if the study short name that identifies the study is associated with
     * a container that is a sibling of the one provided (preventing the use of the study short name as
     * a disambiguating data point)
     * @param shortName identifier for the study
     * @param container current container (the opposite of 'elsewhere')
     * @return tre if there is another container whose parent is the same as the given one with the short name associated with it; false otherwise
     */
    public boolean studyExistsAsSibling(@NotNull String shortName, @NotNull Container container)
    {
        Container parent = container.getParent();
        for (Container otherContainer : getContainers(shortName))
        {
            if (!otherContainer.equals(container) && otherContainer.getParent().equals(parent))
                return true;
        }
        return false;
    }


    /**
     * Determines if the given study short name that identifies the study is associated with
     * a container other than the one provided
     * @param shortName identifier for the study
     * @param container current container (the opposite of 'elsewhere')
     * @return true if there is another container that has this short name associated with it; false otherwise
     */
    public boolean studyExistsElsewhere(@NotNull String shortName, @NotNull Container container)
    {
        MobileAppStudySchema schema = MobileAppStudySchema.getInstance();

        SimpleFilter filter = new SimpleFilter(FieldKey.fromString("ShortName"), shortName.toUpperCase());
        filter.addCondition(FieldKey.fromString("Container"), container, CompareType.NEQ);
        TableSelector selector = new TableSelector(schema.getTableInfoStudy(), Collections.singleton("ShortName"), filter, null);
        return selector.exists();
    }

    /**
     * Updates the study short name for a given study
     * @param study the existing study object
     * @param newShortName the new short name for the study
     * @param user the user making the update request
     * @return the updated study object
     */
    private MobileAppStudy updateStudy(@NotNull MobileAppStudy study, @NotNull String newShortName, boolean collectionEnabled, @NotNull User user)
    {
        MobileAppStudySchema schema = MobileAppStudySchema.getInstance();
        TableInfo studyTable = schema.getTableInfoStudy();
        study.setShortName(newShortName.toUpperCase());
        study.setCollectionEnabled(collectionEnabled);

        return Table.update(user, studyTable, study, study.getRowId());
    }

    /**
     * Insert a new study object into the database
     * @param shortName the short name identifier for the study
     * @param container the container to be associated with this short name
     * @param user the user making the insert request
     * @return the object representing the newly inserted study.
     */
    private MobileAppStudy insertStudy(@NotNull String shortName, boolean collectionEnabled, @NotNull Container container, @NotNull User user)
    {
        MobileAppStudySchema schema = MobileAppStudySchema.getInstance();
        TableInfo studyTable = schema.getTableInfoStudy();

        MobileAppStudy study = new MobileAppStudy();
        study.setShortName(shortName.toUpperCase());
        study.setCollectionEnabled(collectionEnabled);
        study.setContainer(container);
        return Table.insert(user, studyTable, study);
    }

    /**
     * Method to update or insert a study, depending on whether the study already exists or not.
     * @param shortName the short name identifier for the study
     * @param container the container associated with the study
     * @param user the user making the request
     * @return the object representing the newly updated or inserted study.
     */
    public MobileAppStudy insertOrUpdateStudy(@NotNull String shortName, boolean collectionEnabled, @NotNull Container container, @NotNull User user)
    {
        MobileAppStudy study = getStudy(container);
        if (study == null)
            return insertStudy(shortName, collectionEnabled, container, user);
        else
            return updateStudy(study, shortName, collectionEnabled, user);
    }

    /**
     * Add response processing job to queue
     * @param run Runnable to add to processing queue
     */
    void enqueueSurveyResponse(@NotNull Runnable run)
    {
        _shredder.execute(run);
    }

    /**
     * Method to process Survey Responses
     * @param rowId mobileappstudy.Response.RowId to process
     * @param user the user initiating the shredding request
     */
    void shredSurveyResponse(@NotNull Integer rowId, @Nullable User user)
    {
        SurveyResponse surveyResponse = getResponse(rowId);
        MobileAppStudy study = MobileAppStudyManager.get().getStudyFromAppToken(surveyResponse.getAppToken());

        if (surveyResponse != null)
        {
            try
            {
                updateSurveys(surveyResponse, user);
                logger.info(String.format("Processing response %1$s in container %2$s", rowId, surveyResponse.getContainer().getName()));

                this.store(surveyResponse, rowId, user);
                this.updateProcessingStatus(user, rowId, ResponseStatus.PROCESSED);
                logger.info(String.format("Processed response %1$s in container %2$s", rowId, surveyResponse.getContainer().getName()));
                enqueueForwardingJob(user, surveyResponse.getContainer());
            }
            catch (InvalidDesignException e)
            {
                logger.error(String.format("Failed to update survey design: StudyId: %1$s, ActivityId: %2$s, version: %3$s", study.getShortName(),  surveyResponse.getActivityId(), surveyResponse.getSurveyVersion()), e);
                this.updateProcessingStatus(user, rowId, ResponseStatus.ERROR, e.getMessage());
            }
            catch (Exception e)
            {
                logger.error("Error processing response " + rowId + " in container " + surveyResponse.getContainer().getName(), e);
                this.updateProcessingStatus(user, rowId, ResponseStatus.ERROR, e instanceof NullPointerException ? "NullPointerException" : e.getMessage());
            }
        }
        else
        {
            logger.error("No response found for id " + rowId);
        }
    }

    /**
     * Check if survey was previously seen, if not retrieve schema and apply
     * @param surveyResponse that was sent, includes SurveyId and Version
     * @param user executing response (can be null)
     * @throws InvalidDesignException If design schema cannot be applied
     */
    private synchronized void updateSurveys(@NotNull SurveyResponse surveyResponse, @Nullable User user) throws Exception
    {
        //If we've seen this activity metadata before continue
        if (isKnownVersion(surveyResponse.getAppToken(), surveyResponse.getActivityId(), surveyResponse.getSurveyVersion(), surveyResponse.getRowId()))
            return;

        //Else retrieve and apply any changes
        try (DbScope.Transaction transaction = MobileAppStudySchema.getInstance().getSchema().getScope().ensureTransaction())
        {
            new SurveyDesignProcessor(logger).updateSurveyDesign(surveyResponse, user);
            transaction.commit();
        }
    }

    /**
     * Get Response from DB
     * @param rowId mobileappstudy.Response.RowId to retrieve
     * @return SurveyResponse object representing row, or null if not found
     */
    @Nullable
    SurveyResponse getResponse(@NotNull Integer rowId)
    {
        FieldKey fkey = FieldKey.fromParts("rowId");
        SimpleFilter filter = new SimpleFilter(fkey, rowId);

        return new TableSelector(MobileAppStudySchema.getInstance().getTableInfoResponse(), filter, null)
                .getObject(SurveyResponse.class);
    }

    /**
     * Get RowIds for responses that are awaiting processing
     * @return Set of RowIds for responses that are currently in the pending state
     */
    public Collection<Integer> getPendingResponseIds()
    {
        FieldKey fkey = FieldKey.fromParts("Status");
        SimpleFilter filter;
        filter = new SimpleFilter(fkey, ResponseStatus.PENDING.getPkId());
        return new TableSelector(MobileAppStudySchema.getInstance().getTableInfoResponse(), Collections.singleton("RowId"), filter, null)
                .getCollection(Integer.class);
    }

    /**
     * Get the set of responses that are in the specified state
     * @param status to query
     * @param container hosting study to be queried
     * @return Collection of SurveyResponse objects
     */
    public Collection<SurveyResponse> getResponsesByStatus(ResponseStatus status, @NotNull Container container)
    {
        FieldKey fkey = FieldKey.fromParts("Status");
        SimpleFilter filter;

        filter = SimpleFilter.createContainerFilter(container);
        filter.addCondition(fkey, status.getPkId());

        return new TableSelector(MobileAppStudySchema.getInstance().getTableInfoResponse(), filter, null)
                .getCollection(SurveyResponse.class);
    }



    /**
     * Verify if survey exists
     * @param activityId to verify
     * @param container holding study/survey
     * @param user executing query
     * @return true if survey found
     */
    boolean surveyExists(String activityId, Container container, User user)
    {
        try
        {
            getResultTable(activityId, container, user);
            return true;
        }
        catch (NotFoundException e)
        {
            return false;
        }
    }

    /**
     * Verify if appToken is assigned to participant
     * @param appToken to look up
     * @return true if apptoken found in mobilestudy.Participant.appToken
     */
    boolean participantExists(String appToken)
    {
        return getParticipantFromAppToken(appToken) != null;
    }

    /**
     * Retrieve the participant record associated with the supplied appToken
     * @param appToken to lookup
     * @return Participant if found, null if not
     */
    @Nullable
    public Participant getParticipantFromAppToken(String appToken)
    {
        MobileAppStudySchema schema = MobileAppStudySchema.getInstance();
        FieldKey pKey = FieldKey.fromParts("apptoken");

        SimpleFilter filter = new SimpleFilter(pKey, appToken);
        return new TableSelector(schema.getTableInfoParticipant(), filter, null).getObject(Participant.class);
    }

    /**
     * Insert new row into the mobileappstudy.Response table
     * @param resp to insert
     * @return updated object representing row
     */
    @NotNull
    SurveyResponse insertResponse(@NotNull SurveyResponse resp)
    {
        Participant participant = getParticipantFromAppToken(resp.getAppToken());
        if (participant == null)
            throw new IllegalStateException("Could not insert Response, Participant associated with appToken not found");

        resp.setContainer(participant.getContainer());
        resp.setParticipantId(participant.getRowId());

        MobileAppStudySchema schema = MobileAppStudySchema.getInstance();
        TableInfo responseTable = schema.getTableInfoResponse();
        return Table.insert(null, responseTable, resp);
    }

    /**
     * Retrieve the study associated with an appToken via the participant
     * @param appToken to lookup
     * @return MobileAppStudy object, will return null if participant or study not found
     */
    @Nullable
    MobileAppStudy getStudyFromAppToken(String appToken)
    {
        Participant participant = getParticipantFromAppToken(appToken);
        if (null == participant)
            return null;
        return getStudyFromParticipant(participant);
    }

    /**
     * Retrieve the study associated with the participant
     * @param participant to lookup
     * @return MobileAppStudy object, will return null if participant or study not found
     */
    @Nullable
    MobileAppStudy getStudyFromParticipant(@NotNull Participant participant)
    {
        SimpleFilter filter = new SimpleFilter(FieldKey.fromParts("rowId"), participant.getStudyId());
        return new TableSelector(MobileAppStudySchema.getInstance().getTableInfoStudy(),  filter, null).getObject(MobileAppStudy.class);
    }

    public int reprocessResponses(User user, @NotNull Set<Integer> listIds)
    {
        SimpleFilter filter = new SimpleFilter(FieldKey.fromParts("rowId"), listIds, CompareType.IN);
        filter.addCondition(FieldKey.fromParts("Status"), ResponseStatus.ERROR.getPkId(), CompareType.EQUAL);
        Collection<SurveyResponse> responses = new TableSelector(
                MobileAppStudySchema.getInstance().getTableInfoResponse(), filter, null).getCollection(SurveyResponse.class);

        responses.forEach(response ->
        {
            updateProcessingStatus(user, response.getRowId(), ResponseStatus.PENDING);
            enqueueSurveyResponse(() -> shredSurveyResponse(response.getRowId(), user));
        });

        return responses.size();
    }

    public void updateProcessingStatus(@Nullable User user, @NotNull Integer rowId, @NotNull ResponseStatus newStatus)
    {
        updateProcessingStatus(user, rowId, newStatus, null);
    }

    public void updateProcessingStatus(@Nullable User user, @NotNull Integer rowId, @NotNull ResponseStatus newStatus, @Nullable String errorMessage)
    {
        MobileAppStudySchema schema = MobileAppStudySchema.getInstance();
        TableInfo responseTable = schema.getTableInfoResponse();
        SurveyResponse response = new TableSelector(responseTable).getObject(rowId, SurveyResponse.class);

        response.setStatus(newStatus);
        if (errorMessage != null)
            response.setErrorMessage(errorMessage.length() > ERROR_MESSAGE_MAX_SIZE ? errorMessage.substring(0, ERROR_MESSAGE_MAX_SIZE) + TRUNCATED_MESSAGE_SUFFIX : errorMessage);
        else
            response.setErrorMessage(null);
        // we currently have only start and end statuses, so we can safely set the processed and processedBy
        // fields at this point.
        response.setProcessed(new Date());
        if (user != null && !user.isGuest())
            response.setProcessedBy(user);

        Table.update(user, responseTable, response, response.getRowId());
    }

    public Set<Integer> getNonErrorResponses(Set<Integer> listIds)
    {
        SimpleFilter filter = new SimpleFilter(FieldKey.fromParts("rowId"), listIds, CompareType.IN);
        filter.addCondition(FieldKey.fromParts("status"), ResponseStatus.ERROR.getPkId(), CompareType.NEQ);
        Collection<SurveyResponse> responses = new TableSelector(
                MobileAppStudySchema.getInstance().getTableInfoResponse(), filter, null).getCollection(SurveyResponse.class);

        return responses.stream().map(SurveyResponse::getRowId).collect(Collectors.toSet());
    }

    /**
     * Stores the survey results of this object into their respective lists
     * @param surveyResponse the response to be stored
     * @param responseBlobId rowId of the response table
     * @param user the user initiating the store request
     * @throws Exception if there was a problem storing the results in one or more of its lists, in which case none of the lists will be updated
     */
    public void store(@NotNull SurveyResponse surveyResponse, Integer responseBlobId, @Nullable User user) throws Exception
    {
        MobileAppStudySchema schema = MobileAppStudySchema.getInstance();
        DbScope scope = schema.getSchema().getScope();

        List<String> errors = new ArrayList<>();

        // if a user isn't provided, need to create a LimitedUser to use for checking permissions, wrapping the Guest user
        User insertUser = new LimitedUser((user == null)? UserManager.getGuestUser() : user,
                new int[0], Collections.singleton(RoleManager.getRole(SubmitterRole.class)), false);

        try (DbScope.Transaction transaction = scope.ensureTransaction())
        {
            Response response = Response.getResponseObject(surveyResponse.getData());
            storeSurveyResult(response, surveyResponse.getActivityId(), surveyResponse.getParticipantId(), responseBlobId, response.getResults(), errors, surveyResponse.getContainer(), insertUser);
            if (!errors.isEmpty())
                throw new Exception("Problem storing data in list '" + surveyResponse.getActivityId() + "' in container '" + surveyResponse.getContainer().getName() + "'.\n" + StringUtils.join(errors, "\n"));
            else
                transaction.commit();
        }
    }

    /**
     * Store a SurveyResult in a given list and then recursively store its multi-valued results in their respective lists
     *
     * @param response the response whose results are being stored
     * @param listName name of the list to store data in
     * @param participantId identifier of the participant corresponding to this survey
     * @param responseBlobId rowId to the mobileappstudy.response table
     * @param results the collection of results for this survey result
     * @param errors the collection of errors encountered while storing this survey result
     * @param container the container in which the list lives
     * @param user the user who will store the data     @throws Exception if there are problems storing the data.
     */
    private void storeSurveyResult(@NotNull Response response, @NotNull String listName, @NotNull Integer participantId, @NotNull Integer responseBlobId, @Nullable List<SurveyResult> results, @NotNull List<String> errors, @NotNull Container container, @NotNull User user) throws Exception
    {
        if (results == null)
        {
            errors.add("No results provided in response.");
            return;
        }
        // initialize the data map with the survey result values
        Map<String, Object> data = new ArrayListMap<>();
        data.put("startTime", response.getStartTime());
        data.put("endTime", response.getEndTime());
        data.put("responseId", responseBlobId);

        Map<String, Object> row = storeListResults(null, listName, results, data, errors, container, user, participantId);
        if (!row.isEmpty())
        {
            Integer activityId = (Integer) row.get("Key");
            Pair<String, Integer> rowKey = new Pair<>(listName + "Id", activityId);
            List<SurveyResult> multiValuedResults = getMultiValuedResults(listName, results);
            storeMultiValuedResults(multiValuedResults, activityId, rowKey, errors, container, user, participantId);
        }
    }

    /**
     * Extract the subset of results that are single-valued and validate that these fields exist and are of the proper type in the given list
     * @param list the table where the single-valued results are to be stored; used for validating column names and types
     * @param results the results to be filtered
     * @param errors a collection of validation errors
     * @return the filtered set of results
     */
    private List<SurveyResult> getSingleValuedResults(@NotNull TableInfo list, @NotNull List<SurveyResult> results, @NotNull List<String> errors)
    {
        List<SurveyResult> singleValuedResults = new ArrayList<>();
        results.stream().filter(result -> result.getStepResultType().isSingleValued()).forEach(result ->
        {
            if (!StringUtils.isEmpty(result.getResultType())) // instruction steps are represented in the results with an empty string as a resultType
            {
                result.setListName(list.getName());
                if (validateListColumn(list, result.getKey(), result.getStepResultType(), errors))
                {
                    singleValuedResults.add(result);
                }
            }
        });

        return singleValuedResults;
    }

    /**
     * Find the set of results in a list that are multi-valued responses
     * @param baseListName name of the containing response element
     * @param results the set of responses that have more than a single value
     * @return the subset of results that are multi-valued
     */
    private List<SurveyResult> getMultiValuedResults(@NotNull String baseListName, @NotNull List<SurveyResult> results)
    {
        List<SurveyResult> multiValuedResults = new ArrayList<>();
        for (SurveyResult result : results)
        {
            if (!result.getStepResultType().isSingleValued())
            {
                result.setListName(baseListName + StringUtils.capitalize(result.getKey()));
                multiValuedResults.add(result);
            }
        }

        return multiValuedResults;
    }

    /**
     * Gets a table for a given list name in a given container
     * @param listName name of the list whose table is to be returned
     * @param container container for the list
     * @param user user for retrieving the table from the list
     * @return the table
     * @throws NotFoundException if the list or table info cannot be found
     */
    private TableInfo getResultTable(@NotNull String listName, @NotNull Container container, @Nullable User user) throws NotFoundException
    {
        ListDefinition listDef = ListService.get().getList(container, listName);
        if (listDef == null)
            throw new NotFoundException("Invalid list '" + listName + "' for container '" + container.getName() + "'");

        TableInfo resultTable = listDef.getTable(user, container);
        if (resultTable == null)
            throw new NotFoundException("Unable to find table for list '" + listDef.getName() + "' in container '" + container.getName() + "'");

        return resultTable;
    }

    /**
     * Determine if a column of the appropriate type is available in the given table
     * @param table the table in question
     * @param columnName name of the column
     * @param resultType the type of the column we expect
     *@param errors collection of validation errors accumulated thus far  @return true if the expected column with the expected type is found; false otherwise
     */
    private boolean validateListColumn(@NotNull TableInfo table, @NotNull String columnName, SurveyStep.StepResultType resultType, @NotNull List<String> errors)
    {
        //TODO: this should be moved into the SurveyResult.ValueType using a lambda Function<T,R>

        ColumnInfo column = table.getColumn(columnName);
        if (column == null)
        {
            errors.add("Unable to find column '" + columnName + "' in list '" + table.getName() + "'");
        }
        else if ((resultType.getDefaultJdbcType() == JdbcType.TIMESTAMP && (column.getJdbcType() == JdbcType.TIMESTAMP || column.getJdbcType() == JdbcType.DATE))
            || (resultType.getDefaultJdbcType() == JdbcType.INTEGER && (column.getJdbcType() == JdbcType.INTEGER || column.getJdbcType() == JdbcType.DOUBLE)))
        {
            // TODO this seems not quite right. We don't have any INTEGER fields, though perhaps we should.
            //Some columns storage types require info not included in result so can't match here
        }
        else if (column.getJdbcType() != resultType.getDefaultJdbcType())
        {
            errors.add("Type '" + resultType.getResultTypeString() + "' (" + resultType.getDefaultJdbcType() + ") of result '" + columnName + "' does not match expected type (" + column.getJdbcType() + ")");
        }

        return errors.isEmpty();
    }

    /**
     * Inserts a row for the given list table to add the data provided
     * @param table the list table in which data is to be stored
     * @param data the collection of data elements for the new row
     * @param container the container in which the list (table) lives
     * @param user the user inserting data into the list
     * @return the newly created row
     * @throws Exception if the table has no update service or there is any other problem inserting the new row
     */
    private Map<String, Object> storeListData(@NotNull TableInfo table, @NotNull Map<String, Object> data, @NotNull Container container, @Nullable User user) throws Exception
    {
        // Add an entry to the survey list and get the id.
        BatchValidationException exception = new BatchValidationException();

        if (table.getUpdateService() == null)
            throw new NotFoundException("Unable to get update service for table " + table.getName());
        List<Map<String, Object>> rows = table.getUpdateService().insertRows(user, container, Collections.singletonList(data), exception, null, null);
        if (exception.hasErrors())
            throw exception;
        else
            return rows.get(0);
    }

    /**
     * Stores a set of SurveyResult objects in a given list. For each given SurveyResult that is single-valued, store it
     * in the column with the corresponding name.
     *
     * @param activityId identifier of the survey
     * @param listName name of the list in which the responses should be stored
     * @param results the superset of results to be stored.  This may contain multi-valued results as well, but these
     *                will not be handled in this method
     * @param data contains the initial set of data to be stored in the row (primarily the parent key field).
     * @param errors the set of errors accumulated thus far, which will be appended with errors encountered for storing these results
     * @param container the container in which the list lives
     * @param user the user to do the insert
     * @param participantId of respondent
     * @return the newly created list row
     * @throws Exception if there is a problem finding or updating the appropriate lists
     */
    private Map<String, Object> storeListResults(@Nullable Integer activityId, @NotNull String listName, @NotNull List<SurveyResult> results, @NotNull Map<String, Object> data, @NotNull List<String> errors, @NotNull Container container, @NotNull User user, @NotNull Integer participantId) throws Exception
    {
        TableInfo surveyTable = getResultTable(listName, container, user);
        if (surveyTable.getUpdateService() == null)
        {
            errors.add("No update service available for the given survey table: " + listName);
            return Collections.emptyMap();
        }

        // find all the single-value results, check if they are in the list, check the type, and add them to the data map if everything is good
        List<SurveyResult> singleValuedResults = getSingleValuedResults(surveyTable, results, errors);
        if (!errors.isEmpty())
            return Collections.emptyMap();

        for (SurveyResult result: singleValuedResults)
            data.put(result.getKey(), result.getParsedValue());

        data.put("participantId", participantId);
        Map<String, Object> row = storeListData(surveyTable, data, container, user);
        if (activityId == null)
            activityId = (Integer) row.get("Key");

        // Add a resultMetadata row for each of the individual rows using the given activityId
        storeResponseMetadata(singleValuedResults, activityId, container, user, participantId);

        return row;
    }

    /**
     * Recursively stores a set of multi-valued results
     * @param results the set of multi-valued results to be stored
     * @param activityId identifier for the survey being processed
     * @param parentKey the key for the list that these multi-valued results are associated with
     * @param errors the collection of validation errors encountered thus far
     * @param container container for the lists
     * @param user user to do the inserts
     * @param participantId of respondent
     * @throws Exception if there is a problem finding or updating the appropriate lists
     */
    private void storeMultiValuedResults(@NotNull List<SurveyResult> results, @NotNull Integer activityId, @NotNull Pair<String, Integer> parentKey, @NotNull List<String> errors, @NotNull Container container, @NotNull User user, @NotNull Integer participantId) throws Exception
    {
        for (SurveyResult result : results)
        {
            if (result.getStepResultType() == SurveyStep.StepResultType.TextChoice)
            {
                storeResultChoices(result, activityId, parentKey, errors, container, user, participantId);
            }
            else // result is of type GROUPED_RESULT
            {
                if (result.getSkipped())
                    storeResponseMetadata(Collections.singletonList(result), activityId, container, user, participantId);
                else
                {
                    // two scenarios, groupedResult is an array of SurveyResult objects or is an array of an array of SurveyResult objects
                    List<List<SurveyResult>> groupedResultList = new ArrayList<>();
                    for (Object gr : (ArrayList) result.getParsedValue())
                    {
                        if (gr instanceof SurveyResult) // this means we have a single set of grouped results to process.
                        {
                            groupedResultList.add((ArrayList) result.getParsedValue());
                            break;
                        }
                        else
                        {
                            groupedResultList.add((ArrayList) gr);
                        }
                    }

                    // store the data for each of the group result sets
                    for (List<SurveyResult> groupResults : groupedResultList)
                    {
                        String listName = result.getListName();
                        Map<String, Object> data = new ArrayListMap<>();
                        data.put(parentKey.getKey(), parentKey.getValue());
                        Map<String, Object> row = storeListResults(activityId, listName, groupResults, data, errors, container, user, participantId);
                        if (!row.isEmpty())
                        {
                            Pair<String, Integer> rowKey = new Pair<>(listName + "Id", (Integer) row.get("Key"));
                            List<SurveyResult> multiValuedResults = getMultiValuedResults(listName, groupResults);
                            storeMultiValuedResults(multiValuedResults, activityId, rowKey, errors, container, user, participantId);
                        }
                    }
                }
            }
        }
    }

    /**
     * Stores a set of values for a choice response
     * @param result the result whose values are being stored
     * @param activityId identifier for the survey being processed
     * @param parentKey the key for the list to which the choice will be associated
     * @param errors the set of validation errors encountered thus far
     * @param container the container for the lists
     * @param user user to do the inserts
     * @param participantId of respondent
     * @throws Exception if there is a problem finding or updating the appropriate lists
     */
    private void storeResultChoices(@NotNull SurveyResult result, @NotNull Integer activityId, @NotNull Pair<String, Integer> parentKey, @NotNull List<String> errors, @NotNull Container container, @NotNull User user, @NotNull Integer participantId) throws Exception
    {
        if (result.getSkipped()) // store only metadata if the response was skipped
            storeResponseMetadata(Collections.singletonList(result), activityId, container, user, participantId);
        else
        {
            TableInfo table = getResultTable(result.getListName(), container, user);
            validateListColumn(table, result.getKey(), SurveyStep.StepResultType.Text, errors);

            if (errors.isEmpty())
            {
                if (result.getParsedValue() != null)
                {
                    for (TextChoiceResult value : (List<TextChoiceResult>) result.getParsedValue())
                    {
                        Map<String, Object> data = new ArrayListMap<>();
                        data.put(parentKey.getKey(), parentKey.getValue());
                        data.put("participantId", participantId);
                        data.put(result.getKey(), value.getValue());
                        if (StringUtils.isNotBlank(value.getOtherText()))
                            data.put(getOtherOptionKey(result.getKey()), value.getOtherText());

                        storeListData(table, data, container, user);
                    }
                }

                storeResponseMetadata(Collections.singletonList(result), activityId, container, user, participantId);
            }
        }
    }

    /**
     * Stores the response metadata for the given results
     * @param results the results whose metadata is to be stored
     * @param activityId the identifier of the survey whose responses are being stored
     * @param container the container in which the lists live
     * @param user the user to be used for inserting the data
     * @param participantId of respondent
     */
    private void storeResponseMetadata(@NotNull List<SurveyResult> results, @NotNull Integer activityId, @NotNull Container container, @NotNull User user, @NotNull Integer participantId)
    {
        for (SurveyResult result : results)
        {
            MobileAppStudySchema schema = MobileAppStudySchema.getInstance();
            TableInfo responseMetadataTable = schema.getTableInfoResponseMetadata();
            result.setActivityId(activityId);
            result.setContainer(container);
            result.setParticipantId(participantId);
            result.setFieldName(result.getKey());

            Table.insert(user, responseMetadataTable, result);
        }
    }

    /**
     * Withdraw a participant from the study based on their apptoken
     * @param participantId to withdraw
     * @param delete True if existing data should also be deleted
     */
    public void withdrawFromStudy(String participantId, boolean delete) throws Exception
    {
        //Get participant
        Participant participant = this.getParticipantFromAppToken(participantId);

        //sanity check, Should already be checked during initial validation
        if (participant == null)
            throw new IllegalStateException("Participant not found");

        DbScope scope = MobileAppStudySchema.getInstance().getSchema().getScope();

        logger.info(String.format("Attempting to%1$s withdraw participant [%2$s] from study",delete ? " delete response data and" : "", participant.getRowId()));
        try (DbScope.Transaction transaction = scope.ensureTransaction())
        {

            //Set participant status
            if (participant.getStatus() != ParticipantStatus.Withdrawn)
            {
                participant.setStatus(ParticipantStatus.Withdrawn);
                participant.setAppToken(null);

                MobileAppStudySchema schema = MobileAppStudySchema.getInstance();
                participant = Table.update(null, schema.getTableInfoParticipant(), participant, participant.getRowId());
            }

            //Delete data if necessary
            if (delete)
                deleteParticipantData(participant);

            transaction.commit();
        }
        catch (Exception e)
        {
            logger.error(String.format("Unable to withdraw participant [%1$s] from study", participant.getRowId()), e);
            throw e;
        }
    }

    /**
     * Delete participant data from schema tables and Survey lists
     * Assumes: all lists in participant container with a participantId field should be deleted from
     * @param participant
     * @throws Exception
     */
    private void deleteParticipantData(Participant participant) throws Exception
    {
        logger.info(String.format("Deleting participant [%1$s]'s data.", participant.getRowId()));
        deleteParticipantDataFromSurveyLists(participant);
        deleteParticipantDataFromTables(participant);
    }

    /**
     * Initiate deletion of participant data from schema tables
     * @param participant
     */
    private void deleteParticipantDataFromTables(Participant participant)
    {
        MobileAppStudySchema schema = MobileAppStudySchema.getInstance();

        deleteParticipantDataFromTable(schema::getTableInfoResponseMetadata, participant.getRowId());
        deleteParticipantDataFromTable(schema::getTableInfoResponse, participant.getRowId());
        deleteParticipantDataFromTable(schema::getTableInfoEnrollmentToken, participant.getRowId());
    }

    /**
     * Delete data related to participant from a hard table
     * @param tableDelegate Supplier method to get TableInfo from
     * @param participantId to target
     */
    private void deleteParticipantDataFromTable(Supplier<TableInfo> tableDelegate, Integer participantId)
    {
        SimpleFilter filter = new SimpleFilter();
        filter.addCondition(FieldKey.fromParts("ParticipantId"), participantId);
        Table.delete(tableDelegate.get(), filter);
    }

    /**
     * Delete participant data from all lists
     * @param participant to target for deletion
     * @throws Exception
     */
    private void deleteParticipantDataFromSurveyLists(Participant participant) throws Exception
    {
        // Create a LimitedUser to use for checking permissions, wrapping the Guest user
        User user = new LimitedUser(UserManager.getGuestUser(),
                new int[0], Collections.singleton(RoleManager.getRole(EditorRole.class)), false);

        //Get all lists in the container
        Collection<ListDefinition> lists = ListService.get().getLists(participant.getContainer()).values();

        for (ListDefinition list :lists)
            deleteParticipantFromList(list, participant.getContainer(), participant.getRowId(), user);
    }

    /**
     * Delete participant data from a list
     * @param list ListDefinition to delete data from
     * @param container hosting list and participant
     * @param participantId to target
     * @param user LabKey user executing the deletion action
     * @throws Exception
     */
    private void deleteParticipantFromList(ListDefinition list, Container container, Integer participantId, User user) throws Exception
    {
        //Get the table
        TableInfo table = list.getTable(user, container);
        if (table == null)
            throw new NotFoundException("Unable to find table for list '" + list.getName() + "' in container '" + container.getName() + "'");

        List<ColumnInfo> piCols = table.getColumns("ParticipantId");
        if (piCols == null || piCols.size() == 0)
            return;     //No participantId column in list.

        QueryUpdateService qus = table.getUpdateService();
        if (qus == null)
            throw new NotFoundException("Unable to delete participant data because update service for list " + table.getName() + " was null");

        //Get rowIds associated to this participant
        List<Map<String, Object>> rows = getListRowKeys(table, participantId);
        if (rows != null && rows.size() > 0)
            qus.deleteRows(user, container, rows, null,null);
    }

    /**
     * Get the rowIds associated to a participant
     * @param targetTable table to query
     * @param participantId to filter for
     * @return
     * @throws Exception
     */
    private List<Map<String, Object>> getListRowKeys(TableInfo targetTable, int participantId) throws Exception
    {
        List<Map<String, Object>> deleteKeys = new ArrayList<>();

        SimpleFilter filter = new SimpleFilter(FieldKey.fromParts("ParticipantId"), participantId);
        try (Results targetDeletes = new TableSelector(targetTable, targetTable.getPkColumns(), filter, null).getResults(false))
        {
            while (targetDeletes.next())
            {
                Map<String, Object> key = new HashMap<>();
                for (Map.Entry<FieldKey, Object> entry : targetDeletes.getFieldKeyRowMap().entrySet())
                {
                    key.put(entry.getKey().toString(), entry.getValue());
                }
                deleteKeys.add(key);
            }
        }

        return deleteKeys;
    }

    public boolean isKnownVersion(String appToken, String activityId, String versionId, int responseId)
    {
        /* --Equivalent postgres SQL
        SELECT EXISTS(
            SELECT 1
            FROM participant p, mobileappstudy.participant p2, mobileappstudy.response r
            WHERE p2.apptoken = ?
              AND p2.studyid = p.studyid
              AND p.rowid = r.participantid
              AND r.rowid != ?
              AND r.activityid = ?
              AND r.surveyversion = ?
              AND r.status = 1
        )
        */
        MobileAppStudySchema schema = MobileAppStudySchema.getInstance();

        SQLFragment sql = new SQLFragment();
        sql.append("  SELECT 1\n")
            .append("  FROM ")
                .append(schema.getTableInfoParticipant(), "p").append(",")
                .append(schema.getTableInfoParticipant(), "p2").append(",")
                .append(schema.getTableInfoResponse(), "r").append("\n")
            .append("  WHERE p.rowid = r.participantid\n")                  //Join response & participant
            .append("    AND p.studyid = p2.studyid\n")                     //Limit study
            .append("    AND p2.apptoken = ?\n").add(appToken)
            .append("    AND r.rowid != ?\n").add(responseId)               //Ignore current response
            .append("    AND r.activityid = ?\n").add(activityId)               //
            .append("    AND r.surveyversion = ?\n").add(versionId)
                //pending implies it hasn't been applied and error means it may not have worked...
            .append("    AND r.status = ?\n").add(ResponseStatus.PROCESSED.getPkId());

        return new SqlSelector(schema.getSchema(), sql).exists();
    }

    public SurveyDesignProvider getSurveyDesignProvider(Container container)
    {
        if (ServiceSurveyDesignProvider.isConfigured(container))
        {
            return new ServiceSurveyDesignProvider(container, logger);
        }
        else if (FileSurveyDesignProvider.getBasePath(container) != null)
        {
            logger.info("Metadata service parameters not configured; using file system as design metadata provider.");
            return new FileSurveyDesignProvider(container, logger);
        }
        else
        {
            logger.error("No SurveyDesignProvider configured.  Please set the appropriate Module Properties.");
            return null;
        }
    }

    public static String getOtherOptionKey(String optionFieldKey)
    {
        return optionFieldKey + OTHER_OPTION_TITLE;
    }

    private void enqueueForwardingJob(final User user,final Container container)
    {
        SurveyResponseForwardingJob forwarder = new SurveyResponseForwardingJob();

        try
        {
            forwarder.call(user, container);
        }
        catch (Exception e)
        {
            forwarder.setUnsuccessful(container);
        }
    }

    public void setForwardingJobUnsucessful(Container c)
    {
        SurveyResponseForwardingJob forwarder = new SurveyResponseForwardingJob();
        forwarder.setUnsuccessful(c);
    }

    public Map<String, String> getForwardingProperties(Container container)
    {
        return new ForwarderProperties().getForwarderConnection(container);
    }

    public void setForwarderConfiguration(Container container, MobileAppStudyController.ForwardingSettingsForm form)
    {
        logger.info( String.format("Updating forwarder configuration for container: %1$s", container.getName()));
        form.getForwardingType().setForwardingProperties(container, form);

        ForwardingScheduler.get().enableContainer(container, form.getForwardingType() != ForwardingType.Disabled);
    }

    /**
     * Get set of containers
     * @return List of container id strings
     */
    public List<String> getStudyContainers()
    {
        MobileAppStudySchema schema = MobileAppStudySchema.getInstance();
        TableSelector selector = new TableSelector(schema.getTableInfoStudy(), Collections.singleton("Container"), null, null);
        return selector.getArrayList(String.class);
    }

    public String getEnrollmentToken(Container container, Integer participantId)
    {
        FieldKey fkey = FieldKey.fromParts("ParticipantId");
        SimpleFilter filter = SimpleFilter.createContainerFilter(container);
        filter.addCondition(fkey, participantId);
        ColumnInfo column = MobileAppStudySchema.getInstance().getTableInfoEnrollmentToken().getColumn("Token");
        return new TableSelector(column, filter, null).getObject(String.class);
    }

    public boolean hasResponsesToForward(@NotNull Container container)
    {
        FieldKey fkey = FieldKey.fromParts("Status");
        SimpleFilter filter = SimpleFilter.createContainerFilter(container);
        filter.addCondition(fkey, ResponseStatus.PROCESSED.getPkId());
        return new TableSelector(MobileAppStudySchema.getInstance().getTableInfoResponse(), filter, null)
                .getRowCount() > 0;
    }

    public boolean isForwardingEnabled(Container container)
    {
        return ForwardingType.Disabled != ForwarderProperties.getForwardingType(container);
    }
}