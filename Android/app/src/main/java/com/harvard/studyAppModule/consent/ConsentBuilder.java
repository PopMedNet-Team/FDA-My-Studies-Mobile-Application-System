package com.harvard.studyAppModule.consent;

import android.content.Context;

import com.harvard.EligibilityModule.StepsBuilder;
import com.harvard.R;
import com.harvard.studyAppModule.activityBuilder.model.serviceModel.Steps;
import com.harvard.studyAppModule.consent.ConsentSharingStepCustom.ConsentSharingStepCustom;
import com.harvard.studyAppModule.consent.model.Consent;
import com.harvard.studyAppModule.consent.model.ConsentSectionCustomImage;

import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.answerformat.ChoiceAnswerFormat;
import org.researchstack.backbone.answerformat.TextAnswerFormat;
import org.researchstack.backbone.model.Choice;
import org.researchstack.backbone.model.ConsentSection;
import org.researchstack.backbone.model.ConsentSignature;
import org.researchstack.backbone.step.ConsentDocumentStep;
import org.researchstack.backbone.step.ConsentSignatureStep;
import org.researchstack.backbone.step.ConsentVisualStep;
import org.researchstack.backbone.step.FormStep;
import org.researchstack.backbone.step.InstructionStep;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.ui.step.layout.ConsentSignatureStepLayout;

import java.util.ArrayList;
import java.util.List;

import io.realm.RealmList;

/**
 * Created by Rohit on 2/24/2017.
 */

public class ConsentBuilder {


