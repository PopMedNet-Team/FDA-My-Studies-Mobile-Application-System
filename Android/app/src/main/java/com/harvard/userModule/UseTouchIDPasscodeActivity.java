package com.harvard.userModule;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.RelativeLayout;

import com.harvard.AppConfig;
import com.harvard.R;
import com.harvard.studyAppModule.StandaloneActivity;
import com.harvard.studyAppModule.StudyActivity;
import com.harvard.utils.AppController;

public class UseTouchIDPasscodeActivity extends AppCompatActivity {
    private RelativeLayout mBackBtn;
    private AppCompatTextView mTitle;
    private RelativeLayout mCancelBtn;
    private AppCompatTextView mCancelTxt;
    private AppCompatTextView mUseTouchIdLabel;
    private AppCompatTextView mORLabel;
    private AppCompatTextView mEnterPasscodeLabel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_use_touch_idpasscode);
        initializeXMLId();
        setTextForView();
        setFont();
        bindEvents();
        if (AppConfig.AppType.equalsIgnoreCase(getString(R.string.app_gateway))) {
            Intent intent = new Intent(UseTouchIDPasscodeActivity.this, StudyActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(UseTouchIDPasscodeActivity.this, StandaloneActivity.class);
            startActivity(intent);
        }
    }

    private void initializeXMLId() {
        mBackBtn = (RelativeLayout) findViewById(R.id.backBtn);
        mTitle = (AppCompatTextView) findViewById(R.id.title);
        mCancelBtn = (RelativeLayout) findViewById(R.id.cancelBtn);
        mCancelTxt = (AppCompatTextView) findViewById(R.id.cancelTxt);
        mUseTouchIdLabel = (AppCompatTextView) findViewById(R.id.use_touch_id);
        mORLabel = (AppCompatTextView) findViewById(R.id.or);
        mEnterPasscodeLabel = (AppCompatTextView) findViewById(R.id.enter_passcode);
    }

    private void setTextForView() {
        mTitle.setText(getResources().getString(R.string.sign_in));
    }

    private void setFont() {
        try {
            mTitle.setTypeface(AppController.getTypeface(UseTouchIDPasscodeActivity.this, "medium"));
            mCancelTxt.setTypeface(AppController.getTypeface(UseTouchIDPasscodeActivity.this, "medium"));
            mUseTouchIdLabel.setTypeface(AppController.getTypeface(UseTouchIDPasscodeActivity.this, "regular"));
            mORLabel.setTypeface(AppController.getTypeface(UseTouchIDPasscodeActivity.this, "medium"));
            mEnterPasscodeLabel.setTypeface(AppController.getTypeface(UseTouchIDPasscodeActivity.this, "regular"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindEvents() {
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        mCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
}
