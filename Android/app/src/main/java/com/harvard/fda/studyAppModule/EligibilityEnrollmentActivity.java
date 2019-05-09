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

package com.harvard.fda.studyAppModule;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.harvard.fda.R;
import com.harvard.fda.studyAppModule.ResponseServerModel.ResponseServerData;
import com.harvard.fda.studyAppModule.events.VerifyEnrollmentIdEvent;
import com.harvard.fda.utils.AppController;
import com.harvard.fda.utils.URLs;
import com.harvard.fda.webserviceModule.apiHelper.ApiCallResponseServer;
import com.harvard.fda.webserviceModule.events.ResponseServerConfigEvent;

import java.util.HashMap;

public class EligibilityEnrollmentActivity extends AppCompatActivity implements ApiCallResponseServer.OnAsyncRequestComplete {


    private static final int VERIFY_ENROLLMENT_ID = 101;
    private static final int CONSENT_METADATA = 103;


    private RelativeLayout mBackBtn;
    private RelativeLayout mCancelBtn;
    private AppCompatTextView mTitle;
    private TextView mEnrollmentdesc;
    private EditText mEnrollmentID;
    private TextView mSubmit;
    private String mEnteredId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eligibility_enrollment);
        initializeXMLId();
        setTextForView();
        setFont();
        bindEvents();
        mEnrollmentdesc.setText(getIntent().getStringExtra("enrollmentDesc"));
    }

    private void initializeXMLId() {
        mBackBtn = (RelativeLayout) findViewById(R.id.backBtn);
        mTitle = (AppCompatTextView) findViewById(R.id.title);
        mCancelBtn = (RelativeLayout) findViewById(R.id.cancelBtn);
        mEnrollmentdesc = (TextView) findViewById(R.id.enrollmentdesc);
        mEnrollmentID = (EditText) findViewById(R.id.enrollmentidtxt);
        mSubmit = (TextView) findViewById(R.id.submitbutton);
    }

    private void setTextForView() {
        mTitle.setText(getResources().getString(R.string.eligibility));
        mCancelBtn.setVisibility(View.GONE);
        mEnrollmentID.setText("");
    }


    private void setFont() {
        try {
            mTitle.setTypeface(AppController.getTypeface(EligibilityEnrollmentActivity.this, "medium"));
            mEnrollmentdesc.setTypeface(AppController.getTypeface(EligibilityEnrollmentActivity.this, "regular"));
            mEnrollmentID.setTypeface(AppController.getTypeface(EligibilityEnrollmentActivity.this, "regular"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppController.getHelperHideKeyboard(this);
    }

    private void bindEvents() {
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mEnrollmentID.getText().toString().equalsIgnoreCase("")) {
                    callValidateEnrollmentId();
                } else {
                    Toast.makeText(EligibilityEnrollmentActivity.this, R.string.enter_enrollment_id, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void callValidateEnrollmentId() {
        AppController.getHelperProgressDialog().showProgress(EligibilityEnrollmentActivity.this, "", "", false);

        VerifyEnrollmentIdEvent verifyEnrollmentIdEvent = new VerifyEnrollmentIdEvent();

        HashMap<String, String> params = new HashMap<>();
        params.put("studyId", "" + getIntent().getStringExtra("studyId"));
        params.put("token", mEnrollmentID.getText().toString());

        ResponseServerConfigEvent responseServerConfigEvent = new ResponseServerConfigEvent("post_json", URLs.VALIDATE_ENROLLMENT_ID, VERIFY_ENROLLMENT_ID, EligibilityEnrollmentActivity.this, ResponseServerData.class, params, null, null, false, EligibilityEnrollmentActivity.this);

        verifyEnrollmentIdEvent.setResponseServerConfigEvent(responseServerConfigEvent);
        StudyModulePresenter studyModulePresenter = new StudyModulePresenter();
        studyModulePresenter.performVerifyEnrollmentId(verifyEnrollmentIdEvent);
    }


    @Override
    public <T> void asyncResponse(T response, int responseCode, String serverType) {
        AppController.getHelperProgressDialog().dismissDialog();
        if (responseCode == VERIFY_ENROLLMENT_ID) {
            ResponseServerData responseServerData = (ResponseServerData) response;
            if (responseServerData != null) {

                Intent intent = new Intent(this, EnrollmentValidatedActivity.class);
                intent.putExtra("enrollId", mEnrollmentID.getText().toString());
                intent.putExtra("studyId", getIntent().getStringExtra("studyId"));
                intent.putExtra("title", getIntent().getStringExtra("title"));
                intent.putExtra("eligibility", getIntent().getStringExtra("eligibility"));
                intent.putExtra("type", getIntent().getStringExtra("type"));
                mEnteredId = mEnrollmentID.getText().toString();
                mEnrollmentID.setText("");
                if (getIntent().getStringExtra("eligibility").equalsIgnoreCase("combined")) {
                    Intent intent1 = new Intent();
                    intent1.putExtra("enrollId", "" + mEnteredId);
                    setResult(RESULT_OK, intent1);
                    finish();
                } else {
                    startActivity(intent);
                    finish();
                }
            } else {
                Toast.makeText(this, R.string.unable_to_parse, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 12345) {
            if (resultCode == RESULT_OK) {
                Intent intent = new Intent();
                intent.putExtra("enrollId", "" + mEnteredId);
                setResult(RESULT_OK, intent);
                finish();
            }
        }
    }

    @Override
    public <T> void asyncResponseFailure(int responseCode, String errormsg, String statusCode, T response) {
        AppController.getHelperProgressDialog().dismissDialog();
        ResponseServerData responseServerData = (ResponseServerData) response;
        if (responseServerData != null) {
            Toast.makeText(this, responseServerData.getException().toString(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, R.string.unable_to_parse, Toast.LENGTH_SHORT).show();
        }
    }

}
