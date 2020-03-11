package com.harvard.webserviceModule.events;

import android.content.Context;

import com.harvard.utils.URLs;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by Rohit on 2/16/2017.
 */

public class WCPConfigEvent<V> extends WebserviceConfigEvent {
    public WCPConfigEvent(String method, String url, int requestCode, Context context, Class modelclass, HashMap params, HashMap header, JSONObject jsonobj, boolean showAlert, V v) {
        super(method, url, requestCode, context, modelclass, params, header, jsonobj, showAlert, v);
    }

    @Override
    public String getProductionUrl() {
        return URLs.BASE_URL_WCP_SERVER;
    }

    @Override
    public String getDevelopmentUrl() {
        return URLs.BASE_URL_WCP_SERVER;
    }
}
