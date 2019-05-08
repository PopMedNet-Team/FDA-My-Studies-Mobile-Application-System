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

package com.harvard.fda.offlineModule.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;

import com.harvard.fda.AppConfig;


public class SyncAdapterManager {
    public static final String AUTHORITY = AppConfig.PackageName + ".offlineModule.auth.syncadapterexample.provider";
    public static final String ACCOUNT_TYPE = AppConfig.PackageName + ".offlineModule.auth.syncadapterexample";
    public static final String ACCOUNT = "fdaaccount";
    public static Account newAccount;
    // Sync interval constants
    public static final long SYNC_FREQUENCY = 60 * 15;
    private static Context mContext;

    public static void init(Context context) {
        mContext = context;
        newAccount = new Account(ACCOUNT, ACCOUNT_TYPE);
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);
        boolean flag = false;
        try {
            flag = accountManager.addAccountExplicitly(newAccount, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (flag) {
            ContentResolver.setIsSyncable(newAccount, AUTHORITY, 1);
            ContentResolver.setSyncAutomatically(newAccount, AUTHORITY, true);
            ContentResolver.addPeriodicSync(
                    newAccount,
                    AUTHORITY,
                    Bundle.EMPTY, SYNC_FREQUENCY);
        }

    }

}
