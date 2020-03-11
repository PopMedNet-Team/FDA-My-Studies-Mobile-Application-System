package com.harvard.studyAppModule;

import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.harvard.AppConfig;
import com.harvard.R;
import com.harvard.userModule.UserModulePresenter;
import com.harvard.userModule.VerificationStepActivity;
import com.harvard.userModule.event.ChangePasswordEvent;
import com.harvard.userModule.webserviceModel.ChangePasswordData;
import com.harvard.utils.AppController;
import com.harvard.utils.SharedPreferenceHelper;
import com.harvard.utils.URLs;
import com.harvard.webserviceModule.apiHelper.ApiCall;
import com.harvard.webserviceModule.events.RegistrationServerConfigEvent;

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
    private String mPassword;
    private boolean isVerified;
    private String mEmailId;
    private boolean mClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserId = getIntent().getStringExtra("userid");
        mAuth = getIntent().getStringExtra("auth");
        isVerified = getIntent().getBooleanExtra("verified", false);
        mEmailId = getIntent().getStringExtra("email");
        try {
            mPassword = getIntent().getStringExtra("password");
        } catch (Exception e) {
            mPassword = "";
            e.printStackTrace();
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
        if (mPassword != null && !mPassword.equalsIgnoreCase("")) {
            mOldPassword.setText(mPassword);
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
            mOldPasswordLabel.setTypeface(AppController.getTypeface(ChangePasswordActivity.this, "regular"));
            mNewPasswordLabel.setTypeface(AppController.getTypeface(ChangePasswordActivity.this, "regular"));
            mConfirmPasswordLabel.setTypeface(AppController.getTypeface(ChangePasswordActivity.this, "regular"));
            mOldPassword.setTypeface(AppController.getTypeface(ChangePasswordActivity.this, "regular"));
            mNewPassword.setTypeface(AppController.getTypeface(ChangePasswordActivity.this, "regular"));
            mConfirmPassword.setTypeface(AppController.getTypeface(ChangePasswordActivity.this, "regular"));
            mSubmitButton.setTypeface(AppController.getTypeface(ChangePasswordActivity.this, "regular"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindEvents() {
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    AppController.getHelperHideKeyboard(ChangePasswordActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (mFrom != null && mFrom.equalsIgnoreCase("ProfileFragment")) {
                    finish();
                } else {
                    SharedPreferences settings = SharedPreferenceHelper.getPreferences(ChangePasswordActivity.this);
                    settings.edit().clear().apply();
                    // delete passcode from keystore
                    String pass = AppController.refreshKeys("passcode");
                    if (pass != null)
                        AppController.deleteKey("passcode_" + pass);
                    finish();
                }
            }
        });


        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClicked == false) {
                    mClicked = true;
                    String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!\"#$%&'()*+,-.:;<=>?@\\[\\]^_`{|}~])(?=\\S+$).{8,64}$";
                    if (mNewPassword.getText().toString().equalsIgnoreCase("") && mOldPassword.getText().toString().equalsIgnoreCase("") && mConfirmPassword.getText().toString().equalsIgnoreCase("")) {
                        Toast.makeText(ChangePasswordActivity.this, getResources().getString(R.string.enter_all_field_empty), Toast.LENGTH_SHORT).show();
                    } else if (mOldPassword.getText().toString().equalsIgnoreCase("")) {
                        Toast.makeText(ChangePasswordActivity.this, getResources().getString(R.string.password_old_empty), Toast.LENGTH_SHORT).show();
                    } else if (mNewPassword.getText().toString().equalsIgnoreCase("")) {
                        Toast.makeText(ChangePasswordActivity.this, getResources().getString(R.string.password_new_empty), Toast.LENGTH_SHORT).show();
                    } else if (!mNewPassword.getText().toString().matches(PASSWORD_PATTERN)) {
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
        if (mPassword != null && mPassword.equalsIgnoreCase("")) {
            params.put("currentPassword", mPassword);
        } else {
            params.put("currentPassword", mOldPassword.getText().toString());
        }
        params.put("newPassword", mNewPassword.getText().toString());


        RegistrationServerConfigEvent registrationServerConfigEvent = new RegistrationServerConfigEvent("post", URLs.CHANGE_PASSWORD, CHANGE_PASSWORD_REQUEST, ChangePasswordActivity.this, ChangePasswordData.class, params, header, null, false, ChangePasswordActivity.this);
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
            e.printStackTrace();
        }
        if (mFrom != null && mFrom.equalsIgnoreCase("ProfileFragment")) {
            Toast.makeText(this, getResources().getString(R.string.password_change_message), Toast.LENGTH_SHORT).show();
            finish();
        } else {
            AppController.getHelperSharedPreference().writePreference(ChangePasswordActivity.this, getString(R.string.userid), "" + mUserId);
            AppController.getHelperSharedPreference().writePreference(ChangePasswordActivity.this, getString(R.string.auth), "" + mAuth);
            AppController.getHelperSharedPreference().writePreference(ChangePasswordActivity.this, getString(R.string.verified), "" + isVerified);
            AppController.getHelperSharedPreference().writePreference(ChangePasswordActivity.this, getString(R.string.email), "" + mEmailId);
            if (isVerified) {
                Toast.makeText(this, getResources().getString(R.string.password_change_message), Toast.LENGTH_SHORT).show();
                if (AppConfig.AppType.equalsIgnoreCase(getString(R.string.app_gateway))) {
                    Intent intent = new Intent(ChangePasswordActivity.this, StudyActivity.class);
                    ComponentName cn = intent.getComponent();
                    Intent mainIntent = Intent.makeRestartActivityTask(cn);
                    startActivity(mainIntent);
                    finish();
                } else {
                    Intent intent = new Intent(ChangePasswordActivity.this, StandaloneActivity.class);
                    ComponentName cn = intent.getComponent();
                    Intent mainIntent = Intent.makeRestartActivityTask(cn);
                    startActivity(mainIntent);
                    finish();
                }
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
            e.printStackTrace();
        }
        if (mFrom != null && mFrom.equalsIgnoreCase("ProfileFragment")) {
            finish();
        } else {
            SharedPreferences settings = SharedPreferenceHelper.getPreferences(ChangePasswordActivity.this);
            settings.edit().clear().apply();
            // delete passcode from keystore
            String pass = AppController.refreshKeys("passcode");
            if (pass != null)
                AppController.deleteKey("passcode_" + pass);
            finish();
        }
    }
}
