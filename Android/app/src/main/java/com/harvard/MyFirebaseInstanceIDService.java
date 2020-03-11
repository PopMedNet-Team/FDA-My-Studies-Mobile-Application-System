package com.harvard;


import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.harvard.utils.AppController;


public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
        AppController.getHelperSharedPreference().writePreference(this, "deviceToken", token);
        sendRegistrationToServer();
    }

    private void sendRegistrationToServer() {
    }
}