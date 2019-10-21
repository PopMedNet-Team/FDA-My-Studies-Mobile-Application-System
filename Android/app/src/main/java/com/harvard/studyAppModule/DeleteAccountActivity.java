package com.harvard.studyAppModule;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.harvard.R;
import com.harvard.storageModule.DBServiceSubscriber;
import com.harvard.studyAppModule.events.DeleteAccountEvent;
import com.harvard.studyAppModule.events.GetUserStudyInfoEvent;
import com.harvard.studyAppModule.events.WithdrawFromStudyEvent;
import com.harvard.studyAppModule.studyModel.DeleteAccountData;
import com.harvard.studyAppModule.studyModel.StudyHome;
import com.harvard.studyAppModule.studyModel.StudyList;
import com.harvard.userModule.UserModulePresenter;
import com.harvard.userModule.webserviceModel.LoginData;
import com.harvard.userModule.webserviceModel.Studies;
import com.harvard.utils.AppController;
import com.harvard.utils.SharedPreferenceHelper;
import com.harvard.utils.URLs;
import com.harvard.webserviceModule.apiHelper.ApiCall;
import com.harvard.webserviceModule.apiHelper.ApiCallResponseServer;
import com.harvard.webserviceModule.events.RegistrationServerConfigEvent;
import com.harvard.webserviceModule.events.ResponseServerConfigEvent;
import com.harvard.webserviceModule.events.WCPConfigEvent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import io.realm.Realm;
import io.realm.RealmResults;

