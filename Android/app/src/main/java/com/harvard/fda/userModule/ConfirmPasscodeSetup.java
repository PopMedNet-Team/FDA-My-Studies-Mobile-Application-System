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

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.harvard.fda.R;
import com.harvard.fda.passcodeModule.PasscodeView;
import com.harvard.fda.utils.AppController;

public class ConfirmPasscodeSetup extends AppCompatActivity {

    private RelativeLayout mBackBtn;
    private AppCompatTextView mTitle;
    private RelativeLayout mCancelBtn;
    private AppCompatTextView mCancelTxt;
    private PasscodeView mPasscodeView;
    private int JOIN_STUDY_RESPONSE = 100;
    TextView mPasscodetitle;
    TextView mPasscodedesc;
    TextView forgot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passcode_setup);

        initializeXMLId();
        setTextForView();
        setFont();
        bindEvent();
        mTitle.setText(R.string.confirmPascode);
    }

    private void initializeXMLId() {
        mBackBtn = (RelativeLayout) findViewById(R.id.backBtn);
        mTitle = (AppCompatTextView) findViewById(R.id.title);
        mCancelBtn = (RelativeLayout) findViewById(R.id.cancelBtn);
        mCancelTxt = (AppCompatTextView) findViewById(R.id.cancelTxt);
        mPasscodeView = (PasscodeView) findViewById(R.id.passcode_view);
        mPasscodetitle = (TextView) findViewById(R.id.passcodetitle);
        mPasscodedesc = (TextView) findViewById(R.id.passcodedesc);


        forgot = (TextView) findViewById(R.id.forgot);
        forgot.setVisibility(View.GONE);
    }

    private void setTextForView() {
        mCancelBtn.setVisibility(View.GONE);
        mPasscodetitle.setText(getResources().getString(R.string.passcode_confirm_reenter));
    }

    private void setFont() {
        try {
            mTitle.setTypeface(AppController.getTypeface(ConfirmPasscodeSetup.this, "medium"));
            mPasscodetitle.setTypeface(AppController.getTypeface(ConfirmPasscodeSetup.this, "regular"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindEvent() {
        mPasscodeView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mPasscodeView.requestToShowKeyboard();
            }
        }, 400);

        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mPasscodeView.setPasscodeEntryListener(new PasscodeView.PasscodeEntryListener() {
            @Override
            public void onPasscodeEntered(String passcode) {
                if (passcode.equalsIgnoreCase(getIntent().getStringExtra("passcode"))) {
                    AppController.getHelperHideKeyboard(ConfirmPasscodeSetup.this);
                    AppController.getHelperSharedPreference().writePreference(ConfirmPasscodeSetup.this, getString(R.string.initialpasscodeset), "Yes");
                    AppController.getHelperSharedPreference().writePreference(ConfirmPasscodeSetup.this, getString(R.string.usepasscode), "yes");
                    new CreateNewPasscode().execute(passcode);
                    if (getIntent().getStringExtra("from") != null && getIntent().getStringExtra("from").equalsIgnoreCase("StudyInfo")) {
                        mPasscodeView.clearText();
                        Intent intent = new Intent(ConfirmPasscodeSetup.this, SignupProcessCompleteActivity.class);
                        intent.putExtra("from", "StudyInfo");
                        startActivityForResult(intent, JOIN_STUDY_RESPONSE);
                    } else if (getIntent().getStringExtra("from") != null && getIntent().getStringExtra("from").equalsIgnoreCase("profile")) {
                        mPasscodeView.clearText();
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    } else if (getIntent().getStringExtra("from") != null && getIntent().getStringExtra("from").equalsIgnoreCase("profile_change")) {
                        mPasscodeView.clearText();
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    } else if (getIntent().getStringExtra("from") != null && getIntent().getStringExtra("from").equalsIgnoreCase("signin")) {
                        mPasscodeView.clearText();
                        Intent intent = new Intent();
                        setResult(RESULT_OK, intent);
                        finish();
                    } else {
                        mPasscodeView.clearText();
                        Intent intent = new Intent(ConfirmPasscodeSetup.this, SignupProcessCompleteActivity.class);
                        startActivity(intent);
                    }
                } else {
                    Toast.makeText(ConfirmPasscodeSetup.this, R.string.passcodeNotMatching, Toast.LENGTH_SHORT).show();
                    mPasscodeView.clearText();
                }
            }
        });
    }

    private class CreateNewPasscode extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String passcode = params[0];
            // delete passcode from keystore if already exist
            String pass = AppController.refreshKeys("passcode");
            if (pass != null)
                AppController.deleteKey("passcode_" + pass);
            // storing into keystore
            AppController.createNewKeys(ConfirmPasscodeSetup.this, "passcode_" + passcode);
            return null;
        }

        @Override
        protected void onPostExecute(String token) {
        }

        @Override
        protected void onPreExecute() {
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
