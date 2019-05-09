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
import android.text.Html;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.harvard.fda.R;
import com.harvard.fda.utils.AppController;

public class ChooseTouchIDSignInActivity extends AppCompatActivity {
    private AppCompatTextView mTitle;
    private AppCompatTextView mUseTouchIdLabel;
    private AppCompatTextView mORLabel;
    private AppCompatTextView mSignInLabel;
    private AppCompatTextView mUsrPasswordLabel;
    private AppCompatTextView mCancelTxt;
    private RelativeLayout mCancelBtn;
    private RelativeLayout mBackBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_touchid_sign_in);
        initializeXMLId();
        setTextForView();
        customTextView(mSignInLabel);
        setFont();
        bindEvents();
        Intent intent = new Intent(ChooseTouchIDSignInActivity.this, SignInActivity.class);
        startActivity(intent);
    }

    private void initializeXMLId() {
        mBackBtn = (RelativeLayout) findViewById(R.id.backBtn);
        mTitle = (AppCompatTextView) findViewById(R.id.title);
        mCancelBtn = (RelativeLayout) findViewById(R.id.cancelBtn);
        mCancelTxt = (AppCompatTextView) findViewById(R.id.cancelTxt);
        mUseTouchIdLabel = (AppCompatTextView) findViewById(R.id.use_touch_id);
        mORLabel = (AppCompatTextView) findViewById(R.id.or);
        mSignInLabel = (AppCompatTextView) findViewById(R.id.sign_in);
        mUsrPasswordLabel = (AppCompatTextView) findViewById(R.id.username_and_pwd);
    }

    private void setTextForView() {
        mTitle.setText(getResources().getString(R.string.sign_in));
    }

    @SuppressWarnings("deprecation")
    private void customTextView(AppCompatTextView view) {
        String html = "<font color=\"#007cba\"><u>" + getResources().getString(R.string.sign_in) + "</u></font>";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            view.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY));
        } else {
            view.setText(Html.fromHtml(html));
        }
    }

    private void setFont() {
        try {
            mTitle.setTypeface(AppController.getTypeface(ChooseTouchIDSignInActivity.this, "medium"));
            mCancelTxt.setTypeface(AppController.getTypeface(ChooseTouchIDSignInActivity.this, "medium"));
            mUseTouchIdLabel.setTypeface(AppController.getTypeface(ChooseTouchIDSignInActivity.this, "regular"));
            mORLabel.setTypeface(AppController.getTypeface(ChooseTouchIDSignInActivity.this, "regular"));
            mSignInLabel.setTypeface(AppController.getTypeface(ChooseTouchIDSignInActivity.this, "regular"));
            mUsrPasswordLabel.setTypeface(AppController.getTypeface(ChooseTouchIDSignInActivity.this, "regular"));

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

        mUseTouchIdLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ChooseTouchIDSignInActivity.this, getResources().getString(R.string.touch_screen) + " ...", Toast.LENGTH_SHORT).show();
            }
        });

        mSignInLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ChooseTouchIDSignInActivity.this, getResources().getString(R.string.sign_in_Screen) + "...", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
