/*
 * Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * Funding Source: Food and Drug Administration ("Funding Agency") effective 18 September 2014 as Contract no.
 * HHSF22320140030I/HHSF22301006T (the "Prime Contract").
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package com.hphc.mystudies.storageModule;

import com.hphc.mystudies.notificationModule.PendingIntents;
import com.hphc.mystudies.notificationModule.model.NotificationDb;
import com.hphc.mystudies.offlineModule.model.OfflineData;
import com.hphc.mystudies.storageModule.events.DatabaseEvent;
import com.hphc.mystudies.studyAppModule.SurveyActivitiesFragment;
import com.hphc.mystudies.studyAppModule.activityBuilder.model.ActivityRun;
import com.hphc.mystudies.studyAppModule.activityBuilder.model.serviceModel.ActivityObj;
import com.hphc.mystudies.studyAppModule.activityBuilder.model.serviceModel.Steps;
import com.hphc.mystudies.studyAppModule.acvitityListModel.ActivitiesWS;
import com.hphc.mystudies.studyAppModule.acvitityListModel.ActivityListData;
import com.hphc.mystudies.studyAppModule.consent.model.EligibilityConsent;
import com.hphc.mystudies.studyAppModule.custom.Result.StepRecordCustom;
import com.hphc.mystudies.studyAppModule.studyModel.ConsentDocumentData;
import com.hphc.mystudies.studyAppModule.studyModel.ConsentPDF;
import com.hphc.mystudies.studyAppModule.studyModel.ConsentPdfData;
import com.hphc.mystudies.studyAppModule.studyModel.DashboardData;
import com.hphc.mystudies.studyAppModule.studyModel.MotivationalNotification;
import com.hphc.mystudies.studyAppModule.studyModel.NotificationData;
import com.hphc.mystudies.studyAppModule.studyModel.NotificationDbResources;
import com.hphc.mystudies.studyAppModule.studyModel.PendingIntentsResources;
import com.hphc.mystudies.studyAppModule.studyModel.Study;
import com.hphc.mystudies.studyAppModule.studyModel.StudyHome;
import com.hphc.mystudies.studyAppModule.studyModel.StudyList;
import com.hphc.mystudies.studyAppModule.studyModel.StudyResource;
import com.hphc.mystudies.studyAppModule.studyModel.StudyUpdate;
import com.hphc.mystudies.studyAppModule.studyModel.StudyUpdateListdata;
import com.hphc.mystudies.userModule.webserviceModel.Activities;
import com.hphc.mystudies.userModule.webserviceModel.ActivityData;
import com.hphc.mystudies.userModule.webserviceModel.ActivityRunPreference;
import com.hphc.mystudies.userModule.webserviceModel.Studies;
import com.hphc.mystudies.userModule.webserviceModel.StudyData;
import com.hphc.mystudies.userModule.webserviceModel.UserProfileData;
import com.hphc.mystudies.utils.AppController;

import org.researchstack.backbone.task.Task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;

public class DBServiceSubscriber {

    public static String INSERT_AND_UPDATE_OPERATION = "insertAndUpdate";
    public static String TYPE_COPY = "copy";
    public static String TYPE_COPY_UPDATE = "copyAndUpdate";
    public static final String STUDYID = "studyId";
    public static final String ACTIVITYID = "activityId";
    public static final String RUNID = "runId";
    private Realm realm;


    public void updateStudyPreference(Studies studies, double completion, double adherence) {
        realm = AppController.getRealmobj();
        realm.beginTransaction();
        studies.setCompletion((int) completion);
        studies.setAdherence((int) adherence);
        realm.commitTransaction();
        closeRealmObj(realm);
    }

    public void insert(DatabaseEvent event) {
        if (event.getmOperation().equals(INSERT_AND_UPDATE_OPERATION)) {
            realm = AppController.getRealmobj();
            realm.beginTransaction();
            if (event.getmType().equals(TYPE_COPY)) {
                realm.copyToRealm((RealmObject) event.getE());
            } else {
                realm.copyToRealmOrUpdate((RealmObject) event.getE());
            }
            realm.commitTransaction();
            closeRealmObj(realm);
        }
    }

    public ActivityListData getActivities(String studyId, Realm realm) {
        return realm.where(ActivityListData.class).equalTo(STUDYID, studyId).findFirst();
    }

    public MotivationalNotification getMotivationalNotification(String studyId, Realm realm) {
        return realm.where(MotivationalNotification.class).equalTo(STUDYID, studyId).findFirst();
    }

    public void saveMotivationalNotificationToDB(MotivationalNotification motivationalNotification) {
        realm = AppController.getRealmobj();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(motivationalNotification);
        realm.commitTransaction();
        closeRealmObj(realm);
    }

    public void deleteMotivationalNotification(final String studyId) {
        realm = AppController.getRealmobj();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                try {
                    RealmResults<MotivationalNotification> results = realm.where(MotivationalNotification.class).equalTo(STUDYID, studyId).findAll();
                    results.deleteAllFromRealm();
                } catch (Exception e) {
                }
            }
        });
        closeRealmObj(realm);
    }

    public void deleteActivityWSList(ActivityListData activityListData, String activityId) {
        realm = AppController.getRealmobj();
        realm.beginTransaction();
        for (int i = 0; i < activityListData.getActivities().size(); i++) {
            if (activityListData.getActivities().get(i).getActivityId().equals(activityId)) {
                activityListData.getActivities().remove(i);
                break;
            }
        }
        realm.commitTransaction();
        closeRealmObj(realm);
    }

    public void addActivityWSList(ActivityListData activityListData, ActivitiesWS activitiesWS) {
        realm = AppController.getRealmobj();
        realm.beginTransaction();
        activityListData.getActivities().add(activitiesWS);
        realm.commitTransaction();
        closeRealmObj(realm);
    }

    public ActivitiesWS getActivityItem(String studyId, String activityId, Realm realm) {
        ActivityListData activityListData = getActivities(studyId, realm);
        ActivitiesWS activitiesWS = null;
        if (activityListData != null && activityListData.getActivities() != null) {
            for (int i = 0; i < activityListData.getActivities().size(); i++) {
                if (activityListData.getActivities().get(i).getActivityId().equalsIgnoreCase(activityId)) {
                    activitiesWS = activityListData.getActivities().get(i);
                }
            }
        }
        return activitiesWS;
    }

    public NotificationDb getNotificationDb(String activityId, String studyId, String type, Realm realm) {
        return realm.where(NotificationDb.class).equalTo(ACTIVITYID, activityId).equalTo(STUDYID, studyId).equalTo("type", type).findFirst();
    }

    public RealmResults<NotificationDb> getNotificationDbByDate(Date startDate, Realm realm) {
        return realm.where(NotificationDb.class).lessThanOrEqualTo("dateTime", startDate).greaterThanOrEqualTo("endDateTime", startDate).findAll();
    }

    public RealmResults<NotificationDbResources> getNotificationDbResourcesByDate(Date startDate, Realm realm) {
        return realm.where(NotificationDbResources.class).lessThanOrEqualTo("dateTime", startDate).findAll();
    }

    public RealmResults<NotificationDbResources> getNotificationDbResources(String activityId, String studyId, String type, Realm realm) {
        try {
            return realm.where(NotificationDbResources.class).equalTo(ACTIVITYID, activityId).equalTo(STUDYID, studyId).equalTo("type", type).findAllSorted("id", Sort.ASCENDING);
        } catch (Exception e) {
            return null;
        }
    }

    // getUserPreference
    public StudyData getStudyPreference(Realm realm) {
        return realm.where(StudyData.class).findFirst();
    }

    public ActivityData getActivityPreference(String studyId, Realm realm) {
        return realm.where(ActivityData.class).equalTo(STUDYID, studyId).findFirst();
    }

    public Activities getActivityPreferenceBySurveyId(String studyId, String surveyId, Realm realm) {
        ActivityData activityData = getActivityPreference(studyId, realm);
        Activities activities = null;
        if (activityData != null && activityData.getActivities() != null)
            for (int i = 0; i < activityData.getActivities().size(); i++) {
                if (activityData.getActivities().get(i).getStudyId().equalsIgnoreCase(studyId) && activityData.getActivities().get(i).getActivityId().equalsIgnoreCase(surveyId)) {
                    activities = activityData.getActivities().get(i);
                    break;
                }
            }
        return activities;
    }


    public RealmResults<ActivityRun> getAllActivityRunforDate(String activityID, String studyId, Date date, Realm realm) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 1);
        calendar.set(Calendar.AM_PM, Calendar.AM);

        Calendar calendar1 = Calendar.getInstance();
        calendar1.setTime(date);
        calendar1.set(Calendar.HOUR, 11);
        calendar1.set(Calendar.MINUTE, 59);
        calendar1.set(Calendar.SECOND, 59);
        calendar1.set(Calendar.MILLISECOND, 99);
        calendar1.set(Calendar.AM_PM, Calendar.PM);

        return realm.where(ActivityRun.class).equalTo(STUDYID, studyId).equalTo(ACTIVITYID, activityID).greaterThanOrEqualTo("startDate", calendar.getTime()).lessThanOrEqualTo("endDate", calendar1.getTime()).findAll();
    }

    public int getActivityRunForStatsAndCharts(String activityID, String studyId, Date date, Realm realm) {
        RealmResults<ActivityRun> activityRuns = getAllActivityRunFromDB(studyId, activityID, realm);
        int run = 1;
        boolean available = false;
        for (int i = 0; i < activityRuns.size(); i++) {
            run = activityRuns.get(i).getRunId();
            if ((activityRuns.get(i).getStartDate().equals(date) || (date.after(activityRuns.get(i).getStartDate())) && ((activityRuns.get(i).getEndDate().equals(date) || date.before(activityRuns.get(i).getEndDate()))))) {
                available = true;
            }
            if (date.before(activityRuns.get(i).getEndDate())) {
                if (!available)
                    run = run - 1;
                break;
            }
        }
        if (run <= 0) {
            run = 1;
        }
        return run;
    }

    public ActivityRun getPreviousActivityRunFromDB(String activityID, String studyId, Date currentDate, Realm realm) {
        return realm.where(ActivityRun.class).equalTo(STUDYID, studyId).equalTo(ACTIVITYID, activityID).lessThanOrEqualTo("endDate", currentDate).findAllSorted(RUNID, Sort.DESCENDING).first(null);
    }

    public RealmResults<ActivityRun> getAllActivityRunFromDB(String studyId, String activityID, Realm realm) {
        return realm.where(ActivityRun.class).equalTo(STUDYID, studyId).equalTo(ACTIVITYID, activityID).findAllSorted(RUNID, Sort.ASCENDING);
    }

    public void updateActivityRunToDB(String activityID, String studyId, int runId) {
        realm = AppController.getRealmobj();
        ActivityRun activityRun = realm.where(ActivityRun.class).equalTo(STUDYID, studyId).equalTo(ACTIVITYID, activityID).equalTo(RUNID, runId).findFirst();
        realm.beginTransaction();
        activityRun.setCompleted(true);
        realm.commitTransaction();
        closeRealmObj(realm);
    }

    public void saveStudyListToDB(Study study) {
        realm = AppController.getRealmobj();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(study);
        realm.commitTransaction();
        closeRealmObj(realm);
    }

    public void savePendingIntentId(PendingIntents pendingIntents) {
        realm = AppController.getRealmobj();
        realm.beginTransaction();
        realm.copyToRealm(pendingIntents);
        realm.commitTransaction();
        closeRealmObj(realm);
    }

    public void savePendingIntentIdResources(PendingIntentsResources pendingIntents) {
        realm = AppController.getRealmobj();
        realm.beginTransaction();
        realm.copyToRealm(pendingIntents);
        realm.commitTransaction();
        closeRealmObj(realm);
    }

    public RealmResults<PendingIntents> getPendingIntentId(Realm realm) {
        return realm.where(PendingIntents.class).findAll();
    }

    public RealmResults<PendingIntents> getPendingIntentIdByIds(Realm realm, String activityId, String studyId) {
        return realm.where(PendingIntents.class).equalTo(STUDYID, studyId).equalTo(ACTIVITYID, activityId).findAll();
    }

    public RealmResults<PendingIntentsResources> getPendingIntentIdResources(Realm realm) {
        return realm.where(PendingIntentsResources.class).findAll();
    }

    public RealmResults<PendingIntentsResources> getPendingIntentIdResourcesByIds(Realm realm, String activityId, String studyId) {
        return realm.where(PendingIntentsResources.class).equalTo(STUDYID, studyId).equalTo(ACTIVITYID, activityId).findAll();
    }

    public StudyData getStudyPreferences(Realm realm) {
        return realm.where(StudyData.class).findFirst();
    }

    // replaced saveUserPreferencesToDB
    public void saveStudyPreferencesToDB(StudyData studies) {
        realm = AppController.getRealmobj();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(studies);
        realm.commitTransaction();
        closeRealmObj(realm);
    }

    // updateUserPreferenceToDb
    public void updateStudyPreferenceToDb(String lastUpdatedStudyId, boolean lastUpdatedBookMark, String lastUpdatedStatusStatus) {
        realm = AppController.getRealmobj();
        Studies studies = realm.where(Studies.class).equalTo(STUDYID, lastUpdatedStudyId).findFirst();
        realm.beginTransaction();
        if (studies == null) {
            studies = new Studies();
            studies.setStudyId(lastUpdatedStudyId);
            studies.setBookmarked(lastUpdatedBookMark);
            studies.setStatus(lastUpdatedStatusStatus);

            StudyData studyData = getStudyPreferencesListFromDB(realm);
            if (studyData.getStudies() == null) {
                studyData.setStudies(new RealmList<Studies>());
            }
            studyData.getStudies().add(studies);
        } else {
            studies.setBookmarked(lastUpdatedBookMark);
            studies.setStatus(lastUpdatedStatusStatus);
        }

        realm.copyToRealmOrUpdate(studies);
        realm.commitTransaction();
        closeRealmObj(realm);
    }


    public void updateActivityPreferenceVersion(String version, Activities activities) {
        realm = AppController.getRealmobj();
        realm.beginTransaction();
        if (activities != null) {
            activities.setActivityVersion(version);
        }

        realm.commitTransaction();
        closeRealmObj(realm);
    }

    public void updateNotificationToDb(NotificationDb notificationDb) {
        realm = AppController.getRealmobj();
        realm.beginTransaction();
        if (notificationDb != null) {
            realm.copyToRealm(notificationDb);
        }
        realm.commitTransaction();
        closeRealmObj(realm);
    }

    public void updateNotificationToDb(NotificationDbResources notificationDb) {
        realm = AppController.getRealmobj();
        realm.beginTransaction();
        if (notificationDb != null) {
            realm.copyToRealm(notificationDb);
        }
        realm.commitTransaction();
        closeRealmObj(realm);
    }

    public Study getStudyListFromDB(Realm realm) {
        return realm.where(Study.class).findFirst();
    }

    // getUserPreferencesListFromDB
    public StudyData getStudyPreferencesListFromDB(Realm realm) {
        return realm.where(StudyData.class).findFirst();
    }

    private ActivityData getActivityPreferencesListFromDB(String studyId, Realm realm) {
        return realm.where(ActivityData.class).equalTo(STUDYID, studyId).findFirst();
    }

    public StudyList getStudiesDetails(String studyId, Realm realm) {
        return realm.where(StudyList.class).equalTo(STUDYID, studyId).findFirst();
    }

    public void saveStudyInfoToDB(StudyHome studyHome) {
        realm = AppController.getRealmobj();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(studyHome);
        realm.commitTransaction();
        closeRealmObj(realm);
    }

    public void saveStudyDashboardToDB(DashboardData dashboardData) {
        realm = AppController.getRealmobj();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(dashboardData);
        realm.commitTransaction();
        closeRealmObj(realm);
    }

    public void saveActivityState(ActivitiesWS activitiesWS, Realm realm) {
        realm.beginTransaction();
        activitiesWS.setState(SurveyActivitiesFragment.DELETE);
        realm.commitTransaction();
    }

    public DashboardData getDashboardDataFromDB(String studyId, Realm realm) {
        return realm.where(DashboardData.class).equalTo(STUDYID, studyId).findFirst();
    }

    public void deleteStudyInfoFromDb(final String studyId) {
        realm = AppController.getRealmobj();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                try {
                    RealmResults<StudyHome> rows = realm.where(StudyHome.class).equalTo("mStudyId", studyId).findAll();
                    for (int i = 0; i < rows.size(); i++) {
                        if (rows.get(i).getInfo() != null) {
                            rows.get(i).getInfo().deleteAllFromRealm();
                        }
                        if (rows.get(i).getAnchorDate() != null) {
                            if (rows.get(i).getAnchorDate().getQuestionInfo() != null) {
                                rows.get(i).getAnchorDate().getQuestionInfo().deleteFromRealm();
                            }
                            rows.get(i).getAnchorDate().deleteFromRealm();
                        }
                        if (rows.get(i).getBranding() != null) {
                            rows.get(i).getBranding().deleteFromRealm();
                        }
                        if (rows.get(i).getWithdrawalConfig() != null) {
                            rows.get(i).getWithdrawalConfig().deleteFromRealm();
                        }
                        rows.deleteFromRealm(i);
                    }
                } catch (Exception e) {
                }
            }
        });
        closeRealmObj(realm);
    }

    public void deleteActivityRunsFromDb(final String activityId, final String studyId) {
        realm = AppController.getRealmobj();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                try {
                    RealmResults<ActivityRun> rows = realm.where(ActivityRun.class).equalTo(ACTIVITYID, activityId).equalTo(STUDYID, studyId).findAll();
                    rows.deleteAllFromRealm();
                } catch (Exception e) {
                }
            }
        });
        closeRealmObj(realm);
    }

    public void deleteActivityRunsFromDbByStudyID(final String studyId) {
        realm = AppController.getRealmobj();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                try {
                    RealmResults<ActivityRun> rows = realm.where(ActivityRun.class).equalTo(STUDYID, studyId).findAll();
                    rows.deleteAllFromRealm();
                } catch (Exception e) {
                }
            }
        });
        closeRealmObj(realm);
    }

    public void deleteResponseFromDb(final String studyId, Realm realm) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                try {
                    RealmResults<StepRecordCustom> rows = realm.where(StepRecordCustom.class).equalTo(STUDYID, studyId).findAll();
                    if (rows != null) {
                        while (rows.size() > 0) {
                            if (rows.get(0).getTextChoices() != null) {
                                rows.get(0).getTextChoices().deleteAllFromRealm();
                            }
                            rows.deleteFromRealm(0);
                        }
                    }
                } catch (Exception e) {
                }
            }
        });
    }

    public void deleteActivityObjectFromDb(final String activityId, final String studyId) {
        realm = AppController.getRealmobj();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                try {
                    RealmResults<ActivityObj> rows = realm.where(ActivityObj.class).equalTo("surveyId", activityId).equalTo(STUDYID, studyId).findAll();
                    for (int i = 0; i < rows.size(); i++) {
                        if (rows.get(i).getSteps() != null) {
                            for (int j = 0; j < rows.get(i).getSteps().size(); j++) {
                                if (rows.get(i).getSteps().get(j).getDestinations() != null) {
                                    rows.get(i).getSteps().get(j).getDestinations().deleteAllFromRealm();
                                }
                                if (rows.get(i).getSteps().get(j).getSteps() != null) {
                                    rows.get(i).getSteps().get(j).getSteps().deleteAllFromRealm();
                                }
                            }
                            rows.get(i).getSteps().deleteAllFromRealm();
                        }
                        rows.deleteFromRealm(i);
                    }
                } catch (Exception e) {
                }
            }
        });
        closeRealmObj(realm);
    }


    public void deleteAllRun(final RealmResults<ActivityRun> activityRuns) {
        realm = AppController.getRealmobj();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                try {
                    activityRuns.deleteAllFromRealm();
                } catch (Exception e) {
                }
            }
        });
        closeRealmObj(realm);
    }


    public void deleteResponseDataFromDb(final String taskId) {
        realm = AppController.getRealmobj();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                try {
                    RealmResults<StepRecordCustom> rows = realm.where(StepRecordCustom.class).equalTo("taskId", taskId).findAll();
                    for (int i = 0; i < rows.size(); i++) {
                        if (rows.get(i).getTextChoices() != null) {
                            rows.get(i).getTextChoices().deleteAllFromRealm();
                        }
                        rows.deleteFromRealm(i);
                    }
                } catch (Exception e) {
                }
            }
        });
        closeRealmObj(realm);
    }

    public StudyHome getStudyInfoListFromDB(String mStudyId, Realm realm) {
        return realm.where(StudyHome.class).equalTo("mStudyId", mStudyId).findFirst();
    }

    public StepRecordCustom getResultFromDB(String taskStepID, Realm realm) {
        return realm.where(StepRecordCustom.class).equalTo("taskStepID", taskStepID).findFirst();
    }

    public StepRecordCustom getSurveyResponseFromDB(String activityID, String stepId, Realm realm) {
        return realm.where(StepRecordCustom.class).equalTo("activityID", activityID).equalTo("stepId", stepId).findFirst();
    }

    public EligibilityConsent getConsentMetadata(String studyId, Realm realm) {
        return realm.where(EligibilityConsent.class).equalTo(STUDYID, studyId).findFirst();
    }

    public NotificationData getNotificationFromDB(Realm realm) {
        return realm.where(NotificationData.class).findFirst();
    }

    public void saveConsentDocumentToDB(ConsentDocumentData mConsentDocumentData) {
        realm = AppController.getRealmobj();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(mConsentDocumentData);
        realm.commitTransaction();
        closeRealmObj(realm);
    }

    public StudyUpdate getStudyUpdateById(String studyId, Realm realm) {
        return realm.where(StudyUpdate.class).equalTo(STUDYID, studyId).findFirst();
    }

    public void saveStudyUpdateListdataToDB(StudyUpdateListdata studyUpdateListdata) {
        realm = AppController.getRealmobj();
        StudyUpdateListdata studyUpdateListdata1 = realm.where(StudyUpdateListdata.class).findFirst();
        if (studyUpdateListdata1 == null) {
            realm.beginTransaction();
            realm.copyToRealmOrUpdate(studyUpdateListdata);
            realm.commitTransaction();
        } else {
            realm.beginTransaction();
            boolean available = false;
            if (studyUpdateListdata.getStudyUpdates() != null) {
                for (int i = 0; i < studyUpdateListdata1.getStudyUpdates().size(); i++) {
                    if (studyUpdateListdata.getStudyUpdates().get(0).getStudyId().equalsIgnoreCase(studyUpdateListdata1.getStudyUpdates().get(i).getStudyId())) {
                        studyUpdateListdata1.getStudyUpdates().get(i).setCurrentVersion(studyUpdateListdata.getStudyUpdates().get(0).getCurrentVersion());
                        studyUpdateListdata1.getStudyUpdates().get(i).setMessage(studyUpdateListdata.getStudyUpdates().get(0).getMessage());

                        studyUpdateListdata1.getStudyUpdates().get(i).getStudyUpdateData().setActivities(studyUpdateListdata.getStudyUpdates().get(0).getStudyUpdateData().isActivities());
                        studyUpdateListdata1.getStudyUpdates().get(i).getStudyUpdateData().setConsent(studyUpdateListdata.getStudyUpdates().get(0).getStudyUpdateData().isConsent());
                        studyUpdateListdata1.getStudyUpdates().get(i).getStudyUpdateData().setInfo(studyUpdateListdata.getStudyUpdates().get(0).getStudyUpdateData().isInfo());
                        studyUpdateListdata1.getStudyUpdates().get(i).getStudyUpdateData().setResources(studyUpdateListdata.getStudyUpdates().get(0).getStudyUpdateData().isResources());
                        studyUpdateListdata1.getStudyUpdates().get(i).getStudyUpdateData().setStatus(studyUpdateListdata.getStudyUpdates().get(0).getStudyUpdateData().getStatus());

                        studyUpdateListdata1.getStudyUpdates().get(i).getUpdates().setActivities(studyUpdateListdata.getStudyUpdates().get(0).getUpdates().isActivities());
                        studyUpdateListdata1.getStudyUpdates().get(i).getUpdates().setStatus(studyUpdateListdata.getStudyUpdates().get(0).getUpdates().getStatus());
                        studyUpdateListdata1.getStudyUpdates().get(i).getUpdates().setResources(studyUpdateListdata.getStudyUpdates().get(0).getUpdates().isResources());
                        studyUpdateListdata1.getStudyUpdates().get(i).getUpdates().setInfo(studyUpdateListdata.getStudyUpdates().get(0).getUpdates().isInfo());
                        studyUpdateListdata1.getStudyUpdates().get(i).getUpdates().setConsent(studyUpdateListdata.getStudyUpdates().get(0).getUpdates().isConsent());

                        available = true;
                        break;
                    }
                }
            }
            if (!available) {
                studyUpdateListdata1.getStudyUpdates().add(studyUpdateListdata.getStudyUpdates().get(0));
            }
            realm.commitTransaction();
        }
        closeRealmObj(realm);
    }

    public NotificationDb updateNotificationNumber(NotificationDb notificationsDb, Realm realm) {
        realm.beginTransaction();
        int id = notificationsDb.getId();
        id++;
        notificationsDb.setId(id);
        realm.commitTransaction();
        return notificationsDb;
    }


    public ConsentDocumentData getConsentDocumentFromDB(String mStudyId, Realm realm) {
        return realm.where(ConsentDocumentData.class).equalTo("mStudyId", mStudyId).findFirst();
    }

    // UpdateUserPreferenceDB
    public void updateStudyPreferenceDB(String studyId, String status, String enrolleddate, String participantId, String version) {
        realm = AppController.getRealmobj();
        Studies studies = realm.where(Studies.class).equalTo(STUDYID, studyId).findFirst();
        realm.beginTransaction();
        if (studies != null) {
            studies.setStatus(status);
            studies.setEnrolledDate(enrolleddate);
            studies.setVersion(version);
            studies.setParticipantId(participantId);
        } else {
            StudyData studyData = getStudyPreferencesListFromDB(realm);
            Studies studies1 = new Studies();
            studies1.setStudyId(studyId);
            studies1.setBookmarked(false);
            studies1.setStatus(status);
            studies1.setVersion(version);
            studies1.setEnrolledDate(enrolleddate);
            studies1.setParticipantId(participantId);
            studyData.getStudies().add(studies1);
        }
        realm.commitTransaction();
        closeRealmObj(realm);
    }

    public void updateStudyPreferenceVersionDB(String studyId, String version) {
        realm = AppController.getRealmobj();
        Studies studies = realm.where(Studies.class).equalTo(STUDYID, studyId).findFirst();
        realm.beginTransaction();
        if (studies != null) {
            studies.setVersion(version);
        } else {
            StudyData studyData = getStudyPreferencesListFromDB(realm);
            Studies studies1 = new Studies();
            studies1.setStudyId(studyId);
            studies1.setBookmarked(false);
            studies1.setVersion(version);
            studyData.getStudies().add(studies1);
        }
        realm.commitTransaction();
        closeRealmObj(realm);
    }


    public void updateStudyWithddrawnDB(String studyId, String status) {
        realm = AppController.getRealmobj();
        Studies studies = realm.where(Studies.class).equalTo(STUDYID, studyId).findFirst();
        realm.beginTransaction();
        if (studies != null) {
            studies.setStatus(status);
            studies.setAdherence(0);
            studies.setCompletion(0);
        }
        realm.commitTransaction();
        closeRealmObj(realm);
    }

    public void updateStudyWithStudyId(String studyId, Study study, String version) {
        realm = AppController.getRealmobj();
        realm.beginTransaction();
        for (int i = 0; i < study.getStudies().size(); i++) {
            if (study.getStudies().get(i).getStudyId().equalsIgnoreCase(studyId)) {
                study.getStudies().get(i).setStudyVersion(version);
                break;
            }
        }
        realm.commitTransaction();
        closeRealmObj(realm);
    }

    public void savePdfData(String studyId, String pdfPath) {
        realm = AppController.getRealmobj();
        ConsentPdfData consentPdfData;
        consentPdfData = realm.where(ConsentPdfData.class).equalTo("StudyId", studyId).findFirst();
        realm.beginTransaction();
        if (consentPdfData == null) {
            consentPdfData = new ConsentPdfData();
            consentPdfData.setStudyId(studyId);
        }
        consentPdfData.setPdfPath(pdfPath);
        realm.copyToRealmOrUpdate(consentPdfData);
        realm.commitTransaction();
        closeRealmObj(realm);
    }


    // updateUserPreferenceActivityDB
    public void updateActivityPreferenceDB(String activityId, String studyId, int runId, String status, int totalRun, int completedRun, int missedRun, String activityVersion) {
        realm = AppController.getRealmobj();
        Activities activities = getActivityPreferenceBySurveyId(studyId, activityId, realm);
        realm.beginTransaction();
        ActivityRunPreference activityRunPreference = new ActivityRunPreference();
        if (activities != null) {
            activities.setStatus(status);
            activities.setActivityRunId("" + runId);
            activities.getActivityRun().setCompleted(completedRun);
            activities.getActivityRun().setMissed(missedRun);
            activities.getActivityRun().setTotal(totalRun);
            activities.setActivityVersion(activityVersion);
        } else {
            ActivityData activityData = getActivityPreferencesListFromDB(studyId, realm);
            Activities activities1 = new Activities();
            activities1.setStatus(status);
            activities1.setStudyId(studyId);
            activities1.setActivityId(activityId);
            activities1.setActivityRunId("" + runId);
            activities1.setActivityVersion(activityVersion);
            activities1.setBookmarked("false");
            activityRunPreference.setTotal(totalRun);
            activityRunPreference.setCompleted(completedRun);
            activityRunPreference.setMissed(missedRun);
            activities1.setActivityRun(activityRunPreference);
            if (activityData != null && activityData.getActivities() != null) {
                activityData.getActivities().add(activities1);
            } else if (activityData != null) {
                RealmList<Activities> activities2 = new RealmList<>();
                activities2.add(activities1);
                activityData.setActivities(activities2);
            } else {
                activityData = new ActivityData();
                RealmList<Activities> activities2 = new RealmList<>();
                activities2.add(activities1);
                activityData.setActivities(activities2);
                realm.copyToRealm(activityData);
            }
        }
        realm.commitTransaction();
        closeRealmObj(realm);
    }

    public Studies getStudies(String studyId, Realm realm) {
        return realm.where(Studies.class).equalTo(STUDYID, studyId).findFirst();
    }

    public void deleteDb() {
        try {
            realm = AppController.getRealmobj();
            realm.beginTransaction();
            realm.deleteAll();
            realm.commitTransaction();
            closeRealmObj(realm);
        } catch (Exception e) {
        }
    }

    public StepRecordCustom getSavedSteps(Task task, Realm realm) {
        return realm.where(StepRecordCustom.class).equalTo("taskId", task.getIdentifier()).findAllSorted("id", Sort.DESCENDING).first(null);
    }

    public RealmResults<StepRecordCustom> getStepRecordCustom(Task task, Realm realm) {
        return realm.where(StepRecordCustom.class).equalTo("taskId", task.getIdentifier()).findAll();

    }

    public StepRecordCustom getStepRecordCustomById(String taskResultId, String stepResultId, Realm realm) {
        return realm.where(StepRecordCustom.class).equalTo("taskStepID", taskResultId + "_" + stepResultId).findFirst();

    }

    public Number getStepRecordCustomId(Realm realm) {
        return realm.where(StepRecordCustom.class).max("id");
    }

    public void updateStepRecord(StepRecordCustom stepRecordCustom) {
        realm = AppController.getRealmobj();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(stepRecordCustom);
        realm.commitTransaction();
        closeRealmObj(realm);
    }

    public void updateActivityState(ActivityData activityData) {
        realm = AppController.getRealmobj();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(activityData);
        realm.commitTransaction();
        closeRealmObj(realm);
    }

    public ActivityObj getActivityBySurveyId(String studyId, String surveyId, Realm realm) {
        return realm.where(ActivityObj.class).equalTo(STUDYID, studyId).equalTo("surveyId", surveyId).findFirst();
    }

    public Steps getSteps(String identifier, Realm realm) {
        return realm.where(Steps.class).equalTo("key", identifier).findFirst();
    }

    public Steps updateSteps(Steps step, String identifier, Realm realm) {
        Steps steps = new Steps();
        realm.beginTransaction();
        steps.setKey(identifier);
        steps.setDestinations(step.getDestinations());
        steps.setResultType(step.getResultType());
        steps.setText(step.getText());
        steps.setType(step.getType());
        steps.setFormat(step.getFormat());
        steps.setGroupName(step.getGroupName());
        steps.setRepeatable(step.isRepeatable());
        steps.setRepeatableText(step.getRepeatableText());
        steps.setSkippable(step.isSkippable());
        steps.setTitle(step.getTitle());
        realm.commitTransaction();
        return steps;
    }

    public void saveActivity(ActivityObj activityObj) {
        realm = AppController.getRealmobj();
        realm.beginTransaction();
        realm.copyToRealm(activityObj);
        realm.commitTransaction();
        closeRealmObj(realm);
    }

    public void saveNotification(NotificationData notificationData, Realm mRealm) {
        mRealm.beginTransaction();
        mRealm.copyToRealmOrUpdate(notificationData);
        mRealm.commitTransaction();
    }

    public RealmList<StudyList> saveStudyStatusToStudyList(RealmList<StudyList> studyListArrayList, Realm realm) {
        StudyData studyData = getStudyPreference(realm);
        if (studyData != null) {
            realm.beginTransaction();
            RealmList<Studies> userPreferenceStudies = studyData.getStudies();
            if (userPreferenceStudies != null) {
                for (int i = 0; i < userPreferenceStudies.size(); i++) {
                    for (int j = 0; j < studyListArrayList.size(); j++) {
                        if (userPreferenceStudies.get(i).getStudyId().equalsIgnoreCase(studyListArrayList.get(j).getStudyId())) {
                            studyListArrayList.get(j).setBookmarked(userPreferenceStudies.get(i).isBookmarked());
                            studyListArrayList.get(j).setStudyStatus(userPreferenceStudies.get(i).getStatus());
                        }
                    }
                }
            }
            realm.commitTransaction();
        }
        return studyListArrayList;
    }

    public RealmList<StudyList> clearStudyList(RealmList<StudyList> studyListArrayList, Realm realm) {
        realm.beginTransaction();
        studyListArrayList.clear();
        realm.commitTransaction();
        return studyListArrayList;
    }

    public RealmList<StudyList> updateStudyList(RealmList<StudyList> studyListArrayList, ArrayList<StudyList> studyLists, Realm realm) {
        realm.beginTransaction();
        studyListArrayList.addAll(studyLists);
        realm.commitTransaction();
        return studyListArrayList;
    }

    public ConsentPdfData getPDFPath(String studyId, Realm realm) {
        return realm.where(ConsentPdfData.class).equalTo("StudyId", studyId).findFirst();
    }

    public RealmResults<StepRecordCustom> getResult(String activityId, String key, Date startDate, Date endDate, Realm realm) {
        Calendar calendar = Calendar.getInstance();
        if (startDate != null)
            calendar.setTime(startDate);
        else
            calendar.setTime(new Date());
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.AM_PM, Calendar.AM);

        Calendar calendar1 = Calendar.getInstance();
        if (endDate != null)
            calendar1.setTime(endDate);
        else
            calendar1.setTime(new Date());
        calendar1.set(Calendar.HOUR, 11);
        calendar1.set(Calendar.MINUTE, 59);
        calendar1.set(Calendar.SECOND, 59);
        calendar1.set(Calendar.MILLISECOND, 0);
        calendar1.set(Calendar.AM_PM, Calendar.PM);
        return realm.where(StepRecordCustom.class).equalTo("activityID", activityId).equalTo("stepId", key).findAllSorted("completed", Sort.ASCENDING);
    }

    public RealmResults<StepRecordCustom> getResultForStat(String activityId, String key, Date startDate, Date endDate, Realm realm) {
        Calendar calendar = Calendar.getInstance();
        if (startDate != null)
            calendar.setTime(startDate);
        else
            calendar.setTime(new Date());
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.set(Calendar.AM_PM, Calendar.AM);

        Calendar calendar1 = Calendar.getInstance();
        if (endDate != null)
            calendar1.setTime(endDate);
        else
            calendar1.setTime(new Date());
        calendar1.set(Calendar.HOUR, 11);
        calendar1.set(Calendar.MINUTE, 59);
        calendar1.set(Calendar.SECOND, 59);
        calendar1.set(Calendar.MILLISECOND, 0);
        calendar1.set(Calendar.AM_PM, Calendar.PM);
        return realm.where(StepRecordCustom.class).equalTo("activityID", activityId).equalTo("stepId", key).greaterThanOrEqualTo("started", calendar.getTime()).lessThanOrEqualTo("completed", calendar1.getTime()).findAll();
    }

    public void saveResourceList(StudyResource studyResourceData) {
        realm = AppController.getRealmobj();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(studyResourceData);
        realm.commitTransaction();
        closeRealmObj(realm);
    }

    public void deleteResourcesFromDb(final String studyId) {
        realm = AppController.getRealmobj();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                try {
                    RealmResults<StudyResource> rows = realm.where(StudyResource.class).equalTo("mStudyId", studyId).findAll();

                    for (int i = 0; i < rows.size(); i++) {
                        if (rows.get(i).getResources() != null) {
                            rows.get(i).getResources().deleteAllFromRealm();
                        }
                        rows.deleteFromRealm(i);
                    }
                } catch (Exception e) {
                }
            }
        });
        closeRealmObj(realm);
    }

    public Studies getParticipantId(String studyId, Realm realm) {
        return realm.where(Studies.class).equalTo(STUDYID, studyId).findFirst();
    }

    public RealmResults<Studies> getAllStudyIds(Realm realm) {
        return realm.where(Studies.class).equalTo("status", "inProgress").findAll();
    }

    public StudyList getStudyTitle(String studyId, Realm realm) {
        return realm.where(StudyList.class).equalTo(STUDYID, studyId).findFirst();
    }

    public StudyHome getWithdrawalType(String studyId, Realm realm) {
        return realm.where(StudyHome.class).equalTo("mStudyId", studyId).findFirst();
    }

    // Rajeesh
    public void deleteStudyResourceDuplicateRow(final String studyId) {
        realm = AppController.getRealmobj();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                StudyResource root = realm.where(StudyResource.class)
                        .equalTo("mStudyId", studyId)
                        .findFirst();
                if (root != null) {
                    if (root.getResources() != null) {
                        root.getResources().deleteAllFromRealm();
                    }
                    root.deleteFromRealm();
                }
            }
        });
        closeRealmObj(realm);
    }

    public StudyResource getStudyResource(String studyId, Realm realm) {
        return realm.where(StudyResource.class).equalTo("mStudyId", studyId).findFirst();
    }

    public void saveConsentPDF(ConsentPDF consentPDF) {
        realm = AppController.getRealmobj();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(consentPDF);
        realm.commitTransaction();
        closeRealmObj(realm);
    }

    public ConsentPDF getConsentPDF(String studyId, Realm realm) {
        return realm.where(ConsentPDF.class).equalTo(STUDYID, studyId).findFirst();
    }

    // get unique id
    public int getUniqueID(Realm realm) {
        try {
            return realm.where(OfflineData.class).max("number").intValue();
        } catch (Exception e) {
            return 0;
        }
    }

    // save offline data
    public void saveOfflineData(OfflineData offlineData) {
        realm = AppController.getRealmobj();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(offlineData);
        realm.commitTransaction();
        closeRealmObj(realm);
    }

    public RealmResults<OfflineData> getOfflineData(Realm realm) {
        try {
            return realm.where(OfflineData.class).findAll();
        } catch (Exception e) {
        }
        return null;
    }

    // remove data from perticular index
    public void deleteOfflineDataRow(final int index) {
        realm = AppController.getRealmobj();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                OfflineData offlineData = realm.where(OfflineData.class)
                        .equalTo("number", index)
                        .findFirst();
                if (offlineData != null)
                    offlineData.deleteFromRealm();
            }
        });
        closeRealmObj(realm);
    }

    public void deleteActivityDataRow(final String studyId) {
        realm = AppController.getRealmobj();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                ActivityData activityData = getActivityPreference(studyId, realm);
                try {
                    if (activityData != null) {

                        for (int i = 0; i < activityData.getActivities().size(); i++) {
                            if (activityData.getActivities().get(i).getActivityRun() != null) {
                                activityData.getActivities().get(i).getActivityRun().deleteFromRealm();
                            }
                            activityData.getActivities().get(i).deleteFromRealm();
                        }
                        activityData.deleteFromRealm();
                    }
                } catch (Exception e) {
                }
            }
        });
        closeRealmObj(realm);
    }

    public void deleteActivityWSData(final String studyId) {
        realm = AppController.getRealmobj();

        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                ActivityListData activityListDataDB = getActivities(studyId, realm);
                try {
                    if (activityListDataDB != null) {

                        if (activityListDataDB.getActivities() != null) {
                            for (int i = 0; i < activityListDataDB.getActivities().size(); i++) {
                                activityListDataDB.getActivities().get(i).deleteFromRealm();
                            }
                        }
                        if (activityListDataDB.getAnchorDate() != null) {
                            activityListDataDB.getAnchorDate().deleteFromRealm();
                        }
                        activityListDataDB.deleteFromRealm();
                    }
                } catch (Exception e) {
                }
            }
        });
        closeRealmObj(realm);
    }


    // remove data from 0th row
    public void removeOfflineData() {
        realm = AppController.getRealmobj();
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                try {
                    RealmResults<OfflineData> results = getOfflineData(realm);
                    results.deleteFromRealm(0);
                } catch (Exception e) {
                }
            }
        });
        closeRealmObj(realm);
    }

    // save UserProfile Data
    public void saveUserProfileData(UserProfileData userProfileData) {
        realm = AppController.getRealmobj();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(userProfileData);
        realm.commitTransaction();
        closeRealmObj(realm);
    }

    public void deleteUserProfileDataDuplicateRow() {
        try {
            realm = AppController.getRealmobj();
            realm.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                    UserProfileData root = realm.where(UserProfileData.class)
                            .equalTo("id", 1)
                            .findFirst();
                    if (root != null) {
                        if (root.getProfile() != null) {
                            root.getProfile().deleteFromRealm();
                        }
                        if (root.getSettings() != null) {
                            root.getSettings().deleteFromRealm();
                        }
                        root.deleteFromRealm();
                    }
                }
            });
            closeRealmObj(realm);
        } catch (Exception e) {
        }
    }

    public UserProfileData getUserProfileData(Realm realm) {
        return realm.where(UserProfileData.class).findFirst();
    }

    public OfflineData getUserIdOfflineData(String userId, Realm realm) {
        return realm.where(OfflineData.class).equalTo("userProfileId", userId).findFirst();
    }

    public OfflineData getStudyIdOfflineData(String studyId, Realm realm) {
        return realm.where(OfflineData.class).equalTo(STUDYID, studyId).findFirst();
    }

    public OfflineData getActivityIdOfflineData(String activityId, Realm realm) {
        try {
            return realm.where(OfflineData.class).equalTo(ACTIVITYID, activityId).findFirst();
        } catch (Exception e) {
        }
        return null;
    }

    public ActivitiesWS getActivityObj(String activityId, String studyId,Realm realm) {
        return realm.where(ActivityListData.class).equalTo(STUDYID, studyId).findFirst().getActivities().where().equalTo(ACTIVITYID,activityId).findFirst();
    }


    public void closeRealmObj(Realm realm) {
        if (realm != null && !realm.isClosed()) {
            realm.close();
            AppController.clearDBfile();
        }
    }
}
