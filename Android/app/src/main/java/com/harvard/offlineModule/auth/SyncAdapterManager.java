package com.harvard.offlineModule.auth;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.os.Bundle;

import com.harvard.AppConfig;
import com.harvard.R;


public class SyncAdapterManager {
    public static final String AUTHORITY = AppConfig.PackageName + ".offlineModule.auth.syncadapterexample.provider";
    public static final String ACCOUNT_TYPE = AppConfig.PackageName + ".offlineModule.auth.syncadapterexample";
    public static String ACCOUNT = "";
    public static Account newAccount;
    // Sync interval constants
    public static final long SYNC_FREQUENCY = 60 * 15;
    private static Context mContext;

    public static void init(Context context) {
        mContext = context;
        ACCOUNT = context.getString(R.string.app_name);
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
