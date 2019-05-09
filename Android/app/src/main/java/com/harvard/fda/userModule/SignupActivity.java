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

package com.harvard.fda.userModule;

import android.content.ComponentName;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.harvard.fda.AppConfig;
import com.harvard.fda.R;
import com.harvard.fda.studyAppModule.StandaloneActivity;
import com.harvard.fda.studyAppModule.StudyActivity;
import com.harvard.fda.studyAppModule.StudyModulePresenter;
import com.harvard.fda.userModule.event.GetTermsAndConditionEvent;
import com.harvard.fda.userModule.event.RegisterUserEvent;
import com.harvard.fda.userModule.event.UpdateUserProfileEvent;
import com.harvard.fda.userModule.model.TermsAndConditionData;
import com.harvard.fda.userModule.webserviceModel.RegistrationData;
import com.harvard.fda.userModule.webserviceModel.UpdateUserProfileData;
import com.harvard.fda.utils.AppController;
import com.harvard.fda.utils.SetDialogHelper;
import com.harvard.fda.utils.URLs;
import com.harvard.fda.webserviceModule.apiHelper.ApiCall;
import com.harvard.fda.webserviceModule.events.RegistrationServerConfigEvent;
import com.harvard.fda.webserviceModule.events.WCPConfigEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import static com.harvard.fda.BuildConfig.VERSION_NAME;
import static com.harvard.fda.R.string.signup;

