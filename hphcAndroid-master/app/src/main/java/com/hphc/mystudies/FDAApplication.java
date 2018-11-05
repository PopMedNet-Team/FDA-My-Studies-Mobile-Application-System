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
package com.hphc.mystudies;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import com.hphc.mystudies.notificationModule.NotificationModuleSubscriber;
import com.hphc.mystudies.passcodeModule.PasscodeSetupActivity;
import com.hphc.mystudies.storageModule.DBServiceSubscriber;
import com.hphc.mystudies.studyAppModule.StudyModuleSubscriber;
import com.hphc.mystudies.userModule.UserModuleSubscriber;
import com.hphc.mystudies.userModule.webserviceModel.UserProfileData;
import com.hphc.mystudies.utils.AppController;
import com.hphc.mystudies.utils.AppVisibilityDetector;
import com.hphc.mystudies.webserviceModule.WebserviceSubscriber;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;
import java.util.Calendar;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

public class FDAApplication extends Application {
    private static FDAApplication sInstance;
    private FDAEventBusRegistry mRegistry;

    public static FDAApplication getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        sInstance = this;
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        dbInitialize();
        startEventProcessing();


        AppVisibilityDetector.init(FDAApplication.this, new AppVisibilityDetector.AppVisibilityCallback() {
            @Override
            public void onAppGotoForeground() {
                if (AppController.getHelperSharedPreference().readPreference(getApplicationContext(), getResources().getString(R.string.usepasscode), "").equalsIgnoreCase("yes")) {
                    AppController.getHelperSharedPreference().writePreference(getApplicationContext(), "passcodeAnswered", "no");
                    Intent intent = new Intent(getApplicationContext(), PasscodeSetupActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
                try {
                    NotificationModuleSubscriber notificationModuleSubscriber = new NotificationModuleSubscriber(null, null);
                    notificationModuleSubscriber.cancelTwoWeekNotification(getApplicationContext());
                } catch (Exception e) {
                }
            }

            @Override
            public void onAppGotoBackground() {
                //app is from foreground to background

                try {
                    Realm mRealm = AppController.getRealmobj();
                    DBServiceSubscriber dbServiceSubscriber = new DBServiceSubscriber();
                    UserProfileData mUserProfileData = dbServiceSubscriber.getUserProfileData(mRealm);
                    boolean isNotification = true;
                    if(mUserProfileData != null) {
                        isNotification = mUserProfileData.getSettings().isLocalNotifications();
                    }
                    if(isNotification)
                    {
                        Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.DATE, 14);
                        NotificationModuleSubscriber notificationModuleSubscriber = new NotificationModuleSubscriber(null, null);
                        notificationModuleSubscriber.generateTwoWeekNotification(calendar.getTime(), getApplicationContext());
                    }
                    dbServiceSubscriber.closeRealmObj(mRealm);
                } catch (Exception e) {
                }

            }
        });
    }

    private void dbInitialize() {
        Realm.init(this);
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).withLimit(10000).build())
                        .build());
    }

    private void startEventProcessing() {
        mRegistry = new FDAEventBusRegistry(this);
        mRegistry.registerDefaultSubscribers();
        mRegistry.registerSubscriber(new StudyModuleSubscriber());
        mRegistry.registerSubscriber(new UserModuleSubscriber());
        mRegistry.registerSubscriber(new WebserviceSubscriber());
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        sInstance = null;
        mRegistry.unregisterAllSubscribers();
        mRegistry = null;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
