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
package com.hphc.mystudies.EligibilityModule;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.hphc.mystudies.R;
import com.hphc.mystudies.storageModule.DBServiceSubscriber;
import com.hphc.mystudies.studyAppModule.StudyFragment;
import com.hphc.mystudies.userModule.UserModulePresenter;
import com.hphc.mystudies.userModule.event.UpdatePreferenceEvent;
import com.hphc.mystudies.userModule.webserviceModel.LoginData;
import com.hphc.mystudies.utils.AppController;
import com.hphc.mystudies.utils.URLs;
import com.hphc.mystudies.webserviceModule.apiHelper.ApiCall;
import com.hphc.mystudies.webserviceModule.events.RegistrationServerConfigEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class NotEligibleActivity extends AppCompatActivity implements ApiCall.OnAsyncRequestComplete {
    private static final int UPDATE_USERPREFERENCE_RESPONSECODE = 200;
    private static final String STUDY_ID = "studyId";
    DBServiceSubscriber dbServiceSubscriber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_not_eligible);
        dbServiceSubscriber = new DBServiceSubscriber();
        TextView textView = (TextView) findViewById(R.id.notEligibleOK);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        updateuserpreference();
    }

    public void updateuserpreference() {
        AppController.getHelperProgressDialog().showProgress(NotEligibleActivity.this, "", "", false);
        UpdatePreferenceEvent updatePreferenceEvent = new UpdatePreferenceEvent();

        HashMap<String, String> header = new HashMap();
        header.put("auth", AppController.getHelperSharedPreference().readPreference(this, getResources().getString(R.string.auth), ""));
        header.put("userId", AppController.getHelperSharedPreference().readPreference(this, getResources().getString(R.string.userid), ""));

        JSONObject jsonObject = new JSONObject();

        JSONArray studieslist = new JSONArray();
        JSONObject studiestatus = new JSONObject();
        try {
            studiestatus.put("studyId", getIntent().getStringExtra(STUDY_ID));
            studiestatus.put("status", StudyFragment.NOT_ELIGIBLE);
        } catch (JSONException e) {
        }

        studieslist.put(studiestatus);
        try {
            jsonObject.put("studies", studieslist);
        } catch (JSONException e) {
        }
        RegistrationServerConfigEvent registrationServerConfigEvent = new RegistrationServerConfigEvent("post_object", URLs.UPDATE_STUDY_PREFERENCE, UPDATE_USERPREFERENCE_RESPONSECODE, this, LoginData.class, null, header, jsonObject, false, this);

        updatePreferenceEvent.setmRegistrationServerConfigEvent(registrationServerConfigEvent);
        UserModulePresenter userModulePresenter = new UserModulePresenter();
        userModulePresenter.performUpdateUserPreference(updatePreferenceEvent);
    }

    @Override
    public <T> void asyncResponse(T response, int responseCode) {
        AppController.getHelperProgressDialog().dismissDialog();
        dbServiceSubscriber.updateStudyPreferenceDB(getIntent().getStringExtra(STUDY_ID), StudyFragment.NOT_ELIGIBLE, "", "", "");
    }

    @Override
    public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {
        AppController.getHelperProgressDialog().dismissDialog();
    }
}
