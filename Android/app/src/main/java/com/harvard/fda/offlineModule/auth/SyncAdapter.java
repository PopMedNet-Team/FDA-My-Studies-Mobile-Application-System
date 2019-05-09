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
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import com.harvard.fda.R;
import com.harvard.fda.offlineModule.model.OfflineData;
import com.harvard.fda.storageModule.DBServiceSubscriber;
import com.harvard.fda.studyAppModule.StudyModulePresenter;
import com.harvard.fda.studyAppModule.events.ProcessResponseEvent;
import com.harvard.fda.userModule.UserModulePresenter;
import com.harvard.fda.userModule.event.UpdatePreferenceEvent;
import com.harvard.fda.userModule.webserviceModel.LoginData;
import com.harvard.fda.utils.AppController;
import com.harvard.fda.webserviceModule.apiHelper.ApiCall;
import com.harvard.fda.webserviceModule.apiHelper.ApiCallResponseServer;
import com.harvard.fda.webserviceModule.events.RegistrationServerConfigEvent;
import com.harvard.fda.webserviceModule.events.ResponseServerConfigEvent;

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
        getPendingData();
    }


    private void getPendingData() {

        try {
            dbServiceSubscriber = new DBServiceSubscriber();
            mRealm = AppController.getRealmobj(mContext);
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
            }
            dbServiceSubscriber.closeRealmObj(mRealm);
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