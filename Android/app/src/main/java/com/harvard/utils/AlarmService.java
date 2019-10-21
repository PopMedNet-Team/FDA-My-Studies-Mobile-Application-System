package com.harvard.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Naveen Raj on 06/02/2017.
 */

public class AlarmService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Intent myIntent = new Intent(context, ActiveTaskService.class);
            myIntent.putExtra("broadcast", "yes");
            context.startService(myIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}