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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.labkey.api.action.Action;
import org.labkey.api.action.ActionType;
import org.labkey.api.action.ApiQueryResponse;
import org.labkey.api.action.Marshal;
import org.labkey.api.action.Marshaller;
import org.labkey.api.action.MutatingApiAction;
import org.labkey.api.action.ReadOnlyApiAction;
import org.labkey.api.action.ReportingApiQueryResponse;
import org.labkey.api.action.SimpleViewAction;
import org.labkey.api.action.SpringActionController;
import org.labkey.api.data.DataRegion;
import org.labkey.api.data.DataRegionSelection;
import org.labkey.api.query.QueryForm;
import org.labkey.api.query.QuerySettings;
import org.labkey.api.query.QueryView;
import org.labkey.api.query.TempQuerySettings;
import org.labkey.api.query.UserSchema;
import org.labkey.api.security.CSRF;
import org.labkey.api.security.RequiresNoPermission;
import org.labkey.api.security.RequiresPermission;
import org.labkey.api.security.User;
import org.labkey.api.security.permissions.AdminPermission;
import org.labkey.api.util.PageFlowUtil;
import org.labkey.api.view.ActionURL;
import org.labkey.api.view.FolderManagement.FolderManagementViewPostAction;
import org.labkey.api.view.HttpView;
import org.labkey.api.view.JspView;
import org.labkey.api.view.NavTree;
import org.labkey.api.view.ViewContext;
import org.labkey.mobileappstudy.data.EnrollmentTokenBatch;
import org.labkey.mobileappstudy.data.MobileAppStudy;
import org.labkey.mobileappstudy.data.Participant;
import org.labkey.mobileappstudy.data.SurveyMetadata;
import org.labkey.mobileappstudy.data.SurveyResponse;
import org.labkey.mobileappstudy.forwarder.ForwardingType;
import org.labkey.mobileappstudy.query.ReadResponsesQuerySchema;
import org.labkey.mobileappstudy.surveydesign.FileSurveyDesignProvider;
import org.labkey.mobileappstudy.surveydesign.InvalidDesignException;
import org.labkey.mobileappstudy.view.EnrollmentTokenBatchesWebPart;
import org.labkey.mobileappstudy.view.EnrollmentTokensWebPart;
import org.springframework.beans.PropertyValues;
import org.springframework.validation.BindException;
import org.springframework.validation.Errors;
import org.springframework.web.servlet.ModelAndView;

import java.util.Set;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Marshal(Marshaller.Jackson)
public class MobileAppStudyController extends SpringActionController
{
    private static final Logger LOG = Logger.getLogger(MobileAppStudyController.class);
    private static final DefaultActionResolver _actionResolver = new DefaultActionResolver(MobileAppStudyController.class);

    public static final String NAME = "mobileappstudy";

    public MobileAppStudyController()
    {
        setActionResolver(_actionResolver);
    }

    public ActionURL getEnrollmentTokenBatchURL()
    {
        return new ActionURL(TokenBatchAction.class, getContainer());
    }

    /**
     * This action is used only for testing purposes.  It relies on the configuration of the SurveyMetadataDir module property.
     * It will read the file corresponding to the given query parameters and serve up a JSON response by reading the corresponding
     * file in the configured directory.
     */
    @RequiresNoPermission
    public class ActivityMetadataAction extends ReadOnlyApiAction<ActivityMetadataForm>
    {
        @Override
        public void validateForm(ActivityMetadataForm form, Errors errors)
        {
            if (FileSurveyDesignProvider.getBasePath(getContainer()) == null)
            {
                errors.reject(ERROR_REQUIRED, "No SurveyMetadataDirectory configured. Please set the appropriate Module Properties.");
                return;
            }

            if (form.getStudyId() == null)
                errors.reject(ERROR_REQUIRED, "studyId is a required parameter");
            if (form.getActivityId() == null)
                errors.reject(ERROR_REQUIRED, "activityId is a required parameter");
            if (form.getActivityVersion() == null)
                errors.reject(ERROR_REQUIRED, "activityVersion is a required parameter");
        }

