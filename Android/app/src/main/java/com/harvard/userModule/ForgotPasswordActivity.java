package com.harvard.userModule;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.harvard.R;
import com.harvard.userModule.event.ForgotPasswordEvent;
import com.harvard.userModule.webserviceModel.ForgotPasswordData;
import com.harvard.utils.AppController;
import com.harvard.utils.URLs;
import com.harvard.webserviceModule.apiHelper.ApiCall;
import com.harvard.webserviceModule.events.RegistrationServerConfigEvent;

import java.util.HashMap;

public class ForgotPasswordActivity extends AppCompatActivity implements ApiCall.OnAsyncRequestComplete {

    private static final int FORGOT_PASSWORD_REQUEST = 10;
    private RelativeLayout mBackBtn;
    private AppCompatTextView mTitle;
    private RelativeLayout mCancelBtn;
    private AppCompatTextView mCancelTxt;
    private AppCompatEditText mEmail;
    private AppCompatTextView mSubmitButton;
    final int RESEND_CONFIRMATION = 101;
    public static String FROM = "ForgotPasswordActivity";
    private int GO_TO_SIGNIN = 111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        initializeXMLId();
        setTextForView();
        setFont();
        bindEvents();

    }

    private void initializeXMLId() {
        mBackBtn = (RelativeLayout) findViewById(R.id.backBtn);
        mTitle = (AppCompatTextView) findViewById(R.id.title);
        mCancelBtn = (RelativeLayout) findViewById(R.id.cancelBtn);
        mCancelTxt = (AppCompatTextView) findViewById(R.id.cancelTxt);
        mEmail = (AppCompatEditText) findViewById(R.id.edittxt_email);
        mSubmitButton = (AppCompatTextView) findViewById(R.id.submitButton);
    }

    private void setTextForView() {
        mCancelBtn.setVisibility(View.GONE);
        mTitle.setText(getResources().getString(R.string.forgot_password_heading));
    }

    private void setFont() {
        try {
            mTitle.setTypeface(AppController.getTypeface(ForgotPasswordActivity.this, "medium"));
            mCancelTxt.setTypeface(AppController.getTypeface(ForgotPasswordActivity.this, "medium"));
            mEmail.setTypeface(AppController.getTypeface(ForgotPasswordActivity.this, "regular"));
            mSubmitButton.setTypeface(AppController.getTypeface(ForgotPasswordActivity.this, "regular"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindEvents() {
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    AppController.getHelperHideKeyboard(ForgotPasswordActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finish();
            }
        });

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    AppController.getHelperHideKeyboard(ForgotPasswordActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finish();
            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mEmail.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(ForgotPasswordActivity.this, getResources().getString(R.string.email_empty), Toast.LENGTH_SHORT).show();
                } else if (!AppController.getHelperIsValidEmail(mEmail.getText().toString())) {
                    Toast.makeText(ForgotPasswordActivity.this, getResources().getString(R.string.email_validation), Toast.LENGTH_SHORT).show();
                } else {
                    AppController.getHelperProgressDialog().showProgress(ForgotPasswordActivity.this, "", "", false);
                    callForgotPasswordWebService();
                }
            }
        });

    }

    private void callForgotPasswordWebService() {

        HashMap<String, String> params = new HashMap<>();
        params.put("emailId", mEmail.getText().toString());

        ForgotPasswordEvent forgotPasswordEvent = new ForgotPasswordEvent();
        RegistrationServerConfigEvent registrationServerConfigEvent = new RegistrationServerConfigEvent("post", URLs.FORGOT_PASSWORD, FORGOT_PASSWORD_REQUEST, ForgotPasswordActivity.this, ForgotPasswordData.class, params, null, null, false, ForgotPasswordActivity.this);
        forgotPasswordEvent.setmRegistrationServerConfigEvent(registrationServerConfigEvent);
        UserModulePresenter userModulePresenter = new UserModulePresenter();
        userModulePresenter.performForgotPassword(forgotPasswordEvent);
    }

    @Override
    public <T> void asyncResponse(T response, int responseCode) {
        AppController.getHelperProgressDialog().dismissDialog();
        if (responseCode == RESEND_CONFIRMATION) {

        } else {
            try {
                AppController.getHelperHideKeyboard(ForgotPasswordActivity.this);
            } catch (Exception e) {
                e.printStackTrace();
            }
            Toast.makeText(this, getResources().getString(R.string.forgot_password_error), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {
        AppController.getHelperProgressDialog().dismissDialog();
        if (statusCode.equalsIgnoreCase("401")) {
            Toast.makeText(ForgotPasswordActivity.this, errormsg, Toast.LENGTH_SHORT).show();
            AppController.getHelperSessionExpired(ForgotPasswordActivity.this, errormsg);
        } else if (statusCode.equalsIgnoreCase("403")) {
            Intent intent = new Intent(ForgotPasswordActivity.this, VerificationStepActivity.class);
            intent.putExtra("from", FROM);
            intent.putExtra("type", "ForgotPasswordActivity");
            intent.putExtra("userid", "");
            intent.putExtra("auth", "");
            intent.putExtra("verified", false);
            intent.putExtra("email", mEmail.getText().toString());
            startActivityForResult(intent, GO_TO_SIGNIN);
        } else {
            Toast.makeText(this, errormsg, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GO_TO_SIGNIN) {
            if (resultCode == RESULT_OK) {
                try {
                    AppController.getHelperHideKeyboard(ForgotPasswordActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finish();
            }
        }
    }
}