public class SignupActivity extends AppCompatActivity implements ApiCall.OnAsyncRequestComplete {
    private static final int UPDATE_USER_PROFILE = 101;
    private RelativeLayout mBackBtn;
    private AppCompatTextView mTitle;
    private RelativeLayout mCancelBtn;
    private RelativeLayout mInfoIcon;
    private AppCompatTextView mCancelTxt;
    private AppCompatEditText mFirstName;
    private AppCompatEditText mLastName;
    private AppCompatEditText mEmail;
    private AppCompatEditText mPassword;
    private AppCompatEditText mConfirmPassword;
    private Switch mSwitch;
    private AppCompatTextView mFirstNameLabel;
    private AppCompatTextView mLastNameLabel;
    private AppCompatTextView mEmailLabel;
    private AppCompatTextView mPasswordLabel;
    private AppCompatTextView mConfirmPasswordLabel;
    private AppCompatTextView mTouchIdLabel;
    private AppCompatTextView mAgreeLabel;
    private AppCompatCheckBox mAgree;
    private AppCompatTextView mSubmitBtn;
    private int REGISTRATION_REQUEST = 2;
    private int GET_TERMS_AND_CONDITION = 3;
    private int STUDYINFO_REQUEST = 100;
    private boolean mClicked;
    private TermsAndConditionData mTermsAndConditionData;
    RegistrationData mRegistrationData;
    private String mUserAuth;
    private String mUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mClicked = false;
        initializeXMLId();
        setTextForView();
        customTextView(mAgreeLabel);
        setFont();
        bindEvents();
        callGetTermsAndConditionWebservice();
    }

    private void initializeXMLId() {
        mBackBtn = (RelativeLayout) findViewById(R.id.backBtn);
        mInfoIcon = (RelativeLayout) findViewById(R.id.mInfoIcon);
        mTitle = (AppCompatTextView) findViewById(R.id.title);
        mCancelBtn = (RelativeLayout) findViewById(R.id.cancelBtn);
        mCancelTxt = (AppCompatTextView) findViewById(R.id.cancelTxt);
        mFirstNameLabel = (AppCompatTextView) findViewById(R.id.first_name_label);
        mFirstName = (AppCompatEditText) findViewById(R.id.edittxt_first_name);
        mLastNameLabel = (AppCompatTextView) findViewById(R.id.last_name_label);
        mLastName = (AppCompatEditText) findViewById(R.id.edittxt_last_name);
        mEmailLabel = (AppCompatTextView) findViewById(R.id.email_label);
        mEmail = (AppCompatEditText) findViewById(R.id.edittxt_email);
        mPasswordLabel = (AppCompatTextView) findViewById(R.id.password_label);
        mPassword = (AppCompatEditText) findViewById(R.id.edittxt_password);
        mConfirmPasswordLabel = (AppCompatTextView) findViewById(R.id.confirm_password_label);
        mConfirmPassword = (AppCompatEditText) findViewById(R.id.edittxt_confirm_password);
        mTouchIdLabel = (AppCompatTextView) findViewById(R.id.touch_id_label);
        mAgreeLabel = (AppCompatTextView) findViewById(R.id.agree_label);
        mSwitch = (Switch) findViewById(R.id.switch_touch);
        mAgree = (AppCompatCheckBox) findViewById(R.id.agreeButton);
        mSubmitBtn = (AppCompatTextView) findViewById(R.id.submitButton);
    }

    private void setTextForView() {
        mCancelBtn.setVisibility(View.GONE);
        mInfoIcon.setVisibility(View.VISIBLE);
        mTitle.setText(getResources().getString(signup));
    }

    // set link for privacy and policy
    private void customTextView(AppCompatTextView view) {
        SpannableStringBuilder spanTxt = new SpannableStringBuilder(getResources().getString(R.string.i_agree) + " ");
        spanTxt.setSpan(new ForegroundColorSpan(ContextCompat.getColor(SignupActivity.this, R.color.colorPrimaryBlack)), 0, spanTxt.length(), 0);
        spanTxt.append(getResources().getString(R.string.terms2));
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(ContextCompat.getColor(SignupActivity.this, R.color.colorPrimary));    // you can use custom color
                ds.setUnderlineText(false);    // this remove the underline
            }

            @Override
            public void onClick(View widget) {
                if (mTermsAndConditionData != null) {
                    Intent termsIntent = new Intent(SignupActivity.this, TermsPrivacyPolicyActivity.class);
                    termsIntent.putExtra("title", getResources().getString(R.string.terms));
                    termsIntent.putExtra("url", mTermsAndConditionData.getTerms());
                    startActivity(termsIntent);
                }
            }
        }, spanTxt.length() - getResources().getString(R.string.terms2).length(), spanTxt.length(), 0);

        spanTxt.append(" " + getResources().getString(R.string.and));
        spanTxt.setSpan(new ForegroundColorSpan(ContextCompat.getColor(SignupActivity.this, R.color.colorPrimaryBlack)), 20, spanTxt.length(), 0);

        spanTxt.append(" " + getResources().getString(R.string.privacy_policy2));
        String temp = " " + getResources().getString(R.string.privacy_policy2);
        spanTxt.setSpan(new ClickableSpan() {

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(ContextCompat.getColor(SignupActivity.this, R.color.colorPrimary));    // you can use custom color
                ds.setUnderlineText(false);    // this remove the underline
            }

            @Override
            public void onClick(View widget) {
                if (mTermsAndConditionData != null) {
                    Intent termsIntent = new Intent(SignupActivity.this, TermsPrivacyPolicyActivity.class);
                    termsIntent.putExtra("title", getResources().getString(R.string.privacy_policy));
                    termsIntent.putExtra("url", mTermsAndConditionData.getPrivacy());
                    startActivity(termsIntent);
                }
            }
        }, spanTxt.length() - temp.length(), spanTxt.length(), 0);

        view.setMovementMethod(LinkMovementMethod.getInstance());
        view.setText(spanTxt, TextView.BufferType.SPANNABLE);
        mEmail.requestFocus();
    }

    private void setFont() {
        try {
            mTitle.setTypeface(AppController.getTypeface(SignupActivity.this, "medium"));
            mCancelTxt.setTypeface(AppController.getTypeface(SignupActivity.this, "medium"));
            mFirstNameLabel.setTypeface(AppController.getTypeface(SignupActivity.this, "regular"));
            mFirstName.setTypeface(AppController.getTypeface(SignupActivity.this, "regular"));
            mLastNameLabel.setTypeface(AppController.getTypeface(SignupActivity.this, "regular"));
            mLastName.setTypeface(AppController.getTypeface(SignupActivity.this, "regular"));
            mEmailLabel.setTypeface(AppController.getTypeface(SignupActivity.this, "regular"));
            mEmail.setTypeface(AppController.getTypeface(SignupActivity.this, "regular"));
            mPasswordLabel.setTypeface(AppController.getTypeface(SignupActivity.this, "regular"));
            mPassword.setTypeface(AppController.getTypeface(SignupActivity.this, "regular"));
            mConfirmPasswordLabel.setTypeface(AppController.getTypeface(SignupActivity.this, "regular"));
            mConfirmPassword.setTypeface(AppController.getTypeface(SignupActivity.this, "regular"));
            mTouchIdLabel.setTypeface(AppController.getTypeface(SignupActivity.this, "regular"));
            mAgreeLabel.setTypeface(AppController.getTypeface(SignupActivity.this, "regular"));
            mAgree.setTypeface(AppController.getTypeface(SignupActivity.this, "regular"));
            mSubmitBtn.setTypeface(AppController.getTypeface(SignupActivity.this, "regular"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindEvents() {
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    AppController.getHelperHideKeyboard(SignupActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finish();
            }
        });

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClicked == false) {
                    mClicked = true;
                    callRegisterUserWebService();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mClicked = false;
                        }
                    }, 2000);
                }

            }
        });
        mInfoIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetDialogHelper.setNeutralDialog(SignupActivity.this, getResources().getString(R.string.registration_message), false, getResources().getString(R.string.ok), getResources().getString(R.string.why_register));
            }
        });
    }

    private void callRegisterUserWebService() {
        String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!\"#$%&'()*+,-.:;<=>?@\\[\\]^_`{|}~])(?=\\S+$).{8,64}$";
        if (mPassword.getText().toString().equalsIgnoreCase("") && mEmail.getText().toString().equalsIgnoreCase("") && mConfirmPassword.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, getResources().getString(R.string.enter_all_field_empty), Toast.LENGTH_SHORT).show();
        } else if (mEmail.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, getResources().getString(R.string.email_empty), Toast.LENGTH_SHORT).show();
        } else if (!AppController.getHelperIsValidEmail(mEmail.getText().toString())) {
            Toast.makeText(this, getResources().getString(R.string.email_validation), Toast.LENGTH_SHORT).show();
        } else if (mPassword.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, getResources().getString(R.string.password_empty), Toast.LENGTH_SHORT).show();
        } else if (!mPassword.getText().toString().matches(PASSWORD_PATTERN)) {
            Toast.makeText(this, getResources().getString(R.string.password_validation), Toast.LENGTH_SHORT).show();
        } else if (checkPasswordContainsEmailID(mEmail.getText().toString(), mPassword.getText().toString())) {
            Toast.makeText(this, getResources().getString(R.string.password_contain_email), Toast.LENGTH_SHORT).show();
        } else if (mConfirmPassword.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, getResources().getString(R.string.confirm_password_empty), Toast.LENGTH_SHORT).show();
        } else if (!mPassword.getText().toString().equals(mConfirmPassword.getText().toString())) {
            Toast.makeText(this, getResources().getString(R.string.password_mismatch_error), Toast.LENGTH_SHORT).show();
        } else if (!mAgree.isChecked()) {
            Toast.makeText(this, getResources().getString(R.string.terms_and_condition_validation), Toast.LENGTH_SHORT).show();
        } else {
            AppController.getHelperProgressDialog().showProgress(SignupActivity.this, "", "", false);
            RegisterUserEvent registerUserEvent = new RegisterUserEvent();
            HashMap<String, String> params = new HashMap<>();
            params.put("emailId", mEmail.getText().toString());
            params.put("password", mPassword.getText().toString());
            params.put("appId", "" + AppConfig.PackageName);
            RegistrationServerConfigEvent registrationServerConfigEvent = new RegistrationServerConfigEvent("post", URLs.REGISTER_USER, REGISTRATION_REQUEST, this, RegistrationData.class, params, null, null, false, this);
            registerUserEvent.setmRegistrationServerConfigEvent(registrationServerConfigEvent);
            UserModulePresenter userModulePresenter = new UserModulePresenter();
            userModulePresenter.performRegistration(registerUserEvent);
        }
    }

    private boolean checkPasswordContainsEmailID(String email, String password) {
        if (password.contains(email)) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public <T> void asyncResponse(T response, int responseCode) {
        AppController.getHelperProgressDialog().dismissDialog();
        if (GET_TERMS_AND_CONDITION == responseCode) {
            mTermsAndConditionData = (TermsAndConditionData) response;
        } else if (responseCode == REGISTRATION_REQUEST) {
            mRegistrationData = (RegistrationData) response;
            if (mRegistrationData != null) {
                mUserID = mRegistrationData.getUserId();
                mUserAuth = mRegistrationData.getAuth();
                new GetFCMRefreshToken().execute();
            } else {
                Toast.makeText(this, getResources().getString(R.string.unable_to_signup), Toast.LENGTH_SHORT).show();
            }
        } else if (responseCode == UPDATE_USER_PROFILE) {
            UpdateUserProfileData updateUserProfileData = (UpdateUserProfileData) response;
            if (updateUserProfileData != null) {
                if (updateUserProfileData.getMessage().equalsIgnoreCase("success")) {
                    signup(mRegistrationData);
                } else {
                    Toast.makeText(this, getResources().getString(R.string.unable_to_signup), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, getResources().getString(R.string.unable_to_signup), Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void signup(RegistrationData registrationData) {
        if (registrationData != null) {
            AppController.getHelperSharedPreference().writePreference(SignupActivity.this, getString(R.string.refreshToken), registrationData.getRefreshToken());
            if (registrationData.isVerified()) {
                AppController.getHelperSharedPreference().writePreference(SignupActivity.this, getString(R.string.userid), "" + registrationData.getUserId());
                AppController.getHelperSharedPreference().writePreference(SignupActivity.this, getString(R.string.auth), "" + registrationData.getAuth());
                AppController.getHelperSharedPreference().writePreference(SignupActivity.this, getString(R.string.verified), "" + registrationData.isVerified());
                AppController.getHelperSharedPreference().writePreference(SignupActivity.this, getString(R.string.email), "" + mEmail.getText().toString());
                if (getIntent().getStringExtra("from") != null && getIntent().getStringExtra("from").equalsIgnoreCase("StudyInfo")) {
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    if (AppConfig.AppType.equalsIgnoreCase(getString(R.string.app_gateway))) {
                        Intent intent = new Intent(SignupActivity.this, StudyActivity.class);
                        ComponentName cn = intent.getComponent();
                        Intent mainIntent = Intent.makeRestartActivityTask(cn);
                        startActivity(mainIntent);
                        finish();
                    } else {
                        Intent intent = new Intent(SignupActivity.this, StandaloneActivity.class);
                        ComponentName cn = intent.getComponent();
                        Intent mainIntent = Intent.makeRestartActivityTask(cn);
                        startActivity(mainIntent);
                        finish();
                    }
                }
            } else {
                if (getIntent().getStringExtra("from") != null && getIntent().getStringExtra("from").equalsIgnoreCase("StudyInfo")) {
                    Intent intent = new Intent(SignupActivity.this, VerificationStepActivity.class);
                    intent.putExtra("from", "StudyInfo");
                    intent.putExtra("type", "Signup");
                    intent.putExtra("userid", registrationData.getUserId());
                    intent.putExtra("auth", registrationData.getAuth());
                    intent.putExtra("verified", registrationData.isVerified());
                    intent.putExtra("email", mEmail.getText().toString());
                    startActivityForResult(intent, STUDYINFO_REQUEST);
                } else {
                    Intent intent = new Intent(SignupActivity.this, VerificationStepActivity.class);
                    intent.putExtra("from", "Activity");
                    intent.putExtra("type", "Signup");
                    intent.putExtra("userid", registrationData.getUserId());
                    intent.putExtra("auth", registrationData.getAuth());
                    intent.putExtra("verified", registrationData.isVerified());
                    intent.putExtra("email", mEmail.getText().toString());
                    startActivity(intent);
                }
            }
        } else {
            Toast.makeText(this, R.string.unable_to_parse, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == STUDYINFO_REQUEST) {
            if (resultCode == RESULT_OK) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }

    @Override
    public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {
        AppController.getHelperProgressDialog().dismissDialog();
        if (responseCode == UPDATE_USER_PROFILE) {
            if (statusCode.equalsIgnoreCase("401")) {
                Toast.makeText(SignupActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                AppController.getHelperSessionExpired(SignupActivity.this, errormsg);
            } else {
                Toast.makeText(this, errormsg, Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, errormsg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void callGetTermsAndConditionWebservice() {
        AppController.getHelperProgressDialog().showProgress(SignupActivity.this, "", "", false);
        GetTermsAndConditionEvent termsAndConditionEvent = new GetTermsAndConditionEvent();
        HashMap<String, String> header = new HashMap();
        WCPConfigEvent wcpConfigEvent = new WCPConfigEvent("get", URLs.GET_TERMS_AND_CONDITION, GET_TERMS_AND_CONDITION, SignupActivity.this, TermsAndConditionData.class, null, header, null, false, SignupActivity.this);

        termsAndConditionEvent.setWcpConfigEvent(wcpConfigEvent);
        StudyModulePresenter studyModulePresenter = new StudyModulePresenter();
        studyModulePresenter.performGetTermsAndCondition(termsAndConditionEvent);
    }

    private void callUpdateProfileWebService(String deviceToken) {
        AppController.getHelperProgressDialog().showProgress(SignupActivity.this, "", "", false);
        UpdateUserProfileEvent updateUserProfileEvent = new UpdateUserProfileEvent();
        HashMap<String, String> params = new HashMap<>();
        params.put("auth", mUserAuth);
        params.put("userId", mUserID);

        JSONObject jsonObjBody = new JSONObject();
        JSONObject infoJson = new JSONObject();
        try {
            infoJson.put("os", "android");
            infoJson.put("appVersion", VERSION_NAME);
            infoJson.put("deviceToken", deviceToken);

            jsonObjBody.put("info", infoJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONObject settingJson = new JSONObject();
        try {
            settingJson.put("passcode", true);
            settingJson.put("remoteNotifications", true);
            settingJson.put("localNotifications", true);
            jsonObjBody.put("settings", settingJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RegistrationServerConfigEvent registrationServerConfigEvent = new RegistrationServerConfigEvent("post_object", URLs.UPDATE_USER_PROFILE, UPDATE_USER_PROFILE, this, UpdateUserProfileData.class, null, params, jsonObjBody, false, this);
        updateUserProfileEvent.setmRegistrationServerConfigEvent(registrationServerConfigEvent);
        UserModulePresenter userModulePresenter = new UserModulePresenter();
        userModulePresenter.performUpdateUserProfile(updateUserProfileEvent);
    }

    private class GetFCMRefreshToken extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String token = "";
            if (FirebaseInstanceId.getInstance().getToken() == null || FirebaseInstanceId.getInstance().getToken().equalsIgnoreCase("")) {
                boolean regIdStatus = false;
                while (!regIdStatus) {
                    token = AppController.getHelperSharedPreference().readPreference(SignupActivity.this, "deviceToken", "");
                    if (!token.equalsIgnoreCase(""))
                        regIdStatus = true;
                }
            } else {
                AppController.getHelperSharedPreference().writePreference(SignupActivity.this, "deviceToken", FirebaseInstanceId.getInstance().getToken());
                token = AppController.getHelperSharedPreference().readPreference(SignupActivity.this, "deviceToken", "");
            }
            return token;
        }

        @Override
        protected void onPostExecute(String token) {
            callUpdateProfileWebService(token);
        }

        @Override
        protected void onPreExecute() {
            AppController.getHelperProgressDialog().showProgress(SignupActivity.this, "", "", false);
        }
    }
}
