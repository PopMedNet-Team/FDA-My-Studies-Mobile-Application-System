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

package com.harvard.fda.studyAppModule;

import android.os.Bundle;
import android.os.LocaleList;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.harvard.fda.R;
import com.harvard.fda.notificationModule.model.LocalNotification;
import com.harvard.fda.notificationModule.model.NotificationDb;
import com.harvard.fda.storageModule.DBServiceSubscriber;
import com.harvard.fda.studyAppModule.events.GetUserStudyListEvent;
import com.harvard.fda.studyAppModule.studyModel.Notification;
import com.harvard.fda.studyAppModule.studyModel.NotificationData;
import com.harvard.fda.studyAppModule.studyModel.NotificationDbResources;
import com.harvard.fda.studyAppModule.studyModel.Study;
import com.harvard.fda.studyAppModule.studyModel.StudyList;
import com.harvard.fda.utils.AppController;
import com.harvard.fda.utils.URLs;
import com.harvard.fda.webserviceModule.apiHelper.ApiCall;
import com.harvard.fda.webserviceModule.events.WCPConfigEvent;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;


public class NotificationActivity extends AppCompatActivity implements ApiCall.OnAsyncRequestComplete {
    private static final int NOTIFICATIONS = 100;
    private RelativeLayout mBackBtn;
    private RecyclerView mStudyRecyclerView;
    private AppCompatTextView mTitle;
    DBServiceSubscriber dbServiceSubscriber;
    Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_notification);
        dbServiceSubscriber = new DBServiceSubscriber();
        mRealm = AppController.getRealmobj(this);
        AppController.getHelperSharedPreference().writePreference(this, getString(R.string.notification), "false");
        initializeXMLId();
        setTextForView();
        setFont();
        bindEvent();
        getNotification();
    }

    private void initializeXMLId() {
        mBackBtn = (RelativeLayout) findViewById(R.id.backBtn);
        mTitle = (AppCompatTextView) findViewById(R.id.title);
        mStudyRecyclerView = (RecyclerView) findViewById(R.id.studyRecyclerView);
    }

    private void setTextForView() {
        mTitle.setText(getResources().getString(R.string.notifictions));
    }

    private void setFont() {
        try {
            mTitle.setTypeface(AppController.getTypeface(this, "bold"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindEvent() {
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    private void setRecyclearView(RealmList<Notification> notifications) {

        mStudyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mStudyRecyclerView.setNestedScrollingEnabled(false);
        NotificationListAdapter notificationListAdapter = new NotificationListAdapter(this, notifications, mRealm);
        mStudyRecyclerView.setAdapter(notificationListAdapter);
    }

    private void getNotification() {
        AppController.getHelperProgressDialog().showProgress(NotificationActivity.this, "", "", false);
        GetUserStudyListEvent getUserStudyListEvent = new GetUserStudyListEvent();
        HashMap<String, String> header = new HashMap();
        String url = URLs.NOTIFICATIONS + "?skip=0";
        WCPConfigEvent wcpConfigEvent = new WCPConfigEvent("get", url, NOTIFICATIONS, this, NotificationData.class, null, header, null, false, this);

        getUserStudyListEvent.setWcpConfigEvent(wcpConfigEvent);
        StudyModulePresenter studyModulePresenter = new StudyModulePresenter();
        studyModulePresenter.performGetGateWayStudyList(getUserStudyListEvent);
    }

    @Override
    public <T> void asyncResponse(T response, int responseCode) {
        AppController.getHelperProgressDialog().dismissDialog();
        if (response != null) {
            NotificationData notification = (NotificationData) response;
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR, 0);
            RealmResults<NotificationDb> notificationsDb = dbServiceSubscriber.getNotificationDbByDate(new Date(), mRealm);
            RealmResults<NotificationDbResources> notificationsDbResources = dbServiceSubscriber.getNotificationDbResourcesByDate(new Date(), mRealm);
            RealmList<Notification> notifications = new RealmList<>();
            if(notificationsDb != null)
            {
                for (int i = 0; i < notificationsDb.size(); i++)
                {
                    Notification notification1 = new Notification();
                    notification1.setTitle(notificationsDb.get(i).getTitle());
                    if (notificationsDb.get(i).getType().equalsIgnoreCase("resources")) {
                        notification1.setSubtype("Resource");
                    } else {
                        notification1.setSubtype("Activity");
                    }
                    notification1.setStudyId(notificationsDb.get(i).getStudyId());
                    notification1.setMessage(notificationsDb.get(i).getDescription());
                    notification1.setDate(AppController.getDateFormat().format(notificationsDb.get(i).getDateTime()));
                    notification1.setType("Study");
                    notification1.setAudience("");
                    notifications.add(notification1);
                }
            }
            if(notificationsDbResources != null)
            {
                for (int i = 0; i < notificationsDbResources.size(); i++)
                {
                    Notification notification1 = new Notification();
                    notification1.setTitle(notificationsDbResources.get(i).getTitle());
                    if (notificationsDbResources.get(i).getType().equalsIgnoreCase("resources")) {
                        notification1.setSubtype("Resource");
                    } else {
                        notification1.setSubtype("Activity");
                    }
                    notification1.setStudyId(notificationsDbResources.get(i).getStudyId());
                    notification1.setMessage(notificationsDbResources.get(i).getDescription());
                    notification1.setDate(AppController.getDateFormat().format(notificationsDbResources.get(i).getDateTime()));
                    notification1.setType("Study");
                    notification1.setAudience("");
                    notifications.add(notification1);
                }
            }
            for (int i = 0; i < notification.getNotifications().size(); i++) {
                if (notification.getNotifications().get(i).getType().equalsIgnoreCase("Study")) {

                    Study mStudy = dbServiceSubscriber.getStudyListFromDB(mRealm);
                    if (mStudy != null) {
                        RealmList<StudyList> studyListArrayList = mStudy.getStudies();
                        for (int j = 0; j < studyListArrayList.size(); j++) {
                            if (notification.getNotifications().get(i).getStudyId().equalsIgnoreCase(studyListArrayList.get(j).getStudyId())) {
                                if (studyListArrayList.get(j).getStudyStatus().equalsIgnoreCase(StudyFragment.IN_PROGRESS)) {
                                    notifications.add(notification.getNotifications().get(i));
                                }
                            }
                        }
                    }
                } else {
                    notifications.add(notification.getNotifications().get(i));
                }
            }
            Collections.sort(notifications, new Comparator<Notification>() {
                public int compare(Notification o1, Notification o2) {
                    try {
                        return AppController.getDateFormat().parse(o2.getDate()).compareTo(AppController.getDateFormat().parse(o1.getDate()));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    return -1;
                }
            });
            setRecyclearView(notifications);
            dbServiceSubscriber.saveNotification(notification,mRealm);
        } else {
            Toast.makeText(this, R.string.unable_to_parse, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {
        AppController.getHelperProgressDialog().dismissDialog();
        if (statusCode.equalsIgnoreCase("401")) {
            Toast.makeText(NotificationActivity.this, errormsg, Toast.LENGTH_SHORT).show();
            AppController.getHelperSessionExpired(NotificationActivity.this, errormsg);
        } else {
            NotificationData notification = dbServiceSubscriber.getNotificationFromDB(mRealm);
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.MILLISECOND, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.HOUR, 0);
            RealmResults<NotificationDb> notificationsDb = dbServiceSubscriber.getNotificationDbByDate(new Date(), mRealm);
            RealmResults<NotificationDbResources> notificationsDbResources = dbServiceSubscriber.getNotificationDbResourcesByDate(new Date(), mRealm);
            RealmList<Notification> notifications = new RealmList<>();
            if(notificationsDb != null)
            {
                for (int i = 0; i < notificationsDb.size(); i++)
                {
                    Notification notification1 = new Notification();
                    notification1.setTitle(notificationsDb.get(i).getTitle());
                    if (notificationsDb.get(i).getType().equalsIgnoreCase("resources")) {
                        notification1.setSubtype("Resource");
                    } else {
                        notification1.setSubtype("Activity");
                    }
                    notification1.setStudyId(notificationsDb.get(i).getStudyId());
                    notification1.setMessage(notificationsDb.get(i).getDescription());
                    notification1.setDate(AppController.getDateFormat().format(notificationsDb.get(i).getDateTime()));
                    notification1.setType("Study");
                    notification1.setAudience("");
                    notifications.add(notification1);
                }
            }
            if(notificationsDbResources != null)
            {
                for (int i = 0; i < notificationsDbResources.size(); i++)
                {
                    Notification notification1 = new Notification();
                    notification1.setTitle(notificationsDbResources.get(i).getTitle());
                    if (notificationsDbResources.get(i).getType().equalsIgnoreCase("resources")) {
                        notification1.setSubtype("Resource");
                    } else {
                        notification1.setSubtype("Activity");
                    }
                    notification1.setStudyId(notificationsDbResources.get(i).getStudyId());
                    notification1.setMessage(notificationsDbResources.get(i).getDescription());
                    notification1.setDate(AppController.getDateFormat().format(notificationsDbResources.get(i).getDateTime()));
                    notification1.setType("Study");
                    notification1.setAudience("");
                    notifications.add(notification1);
                }
            }
            if (notification != null) {
                for (int i = 0; i < notification.getNotifications().size(); i++) {
                    if (notification.getNotifications().get(i).getType().equalsIgnoreCase("Study")) {
                        Study mStudy = dbServiceSubscriber.getStudyListFromDB(mRealm);
                        if (mStudy != null) {
                            RealmList<StudyList> studyListArrayList = mStudy.getStudies();
                            for (int j = 0; j < studyListArrayList.size(); j++) {
                                if (notification.getNotifications().get(i).getStudyId().equalsIgnoreCase(studyListArrayList.get(j).getStudyId())) {
                                    if (studyListArrayList.get(j).getStudyStatus().equalsIgnoreCase(StudyFragment.IN_PROGRESS)) {
                                        notifications.add(notification.getNotifications().get(i));
                                    }
                                }
                            }
                        }
                    } else {
                        notifications.add(notification.getNotifications().get(i));
                    }
                }
                Collections.sort(notifications, new Comparator<Notification>() {
                    public int compare(Notification o1, Notification o2) {
                        try {
                            return AppController.getDateFormat().parse(o1.getDate()).compareTo(AppController.getDateFormat().parse(o2.getDate()));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        return -1;
                    }
                });
                setRecyclearView(notifications);
            } else {
                Toast.makeText(this, errormsg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        dbServiceSubscriber.closeRealmObj(mRealm);
        super.onDestroy();
    }
}
