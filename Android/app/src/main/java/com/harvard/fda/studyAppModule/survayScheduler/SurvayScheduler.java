/*
 * Copyright © 2017-2019 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * Funding Source: Food and Drug Administration (“Funding Agency”) effective 18 September 2014 as Contract no. HHSF22320140030I/HHSF22301006T (the “Prime Contract”).
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.harvard.fda.studyAppModule.survayScheduler;

import android.content.Context;

import com.harvard.fda.R;
import com.harvard.fda.notificationModule.NotificationModuleSubscriber;
import com.harvard.fda.storageModule.DBServiceSubscriber;
import com.harvard.fda.storageModule.events.DatabaseEvent;
import com.harvard.fda.studyAppModule.SurveyActivitiesFragment;
import com.harvard.fda.studyAppModule.activityBuilder.model.ActivityRun;
import com.harvard.fda.studyAppModule.acvitityListModel.ActivitiesWS;
import com.harvard.fda.studyAppModule.acvitityListModel.ActivityListData;
import com.harvard.fda.studyAppModule.survayScheduler.model.ActivityStatus;
import com.harvard.fda.studyAppModule.survayScheduler.model.CompletionAdeherenceCalc;
import com.harvard.fda.userModule.webserviceModel.Activities;
import com.harvard.fda.userModule.webserviceModel.ActivityData;
import com.harvard.fda.userModule.webserviceModel.StudyData;
import com.harvard.fda.utils.AppController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by Rohit on 3/1/2017.
 */

public class SurvayScheduler {
    public static final String FREQUENCY_TYPE_ONE_TIME = "One Time";
    public static final String FREQUENCY_TYPE_WITHIN_A_DAY = "Within a day";
    public static final String FREQUENCY_TYPE_DAILY = "Daily";
    public static final String FREQUENCY_TYPE_WEEKLY = "Weekly";
    public static final String FREQUENCY_TYPE_MONTHLY = "Monthly";
    public static final String FREQUENCY_TYPE_MANUALLY_SCHEDULE = "Manually schedule";
    private Date mStartTime;
    private Date mEndTime;
    private Date mJoiningTime;
    private String mStudyId;
    private Context mContext;
    private DBServiceSubscriber dbServiceSubscriber;
    Realm mRealm;

    public SurvayScheduler(DBServiceSubscriber dbServiceSubscriber, Realm realm) {
        this.dbServiceSubscriber = dbServiceSubscriber;
        mRealm = realm;
    }

