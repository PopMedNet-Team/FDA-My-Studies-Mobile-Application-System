package com.harvard.passcodeModule;

import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.harvard.R;
import com.harvard.gatewayModule.GatewayActivity;
import com.harvard.storageModule.DBServiceSubscriber;
import com.harvard.utils.AppController;
import com.harvard.utils.SharedPreferenceHelper;

public class PasscodeSetupActivity extends AppCompatActivity {
    private RelativeLayout mBackBtn;
    private AppCompatTextView mTitle, hrLine1;
    private RelativeLayout mCancelBtn;
    private AppCompatTextView mCancelTxt;
    private PasscodeView mPasscodeView;
    TextView forgot;
    TextView mPasscodeTitle;
    TextView mPasscodeDesc;
    DBServiceSubscriber dbServiceSubscriber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passcode_setup);
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dbServiceSubscriber = new DBServiceSubscriber();
        initializeXMLId();
        setTextForView();
        setFont();
        bindEvent();

    }

    private void initializeXMLId() {
        mBackBtn = (RelativeLayout) findViewById(R.id.backBtn);
        mTitle = (AppCompatTextView) findViewById(R.id.title);
        hrLine1 = (AppCompatTextView) findViewById(R.id.hrLine1);
        mCancelBtn = (RelativeLayout) findViewById(R.id.cancelBtn);
        mCancelTxt = (AppCompatTextView) findViewById(R.id.cancelTxt);
        mPasscodeView = (PasscodeView) findViewById(R.id.passcode_view);
        mPasscodeTitle = (TextView) findViewById(R.id.passcodetitle);

        forgot = (TextView) findViewById(R.id.forgot);
        mPasscodeDesc = (TextView) findViewById(R.id.passcodedesc);
    }

    private void setTextForView() {
        mCancelBtn.setVisibility(View.GONE);
        hrLine1.setVisibility(View.GONE);
        mPasscodeDesc.setVisibility(View.INVISIBLE);
        mTitle.setText("");
        mPasscodeTitle.setText(getString(R.string.enter_your_passcode));
    }

    private void setFont() {
        try {
            mTitle.setTypeface(AppController.getTypeface(PasscodeSetupActivity.this, "medium"));
            mPasscodeTitle.setTypeface(AppController.getTypeface(PasscodeSetupActivity.this, "regular"));
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

        mBackBtn.setVisibility(View.GONE);
        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                You will be signed out and will need to sign in again. Are you sure you want to proceed?
                AlertDialog.Builder adb = new AlertDialog.Builder(PasscodeSetupActivity.this);
                adb.setTitle(getResources().getString(R.string.app_name));
                adb.setIcon(android.R.drawable.ic_dialog_alert);
                adb.setMessage(R.string.forgotpasscodemsg);
                adb.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        getHelperLogout();
                    }
                });


                adb.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                adb.show();
            }
        });

        mPasscodeView.setPasscodeEntryListener(new PasscodeView.PasscodeEntryListener() {
            @Override
            public void onPasscodeEntered(String passcode) {
                // chk with keystore passcode
                if (passcode.equalsIgnoreCase(AppController.refreshKeys("passcode"))) {
                    AppController.getHelperSharedPreference().writePreference(getApplicationContext(), "passcodeAnswered", "yes");
                    AppController.getHelperHideKeyboard(PasscodeSetupActivity.this);
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    Toast.makeText(PasscodeSetupActivity.this, R.string.invalidcode, Toast.LENGTH_SHORT).show();
                    mPasscodeView.clearText();
                }
            }
        });
    }

    public void getHelperLogout() {
        SharedPreferences settings = SharedPreferenceHelper.getPreferences(PasscodeSetupActivity.this);
        settings.edit().clear().apply();
// delete passcode from keystore
        String pass = AppController.refreshKeys("passcode");
        if (pass != null)
            AppController.deleteKey("passcode_" + pass);

        dbServiceSubscriber.deleteDb(this);

        Intent intent = new Intent(PasscodeSetupActivity.this, GatewayActivity.class);
        ComponentName cn = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(cn);
        mainIntent.putExtra("from", "forgot");
        startActivity(mainIntent);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    public void onBackPressed() {
        if (getIntent().getStringExtra("from") != null && getIntent().getStringExtra("from").equalsIgnoreCase("profile")) {
            super.onBackPressed();
        }
    }
}
