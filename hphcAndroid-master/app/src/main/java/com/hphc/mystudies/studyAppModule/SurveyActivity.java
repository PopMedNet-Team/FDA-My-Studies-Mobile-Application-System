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
package com.hphc.mystudies.studyAppModule;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.RelativeLayout;

import com.hphc.mystudies.R;
import com.hphc.mystudies.utils.AppController;

public class SurveyActivity extends AppCompatActivity implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback {
    RelativeLayout mBackBtn;
    AppCompatTextView mTitle;
    RelativeLayout mFilterBtn;
    private RelativeLayout myDashboardButtonLayout;
    private AppCompatImageView myDashboardButton;
    private AppCompatTextView myDashboardButtonLabel;
    private RelativeLayout mActivitiesButtonLayout;
    private AppCompatImageView mActivitiesButton;
    private AppCompatTextView mActivitiesButtonLabel;
    private RelativeLayout mResourcesButtonLayout;
    private AppCompatImageView mResourcesButton;
    private AppCompatTextView mResourcesButtonLabel;
    private String studyId;
    public String mFrom;
    public String mTo;
    SurveyDashboardFragment mSurveyDashboardFragment;
    SurveyActivitiesFragment mSurveyActivitiesFragment;
    SurveyResourcesFragment mSurveyResourcesFragment;

    private String title;
    private boolean bookmark;
    private String status;
    private String studyStatus;
    private String position;
    public String mActivityId = "";
    public String mLocalNotification = "";
    private static final String FRAGMENT_CONSTANT = "fragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        initializeXMLId();
        bindEvents();
        // default settings
        mSurveyDashboardFragment = new SurveyDashboardFragment();
        mSurveyActivitiesFragment = new SurveyActivitiesFragment();
        mSurveyResourcesFragment = new SurveyResourcesFragment();

        studyId = getIntent().getStringExtra("studyId");
        mActivityId = "";
        mLocalNotification = "";
        mFrom = "";
        mTo = "";

        if (getIntent().getStringExtra("from") != null && getIntent().getStringExtra("from").equalsIgnoreCase("NotificationActivity")) {
            mFrom = "NotificationActivity";
            if(getIntent().getStringExtra("activityId") != null)
            {
                mActivityId = getIntent().getStringExtra("activityId");
            }
            if(getIntent().getStringExtra("localNotification") != null)
            {
                mLocalNotification = getIntent().getStringExtra("localNotification");
            }
            if (getIntent().getStringExtra("to") != null && getIntent().getStringExtra("to").equalsIgnoreCase("Activity")) {
                defaultFragementSettings();
            } else if (getIntent().getStringExtra("to") != null && getIntent().getStringExtra("to").equalsIgnoreCase("Resource")) {
                openResources();
            } else {
                defaultFragementSettings();
            }
        } else {
            defaultFragementSettings();
        }

