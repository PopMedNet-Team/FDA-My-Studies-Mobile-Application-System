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
package com.hphc.mystudies.userModule;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.hphc.mystudies.R;
import com.hphc.mystudies.userModule.event.ForgotPasswordEvent;
import com.hphc.mystudies.userModule.webserviceModel.ForgotPasswordData;
import com.hphc.mystudies.utils.AppController;
import com.hphc.mystudies.utils.URLs;
import com.hphc.mystudies.webserviceModule.apiHelper.ApiCall;
import com.hphc.mystudies.webserviceModule.events.RegistrationServerConfigEvent;

import java.util.HashMap;

public class ForgotPasswordActivity extends AppCompatActivity implements ApiCall.OnAsyncRequestComplete {

    private static final int FORGOT_PASSWORD_REQUEST = 10;
    private RelativeLayout mBackBtn;
    private AppCompatTextView mTitle;
    private RelativeLayout mCancelBtn;
    private AppCompatTextView mCancelTxt;
    private AppCompatEditText mEmail;
    private AppCompatTextView mSubmitButton;
    private static final int RESEND_CONFIRMATION = 101;
    public static final String FROM = "ForgotPasswordActivity";
    private static final int GO_TO_SIGNIN = 111;

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
        }
    }

    private void bindEvents() {
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    AppController.getHelperHideKeyboard(ForgotPasswordActivity.this);
                } catch (Exception e) {
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
        RegistrationServerConfigEvent registrationServerConfigEvent = new RegistrationServerConfigEvent("post", URLs.FORGOT_PASS, FORGOT_PASSWORD_REQUEST, ForgotPasswordActivity.this, ForgotPasswordData.class, params, null, null, false, ForgotPasswordActivity.this);
        forgotPasswordEvent.setmRegistrationServerConfigEvent(registrationServerConfigEvent);
        UserModulePresenter userModulePresenter = new UserModulePresenter();
        userModulePresenter.performForgotPassword(forgotPasswordEvent);
    }

    @Override
    public <T> void asyncResponse(T response, int responseCode) {
        AppController.getHelperProgressDialog().dismissDialog();
        if (responseCode != RESEND_CONFIRMATION) {
            try {
                AppController.getHelperHideKeyboard(ForgotPasswordActivity.this);
            } catch (Exception e) {
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
        if (requestCode == GO_TO_SIGNIN && resultCode == RESULT_OK) {
            try {
                AppController.getHelperHideKeyboard(ForgotPasswordActivity.this);
            } catch (Exception e) {
            }
            finish();
        }
    }
}
