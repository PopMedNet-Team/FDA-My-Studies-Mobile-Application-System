/*
 * Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors. Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 * Funding Source: Food and Drug Administration (“Funding Agency”) effective 18 September 2014 as Contract no. HHSF22320140030I/HHSF22301006T (the “Prime Contract”).
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */

package com.hphc.mystudies;

import com.hphc.mystudies.FdahpUserRegWSController.DeactivateForm;
import com.hphc.mystudies.bean.ParticipantForm;
import com.hphc.mystudies.bean.ProfileBean;
import com.hphc.mystudies.bean.SettingsBean;
import com.hphc.mystudies.bean.StudiesBean;
import com.hphc.mystudies.model.AppPropertiesDetails;
import com.hphc.mystudies.model.AuthInfo;
import com.hphc.mystudies.model.FdahpUserRegUtil;
import com.hphc.mystudies.model.LoginAttempts;
import com.hphc.mystudies.model.ParticipantActivities;
import com.hphc.mystudies.model.ParticipantStudies;
import com.hphc.mystudies.model.PasswordHistory;
import com.hphc.mystudies.model.StudyConsent;
import com.hphc.mystudies.model.UserAppDetails;
import com.hphc.mystudies.model.UserDetails;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.labkey.api.action.ApiSimpleResponse;
import org.labkey.api.audit.AuditLogService;
import org.labkey.api.data.AuditConfigurable;
import org.labkey.api.data.Container;
import org.labkey.api.data.ContainerManager;
import org.labkey.api.data.DbSchema;
import org.labkey.api.data.DbScope;
import org.labkey.api.data.SQLFragment;
import org.labkey.api.data.SimpleFilter;
import org.labkey.api.data.SqlExecutor;
import org.labkey.api.data.SqlSelector;
import org.labkey.api.data.Table;
import org.labkey.api.data.TableInfo;
import org.labkey.api.data.TableSelector;
import org.labkey.api.gwt.client.AuditBehaviorType;
import org.labkey.api.module.Module;
import org.labkey.api.module.ModuleLoader;
import org.labkey.api.module.ModuleProperty;
import org.labkey.api.query.FieldKey;

import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

public class FdahpUserRegWSManager
{
    private static final FdahpUserRegWSManager _instance = new FdahpUserRegWSManager();

    Properties configProp = FdahpUserRegUtil.getProperties();

    private FdahpUserRegWSManager()
    {
        // prevent external construction with a private default constructor
    }

    public static FdahpUserRegWSManager get()
    {
        return _instance;
    }

    private static final Logger _log = Logger.getLogger(FdahpUserRegWSManager.class);

    /**
     * Saving the user information
     *
     * @param participant
     * @return UserDetails
     */
    public UserDetails saveParticipant(UserDetails participant)
    {
        Container availableContainer = getContainer_AppID(participant.getApplicationId());

        if (availableContainer != null && participant != null)
            participant.setContainer(availableContainer.getId());

        DbScope dbScope = FdahpUserRegWSSchema.getInstance().getSchema().getScope();
        UserDetails addParticipant = null;
        DbScope.Transaction transaction = dbScope.ensureTransaction();
        try
        {
            AuditConfigurable table = (AuditConfigurable) FdahpUserRegWSSchema.getInstance().getParticipantDetails();
            table.setAuditBehavior(AuditBehaviorType.DETAILED);
            if (null != participant && participant.getId() == null)
            {
                addParticipant = Table.insert(null, table, participant);
            }
            else
            {
                addParticipant = Table.update(null, table, participant, participant.getId());
            }

        }
        catch (Exception e)
        {
            _log.error("saveParticipant:", e);
        }
        transaction.commit();
        return addParticipant;
    }

    /**
     * Get the user details
     *
     * @param id
     * @return UserDetails
     */
    public UserDetails getParticipantDetails(String id, String appId, String orgId)
    {
        SimpleFilter filter = new SimpleFilter();
        filter.addCondition(FieldKey.fromParts("UserId"), id);
        filter.addCondition(FieldKey.fromParts("ApplicationId"), appId);
        filter.addCondition(FieldKey.fromParts("OrgId"), orgId);
        return new TableSelector(FdahpUserRegWSSchema.getInstance().getParticipantDetails(), filter, null).getObject(UserDetails.class);
    }

    /**
     * Get the user by email
     *
     * @param email
     * @return List {@link UserDetails}
     */
    public List<UserDetails> getParticipantDetailsListByEmail(String email, String applicationId, String orgId)
    {
        SimpleFilter filter = new SimpleFilter();
        filter.addCondition(FieldKey.fromParts("Email"), email);
        filter.addCondition(FieldKey.fromParts("ApplicationId"), applicationId);
        filter.addCondition(FieldKey.fromParts("OrgId"), orgId);
        return new TableSelector(FdahpUserRegWSSchema.getInstance().getParticipantDetails(), filter, null).getArrayList(UserDetails.class);
    }

    /**
     * Save or update of an user auth info
     *
     * @param userId
     * @param isRefresh
     * @return AuthInfo
     */
    public AuthInfo saveAuthInfo(String userId, boolean isRefresh, String applicationId, String orgId)
    {
        Container availableContainer = getContainer_AppID(applicationId);

        DbScope dbScope = FdahpUserRegWSSchema.getInstance().getSchema().getScope();
        UserDetails addParticipant = null;
        DbScope.Transaction transaction = dbScope.ensureTransaction();
        AuthInfo authInfo = null;
        try
        {
            SimpleFilter filter = new SimpleFilter();
            filter.addCondition(FieldKey.fromParts("ParticipantId"), userId);
            filter.addCondition(FieldKey.fromParts("ApplicationId"), applicationId);
            filter.addCondition(FieldKey.fromParts("OrgId"), orgId);
            authInfo = new TableSelector(FdahpUserRegWSSchema.getInstance().getAuthInfo(), filter, null).getObject(AuthInfo.class);
            String authKey = "0";
            authKey = RandomStringUtils.randomNumeric(9);
            AuditConfigurable table = (AuditConfigurable) FdahpUserRegWSSchema.getInstance().getAuthInfo();
            table.setAuditBehavior(AuditBehaviorType.DETAILED);
            String refreshToken = UUID.randomUUID().toString();
            if (null != authInfo)
            {
                authInfo.setAuthKey(authKey);
                authInfo.setModifiedOn(new Date());
                _log.info("session.expiration.days:" + FdahpUserRegUtil.addMinutes(FdahpUserRegUtil.getCurrentDateTime(), Integer.parseInt((String) configProp.get("session.expiration.time"))));
                authInfo.setSessionExpiredDate(FdahpUserRegUtil.addMinutes(FdahpUserRegUtil.getCurrentDateTime(), Integer.parseInt((String) configProp.get("session.expiration.time"))));
                _log.info("isRefresh:" + isRefresh);
                if (isRefresh)
                {
                    authInfo.setRefreshToken(refreshToken);
                    authInfo.setDeviceToken("");
                    authInfo.setDeviceType("");
                }

                if (availableContainer != null)
                    authInfo.setContainer(availableContainer.getId());

                Table.update(null, table, authInfo, authInfo.getAuthId());
            }
            else
            {
                authInfo = new AuthInfo();
                authInfo.setAuthKey(authKey);
                authInfo.setParticipantId(userId);
                authInfo.setApplicationId(applicationId);
                authInfo.setOrgId(orgId);
                authInfo.setCreatedOn(new Date());
                _log.info("session.expiration.days:" + FdahpUserRegUtil.addMinutes(FdahpUserRegUtil.getCurrentDateTime(), Integer.parseInt((String) configProp.get("session.expiration.time"))));
                authInfo.setSessionExpiredDate(FdahpUserRegUtil.addMinutes(FdahpUserRegUtil.getCurrentDateTime(), Integer.parseInt((String) configProp.get("session.expiration.time"))));
                _log.info("isRefresh:" + isRefresh);
                if (isRefresh)
                {
                    authInfo.setRefreshToken(refreshToken);
                    authInfo.setDeviceToken("");
                    authInfo.setDeviceType("");
                }

                if (availableContainer != null)
                    authInfo.setContainer(availableContainer.getId());

                Table.insert(null, table, authInfo);
            }
        }
        catch (Exception e)
        {
            _log.error("saveAuthInfo:", e);
        }
        transaction.commit();
        return authInfo;
    }

