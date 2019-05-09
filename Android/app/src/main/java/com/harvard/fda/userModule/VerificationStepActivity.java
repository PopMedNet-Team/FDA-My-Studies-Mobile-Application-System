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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.harvard.fda.R;
import com.harvard.fda.gatewayModule.GatewayActivity;
import com.harvard.fda.userModule.event.ResendEmailEvent;
import com.harvard.fda.userModule.event.VerifyUserEvent;
import com.harvard.fda.userModule.webserviceModel.LoginData;
import com.harvard.fda.utils.AppController;
import com.harvard.fda.utils.SharedPreferenceHelper;
import com.harvard.fda.utils.URLs;
import com.harvard.fda.webserviceModule.apiHelper.ApiCall;
import com.harvard.fda.webserviceModule.events.RegistrationServerConfigEvent;

import java.util.HashMap;

public class VerificationStepActivity extends AppCompatActivity implements ApiCall.OnAsyncRequestComplete {
    private AppCompatTextView mVerificationStepsLabel;
    private AppCompatTextView mVerificationEmailMsgLabel;
    private AppCompatTextView mTapBelowTxtLabel;
    private AppCompatTextView mSubmitBtn;
    private AppCompatTextView mHrLine1;
    private AppCompatTextView mCancelTxt;
    private AppCompatTextView mResend;
    private AppCompatEditText mEmailField;
    private AppCompatEditText mVerificationCode;
    private RelativeLayout mBackBtn;
    private RelativeLayout mCancelBtn;
    final int CONFIRM_REGISTER_USER_RESPONSE = 100;
    final int RESEND_CONFIRMATION = 101;
    final int JOIN_STUDY_RESPONSE = 102;
    private String mFrom;
    private String mUserId;
    private String mAuth;
    private boolean isVerified;
    private String mEmailId;
    private String mType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification_step);
        mUserId = getIntent().getStringExtra("userid");
        mAuth = getIntent().getStringExtra("auth");
        isVerified = getIntent().getBooleanExtra("verified", false);
        mEmailId = getIntent().getStringExtra("email");
        mFrom = getIntent().getStringExtra("from");
        mType = getIntent().getStringExtra("type");


        initializeXMLId();
        mHrLine1.setVisibility(View.GONE);
        mEmailField.setVisibility(View.GONE);
