package com.harvard.webserviceModule.events;

import android.content.Context;

import com.harvard.utils.URLs;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Rohit on 2/16/2017.
 */

public class ResponseServerConfigEvent<V> extends WebserviceConfigEvent {
    public ResponseServerConfigEvent(String method, String url, int requestCode, Context context, Class modelclass, HashMap params, HashMap header, JSONObject jsonobj, boolean showAlert, V o) {
        super(method, url, requestCode, context, modelclass, params, header, jsonobj, showAlert, o);
    }

    @Override
    public String getProductionUrl() {
        return URLs.BASE_URL_RESPONSE_SERVER;
    }

    @Override
    public String getDevelopmentUrl() {
        return URLs.BASE_URL_RESPONSE_SERVER;
    }
}