    /**
     * Validating the authkey of an user
     *
     * @param authKey
     * @return boolean true/false
     */
    public boolean validatedAuthKey(String authKey, String applicationId, String orgId)
    {
        boolean isAuthenticated = false;
        try
        {
            AuthInfo authInfo = null;
            AuditConfigurable table = (AuditConfigurable) FdahpUserRegWSSchema.getInstance().getAuthInfo();
            table.setAuditBehavior(AuditBehaviorType.DETAILED);
            SimpleFilter filter = new SimpleFilter();
            filter.addCondition(FieldKey.fromParts("AuthKey"), authKey);
            filter.addCondition(FieldKey.fromParts("ApplicationId"), applicationId);
            filter.addCondition(FieldKey.fromParts("OrgId"), orgId);
            authInfo = new TableSelector(FdahpUserRegWSSchema.getInstance().getAuthInfo(), filter, null).getObject(AuthInfo.class);
            if (authInfo != null)
            {
                _log.info("FdahpUserRegUtil.getCurrentUtilDateTime() :" + FdahpUserRegUtil.getCurrentUtilDateTime());
                _log.info("authInfo.getSessionExpiredDate() :" + authInfo.getSessionExpiredDate());
                if (FdahpUserRegUtil.getCurrentUtilDateTime().before(authInfo.getSessionExpiredDate()) || FdahpUserRegUtil.getCurrentUtilDateTime().equals(authInfo.getSessionExpiredDate()))
                {
                    isAuthenticated = true;
                }
            }
        }
        catch (Exception e)
        {
            _log.error("FdahpUserRegWSManger validatedAuthKey ()", e);
        }
        _log.info("isAuthenticated :" + isAuthenticated);
        return isAuthenticated;
    }

    /**
     * saving the user information on sign up
     *
     * @param email
     * @param password
     * @return ParticipantForm
     */
    public ParticipantForm signingParticipant(String email, String password, String applicationId, String orgId)
    {
        ParticipantForm participantForm = null;
        UserDetails participantDetails = null;
        try
        {
            SimpleFilter filter = new SimpleFilter();
            filter.addCondition(FieldKey.fromParts("Email"), email);
            filter.addCondition(FieldKey.fromParts("ApplicationId"), applicationId);
            filter.addCondition(FieldKey.fromParts("OrgId"), orgId);
            filter.addCondition(FieldKey.fromParts("Password"), FdahpUserRegUtil.getEncryptedString(password));
            participantDetails = new TableSelector(FdahpUserRegWSSchema.getInstance().getParticipantDetails(), filter, null).getObject(UserDetails.class);
            if (null != participantDetails)
            {
                participantForm = new ParticipantForm();
                AuthInfo authInfo = saveAuthInfo(participantDetails.getUserId(), true, applicationId, orgId);
                if (authInfo != null)
                {
                    participantForm.setAuth(authInfo.getAuthKey());
                }
                participantForm.setUserId(participantDetails.getUserId());
                participantForm.setFirstName(participantDetails.getFirstName());
                participantForm.setStatus(participantDetails.getStatus());
                participantForm.setLastName(participantDetails.getLastName());
                participantForm.setEmailId(participantDetails.getEmail());
                participantForm.setTempPassword(participantDetails.getTempPassword());
                participantForm.setTempPasswordDate(participantDetails.getTempPasswordDate());
            }
        }
        catch (Exception e)
        {
            _log.error("FdahpUserRegWSManager signingParticipant()", e);
        }
        return participantForm;
    }

    /**
     * Get the user by email
     *
     * @param email
     * @return UserDetails
     */
    public UserDetails getParticipantDetailsByEmail(String email, String applicationId, String orgId)
    {
        UserDetails participantDetails = null;
        try
        {
            SimpleFilter filter = new SimpleFilter();
            filter.addCondition(FieldKey.fromParts("Email"), email);
            filter.addCondition(FieldKey.fromParts("ApplicationId"), applicationId);
            filter.addCondition(FieldKey.fromParts("OrgId"), orgId);
            participantDetails = new TableSelector(FdahpUserRegWSSchema.getInstance().getParticipantDetails(), filter, null).getObject(UserDetails.class);
        }
        catch (Exception e)
        {
            _log.error("FdahpUserRegWSManager getParticipantDetailsByEmail()", e);
        }
        return participantDetails;
    }

    /**
     * Signout from the app
     *
     * @param userId
     * @return String Success/Failure
     */
    public String signout(String userId, String applicationId, String orgId)
    {
        String message = FdahpUserRegUtil.ErrorCodes.FAILURE.getValue();
        try
        {
            DbSchema schema = FdahpUserRegWSSchema.getInstance().getSchema();
            AuditConfigurable authInfo = (AuditConfigurable) FdahpUserRegWSSchema.getInstance().getAuthInfo();
            authInfo.setAuditBehavior(AuditBehaviorType.DETAILED);
            SqlExecutor executor = new SqlExecutor(schema);
            SQLFragment sqlUpdateVisitDates = new SQLFragment();

            sqlUpdateVisitDates.append("UPDATE ").append(authInfo.getSelectName()).append("\n")
                    .append("SET AuthKey = 0, DeviceToken = NULL,RefreshToken=NULL,DeviceType=NULL, ModifiedOn='" + FdahpUserRegUtil.getCurrentDateTime() + "'")
                    .append(" WHERE ParticipantId = '" + userId + "'")
                    .append(" AND ApplicationId= '" + applicationId + "' AND OrgId='" + orgId + "'");
            int execute = executor.execute(sqlUpdateVisitDates);
            if (execute > 0)
            {
                message = FdahpUserRegUtil.ErrorCodes.SUCCESS.getValue();
            }
        }
        catch (Exception e)
        {
            _log.error("FdahpUserRegWSManager signout error:", e);
        }
        return message;
    }

    /**
     * Get the User Profile info
     *
     * @param userId
     * @return ApiSimpleResponse
     */
    public ApiSimpleResponse getParticipantInfoDetails(String userId, String applicationId, String orgId)
    {
        JSONObject jsonObject = new JSONObject();
        ApiSimpleResponse response = new ApiSimpleResponse();
        try
        {
            UserDetails participantDetails = getParticipantDetails(userId, applicationId, orgId);
            if (participantDetails != null)
            {
                ProfileBean profileBean = new ProfileBean();
                if (participantDetails.getEmail() != null)
                    profileBean.setEmailId(participantDetails.getEmail());
                response.put(FdahpUserRegUtil.ErrorCodes.PROFILE.getValue(), profileBean);
                SettingsBean settingsBean = new SettingsBean();
                if (participantDetails.getLocalNotificationFlag() != null)
                    settingsBean.setLocalNotifications(participantDetails.getLocalNotificationFlag());
                if (participantDetails.getUsePassCode() != null)
                    settingsBean.setPasscode(participantDetails.getUsePassCode());
                if (participantDetails.getRemoteNotificationFlag() != null)
                    settingsBean.setRemoteNotifications(participantDetails.getRemoteNotificationFlag());
                if (participantDetails.getTouchId() != null)
                    settingsBean.setTouchId(participantDetails.getTouchId());
                if (participantDetails.getReminderLeadTime() != null && !participantDetails.getReminderLeadTime().isEmpty())
                {
                    settingsBean.setReminderLeadTime(participantDetails.getReminderLeadTime());
                }
                else
                {
                    settingsBean.setReminderLeadTime("");
                }
                if (participantDetails.getLocale() != null)
                    settingsBean.setLocale(participantDetails.getLocale());
                response.put(FdahpUserRegUtil.ErrorCodes.SETTINGS.getValue(), settingsBean);
                response.put(FdahpUserRegUtil.ErrorCodes.MESSAGE.getValue(), FdahpUserRegUtil.ErrorCodes.SUCCESS.getValue().toLowerCase());
            }
        }
        catch (Exception e)
        {
            _log.error("FdahpUserRegWSManager getParticipantDetails error:", e);
        }
        return response;
    }

    /**
     * Get the user study list
     *
     * @param userId
     * @param appId
     * @param orgId
     * @return ParticipantStudies
     */
    public List<ParticipantStudies> getParticipantStudiesList(String userId, String appId, String orgId)
    {
        List<ParticipantStudies> participantStudiesList = null;
        try
        {
            SimpleFilter filter = new SimpleFilter();
            filter.addCondition(FieldKey.fromParts("UserId"), userId);
            filter.addCondition(FieldKey.fromParts("ApplicationId"), appId);
            filter.addCondition(FieldKey.fromParts("OrgId"), orgId);
            participantStudiesList = new TableSelector(FdahpUserRegWSSchema.getInstance().getParticipantStudies(), filter, null).getArrayList(ParticipantStudies.class);
        }
        catch (Exception e)
        {
            _log.error("FdahpUserRegWSManager getParticipantStudiesList error:", e);
        }
        return participantStudiesList;
    }

