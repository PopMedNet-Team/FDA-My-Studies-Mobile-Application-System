package com.harvard.utils;

import android.util.Log;

import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Timson on 3/23/2015.
 */
public class JsonFormatHelper {

    /**
     * Create new JSON from input Hashmap
     *
     * @param value
     * @return newly created JSON
     */
    public String toJson(HashMap<String, String> value) {
        Gson gson = new Gson();
        return gson.toJson(value);
    }

    /**
     * Convert JSON Object to String format
     *
     * @param jsonObject
     * @return String Representation of jsonObject
     */
    public String toString(JSONObject jsonObject) {
        return jsonObject.toString();
    }

    /**
     * Parse JSON OverviewMainData String using Gson
     *
     * @param response
     * @param type
     * @return
     */
    public <T> T parseJsonData(String response, Class<T> type) {
        Gson gson = new Gson();
        try {
            T responseObj = (T) gson.fromJson(response, type);
            return responseObj;
        } catch (Exception e) {
            return null;
        }
    }
}

