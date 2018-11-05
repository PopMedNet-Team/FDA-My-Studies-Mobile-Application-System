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
package com.hphc.mystudies.notificationModule;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.hphc.mystudies.R;
import com.hphc.mystudies.notificationModule.model.NotificationDb;
import com.hphc.mystudies.storageModule.DBServiceSubscriber;
import com.hphc.mystudies.studyAppModule.activityBuilder.model.ActivityRun;
import com.hphc.mystudies.studyAppModule.acvitityListModel.ActivitiesWS;
import com.hphc.mystudies.studyAppModule.studyModel.NotificationDbResources;
import com.hphc.mystudies.studyAppModule.studyModel.PendingIntentsResources;
import com.hphc.mystudies.studyAppModule.surveyScheduler.SurveyScheduler;
import com.hphc.mystudies.utils.AppController;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmResults;

public class NotificationModuleSubscriber {
    public static final String ACTIVITY = "activity";
    public static final String NO_USE_NOTIFICATION = "noUseNotification";
    public static final String NOTIFICATION_TURN_OFF_NOTIFICATION = "notificationTurnOffNotification";
    public static final String DISPLAY_NOTIFICATION = "android.media.action.DISPLAY_NOTIFICATION";
    public static final String DEFAULT = "android.intent.category.DEFAULT";
    public static final String TITLE = "title";
    public static final String DESCRIPTION = "description";
    public static final String NOTIFICATIONID = "notificationId";
    public static final String RESOURCES = "resources";
    public static final String STUDY_ID = "studyId";
    public static final String ACTIVITY_ID = "activityId";
    public static final String NOTIFICATION_NUM = "notificationNumber";
    private DBServiceSubscriber dbServiceSubscriber;
    private Realm mRealm;
    private int pendingId = 214747;
    private int pendingId1 = 214746;

    public NotificationModuleSubscriber(DBServiceSubscriber dbServiceSubscriber, Realm realm) {
        this.dbServiceSubscriber = dbServiceSubscriber;
        mRealm = realm;
    }