    /**
     * Get the user activity list
     *
     * @param userId
     * @return ParticipantActivities
     */
    public List<ParticipantActivities> getParticipantActivitiesList(String userId)
    {
        List<ParticipantActivities> participantActivitiesList = null;
        try
        {
            SimpleFilter filter = new SimpleFilter();
            filter.addCondition(FieldKey.fromParts("ParticipantId"), userId);
            participantActivitiesList = new TableSelector(FdahpUserRegWSSchema.getInstance().getParticipantActivities(), filter, null).getArrayList(ParticipantActivities.class);
        }
        catch (Exception e)
        {
            _log.error("FdahpUserRegWSManager getParticipantActivitiesList error :", e);
        }
        return participantActivitiesList;
    }

    /**
     * Save or update of an user study info
     *
     * @param participantStudiesList
     * @return String success/Failure
     */
    public String saveParticipantStudies(List<ParticipantStudies> participantStudiesList)
    {
        String message = FdahpUserRegUtil.ErrorCodes.FAILURE.getValue();
        DbScope dbScope = FdahpUserRegWSSchema.getInstance().getSchema().getScope();
        DbScope.Transaction transaction = dbScope.ensureTransaction();
        try
        {
            AuditConfigurable table = (AuditConfigurable) FdahpUserRegWSSchema.getInstance().getParticipantStudies();
            table.setAuditBehavior(AuditBehaviorType.DETAILED);
            for (ParticipantStudies participantStudies : participantStudiesList)
            {
                Container container = getContainer_StudyID(participantStudies.getApplicationId(), participantStudies.getStudyId());
                if (container != null)
                    participantStudies.setContainer(container.getId());

                if (participantStudies.getId() != null)
                {
                    Table.update(null, table, participantStudies, participantStudies.getId());
                }
                else
                {
                    Table.insert(null, table, participantStudies);
                }
                addAuditEvent(participantStudies.getUserId(), "Study State Update", " Study state has been updated " + participantStudies.getStudyId() + ".", "FdaStudyAuditEvent", "");
            }

            if (participantStudiesList.size() > 0)
            {
                message = FdahpUserRegUtil.ErrorCodes.SUCCESS.getValue();
            }
        }
        catch (Exception e)
        {
            _log.error("FdahpUserRegWSManager saveParticipantStudies error :", e);
        }
        transaction.commit();
        return message;
    }

    /**
     * save or update of an user activity state info
     *
     * @param participantActivitiesList
     * @return String Success/Failure
     */
    public String saveParticipantActivities(List<ParticipantActivities> participantActivitiesList)
    {
        String message = FdahpUserRegUtil.ErrorCodes.FAILURE.getValue();
        DbScope dbScope = FdahpUserRegWSSchema.getInstance().getSchema().getScope();
        DbScope.Transaction transaction = dbScope.ensureTransaction();
        try
        {
            AuditConfigurable table = (AuditConfigurable) FdahpUserRegWSSchema.getInstance().getParticipantActivities();
            table.setAuditBehavior(AuditBehaviorType.DETAILED);
            for (ParticipantActivities participantActivities : participantActivitiesList)
            {
                Container container = getContainer_StudyID(participantActivities.getApplicationId(), participantActivities.getStudyId());
                if (container != null)
                    participantActivities.setContainer(container.getId());

                if (participantActivities.getId() != null)
                    Table.update(null, table, participantActivities, participantActivities.getId());
                else
                    Table.insert(null, table, participantActivities);
                addAuditEvent(participantActivities.getParticipantId(), "Activity State Update", "Activity state has been updated " + participantActivities.getActivityId() + ".", "FdaActivityAuditEvent", "");
            }


            if (participantActivitiesList.size() > 0)
            {
                message = FdahpUserRegUtil.ErrorCodes.SUCCESS.getValue();
            }
        }
        catch (Exception e)
        {
            _log.error("FdahpUserRegWSManager saveParticipantActivities error :", e);
        }
        transaction.commit();
        return message;
    }

    /**
     * Get the user preferences
     *
     * @param userId
     * @return ApiSimpleResponse
     */
    public ApiSimpleResponse getPreferences(String userId, String appId, String orgId)
    {
        ApiSimpleResponse response = new ApiSimpleResponse();
        try
        {
            List<ParticipantStudies> participantStudiesList = getParticipantStudiesList(userId, appId, orgId);
            List<StudiesBean> studiesBeenList = new ArrayList<StudiesBean>();
            if (null != participantStudiesList && participantStudiesList.size() > 0)
            {

                for (ParticipantStudies participantStudies : participantStudiesList)
                {
                    StudiesBean studiesBean = new StudiesBean();
                    if (participantStudies.getStudyId() != null)
                        studiesBean.setStudyId(participantStudies.getStudyId());
                    if (participantStudies.getBookmark() != null)
                        studiesBean.setBookmarked(participantStudies.getBookmark());
                    if (participantStudies.getStatus() != null)
                    {
                        studiesBean.setStatus(participantStudies.getStatus());
                    }
                    else
                    {
                        studiesBean.setStatus("");
                    }
                    if (participantStudies.getEnrolledDate() != null)
                    {
                        studiesBean.setEnrolledDate(FdahpUserRegUtil.getFormattedDateTimeZone(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(participantStudies.getEnrolledDate()), "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd'T'HH:mm:ss.SSSZ"));
                    }
                    else
                    {
                        studiesBean.setEnrolledDate("");
                    }
                    if (participantStudies.getCompletion() != null)
                    {
                        studiesBean.setCompletion(participantStudies.getCompletion());
                    }
                    else
                    {
                        studiesBean.setCompletion(0);
                    }
                    if (participantStudies.getAdherence() != null)
                    {
                        studiesBean.setAdherence(participantStudies.getAdherence());
                    }
                    else
                    {
                        studiesBean.setAdherence(0);
                    }
                    if (participantStudies.getParticipantId() != null)
                    {
                        studiesBean.setParticipantId(participantStudies.getParticipantId());
                    }
                    else
                    {
                        studiesBean.setParticipantId("");
                    }
                    studiesBeenList.add(studiesBean);
                }
            }
            response.put(FdahpUserRegUtil.ErrorCodes.STUDIES.getValue(), studiesBeenList);
            response.put(FdahpUserRegUtil.ErrorCodes.MESSAGE.getValue(), FdahpUserRegUtil.ErrorCodes.SUCCESS.getValue().toLowerCase());
        }
        catch (Exception e)
        {
            _log.error("FdahpUserRegWSManager getPreferences error :", e);
        }
        return response;
    }

    /**
     * Get the user study
     *
     * @param studyId
     * @param userId
     * @return ParticipantStudies
     */
    public ParticipantStudies getParticipantStudies(String studyId, String userId, String applicationId, String orgId)
    {
        ParticipantStudies participantStudies = null;
        try
        {
            SimpleFilter filter = new SimpleFilter();
            filter.addCondition(FieldKey.fromParts("UserId"), userId);
            filter.addCondition(FieldKey.fromParts("StudyId"), studyId);
            filter.addCondition(FieldKey.fromParts("ApplicationId"), applicationId);
            filter.addCondition(FieldKey.fromParts("OrgId"), orgId);
            participantStudies = new TableSelector(FdahpUserRegWSSchema.getInstance().getParticipantStudies(), filter, null).getObject(ParticipantStudies.class);
        }
        catch (Exception e)
        {
            _log.error("FdahpUserRegWSManager getParticipantStudies()", e);
        }
        return participantStudies;
    }

    /**
     * Get the user activity List of an study
     *
     * @param studyId
     * @param userId
     * @return List {@link ParticipantActivities}
     */
    public List<ParticipantActivities> getParticipantActivitiesList(String studyId, String userId, String appId, String orgId)
    {
        List<ParticipantActivities> participantActivitiesList = null;
        try
        {
            SimpleFilter filter = new SimpleFilter();
            filter.addCondition(FieldKey.fromParts("ParticipantId"), userId);
            filter.addCondition(FieldKey.fromParts("StudyId"), studyId);
            filter.addCondition(FieldKey.fromParts("ApplicationId"), appId);
            filter.addCondition(FieldKey.fromParts("OrgId"), orgId);
            participantActivitiesList = new TableSelector(FdahpUserRegWSSchema.getInstance().getParticipantActivities(), filter, null).getArrayList(ParticipantActivities.class);
        }
        catch (Exception e)
        {
            _log.error("FdahpUserRegWSManager getParticipantActivitiesList()", e);
        }
        return participantActivitiesList;
    }

