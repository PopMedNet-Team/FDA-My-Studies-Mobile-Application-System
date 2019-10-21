package com.harvard.utils.realm;

import android.support.annotation.Nullable;
import android.util.Log;

import com.harvard.BuildConfig;
import com.harvard.FDAApplication;

import java.io.File;
import java.text.NumberFormat;

public final class Logger {

    private String className;
    private boolean isEnabled;

    private Logger(String name, boolean enabled) {
        int i = name.lastIndexOf('.');
        className = name.substring(i + 1);
        isEnabled = enabled;
    }

    public static Logger getLogger(Class<?> cls) {
        return getLogger(cls, BuildConfig.DEBUG);
    }

    public static Logger getLogger(Class<?> cls, boolean enabled) {
        if (cls == null) {
            throw new NullPointerException();
        }
        return new Logger(cls.getName(), enabled);
    }

    public void debug(String msg) {
        if (isEnabled) {
            if (msg != null) {
                Log.d(className, msg);
            }
        }
    }

    public void warn(String msg) {
        if (isEnabled) {
            if (msg != null) {
                Log.w(className, msg);
            }
        }
    }

    public void warn(Exception e) {
        if (isEnabled) {
            if (e != null) {
                warn(e.getMessage());
            }
        }
    }

    public void error(String msg) {
        if (isEnabled) {
            if (msg != null) {
                Log.e(className, msg);
            }
        }
    }

    public void error(Exception e) {
        if (isEnabled) {
            if (e != null) {
                error(e.getMessage());
            }
        }
    }

    public void info(String msg) {
        if (isEnabled) {
            if (msg != null) {
                Log.i(className, msg);
            }
        }
    }

    public void showStacktrace() {
        if (isEnabled) {
            new RuntimeException("stacktrace / " + Thread.currentThread().getName()).printStackTrace();
        }
    }

    public void showStacktrace(Throwable e) {
        if (isEnabled) {
            e.printStackTrace();
        }
    }

    public static void showDbStats() {
        showDbStats(null);
    }

    public static void showDbStats(@Nullable String prepend) {
        StringBuilder stringBuilder = new StringBuilder((prepend != null ? prepend + ": " : "") + "DB files size: ");
        String root = "/data/data/" + FDAApplication.getInstance().getPackageName();
        int i = 0;
        File file = new File(root + "/files/" + "default" + ".realm");
        if (i > 0) {
            stringBuilder.append(", ");
        }
        NumberFormat formatter = NumberFormat.getInstance();
        String result = formatter.format(file.length());
        stringBuilder.append("default").append(": ").append(result);

        i++;

    }
}
