package com.harvard.userModule;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;

import com.harvard.AppConfig;
import com.harvard.R;
import com.harvard.studyAppModule.StandaloneActivity;
import com.harvard.studyAppModule.StudyActivity;
import com.harvard.utils.AppController;

public class SignupProcessCompleteActivity extends AppCompatActivity {
    private AppCompatTextView mCongratsLabel;
    private AppCompatTextView mNextButton;
    private AppCompatTextView mSignupCompleteLabel;
    private String mFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_process_complete);
        mFrom = getIntent().getStringExtra("from");

        initializeXMLId();
        setFont();
        bindEvents();
    }

    private void initializeXMLId() {
        mCongratsLabel = (AppCompatTextView) findViewById(R.id.congrats_label);
        mSignupCompleteLabel = (AppCompatTextView) findViewById(R.id.signup_complete_txt_label);
        mNextButton = (AppCompatTextView) findViewById(R.id.nextButton);

    }

    private void setFont() {
        try {
            mCongratsLabel.setTypeface(AppController.getTypeface(SignupProcessCompleteActivity.this, "regular"));
            mSignupCompleteLabel.setTypeface(AppController.getTypeface(SignupProcessCompleteActivity.this, "regular"));
            mNextButton.setTypeface(AppController.getTypeface(SignupProcessCompleteActivity.this, "regular"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindEvents() {

        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getIntent().getStringExtra("from") != null && getIntent().getStringExtra("from").equalsIgnoreCase("StudyInfo")) {
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    if (AppConfig.AppType.equalsIgnoreCase(getString(R.string.app_gateway))) {
                        Intent intent = new Intent(SignupProcessCompleteActivity.this, StudyActivity.class);
                        ComponentName cn = intent.getComponent();
                        Intent mainIntent = Intent.makeRestartActivityTask(cn);
                        startActivity(mainIntent);
                        finish();
                    } else {
                        Intent intent = new Intent(SignupProcessCompleteActivity.this, StandaloneActivity.class);
                        ComponentName cn = intent.getComponent();
                        Intent mainIntent = Intent.makeRestartActivityTask(cn);
                        startActivity(mainIntent);
                        finish();
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {

    }
}