    /**
     * withdraw from study
     *
     * @param studyId
     * @param userId
     * @param applicationId
     * @param orgId
     * @param deleteData
     * @return String Success/Failure
     */
    public String withDrawStudy(String studyId, String userId, String applicationId, String orgId, Boolean deleteData)
    {
        String message = FdahpUserRegUtil.ErrorCodes.FAILURE.getValue();
        DbScope dbScope = FdahpUserRegWSSchema.getInstance().getSchema().getScope();
        DbScope.Transaction transaction = dbScope.ensureTransaction();
        try
        {
            DbSchema schema = FdahpUserRegWSSchema.getInstance().getSchema();
            AuditConfigurable table = (AuditConfigurable) FdahpUserRegWSSchema.getInstance().getParticipantStudies();
            table.setAuditBehavior(AuditBehaviorType.DETAILED);

            SqlExecutor executor = new SqlExecutor(schema);
            SQLFragment sqlUpdateVisitDates = new SQLFragment();

            AuditConfigurable participantActivitiesInfo = (AuditConfigurable) FdahpUserRegWSSchema.getInstance().getParticipantActivities();
            participantActivitiesInfo.setAuditBehavior(AuditBehaviorType.DETAILED);

            if (deleteData)
            {
                SimpleFilter filterActivities = new SimpleFilter();
                filterActivities.addCondition(FieldKey.fromParts("ParticipantId"), userId);
                filterActivities.addCondition(FieldKey.fromParts("StudyId"), studyId);
                filterActivities.addCondition(FieldKey.fromParts("ApplicationId"), applicationId);
                filterActivities.addCondition(FieldKey.fromParts("OrgId"), orgId);
                Table.delete(participantActivitiesInfo, filterActivities);

                sqlUpdateVisitDates.append("UPDATE ").append(table.getSelectName()).append("\n")
                        .append("SET Status = 'Withdrawn', ParticipantId = NULL")
                        .append(" WHERE UserId = '" + userId + "'")
                        .append(" and StudyId = '" + studyId + "'");
                int execute = executor.execute(sqlUpdateVisitDates);
                if (execute > 0)
                {
                    message = FdahpUserRegUtil.ErrorCodes.SUCCESS.getValue();
                }
            }
            else
            {
                sqlUpdateVisitDates.append("UPDATE ").append(table.getSelectName()).append("\n")
                        .append("SET Status = 'Withdrawn'")
                        .append(" WHERE UserId = '" + userId + "'")
                        .append(" and StudyId = '" + studyId + "'");
                int execute = executor.execute(sqlUpdateVisitDates);
                if (execute > 0)
                {
                    message = FdahpUserRegUtil.ErrorCodes.SUCCESS.getValue();
                }
            }
        }
        catch (Exception e)
        {
            _log.error("FdahpUserRegWSManager withDrawStudy()", e);
        }
        transaction.commit();
        return message;
    }

    /**
     * Get the auth info of an user
     *
     * @param authKey
     * @param participantId
     * @return AuthInfo
     */
    public AuthInfo getAuthInfo(String authKey, String participantId, String applicationId, String orgId)
    {
        AuthInfo authInfo = null;
        try
        {
            SimpleFilter filter = new SimpleFilter();
            filter.addCondition(FieldKey.fromParts("AuthKey"), authKey);
            filter.addCondition(FieldKey.fromParts("ParticipantId"), participantId);
            filter.addCondition(FieldKey.fromParts("ApplicationId"), applicationId);
            filter.addCondition(FieldKey.fromParts("OrgId"), orgId);
            authInfo = new TableSelector(FdahpUserRegWSSchema.getInstance().getAuthInfo(), filter, null).getObject(AuthInfo.class);
        }
        catch (Exception e)
        {
            _log.error("FdahpUserRegWSManger getAuthInfo ()", e);
        }
        return authInfo;
    }

    /**
     * Update the auth info of an user
     *
     * @param authInfo
     * @return AuthInfo
     */
    public AuthInfo updateAuthInfo(AuthInfo authInfo)
    {
        DbScope dbScope = FdahpUserRegWSSchema.getInstance().getSchema().getScope();
        DbScope.Transaction transaction = dbScope.ensureTransaction();
        try
        {
            AuditConfigurable table = (AuditConfigurable) FdahpUserRegWSSchema.getInstance().getAuthInfo();
            table.setAuditBehavior(AuditBehaviorType.DETAILED);
            if (null != authInfo)
            {
                // No need to set coz it already has container value
//                Container container = getContainer_AppID(authInfo.getApplicationId());
//                if (container != null)
//                    authInfo.setContainer(container.getId());

                authInfo.setModifiedOn(new Date());
                Table.update(null, table, authInfo, authInfo.getAuthId());
            }

        }
        catch (Exception e)
        {
            _log.error("updateAuthInfo:", e);
        }
        transaction.commit();
        return authInfo;
    }

    /**
     * save or update of an study consent of an user
     *
     * @param studyConsent
     * @return StudyConsent
     */
    public StudyConsent saveStudyConsent(StudyConsent studyConsent)
    {
        Container container = getContainer_StudyID(studyConsent.getApplicationId(), studyConsent.getStudyId());
        if (container != null)
            studyConsent.setContainer(container.getId());

        DbScope dbScope = FdahpUserRegWSSchema.getInstance().getSchema().getScope();
        DbScope.Transaction transaction = dbScope.ensureTransaction();
        try
        {
            AuditConfigurable table = (AuditConfigurable) FdahpUserRegWSSchema.getInstance().getStudyConsent();
            table.setAuditBehavior(AuditBehaviorType.DETAILED);

            if (null != studyConsent)
            {
                if (studyConsent.getId() != null)
                {
                    Table.update(null, table, studyConsent, studyConsent.getId());
                }
                else
                {
                    Table.insert(null, table, studyConsent);
                }
            }

        }
        catch (Exception e)
        {
            _log.error("saveStudyConsent:", e);
        }
        transaction.commit();
        return studyConsent;
    }

    /**
     * Get the study consent of an user to particular study
     *
     * @param userId
     * @param studyId
     * @param consentVersion
     * @return StudyConsent
     */
    public StudyConsent getStudyConsent(String userId, String studyId, String consentVersion, String applicationId, String orgId)
    {
        StudyConsent studyConsent = null;
        try
        {
            TableInfo studyConsentInfo = FdahpUserRegWSSchema.getInstance().getStudyConsent();
            SQLFragment sql = null;
            if (consentVersion != null && StringUtils.isNotEmpty(consentVersion))
            {
                sql = new SQLFragment("SELECT * FROM " + studyConsentInfo.getSelectName() + " WHERE userid ='" + userId + "' and studyid ='" + studyId + "' and version ='" + consentVersion + "' and applicationId='" + applicationId + "' and orgId='" + orgId + "'");
            }
            else
            {
                sql = new SQLFragment("SELECT * FROM " + studyConsentInfo.getSelectName() + " WHERE userid ='" + userId + "' and studyid ='" + studyId + "' and applicationId='" + applicationId + "' and orgId='" + orgId + "' order by _ts desc limit 1");
            }
            studyConsent = new SqlSelector(FdahpUserRegWSSchema.getInstance().getSchema(), sql).getObject(StudyConsent.class);

        }
        catch (Exception e)
        {
            _log.error("getStudyConsent Error", e);
        }
        return studyConsent;
    }

    /**
     * Get the user info by using the token
     *
     * @param emailId
     * @param token
     * @return UserDetails
     */
    public UserDetails getParticipantDetailsByToken(String emailId, String token, String applicationId, String orgId)
    {
        UserDetails participantDetails = null;
        try
        {
            SimpleFilter filter = new SimpleFilter();
            filter.addCondition(FieldKey.fromParts("Email"), emailId);
            filter.addCondition(FieldKey.fromParts("SecurityToken"), token);
            filter.addCondition(FieldKey.fromParts("ApplicationId"), applicationId);
            filter.addCondition(FieldKey.fromParts("OrgId"), orgId);
            participantDetails = new TableSelector(FdahpUserRegWSSchema.getInstance().getParticipantDetails(), filter, null).getObject(UserDetails.class);
        }
        catch (Exception e)
        {
            _log.error("getParticipantDetailsByToken Error", e);
        }
        return participantDetails;
    }

