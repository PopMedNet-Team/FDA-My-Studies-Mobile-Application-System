package com.harvard.studyAppModule.activityBuilder;

import android.content.Context;
import android.util.Log;

import com.harvard.R;
import com.harvard.studyAppModule.activityBuilder.model.serviceModel.ActivityObj;
import com.harvard.studyAppModule.activityBuilder.model.serviceModel.Steps;
import com.harvard.studyAppModule.custom.AnswerFormatCustom;
import com.harvard.studyAppModule.custom.ChoiceAnswerFormatCustom;
import com.harvard.studyAppModule.custom.QuestionStepCustom;
import com.harvard.studyAppModule.custom.activeTask.TappingAnswerFormat;
import com.harvard.studyAppModule.custom.question.ChoiceCustomImage;
import com.harvard.studyAppModule.custom.question.ChoiceText;
import com.harvard.studyAppModule.custom.question.ChoiceTextExclusive;
import com.harvard.studyAppModule.custom.question.ContinousScaleAnswerFormat;
import com.harvard.studyAppModule.custom.question.DateAnswerformatCustom;
import com.harvard.studyAppModule.custom.question.DecimalUnitAnswerFormat;
import com.harvard.studyAppModule.custom.question.EmailAnswerFormatCustom;
import com.harvard.studyAppModule.custom.question.HeightAnswerFormat;
import com.harvard.studyAppModule.custom.question.IntegerUnitAnswerFormat;
import com.harvard.studyAppModule.custom.question.LocationAnswerFormat;
import com.harvard.studyAppModule.custom.question.MultiChoiceImageAnswerFormat;
import com.harvard.studyAppModule.custom.question.MultiChoiceTextAnswerFormat;
import com.harvard.studyAppModule.custom.question.ScaleAnswerFormat;
import com.harvard.studyAppModule.custom.question.ScaleTextAnswerFormat;
import com.harvard.studyAppModule.custom.question.SingleChoiceTextAnswerFormat;
import com.harvard.studyAppModule.custom.question.TaskInstructionAnswerFormat;
import com.harvard.studyAppModule.custom.question.TaskIntroductionAnswerFormat;
import com.harvard.studyAppModule.custom.question.TextAnswerFormatRegex;
import com.harvard.studyAppModule.custom.question.TimeIntervalAnswerFormat;
import com.harvard.utils.AppController;

import org.researchstack.backbone.answerformat.BooleanAnswerFormat;
import org.researchstack.backbone.model.Choice;
import org.researchstack.backbone.step.InstructionStep;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by Naveen Raj on 06/07/2017.
 */

public class StepsBuilder {
    private List<Step> steps;

    public StepsBuilder(Context context, ActivityObj activityobj, boolean branching, Realm realm) {
        steps = createsurveyquestion(context, activityobj, branching, realm);
    }

    public List<Step> getsteps() {
        return steps;
    }