    public ArrayList<Step> createsurveyquestion(Context context, Consent consent, String pdftitle) {
        ArrayList<Step> visualSteps = new ArrayList<>();
        ConsentSection consentSection;
        ConsentVisualStep visualStep;
        for (int i = 0; i < consent.getVisualScreens().size(); i++) {
            if (consent.getVisualScreens().get(i).isVisualStep()) {
                if (!consent.getVisualScreens().get(i).getType().equalsIgnoreCase("Custom")) {
                    switch (consent.getVisualScreens().get(i).getType().toLowerCase()) {
                        case "datagathering":
                            consentSection = new ConsentSection(ConsentSection.Type.DataGathering);
                            consentSection.setTitle(consent.getVisualScreens().get(i).getTitle());
                            consentSection.setContent(consent.getVisualScreens().get(i).getDescription());
                            consentSection.setSummary(consent.getVisualScreens().get(i).getText());
                            consentSection.setHtmlContent(consent.getVisualScreens().get(i).getHtml());

                            visualStep = new ConsentVisualStep(consent.getVisualScreens().get(i).getTitle());
                            visualStep.setStepTitle(R.string.notxt);
                            visualStep.setSection(consentSection);
                            visualStep.setNextButtonString(context.getResources().getString(R.string.next1));
                            visualSteps.add(visualStep);
                            break;
                        case "datause":
                            consentSection = new ConsentSection(ConsentSection.Type.DataUse);
                            consentSection.setTitle(consent.getVisualScreens().get(i).getTitle());
                            consentSection.setContent(consent.getVisualScreens().get(i).getDescription());
                            consentSection.setSummary(consent.getVisualScreens().get(i).getText());
                            consentSection.setHtmlContent(consent.getVisualScreens().get(i).getHtml());

                            visualStep = new ConsentVisualStep(consent.getVisualScreens().get(i).getTitle());
                            visualStep.setStepTitle(R.string.notxt);
                            visualStep.setSection(consentSection);
                            visualStep.setNextButtonString(context.getResources().getString(R.string.next1));
                            visualSteps.add(visualStep);
                            break;
                        case "onlyindocument":
                            consentSection = new ConsentSection(ConsentSection.Type.OnlyInDocument);
                            consentSection.setTitle(consent.getVisualScreens().get(i).getTitle());
                            consentSection.setContent(consent.getVisualScreens().get(i).getDescription());
                            consentSection.setSummary(consent.getVisualScreens().get(i).getText());
                            consentSection.setHtmlContent(consent.getVisualScreens().get(i).getHtml());

                            visualStep = new ConsentVisualStep(consent.getVisualScreens().get(i).getTitle());
                            visualStep.setStepTitle(R.string.notxt);
                            visualStep.setSection(consentSection);
                            visualStep.setNextButtonString(context.getResources().getString(R.string.next1));
                            visualSteps.add(visualStep);
                            break;
                        case "overview":
                            consentSection = new ConsentSection(ConsentSection.Type.Overview);
                            consentSection.setTitle(consent.getVisualScreens().get(i).getTitle());
                            consentSection.setContent(consent.getVisualScreens().get(i).getDescription());
                            consentSection.setSummary(consent.getVisualScreens().get(i).getText());
                            consentSection.setHtmlContent(consent.getVisualScreens().get(i).getHtml());

                            visualStep = new ConsentVisualStep(consent.getVisualScreens().get(i).getTitle());
                            visualStep.setStepTitle(R.string.notxt);
                            visualStep.setSection(consentSection);
                            visualStep.setNextButtonString(context.getResources().getString(R.string.next1));
                            visualSteps.add(visualStep);
                            break;
                        case "privacy":
                            consentSection = new ConsentSection(ConsentSection.Type.Privacy);
                            consentSection.setTitle(consent.getVisualScreens().get(i).getTitle());
                            consentSection.setContent(consent.getVisualScreens().get(i).getDescription());
                            consentSection.setSummary(consent.getVisualScreens().get(i).getText());
                            consentSection.setHtmlContent(consent.getVisualScreens().get(i).getHtml());

                            visualStep = new ConsentVisualStep(consent.getVisualScreens().get(i).getTitle());
                            visualStep.setStepTitle(R.string.notxt);
                            visualStep.setSection(consentSection);
                            visualStep.setNextButtonString(context.getResources().getString(R.string.next1));
                            visualSteps.add(visualStep);
                            break;
                        case "studysurvey":
                            consentSection = new ConsentSection(ConsentSection.Type.StudySurvey);
                            consentSection.setTitle(consent.getVisualScreens().get(i).getTitle());
                            consentSection.setContent(consent.getVisualScreens().get(i).getDescription());
                            consentSection.setSummary(consent.getVisualScreens().get(i).getText());
                            consentSection.setHtmlContent(consent.getVisualScreens().get(i).getHtml());

                            visualStep = new ConsentVisualStep(consent.getVisualScreens().get(i).getTitle());
                            visualStep.setStepTitle(R.string.notxt);
                            visualStep.setSection(consentSection);
                            visualStep.setNextButtonString(context.getResources().getString(R.string.next1));
                            visualSteps.add(visualStep);
                            break;
                        case "studytasks":
                            consentSection = new ConsentSection(ConsentSection.Type.StudyTasks);
                            consentSection.setTitle(consent.getVisualScreens().get(i).getTitle());
                            consentSection.setContent(consent.getVisualScreens().get(i).getDescription());
                            consentSection.setSummary(consent.getVisualScreens().get(i).getText());
                            consentSection.setHtmlContent(consent.getVisualScreens().get(i).getHtml());

                            visualStep = new ConsentVisualStep(consent.getVisualScreens().get(i).getTitle());
                            visualStep.setStepTitle(R.string.notxt);
                            visualStep.setSection(consentSection);
                            visualStep.setNextButtonString(context.getResources().getString(R.string.next1));
                            visualSteps.add(visualStep);
                            break;
                        case "timecommitment":
                            consentSection = new ConsentSection(ConsentSection.Type.TimeCommitment);
                            consentSection.setTitle(consent.getVisualScreens().get(i).getTitle());
                            consentSection.setContent(consent.getVisualScreens().get(i).getDescription());
                            consentSection.setSummary(consent.getVisualScreens().get(i).getText());
                            consentSection.setHtmlContent(consent.getVisualScreens().get(i).getHtml());

                            visualStep = new ConsentVisualStep(consent.getVisualScreens().get(i).getTitle());
                            visualStep.setStepTitle(R.string.notxt);
                            visualStep.setSection(consentSection);
                            visualStep.setNextButtonString(context.getResources().getString(R.string.next1));
                            visualSteps.add(visualStep);
                            break;
                        case "withdrawing":
                            consentSection = new ConsentSection(ConsentSection.Type.Withdrawing);
                            consentSection.setTitle(consent.getVisualScreens().get(i).getTitle());
                            consentSection.setContent(consent.getVisualScreens().get(i).getDescription());
                            consentSection.setSummary(consent.getVisualScreens().get(i).getText());
                            consentSection.setHtmlContent(consent.getVisualScreens().get(i).getHtml());

                            visualStep = new ConsentVisualStep(consent.getVisualScreens().get(i).getTitle());
                            visualStep.setStepTitle(R.string.notxt);
                            visualStep.setSection(consentSection);
                            visualStep.setNextButtonString(context.getResources().getString(R.string.next1));
                            visualSteps.add(visualStep);

                            break;
                    }
                } else {
                    // custom consent
                    ConsentSectionCustomImage consentSection1 = new ConsentSectionCustomImage(ConsentSection.Type.Custom);
                    consentSection1.setTitle(consent.getVisualScreens().get(i).getTitle());
                    consentSection1.setContent(consent.getVisualScreens().get(i).getDescription());
                    consentSection1.setSummary(consent.getVisualScreens().get(i).getText());
                    consentSection1.setHtmlContent(consent.getVisualScreens().get(i).getHtml());
                    consentSection1.setCustomImageName("task_img2");

                    visualStep = new ConsentVisualStep(consent.getVisualScreens().get(i).getTitle());
                    visualStep.setStepTitle(R.string.notxt);
                    visualStep.setSection(consentSection1);
                    visualStep.setNextButtonString(context.getResources().getString(R.string.next1));
                    visualSteps.add(visualStep);
                }
            }
        }

        if (consent.getComprehension() != null && consent.getComprehension().getQuestions() != null && consent.getComprehension().getQuestions().size() > 0) {

            InstructionStep instructionStep = new InstructionStep("key", "Comprehension", "Let's do a quick and simple test of your understanding of this Study.");
            instructionStep.setStepTitle(R.string.notxt);
            instructionStep.setOptional(false);
            visualSteps.add(instructionStep);


            RealmList<Steps> stepsRealmList = consent.getComprehension().getQuestions();
            StepsBuilder stepsBuilder = new StepsBuilder(context, stepsRealmList, true);

            visualSteps.addAll(stepsBuilder.getsteps());
        }

        if (!consent.getSharing().getTitle().equalsIgnoreCase("") && !consent.getSharing().getText().equalsIgnoreCase("") && !consent.getSharing().getShortDesc().equalsIgnoreCase("") && !consent.getSharing().getLongDesc().equalsIgnoreCase("")) {
            ConsentSharingStepCustom consentSharingStep = new ConsentSharingStepCustom("sharing", consent.getSharing().getLearnMore());
            consentSharingStep.setText(consent.getSharing().getText());
            consentSharingStep.setTitle(consent.getSharing().getTitle());
            Choice[] choices = new Choice[2];
            choices[0] = new Choice("Share my data with " + consent.getSharing().getShortDesc() + " and qualified researchers worldwide", "yes", "yes");
            choices[1] = new Choice("Only share my data with " + consent.getSharing().getLongDesc(), "no", "no");

            AnswerFormat choiceAnswerFormat = new ChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle.SingleChoice, choices);
            consentSharingStep.setAnswerFormat(choiceAnswerFormat);
            consentSharingStep.setOptional(false);
            consentSharingStep.setStepTitle(R.string.notxt);
            visualSteps.add(consentSharingStep);
        }