    /**
     * Delete of an user account
     *
     * @param userId
     * @return String Success/Failure
     */
    public String deleteAccount(String userId)
    {
        String message = FdahpUserRegUtil.ErrorCodes.FAILURE.getValue();
        DbScope dbScope = FdahpUserRegWSSchema.getInstance().getSchema().getScope();
        DbScope.Transaction transaction = dbScope.ensureTransaction();
        try
        {
            AuditConfigurable participantActivitiesInfo = (AuditConfigurable) FdahpUserRegWSSchema.getInstance().getParticipantActivities();
            participantActivitiesInfo.setAuditBehavior(AuditBehaviorType.DETAILED);
            SimpleFilter filterActivities = new SimpleFilter();
            filterActivities.addCondition(FieldKey.fromParts("ParticipantId"), userId);
            Table.delete(participantActivitiesInfo, filterActivities);

            AuditConfigurable table = (AuditConfigurable) FdahpUserRegWSSchema.getInstance().getParticipantStudies();
            table.setAuditBehavior(AuditBehaviorType.DETAILED);
            SimpleFilter filter = new SimpleFilter();
            filter.addCondition(FieldKey.fromParts("UserId"), userId);
            Table.delete(table, filter);

            AuditConfigurable participantInfo = (AuditConfigurable) FdahpUserRegWSSchema.getInstance().getParticipantDetails();
            participantInfo.setAuditBehavior(AuditBehaviorType.DETAILED);
            SimpleFilter participantFilter = new SimpleFilter();
            participantFilter.addCondition(FieldKey.fromParts("UserId"), userId);
            int count = Table.delete(participantInfo, participantFilter);

            AuditConfigurable authInfo = (AuditConfigurable) FdahpUserRegWSSchema.getInstance().getAuthInfo();
            authInfo.setAuditBehavior(AuditBehaviorType.DETAILED);
            SimpleFilter authInfoFilter = new SimpleFilter();
            authInfoFilter.addCondition(FieldKey.fromParts("ParticipantId"), userId);
            Table.delete(authInfo, authInfoFilter);

            if (count > 0)
                message = FdahpUserRegUtil.ErrorCodes.SUCCESS.getValue();


        }
        catch (Exception e)
        {
            _log.error("deleteAccount error:", e);
        }
        transaction.commit();
        return message;
    }

    /**
     * Deactivate of an user
     *
     * @param userId
     * @param deactivateForm
     * @return String Success/Faliure
     */
    public String deActivate(String userId, DeactivateForm deactivateForm, String applicationId, String orgId)
    {

        String message = FdahpUserRegUtil.ErrorCodes.FAILURE.getValue();
        DbScope dbScope = FdahpUserRegWSSchema.getInstance().getSchema().getScope();
        DbScope.Transaction transaction = dbScope.ensureTransaction();
        int count = 0;
        try
        {

            DbSchema schema = FdahpUserRegWSSchema.getInstance().getSchema();

            SqlExecutor executor = new SqlExecutor(schema);
            SQLFragment sqlUpdateVisitDates = new SQLFragment();
            if (userId != null && !userId.isEmpty())
            {
                if (deactivateForm != null && deactivateForm.getDeleteData() != null && deactivateForm.getDeleteData().size() > 0)
                {

                    AuditConfigurable table = (AuditConfigurable) FdahpUserRegWSSchema.getInstance().getParticipantStudies();
                    table.setAuditBehavior(AuditBehaviorType.DETAILED);
                    sqlUpdateVisitDates.append("UPDATE ").append(table.getSelectName()).append("\n")
                            .append("SET Status = 'Withdrawn', ParticipantId = NULL")
                            .append(" WHERE UserId = '" + userId + "'")
                            .append(" and StudyId IN (" + FdahpUserRegUtil.commaSeparatedString(deactivateForm.getDeleteData()) + ")");
                    //.append(" and ApplicationId = '" + applicationId +"'")
                    //  .append(" and OrgId = '" + orgId +"'");
                    int execute = executor.execute(sqlUpdateVisitDates);
                }

                AuditConfigurable participantActivitiesInfo = (AuditConfigurable) FdahpUserRegWSSchema.getInstance().getParticipantActivities();
                participantActivitiesInfo.setAuditBehavior(AuditBehaviorType.DETAILED);
                SimpleFilter filterActivities = new SimpleFilter();
                filterActivities.addCondition(FieldKey.fromParts("ParticipantId"), userId);
                //filterActivities.addCondition(FieldKey.fromParts("ApplicationId"), applicationId);
                //filterActivities.addCondition(FieldKey.fromParts("OrgId"), orgId);
                Table.delete(participantActivitiesInfo, filterActivities);

                AuditConfigurable authInfo = (AuditConfigurable) FdahpUserRegWSSchema.getInstance().getAuthInfo();
                authInfo.setAuditBehavior(AuditBehaviorType.DETAILED);
                SimpleFilter authInfoFilter = new SimpleFilter();
                authInfoFilter.addCondition(FieldKey.fromParts("ParticipantId"), userId);
                //authInfoFilter.addCondition(FieldKey.fromParts("ApplicationId"), applicationId);
                //authInfoFilter.addCondition(FieldKey.fromParts("OrgId"), orgId);
                Table.delete(authInfo, authInfoFilter);

                AuditConfigurable userAppDetailsInfo = (AuditConfigurable) FdahpUserRegWSSchema.getInstance().getUserAppDetails();
                userAppDetailsInfo.setAuditBehavior(AuditBehaviorType.DETAILED);
                SimpleFilter userAppFilter = new SimpleFilter();
                userAppFilter.addCondition(FieldKey.fromParts("UserId"), userId);
                Table.delete(userAppDetailsInfo, userAppFilter);

                AuditConfigurable participantInfo = (AuditConfigurable) FdahpUserRegWSSchema.getInstance().getParticipantDetails();
                participantInfo.setAuditBehavior(AuditBehaviorType.DETAILED);
                SimpleFilter participantFilter = new SimpleFilter();
                participantFilter.addCondition(FieldKey.fromParts("UserId"), userId);
                count = Table.delete(participantInfo, participantFilter);

                if (count > 0)
                    message = FdahpUserRegUtil.ErrorCodes.SUCCESS.getValue();
            }


        }
        catch (Exception e)
        {
            _log.error("deActivate error:", e);
        }
        transaction.commit();
        return message;
    }

    /**
     * Get the password history of an user
     *
     * @param userId
     * @return PasswordHistory
     */
    public List<PasswordHistory> getPasswordHistoryList(String userId)
    {
        List<PasswordHistory> passwordHistoryList = null;
        try
        {
            TableInfo passwordHistoryInfo = FdahpUserRegWSSchema.getInstance().getPasswordHistory();
            SQLFragment sql = new SQLFragment("SELECT * FROM " + passwordHistoryInfo.getSelectName() + " WHERE userId ='" + userId + "' ORDER BY created");
            passwordHistoryList = new SqlSelector(FdahpUserRegWSSchema.getInstance().getSchema(), sql).getArrayList(PasswordHistory.class);
        }
        catch (Exception e)
        {
            _log.error("getPasswordHistoryList error:", e);
        }
        return passwordHistoryList;
    }

    /**
     * save or update the password history if an user while changing the password
     *
     * @param userId
     * @param password
     * @param applicationId
     * @param orgId
     * @return String Success/Failure
     */
    public String savePasswordHistory(String userId, String password, String applicationId, String orgId)
    {

        Container availableContainer = getContainer_AppID(applicationId);

        Properties configProp = FdahpUserRegUtil.getProperties();
        String message = FdahpUserRegUtil.ErrorCodes.FAILURE.getValue();
        String passwordHistoryCount = (String) configProp.get("password.history.count");
        List<PasswordHistory> passwordHistories = null;
        DbScope dbScope = FdahpUserRegWSSchema.getInstance().getSchema().getScope();
        DbScope.Transaction transaction = dbScope.ensureTransaction();
        try
        {
            passwordHistories = getPasswordHistoryList(userId);

            AuditConfigurable table = (AuditConfigurable) FdahpUserRegWSSchema.getInstance().getPasswordHistory();
            table.setAuditBehavior(AuditBehaviorType.DETAILED);

            if (passwordHistories != null && passwordHistories.size() > (Integer.parseInt(passwordHistoryCount) - 1))
            {
                for (int i = 0; i < ((passwordHistories.size() - Integer.parseInt(passwordHistoryCount)) + 1); i++)
                {
                    SimpleFilter filter = new SimpleFilter();
                    filter.addCondition(FieldKey.fromParts("Id"), passwordHistories.get(i).getId());
                    Table.delete(table, filter);
                }
            }

            PasswordHistory passwordHistory = new PasswordHistory();
            passwordHistory.setUserId(userId);
            passwordHistory.setPassword(password);
            message = FdahpUserRegUtil.ErrorCodes.SUCCESS.getValue();

            if (availableContainer != null)
                passwordHistory.setContainer(availableContainer.getId());

            Table.insert(null, table, passwordHistory);


        }
        catch (Exception e)
        {
            _log.error("getPasswordHistoryList error:", e);
        }
        transaction.commit();
        return message;
    }