        @Override
        public Object execute(ActivityMetadataForm form, BindException errors) throws InvalidDesignException
        {
            logger.info("Processing request with Authorization header: " + getViewContext().getRequest().getHeader("Authorization"));
            FileSurveyDesignProvider provider = new FileSurveyDesignProvider(getContainer(), logger);
            return provider.getSurveyDesign(getContainer(), form.getStudyId(), form.getActivityId(), form.getActivityVersion());
        }
    }

    @RequiresPermission(AdminPermission.class)
    public class TokenBatchAction extends SimpleViewAction
    {
        @Override
        public NavTree appendNavTrail(NavTree root)
        {
            return root;
        }

        @Override
        public ModelAndView getView(Object o, BindException errors)
        {
            setTitle("Enrollment Token Batches");
            return new EnrollmentTokenBatchesWebPart(getViewContext());
        }
    }

    @RequiresPermission(AdminPermission.class)
    public class TokenListAction extends SimpleViewAction
    {
        @Override
        public NavTree appendNavTrail(NavTree root)
        {
            return root.addChild("Token Batches", getEnrollmentTokenBatchURL()).addChild("Enrollment Tokens");
        }

        @Override
        public ModelAndView getView(Object o, BindException errors)
        {
            setTitle("Enrollment Tokens");
            return new EnrollmentTokensWebPart(getViewContext());
        }
    }

    @RequiresPermission(AdminPermission.class)
    public class GenerateTokensAction extends MutatingApiAction<GenerateTokensForm>
    {
        @Override
        public void validateForm(GenerateTokensForm form, Errors errors)
        {
            if (form == null)
                errors.reject(ERROR_MSG, "Invalid input format. Please check the log for errors.");
            else if (form.getCount() == null || form.getCount() <= 0)
                errors.reject(ERROR_MSG, "Count must be provided and greater than 0.");
        }

        @Override
        public Object execute(GenerateTokensForm form, BindException errors)
        {
            EnrollmentTokenBatch batch = MobileAppStudyManager.get().createTokenBatch(form.getCount(), getUser(), getContainer());

            return success(PageFlowUtil.map("batchId", batch.getRowId()));
        }
    }

    @RequiresPermission(AdminPermission.class)
    public class StudyConfigAction extends MutatingApiAction<StudyConfigForm>
    {
        @Override
        public void validateForm(StudyConfigForm form, Errors errors)
        {
            MobileAppStudy study = MobileAppStudyManager.get().getStudy(getContainer());
            if (form == null)
                errors.reject(ERROR_MSG, "Invalid input format.  Please check the log for errors.");
            else if (StringUtils.isEmpty(form.getShortName()))
                errors.reject(ERROR_REQUIRED, "StudyId must be provided.");
            else if (MobileAppStudyManager.get().studyExistsAsSibling(form.getShortName(), getContainer()))
                errors.rejectValue("shortName", ERROR_MSG, "StudyId '" + form.getShortName() + "' is already associated with a different container within this folder. Each study can be associated with only one container per folder.");
            //Check if study exists, name has changed, and at least one participant has enrolled
            else if (study != null && !study.getShortName().equals(form.getShortName()) && MobileAppStudyManager.get().hasStudyParticipants(getContainer()))
                errors.rejectValue("shortName", ERROR_MSG, "This container already has a study with participant data associated with it.  Each container can be configured with only one study and cannot be reconfigured once participant data is present.");
        }

        @Override
        public Object execute(StudyConfigForm form, BindException errors)
        {
            // if submitting again with the same id in the same container, return the existing study object
            MobileAppStudy study = MobileAppStudyManager.get().getStudy(getContainer());
            if (study == null || !study.getShortName().equals(form.getShortName()) || study.getCollectionEnabled() != form.getCollectionEnabled())
                study = MobileAppStudyManager.get().insertOrUpdateStudy(form.getShortName(), form.getCollectionEnabled(), getContainer(), getUser());

            return success(PageFlowUtil.map(
                "rowId", study.getRowId(),
                "studyId", study.getShortName(),
                "collectionEnabled", study.getCollectionEnabled()
            ));
        }
    }

