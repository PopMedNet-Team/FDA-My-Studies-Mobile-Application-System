package com.harvard.webserviceModule.events;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Rohit on 2/16/2017.
 */

public abstract class WebserviceConfigEvent<T, V> {
    private String mUrl;
    private Class<T> tClass;
    private V v;
    private HashMap<String, String> mRequestParams = new HashMap<>();
    private HashMap<String, String> mHeaders = new HashMap<>();
    private JSONObject mRequestParamsJson;
    private JSONArray mRequestParamsJsonArray;
    private boolean mShowAlert;
    private String mRequestType;
    private int mResponseCode;
    private Context mContext;

    public WebserviceConfigEvent(String method, String url, int requestCode, Context context, Class modelclass, HashMap<String, String> params, HashMap<String, String> header, JSONObject jsonobj, boolean showAlert, V v) {
        mUrl = url;
        tClass = modelclass;
        this.v = v;
        mRequestParams=params;
        mHeaders=header;
        mRequestParamsJson=jsonobj;
        mShowAlert=showAlert;
        mRequestType=method;
        mResponseCode=requestCode;
        mContext=context;
    }

    public WebserviceConfigEvent(String method, String url, int requestCode, Context context, Class modelclass, HashMap<String, String> header, JSONArray jsonArray, boolean showAlert, V v) {
        mUrl = url;
        tClass = modelclass;
        this.v = v;
        mHeaders=header;
        mRequestParamsJsonArray=jsonArray;
        mShowAlert=showAlert;
        mRequestType=method;
        mResponseCode=requestCode;
        mContext=context;
    }

    public JSONArray getmRequestParamsJsonArray() {
        return mRequestParamsJsonArray;
    }

    public void setmRequestParamsJsonArray(JSONArray mRequestParamsJsonArray) {
        this.mRequestParamsJsonArray = mRequestParamsJsonArray;
    }

    public abstract String getProductionUrl();

    public abstract String getDevelopmentUrl();

    public JSONObject getmRequestParamsJson() {
        return mRequestParamsJson;
    }

    public void setmRequestParamsJson(JSONObject mRequestParamsJson) {
        this.mRequestParamsJson = mRequestParamsJson;
    }

    public Context getmContext() {
        return mContext;
    }

    public void setmContext(Context mContext) {
        this.mContext = mContext;
    }

    public int getmResponseCode() {
        return mResponseCode;
    }

    public void setmResponseCode(int mResponseCode) {
        this.mResponseCode = mResponseCode;
    }

    public String getmRequestType() {
        return mRequestType;
    }

    public void setmRequestType(String mRequestType) {
        this.mRequestType = mRequestType;
    }

    public String getmUrl() {
        return mUrl;
    }

    public void setmUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public Class<T> gettClass() {
        return tClass;
    }

    public void settClass(Class<T> tClass) {
        this.tClass = tClass;
    }

    public V getV() {
        return v;
    }

    /**
     * setting response interface
     *
     * @param v
     */
    public void setV(V v) {
        this.v = v;
    }

    public HashMap<String, String> getmRequestParams() {
        return mRequestParams;
    }

    public void setmRequestParams(HashMap<String, String> mRequestParams) {
        this.mRequestParams = mRequestParams;
    }

    public HashMap<String, String> getmHeaders() {
        return mHeaders;
    }

    public void setmHeaders(HashMap<String, String> mHeaders) {
        this.mHeaders = mHeaders;
    }

    public boolean ismShowAlert() {
        return mShowAlert;
    }

    public void setmShowAlert(boolean mShowAlert) {
        this.mShowAlert = mShowAlert;
    }
}