    /**
     * Get the all user device token
     *
     * @return Map<String, JSONArray>
     */
    public Map<String, JSONArray> getDeviceTokenOfAllUsers(String appIds)
    {
        String deviceTokens = null;
        List<AuthInfo> authInfoList = new ArrayList<>();
        List<String> deviceTokenList = new ArrayList<>();
        String[] deviceTokenArr = null;
        JSONArray jsonArray = null;
        JSONArray iosJsonArray = null;
        Map<String, JSONArray> deviceMap = new HashMap<>();
        try
        {
            TableInfo authTableInfo = FdahpUserRegWSSchema.getInstance().getAuthInfo();
            SQLFragment sql = new SQLFragment();
            sql.append("SELECT a.devicetoken as devicetoken,a.devicetype as devicetype FROM ").append(FdahpUserRegWSSchema.getInstance().getUserAppDetails(), "u").append(" , ").append(FdahpUserRegWSSchema.getInstance().getAuthInfo(), "a")
                    .append(" where u.userid = a.participantid and u.applicationid in (" + appIds + ") and a.authkey != '0' and a.remotenotificationflag=true and (a.devicetoken is not NULL and a.devicetoken != '' and a.devicetype is not NULL and a.devicetype != '') ");
            ResultSet rs = new SqlSelector(FdahpUserRegWSSchema.getInstance().getSchema(), sql).getResultSet();
            if (rs != null)
            {
                jsonArray = new JSONArray();
                iosJsonArray = new JSONArray();
                while (rs.next())
                {
                    String devicetoken = rs.getString(1);
                    String devicetype = rs.getString(2);
                    if (devicetoken != null && devicetype != null)
                    {
                        if (devicetype.equalsIgnoreCase(FdahpUserRegUtil.ErrorCodes.DEVICE_ANDROID.getValue()))
                        {
                            jsonArray.put(devicetoken.trim());
                        }
                        else if (devicetype.equalsIgnoreCase(FdahpUserRegUtil.ErrorCodes.DEVICE_IOS.getValue()))
                        {
                            iosJsonArray.put(devicetoken.trim());
                        }
                    }
                }
                deviceMap.put(FdahpUserRegUtil.ErrorCodes.DEVICE_ANDROID.getValue(), jsonArray);
                deviceMap.put(FdahpUserRegUtil.ErrorCodes.DEVICE_IOS.getValue(), iosJsonArray);
                rs.close();
            }
            /**
             select a.deviceToken,a.deviceType from fdahpuserregws.authinfo as a,
             fdahpuserregws.userappdetails as u
             where u.applicationid='12'
             and a.deviceToken IS NOT NULL
             and a.deviceToken != '0'
             and a.devicetype IS NOT NULL
             and a.remotenotificationflag=true
             and u.userid=a.participantId;
             */


            /*
            SQLFragment sql=null;
            sql =  new SQLFragment("SELECT * FROM " + authTableInfo.getSelectName() + " WHERE authkey != '0' and remotenotificationflag=true");
            authInfoList = new SqlSelector(FdahpUserRegWSSchema.getInstance().getSchema(), sql).getArrayList(AuthInfo.class);

            if(authInfoList != null && !authInfoList.isEmpty()){
                jsonArray = new JSONArray();
                iosJsonArray = new JSONArray();
                for (AuthInfo authInfo : authInfoList){
                    if(authInfo.getDeviceToken() != null && authInfo.getDeviceType() != null){
                        if(authInfo.getDeviceType().equalsIgnoreCase(FdahpUserRegUtil.ErrorCodes.DEVICE_ANDROID.getValue())){
                            jsonArray.put(authInfo.getDeviceToken().trim());
                        }else  if(authInfo.getDeviceType().equalsIgnoreCase(FdahpUserRegUtil.ErrorCodes.DEVICE_IOS.getValue())){
                            iosJsonArray.put(authInfo.getDeviceToken().trim());
                        }
                    }
                }
                deviceMap.put(FdahpUserRegUtil.ErrorCodes.DEVICE_ANDROID.getValue(),jsonArray);
                deviceMap.put(FdahpUserRegUtil.ErrorCodes.DEVICE_IOS.getValue(),iosJsonArray);


            }*/
        }
        catch (Exception e)
        {
            _log.error("getDeviceTokenOfAllUsers error:", e);
        }
        return deviceMap;
    }

    /**
     * Get the study Level user device tokens
     *
     * @param studyIds
     * @return Map<String, Map < String, JSONArray>>
     */
    public Map<String, Map<String, JSONArray>> getStudyLevelDeviceToken(String studyIds, String appIds)
    {
        Map<String, Map<String, JSONArray>> studyDeviceTokenMap = new HashMap<>();
        try
        {

            SQLFragment sql = new SQLFragment();
            sql.append("SELECT sp.studyid, string_agg(a.devicetoken, ',') as devicetoken,string_agg(a.devicetype, ',') as devicetype FROM ").append(FdahpUserRegWSSchema.getInstance().getParticipantStudies(), "sp").append(" , ").append(FdahpUserRegWSSchema.getInstance().getAuthInfo(), "a").append(" where sp.userid = a.participantid and sp.status not in('yetToJoin','withdrawn','notEligible') and a.authkey != '0' and a.remotenotificationflag=true and sp.studyid in (" + studyIds + ") and sp.applicationId in(" + appIds + ") and (a.devicetoken is not NULL and a.devicetoken != '' and a.devicetype is not NULL and a.devicetype != '') GROUP BY sp.studyid");
            ResultSet rs = new SqlSelector(FdahpUserRegWSSchema.getInstance().getSchema(), sql).getResultSet();
            if (rs != null)
            {
                while (rs.next())
                {
                    String studyid = rs.getString(1);
                    String deviceToken = rs.getString(2);
                    String deviceType = rs.getString(3);
                    if (deviceToken != null)
                    {
                        String[] deviceTokens = deviceToken.split(",");
                        String[] deviceTypes = deviceType.split(",");
                        _log.info("deviceTokens length:" + deviceTokens.length);
                        _log.info("deviceTypes length:" + deviceTypes.length);
                        if (((deviceTokens != null && deviceTokens.length > 0) && (deviceType != null && deviceTypes.length > 0)) && (deviceTokens.length == deviceTypes.length))
                        {

                            JSONArray jsonArray = new JSONArray();
                            JSONArray iosJsonArray = new JSONArray();
                            Map<String, JSONArray> deviceMap = new HashMap<>();
                            for (int i = 0; i < deviceTokens.length; i++)
                            {
                                if (deviceTypes[i] != null && deviceTypes[i].equalsIgnoreCase(FdahpUserRegUtil.ErrorCodes.DEVICE_ANDROID.getValue()))
                                {
                                    jsonArray.put(deviceTokens[i].trim());
                                }
                                else if (deviceTypes[i] != null && deviceTypes[i].equalsIgnoreCase(FdahpUserRegUtil.ErrorCodes.DEVICE_IOS.getValue()))
                                {
                                    iosJsonArray.put(deviceTokens[i].trim());
                                }

                            }
                            deviceMap.put(FdahpUserRegUtil.ErrorCodes.DEVICE_ANDROID.getValue(), jsonArray);
                            deviceMap.put(FdahpUserRegUtil.ErrorCodes.DEVICE_IOS.getValue(), iosJsonArray);

                            studyDeviceTokenMap.put(studyid, deviceMap);
                        }
                    }
                }
                rs.close();
            }
        }
        catch (Exception e)
        {
            _log.error("getStudyLevelDeviceToken error:", e);
        }
        return studyDeviceTokenMap;
    }

