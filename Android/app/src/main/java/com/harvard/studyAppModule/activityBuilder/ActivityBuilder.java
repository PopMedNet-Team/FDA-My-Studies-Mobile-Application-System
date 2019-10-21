package com.harvard.studyAppModule.activityBuilder;

import android.content.Context;

import com.harvard.storageModule.DBServiceSubscriber;
import com.harvard.studyAppModule.activityBuilder.model.serviceModel.ActivityObj;
import com.harvard.studyAppModule.activityBuilder.model.serviceModel.Steps;
import com.harvard.studyAppModule.custom.Result.StepRecordCustom;
import com.harvard.utils.AppController;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.task.OrderedTask;

import java.util.List;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * Created by Rohit on 2/24/2017.
 */

public class ActivityBuilder extends OrderedTask {

    private static boolean mBranching;
    private static DBServiceSubscriber mDBServiceSubscriber;
    private static String mIdentifier;
    private static RealmList<Steps> activityQuestionStep;
    private static Context mcontext;

    private ActivityBuilder(String identifier, List<Step> steps) {
        super(identifier, steps);
    }

    public static ActivityBuilder create(Context context, String identifier, List<Step> steps, ActivityObj activityObj, boolean branching, DBServiceSubscriber dbServiceSubscriber) {
        mIdentifier = identifier;
        activityQuestionStep = activityObj.getSteps();
        mBranching = branching;
        mDBServiceSubscriber = dbServiceSubscriber;
        mcontext = context;
        return new ActivityBuilder(identifier, steps);
    }

