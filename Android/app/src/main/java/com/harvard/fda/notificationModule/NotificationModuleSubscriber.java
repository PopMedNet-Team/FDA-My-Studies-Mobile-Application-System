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

package com.harvard.fda.notificationModule;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.harvard.fda.R;
import com.harvard.fda.notificationModule.model.NotificationDb;
import com.harvard.fda.storageModule.DBServiceSubscriber;
import com.harvard.fda.studyAppModule.activityBuilder.model.ActivityRun;
import com.harvard.fda.studyAppModule.acvitityListModel.ActivitiesWS;
import com.harvard.fda.studyAppModule.studyModel.NotificationDbResources;
import com.harvard.fda.studyAppModule.studyModel.PendingIntentsResources;
import com.harvard.fda.studyAppModule.survayScheduler.SurvayScheduler;
import com.harvard.fda.utils.AppController;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by Rohit on 2/21/2017.
 */

public class NotificationModuleSubscriber {
    public static String ACTIVITY = "activity";
    public static String NO_USE_NOTIFICATION = "noUseNotification";
    public static String NOTIFICATION_TURN_OFF_NOTIFICATION = "notificationTurnOffNotification";
    public static String RESOURCES = "resources";
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
            if (type.equalsIgnoreCase(SurvayScheduler.FREQUENCY_TYPE_ONE_TIME)) {
                Date date = removeOffset(activityRun.getEndDate(), offset);
                time.setTime(date);
                time.add(Calendar.HOUR_OF_DAY, -24);

                Date date1 = removeOffset(activityRun.getStartDate(), offset);
                time1.setTime(date1);

                description = context.getResources().getString(R.string.the_activity) + " " + activitiesWS.getTitle() + " " + context.getResources().getString(R.string.participation_is_important);
                description1 = context.getResources().getString(R.string.the_activity) + " " + activitiesWS.getTitle() + ", " + context.getResources().getString(R.string.now_available_to_take);
            } else if (type.equalsIgnoreCase(SurvayScheduler.FREQUENCY_TYPE_MANUALLY_SCHEDULE)) {
                Date date = removeOffset(activityRun.getStartDate(), offset);
                time.setTime(date);
                description = context.getResources().getString(R.string.scheduled_activity) + " " + activitiesWS.getTitle() + ", " + context.getResources().getString(R.string.valid_until) + " " + notificationFormat.format(activityRun.getEndDate()) + context.getResources().getString(R.string.participation_is_important2);
            } else if (type.equalsIgnoreCase(SurvayScheduler.FREQUENCY_TYPE_WEEKLY)) {
                Date date = removeOffset(activityRun.getEndDate(), offset);
                time.setTime(date);
                time.add(Calendar.HOUR_OF_DAY, -24);

                Date date1 = removeOffset(activityRun.getStartDate(), offset);
                time1.setTime(date1);

                description = context.getResources().getString(R.string.weekly_activity) + " " + activitiesWS.getTitle() + ", " + context.getResources().getString(R.string.participation_is_important);
                description1 = context.getResources().getString(R.string.new_run) + " " + activitiesWS.getTitle() + ", " + context.getResources().getString(R.string.study_complete);
            } else if (type.equalsIgnoreCase(SurvayScheduler.FREQUENCY_TYPE_MONTHLY)) {
                Date date = removeOffset(activityRun.getEndDate(), offset);
                time.setTime(date);
                time.add(Calendar.HOUR_OF_DAY, -72);

                Date date1 = removeOffset(activityRun.getStartDate(), offset);
                time1.setTime(date1);

                description = context.getResources().getString(R.string.monthly_activity) + " " + activitiesWS.getTitle() + ", " + context.getResources().getString(R.string.expire_in_three_days);
                description1 = context.getResources().getString(R.string.new_run_monthly_activity) + " " + activitiesWS.getTitle() + ", " + context.getResources().getString(R.string.study_complete);
            } else if (type.equalsIgnoreCase(SurvayScheduler.FREQUENCY_TYPE_DAILY)) {
                Date date = removeOffset(activityRun.getStartDate(), offset);
                time.setTime(date);
                description = context.getResources().getString(R.string.new_run_daily_activity) + " " + activitiesWS.getTitle() + ", " + context.getResources().getString(R.string.your_participation_important);
            } else if (type.equalsIgnoreCase(SurvayScheduler.FREQUENCY_TYPE_WITHIN_A_DAY)) {
                Date date = removeOffset(activityRun.getStartDate(), offset);
                time.setTime(date);
                description = context.getResources().getString(R.string.new_run_daily_activity) + " " + activitiesWS.getTitle() + ", " + context.getResources().getString(R.string.valid_until) + " " + notificationFormat.format(activityRun.getEndDate()) + context.getResources().getString(R.string.participation_is_important2);
            }


