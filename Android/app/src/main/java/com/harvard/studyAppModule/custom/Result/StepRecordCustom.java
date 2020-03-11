package com.harvard.studyAppModule.custom.Result;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.LinkedTreeMap;
import com.harvard.studyAppModule.activityBuilder.model.Choices;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.utils.FormatHelper;
import org.researchstack.backbone.utils.TextUtils;

import java.util.Date;
import java.util.Map;
import java.util.Set;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Naveen Raj on 02/15/2017.
 */
public class StepRecordCustom extends RealmObject {

    private static final Gson GSON = new GsonBuilder().setDateFormat(FormatHelper.DATE_FORMAT_ISO_8601).create();
    public int id;
    public int taskRecordId;
    public String taskId;
    public String stepId;
    public Date started;
    public Date completed;
    public Date runStartDate;
    public Date runEndDate;
    public String result;
    public String activityID;
    public String studyId;
    @PrimaryKey
    public String taskStepID;
    public String resultType;
    private RealmList<Choices> textChoices;

    public String getStudyId() {
        return studyId;
    }

    public void setStudyId(String studyId) {
        this.studyId = studyId;
    }

    public Date getRunStartDate() {
        return runStartDate;
    }

    public void setRunStartDate(Date runStartDate) {
        this.runStartDate = runStartDate;
    }

    public Date getRunEndDate() {
        return runEndDate;
    }

    public void setRunEndDate(Date runEndDate) {
        this.runEndDate = runEndDate;
    }

    public String getActivityID() {
        return activityID;
    }

    public void setActivityID(String activityID) {
        this.activityID = activityID;
    }

    public RealmList<Choices> getTextChoices() {
        return textChoices;
    }

    public void setTextChoices(RealmList<Choices> textChoices) {
        this.textChoices = textChoices;
    }

    public String getResultType() {
        return resultType;
    }

    public void setResultType(String resultType) {
        this.resultType = resultType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTaskRecordId() {
        return taskRecordId;
    }

    public String getTaskStepID() {
        return taskStepID;
    }

    public void setTaskStepID(String taskStepID) {
        this.taskStepID = taskStepID;
    }

    public void setTaskRecordId(int taskRecordId) {
        this.taskRecordId = taskRecordId;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getStepId() {
        return stepId;
    }

    public void setStepId(String stepId) {
        this.stepId = stepId;
    }

    public Date getStarted() {
        return started;
    }

    public void setStarted(Date started) {
        this.started = started;
    }

    public Date getCompleted() {
        return completed;
    }

    public void setCompleted(Date completed) {
        this.completed = completed;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    static StepResult toStepResult(StepRecordCustom record) {
        StepResult result = new StepResult(new Step(record.getStepId()));
        result.setStartDate(record.getStarted());
        result.setEndDate(record.getCompleted());
        if (!TextUtils.isEmpty(record.getResult())) {
            JsonParser jsonParser = new JsonParser();
            Map jsonElement = (Map) parseData(jsonParser.parse(record.getResult()), result);
            result.setResults(jsonElement);
        }
        return result;
    }

    public static Object parseData(JsonElement jsonElement, StepResult result) {
        Map<String, Object> map = new LinkedTreeMap<String, Object>();
        JsonObject obj = jsonElement.getAsJsonObject();
        Set<Map.Entry<String, JsonElement>> entitySet = obj.entrySet();
        for (Map.Entry<String, JsonElement> entry : entitySet) {
            if (entry.getValue().isJsonArray()) {
                JsonArray arr = entry.getValue().getAsJsonArray();
                Object list[] = new Object[arr.size()];
                int i = 0;
                for (JsonElement anArr : arr) {
                    list[i] = findPrimitiveData(anArr);
                    i++;
                }
                map.put(entry.getKey(), list);
            } else if (entry.getValue().isJsonPrimitive()) {
                map.put(entry.getKey(), findPrimitiveData(entry.getValue()));
            } else {
                Map<String, Object> mapStep = new LinkedTreeMap<String, Object>();
                StepResult stepResult = new StepResult(new Step(entry.getKey()));
                JsonObject objStep = entry.getValue().getAsJsonObject();
                Set<Map.Entry<String, JsonElement>> entitySetStep = objStep.entrySet();
                for (Map.Entry<String, JsonElement> entryStep : entitySetStep) {
                    if (entryStep.getValue().isJsonObject()) {

                        JsonObject objStepResult = entryStep.getValue().getAsJsonObject();
                        Set<Map.Entry<String, JsonElement>> entitySetStepResult = objStepResult.entrySet();
                        for (Map.Entry<String, JsonElement> entryStepVal : entitySetStepResult) {
                            if (entryStepVal.getValue().isJsonArray()) {
                                JsonArray arr = entryStepVal.getValue().getAsJsonArray();
                                Object list[] = new Object[arr.size()];
                                int i = 0;
                                for (JsonElement anArr : arr) {
                                    if (anArr.isJsonObject()) {
                                        list[i] = findPrimitiveData(anArr.getAsJsonObject().get("value"));
                                    } else {
                                        list[i] = findPrimitiveData(anArr);
                                    }
                                    i++;
                                }
                                mapStep.put(entryStepVal.getKey(), list);
                            } else {
                                mapStep.put(entryStepVal.getKey(), findPrimitiveData(entryStepVal.getValue()));
                            }
                        }

                    }
                }
                stepResult.setResults(mapStep);
                map.put(entry.getKey(), stepResult);
            }

        }
        return map;
    }

    private static Object findPrimitiveData(JsonElement jsonElement) {
        if (jsonElement.isJsonPrimitive()) {
            JsonPrimitive prim = jsonElement.getAsJsonPrimitive();
            if (prim.isJsonPrimitive())
                if (prim.isBoolean()) {
                    return prim.getAsBoolean();
                } else if (prim.isString()) {
                    return prim.getAsString();
                } else if (prim.isNumber()) {
                    String num = prim.getAsNumber().toString();

                    if (num.contains(".")) {
                        return Double.parseDouble(num);
                    } else {
                        try {
                            return Integer.parseInt(num);
                        } catch (Exception e) {
                            return Long.parseLong(num);
                        }
                    }

                }
        }
        return jsonElement;
    }
}