        if (consent.getReview() != null && consent.getReview().getSignatureContent() != null && !consent.getReview().getSignatureContent().equalsIgnoreCase("")) {
            StringBuilder docBuilder = new StringBuilder(
                    "</br><div style=\"padding: 10px 10px 10px 10px;\" class='header'>");
            String title = context.getString(R.string.review);
            docBuilder.append(String.format("<h1 style=\"text-align: center; font-family:sans-serif-light;color:#007cba;\">%1$s</h1>", title));
            String detail = context.getString(R.string.reviewmsg);
            docBuilder.append(String.format("<p style=\"text-align: center\">%1$s</p>", detail));
            docBuilder.append("</div></br>");
            docBuilder.append("<div> <h2 style=\"color:#007cba;\"> " + pdftitle + "<h2> </div>");
            docBuilder.append("</div></br>");
            docBuilder.append("<div>" + consent.getReview().getSignatureContent() + "</div>");

            ConsentDocumentStep documentStep = new ConsentDocumentStep("review");
            documentStep.setConsentHTML(docBuilder.toString());
            documentStep.setStepTitle(R.string.notxt);
            documentStep.setConfirmMessage(consent.getReview().getReasonForConsent());
            visualSteps.add(documentStep);
        } else {
            if (consent.getVisualScreens().size() > 0) {
                // Create our HTML to show the user and have them accept or decline.
                StringBuilder docBuilder = new StringBuilder(
                        "</br><div style=\"padding: 10px 10px 10px 10px;\" class='header'>");
                String title = context.getString(R.string.review);
                docBuilder.append(String.format("<h1 style=\"text-align: center; font-family:sans-serif-light;color:#007cba;\">%1$s</h1>", title));
                String detail = context.getString(R.string.reviewmsg);
                docBuilder.append(String.format("<p style=\"text-align: center\">%1$s</p>", detail));
                docBuilder.append("</div></br>");
                docBuilder.append("<div> <h2 style=\"font-family:sans-serif-light;color:#007cba;\"> " + pdftitle + " <h2> </div>");
                docBuilder.append("</div></br>");
                for (int i = 0; i < consent.getVisualScreens().size(); i++) {
                    docBuilder.append("<div> <h3 style=\"font-family:sans-serif-light;color:#007cba;\"> " + consent.getVisualScreens().get(i).getTitle() + "<h3> </div>");
                    docBuilder.append("</br>");
                    docBuilder.append("<div>" + consent.getVisualScreens().get(i).getHtml() + "</div>");
                    docBuilder.append("</br>");
                }
                ConsentDocumentStep documentStep = new ConsentDocumentStep("review");
                documentStep.setConsentHTML(docBuilder.toString());
                documentStep.setStepTitle(R.string.notxt);
                documentStep.setOptional(false);
                documentStep.setConfirmMessage(consent.getReview().getReasonForConsent());
                visualSteps.add(documentStep);


            }
        }