    /**
     * adding the audit log
     *
     * @param userId
     * @param activity
     * @param activityDetails
     * @param eventType
     * @param container
     */
    public static void addAuditEvent(String userId, String activity, String activityDetails, String eventType, String container)
    {
        FdaAuditProvider.FdaAuditEvent event = new FdaAuditProvider.FdaAuditEvent(eventType, container, activityDetails);
        try
        {
            event.setActivity(activity);
            event.setActivityDetails(activityDetails);
            event.setUserId(userId);
            AuditLogService.get().addEvent(null, event);
        }
        catch (Exception e)
        {
            _log.error("addAuditEvent error:", e);
        }
    }

    /**
     * Get the user login attempts
     *
     * @param email
     * @return LoginAttempts
     */
    public LoginAttempts getLoginAttempts(String email)
    {
        LoginAttempts loginAttempts = null;
        try
        {
            SimpleFilter filter = new SimpleFilter();
            filter.addCondition(FieldKey.fromParts("Email"), email);
            loginAttempts = new TableSelector(FdahpUserRegWSSchema.getInstance().getLoginAttempts(), filter, null).getObject(LoginAttempts.class);
        }
        catch (Exception e)
        {
            _log.error("FdahpUserRegWSManger getLoginAttempts ()", e);
        }
        return loginAttempts;
    }

    /**
     * reset the user login attempts
     *
     * @param email
     */
    public void resetLoginAttempts(String email)
    {
        try
        {
            AuditConfigurable table = (AuditConfigurable) FdahpUserRegWSSchema.getInstance().getLoginAttempts();
            table.setAuditBehavior(AuditBehaviorType.DETAILED);
            SimpleFilter filter = new SimpleFilter();
            filter.addCondition(FieldKey.fromParts("Email"), email);
            Table.delete(table, filter);
        }
        catch (Exception e)
        {
            _log.error("FdahpUserRegWSManger resetLoginAttempts ()", e);
        }
    }

    /**
     * save or update failure login attempts of an user
     *
     * @param email
     * @param applicationId
     * @param orgId
     * @return LoginAttempts
     */
    public LoginAttempts updateLoginFailureAttempts(String email, String applicationId, String orgId)
    {
        Container availableContainer = getContainer_AppID(applicationId);

        LoginAttempts loginAttempts = null;
        int count = 0;
        try
        {
            loginAttempts = getLoginAttempts(email);
            AuditConfigurable table = (AuditConfigurable) FdahpUserRegWSSchema.getInstance().getLoginAttempts();
            table.setAuditBehavior(AuditBehaviorType.DETAILED);
            if (loginAttempts != null)
            {
                if (loginAttempts.getAttempts() > 0)
                {
                    count = loginAttempts.getAttempts();
                }
                count++;
                loginAttempts.setAttempts(count);
                loginAttempts.setLastModified(FdahpUserRegUtil.getCurrentUtilDateTime());

                if (availableContainer != null)
                    loginAttempts.setContainer(availableContainer.getId());

                loginAttempts = Table.update(null, table, loginAttempts, loginAttempts.getId());
            }
            else
            {
                loginAttempts = new LoginAttempts();
                count++;
                loginAttempts.setAttempts(count);
                loginAttempts.setEmail(email);
                loginAttempts.setLastModified(FdahpUserRegUtil.getCurrentUtilDateTime());

                if (availableContainer != null)
                    loginAttempts.setContainer(availableContainer.getId());

                loginAttempts = Table.insert(null, table, loginAttempts);
            }
        }
        catch (Exception e)
        {
            _log.error("FdahpUserRegWSManger updateLoginFailureAttempts ()", e);
        }
        return loginAttempts;
    }

    /**
     * Get the study consent list
     *
     * @return List {@link StudyConsent}
     */
    public List<StudyConsent> getStudyConsentList()
    {
        List<StudyConsent> studyConsent = null;
        try
        {
            TableInfo studyConsentInfo = FdahpUserRegWSSchema.getInstance().getStudyConsent();
            SQLFragment sql = null;
            sql = new SQLFragment("SELECT * FROM " + studyConsentInfo.getSelectName() + " WHERE pdf notnull");
            studyConsent = new SqlSelector(FdahpUserRegWSSchema.getInstance().getSchema(), sql).getArrayList(StudyConsent.class);

        }
        catch (Exception e)
        {
            _log.error("getStudyConsent Error", e);
        }
        return studyConsent;
    }

    /**
     * Get the auth info by refresh token
     *
     * @param refreshToken
     * @return AuthInfo
     */
    public AuthInfo getAuthInfoByRefreshToken(String refreshToken)
    {
        DbScope dbScope = FdahpUserRegWSSchema.getInstance().getSchema().getScope();
        UserDetails addParticipant = null;

        AuthInfo authInfo = null;
        try
        {
            SimpleFilter filter = new SimpleFilter();
            filter.addCondition(FieldKey.fromParts("RefreshToken"), refreshToken);
            authInfo = new TableSelector(FdahpUserRegWSSchema.getInstance().getAuthInfo(), filter, null).getObject(AuthInfo.class);
        }
        catch (Exception e)
        {
            _log.error("getAuthInfoByRefreshToken:", e);
        }

        return authInfo;
    }


    /**
     * save or update of an appid and orgid of an user
     *
     * @param userAppDetails
     * @return message
     */
    public String saveUserAppDetails(UserAppDetails userAppDetails)
    {

        Container availableContainer = getContainer_AppID(userAppDetails.getApplicationId());
        if (availableContainer != null)
            userAppDetails.setContainer(availableContainer.getId());

        String message = FdahpUserRegUtil.ErrorCodes.FAILURE.getValue();
        DbScope dbScope = FdahpUserRegWSSchema.getInstance().getSchema().getScope();
        DbScope.Transaction transaction = dbScope.ensureTransaction();
        try
        {
            AuditConfigurable table = (AuditConfigurable) FdahpUserRegWSSchema.getInstance().getUserAppDetails();
            table.setAuditBehavior(AuditBehaviorType.DETAILED);

            if (null != userAppDetails)
            {
                if (userAppDetails != null && userAppDetails.getUserAppId() != null && userAppDetails.getUserAppId() > 0)
                {
                    Table.update(null, table, userAppDetails, userAppDetails.getUserAppId());
                }
                else
                {
                    Table.insert(null, table, userAppDetails);
                }
                message = FdahpUserRegUtil.ErrorCodes.SUCCESS.getValue();
            }

        }
        catch (Exception e)
        {
            _log.error("saveUserAppDetails:", e);
        }
        transaction.commit();
        return message;
    }

    /**
     * Validating the appid and orgId of an user
     *
     * @param appId
     * @return boolean true/false
     * @Param orgId
     */
    public String validatedUserAppDetails(String userId, String appId, String orgId, boolean isLoginAction)
    {
        String errorMessage = "";
        List<UserAppDetails> userAppDetailsList = null;
        try
        {
            TableInfo userAppDetails = FdahpUserRegWSSchema.getInstance().getUserAppDetails();
            SQLFragment sql = new SQLFragment("SELECT * FROM " + userAppDetails.getSelectName() + " WHERE userid ='" + userId + "' and orgid = '" + orgId + "' ORDER BY createdon");
            userAppDetailsList = new SqlSelector(FdahpUserRegWSSchema.getInstance().getSchema(), sql).getArrayList(UserAppDetails.class);
            if (userAppDetailsList != null && userAppDetailsList.size() > 0)
            {
                if (isLoginAction)
                {
                    for (UserAppDetails appDetails : userAppDetailsList)
                    {
                        if (appDetails.getApplicationId().equals(appId))
                        {
                            errorMessage = FdahpUserRegUtil.ErrorCodes.SUCCESS.getValue();
                            break;
                        }
                    }
                    if (StringUtils.isEmpty(errorMessage))
                    {
                        //save orgid and appid for user start
                        UserAppDetails newUserAppDetails = new UserAppDetails();
                        newUserAppDetails.setUserId(userId);
                        newUserAppDetails.setOrgId(orgId);
                        newUserAppDetails.setApplicationId(appId);
                        newUserAppDetails.setCreatedOn(new Date());
                        String message = FdahpUserRegWSManager.get().saveUserAppDetails(newUserAppDetails);
                        errorMessage = FdahpUserRegUtil.ErrorCodes.SUCCESS.getValue();
                        //save orgId and appid for user end
                    }
                }
                else
                {
                    errorMessage = FdahpUserRegUtil.ErrorCodes.APP_EXIST_NOTEXIST.getValue();
                }
            }
            else
            {
                if (isLoginAction)
                    errorMessage = FdahpUserRegUtil.ErrorCodes.LOGIN_ORG_NOTEXIST.getValue();
                else
                    errorMessage = FdahpUserRegUtil.ErrorCodes.ORG_NOTEXIST.getValue();
            }
        }
        catch (Exception e)
        {
            _log.error("FdahpUserRegWSManger validatedUserAppDetails ()", e);
        }
        _log.info("ErrorMessage :" + errorMessage);
        return errorMessage;
    }

