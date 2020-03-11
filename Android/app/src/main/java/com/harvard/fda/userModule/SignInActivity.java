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
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.harvard.fda.AppConfig;
import com.harvard.fda.R;
import com.harvard.fda.notificationModule.NotificationModuleSubscriber;
import com.harvard.fda.studyAppModule.ChangePasswordActivity;
import com.harvard.fda.studyAppModule.StandaloneActivity;
import com.harvard.fda.studyAppModule.StudyActivity;
import com.harvard.fda.studyAppModule.StudyModulePresenter;
import com.harvard.fda.userModule.event.GetTermsAndConditionEvent;
import com.harvard.fda.userModule.event.GetUserProfileEvent;
import com.harvard.fda.userModule.event.LoginEvent;
import com.harvard.fda.userModule.event.UpdateUserProfileEvent;
import com.harvard.fda.userModule.model.TermsAndConditionData;
import com.harvard.fda.userModule.webserviceModel.LoginData;
import com.harvard.fda.userModule.webserviceModel.UpdateUserProfileData;
import com.harvard.fda.userModule.webserviceModel.UserProfileData;
import com.harvard.fda.utils.AppController;
import com.harvard.fda.utils.SetDialogHelper;
import com.harvard.fda.utils.URLs;
import com.harvard.fda.webserviceModule.apiHelper.ApiCall;
import com.harvard.fda.webserviceModule.events.RegistrationServerConfigEvent;
import com.harvard.fda.webserviceModule.events.WCPConfigEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;

import static com.harvard.fda.BuildConfig.VERSION_NAME;

public class SignInActivity extends AppCompatActivity implements ApiCall.OnAsyncRequestComplete {

