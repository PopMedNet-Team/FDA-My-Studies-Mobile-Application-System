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

package com.harvard.fda.utils.realm;

import android.support.annotation.Nullable;
import android.util.Log;

import com.harvard.fda.BuildConfig;
import com.harvard.fda.FDAApplication;

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

        Log.e("compactRealm size", stringBuilder.toString());
    }
}
