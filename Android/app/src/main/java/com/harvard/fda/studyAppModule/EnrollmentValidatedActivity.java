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

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.text.Html;
import android.util.Base64;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.harvard.fda.AppConfig;
import com.harvard.fda.R;
import com.harvard.fda.storageModule.DBServiceSubscriber;
import com.harvard.fda.studyAppModule.consent.ConsentBuilder;
import com.harvard.fda.studyAppModule.consent.CustomConsentViewTaskActivity;
import com.harvard.fda.studyAppModule.consent.model.Consent;
import com.harvard.fda.studyAppModule.consent.model.EligibilityConsent;
import com.harvard.fda.utils.AppController;
import com.harvard.fda.webserviceModule.apiHelper.ApiCall;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import org.json.JSONObject;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.OrderedTask;
import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.ui.step.layout.ConsentSignatureStepLayout;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

import io.realm.Realm;


public class EnrollmentValidatedActivity extends AppCompatActivity implements ApiCall.OnAsyncRequestComplete {

    AppCompatTextView mValidatedlabel, mCompleteLabel, mContinueButton;
    private static final int CONSENT_METADATA = 100;
    EligibilityConsent eligibilityConsent;
    private static final String CONSENT = "consent";
    private static final int CONSENT_RESPONSECODE = 100;
    String signatureBase64 = "";
    String signatureDate = "";
    String firstName = "";
    String lastName = "";
    private static File myFile, file, encryptFile;
    private static final String FILE_FOLDER = "FDA_PDF";
    private String mFileName;
    DBServiceSubscriber dbServiceSubscriber;
    Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrollment_validated);
        dbServiceSubscriber = new DBServiceSubscriber();
        mRealm = AppController.getRealmobj(this);
        initializeXMLId();
        setFont();
        mContinueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                eligibilityConsent = dbServiceSubscriber.getConsentMetadata(getIntent().getStringExtra("studyId"), mRealm);
                if (getIntent().getStringExtra("eligibility").equalsIgnoreCase("token")) {
                    startconsent(eligibilityConsent.getConsent());
                } else {
                    Intent intent = new Intent();
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

    private void initializeXMLId() {
        mValidatedlabel = (AppCompatTextView) findViewById(R.id.validatedlabel);
        mCompleteLabel = (AppCompatTextView) findViewById(R.id.complete_txt_label);
        mContinueButton = (AppCompatTextView) findViewById(R.id.continueButton);

    }

    @Override
    protected void onDestroy() {
        dbServiceSubscriber.closeRealmObj(mRealm);
        super.onDestroy();
    }

    private void setFont() {
        mValidatedlabel.setTypeface(AppController.getTypeface(EnrollmentValidatedActivity.this, "regular"));
        mCompleteLabel.setTypeface(AppController.getTypeface(EnrollmentValidatedActivity.this, "regular"));
        mContinueButton.setTypeface(AppController.getTypeface(EnrollmentValidatedActivity.this, "regular"));
    }


    @Override
    public <T> void asyncResponse(T response, int responseCode) {
    }

    @Override
    public void asyncResponseFailure(int responseCode, String errormsg, String statusCode) {
    }


    private void startconsent(Consent consent) {
        ConsentBuilder consentBuilder = new ConsentBuilder();
        List<Step> consentstep = consentBuilder.createsurveyquestion(this, consent, getIntent().getStringExtra("title"));
        Task consentTask = new OrderedTask(CONSENT, consentstep);
        Intent intent = CustomConsentViewTaskActivity.newIntent(this, consentTask, getIntent().getStringExtra("studyId"), getIntent().getStringExtra("enrollId"), getIntent().getStringExtra("title"), getIntent().getStringExtra("eligibility"), getIntent().getStringExtra("type"));
        startActivityForResult(intent, CONSENT_RESPONSECODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CONSENT_RESPONSECODE) {
            if (resultCode == RESULT_OK) {
                try {
                    TaskResult result = (TaskResult) data.getSerializableExtra(CustomConsentViewTaskActivity.EXTRA_TASK_RESULT);
                    signatureBase64 = (String) result.getStepResult("Signature")
                            .getResultForIdentifier(ConsentSignatureStepLayout.KEY_SIGNATURE);

                    signatureDate = (String) result.getStepResult("Signature")
                            .getResultForIdentifier(ConsentSignatureStepLayout.KEY_SIGNATURE_DATE);

                    String formResult = new Gson().toJson(result.getStepResult(getResources().getString(R.string.signature_form_step)).getResults());
                    JSONObject formResultObj = new JSONObject(formResult);
                    JSONObject fullNameObj = formResultObj.getJSONObject("First Name");
                    JSONObject fullNameResult = fullNameObj.getJSONObject("results");
                    firstName = fullNameResult.getString("answer");

                    JSONObject lastNameObj = formResultObj.getJSONObject("Last Name");
                    JSONObject lastNameResult = lastNameObj.getJSONObject("results");
                    lastName = lastNameResult.getString("answer");

                } catch (Exception e) {
                    e.printStackTrace();
                }


                genarateConsentPDF(signatureBase64);
                // encrypt the genarated pdf
                encryptFile = AppController.genarateEncryptedConsentPDF("/data/data/" + getPackageName() + "/files/", mFileName);
                //After encryption delete the pdf file
                if (encryptFile != null) {
                    File file = new File("/data/data/" + getPackageName() + "/files/" + mFileName + ".pdf");
                    file.delete();
                }
                Intent intent = new Intent(EnrollmentValidatedActivity.this, ConsentCompletedActivity.class);
                intent.putExtra("enrollId", getIntent().getStringExtra("enrollId"));
                intent.putExtra("studyId", getIntent().getStringExtra("studyId"));
                intent.putExtra("title", getIntent().getStringExtra("title"));
                intent.putExtra("eligibility", getIntent().getStringExtra("eligibility"));
                intent.putExtra("type", data.getStringExtra(CustomConsentViewTaskActivity.TYPE));
                // get the encrypted file path
                intent.putExtra("PdfPath", encryptFile.getAbsolutePath());
                startActivity(intent);
                finish();
            } else if (resultCode == 12345) {
                if (AppConfig.AppType.equalsIgnoreCase(getString(R.string.app_gateway))) {
                    Intent intent = new Intent(EnrollmentValidatedActivity.this, StudyActivity.class);
                    ComponentName cn = intent.getComponent();
                    Intent mainIntent = Intent.makeRestartActivityTask(cn);
                    startActivity(mainIntent);
                    finish();
                } else {
                    Intent intent = new Intent(EnrollmentValidatedActivity.this, StandaloneActivity.class);
                    ComponentName cn = intent.getComponent();
                    Intent mainIntent = Intent.makeRestartActivityTask(cn);
                    startActivity(mainIntent);
                    finish();
                }
            } else {
                finish();
            }
        }
    }


    private void genarateConsentPDF(String signatureBase64) {
        try {
            getFile("/data/data/" + getPackageName() + "/files/");
            Date date = new Date();
            String timeStamp = AppController.getDateFormatType3();
            mFileName = timeStamp;
            String filePath = "/data/data/" + getPackageName() + "/files/" + timeStamp + ".pdf";
            myFile = new File(filePath);
            if (!myFile.exists())
                myFile.createNewFile();
            OutputStream output = new FileOutputStream(myFile);

            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, output);
            writer.setFullCompression();

            document.addCreationDate();
            document.setPageSize(PageSize.A4);
            document.setMargins(10, 10, 10, 10);

            document.open();
            Paragraph consentItem;
            if (eligibilityConsent != null && eligibilityConsent.getConsent() != null && eligibilityConsent.getConsent().getReview() != null && eligibilityConsent.getConsent().getReview().getSignatureContent() != null && !eligibilityConsent.getConsent().getReview().getSignatureContent().equalsIgnoreCase("")) {
                consentItem = new Paragraph(Html.fromHtml(eligibilityConsent.getConsent().getReview().getSignatureContent().toString()).toString());
            } else if (eligibilityConsent != null && eligibilityConsent.getConsent() != null && eligibilityConsent.getConsent().getVisualScreens() != null) {
                StringBuilder docBuilder;
                if (eligibilityConsent.getConsent().getVisualScreens().size() > 0) {
                    // Create our HTML to show the user and have them accept or decline.
                    docBuilder = new StringBuilder(
                            "</br><div style=\"padding: 10px 10px 10px 10px;\" class='header'>");
                    String title = getIntent().getStringExtra("title");
                    docBuilder.append(String.format(
                            "<h1 style=\"text-align: center; font-family:sans-serif-light;\">%1$s</h1>",
                            title));


                    docBuilder.append("</div></br>");
                    for (int i = 0; i < eligibilityConsent.getConsent().getVisualScreens().size(); i++) {
                        docBuilder.append("<div>  <h4>" + eligibilityConsent.getConsent().getVisualScreens().get(i).getTitle() + "<h4> </div>");
                        docBuilder.append("</br>");
                        docBuilder.append("<div>" + eligibilityConsent.getConsent().getVisualScreens().get(i).getHtml() + "</div>");
                        docBuilder.append("</br>");
                        docBuilder.append("</br>");
                    }
                    consentItem = new Paragraph(Html.fromHtml(docBuilder.toString()).toString());
                } else {
                    consentItem = new Paragraph("");
                }
            } else {
                consentItem = new Paragraph("");
            }
            StringBuilder docBuilder = new StringBuilder(
                    "</br><div style=\"padding: 10px 10px 10px 10px;\" class='header'>");
            String participant = getResources().getString(R.string.participant);
            docBuilder.append(String.format("<p style=\"text-align: center\">%1$s</p>", participant));
            String detail = getResources().getString(R.string.agree_participate_research_study);
            docBuilder.append(String.format("<p style=\"text-align: center\">%1$s</p>", detail));
            consentItem.add(Html.fromHtml(docBuilder.toString()).toString());

            byte[] signatureBytes;
            Image myImg = null;
            if (signatureBase64 != null) {
                signatureBytes = Base64.decode(signatureBase64, Base64.DEFAULT);
                myImg = Image.getInstance(signatureBytes);
                myImg.setScaleToFitHeight(true);
                myImg.scalePercent(50f);
            }

            PdfPTable table = new PdfPTable(3);
            table.setWidthPercentage(100);
            table.addCell(getCell(firstName + " " + lastName, PdfPCell.ALIGN_CENTER));
            table.addCell(getImage(myImg, PdfPCell.ALIGN_CENTER));
            table.addCell(getCell(signatureDate, PdfPCell.ALIGN_CENTER));
            consentItem.add(table);


            PdfPTable table1 = new PdfPTable(3);
            table1.setWidthPercentage(100);
            table1.addCell(getCell(getResources().getString(R.string.participans_name), PdfPCell.ALIGN_CENTER));
            table1.addCell(getCell(getResources().getString(R.string.participants_signature), PdfPCell.ALIGN_CENTER));
            table1.addCell(getCell(getResources().getString(R.string.date), PdfPCell.ALIGN_CENTER));
            consentItem.add(table1);

            document.add(consentItem);
            document.close();
        } catch (IOException | DocumentException e) {
            Toast.makeText(this, R.string.not_able_create_pdf, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public PdfPCell getImage(Image image, int alignment) {
        PdfPCell cell;
        if (image != null) {
            cell = new PdfPCell(image);
        } else {
            cell = new PdfPCell();
        }
        cell.setPadding(10);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(PdfPCell.ALIGN_BOTTOM);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }

    public PdfPCell getCell(String text, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text));
        cell.setPadding(10);
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(PdfPCell.ALIGN_BOTTOM);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }


    private void getFile(String s) {
        file = new File(s, FILE_FOLDER);
        if (!file.exists()) {
            file.mkdirs();
        }
    }
}