    public Date getJoiningDateOfStudy(StudyData userPreferences, String studyId) {
        Date joiningTime = null;
        for (int i = 0; i < userPreferences.getStudies().size(); i++) {
            if (userPreferences.getStudies().get(i).getStudyId().equalsIgnoreCase(studyId)) {
                try {
                    joiningTime = AppController.getDateFormatUTC().parse(userPreferences.getStudies().get(i).getEnrolledDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
        return joiningTime;
    }

    public int getOffset(Context context) {
        Calendar calendarCurrent = Calendar.getInstance();
        TimeZone currentTimeZone = TimeZone.getDefault();
        int currentOffset = currentTimeZone.getOffset(calendarCurrent.getTimeInMillis());
        if (AppController.getHelperSharedPreference().readPreference(context, context.getResources().getString(R.string.startOffset), "").equalsIgnoreCase("")) {
            AppController.getHelperSharedPreference().writePreference(context, context.getResources().getString(R.string.startOffset), "" + currentTimeZone.getOffset(calendarCurrent.getTimeInMillis()));
        }

        return Integer.parseInt(AppController.getHelperSharedPreference().readPreference(context, context.getResources().getString(R.string.startOffset), "")) - currentOffset;

    }

    public void setRuns(ActivitiesWS activity, String studyId, Date startTime, Date endTime, Date joiningTime, Context context) {
        this.mStartTime = startTime;
        this.mEndTime = endTime;
        this.mStudyId = studyId;
        this.mJoiningTime = joiningTime;
        this.mContext = context;
        int offset = getOffset(mContext);

        if (mEndTime != null && mJoiningTime.after(mEndTime)) {
        } else {
            if (activity.getFrequency().getType().equalsIgnoreCase(FREQUENCY_TYPE_DAILY)) {
                setDailyRun(activity, offset);
            } else if (activity.getFrequency().getType().equalsIgnoreCase(FREQUENCY_TYPE_WEEKLY)) {
                setWeeklyRun(activity, offset);
            } else if (activity.getFrequency().getType().equalsIgnoreCase(FREQUENCY_TYPE_MONTHLY)) {
                setMonthlyRun(activity, offset);
            } else if (activity.getFrequency().getType().equalsIgnoreCase(FREQUENCY_TYPE_MANUALLY_SCHEDULE)) {
                setScheduledRun(activity, offset);
            } else if (activity.getFrequency().getType().equalsIgnoreCase(FREQUENCY_TYPE_ONE_TIME)) {
                setOneTimeRun(activity, offset);
            }
        }
    }

    private Date appleyOffset(Date date, int offset) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.setTimeInMillis(calendar.getTimeInMillis() - offset);
        return calendar.getTime();
    }

    private Date removeOffset(Date date, int offset) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.setTimeInMillis(calendar.getTimeInMillis() + offset);
        return calendar.getTime();
    }

    private void setDailyRun(ActivitiesWS activity, int offset) {
        if (mStartTime != null) {
            Calendar startCalendar = Calendar.getInstance();
            startCalendar.setTime(mStartTime);

            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTime(mEndTime);

            int run = 1;
            while (startCalendar.before(endCalendar)) {
                Date startDate = startCalendar.getTime();
                startCalendar.add(Calendar.DATE, 1);
                for (int j = 0; j < activity.getFrequency().getRuns().size(); j++) {
                    try {
                        String date = AppController.getDateFormatType4().format(startDate);
                        String startDateString = date + " " + activity.getFrequency().getRuns().get(j).getStartTime();
                        Date startDateDate = AppController.getDateFormatType11().parse(startDateString);
                        Calendar calendar1 = Calendar.getInstance();
                        calendar1.setTime(startDateDate);
                        Calendar calendarStartDate = Calendar.getInstance();
                        calendarStartDate.setTimeInMillis(calendar1.getTimeInMillis());
                        String endDateString = date + " " + activity.getFrequency().getRuns().get(j).getEndTime();

                        Date endDateDate = AppController.getDateFormatType11().parse(endDateString);
                        Calendar calendar2 = Calendar.getInstance();
                        calendar2.setTime(endDateDate);
                        Calendar calendarEndDate = Calendar.getInstance();
                        calendarEndDate.setTimeInMillis(calendar2.getTimeInMillis());
                        ActivityRun activityRun = null;
                        if (mJoiningTime.after(calendarEndDate.getTime())) {
                        } else if (mJoiningTime.after(calendarStartDate.getTime())) {
                            if ((mJoiningTime.after(mStartTime) || mJoiningTime.equals(mStartTime))) {
                                if ((calendarEndDate.getTime().before(mEndTime) || calendarEndDate.getTime().equals(mEndTime))) {
                                    activityRun = getActivityRun(activity.getActivityId(), mStudyId, false, appleyOffset(mJoiningTime, offset), appleyOffset(calendarEndDate.getTime(), offset), run);
                                } else {
                                    if (mJoiningTime.before(mEndTime)) {
                                        activityRun = getActivityRun(activity.getActivityId(), mStudyId, false, appleyOffset(mJoiningTime, offset), appleyOffset(mEndTime, offset), run);
                                    }
                                }
                            }
                        } else {
                            if ((calendarStartDate.getTime().after(mStartTime) || calendarStartDate.getTime().equals(mStartTime))) {
                                if ((calendarEndDate.getTime().before(mEndTime) || calendarEndDate.getTime().equals(mEndTime))) {
                                    activityRun = getActivityRun(activity.getActivityId(), mStudyId, false, appleyOffset(calendarStartDate.getTime(), offset), appleyOffset(calendarEndDate.getTime(), offset), run);
                                } else {
                                    if (calendarStartDate.getTime().before(mEndTime)) {
                                        activityRun = getActivityRun(activity.getActivityId(), mStudyId, false, appleyOffset(calendarStartDate.getTime(), offset), appleyOffset(mEndTime, offset), run);
                                    }
                                }
                            }
                        }
                        if (activityRun != null) {
                            insertAndUpdateToDB(mContext,activityRun);
                            NotificationModuleSubscriber notificationModuleSubscriber = new NotificationModuleSubscriber(dbServiceSubscriber, mRealm);
                            if (activity.getFrequency().getRuns().size() > 1) {
                                if (!removeOffset(activityRun.getEndDate(), offset).before(new Date()))
                                    notificationModuleSubscriber.generateActivityLocalNotification(activityRun, mContext, FREQUENCY_TYPE_WITHIN_A_DAY, offset);
                            } else {
                                if (!removeOffset(activityRun.getEndDate(), offset).before(new Date()))
                                    notificationModuleSubscriber.generateActivityLocalNotification(activityRun, mContext, FREQUENCY_TYPE_DAILY, offset);
                            }
                            run++;
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }


    private void setOneTimeRun(ActivitiesWS activity, int offset) {
        if (mStartTime != null) {
            SimpleDateFormat simpleDateFormat = AppController.getDateFormatUTC1();
            ActivityRun activityRun = null;
            Calendar calendarStart = Calendar.getInstance();
            try {
                Date endDate;

                if (!activity.getStartTime().equalsIgnoreCase("")) {
                    calendarStart.setTime(simpleDateFormat.parse(activity.getStartTime().split("\\.")[0]));
                    calendarStart.setTimeInMillis(calendarStart.getTimeInMillis());
                }

                if (activity.getEndTime().equalsIgnoreCase("")) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.YEAR, 25);
                    endDate = calendar.getTime();
                } else {
                    endDate = simpleDateFormat.parse(activity.getEndTime().split("\\.")[0]);
                }

                Calendar calendarEnd = Calendar.getInstance();
                calendarEnd.setTime(endDate);
                calendarEnd.setTimeInMillis(calendarEnd.getTimeInMillis());


                if (!activity.getStartTime().equalsIgnoreCase("")) {
                    if (simpleDateFormat.parse(activity.getStartTime().split("\\.")[0]).before(endDate)) {
                        activityRun = getActivityRun(activity.getActivityId(), mStudyId, false, appleyOffset(simpleDateFormat.parse(activity.getStartTime().split("\\.")[0]), offset), appleyOffset(endDate, offset), 1);
                    }
                }
                else
                {
                    activityRun = getActivityRun(activity.getActivityId(), mStudyId, false, appleyOffset(calendarStart.getTime(), offset), appleyOffset(endDate, offset), 1);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if (activityRun != null) {
                insertAndUpdateToDB(mContext,activityRun);
                NotificationModuleSubscriber notificationModuleSubscriber = new NotificationModuleSubscriber(dbServiceSubscriber, mRealm);
                if (!removeOffset(activityRun.getEndDate(), offset).before(new Date()))
                    notificationModuleSubscriber.generateActivityLocalNotification(activityRun, mContext, FREQUENCY_TYPE_ONE_TIME, offset);
            }
        }
    }

    private void setMonthlyRun(ActivitiesWS activity, int offset) {
        if (mStartTime != null) {
            Calendar startCalendar = Calendar.getInstance();
            if (mJoiningTime.after(mStartTime)) {
                startCalendar.setTime(mJoiningTime);
            } else {
                startCalendar.setTime(mStartTime);
            }

            Calendar startTimeCalender = Calendar.getInstance();
            startTimeCalender.setTime(mStartTime);

            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTime(mEndTime);

            int run = 1;
            while (startCalendar.before(endCalendar)) {
                Date startDate = startCalendar.getTime();
                Calendar startCalendarTime = Calendar.getInstance();
                startCalendarTime.setTime(startDate);
                startCalendarTime.setTimeInMillis(startCalendarTime.getTimeInMillis());
                startCalendar.set(Calendar.DAY_OF_MONTH, startTimeCalender.get(Calendar.DAY_OF_MONTH));
                startCalendar.set(Calendar.HOUR_OF_DAY, startTimeCalender.get(Calendar.HOUR_OF_DAY));
                startCalendar.set(Calendar.MINUTE, startTimeCalender.get(Calendar.MINUTE));
                startCalendar.set(Calendar.SECOND, startTimeCalender.get(Calendar.SECOND));
                startCalendar.add(Calendar.MONTH, 1);
                Date endDate = new Date(startCalendar.getTimeInMillis() - 1000);
                if (endDate.after(endCalendar.getTime())) {
                    endDate = endCalendar.getTime();
                }

                Calendar endCalendarTime = Calendar.getInstance();
                endCalendarTime.setTime(endDate);
                endCalendarTime.setTimeInMillis(endCalendarTime.getTimeInMillis());

                ActivityRun activityRun = getActivityRun(activity.getActivityId(), mStudyId, false, appleyOffset(startCalendarTime.getTime(), offset), appleyOffset(endCalendarTime.getTime(), offset), run);
                if (activityRun != null) {
                    insertAndUpdateToDB(mContext,activityRun);
                    NotificationModuleSubscriber notificationModuleSubscriber = new NotificationModuleSubscriber(dbServiceSubscriber, mRealm);
                    if (!removeOffset(activityRun.getEndDate(), offset).before(new Date()))
                        notificationModuleSubscriber.generateActivityLocalNotification(activityRun, mContext, FREQUENCY_TYPE_MONTHLY, offset);
                    run++;
                }

            }
        }
    }

    private void setScheduledRun(ActivitiesWS activity, int offset) {

        if (mStartTime != null) {
            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTime(mEndTime);

            int run = 1;
            for (int j = 0; j < activity.getFrequency().getRuns().size(); j++) {
                SimpleDateFormat simpleDateFormat = AppController.getDateFormatUTC1();
                ActivityRun activityRun = null;
                try {
                    if (mJoiningTime.after(simpleDateFormat.parse(activity.getFrequency().getRuns().get(j).getEndTime().split("\\.")[0]))) {
                    } else if (mJoiningTime.after(simpleDateFormat.parse(activity.getFrequency().getRuns().get(j).getStartTime().split("\\.")[0]))) {
                        Calendar calendarEnd = Calendar.getInstance();
                        calendarEnd.setTime(simpleDateFormat.parse(activity.getFrequency().getRuns().get(j).getEndTime().split("\\.")[0]));
                        calendarEnd.setTimeInMillis(calendarEnd.getTimeInMillis());
                        if (mJoiningTime.before(calendarEnd.getTime())) {
                            if (mJoiningTime.before(endCalendar.getTime())) {
                                activityRun = getActivityRun(activity.getActivityId(), mStudyId, false, appleyOffset(mJoiningTime, offset), appleyOffset(calendarEnd.getTime(), offset), run);
                            }
                        }
                    } else {
                        Calendar calendarStart = Calendar.getInstance();
                        calendarStart.setTime(simpleDateFormat.parse(activity.getFrequency().getRuns().get(j).getStartTime().split("\\.")[0]));
                        calendarStart.setTimeInMillis(calendarStart.getTimeInMillis());

                        Calendar calendarEnd = Calendar.getInstance();
                        calendarEnd.setTime(simpleDateFormat.parse(activity.getFrequency().getRuns().get(j).getEndTime().split("\\.")[0]));
                        calendarEnd.setTimeInMillis(calendarEnd.getTimeInMillis());
                        if (calendarStart.getTime().before(calendarEnd.getTime())) {
                            if (appleyOffset(calendarStart.getTime(), offset).before(appleyOffset(endCalendar.getTime(), offset))) {
                                activityRun = getActivityRun(activity.getActivityId(), mStudyId, false, appleyOffset(calendarStart.getTime(), offset), appleyOffset(calendarEnd.getTime(), offset), run);
                            }
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                if (activityRun != null) {
                    insertAndUpdateToDB(mContext,activityRun);
                    NotificationModuleSubscriber notificationModuleSubscriber = new NotificationModuleSubscriber(dbServiceSubscriber, mRealm);
                    if (!removeOffset(activityRun.getEndDate(), offset).before(new Date()))
                        notificationModuleSubscriber.generateActivityLocalNotification(activityRun, mContext, FREQUENCY_TYPE_MANUALLY_SCHEDULE, offset);

                    run++;
                }
            }
        }
    }

    private void setWeeklyRun(ActivitiesWS activity, int offset) {
        if (mStartTime != null) {
            Calendar startCalendar = Calendar.getInstance();
            if (mJoiningTime.after(mStartTime)) {
                startCalendar.setTime(mJoiningTime);
            } else {
                startCalendar.setTime(mStartTime);
            }
            Calendar startTimeCalender = Calendar.getInstance();
            startTimeCalender.setTime(mStartTime);

            Calendar endCalendar = Calendar.getInstance();
            endCalendar.setTime(mEndTime);

            int run = 1;
            while (startCalendar.before(endCalendar)) {
                Date startDate = startCalendar.getTime();
                Calendar startCalenderTime = Calendar.getInstance();
                startCalenderTime.setTime(startDate);
                startCalenderTime.setTimeInMillis(startCalenderTime.getTimeInMillis());

                if (startTimeCalender.get(Calendar.DAY_OF_WEEK) < startCalendar.get(Calendar.DAY_OF_WEEK)) {
                    startCalendar.add(Calendar.DATE, -(startCalendar.get(Calendar.DAY_OF_WEEK) - startTimeCalender.get(Calendar.DAY_OF_WEEK)));
                } else if (startTimeCalender.get(Calendar.DAY_OF_WEEK) > startCalendar.get(Calendar.DAY_OF_WEEK)) {
                    startCalendar.add(Calendar.DATE, -7 + (startTimeCalender.get(Calendar.DAY_OF_WEEK) - startCalendar.get(Calendar.DAY_OF_WEEK)));
                }
                startCalendar.set(Calendar.HOUR_OF_DAY, startTimeCalender.get(Calendar.HOUR_OF_DAY));
                startCalendar.set(Calendar.MINUTE, startTimeCalender.get(Calendar.MINUTE));
                startCalendar.set(Calendar.SECOND, startTimeCalender.get(Calendar.SECOND));
                startCalendar.add(Calendar.DATE, 7);
                Date endDate = new Date(startCalendar.getTimeInMillis() - 1000);
                if (endDate.after(endCalendar.getTime())) {
                    endDate = endCalendar.getTime();
                }
                Calendar endCalenderTime = Calendar.getInstance();
                endCalenderTime.setTime(endDate);
                endCalenderTime.setTimeInMillis(endCalenderTime.getTimeInMillis());

                ActivityRun activityRun = getActivityRun(activity.getActivityId(), mStudyId, false, appleyOffset(startCalenderTime.getTime(), offset), appleyOffset(endCalenderTime.getTime(), offset), run);
                if (activityRun != null) {
                    insertAndUpdateToDB(mContext,activityRun);
                    NotificationModuleSubscriber notificationModuleSubscriber = new NotificationModuleSubscriber(dbServiceSubscriber, mRealm);
                    if (!removeOffset(activityRun.getEndDate(), offset).before(new Date()))
                        notificationModuleSubscriber.generateActivityLocalNotification(activityRun, mContext, FREQUENCY_TYPE_WEEKLY, offset);
                    run++;
                }
            }
        }
    }

    /**
     * get activity run for insert
     *
     * @return
     */
    private ActivityRun getActivityRun(String activityId, String studyId, boolean isCompleted, Date startDate, Date endDate, int runId) {
        ActivityRun activityRun = new ActivityRun();
        activityRun.setActivityId(activityId);
        activityRun.setStudyId(studyId);
        activityRun.setCompleted(isCompleted);
        activityRun.setStartDate(startDate);
        activityRun.setEndDate(endDate);
        activityRun.setRunId(runId);
        return activityRun;
    }

    private <E> void insertAndUpdateToDB(Context context,E e) {
        DatabaseEvent databaseEvent = new DatabaseEvent();
        databaseEvent.setE(e);
        databaseEvent.setmType(DBServiceSubscriber.TYPE_COPY);
        databaseEvent.setaClass(ActivityRun.class);
        databaseEvent.setmOperation(DBServiceSubscriber.INSERT_AND_UPDATE_OPERATION);
        dbServiceSubscriber.insert(context,databaseEvent);
    }

    // if currentRunId = 0 then no need to show the current run in UI
    public ActivityStatus getActivityStatus(ActivityData activityData, String studyId, String activityId, Date currentDate) {
        String activityStatus = SurveyActivitiesFragment.YET_To_START;
        int currentRunId = 0;

        RealmResults<ActivityRun> activityRuns = dbServiceSubscriber.getAllActivityRunFromDB(studyId, activityId, mRealm);
        activityRuns = activityRuns.sort("runId", Sort.ASCENDING);
        int totalRun = activityRuns.size();
        int missedRun = 0;
        int completedRun = 0;
        Date currentRunStartDate = null;
        Date currentRunEndDate = null;
        boolean runAvailable = false;
        Activities activitiesForStatus = null;
        ActivityStatus activityStatusData = new ActivityStatus();
        SimpleDateFormat simpleDateFormat = AppController.getDateFormatUTC();

        ActivityRun activityRun = null;
        ActivityRun activityPreviousRun = null;
        boolean previousRun = true;
        for (int i = 0; i < activityRuns.size(); i++) {
            Date activityRunStDate = null;
            Date activityRunEndDate = null;
            try {
                activityRunStDate = simpleDateFormat.parse(simpleDateFormat.format(activityRuns.get(i).getStartDate()));
                activityRunEndDate = simpleDateFormat.parse(simpleDateFormat.format(activityRuns.get(i).getEndDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if ((currentDate.equals(activityRunStDate) || currentDate.after(activityRunStDate)) && (currentDate.equals(activityRunEndDate) || currentDate.before(activityRunEndDate))) {
                activityRun = activityRuns.get(i);
            } else if (currentDate.after(activityRunStDate)) {
                activityPreviousRun = activityRuns.get(i);
                previousRun = false;
            }
        }

        if (activityRun != null) {
            runAvailable = true;
            currentRunId = activityRun.getRunId();
            currentRunStartDate = activityRun.getStartDate();
            currentRunEndDate = activityRun.getEndDate();
        } else {
            if (activityPreviousRun != null) {
                currentRunId = activityPreviousRun.getRunId();
                currentRunStartDate = activityPreviousRun.getStartDate();
                currentRunEndDate = activityPreviousRun.getEndDate();
            }
        }
        boolean activityIdAvailable = false;
        if (activityData.getActivities() != null) {
            for (int i = 0; i < activityData.getActivities().size(); i++) {
                if (activityData.getActivities().get(i).getActivityId().equalsIgnoreCase(activityId)) {
                    activitiesForStatus = activityData.getActivities().get(i);
                    if (activitiesForStatus.getActivityRunId() != null && !activitiesForStatus.getActivityRunId().equalsIgnoreCase("")) {
                        activityIdAvailable = true;
                    }
                }
            }
        }

        if (runAvailable && activityIdAvailable) {
            if (currentRunId == Integer.parseInt(activitiesForStatus.getActivityRunId())) {
                activityStatus = activitiesForStatus.getStatus();
            } else {
                activityStatus = SurveyActivitiesFragment.YET_To_START;
            }
        } else if (runAvailable) {
            activityStatus = SurveyActivitiesFragment.YET_To_START;
        } else if (activityIdAvailable) {
            if (currentRunId == Integer.parseInt(activitiesForStatus.getActivityRunId())) {
                if (activitiesForStatus.getStatus().equalsIgnoreCase(SurveyActivitiesFragment.COMPLETED)) {
                    activityStatus = activitiesForStatus.getStatus();
                } else {
                    activityStatus = SurveyActivitiesFragment.INCOMPLETE;
                }
            } else {
                activityStatus = SurveyActivitiesFragment.INCOMPLETE;
            }
        } else {
            if (activityPreviousRun == null) {
                activityStatus = SurveyActivitiesFragment.YET_To_START;
            } else {
                activityStatus = SurveyActivitiesFragment.INCOMPLETE;
            }
        }

        Activities activities = dbServiceSubscriber.getActivityPreferenceBySurveyId(studyId, activityId, mRealm);
        if (activities != null && activities.getActivityRun() != null) {
            completedRun = activities.getActivityRun().getCompleted();
        }
        if (currentRunId <= 0) {
            missedRun = 0;
            currentRunStartDate = new Date();
            currentRunEndDate = new Date();
        } else {
            missedRun = currentRunId - completedRun;
        }

        if (runAvailable && !activityStatus.equalsIgnoreCase(SurveyActivitiesFragment.COMPLETED)) {
            missedRun--;
        }

        if (missedRun < 0) {
            missedRun = 0;
        }

        activityStatusData.setCompletedRun(completedRun);
        activityStatusData.setCurrentRunId(currentRunId);
        activityStatusData.setMissedRun(missedRun);
        activityStatusData.setCurrentRunStartDate(currentRunStartDate);
        activityStatusData.setCurrentRunEndDate(currentRunEndDate);
        activityStatusData.setStatus(activityStatus);
        activityStatusData.setTotalRun(totalRun);
        activityStatusData.setRunIdAvailable(runAvailable);
        return activityStatusData;
    }


    private Activities getActivitiesDb(ActivityData activityData, String activityId) {
        Activities activities = null;
        if (activityData != null && activityData.getActivities() != null)
            for (int i = 0; i < activityData.getActivities().size(); i++) {
                if (activityData.getActivities().get(i).getActivityId().equalsIgnoreCase(activityId)) {
                    return activityData.getActivities().get(i);
                }
            }
        return null;
    }

    public CompletionAdeherenceCalc completionAndAdherenceCalculation(String studyId, Context mContext) {
        double completion = 0;
        double adherence = 0;
        boolean activityListAvailable = false;

        int completed = 0;
        int missed = 0;
        int total = 0;

        SimpleDateFormat simpleDateFormat = AppController.getDateFormatUTC1();
        ActivityData activityData = dbServiceSubscriber.getActivityPreference(studyId, mRealm);
        ActivityListData activityListDataDB = dbServiceSubscriber.getActivities(studyId, mRealm);
        Date currentDate = new Date();

        Calendar calendarCurrentTime = Calendar.getInstance();
        calendarCurrentTime.setTime(currentDate);
        calendarCurrentTime.setTimeInMillis(calendarCurrentTime.getTimeInMillis() - getOffset(mContext));

        if (activityListDataDB != null) {
            activityListAvailable = true;
            for (int i = 0; i < activityListDataDB.getActivities().size(); i++) {

                try {
                    if (!checkafter(simpleDateFormat.parse(activityListDataDB.getActivities().get(i).getStartTime().split("\\.")[0]))) {
                        ActivityStatus activityStatus = getActivityStatus(activityData, studyId, activityListDataDB.getActivities().get(i).getActivityId(), calendarCurrentTime.getTime());
                        if (activityStatus != null) {
                            if (activityStatus.getCompletedRun() >= 0) {
                                completed = completed + activityStatus.getCompletedRun();
                            }
                            if (activityStatus.getMissedRun() >= 0) {
                                missed = missed + activityStatus.getMissedRun();
                            }
                            if (activityStatus.getTotalRun() >= 0) {
                                total = total + activityStatus.getTotalRun();
                            }
                        }
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }

        if (total > 0)
            completion = (((double) completed + (double) missed) / (double) total) * 100d;

        if (((double) completed + (double) missed) != 0) {
            adherence = ((double) completed / ((double) completed + (double) missed)) * 100;
        }

        CompletionAdeherenceCalc completionAdeherenceCalc = new CompletionAdeherenceCalc();
        completionAdeherenceCalc.setAdherence(adherence);
        completionAdeherenceCalc.setCompletion(completion);
        completionAdeherenceCalc.setActivityAvailable(activityListAvailable);
        if(completed == 0 && missed == 0)
        {
            completionAdeherenceCalc.setNoCompletedAndMissed(true);
        }
        else
        {
            completionAdeherenceCalc.setNoCompletedAndMissed(false);
        }
        return completionAdeherenceCalc;
    }

    boolean checkafter(Date starttime) {
        return starttime.after(new Date());
    }


}
