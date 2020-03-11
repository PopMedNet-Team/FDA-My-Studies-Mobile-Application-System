package com.harvard.webserviceModule;

import com.harvard.R;
import com.harvard.base.BaseSubscriber;
import com.harvard.webserviceModule.apiHelper.ApiCall;
import com.harvard.webserviceModule.apiHelper.ApiCallResponseServer;
import com.harvard.webserviceModule.events.RegistrationServerConfigEvent;
import com.harvard.webserviceModule.events.ResponseServerConfigEvent;
import com.harvard.webserviceModule.events.WCPConfigEvent;

/**
 * Created by Rohit on 2/16/2017.
 */

public class WebserviceSubscriber extends BaseSubscriber {
    public void onEvent(WCPConfigEvent wcpConfigEvent)
    {
        String url = "";
        if(wcpConfigEvent.getmContext().getResources().getString(R.string.app_stage).equalsIgnoreCase("development"))
        {
            url = wcpConfigEvent.getDevelopmentUrl() + wcpConfigEvent.getmUrl();
        }
        else
        {
            url = wcpConfigEvent.getProductionUrl() + wcpConfigEvent.getmUrl();
        }
        url = url.replaceAll(" ", "%20");
        if (wcpConfigEvent.getmRequestType().equalsIgnoreCase("get")) {
            ApiCall apiCall = new ApiCall(wcpConfigEvent.getmContext());
            apiCall.apiCallGet(url, wcpConfigEvent.getmHeaders(), wcpConfigEvent.gettClass(), wcpConfigEvent.getmResponseCode(), wcpConfigEvent.getV(), wcpConfigEvent.ismShowAlert(), "WCP");
        }
        else if(wcpConfigEvent.getmRequestType().equalsIgnoreCase("post_object"))
        {
            ApiCall apiCall = new ApiCall(wcpConfigEvent.getmContext());
            apiCall.apiCallPostJson(url, wcpConfigEvent.getmHeaders(), wcpConfigEvent.gettClass(), wcpConfigEvent.getmRequestParamsJson(), wcpConfigEvent.getmResponseCode(), wcpConfigEvent.getV(), wcpConfigEvent.ismShowAlert(), "WCP");
        }
        else if(wcpConfigEvent.getmRequestType().equalsIgnoreCase("delete"))
        {
            ApiCall apiCall = new ApiCall(wcpConfigEvent.getmContext());
            apiCall.apiCallDeleteHashmap(url, wcpConfigEvent.getmHeaders(), wcpConfigEvent.gettClass(), wcpConfigEvent.getmRequestParams(), wcpConfigEvent.getmResponseCode(), wcpConfigEvent.getV(), wcpConfigEvent.ismShowAlert(), "WCP");
        }
        else if(wcpConfigEvent.getmRequestType().equalsIgnoreCase("delete_object"))
        {
            ApiCall apiCall = new ApiCall(wcpConfigEvent.getmContext());
            apiCall.apiCallDeleteJson(url, wcpConfigEvent.getmHeaders(), wcpConfigEvent.gettClass(), wcpConfigEvent.getmRequestParamsJson(), wcpConfigEvent.getmResponseCode(), wcpConfigEvent.getV(), wcpConfigEvent.ismShowAlert(), "WCP");
        }
        else
        {
            ApiCall apiCall = new ApiCall(wcpConfigEvent.getmContext());
            apiCall.apiCallPostHashmap(url, wcpConfigEvent.getmHeaders(), wcpConfigEvent.gettClass(), wcpConfigEvent.getmRequestParams(), wcpConfigEvent.getmResponseCode(), wcpConfigEvent.getV(), wcpConfigEvent.ismShowAlert(), "WCP");
        }
    }