    /**
     * Ignores the request container. Pulls container context from the appToken used in request
     */
    @RequiresNoPermission
    @CSRF(CSRF.Method.NONE) // No need for CSRF token; request includes a secret (the app token). Plus, mobile app has no ability to provide CSRF token.
    public class ProcessResponseAction extends MutatingApiAction<ResponseForm>
    {
        @Override
        public void validateForm(ResponseForm form, Errors errors)
        {
            //Check if form is valid
            if (form == null)
            {
                errors.reject(ERROR_MSG, "Please check the log for errors.");
                return;
            }

            form.validate(errors);
        }

        @Override
        public Object execute(ResponseForm form, BindException errors)
        {
            //Record response blob
            MobileAppStudyManager manager = MobileAppStudyManager.get();
            //Null checks are done in the validate method
            SurveyResponse resp = new SurveyResponse(
                    form.getParticipantId(),
                    form.getData().toString(),
                    form.getMetadata().getActivityId(),
                    form.getMetadata().getVersion()
            );
            resp = manager.insertResponse(resp);

            //Add a parsing job
            final Integer rowId = resp.getRowId();
            manager.enqueueSurveyResponse(() -> MobileAppStudyManager.get().shredSurveyResponse(rowId, getUser()));

            return success();
        }
    }

    /**
     * Ignores request container. Pulls container context from the appToken used in request
     */
    @RequiresNoPermission
    @CSRF(CSRF.Method.NONE) // No need for CSRF token; request includes a secret (the app token). Plus, mobile app has no ability to provide CSRF token.
    public class WithdrawFromStudyAction extends MutatingApiAction<WithdrawFromStudyForm>
    {
        @Override
        public void validateForm(WithdrawFromStudyForm form, Errors errors)
        {
            //Check if form is valid
            if (form == null)
            {
                errors.reject(ERROR_MSG, "Please check the log for errors.");
                return;
            }

            if (StringUtils.isBlank(form.getParticipantId()))
                errors.reject(ERROR_REQUIRED, "ParticipantId not included in request");
            else if(!MobileAppStudyManager.get().participantExists(form.getParticipantId()))
                errors.reject(ERROR_REQUIRED, "Invalid ParticipantId.");
        }

        @Override
        public Object execute(WithdrawFromStudyForm form, BindException errors) throws Exception
        {
            MobileAppStudyManager.get().withdrawFromStudy(form.getParticipantId(), form.isDelete());
            return success();
        }
    }

    private abstract class BaseEnrollmentAction extends MutatingApiAction<EnrollmentForm>
    {
        @Override
        public void validateForm(EnrollmentForm form, Errors errors)
        {
            if (form == null)
            {
                errors.reject(ERROR_MSG, "Invalid input format.");
            }
            else
            {
                if (StringUtils.isEmpty(form.getShortName()))
                    //StudyId typically refers to the Study.rowId, however in this context it is the Study.shortName.  Issue #28419
                    errors.reject(ERROR_REQUIRED, "StudyId is required");
                else if (!MobileAppStudyManager.get().studyExists(form.getShortName()))
                    errors.rejectValue("studyId", ERROR_MSG, "Study with StudyId '" + form.getShortName() + "' does not exist");
                else if (StringUtils.isNotEmpty(form.getToken()))
                {
                    if (MobileAppStudyManager.get().hasParticipant(form.getShortName(), form.getToken()))
                        errors.reject(ERROR_MSG, "Token already in use");
                    else if (!MobileAppStudyManager.get().isChecksumValid(form.getToken()))
                        errors.rejectValue("token", ERROR_MSG, "Invalid token: '" + form.getToken() + "'");
                    else if (!MobileAppStudyManager.get().isValidStudyToken(form.getToken(), form.getShortName()))
                        errors.rejectValue("token", ERROR_MSG, "Unknown token: '" + form.getToken() + "'");
                }
                // we allow for the possibility that someone can enroll without using an enrollment token
                else if (MobileAppStudyManager.get().enrollmentTokenRequired(form.getShortName()))
                {
                    errors.reject(ERROR_REQUIRED, "Token is required");
                }
            }
        }
    }