    /**
     * Validating the appid and orgId of an user
     *
     * @param appId
     * @return boolean true/false
     * @Param orgId
     */
    public String validatedUserAppDetailsByAllApi(String userId, String email, String appId, String orgId)
    {
        String message = "";
        List<UserAppDetails> userAppDetailsList = null;
        UserDetails userDetails = null;
        try
        {
            if (StringUtils.isNotEmpty(email))
            {
                SimpleFilter filter = new SimpleFilter();
                filter.addCondition(FieldKey.fromParts("Email"), email);
                filter.addCondition(FieldKey.fromParts("ApplicationId"), appId);
                filter.addCondition(FieldKey.fromParts("OrgId"), orgId);
                userDetails = new TableSelector(FdahpUserRegWSSchema.getInstance().getParticipantDetails(), filter, null).getObject(UserDetails.class);
                if (userDetails != null)
                    userId = userDetails.getUserId();
            }
            if (StringUtils.isNotEmpty(userId))
            {
                SimpleFilter filter1 = new SimpleFilter();
                filter1.addCondition(FieldKey.fromParts("UserId"), userId);
                filter1.addCondition(FieldKey.fromParts("ApplicationId"), appId);
                filter1.addCondition(FieldKey.fromParts("OrgId"), orgId);
                userDetails = new TableSelector(FdahpUserRegWSSchema.getInstance().getParticipantDetails(), filter1, null).getObject(UserDetails.class);
                if (userDetails != null)
                {

                    TableInfo userAppDetails = FdahpUserRegWSSchema.getInstance().getUserAppDetails();
                    SQLFragment sql = new SQLFragment("SELECT * FROM " + userAppDetails.getSelectName() + " WHERE userid ='" + userId
                            + "' and orgid = '" + orgId + "' and applicationid='" + appId + "' ORDER BY createdon");
                    userAppDetailsList = new SqlSelector(FdahpUserRegWSSchema.getInstance().getSchema(), sql).getArrayList(UserAppDetails.class);
                    if (userAppDetailsList != null && userAppDetailsList.size() > 0)
                    {
                        message = FdahpUserRegUtil.ErrorCodes.SUCCESS.getValue();
                    }
                    else
                    {
                        message = FdahpUserRegUtil.ErrorCodes.ORG_NOTEXIST.getValue();
                    }
                }
                else
                {
                    message = FdahpUserRegUtil.ErrorCodes.ACCOUNT_DEACTIVATE_ERROR_MSG.getValue();
                }
            }
            else
            {
                message = FdahpUserRegUtil.ErrorCodes.ACCOUNT_DEACTIVATE_ERROR_MSG.getValue();
            }
        }
        catch (Exception e)
        {
            _log.error("FdahpUserRegWSManger validatedUserAppDetailsByAllApi ()", e);
        }
        return message;
    }

    /**
     * Get the auth info by refresh token
     *
     * @param appId
     * @return AppPropertiesDetails
     */
    public AppPropertiesDetails getAppPropertiesDetailsByAppId(String appId)
    {
        DbScope dbScope = FdahpUserRegWSSchema.getInstance().getSchema().getScope();
        AppPropertiesDetails appPropertiesDetails = null;
        try
        {
            SimpleFilter filter = new SimpleFilter();
            filter.addCondition(FieldKey.fromParts("AppId"), appId);
            appPropertiesDetails = new TableSelector(FdahpUserRegWSSchema.getInstance().getAppPropertiesDetails(), filter, null).getObject(AppPropertiesDetails.class);
        }
        catch (Exception e)
        {
            _log.error("getAppPropertiesDetailsByAppId:", e);
        }
        return appPropertiesDetails;
    }

    public String saveAppPropertiesDetails(AppPropertiesDetails appPropertiesDetails)
    {
        Container availableContainer = getContainer_AppID(appPropertiesDetails.getAppId());
        if (availableContainer != null)
            appPropertiesDetails.setContainer(availableContainer.getId());

        String message = FdahpUserRegUtil.ErrorCodes.FAILURE.getValue();
        DbScope dbScope = FdahpUserRegWSSchema.getInstance().getSchema().getScope();
        DbScope.Transaction transaction = dbScope.ensureTransaction();
        try
        {
            AuditConfigurable table = (AuditConfigurable) FdahpUserRegWSSchema.getInstance().getAppPropertiesDetails();
            table.setAuditBehavior(AuditBehaviorType.DETAILED);

            SQLFragment sql = new SQLFragment("SELECT * FROM " + FdahpUserRegWSSchema.getInstance().getAppPropertiesDetails().getSelectName() + " WHERE appId ='" + appPropertiesDetails.getAppId() + "'");
            AppPropertiesDetails appPropertiesDetails1 = new SqlSelector(FdahpUserRegWSSchema.getInstance().getSchema(), sql).getObject(AppPropertiesDetails.class);

            if (appPropertiesDetails != null)
            {
                if (appPropertiesDetails1 != null)
                {
                    Table.update(null, table, appPropertiesDetails, appPropertiesDetails1.getId());
                }
                else
                {
                    Table.insert(null, table, appPropertiesDetails);
                }
                message = FdahpUserRegUtil.ErrorCodes.SUCCESS.getValue();
            }
        }
        catch (Exception e)
        {
            _log.error("appPropertiesDetails:", e);
        }
        transaction.commit();
        return message;
    }

    private Container getContainer_AppID(String postedAppId)
    {
        Container appIdContainer = null;
        Module module = ModuleLoader.getInstance().getModule(FdahpUserRegWSModule.NAME);
        ModuleProperty mp = module.getModuleProperties().get("StudyId");

        List<Container> all = ContainerManager.getChildren(ContainerManager.getRoot());
//        for (Container rootContainer : all)
//        {
//            if (rootContainer.getName().equalsIgnoreCase(module.getName()))
//            {
//                all = ContainerManager.getChildren(rootContainer);
                for (Container appContainer : all)
                {
                    if (postedAppId.equalsIgnoreCase(mp.getValueContainerSpecific(appContainer)))
                    {
                        appIdContainer = appContainer;
                        break;
                    }
                }
//                break;
//            }
//        }
        if (appIdContainer == null)
        {
            _log.error("container not available for AppID " + postedAppId);
        }
        return appIdContainer;
    }

    private Container getContainer_StudyID(String postedAppId, String postedStudyId)
    {
        Container studyIdContainer = null;
        Container appIdContainer = null;
        Module module = ModuleLoader.getInstance().getModule(FdahpUserRegWSModule.NAME);
        ModuleProperty mp = module.getModuleProperties().get("StudyId");

        List<Container> all = ContainerManager.getChildren(ContainerManager.getRoot());
//        for (Container rootContainer : all)
//        {
//            if (rootContainer.getName().equalsIgnoreCase(module.getName()))
//            {
//                all = ContainerManager.getChildren(rootContainer);

                for (Container appContainer : all)
                {
                    if (postedAppId.equalsIgnoreCase(mp.getValueContainerSpecific(appContainer)))
                    {
                        appIdContainer = appContainer;
                        List<Container> allStudy = ContainerManager.getChildren(appContainer);
                        for (Container studyContainer : allStudy)
                        {
                            if (postedStudyId.equalsIgnoreCase(mp.getValueContainerSpecific(studyContainer)))
                            {
                                studyIdContainer = studyContainer;
                                break;
                            }
                        }
                        break;
                    }
                }
//            }
//        }

        if (studyIdContainer == null)
        {
            _log.error("container not available for StudyID" + postedStudyId);
            if (appIdContainer == null)
            {
                _log.error("container not available for AppID " + postedAppId);
            }
            return appIdContainer;
        }
        else
        {
            return studyIdContainer;
        }
    }
}
