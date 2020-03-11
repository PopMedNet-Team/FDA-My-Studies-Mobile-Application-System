package com.harvard.userModule;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.harvard.R;
import com.harvard.passcodeModule.PasscodeView;
import com.harvard.utils.AppController;


public class NewPasscodeSetupActivity extends AppCompatActivity {

    private static final int SIGNIN_RESPONSE = 102;
    private RelativeLayout mBackBtn;
    private AppCompatTextView mTitle;
    private RelativeLayout mCancelBtn;
    private AppCompatTextView mCancelTxt;
    private PasscodeView mPasscodeView;
    private int JOIN_STUDY_RESPONSE = 100;
    private int PROFILE_RESPONSE = 101;
    TextView forgot;
    TextView mPasscodetitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passcode_setup);

        initializeXMLId();
        setTextForView();
        setFont();
        bindEvent();
        mBackBtn.setVisibility(View.GONE);
    }

    private void initializeXMLId() {
        mBackBtn = (RelativeLayout) findViewById(R.id.backBtn);
        mTitle = (AppCompatTextView) findViewById(R.id.title);
        mCancelBtn = (RelativeLayout) findViewById(R.id.cancelBtn);
        mCancelTxt = (AppCompatTextView) findViewById(R.id.cancelTxt);
        mPasscodeView = (PasscodeView) findViewById(R.id.passcode_view);
        mPasscodetitle = (TextView) findViewById(R.id.passcodetitle);
        forgot = (TextView) findViewById(R.id.forgot);
        forgot.setVisibility(View.GONE);
    }

    private void setTextForView() {
        mCancelBtn.setVisibility(View.GONE);
        mTitle.setText(getResources().getString(R.string.setup_passcode));
    }

    private void setFont() {
        try {
            mTitle.setTypeface(AppController.getTypeface(NewPasscodeSetupActivity.this, "medium"));
            mPasscodetitle.setTypeface(AppController.getTypeface(NewPasscodeSetupActivity.this, "regular"));
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

        mPasscodeView.setPasscodeEntryListener(new PasscodeView.PasscodeEntryListener() {
            @Override
            public void onPasscodeEntered(String passcode) {
                if (getIntent().getStringExtra("from") != null && getIntent().getStringExtra("from").equalsIgnoreCase("StudyInfo")) {
                    mPasscodeView.clearText();
                    Intent intent = new Intent(NewPasscodeSetupActivity.this, ConfirmPasscodeSetup.class);
                    intent.putExtra("from", "StudyInfo");
                    intent.putExtra("passcode", "" + passcode);
                    startActivityForResult(intent, JOIN_STUDY_RESPONSE);
                } else if (getIntent().getStringExtra("from") != null && getIntent().getStringExtra("from").equalsIgnoreCase("profile")) {
                    mPasscodeView.clearText();
                    Intent intent = new Intent(NewPasscodeSetupActivity.this, ConfirmPasscodeSetup.class);
                    intent.putExtra("from", "profile");
                    intent.putExtra("passcode", "" + passcode);
                    startActivityForResult(intent, PROFILE_RESPONSE);
                } else if (getIntent().getStringExtra("from") != null && getIntent().getStringExtra("from").equalsIgnoreCase("profile_change")) {
                    mPasscodeView.clearText();
                    Intent intent = new Intent(NewPasscodeSetupActivity.this, ConfirmPasscodeSetup.class);
                    intent.putExtra("from", "profile");
                    intent.putExtra("passcode", "" + passcode);
                    startActivityForResult(intent, PROFILE_RESPONSE);
                } else if (getIntent().getStringExtra("from") != null && getIntent().getStringExtra("from").equalsIgnoreCase("signin")) {
                    mPasscodeView.clearText();
                    Intent intent = new Intent(NewPasscodeSetupActivity.this, ConfirmPasscodeSetup.class);
                    intent.putExtra("from", "signin");
                    intent.putExtra("passcode", "" + passcode);
                    startActivityForResult(intent, SIGNIN_RESPONSE);
                } else {
                    mPasscodeView.clearText();
                    Intent intent = new Intent(NewPasscodeSetupActivity.this, ConfirmPasscodeSetup.class);
                    intent.putExtra("passcode", "" + passcode);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == JOIN_STUDY_RESPONSE) {
            if (resultCode == RESULT_OK) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            } else {
                mPasscodeView.clearText();
            }
        } else if (requestCode == PROFILE_RESPONSE) {
            if (resultCode == RESULT_OK) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            } else {
                mPasscodeView.clearText();
            }
        } else if (requestCode == SIGNIN_RESPONSE) {
            if (resultCode == RESULT_OK) {
                Intent intent = new Intent();
                setResult(RESULT_OK, intent);
                finish();
            } else {
                mPasscodeView.clearText();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if ((getIntent().getStringExtra("from") != null && getIntent().getStringExtra("from").equalsIgnoreCase("profile")) || (getIntent().getStringExtra("from") != null && getIntent().getStringExtra("from").equalsIgnoreCase("profile_change"))) {
            super.onBackPressed();
        }
    }
}