    /**
     * Execute the validation steps for an enrollment token without enrolling
     */
    @RequiresNoPermission
    @CSRF(CSRF.Method.NONE) // No need for CSRF token; request includes a secret (the enrollment token). Plus, mobile app has no ability to provide CSRF token.
    public class ValidateEnrollmentTokenAction extends BaseEnrollmentAction
    {
        @Override
        public Object execute(EnrollmentForm enrollmentForm, BindException errors)
        {
            //If action passes validation then it was successful
            return success();
        }
    }

    @RequiresNoPermission
    @CSRF(CSRF.Method.NONE) // No need for CSRF token; request includes a secret (the enrollment token). Plus, mobile app has no ability to provide CSRF token.
    public class EnrollAction extends BaseEnrollmentAction
    {
        @Override
        public Object execute(EnrollmentForm enrollmentForm, BindException errors)
        {
            Participant participant = MobileAppStudyManager.get().enrollParticipant(enrollmentForm.getShortName(), enrollmentForm.getToken());
            return success(PageFlowUtil.map("appToken", participant.getAppToken()));
        }
    }

    @RequiresPermission(AdminPermission.class)
    public class ReprocessResponseAction extends MutatingApiAction<ReprocessResponseForm>
    {
        private Set<Integer> _ids;

        @Override
        public void validateForm(ReprocessResponseForm form, Errors errors)
        {
            if (form == null)
            {
                errors.reject(ERROR_MSG, "Invalid input format.");
                return;
            }

            Set<String> listIds = DataRegionSelection.getSelected(getViewContext(), form.getKey(), true, false);
            _ids = listIds.stream().map(Integer::valueOf).collect(Collectors.toSet());

            if (_ids.isEmpty())
                errors.reject(ERROR_REQUIRED, "No responses to reprocess");
        }

        @Override
        public Object execute(ReprocessResponseForm form, BindException errors)
        {
            Set<Integer> nonErrorIds = MobileAppStudyManager.get().getNonErrorResponses(_ids);
            int enqueued = MobileAppStudyManager.get().reprocessResponses(getUser(), _ids);

            return success(PageFlowUtil.map("countReprocessed", enqueued, "notReprocessed", nonErrorIds));
        }
    }

    private abstract class BaseQueryAction<FORM extends SelectRowsForm> extends ReadOnlyApiAction<FORM>
    {
        @Override
        public final @NotNull BindException defaultBindParameters(FORM form, PropertyValues params)
        {
            ParticipantForm participantForm = new ParticipantForm();
            BindException exception = defaultBindParameters(participantForm, getCommandName(), getPropertyValues());

            if (!exception.hasErrors())
                exception = super.defaultBindParameters(form, params);

            if (!exception.hasErrors())
                form.setParticipantForm(participantForm);

            return exception;
        }

        @Override
        public void validateForm(FORM form, Errors errors)
        {
            super.validateForm(form, errors);

            // Error when binding means null ParticipantForm, #33486
            if (!errors.hasErrors())
            {
                form.getParticipantForm().validateForm(errors);

                if (!errors.hasErrors())
                {
                    // Set our special, filtered schema on the form so getQuerySettings() works right
                    UserSchema schema = ReadResponsesQuerySchema.get(form.getParticipant());
                    form.setSchema(schema);
                }
            }
        }

        @Override
        public final Object execute(FORM form, BindException errors)
        {
            Participant participant = form.getParticipant();

            // ApiQueryResponse constructs a DataView that initializes its ViewContext from the root context, so we need
            // to modify the root with a read-everywhere user and the study container.
            ViewContext root = HttpView.getRootContext();

            // Shouldn't be null, but just in case
            if (null != root)
            {
                // Setting a ContextualRole would be cleaner, but HttpView.initViewContext() doesn't copy it over
                root.setUser(User.getSearchUser());
                root.setContainer(participant.getContainer());
            }

            return getResponse(form, errors);
        }

