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

package com.harvard.fda.gatewayModule;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.RelativeLayout;

import com.harvard.fda.AppConfig;
import com.harvard.fda.R;
import com.harvard.fda.gatewayModule.events.GetStartedEvent;
import com.harvard.fda.studyAppModule.StandaloneActivity;
import com.harvard.fda.studyAppModule.StudyActivity;
import com.harvard.fda.userModule.SignInActivity;
import com.harvard.fda.userModule.SignupActivity;
import com.harvard.fda.utils.AppController;

public class GatewayActivity extends AppCompatActivity {
    private static final int UPGRADE = 100;
    private AppCompatTextView mGetStarted;
    private RelativeLayout mNewUserLayout;
    private AppCompatTextView mNewUserButton;
    private RelativeLayout mSignInButtonLayout;
    private AppCompatTextView mSignInButton;
    public static String COMMING_FROM = "Gateway";
    private static String FROM = "from";
    private static String TYPEFACE_REGULAR = "regular";
    private static AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gateway);
        initializeXMLId();
        setFont();
        bindEvents();
        setViewPagerView();
        if (getIntent().getStringExtra("from") != null && getIntent().getStringExtra("from").equalsIgnoreCase("forgot")) {
            Intent intent = new Intent(GatewayActivity.this, SignInActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppController.getHelperHideKeyboard(GatewayActivity.this);
        AppController.getHelperSharedPreference().writePreference(GatewayActivity.this, getString(R.string.join), "false");
    }

    private void initializeXMLId() {
        mGetStarted = (AppCompatTextView) findViewById(R.id.mGetStarted);
        mNewUserLayout = (RelativeLayout) findViewById(R.id.mNewUserLayout);
        mNewUserButton = (AppCompatTextView) findViewById(R.id.mNewUserButton);
        mSignInButtonLayout = (RelativeLayout) findViewById(R.id.mSignInButtonLayout);
        mSignInButton = (AppCompatTextView) findViewById(R.id.mSignInButton);
    }

    private void setFont() {
        try {
            mGetStarted.setTypeface(AppController.getTypeface(this, TYPEFACE_REGULAR));
            mNewUserButton.setTypeface(AppController.getTypeface(GatewayActivity.this, TYPEFACE_REGULAR));
            mSignInButton.setTypeface(AppController.getTypeface(GatewayActivity.this, TYPEFACE_REGULAR));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindEvents() {
        mNewUserLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GatewayActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        mSignInButtonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GatewayActivity.this, SignInActivity.class);
                startActivity(intent);
            }
        });

        mGetStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetStartedEvent getStartedEvent = new GetStartedEvent();
                getStartedEvent.setCommingFrom(GatewayActivity.COMMING_FROM);
                onEvent(getStartedEvent);
            }
        });
    }

    private void setViewPagerView() {
        ViewPager viewpager = (ViewPager) findViewById(R.id.viewpager);
        CircleIndicator indicator = (CircleIndicator) findViewById(R.id.indicator);
        viewpager.setAdapter(new GatewayPagerAdapter());
        indicator.setViewPager(viewpager);
        viewpager.setCurrentItem(0);
        indicator.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageScrollStateChanged(int state) {
            }

            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            public void onPageSelected(int position) {
                // Check if this is the page you want.
                if (position == 0) {
                    mGetStarted.setBackground(getResources().getDrawable(R.drawable.rectangle_blue_white));
                    mGetStarted.setTextColor(getResources().getColor(R.color.white));
                } else {
                    mGetStarted.setBackground(getResources().getDrawable(R.drawable.rectangle_black_white));
                    mGetStarted.setTextColor(getResources().getColor(R.color.colorPrimary));
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void onEvent(GetStartedEvent event) {
        if (AppConfig.AppType.equalsIgnoreCase(getString(R.string.app_gateway))) {
            Intent intent = new Intent(GatewayActivity.this, StudyActivity.class);
            intent.putExtra(FROM, event.getCommingFrom());
            startActivity(intent);
            finish();
        } else {
            Intent intent = new Intent(GatewayActivity.this, StandaloneActivity.class);
            intent.putExtra(FROM, event.getCommingFrom());
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UPGRADE) {
            alertDialog.dismiss();
        }
    }
}