//        }
        setTextForView();
        setFont();

        bindEvents();
    }

    private void initializeXMLId() {
        mBackBtn = (RelativeLayout) findViewById(R.id.backBtn);
        mCancelTxt = (AppCompatTextView) findViewById(R.id.cancelTxt);
        mResend = (AppCompatTextView) findViewById(R.id.resend);
        mHrLine1 = (AppCompatTextView) findViewById(R.id.vrLine1);
        mEmailField = (AppCompatEditText) findViewById(R.id.emailField);
        mVerificationCode = (AppCompatEditText) findViewById(R.id.verificationCode);
        mCancelBtn = (RelativeLayout) findViewById(R.id.cancelBtn);
        mVerificationStepsLabel = (AppCompatTextView) findViewById(R.id.verification_steps_label);
        mVerificationEmailMsgLabel = (AppCompatTextView) findViewById(R.id.verification_email_msg_label);
        mTapBelowTxtLabel = (AppCompatTextView) findViewById(R.id.tap_below_txt_label);
        mSubmitBtn = (AppCompatTextView) findViewById(R.id.submitButton);
    }

    private void setTextForView() {
        String msg = "";
        if (mType.equalsIgnoreCase("signup")) {
            msg = getResources().getString(R.string.verification_email_content1) + " " + mEmailId + getResources().getString(R.string.verification_email_content2);
        } else if (mType.equalsIgnoreCase("signin")) {
            msg = getResources().getString(R.string.verification_email_signin);
        } else {
            msg = getResources().getString(R.string.verification_email_forgotpassword) + "(" + mEmailId + ")" + getResources().getString(R.string.verification_email_forgotpassword1);

        }
        mVerificationEmailMsgLabel.setText(msg);
    }

    private void setFont() {
        try {
            mCancelTxt.setTypeface(AppController.getTypeface(VerificationStepActivity.this, "medium"));
            mVerificationStepsLabel.setTypeface(AppController.getTypeface(VerificationStepActivity.this, "regular"));
            mVerificationEmailMsgLabel.setTypeface(AppController.getTypeface(VerificationStepActivity.this, "regular"));
            mTapBelowTxtLabel.setTypeface(AppController.getTypeface(VerificationStepActivity.this, "regular"));
            mSubmitBtn.setTypeface(AppController.getTypeface(VerificationStepActivity.this, "regular"));
            mEmailField.setTypeface(AppController.getTypeface(VerificationStepActivity.this, "regular"));
            mVerificationCode.setTypeface(AppController.getTypeface(VerificationStepActivity.this, "regular"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindEvents() {
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences settings = SharedPreferenceHelper.getPreferences(VerificationStepActivity.this);
                settings.edit().clear().apply();
                // delete passcode from keystore
                String pass = AppController.refreshKeys("passcode");
                if (pass != null)
                    AppController.deleteKey("passcode_"+pass);
                finish();
            }
        });

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences settings = SharedPreferenceHelper.getPreferences(VerificationStepActivity.this);
                settings.edit().clear().apply();
                // delete passcode from keystore
                String pass = AppController.refreshKeys("passcode");
                if (pass != null)
                    AppController.deleteKey("passcode_"+pass);
                finish();
            }
        });

        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                VerifyUserEvent verifyUserEvent = new VerifyUserEvent();
                HashMap<String, String> params = new HashMap<>();
                HashMap<String, String> header = new HashMap<String, String>();
                if (mVerificationCode.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(VerificationStepActivity.this, getResources().getString(R.string.validation_code_error), Toast.LENGTH_SHORT).show();
                } else {
                    AppController.getHelperProgressDialog().showProgress(VerificationStepActivity.this, "", "", false);
                    params.put("emailId", mEmailId);
                    params.put("code", mVerificationCode.getText().toString());
                    RegistrationServerConfigEvent registrationServerConfigEvent = new RegistrationServerConfigEvent("post", URLs.CONFIRM_REGISTER_USER, CONFIRM_REGISTER_USER_RESPONSE, VerificationStepActivity.this, LoginData.class, params, header, null, false, VerificationStepActivity.this);
                    verifyUserEvent.setmRegistrationServerConfigEvent(registrationServerConfigEvent);
                    UserModulePresenter userModulePresenter = new UserModulePresenter();
                    userModulePresenter.performVerifyRegistration(verifyUserEvent);
                }

            }
        });

        mResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AppController.getHelperProgressDialog().showProgress(VerificationStepActivity.this, "", "", false);
                ResendEmailEvent resendEmailEvent = new ResendEmailEvent();
                HashMap<String, String> header = new HashMap<String, String>();

                HashMap<String, String> params = new HashMap<String, String>();
                params.put("emailId", mEmailId);
                RegistrationServerConfigEvent registrationServerConfigEvent = new RegistrationServerConfigEvent("post", URLs.RESEND_CONFIRMATION, RESEND_CONFIRMATION, VerificationStepActivity.this, LoginData.class, params, header, null, false, VerificationStepActivity.this);
                resendEmailEvent.setmRegistrationServerConfigEvent(registrationServerConfigEvent);
                UserModulePresenter userModulePresenter = new UserModulePresenter();
                userModulePresenter.performResendEmail(resendEmailEvent);
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public <T> void asyncResponse(T response, int responseCode) {
        AppController.getHelperProgressDialog().dismissDialog();
        if (responseCode == CONFIRM_REGISTER_USER_RESPONSE) {
            LoginData loginData = (LoginData) response;
            if (mFrom != null && mFrom.equalsIgnoreCase(ForgotPasswordActivity.FROM)) {
                Toast.makeText(this, getResources().getString(R.string.account_verification), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            } else if (mFrom != null && mFrom.equalsIgnoreCase("StudyInfo")) {
                AppController.getHelperSharedPreference().writePreference(VerificationStepActivity.this, getString(R.string.userid), "" + mUserId);
                AppController.getHelperSharedPreference().writePreference(VerificationStepActivity.this, getString(R.string.auth), "" + mAuth);
                AppController.getHelperSharedPreference().writePreference(VerificationStepActivity.this, getString(R.string.verified), "true");
                AppController.getHelperSharedPreference().writePreference(VerificationStepActivity.this, getString(R.string.email), "" + mEmailId);


                Intent intent = new Intent(VerificationStepActivity.this, NewPasscodeSetupActivity.class);
                intent.putExtra("from", "StudyInfo");
                startActivityForResult(intent, JOIN_STUDY_RESPONSE);

                AppController.getHelperSharedPreference().writePreference(VerificationStepActivity.this, getString(R.string.initialpasscodeset), "NO");
            } else {
                AppController.getHelperSharedPreference().writePreference(VerificationStepActivity.this, getString(R.string.userid), "" + mUserId);
                AppController.getHelperSharedPreference().writePreference(VerificationStepActivity.this, getString(R.string.auth), "" + mAuth);
                AppController.getHelperSharedPreference().writePreference(VerificationStepActivity.this, getString(R.string.verified), "true");
                AppController.getHelperSharedPreference().writePreference(VerificationStepActivity.this, getString(R.string.email), "" + mEmailId);


                Intent intent = new Intent(VerificationStepActivity.this, NewPasscodeSetupActivity.class);
                startActivity(intent);
                AppController.getHelperSharedPreference().writePreference(VerificationStepActivity.this, getString(R.string.initialpasscodeset), "NO");
            }
        } else if (responseCode == RESEND_CONFIRMATION) {
            Toast.makeText(this, getResources().getString(R.string.resend_success), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {
        AppController.getHelperProgressDialog().dismissDialog();
        if (responseCode == CONFIRM_REGISTER_USER_RESPONSE) {
            if (statusCode.equalsIgnoreCase("401")) {
                Toast.makeText(this, errormsg, Toast.LENGTH_SHORT).show();
                if (mFrom != null && mFrom.equalsIgnoreCase("Activity")) {
                    SharedPreferences settings = SharedPreferenceHelper.getPreferences(VerificationStepActivity.this);
                    settings.edit().clear().apply();
// delete passcode from keystore
                    String pass = AppController.refreshKeys("passcode");
                    if (pass != null)
                        AppController.deleteKey("passcode_"+pass);
                    Intent intent = new Intent(VerificationStepActivity.this, GatewayActivity.class);
                    ComponentName cn = intent.getComponent();
                    Intent mainIntent = Intent.makeRestartActivityTask(cn);
                    startActivity(mainIntent);
                    finish();
                } else {
                    AppController.getHelperSessionExpired(VerificationStepActivity.this, errormsg);
                }
            } else {
                Toast.makeText(this, errormsg, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (mFrom.equalsIgnoreCase(ForgotPasswordActivity.FROM)) {
            super.onBackPressed();
            SharedPreferences settings = SharedPreferenceHelper.getPreferences(VerificationStepActivity.this);
            settings.edit().clear().apply();
            // delete passcode from keystore
            String pass = AppController.refreshKeys("passcode");
            if (pass != null)
                AppController.deleteKey("passcode_"+pass);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == JOIN_STUDY_RESPONSE) {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