        @Override
        protected String getCommandClassMethodName()
        {
            return "getResponse";
        }

        abstract public ApiQueryResponse getResponse(FORM form, BindException errors);
    }

    @RequiresNoPermission
    @Action(ActionType.SelectData.class)
    public class SelectRowsAction extends BaseQueryAction<SelectRowsForm>
    {
        @Override
        public void validateForm(SelectRowsForm form, Errors errors)
        {
            super.validateForm(form, errors);

            if (!errors.hasErrors())
            {
                String queryName = StringUtils.trimToNull(form.getQueryName());

                if (null == queryName)
                    errors.reject(ERROR_REQUIRED, "No value was supplied for the required parameter 'queryName'");
                else if (null == form.getSchema().getTable(queryName))
                    errors.reject(ERROR_MSG, "Query '" + queryName + "' doesn't exist");
            }
        }

        @Override
        public ApiQueryResponse getResponse(SelectRowsForm form, BindException errors)
        {
            UserSchema schema = form.getSchema();

            // First parameter (ViewContext) is ignored, so just pass null
            QueryView view = QueryView.create(null, schema, form.getQuerySettings(), errors);

            return new ApiQueryResponse(view, false, true,
                    schema.getName(), form.getQueryName(), form.getQuerySettings().getOffset(), null,
                    false, false, false, false, false);
        }
    }

    @RequiresNoPermission
    @Action(ActionType.SelectData.class)
    public class ExecuteSqlAction extends BaseQueryAction<ExecuteSqlForm>
    {
        private String _sql;

        @Override
        public void validateForm(ExecuteSqlForm form, Errors errors)
        {
            super.validateForm(form, errors);

            _sql = StringUtils.trimToNull(form.getSql());
            if (null == _sql)
                errors.reject(ERROR_REQUIRED, "No value was supplied for the required parameter 'sql'");
        }

        @Override
        public ApiQueryResponse getResponse(ExecuteSqlForm form, BindException errors)
        {
            //create a temp query settings object initialized with the posted LabKey SQL
            //this will provide a temporary QueryDefinition to Query
            QuerySettings settings = new TempQuerySettings(getViewContext(), _sql, form.getQuerySettings());

            //need to explicitly turn off various UI options that will try to refer to the
            //current URL and query string
            settings.setAllowChooseView(false);
            settings.setAllowCustomizeView(false);

            //build a query view using the schema and settings
            QueryView view = new QueryView(form.getSchema(), settings, errors);
            view.setShowRecordSelectors(false);
            view.setShowExportButtons(false);
            view.setButtonBarPosition(DataRegion.ButtonBarPosition.NONE);
            view.setShowPagination(false);

            return new ReportingApiQueryResponse(view, false, false, "sql", 0, null, false, false, false, false);
        }
    }

    public static class SelectRowsForm extends QueryForm
    {
        private UserSchema _userSchema = null;
        private ParticipantForm _participantForm = null;

        void setSchema(UserSchema userSchema)
        {
            _userSchema = userSchema;
        }

        @NotNull
        @Override
        public UserSchema getSchema()
        {
            return _userSchema;
        }

        Participant getParticipant()
        {
            return _participantForm.getParticipant();
        }

        ParticipantForm getParticipantForm()
        {
            return _participantForm;
        }

        void setParticipantForm(ParticipantForm participantForm)
        {
            _participantForm = participantForm;
        }
    }

    public static class ExecuteSqlForm extends SelectRowsForm
    {
        private String _sql;

        public String getSql()
        {
            return _sql;
        }

        public void setSql(String sql)
        {
            _sql = sql;
        }
    }

    public static class ReprocessResponseForm
    {
        private String _key;

        public String getKey()
        {
            return _key;
        }
        public void setKey(String key)
        {
            _key = key;
        }
    }


    public static class StudyConfigForm
    {
        private String _shortName;
        private boolean _collectionEnabled;

        public String getShortName()
        {
            return _shortName;
        }

