package com.harvard.offlineModule.auth;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.harvard.storageModule.DBServiceSubscriber;

/**
 * Service that keeps running SyncAdapter in background.
 */
public class SyncAdapterService extends Service {
    private static SyncAdapter syncAdapter = null;
    // Object to use as a thread-safe lock
    private static final Object syncAdapterLock = new Object();
    DBServiceSubscriber mDBServiceSubscriber;

    @Override
    public void onCreate() {
        super.onCreate();
    /*
     * Create the sync adapter as a singleton.
     * Set the sync adapter as syncable
     * Disallow parallel syncs
     */
        mDBServiceSubscriber = new DBServiceSubscriber();
        synchronized (syncAdapterLock) {
            if (syncAdapter == null) {
                syncAdapter = new SyncAdapter(this, true);
            }
        }
    }

    /**
     * Return an object that allows the system to invoke the sync adapter.
     */
    @Override
    public IBinder onBind(Intent intent) {
    /*
     * Get the object that allows external processes
     * to call onPerformSync(). The object is created
     * in the base class code when the SyncAdapter
     * constructors call super()
     */
        return syncAdapter.getSyncAdapterBinder();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
