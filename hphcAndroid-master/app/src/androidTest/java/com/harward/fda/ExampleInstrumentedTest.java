package com.harward.fda;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.hphc.mystudies.notificationModule.NotificationModuleSubscriber;
import com.hphc.mystudies.storageModule.DBServiceSubscriber;
import com.hphc.mystudies.studyAppModule.activityBuilder.model.ActivityRun;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import io.realm.Realm;
import io.realm.RealmConfiguration;

import static junit.framework.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    private Context appContext;
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under test.
        appContext = InstrumentationRegistry.getTargetContext();

//        assertEquals("com.harward.fda", appContext.getPackageName());
    }

    @Test
    public void testNotification()
    {
        appContext = InstrumentationRegistry.getTargetContext();
        try {
            Realm.init(appContext);
            RealmConfiguration testConfig = new RealmConfiguration.Builder().build();
            Realm testRealm = Realm.getInstance(testConfig);
            SimpleDateFormat simpleDateFormat  = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            DBServiceSubscriber dbServiceSubscriber = new DBServiceSubscriber();
            ActivityRun activityRun = new ActivityRun();
            activityRun.setActivityId("DevFetal");
            activityRun.setCompleted(false);
            activityRun.setEndDate(simpleDateFormat.parse("12-01-2018 23:59:59"));
            activityRun.setRunId(5);
            activityRun.setStartDate(simpleDateFormat.parse("12-01-2018 13:40:00"));
            activityRun.setStudyId("TESTSTUDY01");
            NotificationModuleSubscriber notificationModuleSubscriber = new NotificationModuleSubscriber(dbServiceSubscriber, testRealm);
            notificationModuleSubscriber.generateActivityLocalNotification(activityRun, appContext, "Within a day", 0);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
