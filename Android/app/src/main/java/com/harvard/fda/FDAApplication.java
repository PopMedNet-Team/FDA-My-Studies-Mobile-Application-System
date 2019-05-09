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

package com.harvard.fda;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import com.harvard.fda.notificationModule.NotificationModuleSubscriber;
import com.harvard.fda.passcodeModule.PasscodeSetupActivity;
import com.harvard.fda.storageModule.DBServiceSubscriber;
import com.harvard.fda.studyAppModule.StudyModuleSubscriber;
import com.harvard.fda.userModule.UserModuleSubscriber;
import com.harvard.fda.userModule.webserviceModel.UserProfileData;
import com.harvard.fda.utils.AppController;
import com.harvard.fda.utils.AppVisibilityDetector;
import com.harvard.fda.utils.realm.RealmEncryptionHelper;
import com.harvard.fda.webserviceModule.WebserviceSubscriber;
import com.uphyca.stetho_realm.RealmInspectorModulesProvider;

import org.researchstack.backbone.StorageAccess;
import org.researchstack.backbone.storage.database.AppDatabase;
import org.researchstack.backbone.storage.database.sqlite.DatabaseHelper;
import org.researchstack.backbone.storage.file.EncryptionProvider;
import org.researchstack.backbone.storage.file.FileAccess;
import org.researchstack.backbone.storage.file.PinCodeConfig;
import org.researchstack.backbone.storage.file.SimpleFileAccess;
import org.researchstack.backbone.storage.file.UnencryptedProvider;

import java.util.Calendar;

import io.fabric.sdk.android.Fabric;
import io.realm.Realm;

import static android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP;

/**
 * Created by Rohit on 2/15/2017.
 */

public class FDAApplication extends Application {
    private static FDAApplication sInstance;
    private FDAEventBusRegistry mRegistry;

    public static final String NOTIFICATION_CHANNEL_ID_SERVICE = AppConfig.PackageName + ".service";
    public static final String NOTIFICATION_CHANNEL_ID_INFO = AppConfig.PackageName + ".general";

    public static FDAApplication getInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        sInstance = this;
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        dbInitialize();
        initChannel();

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
                    e.printStackTrace();
                }
            }

            @Override
            public void onAppGotoBackground() {
                //app is from foreground to background

                try {
                    Realm mRealm = AppController.getRealmobj(getApplicationContext());
                    DBServiceSubscriber dbServiceSubscriber = new DBServiceSubscriber();
                    UserProfileData mUserProfileData = dbServiceSubscriber.getUserProfileData(mRealm);
                    boolean isNotification = true;
                    if (mUserProfileData != null) {
                        isNotification = mUserProfileData.getSettings().isLocalNotifications();
                    }
                    if (isNotification) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.DATE, 14);
                        NotificationModuleSubscriber notificationModuleSubscriber = new NotificationModuleSubscriber(null, null);
                        notificationModuleSubscriber.generateTwoWeekNotification(calendar.getTime(), getApplicationContext());
                    }
                    dbServiceSubscriber.closeRealmObj(mRealm);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void dbInitialize() {
        Realm.init(this);

        RealmEncryptionHelper realmEncryptionHelper = RealmEncryptionHelper.initHelper(this, getString(R.string.app_name));
        byte[] key = realmEncryptionHelper.getEncryptKey();

        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(RealmInspectorModulesProvider.builder(this).withLimit(10000)
                                .withDefaultEncryptionKey(key)

                                .build())
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

    public void initChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            nm.createNotificationChannel(new NotificationChannel(NOTIFICATION_CHANNEL_ID_SERVICE, "App Service", NotificationManager.IMPORTANCE_HIGH));
            nm.createNotificationChannel(new NotificationChannel(NOTIFICATION_CHANNEL_ID_INFO, "General", NotificationManager.IMPORTANCE_HIGH));
        }
    }
}
