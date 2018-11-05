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
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hphc.mystudies.R;
import com.hphc.mystudies.userModule.UserModulePresenter;
import com.hphc.mystudies.userModule.VerificationStepActivity;
import com.hphc.mystudies.userModule.event.ChangePasswordEvent;
import com.hphc.mystudies.userModule.webserviceModel.ChangePasswordData;
import com.hphc.mystudies.utils.AppController;
import com.hphc.mystudies.utils.SharedPreferenceHelper;
import com.hphc.mystudies.utils.URLs;
import com.hphc.mystudies.webserviceModule.apiHelper.ApiCall;
import com.hphc.mystudies.webserviceModule.events.RegistrationServerConfigEvent;

import java.util.HashMap;

public class ChangePasswordActivity extends AppCompatActivity implements ApiCall.OnAsyncRequestComplete {

    private static final int CHANGE_PASSWORD_REQUEST = 8;
    private RelativeLayout mBackBtn;
    private RelativeLayout mRelpassword;
    private AppCompatTextView mTitle;
    private AppCompatEditText mOldPassword;
    private AppCompatEditText mConfirmPassword;
    private AppCompatEditText mNewPassword;
    private AppCompatTextView mOldPasswordLabel;
    private AppCompatTextView mConfirmPasswordLabel;
    private AppCompatTextView mNewPasswordLabel;
    private AppCompatTextView mSubmitButton;
    private String mFrom = null;
    private String mUserId;
    private String mAuth;
    private String mPass;
    private boolean isVerified;
    private String mEmailId;
    private boolean mClicked;
    private static final String REGULAR = "regular";
    private static final String PROFILEFRAGMENT = "ProfileFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserId = getIntent().getStringExtra("userid");
        mAuth = getIntent().getStringExtra("auth");
        isVerified = getIntent().getBooleanExtra("verified", false);
        mEmailId = getIntent().getStringExtra("email");
        try {
            mPass = getIntent().getStringExtra("password");
        } catch (Exception e) {
            mPass = "";
        }
        setContentView(R.layout.activity_change_password);
        mClicked = false;
        mFrom = getIntent().getStringExtra("from");
        initializeXMLId();
        setTextForView();
        setFont();
        bindEvents();
    }

    private void initializeXMLId() {
        mBackBtn = (RelativeLayout) findViewById(R.id.backBtn);
        mRelpassword = (RelativeLayout) findViewById(R.id.rel_password);
        mTitle = (AppCompatTextView) findViewById(R.id.title);
        mOldPasswordLabel = (AppCompatTextView) findViewById(R.id.oldpassword_label);
        mNewPasswordLabel = (AppCompatTextView) findViewById(R.id.password_label_new);
        mConfirmPasswordLabel = (AppCompatTextView) findViewById(R.id.password_label_confirm);
        mOldPassword = (AppCompatEditText) findViewById(R.id.edittxt_oldpassword);
        mNewPassword = (AppCompatEditText) findViewById(R.id.edittxt_password_new);
        mConfirmPassword = (AppCompatEditText) findViewById(R.id.edittxt_password_confirm);
        mSubmitButton = (AppCompatTextView) findViewById(R.id.submitButton);
    }

    private void setTextForView() {
        if (mPass != null && !mPass.equalsIgnoreCase("")) {
            mOldPassword.setText(mPass);
            mOldPassword.setFocusable(false);
            mOldPassword.setClickable(false);
            mTitle.setText(getResources().getString(R.string.change_password_heading1));
            mRelpassword.setVisibility(View.GONE);
        } else {
            mRelpassword.setVisibility(View.VISIBLE);
            mTitle.setText(getResources().getString(R.string.change_password_heading));
        }
    }

    private void setFont() {
        try {
            mTitle.setTypeface(AppController.getTypeface(ChangePasswordActivity.this, "medium"));
            mOldPasswordLabel.setTypeface(AppController.getTypeface(ChangePasswordActivity.this, REGULAR));
            mNewPasswordLabel.setTypeface(AppController.getTypeface(ChangePasswordActivity.this, REGULAR));
            mConfirmPasswordLabel.setTypeface(AppController.getTypeface(ChangePasswordActivity.this, REGULAR));
            mOldPassword.setTypeface(AppController.getTypeface(ChangePasswordActivity.this, REGULAR));
            mNewPassword.setTypeface(AppController.getTypeface(ChangePasswordActivity.this, REGULAR));
            mConfirmPassword.setTypeface(AppController.getTypeface(ChangePasswordActivity.this, REGULAR));
            mSubmitButton.setTypeface(AppController.getTypeface(ChangePasswordActivity.this, REGULAR));
        } catch (Exception e) {
        }
    }

    private void bindEvents() {
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    AppController.getHelperHideKeyboard(ChangePasswordActivity.this);
                } catch (Exception e) {
                }
                if (mFrom != null && mFrom.equalsIgnoreCase(PROFILEFRAGMENT)) {
                    finish();
                } else {
                    SharedPreferences settings = SharedPreferenceHelper.getPreferences(ChangePasswordActivity.this);
                    settings.edit().clear().apply();
                    // delete passcode from keystore
                    String pass = AppController.refreshKeys("passcode");
                    if (pass != null)
                        AppController.deleteKey("passcode_"+pass);
                    finish();
                }
            }
        });


        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mClicked) {
                    mClicked = true;
                    String PASS_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!\"#$%&'()*+,-.:;<=>?@\\[\\]^_`{|}~])(?=\\S+$).{8,64}$";
                    if (mNewPassword.getText().toString().equalsIgnoreCase("") && mOldPassword.getText().toString().equalsIgnoreCase("") && mConfirmPassword.getText().toString().equalsIgnoreCase("")) {
                        Toast.makeText(ChangePasswordActivity.this, getResources().getString(R.string.enter_all_field_empty), Toast.LENGTH_SHORT).show();
                    } else if (mOldPassword.getText().toString().equalsIgnoreCase("")) {
                        Toast.makeText(ChangePasswordActivity.this, getResources().getString(R.string.password_old_empty), Toast.LENGTH_SHORT).show();
                    } else if (mNewPassword.getText().toString().equalsIgnoreCase("")) {
                        Toast.makeText(ChangePasswordActivity.this, getResources().getString(R.string.password_new_empty), Toast.LENGTH_SHORT).show();
                    } else if (!mNewPassword.getText().toString().matches(PASS_PATTERN)) {
                        Toast.makeText(ChangePasswordActivity.this, getResources().getString(R.string.password_validation), Toast.LENGTH_SHORT).show();
                    } else if (checkPasswordContainsEmailID(mNewPassword.getText().toString())) {
                        Toast.makeText(ChangePasswordActivity.this, getResources().getString(R.string.password_contain_email), Toast.LENGTH_SHORT).show();
                    } else if (mConfirmPassword.getText().toString().equalsIgnoreCase("")) {
                        Toast.makeText(ChangePasswordActivity.this, getResources().getString(R.string.confirm_password_empty), Toast.LENGTH_SHORT).show();
                    }
                    else if (!mConfirmPassword.getText().toString().equals(mNewPassword.getText().toString())) {
                        Toast.makeText(ChangePasswordActivity.this, getResources().getString(R.string.password_mismatch_error1), Toast.LENGTH_SHORT).show();
                    } else {
                        AppController.getHelperProgressDialog().showProgress(ChangePasswordActivity.this, "", "", false);
                        callChangePasswordWebService();
                    }
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mClicked = false;
                        }
                    }, 2000);
                }

            }

        });

    }

    private boolean checkPasswordContainsEmailID(String password) {
        if (password.contains(mEmailId)) {
            return true;
        } else {
            return false;
        }
    }

    private void callChangePasswordWebService() {
        ChangePasswordEvent changePasswordEvent = new ChangePasswordEvent();

        HashMap<String, String> header = new HashMap<>();
        header.put("auth", mAuth);
        header.put("userId", mUserId);

        HashMap<String, String> params = new HashMap<>();
        if (mPass != null && mPass.equalsIgnoreCase("")) {
            params.put("currentPassword", mPass);
        } else {
            params.put("currentPassword", mOldPassword.getText().toString());
        }
        params.put("newPassword", mNewPassword.getText().toString());


        RegistrationServerConfigEvent registrationServerConfigEvent = new RegistrationServerConfigEvent("post", URLs.CHANGE_PASS, CHANGE_PASSWORD_REQUEST, ChangePasswordActivity.this, ChangePasswordData.class, params, header, null, false, ChangePasswordActivity.this);
        changePasswordEvent.setmRegistrationServerConfigEvent(registrationServerConfigEvent);
        UserModulePresenter userModulePresenter = new UserModulePresenter();
        userModulePresenter.performChangePassword(changePasswordEvent);
    }

    @Override
    public <T> void asyncResponse(T response, int responseCode) {
        AppController.getHelperProgressDialog().dismissDialog();
        try {
            AppController.getHelperHideKeyboard(ChangePasswordActivity.this);
        } catch (Exception e) {
        }
        if (mFrom != null && mFrom.equalsIgnoreCase(PROFILEFRAGMENT)) {
            Toast.makeText(this, getResources().getString(R.string.password_change_message), Toast.LENGTH_SHORT).show();
            finish();
        } else {
            AppController.getHelperSharedPreference().writePreference(ChangePasswordActivity.this, getString(R.string.userid), "" + mUserId);
            AppController.getHelperSharedPreference().writePreference(ChangePasswordActivity.this, getString(R.string.auth), "" + mAuth);
            AppController.getHelperSharedPreference().writePreference(ChangePasswordActivity.this, getString(R.string.verified), "" + isVerified);
            AppController.getHelperSharedPreference().writePreference(ChangePasswordActivity.this, getString(R.string.email), "" + mEmailId);
            if (isVerified) {
                Toast.makeText(this, getResources().getString(R.string.password_change_message), Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ChangePasswordActivity.this, StudyActivity.class);
                ComponentName cn = intent.getComponent();
                Intent mainIntent = IntentCompat.makeRestartActivityTask(cn);
                startActivity(mainIntent);
                finish();
            } else {
                Intent intent = new Intent(ChangePasswordActivity.this, VerificationStepActivity.class);
                intent.putExtra("userid", mUserId);
                intent.putExtra("auth", mAuth);
                intent.putExtra("verified", isVerified);
                intent.putExtra("email", mEmailId);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {
        AppController.getHelperProgressDialog().dismissDialog();
        if (statusCode.equalsIgnoreCase("401")) {
            Toast.makeText(ChangePasswordActivity.this, errormsg, Toast.LENGTH_SHORT).show();
            AppController.getHelperSessionExpired(ChangePasswordActivity.this, errormsg);
        } else {
            Toast.makeText(this, errormsg, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed() {
        try {
            AppController.getHelperHideKeyboard(ChangePasswordActivity.this);
        } catch (Exception e) {
        }
        if (mFrom != null && mFrom.equalsIgnoreCase(PROFILEFRAGMENT)) {
            finish();
        } else {
            SharedPreferences settings = SharedPreferenceHelper.getPreferences(ChangePasswordActivity.this);
            settings.edit().clear().apply();
            // delete passcode from keystore
            String pass = AppController.refreshKeys("passcode");
            if (pass != null)
                AppController.deleteKey("passcode_"+pass);
            finish();
        }
    }
}
