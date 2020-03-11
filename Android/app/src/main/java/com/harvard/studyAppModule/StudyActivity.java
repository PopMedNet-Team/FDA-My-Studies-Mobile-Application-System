package com.harvard.studyAppModule;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.harvard.AppConfig;
import com.harvard.MyFirebaseMessagingService;
import com.harvard.R;
import com.harvard.notificationModule.AlarmReceiver;
import com.harvard.notificationModule.NotificationModuleSubscriber;
import com.harvard.offlineModule.model.OfflineData;
import com.harvard.storageModule.DBServiceSubscriber;
import com.harvard.studyAppModule.studyModel.Study;
import com.harvard.studyAppModule.studyModel.StudyList;
import com.harvard.userModule.UserModulePresenter;
import com.harvard.userModule.event.LogoutEvent;
import com.harvard.userModule.webserviceModel.LoginData;
import com.harvard.utils.AppController;
import com.harvard.utils.SetDialogHelper;
import com.harvard.utils.SharedPreferenceHelper;
import com.harvard.utils.URLs;
import com.harvard.webserviceModule.apiHelper.ApiCall;
import com.harvard.webserviceModule.events.RegistrationServerConfigEvent;

import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class StudyActivity extends AppCompatActivity implements View.OnClickListener, ApiCall.OnAsyncRequestComplete {

    private static final int NOTIFICATION_RESULT = 112;
    private Toolbar mToolbar;
    private RelativeLayout mBackBtn;
    private RelativeLayout mNotificationBtn;
    private RelativeLayout mInfoIcon;
    private RelativeLayout mFilter;
    private RelativeLayout mSearchBtn;
    private RelativeLayout mEditBtnLayout;
    private DrawerLayout mDrawer;
    ActionBarDrawerToggle mToggle;
    private AppCompatTextView mTitleFDAListens;
    private AppCompatTextView mTitle;
    private AppCompatTextView mSidebarTitle;
    private LinearLayout mHomeLayout;
    private AppCompatTextView mHomeLabel;
    private LinearLayout mResourcesLayout;
    private AppCompatTextView mResourceLabel;
    private LinearLayout mReachoutLayout;
    private AppCompatTextView mReachoutLabel;
    private LinearLayout mSignInProfileLayout;
    private AppCompatImageView mSigninImg;
    private AppCompatTextView mSigninLabel;
    private LinearLayout mNewUsrReachoutLayout;
    private AppCompatImageView mNewUsrReachoutImg;
    private AppCompatImageView mNotificationIcon;
    private AppCompatImageView mNotificatioStatus;
    private AppCompatTextView mNewUsrReachoutLabel;
    private AppCompatTextView mSignUpLabel;
    private RelativeLayout mSignOutLayout;
    private AppCompatTextView mSignOutLabel;
    private int mPreviousValue = 0;// 0 means signup 1 means signout
    private final int LOGOUT_REPSONSECODE = 100;
    private AppCompatTextView mEditTxt;
    private ProfileFragment mProfileFragment;
    private boolean isExit = false;
    public static int GO_TO_SIGNIN = 11;
    TextView version;
    StudyFragment mStudyFragment;
    private boolean mClicked;
    public static String FROM = "from";
    DBServiceSubscriber dbServiceSubscriber;
    Realm mRealm;
    RelativeLayout mSearchToolBarLayout;
    RelativeLayout mToolBarLayout;
    AppCompatTextView mCancel;
    AppCompatEditText mSearchEditText;
    RelativeLayout mClearLayout;
    private String intentFrom = "";
    private BroadcastReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (AppConfig.AppType.equalsIgnoreCase(getString(R.string.app_gateway))) {
            isExit = false;
            mClicked = false;
            setContentView(R.layout.activity_study);
            if (getIntent().getStringExtra(FROM) != null) {
                intentFrom = getIntent().getStringExtra(FROM);
            } else {
                intentFrom = "";
            }
            dbServiceSubscriber = new DBServiceSubscriber();
            mRealm = AppController.getRealmobj(this);
            initializeXMLId();
            bindEvents();
            setFont();
            // default settings
            loadstudylist();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AppConfig.AppType.equalsIgnoreCase(getString(R.string.app_standalone))) {
            Intent intent = new Intent(StudyActivity.this, StandaloneActivity.class);
            ComponentName cn = intent.getComponent();
            Intent mainIntent = Intent.makeRestartActivityTask(cn);
            startActivity(mainIntent);
            finish();
        } else {
            try {
                if (AppController.getHelperSharedPreference().readPreference(this, getString(R.string.notification), "").equalsIgnoreCase("true")) {
                    mNotificationIcon.setImageResource(R.drawable.notification_white_active);
                    mNotificatioStatus.setVisibility(View.VISIBLE);
                } else {
                    mNotificationIcon.setImageResource(R.drawable.notification_white_active);
                    mNotificatioStatus.setVisibility(View.GONE);
                }

                IntentFilter intentFilter = new IntentFilter("com.fda.notificationReceived");
                mReceiver = new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        mNotificationIcon.setImageResource(R.drawable.notification_white_active);
                        mNotificatioStatus.setVisibility(View.VISIBLE);
                    }
                };
                //registering our receiver
                this.registerReceiver(mReceiver, intentFilter);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        try {
            this.unregisterReceiver(this.mReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent1) {
        super.onNewIntent(intent1);
        setIntent(intent1);
        if (intent1.getStringExtra(FROM) != null) {
            intentFrom = intent1.getStringExtra(FROM);
        } else {
            intentFrom = "";
        }

    }

    public void checkForNotification(Intent intent1) {
        if (!intentFrom.equalsIgnoreCase("")) {
            intentFrom = "";
            String type = intent1.getStringExtra(MyFirebaseMessagingService.TYPE);
            String subType = intent1.getStringExtra(MyFirebaseMessagingService.SUBTYPE);
            String studyId = intent1.getStringExtra(MyFirebaseMessagingService.STUDYID);
            String audience = intent1.getStringExtra(MyFirebaseMessagingService.AUDIENCE);

            String localNotification = "";
            if (intent1.getStringExtra(AlarmReceiver.LOCAL_NOTIFICATION) != null) {
                localNotification = intent1.getStringExtra(AlarmReceiver.LOCAL_NOTIFICATION);
            }
            String activityIdNotification = "";
            if (intent1.getStringExtra(AlarmReceiver.ACTIVITYID) != null) {
                activityIdNotification = intent1.getStringExtra(AlarmReceiver.ACTIVITYID);
            }


            if (!AppController.getHelperSharedPreference().readPreference(StudyActivity.this, getResources().getString(R.string.userid), "").equalsIgnoreCase("")) {
                if (type != null) {
                    if (type.equalsIgnoreCase("Gateway")) {
                        if (subType.equalsIgnoreCase("Study")) {
                            Study mStudy = dbServiceSubscriber.getStudyListFromDB(mRealm);
                            if (mStudy != null) {
                                RealmList<StudyList> studyListArrayList = mStudy.getStudies();
                                studyListArrayList = dbServiceSubscriber.saveStudyStatusToStudyList(studyListArrayList, mRealm);
                                boolean isStudyAvailable = false;
                                for (int i = 0; i < studyListArrayList.size(); i++) {
                                    if (studyId.equalsIgnoreCase(studyListArrayList.get(i).getStudyId())) {
                                        try {
                                            AppController.getHelperSharedPreference().writePreference(StudyActivity.this, getString(R.string.title), "" + studyListArrayList.get(i).getTitle());
                                            AppController.getHelperSharedPreference().writePreference(StudyActivity.this, getString(R.string.bookmark), "" + studyListArrayList.get(i).isBookmarked());
                                            AppController.getHelperSharedPreference().writePreference(StudyActivity.this, getString(R.string.status), "" + studyListArrayList.get(i).getStatus());
                                            AppController.getHelperSharedPreference().writePreference(StudyActivity.this, getString(R.string.studyStatus), "" + studyListArrayList.get(i).getStudyStatus());
                                            AppController.getHelperSharedPreference().writePreference(StudyActivity.this, getString(R.string.position), "" + i);
                                            AppController.getHelperSharedPreference().writePreference(StudyActivity.this, getString(R.string.enroll), "" + studyListArrayList.get(i).getSetting().isEnrolling());
                                            AppController.getHelperSharedPreference().writePreference(StudyActivity.this, getString(R.string.rejoin), "" + studyListArrayList.get(i).getSetting().getRejoin());
                                            AppController.getHelperSharedPreference().writePreference(StudyActivity.this, getString(R.string.studyVersion), "" + studyListArrayList.get(i).getStudyVersion());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        if (studyListArrayList.get(i).getStatus().equalsIgnoreCase(getString(R.string.active)) && studyListArrayList.get(i).getStudyStatus().equalsIgnoreCase(StudyFragment.IN_PROGRESS)) {
                                            Intent intent = new Intent(StudyActivity.this, SurveyActivity.class);
                                            intent.putExtra("studyId", studyId);
                                            startActivity(intent);
                                        } else if (studyListArrayList.get(i).getStatus().equalsIgnoreCase(getString(R.string.paused))) {
                                            Toast.makeText(StudyActivity.this, R.string.study_paused, Toast.LENGTH_SHORT).show();
                                        } else if (studyListArrayList.get(i).getStatus().equalsIgnoreCase(getString(R.string.closed))) {
                                            Toast.makeText(StudyActivity.this, R.string.study_resume, Toast.LENGTH_SHORT).show();
                                        } else {
                                            Intent intent = new Intent(getApplicationContext(), StudyInfoActivity.class);
                                            intent.putExtra("studyId", studyListArrayList.get(i).getStudyId());
                                            intent.putExtra("title", studyListArrayList.get(i).getTitle());
                                            intent.putExtra("bookmark", studyListArrayList.get(i).isBookmarked());
                                            intent.putExtra("status", studyListArrayList.get(i).getStatus());
                                            intent.putExtra("studyStatus", studyListArrayList.get(i).getStudyStatus());
                                            intent.putExtra("position", "" + i);
                                            intent.putExtra("enroll", "" + studyListArrayList.get(i).getSetting().isEnrolling());
                                            intent.putExtra("rejoin", "" + studyListArrayList.get(i).getSetting().getRejoin());
                                            startActivity(intent);
                                        }
                                        isStudyAvailable = true;
                                        break;
                                    }
                                }
                                if (!isStudyAvailable) {
                                    Toast.makeText(StudyActivity.this, R.string.studyNotAvailable, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(StudyActivity.this, R.string.studyNotAvailable, Toast.LENGTH_SHORT).show();
                            }
                        } else if (subType.equalsIgnoreCase("Resource")) {
                            mPreviousValue = R.id.mResourcesLayout;
                            mTitleFDAListens.setText("");
                            mTitle.setText(getResources().getString(R.string.resources));
                            mEditBtnLayout.setVisibility(View.GONE);
                            mNotificationBtn.setVisibility(View.GONE);
                            mFilter.setVisibility(View.GONE);
                            mSearchBtn.setVisibility(View.GONE);
                            closeDrawer();
                            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutContainer, new ResourcesFragment(), "fragment").commit();
                        }
                    } else if (type.equalsIgnoreCase("Study")) {
                        if (subType.equalsIgnoreCase("Activity") || subType.equalsIgnoreCase("Resource")) {
                            Study mStudy = dbServiceSubscriber.getStudyListFromDB(mRealm);
                            if (mStudy != null) {
                                RealmList<StudyList> studyListArrayList = mStudy.getStudies();
                                studyListArrayList = dbServiceSubscriber.saveStudyStatusToStudyList(studyListArrayList, mRealm);
                                boolean isStudyAvailable = false;
                                boolean isStudyJoined = false;
                                for (int i = 0; i < studyListArrayList.size(); i++) {
                                    if (studyId.equalsIgnoreCase(studyListArrayList.get(i).getStudyId())) {
                                        isStudyAvailable = true;
                                        try {
                                            AppController.getHelperSharedPreference().writePreference(StudyActivity.this, getString(R.string.title), "" + studyListArrayList.get(i).getTitle());
                                            AppController.getHelperSharedPreference().writePreference(StudyActivity.this, getString(R.string.bookmark), "" + studyListArrayList.get(i).isBookmarked());
                                            AppController.getHelperSharedPreference().writePreference(StudyActivity.this, getString(R.string.status), "" + studyListArrayList.get(i).getStatus());
                                            AppController.getHelperSharedPreference().writePreference(StudyActivity.this, getString(R.string.studyStatus), "" + studyListArrayList.get(i).getStudyStatus());
                                            AppController.getHelperSharedPreference().writePreference(StudyActivity.this, getString(R.string.position), "" + i);
                                            AppController.getHelperSharedPreference().writePreference(StudyActivity.this, getString(R.string.enroll), "" + studyListArrayList.get(i).getSetting().isEnrolling());
                                            AppController.getHelperSharedPreference().writePreference(StudyActivity.this, getString(R.string.rejoin), "" + studyListArrayList.get(i).getSetting().getRejoin());
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        if (studyListArrayList.get(i).getStatus().equalsIgnoreCase(getString(R.string.active)) && studyListArrayList.get(i).getStudyStatus().equalsIgnoreCase(StudyFragment.IN_PROGRESS)) {
                                            if (subType.equalsIgnoreCase("Resource")) {
                                                mStudyFragment.getStudyUpdate(studyListArrayList.get(i).getStudyId(), studyListArrayList.get(i).getStudyVersion(), studyListArrayList.get(i).getTitle(), "Resource", "NotificationActivity", activityIdNotification, localNotification);
                                            } else {
                                                mStudyFragment.getStudyUpdate(studyListArrayList.get(i).getStudyId(), studyListArrayList.get(i).getStudyVersion(), studyListArrayList.get(i).getTitle(), "", "NotificationActivity", activityIdNotification, localNotification);
                                            }
                                            isStudyJoined = true;
                                            break;
                                        } else {
                                            isStudyJoined = false;
                                            break;
                                        }
                                    }
                                }
                                if (!isStudyAvailable) {
                                    Toast.makeText(StudyActivity.this, R.string.studyNotAvailable, Toast.LENGTH_SHORT).show();
                                } else if (!isStudyJoined) {
                                    Toast.makeText(StudyActivity.this, R.string.studyNotJoined, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(StudyActivity.this, R.string.studyNotAvailable, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(StudyActivity.this, R.string.studyNotAvailable, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initializeXMLId() {
        mInfoIcon = (RelativeLayout) findViewById(R.id.mInfoIcon);
        mEditTxt = (AppCompatTextView) findViewById(R.id.editBtnLabel);
        mEditTxt.setVisibility(View.GONE);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mBackBtn = (RelativeLayout) findViewById(R.id.backBtn);
        mTitleFDAListens = (AppCompatTextView) findViewById(R.id.mTitleFDAListens);
        mTitle = (AppCompatTextView) findViewById(R.id.mTitle);
        mSidebarTitle = (AppCompatTextView) findViewById(R.id.mSidebarTitle);
        mNotificationBtn = (RelativeLayout) findViewById(R.id.mNotificationBtn);
        mInfoIcon = (RelativeLayout) findViewById(R.id.mInfoIcon);
        mEditBtnLayout = (RelativeLayout) findViewById(R.id.editBtnLayout);
        mDrawer = (DrawerLayout) findViewById(R.id.activity_study);
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
        mNotificationIcon = (AppCompatImageView) findViewById(R.id.mNotificationIcon);
        mNotificatioStatus = (AppCompatImageView) findViewById(R.id.notificatioStatus);
        mNewUsrReachoutLabel = (AppCompatTextView) findViewById(R.id.mNewUsrReachoutLabel);
        mSignUpLabel = (AppCompatTextView) findViewById(R.id.mSignUpLabel);
        mSignOutLayout = (RelativeLayout) findViewById(R.id.mSignOutLayout);
        mSignOutLabel = (AppCompatTextView) findViewById(R.id.mSignOutLabel);
        mFilter = (RelativeLayout) findViewById(R.id.mFilter);
        mSearchBtn = (RelativeLayout) findViewById(R.id.mSearchBtn);
        mSearchToolBarLayout = (RelativeLayout) findViewById(R.id.mSearchToolBarLayout);
        mToolBarLayout = (RelativeLayout) findViewById(R.id.mToolBarLayout);
        mCancel = (AppCompatTextView) findViewById(R.id.mCancel);
        mClearLayout = (RelativeLayout) findViewById(R.id.mClearLayout);
        mSearchEditText = (AppCompatEditText) findViewById(R.id.mSearchEditText);

        version = (TextView) findViewById(R.id.version);
        setVersion(version);

        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkSignOrSignOutScenario();
                openDrawer();
                try {
                    AppController.getHelperHideKeyboard(StudyActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        mFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StudyActivity.this, FilterActivity.class);
                startActivityForResult(intent, 999);

            }
        });
        mSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mToolBarLayout.setVisibility(View.GONE);
                mSearchToolBarLayout.setVisibility(View.VISIBLE);
                mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
                mSearchEditText.setText("");
                // forcecfully set focus
                mSearchEditText.post(new Runnable() {
                    @Override
                    public void run() {
                        mSearchEditText.requestFocus();
                        try {
                            InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
                            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        mSearchEditText.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() > 0) {
                    mClearLayout.setVisibility(View.VISIBLE);
                } else {
                    mClearLayout.setVisibility(View.INVISIBLE);
                    mStudyFragment.setStudyFilteredStudyList();
                }
            }
        });

        mSearchEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    if (mSearchEditText.getText().length() > 0) {
                        mStudyFragment.searchFromFilteredStudyList(mSearchEditText.getText().toString().trim());
                        hideKeyboard();
                    } else {
                        Toast.makeText(StudyActivity.this, getResources().getString(R.string.please_enter_key), Toast.LENGTH_LONG).show();
                    }
                    return true;
                }
                return false;
            }
        });

        mClearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSearchEditText.setText("");
                mClearLayout.setVisibility(View.INVISIBLE);
                mStudyFragment.setStudyFilteredStudyList();
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mSearchEditText.setText("");
                setToolBarEnable();
                hideKeyboard();
                mStudyFragment.setStudyFilteredStudyList();

            }
        });


        mNotificationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StudyActivity.this, NotificationActivity.class);
                startActivityForResult(intent, NOTIFICATION_RESULT);

            }
        });
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

        mInfoIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetDialogHelper.setNeutralDialog(StudyActivity.this, getResources().getString(R.string.registration_message), false, getResources().getString(R.string.ok), getResources().getString(R.string.why_register));
            }
        });

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

    private void hideKeyboard() {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setToolBarEnable() {
        mSearchToolBarLayout.setVisibility(View.GONE);
        mToolBarLayout.setVisibility(View.VISIBLE);
        mDrawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    private void setFont() {
        try {
            mEditTxt.setTypeface(AppController.getTypeface(this, "medium"));
            mTitleFDAListens.setTypeface(AppController.getTypeface(this, "bold"));
            mTitle.setTypeface(AppController.getTypeface(this, "bold"));
            mSidebarTitle.setTypeface(AppController.getTypeface(this, "medium"));
            mHomeLabel.setTypeface(AppController.getTypeface(this, "medium"));
            mResourceLabel.setTypeface(AppController.getTypeface(this, "medium"));
            mReachoutLabel.setTypeface(AppController.getTypeface(this, "medium"));
            mSigninLabel.setTypeface(AppController.getTypeface(this, "medium"));
            mNewUsrReachoutLabel.setTypeface(AppController.getTypeface(this, "medium"));
            mSignUpLabel.setTypeface(AppController.getTypeface(this, "medium"));
            mSignOutLabel.setTypeface(AppController.getTypeface(this, "medium"));
            mSearchEditText.setTypeface(AppController.getTypeface(this, "regular"));
            mCancel.setTypeface(AppController.getTypeface(this, "medium"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void bindEvents() {
        mHomeLayout.setOnClickListener(this);
        mResourcesLayout.setOnClickListener(this);
        mReachoutLayout.setOnClickListener(this);
        mSignInProfileLayout.setOnClickListener(this);
        mNewUsrReachoutLayout.setOnClickListener(this);
        mSignOutLayout.setOnClickListener(this);
    }

    private void checkSignOrSignOutScenario() {
        //signIn
        if (AppController.getHelperSharedPreference().readPreference(StudyActivity.this, getString(R.string.userid), "").equalsIgnoreCase("")) {
            mSigninImg.setBackground(getResources().getDrawable(R.drawable.signin_menu1));
            mSigninLabel.setText(getResources().getString(R.string.sign_in_btn));
            mSignOutLayout.setVisibility(View.GONE);
            mReachoutLayout.setVisibility(View.VISIBLE);
            //set Reach out details to new user,
            mNewUsrReachoutImg.setBackground(getResources().getDrawable(R.drawable.newuser_menu1));
            mNewUsrReachoutLabel.setText(getResources().getString(R.string.side_menu_new_user));
            mSignUpLabel.setVisibility(View.VISIBLE);
            mNotificationIcon.setImageResource(R.drawable.notification_white_active);
            mNotificatioStatus.setVisibility(View.GONE);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.mHomeLayout:
                mPreviousValue = R.id.mHomeLayout;
                mTitleFDAListens.setText(getResources().getString(R.string.fda_listens));
                mTitle.setText("");
                mEditBtnLayout.setVisibility(View.GONE);
                mNotificationBtn.setVisibility(View.VISIBLE);
                mFilter.setVisibility(View.VISIBLE);
                mSearchBtn.setVisibility(View.VISIBLE);
                mInfoIcon.setVisibility(View.GONE);

                try {
                    if (AppController.getHelperSharedPreference().readPreference(this, getString(R.string.notification), "").equalsIgnoreCase("true")) {
                        mNotificationIcon.setImageResource(R.drawable.notification_white_active);
                        mNotificatioStatus.setVisibility(View.VISIBLE);
                    } else {
                        mNotificationIcon.setImageResource(R.drawable.notification_white_active);
                        mNotificatioStatus.setVisibility(View.GONE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                closeDrawer();
                mStudyFragment = new StudyFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutContainer, mStudyFragment, "fragment").commit();
                break;

            case R.id.mResourcesLayout:
                if (mPreviousValue == R.id.mResourcesLayout) {
                    closeDrawer();
                } else {
                    mPreviousValue = R.id.mResourcesLayout;
                    mTitleFDAListens.setText("");
                    mTitle.setText(getResources().getString(R.string.resources));
                    mEditBtnLayout.setVisibility(View.GONE);
                    mNotificationBtn.setVisibility(View.GONE);
                    mFilter.setVisibility(View.GONE);
                    mSearchBtn.setVisibility(View.GONE);
                    closeDrawer();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutContainer, new ResourcesFragment(), "fragment").commit();
                }
                break;

            case R.id.mReachoutLayout:
                reachoutMenuClicked();
                break;

            case R.id.mSignInProfileLayout:
                if (AppController.getHelperSharedPreference().readPreference(StudyActivity.this, getString(R.string.userid), "").equalsIgnoreCase("")) {
                    if (mPreviousValue == R.id.mSignInProfileLayout) {
                        closeDrawer();
                    } else {
                        mPreviousValue = R.id.mSignInProfileLayout;
                        mTitleFDAListens.setText("");
                        mTitle.setText(getResources().getString(R.string.sign_in));
                        mEditBtnLayout.setVisibility(View.GONE);
                        mNotificationBtn.setVisibility(View.GONE);
                        mFilter.setVisibility(View.GONE);
                        mSearchBtn.setVisibility(View.GONE);
                        mInfoIcon.setVisibility(View.VISIBLE);
                        closeDrawer();
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutContainer, new SignInFragment(), "fragment").commit();
                    }
                } else {
                    if (mPreviousValue == R.id.mSignInProfileLayout) {
                        closeDrawer();
                    } else {
                        mPreviousValue = R.id.mSignInProfileLayout;
                        mTitleFDAListens.setText("");
                        mTitle.setText(getResources().getString(R.string.profile));
                        mEditBtnLayout.setVisibility(View.VISIBLE);
                        mNotificationBtn.setVisibility(View.GONE);
                        mFilter.setVisibility(View.GONE);
                        mSearchBtn.setVisibility(View.GONE);
                        mInfoIcon.setVisibility(View.GONE);
                        mProfileFragment = new ProfileFragment();

                        mEditBtnLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (mEditTxt.getText().toString().equalsIgnoreCase(getResources().getString(R.string.edit)))
                                    enableEditText();
                                else if (mEditTxt.getText().toString().equalsIgnoreCase(getResources().getString(R.string.cancel)))
                                    disableEditText();
                            }
                        });
                        closeDrawer();
                        mProfileFragment = new ProfileFragment();
                        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutContainer, mProfileFragment, "fragment").commit();
                    }
                }
                break;

            case R.id.mNewUsrReachoutLayout:
                mPreviousValue = R.id.mNewUsrReachoutLayout;
                if (AppController.getHelperSharedPreference().readPreference(StudyActivity.this, getString(R.string.userid), "").equalsIgnoreCase("")) {
                    mTitleFDAListens.setText("");
                    mTitle.setText(getResources().getString(R.string.signup));
                    mEditBtnLayout.setVisibility(View.GONE);
                    mNotificationBtn.setVisibility(View.GONE);
                    mFilter.setVisibility(View.GONE);
                    mSearchBtn.setVisibility(View.GONE);
                    mInfoIcon.setVisibility(View.VISIBLE);
                    closeDrawer();
                    getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutContainer, new SignupFragment(), "fragment").commit();
                } else {
                    // SignOut Reach out menu click
                    reachoutMenuClicked();
                }
                break;
            case R.id.mSignOutLayout:
                closeDrawer();
                logout();
                break;
        }
    }

    private boolean checkOfflineDataEmpty() {
        RealmResults<OfflineData> results = dbServiceSubscriber.getOfflineData(mRealm);
        if (results == null || results.size() == 0) {
            return true;
        } else {
            return false;
        }
    }

    private void reachoutMenuClicked() {
        if (mPreviousValue == R.id.mReachoutLayout) {
            closeDrawer();
        } else {
            mPreviousValue = R.id.mReachoutLayout;
            mTitleFDAListens.setText("");
            mTitle.setText(getResources().getString(R.string.reachout));
            mEditBtnLayout.setVisibility(View.GONE);
            mNotificationBtn.setVisibility(View.GONE);
            mFilter.setVisibility(View.GONE);
            mSearchBtn.setVisibility(View.GONE);
            closeDrawer();
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutContainer, new ReachoutFragment(), "fragment").commit();
        }
    }

    private void enableEditText() {
        mEditTxt.setText(getResources().getString(R.string.cancel));
        mProfileFragment.enableEditText();
    }

    private void disableEditText() {
        mEditTxt.setText(getResources().getString(R.string.edit));
        mProfileFragment.disableEditText();
    }

    public void disableEditTextFromFragment() {
        mEditTxt.setText(getResources().getString(R.string.edit));
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

                        AppController.getHelperProgressDialog().showProgress(StudyActivity.this, "", "", false);
                        LogoutEvent logoutEvent = new LogoutEvent();
                        HashMap<String, String> params = new HashMap<>();
                        params.put("reason", "user_action");
                        HashMap<String, String> header = new HashMap<String, String>();
                        header.put("userId", AppController.getHelperSharedPreference().readPreference(StudyActivity.this, getString(R.string.userid), ""));
                        header.put("auth", AppController.getHelperSharedPreference().readPreference(StudyActivity.this, getString(R.string.auth), ""));
                        RegistrationServerConfigEvent registrationServerConfigEvent = new RegistrationServerConfigEvent("delete", URLs.LOGOUT, LOGOUT_REPSONSECODE, StudyActivity.this, LoginData.class, params, header, null, false, StudyActivity.this);
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

    private void openDrawer() {
        mDrawer.openDrawer(GravityCompat.START);
    }

    private void closeDrawer() {
        mDrawer.closeDrawer(GravityCompat.START);
    }


    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
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

    @Override
    public <T> void asyncResponse(T response, int responseCode) {
        if (responseCode == LOGOUT_REPSONSECODE) {
            Toast.makeText(this, getResources().getString(R.string.signOut), Toast.LENGTH_SHORT).show();
            SharedPreferences settings = SharedPreferenceHelper.getPreferences(StudyActivity.this);
            settings.edit().clear().apply();
            // delete passcode from keystore
            String pass = AppController.refreshKeys("passcode");
            if (pass != null)
                AppController.deleteKey("passcode_" + pass);

            try {
                NotificationModuleSubscriber notificationModuleSubscriber = new NotificationModuleSubscriber(dbServiceSubscriber, mRealm);
                notificationModuleSubscriber.cancleActivityLocalNotification(StudyActivity.this);
                notificationModuleSubscriber.cancleResourcesLocalNotification(StudyActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            //Call AsyncTask
            new ClearNotification().execute();

        } else {
            AppController.getHelperProgressDialog().dismissDialog();
        }
    }

    @Override
    public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {
        AppController.getHelperProgressDialog().dismissDialog();
        if (statusCode.equalsIgnoreCase("401")) {
            Toast.makeText(this, errormsg, Toast.LENGTH_SHORT).show();
            AppController.getHelperSessionExpired(StudyActivity.this, errormsg);
        } else {
            Toast.makeText(this, errormsg, Toast.LENGTH_SHORT).show();
        }
    }

    public void loadstudylist() {
        checkSignOrSignOutScenario();
        mPreviousValue = R.id.mHomeLayout;
        mTitleFDAListens.setText(getResources().getString(R.string.fda_listens));
        mTitle.setText("");
        mEditBtnLayout.setVisibility(View.GONE);
        mNotificationBtn.setVisibility(View.VISIBLE);
        mFilter.setVisibility(View.VISIBLE);
        mSearchBtn.setVisibility(View.VISIBLE);
        mInfoIcon.setVisibility(View.GONE);
        closeDrawer();
        mStudyFragment = new StudyFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutContainer, mStudyFragment, "fragment").commit();
    }

    public void loadsignup() {
        checkSignOrSignOutScenario();
        mPreviousValue = R.id.mNewUsrReachoutLayout;
        mTitleFDAListens.setText("");
        mTitle.setText(getResources().getString(R.string.signup));
        mEditBtnLayout.setVisibility(View.GONE);
        mNotificationBtn.setVisibility(View.GONE);
        mFilter.setVisibility(View.GONE);
        mSearchBtn.setVisibility(View.GONE);
        closeDrawer();
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutContainer, new SignupFragment(), "fragment").commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                if (data != null && data.getStringExtra("action").equalsIgnoreCase("signin")) {
                    new LongOperation().execute();
                } else {
                    loadstudylist();
                }

            }
        } else if (requestCode == NOTIFICATION_RESULT) {
            if (resultCode == RESULT_OK) {
                mPreviousValue = R.id.mResourcesLayout;
                mTitleFDAListens.setText("");
                mTitle.setText(getResources().getString(R.string.resources));
                mEditBtnLayout.setVisibility(View.GONE);
                mNotificationBtn.setVisibility(View.GONE);
                mFilter.setVisibility(View.GONE);
                mSearchBtn.setVisibility(View.GONE);
                closeDrawer();
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutContainer, new ResourcesFragment(), "fragment").commit();
            }
        }
    }

    private class LongOperation extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.interrupted();
            }
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            AppController.getHelperProgressDialog().dismissDialog();
            mPreviousValue = R.id.mSignInProfileLayout;
            mTitleFDAListens.setText("");
            mTitle.setText(getResources().getString(R.string.sign_in));
            mEditBtnLayout.setVisibility(View.GONE);
            mNotificationBtn.setVisibility(View.GONE);
            mFilter.setVisibility(View.GONE);
            mSearchBtn.setVisibility(View.GONE);
            mInfoIcon.setVisibility(View.VISIBLE);
            closeDrawer();
            getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutContainer, new SignInFragment(), "fragment").commit();
        }

        @Override
        protected void onPreExecute() {
            AppController.getHelperProgressDialog().showProgress(StudyActivity.this, "", "", false);
        }
    }

    private class ClearNotification extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                NotificationModuleSubscriber notificationModuleSubscriber = new NotificationModuleSubscriber(dbServiceSubscriber, mRealm);
                notificationModuleSubscriber.cancleActivityLocalNotification(StudyActivity.this);
                notificationModuleSubscriber.cancleResourcesLocalNotification(StudyActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            dbServiceSubscriber.deleteDb(StudyActivity.this);


            return "";
        }

        @Override
        protected void onPostExecute(String result) {
            AppController.getHelperProgressDialog().dismissDialog();
            // clear notifications from notification tray
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(StudyActivity.this);
            notificationManager.cancelAll();


            loadstudylist();
        }

        @Override
        protected void onPreExecute() {
            AppController.getHelperProgressDialog().showProgress(StudyActivity.this, "", "", false);
        }
    }

    @Override
    protected void onDestroy() {
        if (dbServiceSubscriber != null && mRealm != null)
            dbServiceSubscriber.closeRealmObj(mRealm);
        super.onDestroy();
    }

    public String getSearchKey() {
        String key = null;
        try {
            if (mSearchEditText != null && mSearchEditText.getText().length() > 0) {
                key = mSearchEditText.getText().toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return key;
    }


}