    public void onEvent(RegistrationServerConfigEvent registrationServerConfigEvent)
    {
        String url = "";
        if(registrationServerConfigEvent.getmContext().getResources().getString(R.string.app_stage).equalsIgnoreCase("development"))
        {
            url = registrationServerConfigEvent.getDevelopmentUrl() + registrationServerConfigEvent.getmUrl();
        }
        else
        {
            url = registrationServerConfigEvent.getProductionUrl() + registrationServerConfigEvent.getmUrl();
        }
        url = url.replaceAll(" ", "%20");
        if (registrationServerConfigEvent.getmRequestType().equalsIgnoreCase("get")) {
            ApiCall apiCall = new ApiCall(registrationServerConfigEvent.getmContext());
            apiCall.apiCallGet(url, registrationServerConfigEvent.getmHeaders(), registrationServerConfigEvent.gettClass(), registrationServerConfigEvent.getmResponseCode(), registrationServerConfigEvent.getV(), registrationServerConfigEvent.ismShowAlert(), "");
        }
        else if(registrationServerConfigEvent.getmRequestType().equalsIgnoreCase("post_object"))
        {
            ApiCall apiCall = new ApiCall(registrationServerConfigEvent.getmContext());
            apiCall.apiCallPostJson(url, registrationServerConfigEvent.getmHeaders(), registrationServerConfigEvent.gettClass(), registrationServerConfigEvent.getmRequestParamsJson(), registrationServerConfigEvent.getmResponseCode(), registrationServerConfigEvent.getV(), registrationServerConfigEvent.ismShowAlert(), "");
        }
        else if(registrationServerConfigEvent.getmRequestType().equalsIgnoreCase("delete"))
        {
            ApiCall apiCall = new ApiCall(registrationServerConfigEvent.getmContext());
            apiCall.apiCallDeleteHashmap(url, registrationServerConfigEvent.getmHeaders(), registrationServerConfigEvent.gettClass(), registrationServerConfigEvent.getmRequestParams(), registrationServerConfigEvent.getmResponseCode(), registrationServerConfigEvent.getV(), registrationServerConfigEvent.ismShowAlert(), "");
        }
        else if(registrationServerConfigEvent.getmRequestType().equalsIgnoreCase("delete_object"))
        {
            ApiCall apiCall = new ApiCall(registrationServerConfigEvent.getmContext());
            apiCall.apiCallDeleteJson(url, registrationServerConfigEvent.getmHeaders(), registrationServerConfigEvent.gettClass(), registrationServerConfigEvent.getmRequestParamsJson(), registrationServerConfigEvent.getmResponseCode(), registrationServerConfigEvent.getV(), registrationServerConfigEvent.ismShowAlert(), "");
        }
        else if(registrationServerConfigEvent.getmRequestType().equalsIgnoreCase("delete_array"))
        {
            ApiCall apiCall = new ApiCall(registrationServerConfigEvent.getmContext());
            apiCall.apiCallDeleteJsonArray(url, registrationServerConfigEvent.getmHeaders(), registrationServerConfigEvent.gettClass(), registrationServerConfigEvent.getmRequestParamsJsonArray(), registrationServerConfigEvent.getmResponseCode(), registrationServerConfigEvent.getV(), registrationServerConfigEvent.ismShowAlert(), "");
        }
        else
        {
            ApiCall apiCall = new ApiCall(registrationServerConfigEvent.getmContext());
            apiCall.apiCallPostHashmap(url, registrationServerConfigEvent.getmHeaders(), registrationServerConfigEvent.gettClass(), registrationServerConfigEvent.getmRequestParams(), registrationServerConfigEvent.getmResponseCode(), registrationServerConfigEvent.getV(), registrationServerConfigEvent.ismShowAlert(), "");
        }
    }

    public void onEvent(ResponseServerConfigEvent responseServerConfigEvent)
    {
        String url = "";
        if(responseServerConfigEvent.getmContext().getResources().getString(R.string.app_stage).equalsIgnoreCase("development"))
        {
            url = responseServerConfigEvent.getDevelopmentUrl() + responseServerConfigEvent.getmUrl();
        }
        else
        {
            url = responseServerConfigEvent.getProductionUrl() + responseServerConfigEvent.getmUrl();
        }
        url = url.replaceAll(" ", "%20");
        if (responseServerConfigEvent.getmRequestType().equalsIgnoreCase("get")) {
            ApiCallResponseServer apiCall = new ApiCallResponseServer(responseServerConfigEvent.getmContext());
            apiCall.apiCallGet(url, responseServerConfigEvent.getmHeaders(), responseServerConfigEvent.gettClass(), responseServerConfigEvent.getmResponseCode(), responseServerConfigEvent.getV(), responseServerConfigEvent.ismShowAlert(), "Response");
        }
        else if(responseServerConfigEvent.getmRequestType().equalsIgnoreCase("post_object"))
        {
            ApiCallResponseServer apiCall = new ApiCallResponseServer(responseServerConfigEvent.getmContext());
            apiCall.apiCallPostJson(url, responseServerConfigEvent.getmHeaders(), responseServerConfigEvent.gettClass(), responseServerConfigEvent.getmRequestParamsJson(), responseServerConfigEvent.getmResponseCode(), responseServerConfigEvent.getV(), responseServerConfigEvent.ismShowAlert(), "Response");
        }
        else if(responseServerConfigEvent.getmRequestType().equalsIgnoreCase("delete"))
        {
            ApiCallResponseServer apiCall = new ApiCallResponseServer(responseServerConfigEvent.getmContext());
            apiCall.apiCallDeleteHashmap(url, responseServerConfigEvent.getmHeaders(), responseServerConfigEvent.gettClass(), responseServerConfigEvent.getmRequestParams(), responseServerConfigEvent.getmResponseCode(), responseServerConfigEvent.getV(), responseServerConfigEvent.ismShowAlert(), "Response");
        }
        else
        {
            ApiCallResponseServer apiCall = new ApiCallResponseServer(responseServerConfigEvent.getmContext());
            apiCall.apiCallPostHashmap(url, responseServerConfigEvent.getmHeaders(), responseServerConfigEvent.gettClass(), responseServerConfigEvent.getmRequestParams(), responseServerConfigEvent.getmResponseCode(), responseServerConfigEvent.getV(), responseServerConfigEvent.ismShowAlert(), "Response");
        }
    }
}