public class DeleteAccountActivity extends AppCompatActivity implements ApiCall.OnAsyncRequestComplete, ApiCallResponseServer.OnAsyncRequestComplete {
    private RelativeLayout mBackBtn;
    private AppCompatTextView mTitle;
    private View mHrLine;
    private AppCompatTextView mContent;
    private AppCompatTextView mIAgree;
    private AppCompatTextView mIDisagree;
    private LinearLayout middleLayaout;
    private static final int DELETE_ACCOUNT_REPSONSECODE = 101;
    private ArrayList<String> mStoreWithdrawalTypeDeleteFlag = new ArrayList<>();
    private ArrayList<String> mStudyIdList = new ArrayList<>();
    private ArrayList<String> mStudyTitleList = new ArrayList<>();
    private ArrayList<String> mWithdrawalTypeList = new ArrayList<>();
    private String mNoData = "nodata";
    private boolean mNoDataFlag = false;
    private int mTempPos;
    private static final int STUDY_INFO = 10;
    private StudyHome mStudyHome;
    private static final int WITHDRAWFROMSTUDY = 105;
    RealmResults<Studies> mRealmStudie;
    DBServiceSubscriber mDBServiceSubscriber;
    Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_account);
        mDBServiceSubscriber = new DBServiceSubscriber();
        mRealm = AppController.getRealmobj(this);
        initializeXMLId();
        setTextForView();
        setFont();
        bindEvents();
        checkAndCreateDataList();
    }

    private void initializeXMLId() {
        mBackBtn = (RelativeLayout) findViewById(R.id.backBtn);
        mTitle = (AppCompatTextView) findViewById(R.id.title);
        mHrLine = findViewById(R.id.mHrLine);
        mContent = (AppCompatTextView) findViewById(R.id.mContent);
        mIAgree = (AppCompatTextView) findViewById(R.id.mIAgree);
        mIDisagree = (AppCompatTextView) findViewById(R.id.mIDisagree);
        middleLayaout = (LinearLayout) findViewById(R.id.middleLayaout);


    }

    private void deleteaccount_old() {
        AppController.getHelperProgressDialog().showProgress(DeleteAccountActivity.this, "", "", false);
        DeleteAccountEvent deleteAccountEvent = new DeleteAccountEvent();
        HashMap<String, String> header = new HashMap<String, String>();
        header.put("userId", AppController.getHelperSharedPreference().readPreference(DeleteAccountActivity.this, getString(R.string.userid), ""));
        header.put("auth", AppController.getHelperSharedPreference().readPreference(DeleteAccountActivity.this, getString(R.string.auth), ""));
        Gson gson = new Gson();
        DeleteAccountData deleteAccountData = new DeleteAccountData();
        String json = gson.toJson(deleteAccountData);
        try {
            JSONObject obj = new JSONObject(json);
            RegistrationServerConfigEvent registrationServerConfigEvent = new RegistrationServerConfigEvent("delete_object", URLs.DELETE_ACCOUNT, DELETE_ACCOUNT_REPSONSECODE, DeleteAccountActivity.this, LoginData.class, null, header, obj, false, this);
            deleteAccountEvent.setmRegistrationServerConfigEvent(registrationServerConfigEvent);
            UserModulePresenter userModulePresenter = new UserModulePresenter();
            userModulePresenter.performDeleteAccount(deleteAccountEvent);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void setTextForView() {
        mTitle.setText(getResources().getString(R.string.confirmation));
    }

    private void setFont() {
        try {
            mTitle.setTypeface(AppController.getTypeface(DeleteAccountActivity.this, "medium"));
            mContent.setTypeface(AppController.getTypeface(DeleteAccountActivity.this, "regular"));
            mIAgree.setTypeface(AppController.getTypeface(DeleteAccountActivity.this, "regular"));
            mIDisagree.setTypeface(AppController.getTypeface(DeleteAccountActivity.this, "regular"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindEvents() {
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mIDisagree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mIAgree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean noData = false;
                if (mStoreWithdrawalTypeDeleteFlag.size() > 0) {
                    for (int i = 0; i < mStoreWithdrawalTypeDeleteFlag.size(); i++) {
                        if (mStoreWithdrawalTypeDeleteFlag.get(i).equalsIgnoreCase(mNoData)) {
                            Toast.makeText(DeleteAccountActivity.this, getResources().getString(R.string.select_option), Toast.LENGTH_LONG).show();
                            break;
                        }
                        if (i == (mStoreWithdrawalTypeDeleteFlag.size() - 1)) {
                            noData = true;
                        }
                    }

                }
                // if joined study is there then withdraw that
                if (noData) {
                    responseServerWithdrawFromStudy(mStudyIdList.get(0), mStoreWithdrawalTypeDeleteFlag.get(0));
                } else if (mStudyIdList.size() == 0 || mStudyIdList == null) {
                    // if joined study is not there, then directly call this method
                    deactivateAccount();
                }

            }
        });
    }

    private void checkAndCreateDataList() {
        try {
            // get all study id []
            mRealmStudie = mDBServiceSubscriber.getAllStudyIds(mRealm);
            // study Ids are storing to mStudyIdList
            for (int i = 0; i < mRealmStudie.size(); i++)
                mStudyIdList.add(mRealmStudie.get(i).getStudyId());

            for (int i = 0; i < mStudyIdList.size(); i++) {
                // get all study title, mStudyTitleList
                StudyList studyList = mDBServiceSubscriber.getStudyTitle(mStudyIdList.get(i), mRealm);
                String title = studyList.getTitle();
                if (title == null || title.equalsIgnoreCase(""))
                    mStudyTitleList.add(mNoData);
                else
                    mStudyTitleList.add(title);
                // get all withdawalType, mWithdrawalTypeList[]
                try {
                    StudyHome studyHome = mDBServiceSubscriber.getWithdrawalType(mStudyIdList.get(i), mRealm);
                    if (studyHome == null) {
                        mWithdrawalTypeList.add(mNoData);
                    } else {
                        String type = studyHome.getWithdrawalConfig().getType();
                        if (type == null || type.equalsIgnoreCase(""))
                            mWithdrawalTypeList.add(mNoData);
                        else
                            mWithdrawalTypeList.add(type);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            checkWithdrawalTypeListContainsNoData();
            if (mNoDataFlag) {
                setListLeaveStudy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkWithdrawalTypeListContainsNoData() {
        mNoDataFlag = false;
        for (int i = 0; i < mWithdrawalTypeList.size(); i++) {
            if (mWithdrawalTypeList.get(i).equalsIgnoreCase(mNoData)) {
                mTempPos = i;
                // missing details to get eg: withdrawal type
                callGetStudyInfoWebservice();
                break;
            }
            if (i == (mWithdrawalTypeList.size() - 1))
                mNoDataFlag = true;
        }
    }

    private void callGetStudyInfoWebservice() {
        AppController.getHelperProgressDialog().showProgress(DeleteAccountActivity.this, "", "", false);
        HashMap<String, String> header = new HashMap<>();
        String url = URLs.STUDY_INFO + "?studyId=" + mStudyIdList.get(mTempPos);
        GetUserStudyInfoEvent getUserStudyInfoEvent = new GetUserStudyInfoEvent();
        WCPConfigEvent wcpConfigEvent = new WCPConfigEvent("get", url, STUDY_INFO, DeleteAccountActivity.this, StudyHome.class, null, header, null, false, this);

        getUserStudyInfoEvent.setWcpConfigEvent(wcpConfigEvent);
        StudyModulePresenter studyModulePresenter = new StudyModulePresenter();
        studyModulePresenter.performGetGateWayStudyInfo(getUserStudyInfoEvent);
    }

    private void setListLeaveStudy() {
        try {
            for (int i = 0; i < mWithdrawalTypeList.size(); i++) {
                final int pos = i;
                if (mWithdrawalTypeList.get(i).equalsIgnoreCase("ask_user")) {
                    final View child2 = getLayoutInflater().inflate(R.layout.content_delete_account2, null);
                    final AppCompatTextView mentalHealthSurveyTitle = (AppCompatTextView) child2.findViewById(R.id.mentalHealthSurveyTitle);
                    final AppCompatCheckBox mDeleteButton1 = (AppCompatCheckBox) child2.findViewById(R.id.mDeleteButton1);
                    final AppCompatCheckBox mRetainButton1 = (AppCompatCheckBox) child2.findViewById(R.id.mRetainButton1);
                    // font setting
                    mentalHealthSurveyTitle.setTypeface(AppController.getTypeface(DeleteAccountActivity.this, "regular"));
                    mDeleteButton1.setTypeface(AppController.getTypeface(DeleteAccountActivity.this, "regular"));
                    mRetainButton1.setTypeface(AppController.getTypeface(DeleteAccountActivity.this, "regular"));
                    // value setting
                    mentalHealthSurveyTitle.setText(mStudyTitleList.get(i));
                    middleLayaout.addView(child2);
                    mStoreWithdrawalTypeDeleteFlag.add(pos, mNoData);
                    // click listner
                    mDeleteButton1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mDeleteButton1.setChecked(true);
                            mRetainButton1.setChecked(false);
                            mStoreWithdrawalTypeDeleteFlag.set(pos, "true");
                        }
                    });
                    mRetainButton1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mRetainButton1.setChecked(true);
                            mDeleteButton1.setChecked(false);
                            mStoreWithdrawalTypeDeleteFlag.set(pos, "false");
                        }
                    });
                }
                if (mWithdrawalTypeList.get(i).equalsIgnoreCase("delete_data")) {
                    final View child1 = getLayoutInflater().inflate(R.layout.content_delete_account1, null);
                    final AppCompatTextView mMedicationSurveyTitle = (AppCompatTextView) child1.findViewById(R.id.mMedicationSurveyTitle);
                    final AppCompatTextView mMedicationSurveyValue = (AppCompatTextView) child1.findViewById(R.id.mMedicationSurveyValue);
                    // font setting
                    mMedicationSurveyTitle.setTypeface(AppController.getTypeface(DeleteAccountActivity.this, "regular"));
                    mMedicationSurveyValue.setTypeface(AppController.getTypeface(DeleteAccountActivity.this, "regular"));
                    // value settings
                    mMedicationSurveyTitle.setText(mStudyTitleList.get(i));
                    mMedicationSurveyValue.setText(getResources().getString(R.string.response_deleted));
                    middleLayaout.addView(child1);
                    mStoreWithdrawalTypeDeleteFlag.add(pos, "true");
                } else if (mWithdrawalTypeList.get(i).equalsIgnoreCase("no_action")) {
                    final View child1 = getLayoutInflater().inflate(R.layout.content_delete_account1, null);

                    final AppCompatTextView mMedicationSurveyTitle = (AppCompatTextView) child1.findViewById(R.id.mMedicationSurveyTitle);
                    final AppCompatTextView mMedicationSurveyValue = (AppCompatTextView) child1.findViewById(R.id.mMedicationSurveyValue);
                    final RelativeLayout r1 = (RelativeLayout) child1.findViewById(R.id.activity_delete_account1);
                    // font setting
                    mMedicationSurveyTitle.setTypeface(AppController.getTypeface(DeleteAccountActivity.this, "regular"));
                    mMedicationSurveyValue.setTypeface(AppController.getTypeface(DeleteAccountActivity.this, "regular"));
                    // value setting
                    mMedicationSurveyTitle.setText(mStudyTitleList.get(i));
                    mMedicationSurveyValue.setText(getResources().getString(R.string.response_retained));
                    middleLayaout.addView(child1);
                    mStoreWithdrawalTypeDeleteFlag.add(pos, "false");
                }
            }
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
        mHrLine.setVisibility(View.VISIBLE);
    }


    public void responseServerWithdrawFromStudy(String mStudyId, String flag) {
        AppController.getHelperProgressDialog().showProgress(DeleteAccountActivity.this, "", "", false);
        try {
            Studies studies = mDBServiceSubscriber.getParticipantId(mStudyId, mRealm);
            HashMap<String, String> params = new HashMap<>();
            params.put("participantId", studies.getParticipantId());
            params.put("delete", flag);
            WithdrawFromStudyEvent withdrawFromStudyEvent = new WithdrawFromStudyEvent();
            ResponseServerConfigEvent responseServerConfigEvent = new ResponseServerConfigEvent("post_json", URLs.WITHDRAWFROMSTUDY, WITHDRAWFROMSTUDY, DeleteAccountActivity.this, LoginData.class, params, null, null, false, this);
            withdrawFromStudyEvent.setResponseServerConfigEvent(responseServerConfigEvent);
            StudyModulePresenter studyModulePresenter = new StudyModulePresenter();
            studyModulePresenter.performWithdrawFromStudy(withdrawFromStudyEvent);
        } catch (Exception e) {
            AppController.getHelperProgressDialog().dismissDialog();
            e.printStackTrace();
        }
    }

    @Override
    public <T> void asyncResponse(T response, int responseCode) {
        if (responseCode != STUDY_INFO)
            AppController.getHelperProgressDialog().dismissDialog();
        if (responseCode == DELETE_ACCOUNT_REPSONSECODE) {
            LoginData loginData = (LoginData) response;
            if (loginData != null) {

                try {
                    mDBServiceSubscriber.deleteDb(this);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                Toast.makeText(DeleteAccountActivity.this, getResources().getString(R.string.account_deletion), Toast.LENGTH_SHORT).show();
                SharedPreferences settings = SharedPreferenceHelper.getPreferences(DeleteAccountActivity.this);
                settings.edit().clear().apply();
                // delete passcode from keystore
                String pass = AppController.refreshKeys("passcode");
                if (pass != null)
                    AppController.deleteKey("passcode_" + pass);
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            } else {
                Toast.makeText(DeleteAccountActivity.this, R.string.unable_to_parse, Toast.LENGTH_SHORT).show();
            }
        } else if (responseCode == STUDY_INFO) {
            if (response != null) {
                mStudyHome = (StudyHome) response;
                // adding withdrawal type
                mWithdrawalTypeList.set(mTempPos, mStudyHome.getWithdrawalConfig().getType());
                // saving to db
                mStudyHome.setmStudyId(mStudyIdList.get(mTempPos));

                mDBServiceSubscriber.saveStudyInfoToDB(this,mStudyHome);
            }
            checkWithdrawalTypeListContainsNoData();
            if (mNoDataFlag) {
                AppController.getHelperProgressDialog().dismissDialog();
                setListLeaveStudy();
            }
        }
    }

    public void deactivateAccount() {
        HashMap<String, String> header = new HashMap();
        header.put("auth", AppController.getHelperSharedPreference().readPreference(DeleteAccountActivity.this, getResources().getString(R.string.auth), ""));
        header.put("userId", AppController.getHelperSharedPreference().readPreference(DeleteAccountActivity.this, getResources().getString(R.string.userid), ""));
        DeleteAccountEvent deleteAccountEvent = new DeleteAccountEvent();
        Gson gson = new Gson();
        DeleteAccountData deleteAccountData = new DeleteAccountData();
        String json = gson.toJson(deleteAccountData);
        JSONObject obj = null;
        try {
            if (mRealmStudie.size() > 0) {
                obj = new JSONObject(json);
                JSONArray jsonArray1 = new JSONArray();
                for (int i = 0; i < mRealmStudie.size(); i++) {
                    jsonArray1.put(mRealmStudie.get(i).getStudyId());
                }
                obj.put("deleteData", jsonArray1);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            RegistrationServerConfigEvent registrationServerConfigEvent = new RegistrationServerConfigEvent("delete", URLs.DELETE_ACCOUNT, DELETE_ACCOUNT_REPSONSECODE, DeleteAccountActivity.this, LoginData.class, null, header, obj, false, this);
            deleteAccountEvent.setmRegistrationServerConfigEvent(registrationServerConfigEvent);
            UserModulePresenter userModulePresenter = new UserModulePresenter();
            userModulePresenter.performDeleteAccount(deleteAccountEvent);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {
        AppController.getHelperProgressDialog().dismissDialog();
        if (statusCode.equalsIgnoreCase("401")) {
            Toast.makeText(DeleteAccountActivity.this, errormsg, Toast.LENGTH_SHORT).show();
            AppController.getHelperSessionExpired(DeleteAccountActivity.this, errormsg);
        } else {
            Toast.makeText(DeleteAccountActivity.this, errormsg, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public <T> void asyncResponse(T response, int responseCode, String serverType) {
        if (responseCode != WITHDRAWFROMSTUDY)
            AppController.getHelperProgressDialog().dismissDialog();
        if (responseCode == WITHDRAWFROMSTUDY) {
            try {
                // remove the value's because already executed and we have to get next studyId and flag value
                mDBServiceSubscriber.deleteActivityRunsFromDbByStudyID(this,mStudyIdList.get(0));
                mDBServiceSubscriber.deleteResponseFromDb(mStudyIdList.get(0), mRealm);
                mStoreWithdrawalTypeDeleteFlag.remove(0);
                mStudyIdList.remove(0);
                if (mStoreWithdrawalTypeDeleteFlag.isEmpty()) {
                    // for every studyid called responseServerWithdrawFromStudy services then call registration api
                    deactivateAccount();
                } else {
                    responseServerWithdrawFromStudy(mStudyIdList.get(0), mStoreWithdrawalTypeDeleteFlag.get(0));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    public <T> void asyncResponseFailure(int responseCode, String errormsg, String statusCode, T response) {
        AppController.getHelperProgressDialog().dismissDialog();
    }

    @Override
    protected void onDestroy() {
        mDBServiceSubscriber.closeRealmObj(mRealm);
        super.onDestroy();
    }
}
