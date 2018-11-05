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
package com.hphc.mystudies.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

import org.jsoup.Jsoup;

import java.io.IOException;

public class VersionChecker extends AsyncTask<String, String, String> {

    private String newVersion;
    private Upgrade upgrade;
    private Context context;
    public static final String PLAY_STORE_URL = "https://play.google.com/store/apps/details?id=com.harvard.fda&hl=en";
    public VersionChecker(Upgrade upgrade, Context context)
    {
        this.upgrade = upgrade;
        this.context = context;
    }
    @Override
    protected String doInBackground(String... params) {

        try {
            newVersion = Jsoup.connect(PLAY_STORE_URL)
                    .timeout(30000)
                    .userAgent("Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6")
                    .referrer("http://www.google.com")
                    .get()
                    .select("div[itemprop=softwareVersion]")
                    .first()
                    .ownText();


        } catch (IOException e) {
        }
        return newVersion;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        String currentVersion = currentVersion();
        newVersion = "7.2";
        if((currentVersion != null && currentVersion.equalsIgnoreCase(newVersion)) || newVersion == null)
        {
            upgrade.isUpgrade(false, newVersion);
        }
        else {
            upgrade.isUpgrade(true, newVersion);
        }
    }

    public interface Upgrade{
        void isUpgrade(boolean b, String newVersion);
    }
    public String currentVersion() {
        PackageInfo pInfo = null;
        try {
            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
        }
        if (pInfo == null) {
            return null;
        } else {
            return pInfo.versionName;
        }
    }

}