    @Override
    public Step getStepAfterStep(Step previousStep, TaskResult taskResult) {

        if (mBranching) {
            Steps stepsData = null;
            for (int i = 0; i < activityQuestionStep.size(); i++) {
                if (previousStep == null) {
                    return steps.get(0);
                } else if (previousStep.getIdentifier().equalsIgnoreCase(activityQuestionStep.get(i).getKey())) {
                    stepsData = activityQuestionStep.get(i);
                }
            }

            if (stepsData.getResultType().equalsIgnoreCase("textScale") || stepsData.getResultType().equalsIgnoreCase("imageChoice") || stepsData.getResultType().equalsIgnoreCase("textChoice") || stepsData.getResultType().equalsIgnoreCase("boolean")) {

                if (stepsData != null && stepsData.getDestinations().size() == 1 && stepsData.getDestinations().get(0).getCondition().equalsIgnoreCase("") && stepsData.getDestinations().get(0).getDestination().equalsIgnoreCase("")) {
                    return null;
                } else if (stepsData != null && stepsData.getDestinations().size() == 1 && (stepsData.getDestinations().get(0).getCondition().equalsIgnoreCase(""))) {
                    for (int i = 0; i < steps.size(); i++) {
                        if (steps.get(i).getIdentifier().equalsIgnoreCase(stepsData.getDestinations().get(0).getDestination())) {
                            return steps.get(i);
                        }
                    }
                } else if (stepsData != null) {
                    Map<String, StepResult> map = taskResult.getResults();
                    String answer = "";
                    String destination = "";
                    for (Map.Entry<String, StepResult> pair : map.entrySet()) {
                        if (pair.getKey().equalsIgnoreCase(stepsData.getKey())) {
                            try {
                                StepResult stepResult = pair.getValue();
                                Object o = stepResult.getResults().get("answer");
                                if (o instanceof Object[]) {
                                    Object[] objects = (Object[]) o;
                                    if (objects[0] instanceof String) {
                                        answer = "" + ((String) objects[0]);
                                    } else if (objects[0] instanceof Integer) {
                                        answer = "" + ((int) objects[0]);
                                    }
                                } else {
                                    answer = "" + stepResult.getResults().get("answer");
                                }
                            } catch (Exception e) {
                                answer = "";
                                e.printStackTrace();
                            }
                            if (answer == null || answer.equalsIgnoreCase("null")) {
                                answer = "";
                            }
                            if (!answer.equalsIgnoreCase("")) {
                                if (stepsData.getResultType().equalsIgnoreCase("imageChoice") || stepsData.getResultType().equalsIgnoreCase("textChoice")) {
                                    Realm realm = AppController.getRealmobj(mcontext);
                                    StepRecordCustom stepRecordCustom = mDBServiceSubscriber.getResultFromDB(mIdentifier + "_" + pair.getKey(), realm);
                                    for (int j = 0; j < stepRecordCustom.getTextChoices().size(); j++) {
                                        if (stepRecordCustom.getTextChoices().get(j).getValue().equalsIgnoreCase(answer)) {
                                            answer = stepRecordCustom.getTextChoices().get(j).getText();
                                            break;
                                        }
                                    }
                                    mDBServiceSubscriber.closeRealmObj(realm);
                                }
                            }
                            break;
                        }
                    }
                    for (int j = 0; j < stepsData.getDestinations().size(); j++) {
                        if (stepsData.getDestinations().get(j).getCondition().equalsIgnoreCase(answer)) {
                            destination = stepsData.getDestinations().get(j).getDestination();
                        }
                    }
                    for (int k = 0; k < steps.size(); k++) {
                        if (steps.get(k).getIdentifier().equalsIgnoreCase(destination)) {
                            return steps.get(k);
                        }
                    }

                    // if destination doesn't satisfy
                    if (previousStep == null) {
                        return steps.get(0);
                    }

                    if (destination.equalsIgnoreCase("")) {
                        return null;
                    }

                    int nextIndex = steps.indexOf(previousStep) + 1;

                    if (nextIndex < steps.size()) {
                        return steps.get(nextIndex);
                    }
                }
            } else if (stepsData.getResultType().equalsIgnoreCase("scale") || stepsData.getResultType().equalsIgnoreCase("continuousScale") || stepsData.getResultType().equalsIgnoreCase("numeric") || stepsData.getResultType().equalsIgnoreCase("timeInterval") || stepsData.getResultType().equalsIgnoreCase("height")) {
                if (stepsData != null && stepsData.getDestinations().size() == 1 && stepsData.getDestinations().get(0).getCondition().equalsIgnoreCase("") && stepsData.getDestinations().get(0).getDestination().equalsIgnoreCase("")) {
                    return null;
                } else if (stepsData != null && stepsData.getDestinations().size() == 1 && (stepsData.getDestinations().get(0).getCondition().equalsIgnoreCase(""))) {
                    for (int i = 0; i < steps.size(); i++) {
                        if (steps.get(i).getIdentifier().equalsIgnoreCase(stepsData.getDestinations().get(0).getDestination())) {
                            return steps.get(i);
                        }
                    }
                } else if (stepsData != null) {
                    Map<String, StepResult> map = taskResult.getResults();
                    String answer = "";
                    String destination = "";
                    for (Map.Entry<String, StepResult> pair : map.entrySet()) {
                        if (pair.getKey().equalsIgnoreCase(stepsData.getKey())) {
                            try {
                                StepResult stepResult = pair.getValue();
                                Object o = stepResult.getResults().get("answer");
                                if (o instanceof Object[]) {
                                    Object[] objects = (Object[]) o;
                                    if (objects[0] instanceof String) {
                                        answer = "" + ((String) objects[0]);
                                    } else if (objects[0] instanceof Integer) {
                                        answer = "" + ((int) objects[0]);
                                    }
                                } else {
                                    answer = "" + stepResult.getResults().get("answer");
                                }
                            } catch (Exception e) {
                                answer = "";
                                e.printStackTrace();
                            }
                            if (answer == null || answer.equalsIgnoreCase("null")) {
                                answer = "";
                            }

                            break;
                        }
                    }
                    for (int j = 0; j < stepsData.getDestinations().size(); j++) {
                        if (answer.equalsIgnoreCase("")) {
                            if (stepsData.getDestinations().get(j).getCondition().equalsIgnoreCase(answer)) {
                                destination = stepsData.getDestinations().get(j).getDestination();
                            }
                        } else if (!stepsData.getDestinations().get(j).getCondition().equalsIgnoreCase("")) {
                            double condition = Double.parseDouble(stepsData.getDestinations().get(j).getCondition());
                            if(stepsData.getResultType().equalsIgnoreCase("timeInterval"))
                            {
                                condition = Double.parseDouble(stepsData.getDestinations().get(j).getCondition())/3600d;
                            }
                            double answerDouble = Double.parseDouble(answer);
                            if(stepsData.getDestinations().get(j).getOperator().equalsIgnoreCase("e"))
                            {
                                 if(answerDouble == condition)
                                 {
                                     destination = stepsData.getDestinations().get(j).getDestination();
                                 }
                            }
                            else if(stepsData.getDestinations().get(j).getOperator().equalsIgnoreCase("gt"))
                            {
                                if(answerDouble > condition)
                                {
                                    destination = stepsData.getDestinations().get(j).getDestination();
                                }
                            }
                            else if(stepsData.getDestinations().get(j).getOperator().equalsIgnoreCase("lt"))
                            {
                                if(answerDouble < condition)
                                {
                                    destination = stepsData.getDestinations().get(j).getDestination();
                                }
                            }
                            else if(stepsData.getDestinations().get(j).getOperator().equalsIgnoreCase("gte"))
                            {
                                if(answerDouble >= condition)
                                {
                                    destination = stepsData.getDestinations().get(j).getDestination();
                                }
                            }
                            else if(stepsData.getDestinations().get(j).getOperator().equalsIgnoreCase("lte"))
                            {
                                if(answerDouble <= condition)
                                {
                                    destination = stepsData.getDestinations().get(j).getDestination();
                                }
                            }
                            else if(stepsData.getDestinations().get(j).getOperator().equalsIgnoreCase("ne"))
                            {
                                if(answerDouble != condition)
                                {
                                    destination = stepsData.getDestinations().get(j).getDestination();
                                }
                            }
                        }
                    }
                    for (int k = 0; k < steps.size(); k++) {
                        if (steps.get(k).getIdentifier().equalsIgnoreCase(destination)) {
                            return steps.get(k);
                        }
                    }

                    // if destination doesn't satisfy
                    if (previousStep == null) {
                        return steps.get(0);
                    }

                    if (destination.equalsIgnoreCase("")) {
                        return null;
                    }

                    int nextIndex = steps.indexOf(previousStep) + 1;

                    if (nextIndex < steps.size()) {
                        return steps.get(nextIndex);
                    }
                }
            } else {

                if (previousStep == null) {
                    return steps.get(0);
                }

                if (stepsData != null && stepsData.getDestinations().size() == 1 && (stepsData.getDestinations().get(0).getCondition().equalsIgnoreCase("")) && stepsData.getDestinations().get(0).getDestination().equalsIgnoreCase("")) {
                    return null;
                } else if (stepsData != null && stepsData.getDestinations().size() == 1 && (stepsData.getDestinations().get(0).getCondition().equalsIgnoreCase(""))) {
                    for (int i = 0; i < steps.size(); i++) {
                        if (steps.get(i).getIdentifier().equalsIgnoreCase(stepsData.getDestinations().get(0).getDestination())) {
                            return steps.get(i);
                        }
                    }
                } else {

                    int nextIndex = steps.indexOf(previousStep) + 1;

                    if (nextIndex < steps.size()) {
                        return steps.get(nextIndex);
                    }
                }
            }

        } else {
            if (previousStep == null) {
                return steps.get(0);
            }

            int nextIndex = steps.indexOf(previousStep) + 1;

            if (nextIndex < steps.size()) {
                return steps.get(nextIndex);
            }
        }


        return null;
    }

