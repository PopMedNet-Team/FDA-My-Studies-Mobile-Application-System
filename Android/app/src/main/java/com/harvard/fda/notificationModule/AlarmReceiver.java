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

import android.app.Notification;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

import com.harvard.fda.AppConfig;
import com.harvard.fda.FDAApplication;
import com.harvard.fda.R;
import com.harvard.fda.storageModule.DBServiceSubscriber;
import com.harvard.fda.studyAppModule.NotificationActivity;
import com.harvard.fda.studyAppModule.StandaloneActivity;
import com.harvard.fda.studyAppModule.StudyActivity;
import com.harvard.fda.userModule.webserviceModel.UserProfileData;
import com.harvard.fda.utils.AppController;

import java.util.Calendar;

import io.realm.Realm;

/**
 * Created by USER on 20-05-2017.
 */

public class AlarmReceiver extends BroadcastReceiver {

    public static String TYPE = "type";
    public static String SUBTYPE = "subtype";
    public static String STUDYID = "studyId";
    public static String ACTIVITYID = "activityId";
    public static String AUDIENCE = "audience";
    public static String LOCAL_NOTIFICATION = "localNotification";
    public static String TITLE = "title";
    public static String MESSAGE = "message";
    public static String NOTIFICATION_INTENT = "notificationIntent";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent notificationIntent1 = new Intent(context, NotificationActivity.class);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(NotificationActivity.class);
        stackBuilder.addNextIntent(notificationIntent1);

        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");
        String type = intent.getStringExtra("type");
        String date = intent.getStringExtra("date");
        String studyId = null;
        if (intent.getStringExtra("studyId") != null) {
            studyId = intent.getStringExtra("studyId");
        }

        String activityId = null;
        if (intent.getStringExtra("activityId") != null) {
            activityId = intent.getStringExtra("activityId");
        }

        int number = intent.getIntExtra("notificationNumber", 0);
        int notificationId = intent.getIntExtra("notificationId", 0);

        Intent notificationIntent;
        if (AppConfig.AppType.equalsIgnoreCase(context.getString(R.string.app_gateway))) {
             notificationIntent = new Intent(context, StudyActivity.class);
        } else {
             notificationIntent = new Intent(context, StandaloneActivity.class);
        }
        PendingIntent contentIntent = null;
        if (!type.equalsIgnoreCase(NotificationModuleSubscriber.NO_USE_NOTIFICATION)) {
            notificationIntent.putExtra(StudyActivity.FROM, NOTIFICATION_INTENT);

            notificationIntent.putExtra(TYPE, "Study");
            if (type.equalsIgnoreCase("resources")) {
                notificationIntent.putExtra(SUBTYPE, "Resource");
            } else {
                notificationIntent.putExtra(SUBTYPE, "Activity");
            }
            notificationIntent.putExtra(STUDYID, studyId);
            notificationIntent.putExtra(ACTIVITYID, activityId);
            notificationIntent.putExtra(AUDIENCE, "");
            notificationIntent.putExtra(LOCAL_NOTIFICATION, "true");
            notificationIntent.putExtra(TITLE, title);
            notificationIntent.putExtra(MESSAGE, description);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            contentIntent = PendingIntent.getActivity(context, notificationId, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }


        int notifyIcon = R.mipmap.ic_launcher;
        Bitmap icon = BitmapFactory.decodeResource(context.getResources(), notifyIcon);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Notification notification = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            if (type.equalsIgnoreCase(NotificationModuleSubscriber.NO_USE_NOTIFICATION)) {
                notification = builder.setContentTitle(title)
                        .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                        .setContentText(description)
                        .setChannelId(FDAApplication.NOTIFICATION_CHANNEL_ID_INFO)
                        .setStyle(new android.support.v4.app.NotificationCompat.BigTextStyle().bigText(description))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setAutoCancel(true)
                        .build();
            } else {
                notification = builder.setContentTitle(title)
                        .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                        .setContentText(description)
                        .setChannelId(FDAApplication.NOTIFICATION_CHANNEL_ID_INFO)
                        .setStyle(new android.support.v4.app.NotificationCompat.BigTextStyle().bigText(description))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setAutoCancel(true)
                        .setContentIntent(contentIntent)
                        .build();
            }
        } else {
            if (type.equalsIgnoreCase(NotificationModuleSubscriber.NO_USE_NOTIFICATION)) {
                notification = builder.setContentTitle(title)
                        .setContentText(description)
                        .setChannelId(FDAApplication.NOTIFICATION_CHANNEL_ID_INFO)
                        .setStyle(new android.support.v4.app.NotificationCompat.BigTextStyle().bigText(description))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setAutoCancel(true)
                        .build();
            } else {
                notification = builder.setContentTitle(title)
                        .setContentText(description)
                        .setChannelId(FDAApplication.NOTIFICATION_CHANNEL_ID_INFO)
                        .setStyle(new android.support.v4.app.NotificationCompat.BigTextStyle().bigText(description))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setAutoCancel(true)
                        .setContentIntent(contentIntent)
                        .build();
            }
        }


        try {
            int count = Integer.parseInt(AppController.getHelperSharedPreference().readPreference(context, context.getResources().getString(R.string.notificationCount), "0")) + 1;
            AppController.getHelperSharedPreference().writePreference(context, context.getResources().getString(R.string.notificationCount), "" + count);
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

            Realm mRealm = AppController.getRealmobj(context);
            DBServiceSubscriber dbServiceSubscriber = new DBServiceSubscriber();
            UserProfileData mUserProfileData = dbServiceSubscriber.getUserProfileData(mRealm);
            boolean isNotification = true;
            if (mUserProfileData != null) {
                isNotification = mUserProfileData.getSettings().isLocalNotifications();
            }
            if (isNotification || type.equalsIgnoreCase(NotificationModuleSubscriber.NOTIFICATION_TURN_OFF_NOTIFICATION)) {
                notificationManager.notify(count, notification);
                if (type.equalsIgnoreCase(NotificationModuleSubscriber.NOTIFICATION_TURN_OFF_NOTIFICATION)) {
                    NotificationModuleSubscriber notificationModuleSubscriber = new NotificationModuleSubscriber(dbServiceSubscriber, mRealm);
                    notificationModuleSubscriber.generateNotificationTurnOffNotification(Calendar.getInstance().getTime(), context);
                }
            }

            try {
                dbServiceSubscriber.closeRealmObj(mRealm);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
