package com.harvard.studyAppModule;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.LinkedTreeMap;
import com.harvard.R;
import com.harvard.storageModule.DBServiceSubscriber;
import com.harvard.studyAppModule.activityBuilder.model.serviceModel.ActivityObj;
import com.harvard.studyAppModule.consent.model.EligibilityConsent;
import com.harvard.studyAppModule.custom.Result.StepRecordCustom;
import com.harvard.utils.AppController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmResults;

public class StudylauncherActivity extends AppCompatActivity {

    Button result;
    Realm realm;
    DBServiceSubscriber mDBServiceSubscriber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_studylauncher);
        realm = AppController.getRealmobj(this);
        mDBServiceSubscriber = new DBServiceSubscriber();

        result = (Button) findViewById(R.id.result);
        result.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generateresult();
            }
        });
    }

    @Override
    protected void onDestroy() {
        mDBServiceSubscriber.closeRealmObj(realm);
        super.onDestroy();
    }

    private void generateresult() {
        ActivityObj activityObj = null;

        activityObj = realm.where(ActivityObj.class).equalTo("surveyId", "initial").findFirst();

        JSONObject responsejson = new JSONObject();
        try {

            JSONObject metaobj = new JSONObject();
            metaobj.put("studyId", activityObj.getMetadata().getStudyId());
            metaobj.put("activityId", activityObj.getMetadata().getActivityId());
            metaobj.put("version", activityObj.getMetadata().getVersion());
            responsejson.put("metadata", metaobj);

            responsejson.put("participantId", "846654c5bd41357cb59222f793d97ff4");

            JSONObject dataobj = new JSONObject();
            dataobj.put("startTime", activityObj.getMetadata().getStartDate());
            dataobj.put("endTime", activityObj.getMetadata().getEndDate());

            JSONArray resultarray = new JSONArray();
            JsonParser jsonParser = new JsonParser();
            RealmResults<StepRecordCustom> StepRecord = realm.where(StepRecordCustom.class).equalTo("taskId", "initial").findAll();
            for (int i = 0; i < activityObj.getSteps().size(); i++) {
                for (int j = 0; j < StepRecord.size(); j++) {
                    if (StepRecord.get(j).getStepId().equalsIgnoreCase(activityObj.getSteps().get(i).getKey())) {
                        JSONObject resultarrobj = new JSONObject();

                        resultarrobj.put("resultType", activityObj.getSteps().get(i).getResultType());
                        resultarrobj.put("key", activityObj.getSteps().get(i).getKey());

                        resultarrobj.put("startTime", StepRecord.get(j).getStarted());
                        resultarrobj.put("endTime", StepRecord.get(j).getCompleted());
                        if (!activityObj.getSteps().get(i).getResultType().equalsIgnoreCase("grouped")) {
                            if (StepRecord.get(j).getResult().equalsIgnoreCase("{}")) {
                                resultarrobj.put("skipped", true);
                                resultarrobj.put("value", "");
                            } else {
                                resultarrobj.put("skipped", false);
                                resultarrobj.put("value", findPrimitiveData(jsonParser.parse(StepRecord.get(j).getResult()), activityObj, i));
                            }
                        } else {
                            Map<String, Object> map = (Map<String, Object>) parseData(jsonParser.parse(StepRecord.get(j).getResult()));
                            JSONArray jsonArrayMain = new JSONArray();

                            int k = -1;
                            boolean update;
                            boolean createResult = true;
                            while (createResult) {
                                JSONArray jsonArray = new JSONArray();
                                update = false;
                                for (Map.Entry<String, Object> entry : map.entrySet()) {
                                    Map<String, Object> mapEntry = (Map<String, Object>) entry.getValue();
                                    JSONObject jsonObject = new JSONObject();
                                    String identifier = (String) mapEntry.get("identifier");
                                    if (k == -1) {
                                        if (!identifier.contains("_addMoreEnabled")) {
                                            for (int c = 0; c < activityObj.getSteps().size(); c++) {
                                                if (activityObj.getSteps().get(c).getSteps().size() > 0) {
                                                    for (int c1 = 0; c1 < activityObj.getSteps().get(c).getSteps().size(); c1++) {
                                                        if (activityObj.getSteps().get(c).getSteps().get(c1).getKey().equalsIgnoreCase((String) mapEntry.get("identifier"))) {
                                                            jsonObject.put("resultType", activityObj.getSteps().get(c).getSteps().get(c1).getResultType());
                                                        }
                                                    }
                                                }
                                            }
                                            jsonObject.put("key", mapEntry.get("identifier"));
                                            jsonObject.put("startTime", mapEntry.get("startDate"));
                                            jsonObject.put("endTime", mapEntry.get("endDate"));
                                            jsonObject.put("skipped", false);
                                            Map<String, Object> mapEntryResult = (Map<String, Object>) mapEntry.get("results");
                                            Object o = mapEntryResult.get("answer");
                                            if (o instanceof Object[]) {
                                                Object[] objects = (Object[]) o;
                                                if (objects[0] instanceof Integer) {
                                                    JSONArray jsonArray1 = new JSONArray();
                                                    for (int l = 0; l < objects.length; l++) {
                                                        for (int c = 0; c < activityObj.getSteps().size(); c++) {
                                                            if (activityObj.getSteps().get(c).getSteps().size() > 0) {
                                                                for (int c1 = 0; c1 < activityObj.getSteps().get(c).getSteps().size(); c1++) {
                                                                    if (activityObj.getSteps().get(c).getSteps().get(c1).getKey().equalsIgnoreCase((String) mapEntry.get("identifier"))) {
                                                                        jsonArray1.put(activityObj.getSteps().get(c).getSteps().get(c1).getFormat().getTextChoices().get((int) objects[l]).getValue());
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                    jsonObject.put("value", jsonArray1);
                                                } else if (objects[0] instanceof String) {
                                                    JSONArray jsonArray1 = new JSONArray();
                                                    for (int l = 0; l < objects.length; l++) {
                                                        jsonArray1.put((String) objects[l]);
                                                    }
                                                    jsonObject.put("value", jsonArray1);
                                                }
                                            } else {
                                                jsonObject.put("value", mapEntryResult.get("answer"));
                                            }
                                            jsonArray.put(jsonObject);
                                            update = true;
                                        }
                                    } else if (identifier.contains(k + "_addMoreEnabled")) {
                                        for (int c = 0; c < activityObj.getSteps().size(); c++) {
                                            if (activityObj.getSteps().get(c).getSteps().size() > 0) {
                                                for (int c1 = 0; c1 < activityObj.getSteps().get(c).getSteps().size(); c1++) {
                                                    if (activityObj.getSteps().get(c).getSteps().get(c1).getKey().equalsIgnoreCase((String) mapEntry.get("identifier"))) {
                                                        jsonObject.put("resultType", activityObj.getSteps().get(c).getSteps().get(c1).getResultType());
                                                    }
                                                }
                                            }
                                        }
                                        jsonObject.put("key", mapEntry.get("identifier"));
                                        jsonObject.put("startTime", mapEntry.get("startDate"));
                                        jsonObject.put("endTime", mapEntry.get("endDate"));
                                        jsonObject.put("skipped", false);
                                        Map<String, Object> mapEntryResult = (Map<String, Object>) mapEntry.get("results");
                                        Object o = mapEntryResult.get("answer");
                                        if (o instanceof Object[]) {
                                            Object[] objects = (Object[]) o;
                                            if (objects[0] instanceof Integer) {
                                                JSONArray jsonArray1 = new JSONArray();
                                                for (int l = 0; l < objects.length; l++) {
                                                    for (int c = 0; c < activityObj.getSteps().size(); c++) {
                                                        if (activityObj.getSteps().get(c).getSteps().size() > 0) {
                                                            for (int c1 = 0; c1 < activityObj.getSteps().get(c).getSteps().size(); c1++) {
                                                                if (activityObj.getSteps().get(c).getSteps().get(c1).getKey().equalsIgnoreCase((String) mapEntry.get("identifier"))) {
                                                                    jsonArray1.put(activityObj.getSteps().get(c).getSteps().get(c1).getFormat().getTextChoices().get((int) objects[l]).getValue());
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                                jsonObject.put("value", jsonArray1);
                                            } else if (objects[0] instanceof String) {
                                                JSONArray jsonArray1 = new JSONArray();
                                                for (int l = 0; l < objects.length; l++) {
                                                    jsonArray1.put((String) objects[l]);
                                                }
                                                jsonObject.put("value", jsonArray1);
                                            }
                                        } else {
                                            jsonObject.put("value", mapEntryResult.get("answer"));
                                        }
                                        jsonArray.put(jsonObject);
                                        update = true;
                                    }
                                }
                                k++;
                                if (update) {
                                    jsonArrayMain.put(jsonArray);
                                } else {
                                    createResult = false;
                                }
                            }
                            resultarrobj.put("value", jsonArrayMain);
                        }
                        resultarray.put(resultarrobj);
                    }
                }
            }
            dataobj.put("results", resultarray);

            responsejson.put("data", dataobj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }



    private static Object findPrimitiveData(JsonElement jsonElement, ActivityObj activityObj, int position) {
        JsonObject obj = jsonElement.getAsJsonObject();
        Set<Map.Entry<String, JsonElement>> entitySet = obj.entrySet();
        JsonPrimitive prim = null;
        for (Map.Entry<String, JsonElement> entry : entitySet) {
            if (entry.getValue().isJsonArray()) {
                JsonArray arr = entry.getValue().getAsJsonArray();
                JSONArray jsonArray = new JSONArray();
                for (JsonElement anArr : arr) {
                    jsonArray.put(activityObj.getSteps().get(position).getFormat().getTextChoices().get(anArr.getAsInt()).getValue());
                }
                return jsonArray;
            } else {
                prim = entry.getValue().getAsJsonPrimitive();
            }
        }
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
        return null;
    }

    public static Object parseData(JsonElement jsonElement) {
        Map<String, Object> map = new LinkedTreeMap<String, Object>();
        if (jsonElement.isJsonArray()) {
            JsonArray arr = jsonElement.getAsJsonArray();
            Object list[] = new Object[arr.size()];
            int i = 0;
            for (JsonElement anArr : arr) {
                list[i] = parseData(anArr);
                i++;
            }
            return list;
        } else if (jsonElement.isJsonPrimitive()) {
            return findPrimitiveData(jsonElement);
        } else {
            JsonObject objStep = jsonElement.getAsJsonObject();
            Set<Map.Entry<String, JsonElement>> entitySetStep = objStep.entrySet();
            for (Map.Entry<String, JsonElement> entryStep : entitySetStep) {
                map.put(entryStep.getKey(), parseData(entryStep.getValue()));
            }
        }

        return map;

    }

    private static Object findPrimitiveData(JsonElement jsonElement) {
        JsonPrimitive prim = jsonElement.getAsJsonPrimitive();
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
        return null;
    }
}
