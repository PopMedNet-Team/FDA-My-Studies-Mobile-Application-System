package com.harvard.offlineModule.auth;

import android.accounts.Account;
import android.app.ActivityManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.SyncResult;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.harvard.R;
import com.harvard.offlineModule.model.OfflineData;
import com.harvard.storageModule.DBServiceSubscriber;
import com.harvard.studyAppModule.StudyModulePresenter;
import com.harvard.studyAppModule.events.ProcessResponseEvent;
import com.harvard.userModule.UserModulePresenter;
import com.harvard.userModule.event.UpdatePreferenceEvent;
import com.harvard.userModule.webserviceModel.LoginData;
import com.harvard.utils.ActiveTaskService;
import com.harvard.utils.AppController;
import com.harvard.webserviceModule.apiHelper.ApiCall;
import com.harvard.webserviceModule.apiHelper.ApiCallResponseServer;
import com.harvard.webserviceModule.events.RegistrationServerConfigEvent;
import com.harvard.webserviceModule.events.ResponseServerConfigEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmResults;


public class SyncAdapter extends AbstractThreadedSyncAdapter implements ApiCall.OnAsyncRequestComplete, ApiCallResponseServer.OnAsyncRequestComplete {

    private Context mContext;
    private int UPDATE_USERPREFERENCE_RESPONSECODE = 102;
    private DBServiceSubscriber dbServiceSubscriber;
    Realm mRealm;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        this.mContext = context;
        dbServiceSubscriber = new DBServiceSubscriber();

    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient contentProviderClient, SyncResult syncResult) {

        if (!isMyServiceRunning(ActiveTaskService.class)) {
            Intent myIntent = new Intent(mContext, ActiveTaskService.class);
            myIntent.putExtra("SyncAdapter", "yes");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mContext.startForegroundService(myIntent);
            } else {
                mContext.startService(myIntent);
            }
        }

    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    private void getPendingData() {
        try {
            dbServiceSubscriber = new DBServiceSubscriber();

            RealmResults<OfflineData> results = dbServiceSubscriber.getOfflineData(mRealm);
            if (results.size() > 0) {
                for (int i = 0; i < results.size(); i++) {
                    String httpMethod = results.get(i).getHttpMethod().toString();
                    String url = results.get(i).getUrl().toString();
                    String normalParam = results.get(i).getNormalParam().toString();
                    String jsonObject = results.get(i).getJsonParam().toString();
                    String serverType = results.get(i).getServerType().toString();
                    updateServer(httpMethod, url, normalParam, jsonObject, serverType);
                    break;
                }
            } else {
                dbServiceSubscriber.closeRealmObj(mRealm);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateServer(String httpMethod, String url, String normalParam, String jsonObjectString, String serverType) {

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonObjectString);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (serverType.equalsIgnoreCase("registration")) {
            HashMap<String, String> header = new HashMap();
            header.put("auth", AppController.getHelperSharedPreference().readPreference(mContext, mContext.getResources().getString(R.string.auth), ""));
            header.put("userId", AppController.getHelperSharedPreference().readPreference(mContext, mContext.getResources().getString(R.string.userid), ""));

            UpdatePreferenceEvent updatePreferenceEvent = new UpdatePreferenceEvent();
            RegistrationServerConfigEvent registrationServerConfigEvent = new RegistrationServerConfigEvent(httpMethod, url, UPDATE_USERPREFERENCE_RESPONSECODE, mContext, LoginData.class, null, header, jsonObject, false, this);
            updatePreferenceEvent.setmRegistrationServerConfigEvent(registrationServerConfigEvent);
            UserModulePresenter userModulePresenter = new UserModulePresenter();
            userModulePresenter.performUpdateUserPreference(updatePreferenceEvent);
        } else if (serverType.equalsIgnoreCase("response")) {
            ProcessResponseEvent processResponseEvent = new ProcessResponseEvent();
            ResponseServerConfigEvent responseServerConfigEvent = new ResponseServerConfigEvent(httpMethod, url, UPDATE_USERPREFERENCE_RESPONSECODE, mContext, LoginData.class, null, null, jsonObject, false, this);

            processResponseEvent.setResponseServerConfigEvent(responseServerConfigEvent);
            StudyModulePresenter studyModulePresenter = new StudyModulePresenter();
            studyModulePresenter.performProcessResponse(processResponseEvent);
        } else if (serverType.equalsIgnoreCase("wcp")) {
        }
    }


    @Override
    public <T> void asyncResponse(T response, int responseCode) {
        if (responseCode == UPDATE_USERPREFERENCE_RESPONSECODE) {
            dbServiceSubscriber.removeOfflineData(mContext);
            getPendingData();
        }
    }

    @Override
    public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {
    }

    @Override
    public <T> void asyncResponse(T response, int responseCode, String serverType) {
        if (responseCode == UPDATE_USERPREFERENCE_RESPONSECODE) {
            dbServiceSubscriber.removeOfflineData(mContext);
            getPendingData();
        }
    }

    @Override
    public <T> void asyncResponseFailure(int responseCode, String errormsg, String statusCode, T response) {
    }
}