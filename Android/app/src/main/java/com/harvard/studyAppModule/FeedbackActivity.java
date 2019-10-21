package com.harvard.studyAppModule;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.harvard.R;
import com.harvard.studyAppModule.events.ContactUsEvent;
import com.harvard.studyAppModule.studyModel.ReachOut;
import com.harvard.utils.AppController;
import com.harvard.utils.URLs;
import com.harvard.webserviceModule.apiHelper.ApiCall;
import com.harvard.webserviceModule.events.WCPConfigEvent;

import java.util.HashMap;

public class FeedbackActivity extends AppCompatActivity implements ApiCall.OnAsyncRequestComplete {
    private AppCompatTextView mTitle;
    private AppCompatTextView mFeedbackText;
    private AppCompatEditText mFeedbackEdittext;
    private AppCompatEditText mSubject;
    private RelativeLayout mBackBtn;
    private AppCompatTextView mSubmitButton;
    private final int FEEDBACK = 16;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        initializeXMLId();
        setFont();
        bindEvents();
    }

    private void initializeXMLId() {
        mBackBtn = (RelativeLayout) findViewById(R.id.backBtn);
        mTitle = (AppCompatTextView) findViewById(R.id.title);
        mFeedbackText = (AppCompatTextView) findViewById(R.id.feedback_label);
        mFeedbackEdittext = (AppCompatEditText) findViewById(R.id.edittxt_feedback);
        mSubject = (AppCompatEditText) findViewById(R.id.subject);
        mSubmitButton = (AppCompatTextView) findViewById(R.id.submitButton);
    }

    private void setFont() {
        try {
            mTitle.setTypeface(AppController.getTypeface(FeedbackActivity.this, "medium"));
            mFeedbackText.setTypeface(AppController.getTypeface(FeedbackActivity.this, "regular"));

            mFeedbackEdittext.setTypeface(AppController.getTypeface(FeedbackActivity.this, "regular"));


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void bindEvents() {
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                    inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                finish();
            }
        });
        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSubject.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(FeedbackActivity.this, getResources().getString(R.string.subject_empty), Toast.LENGTH_SHORT).show();
                } else if (mFeedbackEdittext.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(FeedbackActivity.this, getResources().getString(R.string.feedback_empty), Toast.LENGTH_SHORT).show();
                } else {
                    callmFeedbackWebservice();
                }
            }
        });
    }

    private void callmFeedbackWebservice() {
        AppController.getHelperProgressDialog().showProgress(FeedbackActivity.this, "", "", false);
        ContactUsEvent contactUsEvent = new ContactUsEvent();
        HashMap<String, String> params = new HashMap<>();
        params.put("subject", mSubject.getText().toString());
        params.put("body", mFeedbackEdittext.getText().toString());
        WCPConfigEvent wcpConfigEvent = new WCPConfigEvent("post", URLs.FEEDBACK, FEEDBACK, FeedbackActivity.this, ReachOut.class, params, null, null, false, this);

        contactUsEvent.setWcpConfigEvent(wcpConfigEvent);
        StudyModulePresenter studyModulePresenter = new StudyModulePresenter();
        studyModulePresenter.performContactUsEvent(contactUsEvent);
    }

    @Override
    public <T> void asyncResponse(T response, int responseCode) {
        if (responseCode == FEEDBACK) {
            if (response != null) {
                AppController.getHelperProgressDialog().dismissDialog();
                Toast.makeText(FeedbackActivity.this, getResources().getString(R.string.feedback_submit_success), Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {
        AppController.getHelperProgressDialog().dismissDialog();
        if (statusCode.equalsIgnoreCase("401")) {
            Toast.makeText(FeedbackActivity.this, errormsg, Toast.LENGTH_SHORT).show();
            AppController.getHelperSessionExpired(FeedbackActivity.this, errormsg);
        } else {
            if (responseCode == FEEDBACK) {
                Toast.makeText(FeedbackActivity.this, errormsg, Toast.LENGTH_SHORT).show();
            }

        }
    }
}
