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

package com.harvard.fda.webserviceModule.apiHelper;

import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.harvard.fda.R;
import com.harvard.fda.studyAppModule.activityBuilder.model.serviceModel.ActivityInfoData;
import com.harvard.fda.userModule.webserviceModel.RefreshToken;
import com.harvard.fda.utils.AppController;
import com.harvard.fda.utils.URLs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.util.HashMap;


public class ApiCall<T, V> extends AsyncTask<T, String, String> {

    private String mUrlPassed;
    private HashMap<String, String> mHashmapData;
    private JSONObject mJsonData;
    private JSONArray mJsonArray;
    private String mWebserviceType;
    private Class<T> mGenericClass;
    private Context mContext;
    private HashMap<String, String> mHeadersData;
    private HashMap<String, String> mFormData;
    private HashMap<String, File> mFilesData;
    private T t;
    private OnAsyncRequestComplete mOnAsyncRequestComplete;
    private int mResultCode;
    private Responsemodel mResponseModel;
    private boolean mShowAlert;
    private String serverType;

    public ApiCall(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * To make a POST Hashmap request
     *
     * @param url          url path
     * @param genericClass model class to parse
     * @param headers      null if no header to pass
     * @param hashMap      params
     * @param resultCode   call back code
     * @param v            Activity context
     * @param ShowAlert    wherever to show alert
     */
    public void apiCallPostHashmap(String url, HashMap<String, String> headers, Class<T> genericClass, HashMap<String, String> hashMap, int resultCode, V v, boolean ShowAlert, String serverType) {
        this.mUrlPassed = url;
        this.mGenericClass = genericClass;
        this.mHeadersData = headers;
        this.mHashmapData = hashMap;
        this.mWebserviceType = "post_hashmap";
        this.mResultCode = resultCode;
        this.mOnAsyncRequestComplete = (OnAsyncRequestComplete) v;
        this.serverType = serverType;
        this.mShowAlert = ShowAlert;
        execute();
    }

    /**
     * To make a POST Json request
     *
     * @param url          url path
     * @param genericClass model class to parse
     * @param headers      null if no header to pass
     * @param jsonData     json object params
     * @param resultCode   call back code
     * @param v            activity context
     * @param ShowAlert    wherever to show alert
     */
    public void apiCallPostJson(String url, HashMap<String, String> headers, Class<T> genericClass, JSONObject jsonData, int resultCode, V v, boolean ShowAlert, String serverType) {
        this.mUrlPassed = url;
        this.mGenericClass = genericClass;
        this.mHeadersData = headers;
        this.mJsonData = jsonData;
        this.mWebserviceType = "post_json";
        this.mResultCode = resultCode;
        this.mOnAsyncRequestComplete = (OnAsyncRequestComplete) v;
        this.mShowAlert = ShowAlert;
        this.serverType = serverType;
        execute();
    }

    /**
     * To make a DELETE Json request
     *
     * @param url          url path
     * @param genericClass model class to parse
     * @param headers      null if no header to pass
     * @param jsonData     json object params
     * @param resultCode   call back code
     * @param v            activity context
     * @param ShowAlert    wherever to show alert
     */
    public void apiCallDeleteJson(String url, HashMap<String, String> headers, Class<T> genericClass, JSONObject jsonData, int resultCode, V v, boolean ShowAlert, String serverType) {
        this.mUrlPassed = url;
        this.mGenericClass = genericClass;
        this.mHeadersData = headers;
        this.mJsonData = jsonData;
        this.mWebserviceType = "delete_json";
        this.mResultCode = resultCode;
        this.mOnAsyncRequestComplete = (OnAsyncRequestComplete) v;
        this.mShowAlert = ShowAlert;
        this.serverType = serverType;
        execute();
    }

    public void apiCallDeleteJsonArray(String url, HashMap<String, String> headers, Class<T> genericClass, JSONArray jsonArray, int resultCode, V v, boolean ShowAlert, String serverType) {
        this.mUrlPassed = url;
        this.mGenericClass = genericClass;
        this.mHeadersData = headers;
        this.mJsonArray = jsonArray;
        this.mWebserviceType = "delete_jsonArray";
        this.mResultCode = resultCode;
        this.mOnAsyncRequestComplete = (OnAsyncRequestComplete) v;
        this.mShowAlert = ShowAlert;
        this.serverType = serverType;
        execute();
    }


    /**
     * To make a POST Multi-part request
     *
     * @param url          url path
     * @param genericClass model class to parse
     * @param headers      null if no header to pass
     * @param mFormData    null if no form data
     * @param files        null if no files to upload
     * @param resultCode   call back code
     * @param v            activity context
     * @param ShowAlert    wherever to show alert
     */
    public void apiCallMultipart(String url, Class<T> genericClass, HashMap<String, String> headers, HashMap<String, String> mFormData, HashMap<String, File> files, int resultCode, V v, boolean ShowAlert, String serverType) {
        this.mUrlPassed = url;
        this.mGenericClass = genericClass;
        this.mHeadersData = headers;
        this.mFormData = mFormData;
        this.mFilesData = files;
        this.mWebserviceType = "post_multi";
        this.mResultCode = resultCode;
        this.mOnAsyncRequestComplete = (OnAsyncRequestComplete) v;
        this.serverType = serverType;
        this.mShowAlert = ShowAlert;
        execute();
    }

    /**
     * To make a GET request
     *
     * @param url          url path
     * @param headers      null if no header to pass
     * @param genericClass model class to parse
     * @param resultCode   call back code
     * @param v            activity context
     * @param ShowAlert    wherever to show alert
     */
    public void apiCallGet(String url, HashMap<String, String> headers, Class<T> genericClass, int resultCode, V v, boolean ShowAlert, String serverType) {
        this.mUrlPassed = url;
        this.mHeadersData = headers;
        this.mGenericClass = genericClass;
        this.mWebserviceType = "get";
        this.mResultCode = resultCode;
        this.mOnAsyncRequestComplete = (OnAsyncRequestComplete) v;
        this.mShowAlert = ShowAlert;
        this.serverType = serverType;
        execute();
    }


    /**
     * To make a DELETE Hashmap request
     *
     * @param url          url path
     * @param genericClass model class to parse
     * @param headers      null if no header to pass
     * @param hashMap      params
     * @param resultCode   call back code
     * @param v            Activity context
     * @param ShowAlert    wherever to show alert
     */
    public void apiCallDeleteHashmap(String url, HashMap<String, String> headers, Class<T> genericClass, HashMap<String, String> hashMap, int resultCode, V v, boolean ShowAlert, String serverType) {
        this.mUrlPassed = url;
        this.mGenericClass = genericClass;
        this.mHeadersData = headers;
        this.mHashmapData = hashMap;
        this.mWebserviceType = "delete_hashmap";
        this.mResultCode = resultCode;
        this.mOnAsyncRequestComplete = (ApiCall.OnAsyncRequestComplete) v;
        this.mShowAlert = ShowAlert;
        this.serverType = serverType;
        execute();
    }

    // Interface to be implemented by calling activity
    public interface OnAsyncRequestComplete {
        /**
         * @param response     --> web service response
         * @param responseCode --> call back code
         * @param <T>          --> Generic class
         */
        <T> void asyncResponse(T response, int responseCode);

        /**
         * @param responseCode --> response code call back
         * @param errormsg     --> error msg
         */
        void asyncResponseFailure(int responseCode, String errormsg, String statusCode);
    }


    @SafeVarargs
    @Override
    protected final String doInBackground(T... params) {

        String responseCode;
        ConnectionDetector connectionDetector = new ConnectionDetector(mContext);
        String response;
        if (connectionDetector.isConnectingToInternet()) {
            switch (mWebserviceType) {
                case "get":
                    mResponseModel = HttpRequest.getRequest(mUrlPassed, mHeadersData, serverType);
                    break;
                case "post_hashmap":
                    mResponseModel = HttpRequest.postRequestsWithHashmap(mUrlPassed, mHashmapData, mHeadersData, serverType);
                    break;
                case "post_json":
                    mResponseModel = HttpRequest.makePostRequestWithJson(mUrlPassed, mJsonData, mHeadersData, serverType);
                    break;
                case "post_multi":
                    mResponseModel = HttpRequest.postRequestMultipart(mUrlPassed, mHeadersData, mFormData, mFilesData, serverType);
                    break;
                case "delete_hashmap":
                    mResponseModel = HttpRequest.deleteRequestsWithHashmap(mUrlPassed, mHashmapData, mHeadersData, serverType);
                    break;
                case "delete_json":
                    mResponseModel = HttpRequest.makeDeleteRequestWithJson(mUrlPassed, mJsonData, mHeadersData, serverType);
                    break;
                case "delete_jsonArray":
                    mResponseModel = HttpRequest.makeDeleteRequestWithJsonArray(mUrlPassed, mJsonArray, mHeadersData, serverType);
                    break;
            }
            String url = "";
            boolean parse = true;
            if (url.equalsIgnoreCase(mUrlPassed)) {
                responseCode = "200";
                response = "";
                try {
                    JSONObject jsonObject = new JSONObject(loadJSONFromAsset());
                    response = jsonObject.toString();
                    mResponseModel = new Responsemodel();
                    mResponseModel.setResponse(response);
                    mResponseModel.setResponseCode(responseCode);
                    mResponseModel.setResponseData(response);
                    mResponseModel.setServermsg("success");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                responseCode = mResponseModel.getResponseCode();
                response = mResponseModel.getResponse();
            }
            if (responseCode.equalsIgnoreCase("0") && response.equalsIgnoreCase("timeout")) {
                response = "timeout";
            } else if (responseCode.equalsIgnoreCase("0") && response.equalsIgnoreCase("")) {
                response = "error";
            } else if (Integer.parseInt(responseCode) >= 201 && Integer.parseInt(responseCode) < 300 && response.equalsIgnoreCase("")) {
                response = "No data";
            } else if (Integer.parseInt(responseCode) >= 400 && Integer.parseInt(responseCode) < 500 && response.equalsIgnoreCase("http_not_ok")) {
                response = "client error";
            } else if (Integer.parseInt(responseCode) >= 500 && Integer.parseInt(responseCode) < 600 && response.equalsIgnoreCase("http_not_ok")) {
                response = "server error";
            } else if (response.equalsIgnoreCase("http_not_ok")) {
                response = "Unknown error";
            } else if (Integer.parseInt(responseCode) == HttpURLConnection.HTTP_UNAUTHORIZED) {
                response = "session expired";

                if (!this.mUrlPassed.contains("login.api")) {
                    String refreshTokenUrl = URLs.REFRESH_TOKEN;
                    JSONObject refreshTokenJsonData = new JSONObject();
                    try {
                        refreshTokenJsonData.put("refreshToken", AppController.getHelperSharedPreference().readPreference(mContext, mContext.getString(R.string.refreshToken), ""));
//                        refreshTokenJsonData.put("refreshToken", null);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    HashMap<String, String> refreshTokenHashMap = new HashMap<>();

                    mResponseModel = HttpRequest.makePostRequestWithJsonRefreshToken(refreshTokenUrl, refreshTokenJsonData, refreshTokenHashMap, "");
                    String s = checkResponse(true, mResponseModel, HttpURLConnection.HTTP_FORBIDDEN);
                    if (s.equalsIgnoreCase("success")) {
                        if (mHeadersData.containsKey("auth")) {
                            String s1 = AppController.getHelperSharedPreference().readPreference(mContext, mContext.getString(R.string.auth), "");
                            mHeadersData.put("auth", "" + s1);
                        }
                        switch (mWebserviceType) {
                            case "get":
                                mResponseModel = HttpRequest.getRequest(mUrlPassed, mHeadersData, serverType);
                                break;
                            case "post_hashmap":
                                mResponseModel = HttpRequest.postRequestsWithHashmap(mUrlPassed, mHashmapData, mHeadersData, serverType);
                                break;
                            case "post_json":
                                mResponseModel = HttpRequest.makePostRequestWithJson(mUrlPassed, mJsonData, mHeadersData, serverType);
                                break;
                            case "post_multi":
                                mResponseModel = HttpRequest.postRequestMultipart(mUrlPassed, mHeadersData, mFormData, mFilesData, serverType);
                                break;
                            case "delete_hashmap":
                                mResponseModel = HttpRequest.deleteRequestsWithHashmap(mUrlPassed, mHashmapData, mHeadersData, serverType);
                                break;
                            case "delete_json":
                                mResponseModel = HttpRequest.makeDeleteRequestWithJson(mUrlPassed, mJsonData, mHeadersData, serverType);
                                break;
                            case "delete_jsonArray":
                                mResponseModel = HttpRequest.makeDeleteRequestWithJsonArray(mUrlPassed, mJsonArray, mHeadersData, serverType);
                                break;
                        }
                        response = checkResponse(false, mResponseModel, HttpURLConnection.HTTP_UNAUTHORIZED);
                    } else if (s.equalsIgnoreCase("session expired") || s.equalsIgnoreCase("Unknown error") || s.equalsIgnoreCase("server error") || s.equalsIgnoreCase("client error") || s.equalsIgnoreCase("No data")) {
                        mResponseModel.setResponseCode("401");
                        mResponseModel.setServermsg("session expired");
                    }
                }
            } else if (Integer.parseInt(responseCode) == HttpURLConnection.HTTP_OK && !response.equalsIgnoreCase("")) {
                response = "success";

                t = parseJson(mResponseModel, mGenericClass);
                if (mUrlPassed.contains(URLs.BASE_URL_WCP_SERVER + "activity")) {
                    try {
                        ActivityInfoData activityInfoData = (ActivityInfoData) t;
                        JSONObject jsonObject = new JSONObject(mResponseModel.getResponse());
                        JSONObject jsonObjectActivity = jsonObject.getJSONObject("activity");
                        JSONArray jsonArray = jsonObjectActivity.getJSONArray("steps");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                            JSONObject format = jsonObject2.getJSONObject("format");
                            String key = jsonObject2.getString("key");
                            String defaultString = null;
                            try {
                                Object defaultValue = format.get("default");
                                defaultString = "";
                                if (defaultValue instanceof Integer) {
                                    defaultString = "" + (int) defaultValue;
                                } else if (defaultValue instanceof Double) {
                                    defaultString = "" + (double) defaultValue;
                                } else if (defaultValue instanceof Long) {
                                    defaultString = "" + (long) defaultValue;
                                } else if (defaultValue instanceof String) {
                                    defaultString = "" + (String) defaultValue;
                                } else {
                                    defaultString = "" + defaultValue;
                                }
                            } catch (JSONException e) {
                                defaultString = "";
                                e.printStackTrace();
                            }
                            for (int k = 0; k < activityInfoData.getActivity().getSteps().size(); k++) {
                                if (activityInfoData.getActivity().getSteps().get(k).getKey().equalsIgnoreCase(key)) {
                                    activityInfoData.getActivity().getSteps().get(k).getFormat().setDefaultValue(defaultString);
                                }
                            }
                        }
                        t = (T) activityInfoData;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } else {
                response = "";
            }
        } else {
            return "No network";
        }
        return response;
    }


    public void onPreExecute() {
    }

    private String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = mContext.getAssets().open("Activity_Question.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public void onPostExecute(String response) {
        String msg;
        switch (response) {
            case "timeout":
                msg = mResponseModel.getServermsg();
                setShowalert(msg);
                mOnAsyncRequestComplete.asyncResponseFailure(mResultCode, msg, mResponseModel.getResponseCode());
                break;
            case "No network":
                msg = mContext.getResources().getString(R.string.check_internet);
                setShowalert(msg);
                mOnAsyncRequestComplete.asyncResponseFailure(mResultCode, msg, "");
                break;
            case "error":
                msg = mResponseModel.getServermsg();
                setShowalert(msg);
                mOnAsyncRequestComplete.asyncResponseFailure(mResultCode, msg, mResponseModel.getResponseCode());
                break;
            case "":
                msg = mContext.getResources().getString(R.string.unknown_error);
                setShowalert(msg);
                mOnAsyncRequestComplete.asyncResponseFailure(mResultCode, msg, mResponseModel.getResponseCode());
                break;
            case "client error":
                msg = mResponseModel.getServermsg();
                setShowalert(msg);
                mOnAsyncRequestComplete.asyncResponseFailure(mResultCode, msg, mResponseModel.getResponseCode());
                break;
            case "server error":
                msg = mResponseModel.getServermsg();
                setShowalert(msg);
                mOnAsyncRequestComplete.asyncResponseFailure(mResultCode, msg, mResponseModel.getResponseCode());
                break;
            case "Unknown error":
                msg = mResponseModel.getServermsg();
                setShowalert(msg);
                mOnAsyncRequestComplete.asyncResponseFailure(mResultCode, msg, mResponseModel.getResponseCode());
                break;
            case "No data":
                msg = mResponseModel.getServermsg();
                setShowalert(msg);
                mOnAsyncRequestComplete.asyncResponseFailure(mResultCode, msg, mResponseModel.getResponseCode());
                break;
            case "session expired":
                msg = mResponseModel.getServermsg();
                mOnAsyncRequestComplete.asyncResponseFailure(mResultCode, msg, mResponseModel.getResponseCode());
                break;
            case "success":
                mOnAsyncRequestComplete.asyncResponse(t, mResultCode);
                break;
        }
    }


    private String checkResponse(boolean forRefreshToken, Responsemodel mResponseModel, int httpUnauthorized) {
        String responseCode = mResponseModel.getResponseCode();
        String response = mResponseModel.getResponse();

        if (responseCode.equalsIgnoreCase("0") && response.equalsIgnoreCase("timeout")) {
            response = "timeout";
        } else if (responseCode.equalsIgnoreCase("0") && response.equalsIgnoreCase("")) {
            response = "error";
        } else if (Integer.parseInt(responseCode) >= 201 && Integer.parseInt(responseCode) < 300 && response.equalsIgnoreCase("")) {
            response = "No data";
        } else if (Integer.parseInt(responseCode) >= 400 && Integer.parseInt(responseCode) < 500 && response.equalsIgnoreCase("http_not_ok")) {
            response = "client error";
        } else if (Integer.parseInt(responseCode) >= 500 && Integer.parseInt(responseCode) < 600 && response.equalsIgnoreCase("http_not_ok")) {
            response = "server error";
        } else if (response.equalsIgnoreCase("http_not_ok")) {
            response = "Unknown error";
        } else if (Integer.parseInt(responseCode) == httpUnauthorized) {
            response = "session expired";
        } else if (Integer.parseInt(responseCode) == HttpURLConnection.HTTP_OK && !response.equalsIgnoreCase("")) {
            response = "success";


            if (forRefreshToken) {
                t = parseJson(mResponseModel, RefreshToken.class);
                RefreshToken refreshToken = (RefreshToken) t;
                if (refreshToken != null) {
                    AppController.getHelperSharedPreference().writePreference(mContext, mContext.getString(R.string.auth), refreshToken.getAuth());
                    AppController.getHelperSharedPreference().writePreference(mContext, mContext.getString(R.string.refreshToken), refreshToken.getRefreshToken());
                } else {
                    response = "error";
                }
            } else {
                t = parseJson(mResponseModel, mGenericClass);
                if (mUrlPassed.contains(URLs.BASE_URL_WCP_SERVER + "activity")) {
                    try {
                        ActivityInfoData activityInfoData = (ActivityInfoData) t;
                        JSONObject jsonObject = new JSONObject(mResponseModel.getResponse());
                        JSONObject jsonObjectActivity = jsonObject.getJSONObject("activity");
                        JSONArray jsonArray = jsonObjectActivity.getJSONArray("steps");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject2 = jsonArray.getJSONObject(i);
                            JSONObject format = jsonObject2.getJSONObject("format");
                            String key = jsonObject2.getString("key");
                            String defaultString = null;
                            try {
                                Object defaultValue = format.get("default");
                                defaultString = "";
                                if (defaultValue instanceof Integer) {
                                    defaultString = "" + (int) defaultValue;
                                } else if (defaultValue instanceof Double) {
                                    defaultString = "" + (double) defaultValue;
                                } else if (defaultValue instanceof Long) {
                                    defaultString = "" + (long) defaultValue;
                                } else if (defaultValue instanceof String) {
                                    defaultString = "" + (String) defaultValue;
                                } else {
                                    defaultString = "" + defaultValue;
                                }
                            } catch (JSONException e) {
                                defaultString = "";
                                e.printStackTrace();
                            }
                            for (int k = 0; k < activityInfoData.getActivity().getSteps().size(); k++) {
                                if (activityInfoData.getActivity().getSteps().get(k).getKey().equalsIgnoreCase(key)) {
                                    activityInfoData.getActivity().getSteps().get(k).getFormat().setDefaultValue(defaultString);
                                }
                            }
                        }
                        t = (T) activityInfoData;

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return response;
    }

    private void setShowalert(String msg) {
        if (mShowAlert) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext, R.style.MyAlertDialogStyle);
            alertDialogBuilder.setTitle(mContext.getApplicationInfo().loadLabel(mContext.getPackageManager()).toString());

            alertDialogBuilder.setMessage(msg).setCancelable(false)
                    .setPositiveButton(mContext.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.dismiss();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }
    }

    private T parseJson(Responsemodel mResponseModel, Class mGenericClass) {
        Gson gson = new Gson();
        try {
            JsonReader reader = new JsonReader(new StringReader(mResponseModel.getResponse()));
            reader.setLenient(true);
            return gson.fromJson(reader, mGenericClass);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