            int notificationId;

            AlarmManager alarmManager = null;
            int notificationNumber;
            if (time.getTime().after(new Date())) {
                if (notificationsDb != null) {
                    notificationId = notificationsDb.getNotificationId();
                    notificationsDb = dbServiceSubscriber.updateNotificationNumber(notificationsDb, mRealm);
                    mRealm.beginTransaction();
                    notificationsDb.setTitle(title);
                    notificationsDb.setDescription(description);
                    notificationsDb.setDateTime(time.getTime());
                    notificationsDb.setEndDateTime(removeOffset(activityRun.getEndDate(), offset));
                    mRealm.commitTransaction();
                } else {
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
                    dbServiceSubscriber.updateNotificationToDb(context,notificationDb);
                }


                alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

                Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
                notificationIntent.addCategory("android.intent.category.DEFAULT");
                notificationIntent.putExtra("title", title);
                notificationIntent.putExtra("description", description);
                notificationIntent.putExtra("type", ACTIVITY);
                notificationIntent.putExtra("notificationId", notificationId);
                notificationIntent.putExtra("studyId", activityRun.getStudyId());
                notificationIntent.putExtra("activityId", activityRun.getActivityId());
                notificationIntent.putExtra("date", AppController.getDateFormat().format(time.getTime()));
                if (notificationsDb != null) {
                    notificationNumber = notificationsDb.getId();
                } else {
                    notificationNumber = 1;
                }
                notificationIntent.putExtra("notificationNumber", notificationNumber);
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


                    dbServiceSubscriber.savePendingIntentId(context,pendingIntents);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), broadcast);
                    } else {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), broadcast);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Notification availability for monthly, weekly, One time
            if (type.equalsIgnoreCase(SurvayScheduler.FREQUENCY_TYPE_MONTHLY) || type.equalsIgnoreCase(SurvayScheduler.FREQUENCY_TYPE_WEEKLY) || type.equalsIgnoreCase(SurvayScheduler.FREQUENCY_TYPE_ONE_TIME)) {
                if (time1.getTime().after(new Date())) {
                    if (notificationsDb != null) {
                        notificationId = notificationsDb.getNotificationId();
                        notificationsDb = dbServiceSubscriber.updateNotificationNumber(notificationsDb, mRealm);
                        mRealm.beginTransaction();
                        notificationsDb.setTitle(title);
                        notificationsDb.setDescription(description1);
                        notificationsDb.setDateTime(time1.getTime());
                        notificationsDb.setEndDateTime(removeOffset(activityRun.getEndDate(), offset));
                        mRealm.commitTransaction();
                    } else {
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
                        dbServiceSubscriber.updateNotificationToDb(context,notificationDb);
                    }


                    Intent notificationIntent1 = new Intent("android.media.action.DISPLAY_NOTIFICATION");
                    notificationIntent1.addCategory("android.intent.category.DEFAULT");
                    notificationIntent1.putExtra("title", title);
                    notificationIntent1.putExtra("description", description1);
                    notificationIntent1.putExtra("type", ACTIVITY);
                    notificationIntent1.putExtra("studyId", activityRun.getStudyId());
                    notificationIntent1.putExtra("activityId", activityRun.getActivityId());
                    notificationIntent1.putExtra("notificationId", notificationId);
                    notificationIntent1.putExtra("date", AppController.getDateFormat().format(time1.getTime()));
                    if (notificationsDb != null) {
                        notificationNumber = notificationsDb.getId();
                    } else {
                        notificationNumber = 1;
                    }
                    notificationIntent1.putExtra("notificationNumber", notificationNumber);
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


                        dbServiceSubscriber.savePendingIntentId(context,pendingIntents);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time1.getTimeInMillis(), broadcast);
                        } else {
                            alarmManager.setExact(AlarmManager.RTC_WAKEUP, time1.getTimeInMillis(), broadcast);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }


        } catch (Exception e) {
            description = "description";
            e.printStackTrace();
        }
    }

    public void generateTwoWeekNotification(Date date, Context context) {
        try {
            String title = context.getResources().getString(R.string.my_studies);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
            notificationIntent.addCategory("android.intent.category.DEFAULT");
            notificationIntent.putExtra("title", title);
            notificationIntent.putExtra("description", context.getResources().getString(R.string.studie_your_enrolled));
            notificationIntent.putExtra("type", NO_USE_NOTIFICATION);
            notificationIntent.putExtra("date", AppController.getDateFormat().format(calendar.getTime()));

            PendingIntent broadcast = PendingIntent.getBroadcast(context, pendingId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), broadcast);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), broadcast);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancelTwoWeekNotification(Context context) {
        try {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            String title = context.getResources().getString(R.string.my_studies);
            Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
            notificationIntent.addCategory("android.intent.category.DEFAULT");
            notificationIntent.putExtra("title", title);
            notificationIntent.putExtra("description", context.getResources().getString(R.string.studie_your_enrolled));
            notificationIntent.putExtra("type", NO_USE_NOTIFICATION);
            PendingIntent broadcast = PendingIntent.getBroadcast(context, pendingId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            broadcast.cancel();
            alarmManager.cancel(broadcast);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void generateNotificationTurnOffNotification(Date date, Context context) {
        try {
            String title = context.getResources().getString(R.string.my_studies);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.DAY_OF_MONTH, 7);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
            notificationIntent.addCategory("android.intent.category.DEFAULT");
            notificationIntent.putExtra("title", title);
            notificationIntent.putExtra("description", context.getResources().getString(R.string.notificatinturnoffnotification));
            notificationIntent.putExtra("type", NOTIFICATION_TURN_OFF_NOTIFICATION);
            notificationIntent.putExtra("date", AppController.getDateFormat().format(calendar.getTime()));

            PendingIntent broadcast = PendingIntent.getBroadcast(context, pendingId1, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), broadcast);
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), broadcast);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancelNotificationTurnOffNotification(Context context) {
        try {
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            String title = context.getResources().getString(R.string.my_studies);
            Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
            notificationIntent.addCategory("android.intent.category.DEFAULT");
            notificationIntent.putExtra("title", title);
            notificationIntent.putExtra("description", context.getResources().getString(R.string.notificatinturnoffnotification));
            notificationIntent.putExtra("type", NOTIFICATION_TURN_OFF_NOTIFICATION);
            PendingIntent broadcast = PendingIntent.getBroadcast(context, pendingId1, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            broadcast.cancel();
            alarmManager.cancel(broadcast);
        } catch (Exception e) {
            e.printStackTrace();
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
            dbServiceSubscriber.updateNotificationToDb(context,notificationDb);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

            Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
            notificationIntent.addCategory("android.intent.category.DEFAULT");
            notificationIntent.putExtra("title", title);
            notificationIntent.putExtra("description", description);
            notificationIntent.putExtra("type", RESOURCES);
            notificationIntent.putExtra("studyId", studyId);
            notificationIntent.putExtra("notificationId", notificationId);
            notificationIntent.putExtra("activityId", activityId);
            notificationIntent.putExtra("date", AppController.getDateFormat().format(time.getTime()));
            int notificationNumber;
            if (notificationsDb != null) {
                notificationNumber = notificationsDb.getId();
            } else {
                notificationNumber = 1;
            }
            notificationIntent.putExtra("notificationNumber", notificationNumber);
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


                dbServiceSubscriber.savePendingIntentIdResources(context,pendingIntents);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), broadcast);
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(), broadcast);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void cancleResourcesLocalNotification(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        RealmResults<PendingIntentsResources> pendingIntentses = dbServiceSubscriber.getPendingIntentIdResources(mRealm);
        if (pendingIntentses != null) {
            for (int i = 0; i < pendingIntentses.size(); i++) {
                try {
                    Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
                    notificationIntent.addCategory("android.intent.category.DEFAULT");
                    notificationIntent.putExtra("title", pendingIntentses.get(i).getTitle());
                    notificationIntent.putExtra("description", pendingIntentses.get(i).getDescription());
                    notificationIntent.putExtra("type", pendingIntentses.get(i).getType());
                    notificationIntent.putExtra("studyId", pendingIntentses.get(i).getStudyId());
                    notificationIntent.putExtra("activityId", pendingIntentses.get(i).getActivityId());
                    notificationIntent.putExtra("notificationId", pendingIntentses.get(i).getNotificationId());
                    PendingIntent broadcast = PendingIntent.getBroadcast(context, pendingIntentses.get(i).getPendingIntentId(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    broadcast.cancel();
                    alarmManager.cancel(broadcast);
                } catch (Exception e) {
                    e.printStackTrace();
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
                    Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
                    notificationIntent.addCategory("android.intent.category.DEFAULT");
                    notificationIntent.putExtra("title", pendingIntentses.get(i).getTitle());
                    notificationIntent.putExtra("description", pendingIntentses.get(i).getDescription());
                    notificationIntent.putExtra("type", pendingIntentses.get(i).getType());
                    notificationIntent.putExtra("studyId", pendingIntentses.get(i).getStudyId());
                    notificationIntent.putExtra("activityId", pendingIntentses.get(i).getActivityId());
                    notificationIntent.putExtra("notificationId", pendingIntentses.get(i).getNotificationId());
                    PendingIntent broadcast = PendingIntent.getBroadcast(context, pendingIntentses.get(i).getPendingIntentId(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    broadcast.cancel();
                    alarmManager.cancel(broadcast);
                } catch (Exception e) {
                    e.printStackTrace();
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
                    Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
                    notificationIntent.addCategory("android.intent.category.DEFAULT");
                    notificationIntent.putExtra("title", pendingIntentses.get(i).getTitle());
                    notificationIntent.putExtra("description", pendingIntentses.get(i).getDescription());
                    notificationIntent.putExtra("type", pendingIntentses.get(i).getType());
                    notificationIntent.putExtra("studyId", pendingIntentses.get(i).getStudyId());
                    notificationIntent.putExtra("activityId", pendingIntentses.get(i).getActivityId());
                    notificationIntent.putExtra("notificationId", pendingIntentses.get(i).getNotificationId());
                    PendingIntent broadcast = PendingIntent.getBroadcast(context, pendingIntentses.get(i).getPendingIntentId(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    broadcast.cancel();
                    alarmManager.cancel(broadcast);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void cancleActivityLocalNotification(Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        RealmResults<PendingIntents> pendingIntentses = dbServiceSubscriber.getPendingIntentId(mRealm);
        if (pendingIntentses != null) {
            for (int i = 0; i < pendingIntentses.size(); i++) {
                Intent notificationIntent = new Intent("android.media.action.DISPLAY_NOTIFICATION");
                notificationIntent.addCategory("android.intent.category.DEFAULT");
                notificationIntent.putExtra("title", pendingIntentses.get(i).getTitle());
                notificationIntent.putExtra("description", pendingIntentses.get(i).getDescription());
                notificationIntent.putExtra("type", pendingIntentses.get(i).getType());
                notificationIntent.putExtra("studyId", pendingIntentses.get(i).getStudyId());
                notificationIntent.putExtra("activityId", pendingIntentses.get(i).getActivityId());
                notificationIntent.putExtra("notificationId", pendingIntentses.get(i).getNotificationId());
                PendingIntent broadcast = PendingIntent.getBroadcast(context, pendingIntentses.get(i).getPendingIntentId(), notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                broadcast.cancel();
                alarmManager.cancel(broadcast);
            }
        }
    }
}
