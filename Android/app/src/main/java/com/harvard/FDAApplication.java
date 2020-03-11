package com.harvard;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.multidex.MultiDex;

import com.crashlytics.android.Crashlytics;
import com.facebook.stetho.Stetho;
import com.harvard.notificationModule.NotificationModuleSubscriber;
import com.harvard.passcodeModule.PasscodeSetupActivity;
import com.harvard.storageModule.DBServiceSubscriber;
import com.harvard.studyAppModule.StudyModuleSubscriber;
import com.harvard.userModule.UserModuleSubscriber;
import com.harvard.userModule.webserviceModel.UserProfileData;
import com.harvard.utils.AppController;
import com.harvard.utils.AppVisibilityDetector;
import com.harvard.utils.realm.RealmEncryptionHelper;
import com.harvard.webserviceModule.WebserviceSubscriber;
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

//        researchstackinit();
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
                    Realm mRealm = AppController.getRealmobj(getBaseContext());
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

    private void researchstackinit() {
        // Customize your pin code preferences
        PinCodeConfig pinCodeConfig = new PinCodeConfig(); // default pin config (4-digit, 1 min lockout)

        // Customize encryption preferences
        EncryptionProvider encryptionProvider = new UnencryptedProvider(); // No pin, no encryption

        // If you have special file handling needs, implement FileAccess
        FileAccess fileAccess = new SimpleFileAccess();

        // If you have your own custom database, implement AppDatabase
        AppDatabase database = new DatabaseHelper(this,
                DatabaseHelper.DEFAULT_NAME,
                null,
                DatabaseHelper.DEFAULT_VERSION);

        StorageAccess.getInstance().init(pinCodeConfig, encryptionProvider, null, null);

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
            nm.createNotificationChannel(new NotificationChannel(NOTIFICATION_CHANNEL_ID_SERVICE, "App Service", NotificationManager.IMPORTANCE_DEFAULT));
            nm.createNotificationChannel(new NotificationChannel(NOTIFICATION_CHANNEL_ID_INFO, "General", NotificationManager.IMPORTANCE_HIGH));
        }
    }
}