        public void setShortName(String shortName)
        {
            _shortName = shortName;
        }

        public boolean getCollectionEnabled() {
            return _collectionEnabled;
        }
        public void setCollectionEnabled(boolean collectionEnabled) {
            _collectionEnabled = collectionEnabled;
        }

        //StudyId typically refers to the Study.rowId, however in this context it is the Study.shortName.  Issue #28419
        //Adding this since it could potentially be exposed
        public void setStudyId(String studyId)
        {
            setShortName(studyId);
        }

        public String getStudyId()
        {
            return _shortName;
        }
    }

    public static class EnrollmentForm
    {
        private String _token;
        private String _shortName;

        public String getToken()
        {
            return _token;
        }
        public void setToken(String token)
        {
            _token = isBlank(token) ? null : token.trim().toUpperCase();
        }

        public String getShortName()
        {
            return _shortName;
        }
        public void setShortName(String shortName)
        {
            _shortName = isBlank(shortName) ? null : shortName.trim().toUpperCase();
        }

        //StudyId typically refers to the Study.rowId, however in this context it is the Study.shortName.  Issue #28419
        public void setStudyId(String studyId)
        {
            setShortName(studyId);
        }

        public String getStudyId()
        {
            return _shortName;
        }
    }

    public static class WithdrawFromStudyForm
    {
        private String _participantId;
        private boolean _delete;

        public boolean isDelete()
        {
            return _delete;
        }
        public void setDelete(boolean delete)
        {
            _delete = delete;
        }

        public String getParticipantId()
        {
            return _participantId;
        }
        public void setParticipantId(String participantId)
        {
            _participantId = participantId;
        }
    }

    public static class GenerateTokensForm
    {
        private Integer _count;

        public Integer getCount()
        {
            return _count;
        }

        public void setCount(Integer count)
        {
            _count = count;
        }
    }

    public static class ParticipantForm
    {
        private String _appToken;

        // Filled in by successful validate()
        protected Participant _participant;
        protected MobileAppStudy _study;

        //ParticipantId from JSON request is really the apptoken internally

        @JsonIgnore
        public String getAppToken()
        {
            return getParticipantId();
        }

        //ParticipantId from JSON request is really the apptoken internally
        public String getParticipantId()
        {
            return _appToken;
        }

        public void setParticipantId(String appToken)
        {
            _appToken = appToken;
        }

        public final void validate(Errors errors)
        {
            validateForm(errors);

            if (errors.hasErrors())
                LOG.error("Problem processing participant request: " + errors.getAllErrors().toString());
        }

        protected void validateForm(Errors errors)
        {
            String appToken = getAppToken();

            if (StringUtils.isBlank(appToken))
            {
                errors.reject(ERROR_REQUIRED, "ParticipantId not included in request");
            }
            else
            {
                //Check if there is an associated participant for the appToken
                _participant = MobileAppStudyManager.get().getParticipantFromAppToken(appToken);

                if (_participant == null)
                    errors.reject(ERROR_MSG, "Unable to identify participant");
                else if (Participant.ParticipantStatus.Withdrawn == _participant.getStatus())
                    errors.reject(ERROR_MSG, "Participant has withdrawn from study");
                else
                {
                    //Check if there is an associated study for the appToken
                    _study = MobileAppStudyManager.get().getStudyFromParticipant(_participant);
                    if (_study == null)
                        errors.reject(ERROR_MSG, "AppToken not associated with study");
                }
            }

            // If we have participant and study then we shouldn't have errors (and vice versa)
            assert (null != _participant && null != _study) == !errors.hasErrors();
        }

        @JsonIgnore
        public Participant getParticipant()
        {
            return _participant;
        }
    }

    public static class ResponseForm extends ParticipantForm
    {
        private String _type; // Unused, but don't delete... Jackson binding against our test responses goes crazy without it
        private JsonNode _data;
        private SurveyMetadata _metadata;

        public SurveyMetadata getMetadata()
        {
            return _metadata;
        }
        public void setMetadata(@NotNull SurveyMetadata metadata)
        {
            _metadata = metadata;
        }

