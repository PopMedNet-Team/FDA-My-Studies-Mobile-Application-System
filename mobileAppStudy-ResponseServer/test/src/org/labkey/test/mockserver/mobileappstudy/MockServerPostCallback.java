/*
 * Copyright (c) 2019 LabKey Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.labkey.test.mockserver.mobileappstudy;

import org.labkey.test.tests.mobileappstudy.ForwardResponseTest;
import org.mockserver.mock.action.ExpectationResponseCallback;
import org.mockserver.model.HttpRequest;
import org.mockserver.model.HttpResponse;

import static org.mockserver.model.HttpResponse.notFoundResponse;
import static org.mockserver.model.HttpResponse.response;

public class MockServerPostCallback implements ExpectationResponseCallback
{
    public static final int PUT_SUCCESS_RESPONSE_CODE = 202;
    public static final int UNAUTHORIZED = 401;
    public static final int PUT_FAILURE_RESPONSE_CODE = 500;

    @Override
    public HttpResponse handle(HttpRequest httpRequest)
    {
        if (httpRequest.withHeader(ForwardResponseTest.OAUTH_TOKEN_HEADER).getHeader(ForwardResponseTest.OAUTH_TOKEN_HEADER).equals(ForwardResponseTest.OAUTH_INVALID_TEST_TOKEN))
        {
            return response()
                    .withStatusCode(UNAUTHORIZED);
        }
        else if (httpRequest.getPath().getValue().contains(ForwardResponseTest.STUDY_NAME01) || httpRequest.getPath().getValue().contains(ForwardResponseTest.STUDY_NAME03)
                || httpRequest.getPath().getValue().contains(ForwardResponseTest.STUDY_NAME04) || httpRequest.getPath().getValue().contains(ForwardResponseTest.STUDY_NAME06))
            return response()
                    .withStatusCode(PUT_SUCCESS_RESPONSE_CODE);
        else if (httpRequest.getPath().getValue().contains(ForwardResponseTest.STUDY_NAME02) || httpRequest.getPath().getValue().contains(ForwardResponseTest.STUDY_NAME05))
            return response()
                    .withStatusCode(PUT_FAILURE_RESPONSE_CODE);
        else if (httpRequest.getPath().getValue().contains(ForwardResponseTest.OAUTH_TOKEN_URL_PATH))
            return response()
                .withStatusCode(200)
                .withBody(String.format("{\"%1$s\":\"%2$s\"}", ForwardResponseTest.OAUTH_TOKEN_FIELD, ForwardResponseTest.OAUTH_VALID_TEST_TOKEN));
        else if (httpRequest.getPath().getValue().contains(ForwardResponseTest.OAUTH_ENDPOINT_PATH) || httpRequest.getPath().getValue().contains(ForwardResponseTest.OAUTH_ENDPOINT_PATH2))
            return response()
                    .withStatusCode(200);
        else
            return notFoundResponse();
    }
}