        FormStep formStep = new FormStep(context.getResources().getString(R.string.signature_form_step),
                "",
                "");
        formStep.setStepTitle(R.string.notxt);

        TextAnswerFormat format = new TextAnswerFormat();
        format.setIsMultipleLines(false);

        QuestionStep fullName = new QuestionStep(context.getResources().getString(R.string.first_name1), context.getResources().getString(R.string.first_name2), format);
        QuestionStep lastName = new QuestionStep(context.getResources().getString(R.string.last_name1), context.getResources().getString(R.string.last_name2), format);
        fullName.setPlaceholder(context.getResources().getString(R.string.first_name3));
        lastName.setPlaceholder(context.getResources().getString(R.string.last_name3));
        List<QuestionStep> questionSteps = new ArrayList<>();
        questionSteps.add(fullName);
        questionSteps.add(lastName);
        formStep.setFormSteps(questionSteps);
        formStep.setOptional(false);
        visualSteps.add(formStep);

        ConsentSignature signature = new ConsentSignature();
        signature.setRequiresName(true);
        signature.setRequiresSignatureImage(true);

        ConsentSignatureStep signatureStep = new ConsentSignatureStep(context.getResources().getString(R.string.signature));
        signatureStep.setStepTitle(R.string.notxt);
        signatureStep.setTitle(context.getString(R.string.signtitle));
        signatureStep.setText(context.getString(R.string.signdesc));
        signatureStep.setSignatureDateFormat(signature.getSignatureDateFormatString());
        signatureStep.setOptional(false);
        signatureStep.setStepLayoutClass(ConsentSignatureStepLayout.class);

        visualSteps.add(signatureStep);
        return visualSteps;
    }
}