        public JsonNode getData()
        {
            return _data;
        }
        public void setData(@NotNull JsonNode data)
        {
            _data = data;
        }

        public String getType()
        {
            return _type;
        }

        public void setType(String type)
        {
            _type = type;
        }

        @Override
        protected void validateForm(Errors errors)
        {
            // First, check if form's required fields are present
            SurveyMetadata info = getMetadata();

            if (info == null)
            {
                errors.reject(ERROR_REQUIRED, "Metadata not found");
            }
            else
            {
                super.validateForm(errors);

                if (!errors.hasErrors())
                {
                    if (isBlank(info.getActivityId()))
                        errors.reject(ERROR_REQUIRED, "ActivityId not included in request");
                    if (isBlank(info.getVersion()))
                        errors.reject(ERROR_REQUIRED, "SurveyVersion not included in request");
                    if (getData() == null)
                        errors.reject(ERROR_REQUIRED, "Response not included in request");
                    if (!_study.getCollectionEnabled())
                        errors.reject(ERROR_MSG, String.format("Response collection is not currently enabled for study [ %1s ]", _study.getShortName()));
                }
            }
        }
    }

    public static class ActivityMetadataForm
    {
        private String studyId;
        private String activityId;
        private String activityVersion;

        public String getStudyId()
        {
            return studyId;
        }

        public void setStudyId(String studyId)
        {
            this.studyId = studyId;
        }

        public String getActivityId()
        {
            return activityId;
        }

        public void setActivityId(String activityId)
        {
            this.activityId = activityId;
        }

        public String getActivityVersion()
        {
            return activityVersion;
        }

        public void setActivityVersion(String activityVersion)
        {
            this.activityVersion = activityVersion;
        }
    }

    @RequiresPermission(AdminPermission.class)
    public static class ForwardingSettingsAction extends FolderManagementViewPostAction<ForwardingSettingsForm>
    {
        @Override
        protected HttpView getTabView(ForwardingSettingsForm form, boolean reshow, BindException errors)
        {
            return new JspView<>("/org/labkey/mobileappstudy/view/forwarderSettings.jsp", form, errors);
        }

        @Override
        public void validateCommand(ForwardingSettingsForm form, Errors errors)
        {
            form.getForwardingType().validateConfig(form, errors);
        }

        @Override
        public boolean handlePost(ForwardingSettingsForm form, BindException errors)
        {
            MobileAppStudyManager.get().setForwarderConfiguration(getContainer(), form);
            return true;
        }
    }

    public static class ForwardingSettingsForm
    {
        private ForwardingType forwardingType = ForwardingType.Disabled;
        private String basicURL;
        private String username;
        private String password;
        private String tokenRequestURL;
        private String tokenField;
        private String header;
        private String oauthURL;

        public ForwardingType getForwardingType ()
        {
            return forwardingType;
        }

        public void setForwardingType(ForwardingType forwardingType)
        {
            this.forwardingType = forwardingType;
        }

        public String getBasicURL()
        {
            return basicURL;
        }

        public void setBasicURL(String url)
        {
            this.basicURL = url;
        }

        public String getUsername()
        {
            return username;
        }

        public void setUsername(String username)
        {
            this.username = username;
        }

        public String getPassword()
        {
            return password;
        }

        public void setPassword(String password)
        {
            this.password = password;
        }

        public String getTokenRequestURL()
        {
            return tokenRequestURL;
        }

        public void setTokenRequestURL(String tokenRequestURL)
        {
            this.tokenRequestURL = tokenRequestURL;
        }

        public String getTokenField()
        {
            return tokenField;
        }

        public void setTokenField(String tokenField)
        {
            this.tokenField = tokenField;
        }

        public String getHeader()
        {
            return header;
        }

        public void setHeader(String header)
        {
            this.header = header;
        }

        public String getOauthURL()
        {
            return oauthURL;
        }

        public void setOauthURL(String oauthURL)
        {
            this.oauthURL = oauthURL;
        }

    }
}