    @Override
    public Step getStepBeforeStep(Step step, TaskResult taskResult) {

        if (mBranching) {
            String identifier = "";
            for (int i = 0; i < activityQuestionStep.size(); i++) {
                for (int k = 0; k < activityQuestionStep.get(i).getDestinations().size(); k++) {
                    if (activityQuestionStep.get(i).getDestinations().get(k).getDestination().equalsIgnoreCase(step.getIdentifier())) {
                        Map<String, StepResult> map = taskResult.getResults();
                        for (Map.Entry<String, StepResult> pair : map.entrySet()) {
                            if (pair.getKey().equalsIgnoreCase(activityQuestionStep.get(i).getKey())) {
                                if (activityQuestionStep.get(i).getResultType().equalsIgnoreCase("textScale") || activityQuestionStep.get(i).getResultType().equalsIgnoreCase("imageChoice") || activityQuestionStep.get(i).getResultType().equalsIgnoreCase("textChoice") || activityQuestionStep.get(i).getResultType().equalsIgnoreCase("boolean")) {
                                    try {
                                        if (pair.getValue() != null) {
                                            String answer = null;
                                            try {
                                                StepResult stepResult = pair.getValue();

                                                Object o = stepResult.getResults().get("answer");
                                                if (o instanceof Object[]) {
                                                    Object[] objects = (Object[]) o;
                                                    if (objects[0] instanceof String) {
                                                        answer = "" + ((String) objects[0]);
                                                    } else if (objects[0] instanceof Integer) {
                                                        answer = "" + ((int) objects[0]);
                                                    }
                                                } else {
                                                    answer = "" + stepResult.getResults().get("answer");
                                                }


                                            } catch (Exception e) {
                                                answer = "";
                                                e.printStackTrace();
                                            }
                                            if (answer == null || answer.equalsIgnoreCase("null")) {
                                                answer = "";
                                            }
                                            if (!answer.equalsIgnoreCase("")) {
                                                if (activityQuestionStep.get(i).getResultType().equalsIgnoreCase("imageChoice") || activityQuestionStep.get(i).getResultType().equalsIgnoreCase("textChoice")) {
                                                    Realm realm = AppController.getRealmobj(mcontext);
                                                    StepRecordCustom stepRecordCustom = mDBServiceSubscriber.getResultFromDB(mIdentifier + "_" + activityQuestionStep.get(i).getKey(), realm);
                                                    for (int j = 0; j < stepRecordCustom.getTextChoices().size(); j++) {
                                                        if (stepRecordCustom.getTextChoices().get(j).getValue().equalsIgnoreCase(answer)) {
                                                            answer = stepRecordCustom.getTextChoices().get(j).getText();
                                                            break;
                                                        }
                                                    }
                                                    mDBServiceSubscriber.closeRealmObj(realm);
                                                }
                                            }
                                            if (activityQuestionStep.get(i).getDestinations().get(k).getCondition().equalsIgnoreCase(answer)) {
                                                identifier = activityQuestionStep.get(i).getKey();
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        int nextIndex = steps.indexOf(step) - 1;

                                        if (nextIndex >= 0) {
                                            return steps.get(nextIndex);
                                        }
                                    }
                                }
                                else if (activityQuestionStep.get(i).getResultType().equalsIgnoreCase("scale") || activityQuestionStep.get(i).getResultType().equalsIgnoreCase("continuousScale") || activityQuestionStep.get(i).getResultType().equalsIgnoreCase("numeric") || activityQuestionStep.get(i).getResultType().equalsIgnoreCase("timeInterval") || activityQuestionStep.get(i).getResultType().equalsIgnoreCase("height")) {
                                    try {
                                        if (pair.getValue() != null) {
                                            String answer = null;
                                            try {
                                                StepResult stepResult = pair.getValue();

                                                Object o = stepResult.getResults().get("answer");
                                                if (o instanceof Object[]) {
                                                    Object[] objects = (Object[]) o;
                                                    if (objects[0] instanceof String) {
                                                        answer = "" + ((String) objects[0]);
                                                    } else if (objects[0] instanceof Integer) {
                                                        answer = "" + ((int) objects[0]);
                                                    }
                                                } else {
                                                    answer = "" + stepResult.getResults().get("answer");
                                                }


                                            } catch (Exception e) {
                                                answer = "";
                                                e.printStackTrace();
                                            }
                                            if (answer == null || answer.equalsIgnoreCase("null")) {
                                                answer = "";
                                            }
                                            if(answer.equalsIgnoreCase("")) {
                                                if (activityQuestionStep.get(i).getDestinations().get(k).getCondition().equalsIgnoreCase(answer)) {
                                                    identifier = activityQuestionStep.get(i).getKey();
                                                }
                                            }
                                            else if (!activityQuestionStep.get(i).getDestinations().get(k).getCondition().equalsIgnoreCase("")) {
                                                double condition = Double.parseDouble(activityQuestionStep.get(i).getDestinations().get(k).getCondition());
                                                double answerDouble = Double.parseDouble(answer);
                                                if(activityQuestionStep.get(i).getDestinations().get(k).getOperator().equalsIgnoreCase("e"))
                                                {
                                                    if(answerDouble == condition)
                                                    {
                                                        identifier = activityQuestionStep.get(i).getKey();
                                                    }
                                                }
                                                else if(activityQuestionStep.get(i).getDestinations().get(k).getOperator().equalsIgnoreCase("gt"))
                                                {
                                                    if(answerDouble > condition)
                                                    {
                                                        identifier = activityQuestionStep.get(i).getKey();
                                                    }
                                                }
                                                else if(activityQuestionStep.get(i).getDestinations().get(k).getOperator().equalsIgnoreCase("lt"))
                                                {
                                                    if(answerDouble < condition)
                                                    {
                                                        identifier = activityQuestionStep.get(i).getKey();
                                                    }
                                                }
                                                else if(activityQuestionStep.get(i).getDestinations().get(k).getOperator().equalsIgnoreCase("gte"))
                                                {
                                                    if(answerDouble >= condition)
                                                    {
                                                        identifier = activityQuestionStep.get(i).getKey();
                                                    }
                                                }
                                                else if(activityQuestionStep.get(i).getDestinations().get(k).getOperator().equalsIgnoreCase("lte"))
                                                {
                                                    if(answerDouble <= condition)
                                                    {
                                                        identifier = activityQuestionStep.get(i).getKey();
                                                    }
                                                }
                                                else if(activityQuestionStep.get(i).getDestinations().get(k).getOperator().equalsIgnoreCase("ne"))
                                                {
                                                    if(answerDouble != condition)
                                                    {
                                                        identifier = activityQuestionStep.get(i).getKey();
                                                    }
                                                }
                                            }
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                        int nextIndex = steps.indexOf(step) - 1;

                                        if (nextIndex >= 0) {
                                            return steps.get(nextIndex);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            for (int j = 0; j < steps.size(); j++) {
                if (steps.get(j).getIdentifier().equalsIgnoreCase(identifier)) {
                    return steps.get(j);
                }
            }
        } else {
            int nextIndex = steps.indexOf(step) - 1;

            if (nextIndex >= 0) {
                return steps.get(nextIndex);
            }

            return null;
        }


        int nextIndex = steps.indexOf(step) - 1;

        if (nextIndex >= 0) {
            return steps.get(nextIndex);
        }

        return null;
    }

}
