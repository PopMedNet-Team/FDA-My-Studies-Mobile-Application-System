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

package com.harvard.fda.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Application;
import android.app.Application.ActivityLifecycleCallbacks;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

/**
 * Created by Naveen Raj on 05/06/2017.
 */

public class AppVisibilityDetector {
    private static boolean DEBUG = true;
    private static final String TAG = "AppVisibilityDetector";
    private static AppVisibilityCallback sAppVisibilityCallback;
    private static boolean sIsForeground = false;
    private static Handler sHandler;
    private static final int MSG_GOTO_FOREGROUND = 1;
    private static final int MSG_GOTO_BACKGROUND = 2;

    public static void init(final Application app, AppVisibilityCallback appVisibilityCallback) {
        checkIsMainProcess(app);
        sAppVisibilityCallback = appVisibilityCallback;
        app.registerActivityLifecycleCallbacks(new AppActivityLifecycleCallbacks());

        sHandler = new Handler(app.getMainLooper()) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_GOTO_FOREGROUND:
                        performAppGotoForeground();
                        break;
                    case MSG_GOTO_BACKGROUND:
                        performAppGotoBackground();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private static void checkIsMainProcess(Application app) {
        ActivityManager activityManager = (ActivityManager) app.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> runningAppProcessInfoList = activityManager.getRunningAppProcesses();
        String currProcessName = null;
        int currPid = android.os.Process.myPid();
        //find the process name
        for (RunningAppProcessInfo processInfo : runningAppProcessInfoList) {
            if (processInfo.pid == currPid) {
                currProcessName = processInfo.processName;
            }
        }

        //is current process the main process
        try {
            if (!TextUtils.equals(currProcessName, app.getPackageName())) {
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void performAppGotoForeground() {
        if (!sIsForeground && null != sAppVisibilityCallback) {
            sIsForeground = true;
            sAppVisibilityCallback.onAppGotoForeground();
        }
    }

    private static void performAppGotoBackground() {
        if (sIsForeground && null != sAppVisibilityCallback) {
            sIsForeground = false;
            sAppVisibilityCallback.onAppGotoBackground();
        }
    }

    public interface AppVisibilityCallback {
        void onAppGotoForeground();

        void onAppGotoBackground();
    }

    private static class AppActivityLifecycleCallbacks implements ActivityLifecycleCallbacks {
        int activityDisplayCount = 0;

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        }

        @Override
        public void onActivityStarted(Activity activity) {
            sHandler.removeMessages(MSG_GOTO_FOREGROUND);
            sHandler.removeMessages(MSG_GOTO_BACKGROUND);
            if (activityDisplayCount == 0) {
                sHandler.sendEmptyMessage(MSG_GOTO_FOREGROUND);
            }
            activityDisplayCount++;

        }

        @Override
        public void onActivityResumed(Activity activity) {
        }

        @Override
        public void onActivityPaused(Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override
        public void onActivityStopped(Activity activity) {
            sHandler.removeMessages(MSG_GOTO_FOREGROUND);
            sHandler.removeMessages(MSG_GOTO_BACKGROUND);
            if (activityDisplayCount > 0) {
                activityDisplayCount--;
            }

            if (activityDisplayCount == 0) {
                sHandler.sendEmptyMessage(MSG_GOTO_BACKGROUND);
            }

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
        }
    }
}
