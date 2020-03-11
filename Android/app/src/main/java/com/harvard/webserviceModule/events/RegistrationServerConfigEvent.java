package com.harvard.webserviceModule.events;

import android.content.Context;

import com.harvard.utils.URLs;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Rohit on 2/16/2017.
 */

public class RegistrationServerConfigEvent<V> extends WebserviceConfigEvent {

    /**
     *
     * @param method
     * @param Url
     * @param RequestCode
     * @param context
     * @param modelclass
     * @param params
     * @param headers
     * @param jsonobj
     * @param showAlert
     */
    public RegistrationServerConfigEvent(String method, String Url, int RequestCode, Context context, Class modelclass, HashMap<String, String> params, HashMap<String,String> headers, JSONObject jsonobj, boolean showAlert, V v) {
        super(method,Url,RequestCode,context,modelclass,params,headers,jsonobj,showAlert,v);
    }

    public RegistrationServerConfigEvent(String method, String Url, int RequestCode, Context context, Class modelclass, HashMap<String,String> headers, JSONArray jsonArray, boolean showAlert, V v) {
        super(method,Url,RequestCode,context,modelclass,headers,jsonArray,showAlert,v);
    }

    @Override
    public String getProductionUrl() {
        return URLs.BASE_URL_REGISTRATION_SERVER;
    }

    @Override
    public String getDevelopmentUrl() {
        return URLs.BASE_URL_REGISTRATION_SERVER;
    }
}
