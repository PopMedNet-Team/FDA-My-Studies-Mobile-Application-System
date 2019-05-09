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

package com.harvard.fda.utils;

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

