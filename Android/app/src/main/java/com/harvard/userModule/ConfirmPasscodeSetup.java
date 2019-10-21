package com.harvard.userModule;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.harvard.R;
import com.harvard.passcodeModule.PasscodeView;
import com.harvard.utils.AppController;

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
//                    AppController.getHelperSharedPreference().writePreference(ConfirmPasscodeSetup.this, getString(R.string.passcode), passcode);
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
