package com.harvard.userModule;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.webkit.WebView;
import android.widget.RelativeLayout;

import com.harvard.R;
import com.harvard.utils.AppController;

public class TermsPrivacyPolicyActivity extends AppCompatActivity {
    private RelativeLayout mBackBtn;
    private AppCompatTextView mTitle;
    private WebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_privacy_policy);
        initializeXMLId();
        setTextForView();
        setFont();
        bindEvents();
    }

    private void initializeXMLId() {
        mBackBtn = (RelativeLayout) findViewById(R.id.backBtn);
        mTitle = (AppCompatTextView) findViewById(R.id.title);
        mWebView = (WebView) findViewById(R.id.webView);
    }

    private void setTextForView() {
        try {
            String title = getIntent().getStringExtra("title");
            mTitle.setText(title);
            mWebView.getSettings().setLoadsImagesAutomatically(true);
            mWebView.getSettings().setJavaScriptEnabled(true);
            mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
            mWebView.loadUrl(getIntent().getStringExtra("url"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setFont() {
        try {
            mTitle.setTypeface(AppController.getTypeface(TermsPrivacyPolicyActivity.this, "medium"));

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
    }

}
