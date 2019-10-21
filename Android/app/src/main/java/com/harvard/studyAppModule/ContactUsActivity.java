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

public class ContactUsActivity extends AppCompatActivity implements ApiCall.OnAsyncRequestComplete {

    private RelativeLayout mBackBtn;
    private AppCompatTextView mTitle;
    private AppCompatTextView mFirstNameText;
    private AppCompatTextView mEmailText;
    private AppCompatTextView mSubjectText;
    private AppCompatTextView mMessageText;
    private AppCompatEditText mEmail;
    private AppCompatEditText mSubject;
    private AppCompatEditText mMessage;
    private AppCompatEditText mFirstName;
    private final int CONTACT_US = 15;
    private AppCompatTextView mSubmitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        initializeXMLId();
        setFont();
        bindEvents();
        mEmail.setText("" + AppController.getHelperSharedPreference().readPreference(this, getString(R.string.email), ""));
    }

    private void initializeXMLId() {
        mBackBtn = (RelativeLayout) findViewById(R.id.backBtn);

        mTitle = (AppCompatTextView) findViewById(R.id.title);
        mFirstNameText = (AppCompatTextView) findViewById(R.id.firstName);
        mEmailText = (AppCompatTextView) findViewById(R.id.email_label);
        mSubjectText = (AppCompatTextView) findViewById(R.id.subject_label);
        mMessageText = (AppCompatTextView) findViewById(R.id.message_label);

        mFirstName = (AppCompatEditText) findViewById(R.id.edittxt_firstName);
        mEmail = (AppCompatEditText) findViewById(R.id.edittxt_email);
        mSubject = (AppCompatEditText) findViewById(R.id.edittxt_subject);
        mMessage = (AppCompatEditText) findViewById(R.id.edittxt_message);

        mSubmitButton = (AppCompatTextView) findViewById(R.id.submitButton);
    }

    private void setFont() {
        try {
            mTitle.setTypeface(AppController.getTypeface(ContactUsActivity.this, "medium"));
            mFirstNameText.setTypeface(AppController.getTypeface(ContactUsActivity.this, "regular"));
            mEmailText.setTypeface(AppController.getTypeface(ContactUsActivity.this, "regular"));
            mSubjectText.setTypeface(AppController.getTypeface(ContactUsActivity.this, "regular"));
            mMessageText.setTypeface(AppController.getTypeface(ContactUsActivity.this, "regular"));

            mFirstName.setTypeface(AppController.getTypeface(ContactUsActivity.this, "regular"));
            mEmail.setTypeface(AppController.getTypeface(ContactUsActivity.this, "regular"));
            mSubject.setTypeface(AppController.getTypeface(ContactUsActivity.this, "regular"));
            mMessage.setTypeface(AppController.getTypeface(ContactUsActivity.this, "regular"));


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
                if (mFirstName.getText().toString().equalsIgnoreCase("") && mEmail.getText().toString().equalsIgnoreCase("") && mSubject.getText().toString().equalsIgnoreCase("") && mMessage.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(ContactUsActivity.this, getResources().getString(R.string.enter_all_field_empty), Toast.LENGTH_SHORT).show();
                } else if (mFirstName.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(ContactUsActivity.this, getResources().getString(R.string.first_name_empty), Toast.LENGTH_SHORT).show();
                } else if (mEmail.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(ContactUsActivity.this, getResources().getString(R.string.email_empty), Toast.LENGTH_SHORT).show();
                } else if (!AppController.getHelperIsValidEmail(mEmail.getText().toString())) {
                    Toast.makeText(ContactUsActivity.this, getResources().getString(R.string.email_validation), Toast.LENGTH_SHORT).show();
                } else if (mSubject.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(ContactUsActivity.this, getResources().getString(R.string.subject_empty), Toast.LENGTH_SHORT).show();
                } else if (mMessage.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(ContactUsActivity.this, getResources().getString(R.string.message_empty), Toast.LENGTH_SHORT).show();
                } else {
                    callContactUsWebservice();
                }
            }
        });
    }

    private void callContactUsWebservice() {
        AppController.getHelperProgressDialog().showProgress(ContactUsActivity.this, "", "", false);
        ContactUsEvent contactUsEvent = new ContactUsEvent();
        HashMap<String, String> params = new HashMap<>();
        params.put("subject", mSubject.getText().toString());
        params.put("body", mMessage.getText().toString());
        params.put("firstName", mFirstName.getText().toString());
        params.put("email", mEmail.getText().toString());
        WCPConfigEvent wcpConfigEvent = new WCPConfigEvent("post", URLs.CONTACT_US, CONTACT_US, ContactUsActivity.this, ReachOut.class, params, null, null, false, this);

        contactUsEvent.setWcpConfigEvent(wcpConfigEvent);
        StudyModulePresenter studyModulePresenter = new StudyModulePresenter();
        studyModulePresenter.performContactUsEvent(contactUsEvent);
    }

    @Override
    public <T> void asyncResponse(T response, int responseCode) {
        if (responseCode == CONTACT_US) {
            if (response != null) {
                AppController.getHelperProgressDialog().dismissDialog();
                Toast.makeText(ContactUsActivity.this, getResources().getString(R.string.contact_us_message), Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {
        AppController.getHelperProgressDialog().dismissDialog();
        if (statusCode.equalsIgnoreCase("401")) {
            Toast.makeText(ContactUsActivity.this, errormsg, Toast.LENGTH_SHORT).show();
            AppController.getHelperSessionExpired(ContactUsActivity.this, errormsg);
        } else {
            if (responseCode == CONTACT_US) {
                Toast.makeText(ContactUsActivity.this, errormsg, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
