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

package com.harvard.fda.webserviceModule;

import com.harvard.fda.R;
import com.harvard.fda.base.BaseSubscriber;
import com.harvard.fda.webserviceModule.apiHelper.ApiCall;
import com.harvard.fda.webserviceModule.apiHelper.ApiCallResponseServer;
import com.harvard.fda.webserviceModule.events.RegistrationServerConfigEvent;
import com.harvard.fda.webserviceModule.events.ResponseServerConfigEvent;
import com.harvard.fda.webserviceModule.events.WCPConfigEvent;

/**
 * Created by Rohit on 2/16/2017.
 */

public class WebserviceSubscriber extends BaseSubscriber {
    public void onEvent(WCPConfigEvent wcpConfigEvent)
    {
        String url = "";
        url = wcpConfigEvent.getDevelopmentUrl() + wcpConfigEvent.getmUrl();
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
        url = registrationServerConfigEvent.getDevelopmentUrl() + registrationServerConfigEvent.getmUrl();
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
        url = responseServerConfigEvent.getDevelopmentUrl() + responseServerConfigEvent.getmUrl();
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