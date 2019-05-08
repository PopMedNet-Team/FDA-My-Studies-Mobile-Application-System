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
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.RelativeLayout;

import com.harvard.fda.AppConfig;
import com.harvard.fda.R;
import com.harvard.fda.studyAppModule.StandaloneActivity;
import com.harvard.fda.studyAppModule.StudyActivity;
import com.harvard.fda.utils.AppController;

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
