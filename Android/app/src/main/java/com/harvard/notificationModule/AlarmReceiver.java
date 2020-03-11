package com.harvard.notificationModule;

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
import android.util.Log;

import com.harvard.AppConfig;
import com.harvard.FDAApplication;
import com.harvard.R;
import com.harvard.storageModule.DBServiceSubscriber;
import com.harvard.studyAppModule.NotificationActivity;
import com.harvard.studyAppModule.StandaloneActivity;
import com.harvard.studyAppModule.StudyActivity;
import com.harvard.userModule.webserviceModel.UserProfileData;
import com.harvard.utils.AppController;

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
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(description))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setAutoCancel(true)
                        .setGroup("group")
                        .build();
            } else {
                notification = builder.setContentTitle(title)
                        .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                        .setContentText(description)
                        .setChannelId(FDAApplication.NOTIFICATION_CHANNEL_ID_INFO)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(description))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setAutoCancel(true)
                        .setContentIntent(contentIntent)
                        .setGroup("group")
                        .build();
            }
        } else {
            if (type.equalsIgnoreCase(NotificationModuleSubscriber.NO_USE_NOTIFICATION)) {
                notification = builder.setContentTitle(title)
                        .setContentText(description)
                        .setChannelId(FDAApplication.NOTIFICATION_CHANNEL_ID_INFO)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(description))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setAutoCancel(true)
                        .setGroup("group")
                        .build();
            } else {
                notification = builder.setContentTitle(title)
                        .setContentText(description)
                        .setChannelId(FDAApplication.NOTIFICATION_CHANNEL_ID_INFO)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(description))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setAutoCancel(true)
                        .setContentIntent(contentIntent)
                        .setGroup("group")
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