    private Date removeOffset(Date date, int offset) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.setTimeInMillis(calendar.getTimeInMillis() + offset);
        return calendar.getTime();
    }

    public void generateActivityLocalNotification(ActivityRun activityRun, Context context, String type, int offset) {
        String title = context.getResources().getString(R.string.my_studies);
        String description = "";
        String description1 = "";
        SimpleDateFormat notificationFormat = AppController.getNotificationDateFormat();
        Calendar time = Calendar.getInstance();
        Calendar time1 = Calendar.getInstance();
        ActivitiesWS activitiesWS = dbServiceSubscriber.getActivityItem(activityRun.getStudyId(), activityRun.getActivityId(), mRealm);
        NotificationDb notificationsDb = dbServiceSubscriber.getNotificationDb(activityRun.getActivityId(), activityRun.getStudyId(), ACTIVITY, mRealm);
        try {
            if (type.equalsIgnoreCase(SurveyScheduler.FREQUENCY_TYPE_ONE_TIME)) {
                Date date = removeOffset(activityRun.getEndDate(), offset);
                time.setTime(date);
                time.add(Calendar.HOUR_OF_DAY, -24);

                Date date1 = removeOffset(activityRun.getStartDate(), offset);
                time1.setTime(date1);

                description = context.getResources().getString(R.string.the_activity) + " " + activitiesWS.getTitle() + " " + context.getResources().getString(R.string.participation_is_important);
                description1 = context.getResources().getString(R.string.the_activity) + " " + activitiesWS.getTitle() + ", " + context.getResources().getString(R.string.now_available_to_take);
            } else if (type.equalsIgnoreCase(SurveyScheduler.FREQUENCY_TYPE_MANUALLY_SCHEDULE)) {
                Date date = removeOffset(activityRun.getStartDate(), offset);
                time.setTime(date);
                description = context.getResources().getString(R.string.scheduled_activity) + " " + activitiesWS.getTitle() + ", " + context.getResources().getString(R.string.valid_until) + " " + notificationFormat.format(activityRun.getEndDate()) + context.getResources().getString(R.string.participation_is_important2);
            } else if (type.equalsIgnoreCase(SurveyScheduler.FREQUENCY_TYPE_WEEKLY)) {
                Date date = removeOffset(activityRun.getEndDate(), offset);
                time.setTime(date);
                time.add(Calendar.HOUR_OF_DAY, -24);

                Date date1 = removeOffset(activityRun.getStartDate(), offset);
                time1.setTime(date1);

                description = context.getResources().getString(R.string.weekly_activity) + " " + activitiesWS.getTitle() + ", " + context.getResources().getString(R.string.participation_is_important);
                description1 = context.getResources().getString(R.string.new_run) + " " + activitiesWS.getTitle() + ", " + context.getResources().getString(R.string.study_complete);
            } else if (type.equalsIgnoreCase(SurveyScheduler.FREQUENCY_TYPE_MONTHLY)) {
                Date date = removeOffset(activityRun.getEndDate(), offset);
                time.setTime(date);
                time.add(Calendar.HOUR_OF_DAY, -72);

                Date date1 = removeOffset(activityRun.getStartDate(), offset);
                time1.setTime(date1);

                description = context.getResources().getString(R.string.monthly_activity) + " " + activitiesWS.getTitle() + ", " + context.getResources().getString(R.string.expire_in_three_days);
                description1 = context.getResources().getString(R.string.new_run_monthly_activity) + " " + activitiesWS.getTitle() + ", " + context.getResources().getString(R.string.study_complete);
            } else if (type.equalsIgnoreCase(SurveyScheduler.FREQUENCY_TYPE_DAILY)) {
                Date date = removeOffset(activityRun.getStartDate(), offset);
                time.setTime(date);
                description = context.getResources().getString(R.string.new_run_daily_activity) + " " + activitiesWS.getTitle() + ", " + context.getResources().getString(R.string.your_participation_important);
            } else if (type.equalsIgnoreCase(SurveyScheduler.FREQUENCY_TYPE_WITHIN_A_DAY)) {
                Date date = removeOffset(activityRun.getStartDate(), offset);
                time.setTime(date);
                description = context.getResources().getString(R.string.new_run_daily_activity) + " " + activitiesWS.getTitle() + ", " + context.getResources().getString(R.string.valid_until) + " " + notificationFormat.format(activityRun.getEndDate()) + context.getResources().getString(R.string.participation_is_important2);
            }


            int notificationId;

            AlarmManager alarmManager = null;
            int notificationNumber;
            if (time.getTime().after(new Date())) {
                if (notificationsDb == null) {
                    notificationId = new Random().nextInt();
                    NotificationDb notificationDb = new NotificationDb();
                    notificationDb.setStudyId(activityRun.getStudyId());
                    notificationDb.setActivityId(activityRun.getActivityId());
                    notificationDb.setNotificationId(notificationId);
                    notificationDb.setDateTime(time.getTime());
                    notificationDb.setType(ACTIVITY);
                    notificationDb.setId(1);
                    notificationDb.setTitle(title);
                    notificationDb.setDescription(description);
                    notificationDb.setEndDateTime(removeOffset(activityRun.getEndDate(), offset));
                    dbServiceSubscriber.updateNotificationToDb(notificationDb);
                } else {
                    notificationId = notificationsDb.getNotificationId();
                    notificationsDb = dbServiceSubscriber.updateNotificationNumber(notificationsDb, mRealm);
                    mRealm.beginTransaction();
                    notificationsDb.setTitle(title);
                    notificationsDb.setDescription(description);
                    notificationsDb.setDateTime(time.getTime());
                    notificationsDb.setEndDateTime(removeOffset(activityRun.getEndDate(), offset));
                    mRealm.commitTransaction();
                }


                alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                Intent notificationIntent = new Intent(DISPLAY_NOTIFICATION);
                notificationIntent.addCategory(DEFAULT);
                notificationIntent.putExtra(TITLE, title);
                notificationIntent.putExtra(DESCRIPTION, description);
                notificationIntent.putExtra("type", ACTIVITY);
                notificationIntent.putExtra(NOTIFICATIONID, notificationId);
                notificationIntent.putExtra(STUDY_ID, activityRun.getStudyId());
                notificationIntent.putExtra(ACTIVITY_ID, activityRun.getActivityId());
                notificationIntent.putExtra("date", AppController.getDateFormat().format(time.getTime()));
                if (notificationsDb == null) {
                    notificationNumber = 1;
                } else {
                    notificationNumber = notificationsDb.getId();
                }
                notificationIntent.putExtra(NOTIFICATION_NUM, notificationNumber);
                try {
                    int pendingIntentId = Integer.parseInt(AppController.getHelperSharedPreference().readPreference(context, context.getResources().getString(R.string.pendingCount), "0")) + 1;
                    AppController.getHelperSharedPreference().writePreference(context, context.getResources().getString(R.string.pendingCount), "" + pendingIntentId);
                    PendingIntent broadcast = PendingIntent.getBroadcast(context, pendingIntentId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    PendingIntents pendingIntents = new PendingIntents();
                    pendingIntents.setActivityId(activityRun.getActivityId());
                    pendingIntents.setStudyId(activityRun.getStudyId());
                    pendingIntents.setPendingIntentId(pendingIntentId);
                    pendingIntents.setDescription(description);
                    pendingIntents.setTitle(title);
                    pendingIntents.setType(ACTIVITY);
                    pendingIntents.setNotificationId(notificationId);


                    dbServiceSubscriber.savePendingIntentId(pendingIntents);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && alarmManager != null) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), broadcast);
                    } else if(alarmManager != null) {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), broadcast);
                    }
                } catch (Exception e) {
                }
            }

            // Notification availability for monthly, weekly, One time
            if (type.equalsIgnoreCase(SurveyScheduler.FREQUENCY_TYPE_MONTHLY) || type.equalsIgnoreCase(SurveyScheduler.FREQUENCY_TYPE_WEEKLY) || type.equalsIgnoreCase(SurveyScheduler.FREQUENCY_TYPE_ONE_TIME)) {
                if (time1.getTime().after(new Date())) {
                    if (notificationsDb == null) {
                        notificationId = new Random().nextInt();
                        NotificationDb notificationDb = new NotificationDb();
                        notificationDb.setStudyId(activityRun.getStudyId());
                        notificationDb.setActivityId(activityRun.getActivityId());
                        notificationDb.setNotificationId(notificationId);
                        notificationDb.setDateTime(time1.getTime());
                        notificationDb.setType(ACTIVITY);
                        notificationDb.setTitle(title);
                        notificationDb.setDescription(description1);
                        notificationDb.setId(1);
                        notificationDb.setEndDateTime(removeOffset(activityRun.getEndDate(), offset));
                        dbServiceSubscriber.updateNotificationToDb(notificationDb);
                    } else {
                        notificationId = notificationsDb.getNotificationId();
                        notificationsDb = dbServiceSubscriber.updateNotificationNumber(notificationsDb, mRealm);
                        mRealm.beginTransaction();
                        notificationsDb.setTitle(title);
                        notificationsDb.setDescription(description1);
                        notificationsDb.setDateTime(time1.getTime());
                        notificationsDb.setEndDateTime(removeOffset(activityRun.getEndDate(), offset));
                        mRealm.commitTransaction();
                    }


                    Intent notificationIntent1 = new Intent(DISPLAY_NOTIFICATION);
                    notificationIntent1.addCategory(DEFAULT);
                    notificationIntent1.putExtra(TITLE, title);
                    notificationIntent1.putExtra(DESCRIPTION, description1);
                    notificationIntent1.putExtra("type", ACTIVITY);
                    notificationIntent1.putExtra(STUDY_ID, activityRun.getStudyId());
                    notificationIntent1.putExtra(ACTIVITY_ID, activityRun.getActivityId());
                    notificationIntent1.putExtra(NOTIFICATIONID, notificationId);
                    notificationIntent1.putExtra("date", AppController.getDateFormat().format(time1.getTime()));
                    if (notificationsDb == null) {
                        notificationNumber = 1;
                    } else {
                        notificationNumber = notificationsDb.getId();
                    }
                    notificationIntent1.putExtra(NOTIFICATION_NUM, notificationNumber);
                    try {
                        int pendingIntentId = Integer.parseInt(AppController.getHelperSharedPreference().readPreference(context, context.getResources().getString(R.string.pendingCount), "0")) + 1;
                        AppController.getHelperSharedPreference().writePreference(context, context.getResources().getString(R.string.pendingCount), "" + pendingIntentId);
                        PendingIntent broadcast = PendingIntent.getBroadcast(context, pendingIntentId, notificationIntent1, PendingIntent.FLAG_UPDATE_CURRENT);
                        PendingIntents pendingIntents = new PendingIntents();
                        pendingIntents.setActivityId(activityRun.getActivityId());
                        pendingIntents.setStudyId(activityRun.getStudyId());
                        pendingIntents.setPendingIntentId(pendingIntentId);
                        pendingIntents.setDescription(description1);
                        pendingIntents.setTitle(title);
                        pendingIntents.setType(ACTIVITY);
                        pendingIntents.setNotificationId(notificationId);


                        dbServiceSubscriber.savePendingIntentId(pendingIntents);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && alarmManager != null) {
                            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time1.getTimeInMillis(), broadcast);
                        } else if(alarmManager != null) {
                            alarmManager.setExact(AlarmManager.RTC_WAKEUP, time1.getTimeInMillis(), broadcast);
                        }
                    } catch (Exception e) {
                    }
                }
            }


        } catch (Exception e) {
        }
    }

    public void generateTwoWeekNotification(Date date, Context context) {
        try {
            String title = context.getResources().getString(R.string.my_studies);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Intent notificationIntent = new Intent(DISPLAY_NOTIFICATION);
            notificationIntent.addCategory(DEFAULT);
            notificationIntent.putExtra(TITLE, title);
            notificationIntent.putExtra(DESCRIPTION, context.getResources().getString(R.string.studie_your_enrolled));
            notificationIntent.putExtra("type", NO_USE_NOTIFICATION);
            notificationIntent.putExtra("date", AppController.getDateFormat().format(calendar.getTime()));

            PendingIntent broadcast = PendingIntent.getBroadcast(context, pendingId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && alarmManager != null) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), broadcast);
            } else if(alarmManager != null) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), broadcast);
            }
        } catch (Exception e) {
        }
    }

    public void cancelTwoWeekNotification(Context context) {
        try {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            String title = context.getResources().getString(R.string.my_studies);
            Intent notificationIntent = new Intent(DISPLAY_NOTIFICATION);
            notificationIntent.addCategory(DEFAULT);
            notificationIntent.putExtra(TITLE, title);
            notificationIntent.putExtra(DESCRIPTION, context.getResources().getString(R.string.studie_your_enrolled));
            notificationIntent.putExtra("type", NO_USE_NOTIFICATION);
            PendingIntent broadcast = PendingIntent.getBroadcast(context, pendingId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            broadcast.cancel();
            alarmManager.cancel(broadcast);
        } catch (Exception e) {
        }
    }

    public void generateNotificationTurnOffNotification(Date date, Context context) {
        try {
            String title = context.getResources().getString(R.string.my_studies);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH, 7);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Intent notificationIntent = new Intent(DISPLAY_NOTIFICATION);
            notificationIntent.addCategory(DEFAULT);
            notificationIntent.putExtra(TITLE, title);
            notificationIntent.putExtra(DESCRIPTION, context.getResources().getString(R.string.notificatinturnoffnotification));
            notificationIntent.putExtra("type", NOTIFICATION_TURN_OFF_NOTIFICATION);
            notificationIntent.putExtra("date", AppController.getDateFormat().format(calendar.getTime()));

            PendingIntent broadcast = PendingIntent.getBroadcast(context, pendingId1, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && alarmManager != null) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), broadcast);
            } else if(alarmManager != null) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), broadcast);
            }
        } catch (Exception e) {
        }
    }

    public void cancelNotificationTurnOffNotification(Context context) {
        try {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            String title = context.getResources().getString(R.string.my_studies);
            Intent notificationIntent = new Intent(DISPLAY_NOTIFICATION);
            notificationIntent.addCategory(DEFAULT);
            notificationIntent.putExtra(TITLE, title);
            notificationIntent.putExtra(DESCRIPTION, context.getResources().getString(R.string.notificatinturnoffnotification));
            notificationIntent.putExtra("type", NOTIFICATION_TURN_OFF_NOTIFICATION);
            PendingIntent broadcast = PendingIntent.getBroadcast(context, pendingId1, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            broadcast.cancel();
            alarmManager.cancel(broadcast);
        } catch (Exception e) {
        }
    }


    public void generateAnchorDateLocalNotification(Date date, String activityId, String studyId, Context context, String notificationTest, String resourceId) {
        String title = "My Studies";
        String description = "";
        Calendar time = Calendar.getInstance();
        NotificationDbResources notificationsDb = null;
        RealmResults<NotificationDbResources> notificationsDbs = dbServiceSubscriber.getNotificationDbResources(activityId, studyId, RESOURCES, mRealm);
        int id = 0;
        if(notificationsDbs != null && notificationsDbs.size() > 0)
        {
            for(int i = 0; i < notificationsDbs.size(); i++)
            {
                if (notificationsDbs.get(i).getResourceId().equalsIgnoreCase(resourceId)) {
                    notificationsDb = notificationsDbs.get(i);
                }
                id = notificationsDbs.get(i).getId();
            }
        }
        id++;
        description = notificationTest;
        time.setTime(date);

        int notificationId = new Random().nextInt(10000);
        NotificationDbResources notificationDb = new NotificationDbResources();
        notificationDb.setStudyId(studyId);
        notificationDb.setActivityId(activityId);
        notificationDb.setNotificationId(notificationId);
        notificationDb.setType(RESOURCES);
        notificationDb.setDateTime(time.getTime());
        notificationDb.setResourceId(resourceId);
        notificationDb.setTitle(title);
        notificationDb.setDescription(description);
        notificationDb.setId(id);
        if (notificationsDb == null) {
            dbServiceSubscriber.updateNotificationToDb(notificationDb);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Intent notificationIntent = new Intent(DISPLAY_NOTIFICATION);
            notificationIntent.addCategory(DEFAULT);
            notificationIntent.putExtra(TITLE, title);
            notificationIntent.putExtra(DESCRIPTION, description);
            notificationIntent.putExtra("type", RESOURCES);
            notificationIntent.putExtra(STUDY_ID, studyId);
            notificationIntent.putExtra(NOTIFICATIONID, notificationId);
            notificationIntent.putExtra(ACTIVITY_ID, activityId);
            notificationIntent.putExtra("date", AppController.getDateFormat().format(time.getTime()));
            int notificationNumber = 1;
            notificationIntent.putExtra(NOTIFICATION_NUM, notificationNumber);
            try {
                int pendingIntentId = Integer.parseInt(AppController.getHelperSharedPreference().readPreference(context, context.getResources().getString(R.string.pendingCountResources), "0")) + 1;
                AppController.getHelperSharedPreference().writePreference(context, context.getResources().getString(R.string.pendingCountResources), "" + pendingIntentId);
                PendingIntent broadcast = PendingIntent.getBroadcast(context, pendingIntentId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                PendingIntentsResources pendingIntents = new PendingIntentsResources();
                pendingIntents.setActivityId(activityId);
                pendingIntents.setStudyId(studyId);
                pendingIntents.setPendingIntentId(pendingIntentId);

                pendingIntents.setTitle(title);
                pendingIntents.setType(description);
                pendingIntents.setDescription(RESOURCES);
                pendingIntents.setNotificationId(notificationId);


                dbServiceSubscriber.savePendingIntentIdResources(pendingIntents);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && alarmManager != null) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), broadcast);
                } else if(alarmManager != null) {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), broadcast);
                }
            } catch (Exception e) {
            }
        }
    }

    public void cancleResourcesLocalNotification(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        RealmResults<PendingIntentsResources> pendingIntentses = dbServiceSubscriber.getPendingIntentIdResources(mRealm);
        if (pendingIntentses != null) {
            for (int i = 0; i < pendingIntentses.size(); i++) {
                try {
                    Intent notificationIntent = new Intent(DISPLAY_NOTIFICATION);
                    notificationIntent.addCategory(DEFAULT);
                    notificationIntent.putExtra(TITLE, pendingIntentses.get(i).getTitle());
                    notificationIntent.putExtra(DESCRIPTION, pendingIntentses.get(i).getDescription());
                    notificationIntent.putExtra("type", pendingIntentses.get(i).getType());
                    notificationIntent.putExtra(STUDY_ID, pendingIntentses.get(i).getStudyId());
                    notificationIntent.putExtra(ACTIVITY_ID, pendingIntentses.get(i).getActivityId());
                    notificationIntent.putExtra(NOTIFICATIONID, pendingIntentses.get(i).getNotificationId());
                    PendingIntent broadcast = PendingIntent.getBroadcast(context, pendingIntentses.get(i).getPendingIntentId(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    broadcast.cancel();
                    alarmManager.cancel(broadcast);
                } catch (Exception e) {
                }
            }
        }
    }

    public void cancleResourcesLocalNotificationByIds(Context context, String activityId, String studyId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        RealmResults<PendingIntentsResources> pendingIntentses = dbServiceSubscriber.getPendingIntentIdResourcesByIds(mRealm, activityId, studyId);
        if (pendingIntentses != null) {
            for (int i = 0; i < pendingIntentses.size(); i++) {
                try {
                    Intent notificationIntent = new Intent(DISPLAY_NOTIFICATION);
                    notificationIntent.addCategory(DEFAULT);
                    notificationIntent.putExtra(TITLE, pendingIntentses.get(i).getTitle());
                    notificationIntent.putExtra(DESCRIPTION, pendingIntentses.get(i).getDescription());
                    notificationIntent.putExtra("type", pendingIntentses.get(i).getType());
                    notificationIntent.putExtra(STUDY_ID, pendingIntentses.get(i).getStudyId());
                    notificationIntent.putExtra(ACTIVITY_ID, pendingIntentses.get(i).getActivityId());
                    notificationIntent.putExtra(NOTIFICATIONID, pendingIntentses.get(i).getNotificationId());
                    PendingIntent broadcast = PendingIntent.getBroadcast(context, pendingIntentses.get(i).getPendingIntentId(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    broadcast.cancel();
                    alarmManager.cancel(broadcast);
                } catch (Exception e) {
                }
            }
        }
    }

    public void cancleActivityLocalNotificationByIds(Context context, String activityId, String studyId) {
        try {

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            RealmResults<PendingIntents> pendingIntentses = dbServiceSubscriber.getPendingIntentIdByIds(mRealm, activityId, studyId);
            if (pendingIntentses != null) {
                for (int i = 0; i < pendingIntentses.size(); i++) {
                    Intent notificationIntent = new Intent(DISPLAY_NOTIFICATION);
                    notificationIntent.addCategory(DEFAULT);
                    notificationIntent.putExtra(TITLE, pendingIntentses.get(i).getTitle());
                    notificationIntent.putExtra(DESCRIPTION, pendingIntentses.get(i).getDescription());
                    notificationIntent.putExtra("type", pendingIntentses.get(i).getType());
                    notificationIntent.putExtra(STUDY_ID, pendingIntentses.get(i).getStudyId());
                    notificationIntent.putExtra(ACTIVITY_ID, pendingIntentses.get(i).getActivityId());
                    notificationIntent.putExtra(NOTIFICATIONID, pendingIntentses.get(i).getNotificationId());
                    PendingIntent broadcast = PendingIntent.getBroadcast(context, pendingIntentses.get(i).getPendingIntentId(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    broadcast.cancel();
                    alarmManager.cancel(broadcast);
                }
            }
        } catch (Exception e) {
        }
    }

    public void cancleActivityLocalNotification(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        RealmResults<PendingIntents> pendingIntentses = dbServiceSubscriber.getPendingIntentId(mRealm);
        if (pendingIntentses != null) {
            for (int i = 0; i < pendingIntentses.size(); i++) {
                Intent notificationIntent = new Intent(DISPLAY_NOTIFICATION);
                notificationIntent.addCategory(DEFAULT);
                notificationIntent.putExtra(TITLE, pendingIntentses.get(i).getTitle());
                notificationIntent.putExtra(DESCRIPTION, pendingIntentses.get(i).getDescription());
                notificationIntent.putExtra("type", pendingIntentses.get(i).getType());
                notificationIntent.putExtra(STUDY_ID, pendingIntentses.get(i).getStudyId());
                notificationIntent.putExtra(ACTIVITY_ID, pendingIntentses.get(i).getActivityId());
                notificationIntent.putExtra(NOTIFICATIONID, pendingIntentses.get(i).getNotificationId());
                PendingIntent broadcast = PendingIntent.getBroadcast(context, pendingIntentses.get(i).getPendingIntentId(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                broadcast.cancel();
                alarmManager.cancel(broadcast);
            }
        }
    }
}