        try {
            title = AppController.getHelperSharedPreference().readPreference(SurveyActivity.this, getResources().getString(R.string.title), "");
            if (AppController.getHelperSharedPreference().readPreference(SurveyActivity.this, getResources().getString(R.string.bookmark), "").equalsIgnoreCase("true"))
                bookmark = true;
            else
                bookmark = false;
            status = AppController.getHelperSharedPreference().readPreference(SurveyActivity.this, getResources().getString(R.string.status), "");
            studyStatus = AppController.getHelperSharedPreference().readPreference(SurveyActivity.this, getResources().getString(R.string.studyStatus), "");
            position = AppController.getHelperSharedPreference().readPreference(SurveyActivity.this, getResources().getString(R.string.position), "");
        } catch (Exception e) {
        }
    }

    private void initializeXMLId() {
        mBackBtn = (RelativeLayout) findViewById(R.id.backBtn);
        mTitle = (AppCompatTextView) findViewById(R.id.title);
        mFilterBtn = (RelativeLayout) findViewById(R.id.filterBtn);
        myDashboardButtonLayout = (RelativeLayout) findViewById(R.id.myDashboardButtonLayout);
        myDashboardButton = (AppCompatImageView) findViewById(R.id.myDashboardButton);
        myDashboardButtonLabel = (AppCompatTextView) findViewById(R.id.myDashboardButtonLabel);
        mActivitiesButtonLayout = (RelativeLayout) findViewById(R.id.mActivitiesButtonLayout);
        mActivitiesButton = (AppCompatImageView) findViewById(R.id.mActivitiesButton);
        mActivitiesButtonLabel = (AppCompatTextView) findViewById(R.id.mActivitiesButtonLabel);
        mResourcesButtonLayout = (RelativeLayout) findViewById(R.id.mResourcesButtonLayout);
        mResourcesButton = (AppCompatImageView) findViewById(R.id.mResourcesButton);
        mResourcesButtonLabel = (AppCompatTextView) findViewById(R.id.mResourcesButtonLabel);


    }

    private void bindEvents() {
        myDashboardButtonLayout.setOnClickListener(this);
        mActivitiesButtonLayout.setOnClickListener(this);
        mResourcesButtonLayout.setOnClickListener(this);
    }

    private void defaultFragementSettings() {
        myDashboardButton.setBackgroundResource(R.drawable.dashboard_grey);
        mActivitiesButton.setBackgroundResource(R.drawable.activities_blue_active);
        mResourcesButton.setBackgroundResource(R.drawable.resources_grey);
        myDashboardButtonLabel.setTextColor(getResources().getColor(R.color.colorPrimaryBlack));
        mActivitiesButtonLabel.setTextColor(getResources().getColor(R.color.colorPrimary));
        mResourcesButtonLabel.setTextColor(getResources().getColor(R.color.colorPrimaryBlack));
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutContainer, mSurveyActivitiesFragment, FRAGMENT_CONSTANT).commit();
    }

    private void openResources() {
        myDashboardButton.setBackgroundResource(R.drawable.dashboard_grey);
        mActivitiesButton.setBackgroundResource(R.drawable.activities_grey);
        mResourcesButton.setBackgroundResource(R.drawable.resources_blue_active);
        myDashboardButtonLabel.setTextColor(getResources().getColor(R.color.colorPrimaryBlack));
        mActivitiesButtonLabel.setTextColor(getResources().getColor(R.color.colorPrimaryBlack));
        mResourcesButtonLabel.setTextColor(getResources().getColor(R.color.colorPrimary));
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutContainer, mSurveyResourcesFragment, FRAGMENT_CONSTANT).commit();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.myDashboardButtonLayout:
                myDashboardButton.setBackgroundResource(R.drawable.dashboard_blue_active);
                mActivitiesButton.setBackgroundResource(R.drawable.activities_grey);
                mResourcesButton.setBackgroundResource(R.drawable.resources_grey);
                myDashboardButtonLabel.setTextColor(getResources().getColor(R.color.colorPrimary));
                mActivitiesButtonLabel.setTextColor(getResources().getColor(R.color.colorPrimaryBlack));
                mResourcesButtonLabel.setTextColor(getResources().getColor(R.color.colorPrimaryBlack));
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutContainer, mSurveyDashboardFragment, FRAGMENT_CONSTANT).commit();
                break;

            case R.id.mActivitiesButtonLayout:
                myDashboardButton.setBackgroundResource(R.drawable.dashboard_grey);
                mActivitiesButton.setBackgroundResource(R.drawable.activities_blue_active);
                mResourcesButton.setBackgroundResource(R.drawable.resources_grey);
                myDashboardButtonLabel.setTextColor(getResources().getColor(R.color.colorPrimaryBlack));
                mActivitiesButtonLabel.setTextColor(getResources().getColor(R.color.colorPrimary));
                mResourcesButtonLabel.setTextColor(getResources().getColor(R.color.colorPrimaryBlack));
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutContainer, mSurveyActivitiesFragment, FRAGMENT_CONSTANT).commit();
                break;

            case R.id.mResourcesButtonLayout:
                myDashboardButton.setBackgroundResource(R.drawable.dashboard_grey);
                mActivitiesButton.setBackgroundResource(R.drawable.activities_grey);
                mResourcesButton.setBackgroundResource(R.drawable.resources_blue_active);
                myDashboardButtonLabel.setTextColor(getResources().getColor(R.color.colorPrimaryBlack));
                mActivitiesButtonLabel.setTextColor(getResources().getColor(R.color.colorPrimaryBlack));
                mResourcesButtonLabel.setTextColor(getResources().getColor(R.color.colorPrimary));
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutContainer, mSurveyResourcesFragment, FRAGMENT_CONSTANT).commit();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(SurveyActivity.this, StudyActivity.class);
        ComponentName cn = intent.getComponent();
        Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
        startActivity(mainIntent);
        finish();
    }

    public String getStudyId() {
        return studyId;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1000) {
            if (mSurveyActivitiesFragment != null)
                mSurveyActivitiesFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        } else if (requestCode == 2000 && mSurveyDashboardFragment != null)
            mSurveyDashboardFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public String getTitle1() {
        return title;
    }

    public boolean getBookmark() {
        return bookmark;
    }

    public String getStatus() {
        return status;
    }

    public String getStudyStatus() {
        return studyStatus;
    }

    public String getPosition() {
        return position;
    }

}
