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

package com.harvard.fda.studyAppModule;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.harvard.fda.AppConfig;
import com.harvard.fda.R;
import com.harvard.fda.notificationModule.NotificationModuleSubscriber;
import com.harvard.fda.offlineModule.model.OfflineData;
import com.harvard.fda.storageModule.DBServiceSubscriber;
import com.harvard.fda.userModule.UserModulePresenter;
import com.harvard.fda.userModule.event.LogoutEvent;
import com.harvard.fda.userModule.webserviceModel.LoginData;
import com.harvard.fda.utils.AppController;
import com.harvard.fda.utils.SharedPreferenceHelper;
import com.harvard.fda.utils.URLs;
import com.harvard.fda.webserviceModule.apiHelper.ApiCall;
import com.harvard.fda.webserviceModule.events.RegistrationServerConfigEvent;

import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmResults;

public class SurveyActivity extends AppCompatActivity implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback, ApiCall.OnAsyncRequestComplete {
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

    private final int LOGOUT_REPSONSECODE = 100;

    private String title;
    private boolean bookmark;
    private String status;
    private String studyStatus;
    private String position;
    private String enroll;
    private String rejoin;
    public String mActivityId = "";
    public String mLocalNotification = "";
    LinearLayout menulayout;
    DrawerLayout mDrawer;
    LinearLayout mHomeLayout;
    AppCompatTextView mHomeLabel;
    LinearLayout mResourcesLayout;
    AppCompatTextView mResourceLabel;
    LinearLayout mReachoutLayout;
    AppCompatTextView mReachoutLabel;
    LinearLayout mSignInProfileLayout;
    AppCompatImageView mSigninImg;
    AppCompatTextView mSigninLabel;
    LinearLayout mNewUsrReachoutLayout;
    AppCompatImageView mNewUsrReachoutImg;
    AppCompatTextView mSignUpLabel;
    RelativeLayout mSignOutLayout;
    AppCompatTextView mSignOutLabel;
    AppCompatTextView mNewUsrReachoutLabel;
    private int mPreviousValue = 0;
    DBServiceSubscriber dbServiceSubscriber;
    Realm mRealm;
    Toolbar mToolbar;
    private boolean isExit = false;
    TextView menutitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_survey);
        initializeXMLId();
        bindEvents();
        // default settings

        isExit = false;
        mSurveyDashboardFragment = new SurveyDashboardFragment();
        mSurveyActivitiesFragment = new SurveyActivitiesFragment();
        mSurveyResourcesFragment = new SurveyResourcesFragment();

        studyId = getIntent().getStringExtra("studyId");
        mActivityId = "";
        mLocalNotification = "";
        mFrom = "";
        mTo = "";

        dbServiceSubscriber = new DBServiceSubscriber();
        mRealm = AppController.getRealmobj(this);
        checkSignOrSignOutScenario();

        if (getIntent().getStringExtra("from") != null && getIntent().getStringExtra("from").equalsIgnoreCase("NotificationActivity")) {
            mFrom = "NotificationActivity";
            if (getIntent().getStringExtra("activityId") != null) {
                mActivityId = getIntent().getStringExtra("activityId");
            }
            if (getIntent().getStringExtra("localNotification") != null) {
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
            enroll = AppController.getHelperSharedPreference().readPreference(SurveyActivity.this, getResources().getString(R.string.enroll), "");
            rejoin = AppController.getHelperSharedPreference().readPreference(SurveyActivity.this, getResources().getString(R.string.rejoin), "");
        } catch (Exception e) {
            e.printStackTrace();
        }

        mDrawer = findViewById(R.id.survey_menu);
        if (AppConfig.AppType.equalsIgnoreCase(getString(R.string.app_gateway))) {
            mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        } else {
            mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        }

        mDrawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(View drawerView) {
                checkSignOrSignOutScenario();
            }

            @Override
            public void onDrawerClosed(View drawerView) {

            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });
    }


    private void checkSignOrSignOutScenario() {
        //signIn
        if (AppController.getHelperSharedPreference().readPreference(SurveyActivity.this, getString(R.string.userid), "").equalsIgnoreCase("")) {
            mSigninImg.setBackground(getResources().getDrawable(R.drawable.signin_menu1));
            mSigninLabel.setText(getResources().getString(R.string.sign_in_btn));
            mSignOutLayout.setVisibility(View.GONE);
            mReachoutLayout.setVisibility(View.VISIBLE);
            //set Reach out details to new user,
            mNewUsrReachoutImg.setBackground(getResources().getDrawable(R.drawable.newuser_menu1));
            mNewUsrReachoutLabel.setText(getResources().getString(R.string.side_menu_new_user));
            mSignUpLabel.setVisibility(View.VISIBLE);
        } else {
            //Sign out
            mSigninImg.setBackground(getResources().getDrawable(R.drawable.profile_menu1));
            mSigninLabel.setText(getResources().getString(R.string.profile_small));
            mSignOutLayout.setVisibility(View.VISIBLE);
            mReachoutLayout.setVisibility(View.GONE);
            //set Reach out details to new user,
            mNewUsrReachoutImg.setBackground(getResources().getDrawable(R.drawable.reachout_menu1));
            mNewUsrReachoutLabel.setText(getResources().getString(R.string.side_menu_reach_out));
            mSignUpLabel.setVisibility(View.GONE);
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

        menulayout = (LinearLayout) findViewById(R.id.menulayout);


        mHomeLayout = (LinearLayout) findViewById(R.id.mHomeLayout);
        mHomeLabel = (AppCompatTextView) findViewById(R.id.mHomeLabel);
        mResourcesLayout = (LinearLayout) findViewById(R.id.mResourcesLayout);
        mResourceLabel = (AppCompatTextView) findViewById(R.id.mResourceLabel);
        mReachoutLayout = (LinearLayout) findViewById(R.id.mReachoutLayout);
        mReachoutLabel = (AppCompatTextView) findViewById(R.id.mReachoutLabel);
        mSignInProfileLayout = (LinearLayout) findViewById(R.id.mSignInProfileLayout);
        mSigninImg = (AppCompatImageView) findViewById(R.id.signinImg);
        mSigninLabel = (AppCompatTextView) findViewById(R.id.mSigninLabel);
        mNewUsrReachoutLayout = (LinearLayout) findViewById(R.id.mNewUsrReachoutLayout);
        mNewUsrReachoutImg = (AppCompatImageView) findViewById(R.id.mNewUsrReachoutImg);
        mNewUsrReachoutLabel = (AppCompatTextView) findViewById(R.id.mNewUsrReachoutLabel);
        mSignUpLabel = (AppCompatTextView) findViewById(R.id.mSignUpLabel);
        mSignOutLayout = (RelativeLayout) findViewById(R.id.mSignOutLayout);
        mSignOutLabel = (AppCompatTextView) findViewById(R.id.mSignOutLabel);
        menutitle = findViewById(R.id.menutitle);


        mHomeLabel.setTypeface(AppController.getTypeface(this, "medium"));
        mResourceLabel.setTypeface(AppController.getTypeface(this, "medium"));
        mReachoutLabel.setTypeface(AppController.getTypeface(this, "medium"));
        mSigninLabel.setTypeface(AppController.getTypeface(this, "medium"));
        mNewUsrReachoutLabel.setTypeface(AppController.getTypeface(this, "medium"));
        mSignUpLabel.setTypeface(AppController.getTypeface(this, "medium"));
        mSignOutLabel.setTypeface(AppController.getTypeface(this, "medium"));


        RelativeLayout menu = findViewById(R.id.menu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDrawer();
            }
        });

        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setVisibility(View.GONE);

        mHomeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menulayout.setVisibility(View.VISIBLE);
                mToolbar.setVisibility(View.GONE);
                closeDrawer();
                if (mPreviousValue != R.id.mHomeLayout) {
                    mPreviousValue = R.id.mHomeLayout;
                    defaultFragementSettings();
                }
            }
        });
        mResourcesLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menulayout.setVisibility(View.GONE);
                mToolbar.setVisibility(View.VISIBLE);
                menutitle.setText(R.string.resources);
                closeDrawer();
                if (mPreviousValue != R.id.mResourcesLayout) {
                    mPreviousValue = R.id.mResourcesLayout;
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutContainer, new ResourcesFragment(), "fragment").commit();
                }
            }
        });
        mSignInProfileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menulayout.setVisibility(View.GONE);
                mToolbar.setVisibility(View.VISIBLE);

                closeDrawer();
                if (mPreviousValue != R.id.mSignInProfileLayout) {
                    mPreviousValue = R.id.mSignInProfileLayout;
                    if (AppController.getHelperSharedPreference().readPreference(SurveyActivity.this, getString(R.string.userid), "").equalsIgnoreCase("")) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutContainer, new SignInFragment(), "fragment").commit();
                        menutitle.setText(R.string.sign_in);
                    } else {
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutContainer, new ProfileFragment(), "fragment").commit();
                        menutitle.setText(R.string.profile);
                    }
                }
            }
        });
        mNewUsrReachoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menulayout.setVisibility(View.GONE);
                mToolbar.setVisibility(View.VISIBLE);
                closeDrawer();
                if (mPreviousValue != R.id.mNewUsrReachoutLayout) {
                    mPreviousValue = R.id.mNewUsrReachoutLayout;
                    if (AppController.getHelperSharedPreference().readPreference(SurveyActivity.this, getString(R.string.userid), "").equalsIgnoreCase("")) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutContainer, new SignupFragment(), "fragment").commit();
                        menutitle.setText(R.string.sign_up);
                    } else {
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutContainer, new ReachoutFragment(), "fragment").commit();
                        menutitle.setText(R.string.reachout);
                    }
                }
            }
        });
        mSignOutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeDrawer();
                if (mPreviousValue != R.id.mSignOutLayout) {
                    mPreviousValue = R.id.mSignOutLayout;
                    logout();
                }
            }
        });


        TextView version = (TextView) findViewById(R.id.version);
        setVersion(version);
    }


    private void logout() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this, R.style.MyAlertDialogStyle);

        alertDialogBuilder.setTitle(getResources().getString(R.string.sign_out));
        String message;
        if (checkOfflineDataEmpty()) {
            message = getResources().getString(R.string.sign_out_message);
        } else {
            message = getResources().getString(R.string.sign_out_message_data_lost);
        }

        alertDialogBuilder.setMessage(message).setCancelable(false)
                .setPositiveButton(getResources().getString(R.string.sign_out), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        AppController.getHelperProgressDialog().showProgress(SurveyActivity.this, "", "", false);
                        LogoutEvent logoutEvent = new LogoutEvent();
                        HashMap<String, String> params = new HashMap<>();
                        params.put("reason", "user_action");
                        HashMap<String, String> header = new HashMap<String, String>();
                        header.put("userId", AppController.getHelperSharedPreference().readPreference(SurveyActivity.this, getString(R.string.userid), ""));
                        header.put("auth", AppController.getHelperSharedPreference().readPreference(SurveyActivity.this, getString(R.string.auth), ""));
                        RegistrationServerConfigEvent registrationServerConfigEvent = new RegistrationServerConfigEvent("delete", URLs.LOGOUT, LOGOUT_REPSONSECODE, SurveyActivity.this, LoginData.class, params, header, null, false, SurveyActivity.this);
                        logoutEvent.setmRegistrationServerConfigEvent(registrationServerConfigEvent);
                        UserModulePresenter userModulePresenter = new UserModulePresenter();
                        userModulePresenter.performLogout(logoutEvent);
                    }
                });

        alertDialogBuilder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();

    }

    private boolean checkOfflineDataEmpty() {
        RealmResults<OfflineData> results = dbServiceSubscriber.getOfflineData(mRealm);
        if (results == null || results.size() == 0) {
            return true;
        } else {
            return false;
        }
    }

    public void openDrawer() {
        mDrawer.openDrawer(GravityCompat.START);
    }

    private void closeDrawer() {
        mDrawer.closeDrawer(GravityCompat.START);
    }

    public void setVersion(TextView version) {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_META_DATA);
            version.append("" + pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            version.setText("");
        }
    }

    private void bindEvents() {
        myDashboardButtonLayout.setOnClickListener(this);
        mActivitiesButtonLayout.setOnClickListener(this);
        mResourcesButtonLayout.setOnClickListener(this);
    }

    private void defaultFragementSettings() {
        menulayout.setVisibility(View.VISIBLE);
        mToolbar.setVisibility(View.GONE);

        myDashboardButton.setBackgroundResource(R.drawable.dashboard_grey);
        mActivitiesButton.setBackgroundResource(R.drawable.activities_blue_active);
        mResourcesButton.setBackgroundResource(R.drawable.resources_grey);
        myDashboardButtonLabel.setTextColor(getResources().getColor(R.color.colorPrimaryBlack));
        mActivitiesButtonLabel.setTextColor(getResources().getColor(R.color.colorPrimary));
        mResourcesButtonLabel.setTextColor(getResources().getColor(R.color.colorPrimaryBlack));
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutContainer, mSurveyActivitiesFragment, "fragment").commit();
    }

    private void openResources() {
        myDashboardButton.setBackgroundResource(R.drawable.dashboard_grey);
        mActivitiesButton.setBackgroundResource(R.drawable.activities_grey);
        mResourcesButton.setBackgroundResource(R.drawable.resources_blue_active);
        myDashboardButtonLabel.setTextColor(getResources().getColor(R.color.colorPrimaryBlack));
        mActivitiesButtonLabel.setTextColor(getResources().getColor(R.color.colorPrimaryBlack));
        mResourcesButtonLabel.setTextColor(getResources().getColor(R.color.colorPrimary));
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutContainer, mSurveyResourcesFragment, "fragment").commit();
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
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutContainer, mSurveyDashboardFragment, "fragment").commit();
                break;

            case R.id.mActivitiesButtonLayout:
                myDashboardButton.setBackgroundResource(R.drawable.dashboard_grey);
                mActivitiesButton.setBackgroundResource(R.drawable.activities_blue_active);
                mResourcesButton.setBackgroundResource(R.drawable.resources_grey);
                myDashboardButtonLabel.setTextColor(getResources().getColor(R.color.colorPrimaryBlack));
                mActivitiesButtonLabel.setTextColor(getResources().getColor(R.color.colorPrimary));
                mResourcesButtonLabel.setTextColor(getResources().getColor(R.color.colorPrimaryBlack));
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutContainer, mSurveyActivitiesFragment, "fragment").commit();
                break;

            case R.id.mResourcesButtonLayout:
                myDashboardButton.setBackgroundResource(R.drawable.dashboard_grey);
                mActivitiesButton.setBackgroundResource(R.drawable.activities_grey);
                mResourcesButton.setBackgroundResource(R.drawable.resources_blue_active);
                myDashboardButtonLabel.setTextColor(getResources().getColor(R.color.colorPrimaryBlack));
                mActivitiesButtonLabel.setTextColor(getResources().getColor(R.color.colorPrimaryBlack));
                mResourcesButtonLabel.setTextColor(getResources().getColor(R.color.colorPrimary));
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutContainer, mSurveyResourcesFragment, "fragment").commit();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (AppConfig.AppType.equalsIgnoreCase(getString(R.string.app_gateway))) {
            Intent intent = new Intent(SurveyActivity.this, StudyActivity.class);
            ComponentName cn = intent.getComponent();
            Intent mainIntent = Intent.makeRestartActivityTask(cn);
            startActivity(mainIntent);
            finish();
        } else if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else if (mPreviousValue != R.id.mHomeLayout) {
            mPreviousValue = R.id.mHomeLayout;
            defaultFragementSettings();
        } else {
            if (isExit) {
                finish();
            } else {
                Toast.makeText(this, R.string.press_back_to_exit, Toast.LENGTH_SHORT).show();
                isExit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isExit = false;
                    }
                }, 3000);
            }
        }
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
        } else if (requestCode == 2000) {
            if (mSurveyDashboardFragment != null)
                mSurveyDashboardFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
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

    public String getEnroll() {
        return enroll;
    }

    public String getRejoin() {
        return rejoin;
    }

    @Override
    public <T> void asyncResponse(T response, int responseCode) {
        if (responseCode == LOGOUT_REPSONSECODE) {
            Toast.makeText(this, getResources().getString(R.string.signOut), Toast.LENGTH_SHORT).show();
            SharedPreferences settings = SharedPreferenceHelper.getPreferences(SurveyActivity.this);
            settings.edit().clear().apply();
            // delete passcode from keystore
            String pass = AppController.refreshKeys("passcode");
            if (pass != null)
                AppController.deleteKey("passcode_" + pass);

            try {
                NotificationModuleSubscriber notificationModuleSubscriber = new NotificationModuleSubscriber(dbServiceSubscriber, mRealm);
                notificationModuleSubscriber.cancleActivityLocalNotification(SurveyActivity.this);
                notificationModuleSubscriber.cancleResourcesLocalNotification(SurveyActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //Call AsyncTask
            new ClearNotification().execute();

        }
    }

    @Override
    public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {

    }


    private class ClearNotification extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                NotificationModuleSubscriber notificationModuleSubscriber = new NotificationModuleSubscriber(dbServiceSubscriber, mRealm);
                notificationModuleSubscriber.cancleActivityLocalNotification(SurveyActivity.this);
                notificationModuleSubscriber.cancleResourcesLocalNotification(SurveyActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            dbServiceSubscriber.deleteDb(SurveyActivity.this);


            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            AppController.getHelperProgressDialog().dismissDialog();
            // clear notifications from notification tray
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(SurveyActivity.this);
            notificationManager.cancelAll();


            signout();
        }

        @Override
        protected void onPreExecute() {
            AppController.getHelperProgressDialog().showProgress(SurveyActivity.this, "", "", false);
        }
    }

    public void signout() {
        Intent intent = new Intent(SurveyActivity.this, StandaloneStudyInfoActivity.class);
        ComponentName cn = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(cn);
        startActivity(mainIntent);
        finish();
    }
}
