package com.harvard.studyAppModule;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.harvard.R;
import com.harvard.utils.AppController;
import com.harvard.webserviceModule.apiHelper.ConnectionDetector;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.crypto.CipherInputStream;

import static android.os.Build.VERSION_CODES.M;

public class GatewayResourcesWebViewActivity extends AppCompatActivity {
    private AppCompatTextView mTitle;
    private RelativeLayout mBackBtn;
    private WebView mWebView;
    private RelativeLayout mShareBtn;
    private AppCompatImageView mShareIcon;
    private PDFView mPdfView;
    private String mDownloadedFilePath;
    private String mFileName;
    private String downloadingFileURL = "";
    private static final int PERMISSION_REQUEST_CODE = 1000;
    private String mIntentTitle;
    private String mIntentType;
    private String mIntentContent;
    private File mFinalMSharingFile;
    private ConnectionDetector connectionDetector = new ConnectionDetector(GatewayResourcesWebViewActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resources_web_view);

        mDownloadedFilePath = "/data/data/" + getPackageName() + "/files/";
        initializeXMLId();
        downloadingFileURL = getIntent().getStringExtra("content");
        mIntentTitle = getIntent().getStringExtra("title");
        mIntentType = getIntent().getStringExtra("type");
        mIntentContent = getIntent().getStringExtra("content");

        defaultPDFShow();

        setFont();

        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        mShareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setData(Uri.parse("mailto:"));
                    shareIntent.setType("application/pdf");
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, mIntentTitle);

                    ///////// default pdf show
                    if (mFinalMSharingFile.exists()) {
                        Uri fileUri = FileProvider.getUriForFile(GatewayResourcesWebViewActivity.this, getString(R.string.FileProvider_authorities), mFinalMSharingFile);
                        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                        startActivity(shareIntent);
                    }
                    ////////
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void defaultPDFShow() {
        if (mIntentType.equalsIgnoreCase("pdf")) {
            mWebView.setVisibility(View.GONE);
            mTitle.setText(mIntentTitle);
            // checking the permissions
            if ((ActivityCompat.checkSelfPermission(GatewayResourcesWebViewActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) || (ActivityCompat.checkSelfPermission(GatewayResourcesWebViewActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                String[] permission = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                if (!hasPermissions(permission)) {
                    ActivityCompat.requestPermissions((Activity) GatewayResourcesWebViewActivity.this, permission, PERMISSION_REQUEST_CODE);
                } else {
                    mFinalMSharingFile = getAssetsPdfPath();
                    DisplayPDFView(mFinalMSharingFile.getAbsolutePath());
                }
            } else {
                mFinalMSharingFile = getAssetsPdfPath();
                DisplayPDFView(mFinalMSharingFile.getAbsolutePath());
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(GatewayResourcesWebViewActivity.this, getResources().getString(R.string.permission_deniedDate), Toast.LENGTH_SHORT).show();
                    finish();
                } else {

                    ///////// default pdf show
                    mFinalMSharingFile = getAssetsPdfPath();
                    DisplayPDFView(mFinalMSharingFile.getAbsolutePath());
                    /////////
                }
                break;
        }
    }

    private void initializeXMLId() {
        mBackBtn = (RelativeLayout) findViewById(R.id.backBtn);
        mTitle = (AppCompatTextView) findViewById(R.id.title);
        mWebView = (WebView) findViewById(R.id.webView);
        mShareBtn = (RelativeLayout) findViewById(R.id.shareBtn);
        mShareIcon = (AppCompatImageView) findViewById(R.id.shareIcon);
        mPdfView = (PDFView) findViewById(R.id.pdfView);
    }


    private void setFont() {
        mTitle.setTypeface(AppController.getTypeface(this, "bold"));
    }


    public boolean hasPermissions(String[] permissions) {
        if (android.os.Build.VERSION.SDK_INT >= M && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(GatewayResourcesWebViewActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public File copy(File src) throws IOException {
        String primaryStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + mIntentTitle + ".pdf";
        File file = new File(primaryStoragePath);
        if (!file.exists())
            file.createNewFile();

        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(file);
        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();

        return file;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            if (mFinalMSharingFile.exists()) {
                mFinalMSharingFile.delete();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    private void DisplayPDFView(String filePath) {
        mPdfView.setVisibility(View.VISIBLE);
        try {
            mPdfView.fromFile(new File(filePath))
                    .defaultPage(0)
                    .enableAnnotationRendering(true)
                    .scrollHandle(new DefaultScrollHandle(GatewayResourcesWebViewActivity.this))
                    .load();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public File getAssetsPdfPath() {
        String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + mIntentTitle + ".pdf";


        File destinationFile = new File(filePath);

        try {
            FileOutputStream outputStream = new FileOutputStream(destinationFile);
            InputStream inputStream = getAssets().open("pdf/appglossary.pdf");
            byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, length);
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
        }

        return destinationFile;
    }

}
