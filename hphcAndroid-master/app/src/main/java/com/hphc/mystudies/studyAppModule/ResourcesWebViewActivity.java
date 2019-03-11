/*
 * Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * Funding Source: Food and Drug Administration ("Funding Agency") effective 18 September 2014 as Contract no.
 * HHSF22320140030I/HHSF22301006T (the "Prime Contract").
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package com.hphc.mystudies.studyAppModule;

import static android.os.Build.VERSION_CODES.M;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.text.Html;
import android.view.View;
import android.webkit.WebView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.hphc.mystudies.R;
import com.hphc.mystudies.utils.AppController;
import com.hphc.mystudies.webserviceModule.apiHelper.ConnectionDetector;

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

public class ResourcesWebViewActivity extends AppCompatActivity {
    private AppCompatTextView mTitle;
    private RelativeLayout mBackBtn;
    private WebView mWebView;
    private RelativeLayout mShareBtn;
    private PDFView mPdfView;
    private String mDownloadedFilePath = "/data/data/com.harvard.fda/files/";
    private String mFileName;
    private String downloadingFileURL = "";
    private static final int PERMISSION_REQUEST_CODE = 1000;
    private String mIntentTitle;
    private String mIntentType;
    private String mIntentContent;
    private File mFinalMSharingFile;
    private ConnectionDetector connectionDetector = new ConnectionDetector(ResourcesWebViewActivity.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resources_web_view);
        initializeXMLId();
        downloadingFileURL = getIntent().getStringExtra("content");
        mIntentTitle = getIntent().getStringExtra("title");
        mIntentType = getIntent().getStringExtra("type");
        mIntentContent = getIntent().getStringExtra("content");
        String studyId = getIntent().getStringExtra("studyId");

        String title;
        // removing space b/w the string : name of the pdf
        try {
            title = mIntentTitle.replaceAll("\\s+", "");
        } catch (Exception e) {
            title = mIntentTitle;
        }
        mFileName = title + studyId;

        if (mIntentType.equalsIgnoreCase("pdf")) {
            mWebView.setVisibility(View.GONE);
            mTitle.setText(mIntentTitle);
            // checking the permissions
            if ((ActivityCompat.checkSelfPermission(ResourcesWebViewActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) || (ActivityCompat.checkSelfPermission(ResourcesWebViewActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
                String[] permission = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                if (!hasPermissions(permission)) {
                    ActivityCompat.requestPermissions((Activity) ResourcesWebViewActivity.this, permission, PERMISSION_REQUEST_CODE);
                } else {
                    if (connectionDetector.isConnectingToInternet()) {
                        // starting new Async Task for downlaoding pdf file
                        new DownloadFileFromURL(downloadingFileURL, mDownloadedFilePath, mFileName).execute();
                    } else {
                        // offline functionality
                        offLineFunctionality();
                    }
                }
            } else {
                if (connectionDetector.isConnectingToInternet()) {
                    // starting new Async Task for downlaoding pdf file
                    new DownloadFileFromURL(downloadingFileURL, mDownloadedFilePath, mFileName).execute();
                } else {
                    // offline functionality
                    offLineFunctionality();
                }
            }
        } else {
            setTextForView();
        }

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
                    shareIntent.setDataAndType(Uri.parse("mailto:"), "application/pdf");
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    shareIntent.putExtra(Intent.EXTRA_SUBJECT, mIntentTitle);
                    // if pdf then attach and send else content send
                    if (mIntentType.equalsIgnoreCase("pdf")) {
                        File file = new File(mDownloadedFilePath + mFileName + ".pdf");
                        if (file.exists()) {
                            mFinalMSharingFile = copy(file);
                            Uri fileUri = FileProvider.getUriForFile(ResourcesWebViewActivity.this, "com.myfileprovider", mFinalMSharingFile);
                            shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                        }
                    } else {
                        shareIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(mIntentContent));
                    }


                    startActivity(shareIntent);
                } catch (Exception e) {
                }
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                Toast.makeText(ResourcesWebViewActivity.this, getResources().getString(R.string.permission_deniedDate), Toast.LENGTH_SHORT).show();
                finish();
            } else {
                if (connectionDetector.isConnectingToInternet()) {
                    new DownloadFileFromURL(downloadingFileURL, mDownloadedFilePath, mFileName).execute();
                } else {
                    Toast.makeText(ResourcesWebViewActivity.this, getResources().getString(R.string.check_internet), Toast.LENGTH_LONG).show();
                }
            }

        }
    }

    private void initializeXMLId() {
        mBackBtn = (RelativeLayout) findViewById(R.id.backBtn);
        mTitle = (AppCompatTextView) findViewById(R.id.title);
        mWebView = (WebView) findViewById(R.id.webView);
        mShareBtn = (RelativeLayout) findViewById(R.id.shareBtn);
        mPdfView = (PDFView) findViewById(R.id.pdfView);
    }

    private void setTextForView() {
        String title = mIntentTitle;
        mTitle.setText(title);
        String webData = mIntentContent;
        mWebView.getSettings().setLoadsImagesAutomatically(true);
        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDefaultTextEncodingName("utf-8");
        mWebView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        mWebView.loadData(webData, "text/html; charset=utf-8", "UTF-8");

    }

    private void setFont() {
        mTitle.setTypeface(AppController.getTypeface(this, "bold"));
    }


    public boolean hasPermissions(String[] permissions) {
        if (android.os.Build.VERSION.SDK_INT >= M && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(ResourcesWebViewActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public File copy(File src) throws IOException {
        File file = null;
        InputStream in = null;
        OutputStream out = null;

        try {
            String primaryStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + mIntentTitle + ".pdf";
            file = new File(primaryStoragePath);
            if (!file.exists())
                file.createNewFile();

            in = new FileInputStream(src);
            out = new FileOutputStream(file);
            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }

        } catch (IOException e) {
        }
        finally {
            try {
                if (in != null) {
                    in.close();
                }

            } catch (IOException e) {
            }
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
            }
        }

        return file;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            File file = new File(mDownloadedFilePath + mFileName + ".pdf");
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
        }
        try {
            if (mFinalMSharingFile.exists()) {
                mFinalMSharingFile.delete();
            }

        } catch (Exception e) {
        }
    }

    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         */
        String downloadUrl = "";
        String filePath = "";
        String fileName = "";

        public DownloadFileFromURL(String downloadUrl, String filePath, String fileName) {
            this.downloadUrl = downloadUrl;
            this.filePath = filePath;
            this.fileName = fileName;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            AppController.getHelperProgressDialog().showProgress(ResourcesWebViewActivity.this, "", "", false);
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            InputStream input = null;
            OutputStream output = null;
            try {
                URL url = new URL(downloadUrl);
                URLConnection conection = url.openConnection();
                conection.connect();
                // getting file length
                int lenghtOfFile = conection.getContentLength();

                // input stream to read file - with 8k buffer
                input = new BufferedInputStream(url.openStream(), 8192);

                // Output stream to write file
                output = new FileOutputStream(filePath + fileName + ".pdf");

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress(Integer.toString((int) ((total * 100) / lenghtOfFile)));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();



            } catch (Exception e) {
                // while downloading time, net got disconnected so delete the file
                try {
                    new File(filePath + fileName + ".pdf").delete();
                } catch (Exception e1) {
                }
            }
            finally {
                try {
                    // closing streams
                    if (output != null) {
                        output.close();
                    }

                } catch (IOException e) {
                }
                try {
                    if (input != null) {
                        input.close();
                    }
                } catch (IOException e) {
                }
            }
            return null;
        }


        /**
         * After completing background task
         * Dismiss the progress dialog
         **/
        @Override
        protected void onPostExecute(String file_url) {
            try {
                // downlaod success mean file exist else check offline file
                File file = new File(filePath + fileName + ".pdf");
                if (file.exists()) {
                    AppController.genarateEncryptedConsentPDF(filePath, fileName);
                    displayPDFView(filePath + fileName + ".pdf");
                } else {
                    // offline functionality
                    offLineFunctionality();
                }
            } catch (Exception e) {
            }
            // dismiss the dialog after the file was downloaded
            AppController.getHelperProgressDialog().dismissDialog();

        }

    }

    private void offLineFunctionality() {
        try {
            // checking encrypted file is there or not?
            File file = new File(mDownloadedFilePath + mFileName + ".txt");
            if (file.exists()) {
                // decrypt the file
                File decryptFile = getEncryptedFilePath(mDownloadedFilePath + mFileName + ".txt");
                if (decryptFile != null) {
                    displayPDFView(decryptFile.getAbsolutePath());
                }
            } else {
                Toast.makeText(ResourcesWebViewActivity.this, getResources().getString(R.string.check_internet), Toast.LENGTH_LONG).show();
            }
        } catch (Resources.NotFoundException e) {
            Toast.makeText(ResourcesWebViewActivity.this, getResources().getString(R.string.check_internet), Toast.LENGTH_LONG).show();
        }
    }

    private File getEncryptedFilePath(String filePath) {
        OutputStream output = null;
        try {
            CipherInputStream cis = AppController.genarateDecryptedConsentPDF(filePath);
            byte[] byteArray = AppController.cipherInputStreamConvertToByte(cis);
            File file = new File(mDownloadedFilePath + mFileName + ".pdf");
            if (!file.exists() && file == null) {
                file.createNewFile();
            }
            output = new FileOutputStream(file);
            output.write(byteArray);

            return file;
        } catch (IOException e) {
        }
        finally {
            try {
                if (output != null) {
                    output.close();
                }
            } catch (IOException e) {
            }
        }
        return null;
    }

    private void displayPDFView(String filePath) {
        mPdfView.setVisibility(View.VISIBLE);
        try {
            mPdfView.fromFile(new File(filePath))
                    .defaultPage(0)
                    .enableAnnotationRendering(true)
                    .scrollHandle(new DefaultScrollHandle(ResourcesWebViewActivity.this))
                    .load();
        } catch (Exception e) {
        }
    }

}