    private static final int UPDATE_USER_PROFILE = 101;
    private static final int USER_PROFILE_REQUEST = 102;
    private static final int PASSCODE_RESPONSE = 103;
    private RelativeLayout mBackBtn;
    private RelativeLayout mInfoIcon;
    private AppCompatTextView mTitle;
    private RelativeLayout mCancelBtn;
    private AppCompatTextView mCancelTxt;
    private AppCompatEditText mEmail;
    private AppCompatEditText mPassword;
    private AppCompatTextView mEmailLabel;
    private AppCompatTextView mPasswordLabel;
    private AppCompatTextView mSignInLabel;
    private AppCompatTextView mForgotPasswordLabel;
    private AppCompatTextView mNewUsrSignUp;
    private int LOGIN_REQUEST = 1;
    private int STUDYINFO_REQUEST = 100;
    private boolean mClicked;
    private String mUserAuth;
    private String mUserID;
    LoginData loginData;
    private AppCompatTextView mAgreeLabel;
    private int GET_TERMS_AND_CONDITION = 3;
    private TermsAndConditionData mTermsAndConditionData;
    private UserProfileData userProfileData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        mClicked = false;
        initializeXMLId();
        setTextForView();
        customTextViewAgree(mAgreeLabel);
        setFont();
        customTextView();
        bindEvents();
        callGetTermsAndConditionWebservice();
    }

    private void initializeXMLId() {
        mBackBtn = (RelativeLayout) findViewById(R.id.backBtn);
        mInfoIcon = (RelativeLayout) findViewById(R.id.mInfoIcon);
        mTitle = (AppCompatTextView) findViewById(R.id.title);
        mCancelBtn = (RelativeLayout) findViewById(R.id.cancelBtn);
        mCancelTxt = (AppCompatTextView) findViewById(R.id.cancelTxt);
        mEmailLabel = (AppCompatTextView) findViewById(R.id.email_label);
        mEmail = (AppCompatEditText) findViewById(R.id.edittxt_email);
        mPasswordLabel = (AppCompatTextView) findViewById(R.id.password_label);
        mPassword = (AppCompatEditText) findViewById(R.id.edittxt_password);
        mSignInLabel = (AppCompatTextView) findViewById(R.id.signInButton);
        mForgotPasswordLabel = (AppCompatTextView) findViewById(R.id.forgot_password);
        mNewUsrSignUp = (AppCompatTextView) findViewById(R.id.newUsrSignUp);
        mAgreeLabel = (AppCompatTextView) findViewById(R.id.agree_terms);
    }

    private void setTextForView() {
        mCancelBtn.setVisibility(View.GONE);
        mInfoIcon.setVisibility(View.VISIBLE);
        mTitle.setText(getResources().getString(R.string.sign_in));
    }

    @SuppressWarnings("deprecation")
    private void customTextView() {
        String html = getResources().getString(R.string.new_user) + " <font color=\"#007cba\">" + getResources().getString(R.string.side_menu_sign_up) + "</font>";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            mNewUsrSignUp.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY));
        } else {
            mNewUsrSignUp.setText(Html.fromHtml(html));
        }
    }

    // set link for privacy and policy
    private void customTextViewAgree(AppCompatTextView view) {
        SpannableStringBuilder spanTxt = new SpannableStringBuilder(getResources().getString(R.string.you_agree_this_app));
        spanTxt.setSpan(new ForegroundColorSpan(ContextCompat.getColor(SignInActivity.this, R.color.colorPrimaryBlack)), 0, spanTxt.length(), 0);
        spanTxt.append(getResources().getString(R.string.terms2));
        spanTxt.setSpan(new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(ContextCompat.getColor(SignInActivity.this, R.color.colorPrimary));    // you can use custom color
                ds.setUnderlineText(false);    // this remove the underline
            }

            @Override
            public void onClick(View widget) {
                if (mTermsAndConditionData != null) {
                    Intent termsIntent = new Intent(SignInActivity.this, TermsPrivacyPolicyActivity.class);
                    termsIntent.putExtra("title", getResources().getString(R.string.terms));
                    termsIntent.putExtra("url", mTermsAndConditionData.getTerms());
                    startActivity(termsIntent);
                }
            }
        }, spanTxt.length() - getResources().getString(R.string.terms2).length(), spanTxt.length(), 0);

        spanTxt.append(" " + getResources().getString(R.string.and));
        spanTxt.setSpan(new ForegroundColorSpan(ContextCompat.getColor(SignInActivity.this, R.color.colorPrimaryBlack)), spanTxt.length() - " and".length(), spanTxt.length(), 0);

        spanTxt.append(" " + getResources().getString(R.string.privacy_policy2));
        String temp = " " + getResources().getString(R.string.privacy_policy2);
        spanTxt.setSpan(new ClickableSpan() {

            @Override
            public void updateDrawState(TextPaint ds) {
                ds.setColor(ContextCompat.getColor(SignInActivity.this, R.color.colorPrimary));    // you can use custom color
                ds.setUnderlineText(false);    // this remove the underline
            }

            @Override
            public void onClick(View widget) {
                if (mTermsAndConditionData != null) {
                    Intent termsIntent = new Intent(SignInActivity.this, TermsPrivacyPolicyActivity.class);
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
            mTitle.setTypeface(AppController.getTypeface(SignInActivity.this, "medium"));
            mCancelTxt.setTypeface(AppController.getTypeface(SignInActivity.this, "medium"));
            mEmailLabel.setTypeface(AppController.getTypeface(SignInActivity.this, "regular"));
            mEmail.setTypeface(AppController.getTypeface(SignInActivity.this, "regular"));
            mPasswordLabel.setTypeface(AppController.getTypeface(SignInActivity.this, "regular"));
            mPassword.setTypeface(AppController.getTypeface(SignInActivity.this, "regular"));
            mSignInLabel.setTypeface(AppController.getTypeface(SignInActivity.this, "regular"));
            mForgotPasswordLabel.setTypeface(AppController.getTypeface(SignInActivity.this, "regular"));
            mNewUsrSignUp.setTypeface(AppController.getTypeface(SignInActivity.this, "regular"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callGetTermsAndConditionWebservice() {
        AppController.getHelperProgressDialog().showProgress(SignInActivity.this, "", "", false);
        GetTermsAndConditionEvent termsAndConditionEvent = new GetTermsAndConditionEvent();
        HashMap<String, String> header = new HashMap();
        WCPConfigEvent wcpConfigEvent = new WCPConfigEvent("get", URLs.GET_TERMS_AND_CONDITION, GET_TERMS_AND_CONDITION, SignInActivity.this, TermsAndConditionData.class, null, header, null, false, SignInActivity.this);

        termsAndConditionEvent.setWcpConfigEvent(wcpConfigEvent);
        StudyModulePresenter studyModulePresenter = new StudyModulePresenter();
        studyModulePresenter.performGetTermsAndCondition(termsAndConditionEvent);
    }

    private void bindEvents() {
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    AppController.getHelperHideKeyboard(SignInActivity.this);
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

        mSignInLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (mClicked == false) {
                    mClicked = true;
                    callLoginWebService();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mClicked = false;
                        }
                    }, 2000);
                }

            }
        });

        mForgotPasswordLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEmail.setText("");
                mPassword.setText("");
                mEmail.requestFocus();
                Intent intent = new Intent(SignInActivity.this, ForgotPasswordActivity.class);
                startActivity(intent);
            }
        });
        mNewUsrSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getIntent().getStringExtra("from") != null && getIntent().getStringExtra("from").equalsIgnoreCase("StudyInfo")) {
                    Intent intent = new Intent(SignInActivity.this, SignupActivity.class);
                    intent.putExtra("from", "StudyInfo");
                    startActivityForResult(intent, STUDYINFO_REQUEST);
                } else {
                    Intent intent = new Intent(SignInActivity.this, SignupActivity.class);
                    startActivity(intent);
                }
            }
        });
        mInfoIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SetDialogHelper.setNeutralDialog(SignInActivity.this, getResources().getString(R.string.registration_message), false, getResources().getString(R.string.ok), getResources().getString(R.string.why_register));
            }
        });
    }


    private void callLoginWebService() {
        if (mEmail.getText().toString().equalsIgnoreCase("") && mPassword.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, getResources().getString(R.string.enter_all_field_empty), Toast.LENGTH_SHORT).show();
        } else if (mEmail.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, getResources().getString(R.string.email_empty), Toast.LENGTH_SHORT).show();
        } else if (!AppController.getHelperIsValidEmail(mEmail.getText().toString())) {
            Toast.makeText(this, getResources().getString(R.string.email_validation), Toast.LENGTH_SHORT).show();
        } else if (mPassword.getText().toString().equalsIgnoreCase("")) {
            Toast.makeText(this, getResources().getString(R.string.password_empty), Toast.LENGTH_SHORT).show();
        } else {
            AppController.getHelperProgressDialog().showProgress(SignInActivity.this, "", "", false);
            LoginEvent loginEvent = new LoginEvent();
            HashMap<String, String> params = new HashMap<>();
            params.put("emailId", mEmail.getText().toString());
            params.put("password", mPassword.getText().toString());
            params.put("appId", "" + AppConfig.PackageName);
            RegistrationServerConfigEvent registrationServerConfigEvent = new RegistrationServerConfigEvent("post", URLs.LOGIN, LOGIN_REQUEST, this, LoginData.class, params, null, null, false, this);
            loginEvent.setmRegistrationServerConfigEvent(registrationServerConfigEvent);
            UserModulePresenter userModulePresenter = new UserModulePresenter();
            userModulePresenter.performLogin(loginEvent);
        }
    }

    @Override
    public <T> void asyncResponse(T response, int responseCode) {
        AppController.getHelperProgressDialog().dismissDialog();
        if (responseCode == LOGIN_REQUEST) {
            //FilterActivity Screen json object clearing
            AppController.getHelperSharedPreference().writePreference(SignInActivity.this, getString(R.string.json_object_filter), "");
            loginData = (LoginData) response;
            if (loginData != null) {
                mUserAuth = loginData.getAuth();
                mUserID = loginData.getUserId();
                AppController.getHelperSharedPreference().writePreference(SignInActivity.this, getString(R.string.refreshToken), loginData.getRefreshToken());
                new GetFCMRefreshToken().execute();
            }
        } else if (responseCode == UPDATE_USER_PROFILE) {
            UpdateUserProfileData updateUserProfileData = (UpdateUserProfileData) response;
            if (updateUserProfileData != null) {
                if (updateUserProfileData.getMessage().equalsIgnoreCase("success")) {
                    callUserProfileWebService();
                } else {
                    Toast.makeText(this, getResources().getString(R.string.not_able_to_login), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, getResources().getString(R.string.not_able_to_login), Toast.LENGTH_SHORT).show();
            }
        } else if (responseCode == USER_PROFILE_REQUEST) {
            userProfileData = (UserProfileData) response;
            if (userProfileData != null) {
                if (userProfileData.getSettings().isPasscode()) {
                    AppController.getHelperSharedPreference().writePreference(SignInActivity.this, getString(R.string.initialpasscodeset), "no");
                    if (loginData.isVerified()) {
                        AppController.getHelperSharedPreference().writePreference(SignInActivity.this, getString(R.string.userid), "" + loginData.getUserId());
                        AppController.getHelperSharedPreference().writePreference(SignInActivity.this, getString(R.string.auth), "" + loginData.getAuth());
                        AppController.getHelperSharedPreference().writePreference(SignInActivity.this, getString(R.string.verified), "" + loginData.isVerified());
                        AppController.getHelperSharedPreference().writePreference(SignInActivity.this, getString(R.string.email), "" + mEmail.getText().toString());

                        Intent intent = new Intent(SignInActivity.this, NewPasscodeSetupActivity.class);
                        intent.putExtra("from", "signin");
                        startActivityForResult(intent, PASSCODE_RESPONSE);
                    } else {
                        Intent intent = new Intent(SignInActivity.this, VerificationStepActivity.class);
                        intent.putExtra("from", "Activity");
                        intent.putExtra("type", "Signin");
                        intent.putExtra("userid", loginData.getUserId());
                        intent.putExtra("auth", loginData.getAuth());
                        intent.putExtra("verified", loginData.isVerified());
                        intent.putExtra("email", mEmail.getText().toString());
                        intent.putExtra("password", mPassword.getText().toString());
                        startActivity(intent);
                    }
                } else {
                    login();
                }
            } else {
                Toast.makeText(this, getResources().getString(R.string.not_able_to_login), Toast.LENGTH_SHORT).show();
            }
        } else if (GET_TERMS_AND_CONDITION == responseCode) {
            mTermsAndConditionData = (TermsAndConditionData) response;
        }
    }

    private class GetFCMRefreshToken extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String token = "";
            if (FirebaseInstanceId.getInstance().getToken() == null || FirebaseInstanceId.getInstance().getToken().equalsIgnoreCase("")) {
                boolean regIdStatus = false;
                while (!regIdStatus) {
                    token = AppController.getHelperSharedPreference().readPreference(SignInActivity.this, "deviceToken", "");
                    if (!token.equalsIgnoreCase(""))
                        regIdStatus = true;
                }
            } else {
                AppController.getHelperSharedPreference().writePreference(SignInActivity.this, "deviceToken", FirebaseInstanceId.getInstance().getToken());
                token = AppController.getHelperSharedPreference().readPreference(SignInActivity.this, "deviceToken", "");
            }
            return token;
        }

        @Override
        protected void onPostExecute(String token) {
            callUpdateProfileWebService(token);
        }

        @Override
        protected void onPreExecute() {
            AppController.getHelperProgressDialog().showProgress(SignInActivity.this, "", "", false);
        }
    }

    private void login() {
        if (loginData.getResetPassword()) {
            Intent intent = new Intent(SignInActivity.this, ChangePasswordActivity.class);
            intent.putExtra("from", "SignInFragment");
            intent.putExtra("password", mPassword.getText().toString());
            intent.putExtra("userid", loginData.getUserId());
            intent.putExtra("auth", loginData.getAuth());
            intent.putExtra("verified", loginData.isVerified());
            intent.putExtra("email", mEmail.getText().toString());
            startActivity(intent);
        } else if (loginData.isVerified()) {
            AppController.getHelperSharedPreference().writePreference(SignInActivity.this, getString(R.string.userid), "" + loginData.getUserId());
            AppController.getHelperSharedPreference().writePreference(SignInActivity.this, getString(R.string.auth), "" + loginData.getAuth());
            AppController.getHelperSharedPreference().writePreference(SignInActivity.this, getString(R.string.verified), "" + loginData.isVerified());
            AppController.getHelperSharedPreference().writePreference(SignInActivity.this, getString(R.string.email), "" + mEmail.getText().toString());
            if (userProfileData != null && (!userProfileData.getSettings().isLocalNotifications() || userProfileData.getSettings().isRemoteNotifications())) {
                NotificationModuleSubscriber notificationModuleSubscriber = new NotificationModuleSubscriber(null, null);
                notificationModuleSubscriber.generateNotificationTurnOffNotification(Calendar.getInstance().getTime(), SignInActivity.this);
            }
            if (getIntent().getStringExtra("from") != null && getIntent().getStringExtra("from").equalsIgnoreCase("StudyInfo")) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            } else {
                if (AppConfig.AppType.equalsIgnoreCase(getString(R.string.app_gateway))) {
                    Intent intent = new Intent(SignInActivity.this, StudyActivity.class);
                    ComponentName cn = intent.getComponent();
                    Intent mainIntent = Intent.makeRestartActivityTask(cn);
                    startActivity(mainIntent);
                    finish();
                } else {
                    Intent intent = new Intent(SignInActivity.this, StandaloneActivity.class);
                    ComponentName cn = intent.getComponent();
                    Intent mainIntent = Intent.makeRestartActivityTask(cn);
                    startActivity(mainIntent);
                    finish();
                }
            }
        } else {
            if (getIntent().getStringExtra("from") != null && getIntent().getStringExtra("from").equalsIgnoreCase("StudyInfo")) {
                Intent intent = new Intent(SignInActivity.this, VerificationStepActivity.class);
                intent.putExtra("from", "StudyInfo");
                intent.putExtra("type", "Signin");
                intent.putExtra("userid", loginData.getUserId());
                intent.putExtra("auth", loginData.getAuth());
                intent.putExtra("verified", loginData.isVerified());
                intent.putExtra("email", mEmail.getText().toString());
                intent.putExtra("password", mPassword.getText().toString());
                startActivityForResult(intent, STUDYINFO_REQUEST);
            } else {
                Intent intent = new Intent(SignInActivity.this, VerificationStepActivity.class);
                intent.putExtra("from", "Activity");
                intent.putExtra("type", "Signin");
                intent.putExtra("userid", loginData.getUserId());
                intent.putExtra("auth", loginData.getAuth());
                intent.putExtra("verified", loginData.isVerified());
                intent.putExtra("email", mEmail.getText().toString());
                intent.putExtra("password", mPassword.getText().toString());
                startActivity(intent);
            }
        }
    }

    @Override
    public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {
        AppController.getHelperProgressDialog().dismissDialog();
        if (responseCode == UPDATE_USER_PROFILE) {
            if (statusCode.equalsIgnoreCase("401")) {
                Toast.makeText(SignInActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                AppController.getHelperSessionExpired(SignInActivity.this, errormsg);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == STUDYINFO_REQUEST) {
            if (resultCode == RESULT_OK) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            }
        } else if (requestCode == PASSCODE_RESPONSE) {
            login();
        }
    }

    private void callUpdateProfileWebService(String deviceToken) {
        AppController.getHelperProgressDialog().showProgress(SignInActivity.this, "", "", false);
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

        RegistrationServerConfigEvent registrationServerConfigEvent = new RegistrationServerConfigEvent("post_object", URLs.UPDATE_USER_PROFILE, UPDATE_USER_PROFILE, this, UpdateUserProfileData.class, null, params, jsonObjBody, false, this);
        updateUserProfileEvent.setmRegistrationServerConfigEvent(registrationServerConfigEvent);
        UserModulePresenter userModulePresenter = new UserModulePresenter();
        userModulePresenter.performUpdateUserProfile(updateUserProfileEvent);
    }


    private void callUserProfileWebService() {
        HashMap<String, String> header = new HashMap<>();
        header.put("auth", mUserAuth);
        header.put("userId", mUserID);
        GetUserProfileEvent getUserProfileEvent = new GetUserProfileEvent();
        RegistrationServerConfigEvent registrationServerConfigEvent = new RegistrationServerConfigEvent("get", URLs.GET_USER_PROFILE, USER_PROFILE_REQUEST, SignInActivity.this, UserProfileData.class, null, header, null, false, this);
        getUserProfileEvent.setmRegistrationServerConfigEvent(registrationServerConfigEvent);
        UserModulePresenter userModulePresenter = new UserModulePresenter();
        userModulePresenter.performGetUserProfile(getUserProfileEvent);
    }
}