    private List<Step> createsurveyquestion(Context context, ActivityObj activityobj, boolean branching, Realm realm) {
        ArrayList<Step> steps = new ArrayList<>();
        RealmList<Steps> activityQuestionStep = activityobj.getSteps();
        if (activityQuestionStep != null)
            for (int i = 0; i < activityQuestionStep.size(); i++) {
                if (activityQuestionStep.get(i).getType().equalsIgnoreCase("question")) {
                    switch (activityQuestionStep.get(i).getResultType()) {
                        case "scale":
                            ScaleAnswerFormat ScaleFormat = new ScaleAnswerFormat(ChoiceAnswerFormatCustom.CustomAnswerStyle.Scale, activityQuestionStep.get(i).getFormat().getStep(), activityQuestionStep.get(i).getFormat().getMinValue(), activityQuestionStep.get(i).getFormat().getMaxValue(), activityQuestionStep.get(i).getFormat().isVertical(), activityQuestionStep.get(i).getFormat().getMaxDesc(), activityQuestionStep.get(i).getFormat().getMinDesc(), activityQuestionStep.get(i).getFormat().getMaxImage(), activityQuestionStep.get(i).getFormat().getMinImage(), activityQuestionStep.get(i).getFormat().getDefaultValue());

                            QuestionStepCustom scaleStep = new QuestionStepCustom(activityQuestionStep.get(i).getKey(), context.getResources().getString(R.string.survey), ScaleFormat);
                            scaleStep.setAnswerFormat1(ScaleFormat);
                            if (branching)
                                scaleStep.setStepTitle(R.string.notxt);
                            scaleStep.setTitle(activityQuestionStep.get(i).getTitle());
                            scaleStep.setText(activityQuestionStep.get(i).getText().replaceAll("(\r\n|\n)", "<br />"));
                            scaleStep.setOptional(activityQuestionStep.get(i).isSkippable());
                            steps.add(scaleStep);
                            break;
                        case "continuousScale":
                            ContinousScaleAnswerFormat ContinousScaleFormat = new ContinousScaleAnswerFormat(ChoiceAnswerFormatCustom.CustomAnswerStyle.Scale, activityQuestionStep.get(i).getFormat().getMaxFractionDigits(), activityQuestionStep.get(i).getFormat().getMinValue(), activityQuestionStep.get(i).getFormat().getMaxValue(), activityQuestionStep.get(i).getFormat().isVertical(), activityQuestionStep.get(i).getFormat().getMaxDesc(), activityQuestionStep.get(i).getFormat().getMinDesc(), activityQuestionStep.get(i).getFormat().getMaxImage(), activityQuestionStep.get(i).getFormat().getMinImage(), activityQuestionStep.get(i).getFormat().getDefaultValue());
                            QuestionStepCustom continousscaleStep = new QuestionStepCustom(activityQuestionStep.get(i).getKey(), context.getResources().getString(R.string.survey), ContinousScaleFormat);
                            continousscaleStep.setAnswerFormat1(ContinousScaleFormat);
                            if (branching)
                                continousscaleStep.setStepTitle(R.string.notxt);
                            continousscaleStep.setTitle(activityQuestionStep.get(i).getTitle());
                            continousscaleStep.setText(activityQuestionStep.get(i).getText().replaceAll("(\r\n|\n)", "<br />"));
                            continousscaleStep.setOptional(activityQuestionStep.get(i).isSkippable());
                            steps.add(continousscaleStep);
                            break;
                        case "textScale":
                            ChoiceTextExclusive[] textScaleChoices = new ChoiceTextExclusive[activityQuestionStep.get(i).getFormat().getTextChoices().size()];
                            for (int j = 0; j < activityQuestionStep.get(i).getFormat().getTextChoices().size(); j++) {
                                textScaleChoices[j] = new ChoiceTextExclusive(activityQuestionStep.get(i).getFormat().getTextChoices().get(j).getText(), activityQuestionStep.get(i).getFormat().getTextChoices().get(j).getValue(), activityQuestionStep.get(i).getFormat().getTextChoices().get(j).getDetail(), activityQuestionStep.get(i).getFormat().getTextChoices().get(j).isExclusive(), null);
                            }

                            ScaleTextAnswerFormat ScaleTextFormat = new ScaleTextAnswerFormat(ChoiceAnswerFormatCustom.CustomAnswerStyle.ScaleText, textScaleChoices, activityQuestionStep.get(i).getFormat().getDefaultValue(), activityQuestionStep.get(i).getFormat().isVertical());

                            QuestionStepCustom scaleTextStep = new QuestionStepCustom(activityQuestionStep.get(i).getKey(), context.getResources().getString(R.string.survey), ScaleTextFormat);
                            scaleTextStep.setAnswerFormat1(ScaleTextFormat);
                            if (branching)
                                scaleTextStep.setStepTitle(R.string.notxt);
                            scaleTextStep.setTitle(activityQuestionStep.get(i).getTitle());
                            scaleTextStep.setText(activityQuestionStep.get(i).getText().replaceAll("(\r\n|\n)", "<br />"));
                            scaleTextStep.setOptional(activityQuestionStep.get(i).isSkippable());
                            steps.add(scaleTextStep);
                            break;
                        case "valuePicker":
                            QuestionStepCustom valuepicker = new QuestionStepCustom(activityQuestionStep.get(i).getKey());
                            if (branching)
                                valuepicker.setStepTitle(R.string.notxt);
                            Choice[] valuechoice = new Choice[activityQuestionStep.get(i).getFormat().getTextChoices().size()];
                            for (int j = 0; j < activityQuestionStep.get(i).getFormat().getTextChoices().size(); j++) {
                                valuechoice[j] = new Choice(activityQuestionStep.get(i).getFormat().getTextChoices().get(j).getText(), activityQuestionStep.get(i).getFormat().getTextChoices().get(j).getValue(), activityQuestionStep.get(i).getFormat().getTextChoices().get(j).getDetail());
                            }
                            ChoiceAnswerFormatCustom pickerformat = new ChoiceAnswerFormatCustom(AnswerFormatCustom.CustomAnswerStyle.valuePicker, valuepicker, valuechoice);
                            valuepicker.setTitle(activityobj.getSteps().get(i).getTitle());
                            valuepicker.setAnswerFormat1(pickerformat);
                            valuepicker.setText(activityQuestionStep.get(i).getText().replaceAll("(\r\n|\n)", "<br />"));
                            valuepicker.setOptional(activityQuestionStep.get(i).isSkippable());
                            steps.add(valuepicker);
                            break;
                        case "imageChoice":
                            QuestionStepCustom multichoiceStep = new QuestionStepCustom(activityobj.getSteps().get(i).getKey());
                            if (branching)
                                multichoiceStep.setStepTitle(R.string.notxt);
                            ChoiceCustomImage[] choicechoices = new ChoiceCustomImage[activityQuestionStep.get(i).getFormat().getImageChoices().size()];
                            for (int j = 0; j < activityQuestionStep.get(i).getFormat().getImageChoices().size(); j++) {
                                choicechoices[j] = new ChoiceCustomImage(activityQuestionStep.get(i).getFormat().getImageChoices().get(j).getText(), activityQuestionStep.get(i).getFormat().getImageChoices().get(j).getValue(), activityQuestionStep.get(i).getFormat().getImageChoices().get(j).getImage(), activityQuestionStep.get(i).getFormat().getImageChoices().get(j).getSelectedImage());
                            }
                            MultiChoiceImageAnswerFormat multichoiceFormat = new MultiChoiceImageAnswerFormat(ChoiceAnswerFormatCustom.CustomAnswerStyle.MultipleImageChoice, choicechoices);
                            multichoiceStep.setTitle(activityobj.getSteps().get(i).getTitle());
                            multichoiceStep.setText(activityQuestionStep.get(i).getText().replaceAll("(\r\n|\n)", "<br />"));
                            multichoiceStep.setAnswerFormat1(multichoiceFormat);
                            multichoiceStep.setOptional(activityQuestionStep.get(i).isSkippable());
                            steps.add(multichoiceStep);
                            break;
                        case "textChoice":
                            if (activityQuestionStep.get(i).getFormat().getSelectionStyle().equalsIgnoreCase("Single")) {
                                ChoiceText[] choices = new ChoiceText[activityQuestionStep.get(i).getFormat().getTextChoices().size()];
                                for (int j = 0; j < activityQuestionStep.get(i).getFormat().getTextChoices().size(); j++) {
                                    choices[j] = new ChoiceText(activityQuestionStep.get(i).getFormat().getTextChoices().get(j).getText(), activityQuestionStep.get(i).getFormat().getTextChoices().get(j).getValue(), activityQuestionStep.get(i).getFormat().getTextChoices().get(j).getDetail(),activityQuestionStep.get(i).getFormat().getTextChoices().get(j).getOther());
                                }
                                SingleChoiceTextAnswerFormat choiceAnswerFormat = new SingleChoiceTextAnswerFormat(AnswerFormatCustom.CustomAnswerStyle.SingleTextChoice, choices);
                                QuestionStepCustom multiStep = new QuestionStepCustom(activityobj.getSteps().get(i).getKey());
                                if (branching)
                                    multiStep.setStepTitle(R.string.notxt);
                                multiStep.setTitle(activityobj.getSteps().get(i).getTitle());
                                multiStep.setText(activityQuestionStep.get(i).getText().replaceAll("(\r\n|\n)", "<br />"));
                                multiStep.setAnswerFormat1(choiceAnswerFormat);
                                multiStep.setOptional(activityQuestionStep.get(i).isSkippable());
                                steps.add(multiStep);
                            } else {
                                ChoiceTextExclusive[] choices = new ChoiceTextExclusive[activityQuestionStep.get(i).getFormat().getTextChoices().size()];
                                for (int j = 0; j < activityQuestionStep.get(i).getFormat().getTextChoices().size(); j++) {
                                    choices[j] = new ChoiceTextExclusive(activityQuestionStep.get(i).getFormat().getTextChoices().get(j).getText(), activityQuestionStep.get(i).getFormat().getTextChoices().get(j).getValue(), activityQuestionStep.get(i).getFormat().getTextChoices().get(j).getDetail(), activityQuestionStep.get(i).getFormat().getTextChoices().get(j).isExclusive(), activityQuestionStep.get(i).getFormat().getTextChoices().get(j).getOther());
                                }
                                MultiChoiceTextAnswerFormat choiceAnswerFormat = new MultiChoiceTextAnswerFormat(AnswerFormatCustom.CustomAnswerStyle.MultipleTextChoice, choices);
                                QuestionStepCustom multiStep = new QuestionStepCustom(activityobj.getSteps().get(i).getKey());
                                if (branching)
                                    multiStep.setStepTitle(R.string.notxt);
                                multiStep.setTitle(activityobj.getSteps().get(i).getTitle());
                                multiStep.setText(activityQuestionStep.get(i).getText().replaceAll("(\r\n|\n)", "<br />"));
                                multiStep.setAnswerFormat1(choiceAnswerFormat);
                                multiStep.setOptional(activityQuestionStep.get(i).isSkippable());
                                steps.add(multiStep);
                            }
                            break;
                        case "boolean":
                            QuestionStep booleanStep = new QuestionStep(activityobj.getSteps().get(i).getKey());
                            if (branching)
                                booleanStep.setStepTitle(R.string.notxt);
                            booleanStep.setTitle(activityobj.getSteps().get(i).getTitle());
                            booleanStep.setText(activityQuestionStep.get(i).getText().replaceAll("(\r\n|\n)", "<br />"));
                            booleanStep.setAnswerFormat(new BooleanAnswerFormat(context.getResources().getString(R.string.yes), context.getResources().getString(R.string.no)));
                            booleanStep.setOptional(activityQuestionStep.get(i).isSkippable());
                            steps.add(booleanStep);
                            break;
                        case "numeric":
                            ChoiceAnswerFormatCustom answerFormat;
                            if (activityQuestionStep.get(i).getFormat().getStyle().equalsIgnoreCase("Integer")) {
                                if (activityQuestionStep.get(i).getFormat().getMinValue() == 0 && activityQuestionStep.get(i).getFormat().getMaxValue() == 0) {
                                    realm.beginTransaction();
                                    activityQuestionStep.get(i).getFormat().setMaxValue(Integer.MAX_VALUE);
                                    realm.commitTransaction();
                                }
                                answerFormat = new IntegerUnitAnswerFormat(activityQuestionStep.get(i).getFormat().getMinValue(), activityQuestionStep.get(i).getFormat().getMaxValue(), activityQuestionStep.get(i).getFormat().getUnit());
                            } else {
                                if (activityQuestionStep.get(i).getFormat().getMinValue() == 0 && activityQuestionStep.get(i).getFormat().getMaxValue() == 0) {
                                    answerFormat = new DecimalUnitAnswerFormat(activityQuestionStep.get(i).getFormat().getMinValue(), Float.MAX_VALUE, activityQuestionStep.get(i).getFormat().getUnit());
                                } else {
                                    answerFormat = new DecimalUnitAnswerFormat(activityQuestionStep.get(i).getFormat().getMinValue(), activityQuestionStep.get(i).getFormat().getMaxValue(), activityQuestionStep.get(i).getFormat().getUnit());
                                }
                            }
                            QuestionStepCustom numericItem = new QuestionStepCustom(activityobj.getSteps().get(i).getKey(), activityobj.getSteps().get(i).getTitle(), answerFormat);
                            numericItem.setText(activityobj.getSteps().get(i).getText().replaceAll("(\r\n|\n)", "<br />"));
                            numericItem.setPlaceholder(activityQuestionStep.get(i).getFormat().getPlaceholder());
                            if (branching)
                                numericItem.setStepTitle(R.string.notxt);
                            numericItem.setText(activityQuestionStep.get(i).getText().replaceAll("(\r\n|\n)", "<br />"));
                            numericItem.setAnswerFormat1(answerFormat);
                            numericItem.setOptional(activityQuestionStep.get(i).isSkippable());
                            steps.add(numericItem);
                            break;
                        case "timeOfDay":
                            QuestionStepCustom timeOfDayStep = new QuestionStepCustom(activityobj.getSteps().get(i).getKey(), context.getResources().getString(R.string.survey));
                            DateAnswerformatCustom customChoiceAnswerFormat = new DateAnswerformatCustom(AnswerFormatCustom.DateAnswerStyle.TimeOfDay);
                            timeOfDayStep.setAnswerFormat1(customChoiceAnswerFormat);
                            if (branching)
                                timeOfDayStep.setStepTitle(R.string.notxt);
                            timeOfDayStep.setPlaceholder(context.getResources().getString(R.string.enter_time));
                            timeOfDayStep.setTitle(activityobj.getSteps().get(i).getTitle());
                            timeOfDayStep.setText(activityQuestionStep.get(i).getText().replaceAll("(\r\n|\n)", "<br />"));
                            timeOfDayStep.setOptional(activityQuestionStep.get(i).isSkippable());
                            steps.add(timeOfDayStep);
                            break;
                        case "date":
                            SimpleDateFormat simpleDateFormat = AppController.getDateFormat();
                            Date mindate = null, maxdate = null, defaultval = null;
                            try {
                                mindate = simpleDateFormat.parse(activityQuestionStep.get(i).getFormat().getMinDate());
                                maxdate = simpleDateFormat.parse(activityQuestionStep.get(i).getFormat().getMaxDate());
                                if (activityQuestionStep.get(i).getFormat().getDefaultValue() != null && !activityQuestionStep.get(i).getFormat().getDefaultValue().equalsIgnoreCase(""))
                                    defaultval = simpleDateFormat.parse(activityQuestionStep.get(i).getFormat().getDefaultValue());
                                else
                                    defaultval = Calendar.getInstance().getTime();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            DateAnswerformatCustom dateFormat;
                            if (activityQuestionStep.get(i).getFormat().getStyle().equalsIgnoreCase("Date")) {
                                if (maxdate != null) {
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTime(maxdate);
                                    calendar.set(Calendar.HOUR, 23);
                                    calendar.set(Calendar.MINUTE, 59);
                                    calendar.set(Calendar.SECOND, 59);
                                    maxdate = calendar.getTime();
                                }
                                dateFormat = new DateAnswerformatCustom(AnswerFormatCustom.DateAnswerStyle.Date, defaultval, mindate, maxdate, activityobj.getSteps().get(i).getFormat().getDateRange());
                            } else {
                                dateFormat = new DateAnswerformatCustom(AnswerFormatCustom.DateAnswerStyle.DateAndTime, defaultval, mindate, maxdate, activityobj.getSteps().get(i).getFormat().getDateRange());
                            }
                            QuestionStepCustom dateFormatItem = new QuestionStepCustom(activityobj.getSteps().get(i).getKey(), activityobj.getSteps().get(i).getTitle(), dateFormat);
                            if (branching)
                                dateFormatItem.setStepTitle(R.string.notxt);
                            dateFormatItem.setText(activityQuestionStep.get(i).getText().replaceAll("(\r\n|\n)", "<br />"));
                            dateFormatItem.setAnswerFormat1(dateFormat);
                            dateFormatItem.setOptional(activityQuestionStep.get(i).isSkippable());
                            steps.add(dateFormatItem);
                            break;
                        case "text":
                            TextAnswerFormatRegex textAnswerFormat = new TextAnswerFormatRegex(activityQuestionStep.get(i).getFormat().getMaxLength(), activityQuestionStep.get(i).getFormat().getValidationRegex(), activityQuestionStep.get(i).getFormat().getInvalidMessage());
                            textAnswerFormat.setIsMultipleLines(activityQuestionStep.get(i).getFormat().isMultipleLines());
                            QuestionStepCustom textstep = new QuestionStepCustom(activityobj.getSteps().get(i).getKey(), activityobj.getSteps().get(i).getTitle(), textAnswerFormat);
                            if (branching)
                                textstep.setStepTitle(R.string.notxt);
                            textstep.setText(activityQuestionStep.get(i).getText().replaceAll("(\r\n|\n)", "<br />"));
                            textstep.setPlaceholder(activityQuestionStep.get(i).getFormat().getPlaceholder());
                            textstep.setAnswerFormat1(textAnswerFormat);
                            textstep.setOptional(activityQuestionStep.get(i).isSkippable());
                            steps.add(textstep);
                            break;
                        case "email":
                            EmailAnswerFormatCustom emailformat = new EmailAnswerFormatCustom(255);
                            QuestionStepCustom emailstep = new QuestionStepCustom(activityobj.getSteps().get(i).getKey(), activityobj.getSteps().get(i).getTitle(), emailformat);
                            emailstep.setPlaceholder(activityQuestionStep.get(i).getFormat().getPlaceholder());
                            emailstep.setText(activityQuestionStep.get(i).getText().replaceAll("(\r\n|\n)", "<br />"));
                            if (branching)
                                emailstep.setStepTitle(R.string.notxt);
                            emailstep.setOptional(activityQuestionStep.get(i).isSkippable());
                            steps.add(emailstep);
                            break;
                        case "timeInterval":
                            QuestionStepCustom timeIntervalStep = new QuestionStepCustom(activityobj.getSteps().get(i).getKey(), context.getResources().getString(R.string.survey));
                            TimeIntervalAnswerFormat timeIntervalAnswerFormat = new TimeIntervalAnswerFormat(ChoiceAnswerFormatCustom.CustomAnswerStyle.TimeInterval, activityobj.getSteps().get(i).getFormat().getStep(), activityobj.getSteps().get(i).getFormat().getDefaultValue());
                            timeIntervalStep.setAnswerFormat1(timeIntervalAnswerFormat);
                            if (branching)
                                timeIntervalStep.setStepTitle(R.string.notxt);
                            timeIntervalStep.setTitle(activityobj.getSteps().get(i).getTitle());
                            timeIntervalStep.setText(activityQuestionStep.get(i).getText().replaceAll("(\r\n|\n)", "<br />"));
                            timeIntervalStep.setOptional(activityobj.getSteps().get(i).isSkippable());
                            steps.add(timeIntervalStep);
                            break;
                        case "height":
                            QuestionStepCustom HeightStep = new QuestionStepCustom(activityobj.getSteps().get(i).getKey(), context.getResources().getString(R.string.survey));
                            HeightAnswerFormat HeightAnswerFormat = new HeightAnswerFormat(ChoiceAnswerFormatCustom.CustomAnswerStyle.Height, activityobj.getSteps().get(i).getFormat().getPlaceholder(), activityobj.getSteps().get(i).getFormat().getMeasurementSystem());
                            HeightStep.setAnswerFormat1(HeightAnswerFormat);
                            if (branching)
                                HeightStep.setStepTitle(R.string.notxt);
                            HeightStep.setTitle(activityobj.getSteps().get(i).getTitle());
                            HeightStep.setText(activityQuestionStep.get(i).getText().replaceAll("(\r\n|\n)", "<br />"));
                            HeightStep.setOptional(activityobj.getSteps().get(i).isSkippable());
                            steps.add(HeightStep);
                            break;
                        case "location":
                            QuestionStepCustom locationStep = new QuestionStepCustom(activityobj.getSteps().get(i).getKey(), context.getResources().getString(R.string.survey));
                            LocationAnswerFormat locationAnswerFormat = new LocationAnswerFormat(ChoiceAnswerFormatCustom.CustomAnswerStyle.Location, activityobj.getSteps().get(i).getFormat().isUseCurrentLocation());
                            locationStep.setAnswerFormat1(locationAnswerFormat);
                            if (branching)
                                locationStep.setStepTitle(R.string.notxt);
                            locationStep.setPlaceholder(context.getResources().getString(R.string.enter_location));
                            locationStep.setTitle(activityobj.getSteps().get(i).getTitle());
                            locationStep.setText(activityQuestionStep.get(i).getText().replaceAll("(\r\n|\n)", "<br />"));
                            locationStep.setOptional(activityobj.getSteps().get(i).isSkippable());
                            steps.add(locationStep);
                            break;
                        case "form":
                            QuestionStepCustom formstep = new QuestionStepCustom(activityobj.getSteps().get(i).getKey());
                            RealmList<Steps> activityFormStep = activityobj.getSteps();
                            ChoiceAnswerFormatCustom formsFormat = new ChoiceAnswerFormatCustom(ChoiceAnswerFormatCustom.CustomAnswerStyle.Form, formstep, getformquestion(context, activityFormStep, realm), activityobj.getSteps().get(i).isRepeatable(), activityobj.getSteps().get(i).getRepeatableText());
                            formstep.setAnswerFormat1(formsFormat);
                            if (branching)
                                formstep.setStepTitle(R.string.notxt);
                            formstep.setOptional(activityobj.getSteps().get(i).isSkippable());
                            steps.add(formstep);
                            break;
                    }
                } else if (activityQuestionStep.get(i).getType().equalsIgnoreCase("form")) {
                    QuestionStepCustom formstep = new QuestionStepCustom(activityobj.getSteps().get(i).getKey());
                    RealmList<Steps> activityFormStep = activityobj.getSteps().get(i).getSteps();
                    ArrayList<QuestionStep> questionSteps = getformquestion(context, activityFormStep, realm);
                    ChoiceAnswerFormatCustom formsFormat = new ChoiceAnswerFormatCustom(ChoiceAnswerFormatCustom.CustomAnswerStyle.Form, formstep, questionSteps, activityobj.getSteps().get(i).isRepeatable(), activityobj.getSteps().get(i).getRepeatableText());
                    formstep.setAnswerFormat1(formsFormat);
                    if (branching)
                        formstep.setStepTitle(R.string.notxt);
                    formstep.setOptional(activityobj.getSteps().get(i).isSkippable());
                    steps.add(formstep);
                } else if (activityQuestionStep.get(i).getType().equalsIgnoreCase("instruction")) {
                    InstructionStep instructionStep = new InstructionStep(activityQuestionStep.get(i).getKey(), activityQuestionStep.get(i).getTitle(), activityQuestionStep.get(i).getText().replaceAll("(\r\n|\n)", "<br />"));
                    instructionStep.setOptional(activityQuestionStep.get(i).isSkippable());
                    if (branching)
                        instructionStep.setStepTitle(R.string.notxt);
                    steps.add(instructionStep);
                } else if (activityQuestionStep.get(i).getType().equalsIgnoreCase("task")) {
                    switch (activityQuestionStep.get(i).getResultType()) {
                        case "fetalKickCounter":
                            QuestionStepCustom fetalkickIntroStep = new QuestionStepCustom(activityobj.getSteps().get(i).getKey() + "Intro");
                            TaskIntroductionAnswerFormat taskIntroductionAnswerFormat = new TaskIntroductionAnswerFormat(ChoiceAnswerFormatCustom.CustomAnswerStyle.TaskIntroStep, R.drawable.task_img1, R.string.fetalkicktitle, R.string.fetalkickdesc);
                            fetalkickIntroStep.setAnswerFormat1(taskIntroductionAnswerFormat);
                            fetalkickIntroStep.setOptional(false);
                            steps.add(fetalkickIntroStep);

                            QuestionStepCustom fetalkickInstructionStep = new QuestionStepCustom(activityobj.getSteps().get(i).getKey() + "Instruction");
                            TaskInstructionAnswerFormat taskInstructionAnswerFormat = new TaskInstructionAnswerFormat(ChoiceAnswerFormatCustom.CustomAnswerStyle.TaskinstructionStep, activityobj.getSteps().get(i).getText().replaceAll("(\r\n|\n)", "<br />"));
                            fetalkickInstructionStep.setAnswerFormat1(taskInstructionAnswerFormat);
                            fetalkickInstructionStep.setOptional(false);
                            steps.add(fetalkickInstructionStep);

                            QuestionStepCustom fetalkickStep = new QuestionStepCustom(activityobj.getSteps().get(i).getKey());
                            TappingAnswerFormat multiFormat = new TappingAnswerFormat(ChoiceAnswerFormatCustom.CustomAnswerStyle.Tapping, activityobj.getSteps().get(i).getFormat().getDuration(), activityobj.getSteps().get(i).getFormat().getKickCount());
                            fetalkickStep.setAnswerFormat1(multiFormat);
                            fetalkickStep.setOptional(activityobj.getSteps().get(i).isSkippable());
                            steps.add(fetalkickStep);
                            break;
                    }
                }
            }
        return steps;
    }

    private static ArrayList<QuestionStep> getformquestion(Context context, RealmList<Steps> formsteps, Realm realm) {
        ArrayList<QuestionStep> formquesteps = new ArrayList<>();
        for (int i = 0; i < formsteps.size(); i++) {
            if (!formsteps.get(i).getKey().contains("_addMoreEnabled"))
                switch (formsteps.get(i).getResultType()) {
                    case "scale":
                        ScaleAnswerFormat ScaleFormat = new ScaleAnswerFormat(ChoiceAnswerFormatCustom.CustomAnswerStyle.Scale, formsteps.get(i).getFormat().getStep(), formsteps.get(i).getFormat().getMinValue(), formsteps.get(i).getFormat().getMaxValue(), formsteps.get(i).getFormat().isVertical(), formsteps.get(i).getFormat().getMaxDesc(), formsteps.get(i).getFormat().getMinDesc(), formsteps.get(i).getFormat().getMaxImage(), formsteps.get(i).getFormat().getMinImage(), formsteps.get(i).getFormat().getDefaultValue());
                        QuestionStepCustom scaleStep = new QuestionStepCustom(formsteps.get(i).getKey(), context.getResources().getString(R.string.survey), ScaleFormat);
                        scaleStep.setAnswerFormat1(ScaleFormat);
                        scaleStep.setTitle(formsteps.get(i).getTitle());
                        scaleStep.setText(formsteps.get(i).getText().replaceAll("(\r\n|\n)", "<br />"));
                        scaleStep.setOptional(formsteps.get(i).isSkippable());
                        formquesteps.add(scaleStep);
                        break;
                    case "continuousScale":
                        ContinousScaleAnswerFormat ContinousScaleFormat = new ContinousScaleAnswerFormat(ChoiceAnswerFormatCustom.CustomAnswerStyle.Scale, formsteps.get(i).getFormat().getMaxFractionDigits(), formsteps.get(i).getFormat().getMinValue(), formsteps.get(i).getFormat().getMaxValue(), formsteps.get(i).getFormat().isVertical(), formsteps.get(i).getFormat().getMaxDesc(), formsteps.get(i).getFormat().getMinDesc(), formsteps.get(i).getFormat().getMaxImage(), formsteps.get(i).getFormat().getMinImage(), formsteps.get(i).getFormat().getDefaultValue());
                        QuestionStepCustom continousscaleStep = new QuestionStepCustom(formsteps.get(i).getKey(), context.getResources().getString(R.string.survey), ContinousScaleFormat);
                        continousscaleStep.setAnswerFormat1(ContinousScaleFormat);
                        continousscaleStep.setTitle(formsteps.get(i).getTitle());
                        continousscaleStep.setText(formsteps.get(i).getText().replaceAll("(\r\n|\n)", "<br />"));
                        continousscaleStep.setOptional(formsteps.get(i).isSkippable());
                        formquesteps.add(continousscaleStep);
                        break;
                    case "textScale":
                        ChoiceTextExclusive[] textScaleChoices = new ChoiceTextExclusive[formsteps.get(i).getFormat().getTextChoices().size()];
                        for (int j = 0; j < formsteps.get(i).getFormat().getTextChoices().size(); j++) {
                            textScaleChoices[j] = new ChoiceTextExclusive(formsteps.get(i).getFormat().getTextChoices().get(j).getText(), formsteps.get(i).getFormat().getTextChoices().get(j).getValue(), formsteps.get(i).getFormat().getTextChoices().get(j).getDetail(), formsteps.get(i).getFormat().getTextChoices().get(j).isExclusive(), null);
                        }

                        ScaleTextAnswerFormat ScaleTextFormat = new ScaleTextAnswerFormat(ChoiceAnswerFormatCustom.CustomAnswerStyle.ScaleText, textScaleChoices, formsteps.get(i).getFormat().getDefaultValue(), formsteps.get(i).getFormat().isVertical());

                        QuestionStepCustom scaleTextStep = new QuestionStepCustom(formsteps.get(i).getKey(), context.getResources().getString(R.string.survey), ScaleTextFormat);
                        scaleTextStep.setAnswerFormat1(ScaleTextFormat);
                        scaleTextStep.setTitle(formsteps.get(i).getTitle());
                        scaleTextStep.setText(formsteps.get(i).getText().replaceAll("(\r\n|\n)", "<br />"));
                        scaleTextStep.setOptional(formsteps.get(i).isSkippable());
                        formquesteps.add(scaleTextStep);
                        break;
                    case "valuePicker":
                        QuestionStepCustom valuepicker = new QuestionStepCustom(formsteps.get(i).getKey());
                        Choice[] valuechoice = new Choice[formsteps.get(i).getFormat().getTextChoices().size()];
                        for (int j = 0; j < formsteps.get(i).getFormat().getTextChoices().size(); j++) {
                            valuechoice[j] = new Choice(formsteps.get(i).getFormat().getTextChoices().get(j).getText(), formsteps.get(i).getFormat().getTextChoices().get(j).getValue(), formsteps.get(i).getFormat().getTextChoices().get(j).getDetail());
                        }
                        ChoiceAnswerFormatCustom pickerformat = new ChoiceAnswerFormatCustom(AnswerFormatCustom.CustomAnswerStyle.valuePicker, valuepicker, valuechoice);
                        valuepicker.setTitle(formsteps.get(i).getTitle());
                        valuepicker.setText(formsteps.get(i).getText().replaceAll("(\r\n|\n)", "<br />"));
                        valuepicker.setAnswerFormat1(pickerformat);
                        valuepicker.setOptional(formsteps.get(i).isSkippable());
                        formquesteps.add(valuepicker);
                        break;
                    case "imageChoice":
                        QuestionStepCustom multichoiceStep = new QuestionStepCustom(formsteps.get(i).getKey());
                        ChoiceCustomImage[] choicechoices = new ChoiceCustomImage[formsteps.get(i).getFormat().getImageChoices().size()];
                        for (int j = 0; j < formsteps.get(i).getFormat().getImageChoices().size(); j++) {
                            choicechoices[j] = new ChoiceCustomImage(formsteps.get(i).getFormat().getImageChoices().get(j).getText(), formsteps.get(i).getFormat().getImageChoices().get(j).getValue(), formsteps.get(i).getFormat().getImageChoices().get(j).getImage(), formsteps.get(i).getFormat().getImageChoices().get(j).getSelectedImage());
                        }
                        MultiChoiceImageAnswerFormat multichoiceFormat = new MultiChoiceImageAnswerFormat(ChoiceAnswerFormatCustom.CustomAnswerStyle.MultipleImageChoice, choicechoices);
                        multichoiceStep.setTitle(formsteps.get(i).getTitle());
                        multichoiceStep.setText(formsteps.get(i).getText().replaceAll("(\r\n|\n)", "<br />"));
                        multichoiceStep.setAnswerFormat1(multichoiceFormat);
                        multichoiceStep.setOptional(formsteps.get(i).isSkippable());
                        formquesteps.add(multichoiceStep);
                        break;
                    case "textChoice":
                        if (formsteps.get(i).getFormat().getSelectionStyle().equalsIgnoreCase("Single")) {
                            QuestionStepCustom multiStep = new QuestionStepCustom(formsteps.get(i).getKey());
                            ChoiceText[] choices = new ChoiceText[formsteps.get(i).getFormat().getTextChoices().size()];
                            for (int j = 0; j < formsteps.get(i).getFormat().getTextChoices().size(); j++) {
                                choices[j] = new ChoiceText(formsteps.get(i).getFormat().getTextChoices().get(j).getText(), formsteps.get(i).getFormat().getTextChoices().get(j).getValue(), formsteps.get(i).getFormat().getTextChoices().get(j).getDetail(),formsteps.get(i).getFormat().getTextChoices().get(j).getOther());
                            }
                            SingleChoiceTextAnswerFormat choiceAnswerFormat = new SingleChoiceTextAnswerFormat(AnswerFormatCustom.CustomAnswerStyle.SingleTextChoice, choices);
                            multiStep.setTitle(formsteps.get(i).getTitle());
                            multiStep.setText(formsteps.get(i).getText().replaceAll("(\r\n|\n)", "<br />"));
                            multiStep.setAnswerFormat1(choiceAnswerFormat);
                            multiStep.setOptional(formsteps.get(i).isSkippable());
                            formquesteps.add(multiStep);
                        } else {
                            ChoiceTextExclusive[] choices = new ChoiceTextExclusive[formsteps.get(i).getFormat().getTextChoices().size()];
                            for (int j = 0; j < formsteps.get(i).getFormat().getTextChoices().size(); j++) {
                                choices[j] = new ChoiceTextExclusive(formsteps.get(i).getFormat().getTextChoices().get(j).getText(), formsteps.get(i).getFormat().getTextChoices().get(j).getValue(), formsteps.get(i).getFormat().getTextChoices().get(j).getDetail(), formsteps.get(i).getFormat().getTextChoices().get(j).isExclusive(), formsteps.get(i).getFormat().getTextChoices().get(j).getOther());
                            }
                            MultiChoiceTextAnswerFormat choiceAnswerFormat = new MultiChoiceTextAnswerFormat(AnswerFormatCustom.CustomAnswerStyle.MultipleTextChoice, choices);
                            QuestionStepCustom multiStep = new QuestionStepCustom(formsteps.get(i).getKey());
                            multiStep.setTitle(formsteps.get(i).getTitle());
                            multiStep.setText(formsteps.get(i).getText().replaceAll("(\r\n|\n)", "<br />"));
                            multiStep.setAnswerFormat1(choiceAnswerFormat);
                            multiStep.setOptional(formsteps.get(i).isSkippable());
                            formquesteps.add(multiStep);
                        }
                        break;
                    case "boolean":
                        QuestionStep booleanStep = new QuestionStep(formsteps.get(i).getKey());
                        booleanStep.setTitle(formsteps.get(i).getTitle());
                        booleanStep.setText(formsteps.get(i).getText().replaceAll("(\r\n|\n)", "<br />"));
                        booleanStep.setAnswerFormat(new BooleanAnswerFormat(context.getResources().getString(R.string.true_text), context.getResources().getString(R.string.false_text)));
                        booleanStep.setOptional(formsteps.get(i).isSkippable());
                        formquesteps.add(booleanStep);
                        break;
                    case "numeric":
                        ChoiceAnswerFormatCustom answerFormat;
                        if (formsteps.get(i).getFormat().getStyle().equalsIgnoreCase("Integer")) {
                            if (formsteps.get(i).getFormat().getMinValue() == 0 && formsteps.get(i).getFormat().getMaxValue() == 0) {
                                realm.beginTransaction();
                                formsteps.get(i).getFormat().setMaxValue(Integer.MAX_VALUE);
                                realm.commitTransaction();
                            }
                            answerFormat = new IntegerUnitAnswerFormat(formsteps.get(i).getFormat().getMinValue(), formsteps.get(i).getFormat().getMaxValue(), formsteps.get(i).getFormat().getUnit());
                        } else {
                            if (formsteps.get(i).getFormat().getMinValue() == 0 && formsteps.get(i).getFormat().getMaxValue() == 0) {
                                answerFormat = new DecimalUnitAnswerFormat(formsteps.get(i).getFormat().getMinValue(), Float.MAX_VALUE, formsteps.get(i).getFormat().getUnit());
                            } else {
                                answerFormat = new DecimalUnitAnswerFormat(formsteps.get(i).getFormat().getMinValue(), formsteps.get(i).getFormat().getMaxValue(), formsteps.get(i).getFormat().getUnit());
                            }
                        }
                        QuestionStepCustom numericItem = new QuestionStepCustom(formsteps.get(i).getKey(), formsteps.get(i).getTitle(), answerFormat);
                        numericItem.setText(formsteps.get(i).getText().replaceAll("(\r\n|\n)", "<br />"));
                        numericItem.setPlaceholder(formsteps.get(i).getFormat().getPlaceholder());
                        numericItem.setAnswerFormat1(answerFormat);
                        numericItem.setOptional(formsteps.get(i).isSkippable());
                        formquesteps.add(numericItem);
                        break;
                    case "timeOfDay":
                        QuestionStepCustom timeOfDayStep = new QuestionStepCustom(formsteps.get(i).getKey(), context.getResources().getString(R.string.survey));
                        DateAnswerformatCustom customChoiceAnswerFormat = new DateAnswerformatCustom(AnswerFormatCustom.DateAnswerStyle.TimeOfDay);
                        timeOfDayStep.setAnswerFormat1(customChoiceAnswerFormat);
                        timeOfDayStep.setPlaceholder(context.getResources().getString(R.string.enter_time));
                        timeOfDayStep.setTitle(formsteps.get(i).getTitle());
                        timeOfDayStep.setText(formsteps.get(i).getText().replaceAll("(\r\n|\n)", "<br />"));
                        timeOfDayStep.setOptional(formsteps.get(i).isSkippable());
                        formquesteps.add(timeOfDayStep);
                        break;
                    case "date":
                        SimpleDateFormat simpleDateFormat = AppController.getDateFormat();
                        Date mindate = null, maxdate = null, defaultval = null;
                        try {
                            mindate = simpleDateFormat.parse(formsteps.get(i).getFormat().getMinDate());
                            maxdate = simpleDateFormat.parse(formsteps.get(i).getFormat().getMaxDate());
                            if (formsteps.get(i).getFormat().getDefaultValue() != null && !formsteps.get(i).getFormat().getDefaultValue().equalsIgnoreCase(""))
                                defaultval = simpleDateFormat.parse(formsteps.get(i).getFormat().getDefaultValue());
                            else
                                defaultval = Calendar.getInstance().getTime();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        DateAnswerformatCustom dateFormat;
                        if (formsteps.get(i).getFormat().getStyle().equalsIgnoreCase("Date")) {
                            if (maxdate != null) {
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTime(maxdate);
                                calendar.set(Calendar.HOUR, 23);
                                calendar.set(Calendar.MINUTE, 59);
                                calendar.set(Calendar.SECOND, 59);
                                maxdate = calendar.getTime();
                            }
                            dateFormat = new DateAnswerformatCustom(AnswerFormatCustom.DateAnswerStyle.Date, defaultval, mindate, maxdate, formsteps.get(i).getFormat().getDateRange());
                        } else {
                            dateFormat = new DateAnswerformatCustom(AnswerFormatCustom.DateAnswerStyle.DateAndTime, defaultval, mindate, maxdate, formsteps.get(i).getFormat().getDateRange());
                        }
                        QuestionStepCustom dateFormatItem = new QuestionStepCustom(formsteps.get(i).getKey(), formsteps.get(i).getTitle(), dateFormat);
                        dateFormatItem.setText(formsteps.get(i).getText().replaceAll("(\r\n|\n)", "<br />"));
                        dateFormatItem.setAnswerFormat1(dateFormat);
                        dateFormatItem.setOptional(formsteps.get(i).isSkippable());
                        formquesteps.add(dateFormatItem);
                        break;
                    case "text":
                        TextAnswerFormatRegex textAnswerFormat = new TextAnswerFormatRegex(formsteps.get(i).getFormat().getMaxLength(), formsteps.get(i).getFormat().getValidationRegex(), formsteps.get(i).getFormat().getInvalidMessage());
                        textAnswerFormat.setIsMultipleLines(formsteps.get(i).getFormat().isMultipleLines());
                        QuestionStepCustom textstep = new QuestionStepCustom(formsteps.get(i).getKey(), formsteps.get(i).getTitle(), textAnswerFormat);
                        textstep.setText(formsteps.get(i).getText().replaceAll("(\r\n|\n)", "<br />"));
                        textstep.setPlaceholder(formsteps.get(i).getFormat().getPlaceholder());
                        textstep.setAnswerFormat1(textAnswerFormat);
                        textstep.setOptional(formsteps.get(i).isSkippable());
                        formquesteps.add(textstep);
                        break;
                    case "email":
                        EmailAnswerFormatCustom emailformat = new EmailAnswerFormatCustom(255);
                        QuestionStepCustom emailstep = new QuestionStepCustom(formsteps.get(i).getKey(), formsteps.get(i).getTitle(), emailformat);
                        emailstep.setText(formsteps.get(i).getText().replaceAll("(\r\n|\n)", "<br />"));
                        emailstep.setPlaceholder(formsteps.get(i).getFormat().getPlaceholder());
                        emailstep.setOptional(formsteps.get(i).isSkippable());
                        formquesteps.add(emailstep);
                        break;
                    case "timeInterval":
                        QuestionStepCustom timeIntervalStep = new QuestionStepCustom(formsteps.get(i).getKey(), context.getResources().getString(R.string.survey));
                        TimeIntervalAnswerFormat timeIntervalAnswerFormat = new TimeIntervalAnswerFormat(ChoiceAnswerFormatCustom.CustomAnswerStyle.TimeInterval, formsteps.get(i).getFormat().getStep(), formsteps.get(i).getFormat().getDefaultValue());
                        timeIntervalStep.setAnswerFormat1(timeIntervalAnswerFormat);
                        timeIntervalStep.setTitle(formsteps.get(i).getTitle());
                        timeIntervalStep.setText(formsteps.get(i).getText().replaceAll("(\r\n|\n)", "<br />"));
                        timeIntervalStep.setOptional(formsteps.get(i).isSkippable());
                        formquesteps.add(timeIntervalStep);
                        break;
                    case "height":
                        QuestionStepCustom HeightStep = new QuestionStepCustom(formsteps.get(i).getKey(), context.getResources().getString(R.string.survey));
                        HeightAnswerFormat HeightAnswerFormat = new HeightAnswerFormat(ChoiceAnswerFormatCustom.CustomAnswerStyle.Height, formsteps.get(i).getFormat().getPlaceholder(), formsteps.get(i).getFormat().getMeasurementSystem());
                        HeightStep.setAnswerFormat1(HeightAnswerFormat);
                        HeightStep.setTitle(formsteps.get(i).getTitle());
                        HeightStep.setText(formsteps.get(i).getText().replaceAll("(\r\n|\n)", "<br />"));
                        HeightStep.setOptional(formsteps.get(i).isSkippable());
                        formquesteps.add(HeightStep);
                        break;
                    case "location":
                        QuestionStepCustom locationStep = new QuestionStepCustom(formsteps.get(i).getKey(), context.getResources().getString(R.string.survey));
                        LocationAnswerFormat locationAnswerFormat = new LocationAnswerFormat(ChoiceAnswerFormatCustom.CustomAnswerStyle.Location, formsteps.get(i).getFormat().isUseCurrentLocation());
                        locationStep.setAnswerFormat1(locationAnswerFormat);
                        locationStep.setPlaceholder(context.getResources().getString(R.string.enter_location));
                        locationStep.setTitle(formsteps.get(i).getTitle());
                        locationStep.setText(formsteps.get(i).getText().replaceAll("(\r\n|\n)", "<br />"));
                        locationStep.setOptional(formsteps.get(i).isSkippable());
                        formquesteps.add(locationStep);
                        break;
                }
        }
        return formquesteps;
    }
}
