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

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.util.Base64;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.harvard.fda.R;
import com.harvard.fda.storageModule.DBServiceSubscriber;
import com.harvard.fda.studyAppModule.events.ConsentPDFEvent;
import com.harvard.fda.studyAppModule.studyModel.ConsentPDF;
import com.harvard.fda.studyAppModule.studyModel.ConsentPdfData;
import com.harvard.fda.userModule.UserModulePresenter;
import com.harvard.fda.utils.AppController;
import com.harvard.fda.utils.URLs;
import com.harvard.fda.webserviceModule.apiHelper.ApiCall;
import com.harvard.fda.webserviceModule.events.RegistrationServerConfigEvent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

import javax.crypto.CipherInputStream;

import io.realm.Realm;

import static android.os.Build.VERSION_CODES.M;

public class PDFDisplayActivity extends AppCompatActivity implements ApiCall.OnAsyncRequestComplete {
    private final int CONSENTPDF = 7;
    private PDFView mPdfView;
    private String mStudyId;
    private String mSharePDFFilePath;
    private RelativeLayout mShareBtn;
    private RelativeLayout mBackBtn;
    private static final int PERMISSION_REQUEST_CODE = 1000;
    private byte[] bytesArray = null;
    DBServiceSubscriber db;
    Realm mRealm;
    private String mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfdisplay);
        db = new DBServiceSubscriber();
        mRealm = AppController.getRealmobj(this);
        mPdfView = (PDFView) findViewById(R.id.pdfView);
        mBackBtn = (RelativeLayout) findViewById(R.id.backBtn);
        mShareBtn = (RelativeLayout) findViewById(R.id.shareBtn);
        AppCompatTextView title = (AppCompatTextView) findViewById(R.id.title);
        title.setText(getResources().getString(R.string.consent_pdf1));
        title.setTypeface(AppController.getTypeface(this, "bold"));

        mStudyId = getIntent().getStringExtra("studyId");
        mTitle = getIntent().getStringExtra("title");
        ConsentPdfData studies = db.getPDFPath(mStudyId, mRealm);
        try {
            if (studies == null) {
                callGetConsentPDFWebservice();
            } else {
                File file = new File(studies.getPdfPath().toString());
                if (file.exists()) {
                    CipherInputStream cis = AppController.genarateDecryptedConsentPDF(studies.getPdfPath().toString());
                    //we will get byte array pass to pdf view
                    bytesArray = AppController.cipherInputStreamConvertToByte(cis);
                    setPDFView();
                } else {
                    callGetConsentPDFWebservice();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // checking the permissions
                if ((ActivityCompat.checkSelfPermission(PDFDisplayActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) || (ActivityCompat.checkSelfPermission(PDFDisplayActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                    String[] permission = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    if (!hasPermissions(permission)) {
                        // just checking is it already denied?
                        Toast.makeText(PDFDisplayActivity.this, getResources().getString(R.string.permission_enable_message), Toast.LENGTH_LONG).show();
                    } else {
                        sharePDF();
                    }
                } else {
                    sharePDF();
                }
            }
        });
    }


    private void sharePDF() {
        try {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setData(Uri.parse("mailto:"));
            shareIntent.setType("application/pdf");
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.signed_consent));
            File mShareFile = new File(mSharePDFFilePath);
            if (mShareFile.exists()) {
                Uri fileUri = FileProvider.getUriForFile(PDFDisplayActivity.this, "com.myfileprovider", mShareFile);
                shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                startActivity(shareIntent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void callGetConsentPDFWebservice() {
        AppController.getHelperProgressDialog().showProgress(PDFDisplayActivity.this, "", "", false);
        ConsentPDFEvent consentPDFEvent = new ConsentPDFEvent();
        HashMap<String, String> header = new HashMap<>();
        header.put("auth", AppController.getHelperSharedPreference().readPreference(this, getResources().getString(R.string.auth), ""));
        header.put("userId", AppController.getHelperSharedPreference().readPreference(this, getResources().getString(R.string.userid), ""));
        String url = URLs.CONSENTPDF + "?studyId=" + mStudyId + "&consentVersion=";
        RegistrationServerConfigEvent registrationServerConfigEvent = new RegistrationServerConfigEvent("get", url, CONSENTPDF, PDFDisplayActivity.this, ConsentPDF.class, null, header, null, false, PDFDisplayActivity.this);
        consentPDFEvent.setmRegistrationServerConfigEvent(registrationServerConfigEvent);
        UserModulePresenter userModulePresenter = new UserModulePresenter();
        userModulePresenter.performConsentPDF(consentPDFEvent);
    }


    private void setPDFView() {
        // before writing pdf check permission
        pdfWritingPermission();
        mPdfView.fromBytes(bytesArray)
                .defaultPage(0)
                .enableAnnotationRendering(true)
                .scrollHandle(new DefaultScrollHandle(this))
                .load();
    }


    private void pdfWritingPermission() {
        // checking the permissions
        if ((ActivityCompat.checkSelfPermission(PDFDisplayActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) || (ActivityCompat.checkSelfPermission(PDFDisplayActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            String[] permission = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            if (!hasPermissions(permission)) {
                ActivityCompat.requestPermissions((Activity) PDFDisplayActivity.this, permission, PERMISSION_REQUEST_CODE);
            } else {
                // sharing pdf creating
                sharePDFCreation();
            }
        } else {
            // sharing pdf creating
            sharePDFCreation();
        }
    }

    public boolean hasPermissions(String[] permissions) {
        if (android.os.Build.VERSION.SDK_INT >= M && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(PDFDisplayActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(PDFDisplayActivity.this, getResources().getString(R.string.permission_enable_message), Toast.LENGTH_LONG).show();
                } else {
                    sharePDFCreation();
                }
                break;
        }
    }

    public void sharePDFCreation() {
        try {
            String temPDFPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + mTitle + "_" + getString(R.string.signed_consent) + ".pdf";
            File file = new File(temPDFPath);
            if (!file.exists())
                file.createNewFile();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytesArray);
            fos.close();
            mSharePDFFilePath = file.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    @Override
    public <T> void asyncResponse(T response, int responseCode) {
        try {
            AppController.getHelperProgressDialog().dismissDialog();
            ConsentPDF consentPDFData = (ConsentPDF) response;
            if (consentPDFData != null) {

                try {
                    bytesArray = Base64.decode(consentPDFData.getConsent().getContent(), Base64.DEFAULT);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                setPDFView();
                try {

                    consentPDFData.setStudyId(mStudyId);
                    db.saveConsentPDF(this,consentPDFData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {
        AppController.getHelperProgressDialog().dismissDialog();
        if (statusCode.equalsIgnoreCase("401")) {
            Toast.makeText(PDFDisplayActivity.this, errormsg, Toast.LENGTH_SHORT).show();
            AppController.getHelperSessionExpired(PDFDisplayActivity.this, errormsg);
        } else {
            try {
                ConsentPDF consentPDFData = db.getConsentPDF(mStudyId, mRealm);
                if (consentPDFData != null) {

                    try {
                        bytesArray = Base64.decode(consentPDFData.getConsent().getContent(), Base64.DEFAULT);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    setPDFView();
                } else {
                    Toast.makeText(PDFDisplayActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(PDFDisplayActivity.this, errormsg, Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        db.closeRealmObj(mRealm);
        try {
            File file = new File(mSharePDFFilePath);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

}
