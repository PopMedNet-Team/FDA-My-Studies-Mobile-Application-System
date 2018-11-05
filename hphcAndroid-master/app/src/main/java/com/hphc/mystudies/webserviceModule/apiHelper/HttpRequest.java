/*
 * Copyright © 2017-2018 Harvard Pilgrim Health Care Institute (HPHCI) and its Contributors.
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * Funding Source: Food and Drug Administration ("Funding Agency") effective 18 September 2014 as Contract no.
 * HHSF22320140030I/HHSF22301006T (the "Prime Contract").
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 */
package com.hphc.mystudies.webserviceModule.apiHelper;

import android.os.Build;
import android.util.Base64;

import com.google.gson.Gson;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;

public class HttpRequest {

    private static String basicAuth = "com.harvard.fda:a7296a62-44e8-4a06-8d85-50bc861d6c64";

    /**
     * To make a Get request
     *
     * @param url          -->  url path
     * @param mHeadersData --> null if no header
     * @return Responsemodel
     */
   public static Responsemodel getRequest(String url, HashMap<String, String> mHeadersData, String serverType) {
        StringBuffer response = new StringBuffer();
        Responsemodel responseModel = new Responsemodel();
        String responsee;
        String responseData;
        HttpURLConnection urlConnection;
        int responseCode = 0;
        try {
            URL obj = new URL(url);
            urlConnection = (HttpURLConnection) obj.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setReadTimeout(180000);// 3 min timeout
            if (serverType.equalsIgnoreCase("WCP")) {
                String encoding = Base64.encodeToString(basicAuth.getBytes(), Base64.DEFAULT);
                urlConnection.setRequestProperty("Authorization", "Basic " + encoding);
            }

            if (mHeadersData != null) {
                Set mapSet = (Set) mHeadersData.entrySet();
                Iterator mapIterator = mapSet.iterator();
                while (mapIterator.hasNext()) {
                    Map.Entry mapEntry = (Map.Entry) mapIterator.next();
                    String keyValue = (String) mapEntry.getKey();
                    String value = (String) mapEntry.getValue();
                    urlConnection.setRequestProperty(keyValue, value);
                }
            }
            try {
                // Will throw IOException if server responds with 401.
                responseCode = urlConnection.getResponseCode();
            } catch (IOException e) {
                // Will return 401, because now connection has the correct internal state.
                responseCode = urlConnection.getResponseCode();
            }
            if (serverType.equalsIgnoreCase("Response")) {
                BufferedReader in = null;
                try {
                    in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                } catch (IOException e) {
                    in = new BufferedReader(new InputStreamReader(urlConnection.getErrorStream()));
                }
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                urlConnection.disconnect();
                responsee = response.toString();
                responseData = response.toString();
            } else {
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();
                    urlConnection.disconnect();
                    responsee = response.toString();
                    responseData = response.toString();
                } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    responseData = "";
                    responsee = "session expired";
                } else {
                    responseData = "";
                    responsee = "http_not_ok";
                }
            }
            if (urlConnection.getHeaderField("StatusMessage") != null) {
                responseModel.setServermsg(urlConnection.getHeaderField("StatusMessage"));
            } else if (responseCode != HttpURLConnection.HTTP_OK && urlConnection.getHeaderField("StatusMessage") == null) {
                responseModel.setServermsg("server error");
            } else {
                responseModel.setServermsg("success");
            }
        } catch (ConnectException e) {
            responseModel.setServermsg("No internet connection/cannot connect to server");
            responseData = "timeout";
            responsee = "timeout";
        } catch (Exception e) {
            responseModel.setServermsg("No internet connection/cannot connect to server");
            responsee = "";
            responseData = "";
        }

        responseModel.setResponseCode("" + responseCode);
        responseModel.setResponse(responsee);
        responseModel.setResponseData(responseData);

        return responseModel;
    }

    /**
     * To make post request using hashmap
     *
     * @param url          --> url path
     * @param params       --> Hashmap params
     * @param mHeadersData --> null if no header
     * @return Responsemodel
     */
    static Responsemodel postRequestsWithHashmap(String url, HashMap<String, String> params, HashMap<String, String> mHeadersData, String serverType) {
        Responsemodel responseModel = new Responsemodel();
        String response = "";
        String responseData = "";
        int responseCode = 0;
        URL url1;
        try {
            url1 = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
            conn.setReadTimeout(180000);
            conn.setConnectTimeout(180000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            if (serverType.equalsIgnoreCase("WCP")) {
                String encoding = Base64.encodeToString(basicAuth.getBytes(), Base64.DEFAULT);
                conn.setRequestProperty("Authorization", "Basic " + encoding);
            }

            if (mHeadersData != null) {
                Set mapSet = (Set) mHeadersData.entrySet();
                Iterator mapIterator = mapSet.iterator();
                while (mapIterator.hasNext()) {
                    Map.Entry mapEntry = (Map.Entry) mapIterator.next();
                    String keyValue = (String) mapEntry.getKey();
                    String value = (String) mapEntry.getValue();
                    conn.setRequestProperty(keyValue, value);
                }
            }

            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(params));

            writer.flush();
            writer.close();
            os.close();

            try {
                // Will throw IOException if server responds with 401.
                responseCode = conn.getResponseCode();
            } catch (IOException e) {
                // Will return 401, because now connection has the correct internal state.
                responseCode = conn.getResponseCode();
            }
            if (serverType.equalsIgnoreCase("Response")) {
                String line;
                BufferedReader br;
                try {
                    br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } catch (IOException e) {
                    br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                }

                while ((line = br.readLine()) != null) {
                    response += line;
                    responseData += line;
                }
            } else {
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                        responseData += line;
                    }
                } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    response = "session expired";
                    responseData = "";
                } else {
                    responseData = "";
                    response = "http_not_ok";
                }
            }
            if (conn.getHeaderField("StatusMessage") != null) {
                responseModel.setServermsg(conn.getHeaderField("StatusMessage"));
            } else if (responseCode != HttpURLConnection.HTTP_OK && conn.getHeaderField("StatusMessage") == null) {
                responseModel.setServermsg("server error");
            } else {
                responseModel.setServermsg("success");
            }
        } catch (SocketTimeoutException e) {
            responseModel.setServermsg("No internet connection/cannot connect to server");
            responseData = "";
            response = "timeout";
        } catch (Exception e) {
            responseModel.setServermsg("No internet connection/cannot connect to server");
            responseData = "";
            response = "";
        }
        responseModel.setResponseCode("" + responseCode);
        responseModel.setResponse(response);
        responseModel.setResponseData(responseData);
        return responseModel;
    }

    /**
     * To make post request using json object
     *
     * @param urlpath      -->url path
     * @param jsonObject   -->json object
     * @param mHeadersData --> null if no header
     * @return Responsemodel
     */
    static Responsemodel makePostRequestWithJson(String urlpath, JSONObject jsonObject, HashMap<String, String> mHeadersData, String serverType) {
        Responsemodel responseModel = new Responsemodel();
        String response = "";
        String responseData = "";
        int responseCode = 0;
        URL url1;
        try {
            url1 = new URL(urlpath);
            HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
            conn.setReadTimeout(180000);
            conn.setConnectTimeout(180000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            if (serverType.equalsIgnoreCase("WCP")) {
                String encoding = Base64.encodeToString(basicAuth.getBytes(), Base64.DEFAULT);
                conn.setRequestProperty("Authorization", "Basic " + encoding);
            }

            if (mHeadersData != null) {
                Set mapSet = (Set) mHeadersData.entrySet();
                Iterator mapIterator = mapSet.iterator();
                while (mapIterator.hasNext()) {
                    Map.Entry mapEntry = (Map.Entry) mapIterator.next();
                    String keyValue = (String) mapEntry.getKey();
                    String value = (String) mapEntry.getValue();
                    conn.setRequestProperty(keyValue, value);
                }
            }

            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(jsonObject.toString());

            writer.flush();
            writer.close();
            os.close();

            try {
                // Will throw IOException if server responds with 401.
                responseCode = conn.getResponseCode();
            } catch (IOException e) {
                // Will return 401, because now connection has the correct internal state.
                responseCode = conn.getResponseCode();
            }

            if (serverType.equalsIgnoreCase("Response")) {
                String line;
                BufferedReader br;
                try {
                    br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } catch (IOException e) {
                    br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                }
                while ((line = br.readLine()) != null) {
                    response += line;
                    responseData += line;
                }
            } else {
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                        responseData += line;
                    }
                } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    response = "session expired";
                    responseData = "";
                } else {
                    response = "http_not_ok";
                    responseData = "";
                }
            }
            if (conn.getHeaderField("StatusMessage") != null) {
                responseModel.setServermsg(conn.getHeaderField("StatusMessage"));
            } else if (responseCode != HttpURLConnection.HTTP_OK && conn.getHeaderField("StatusMessage") == null) {
                responseModel.setServermsg("server error");
            } else {
                responseModel.setServermsg("success");
            }
        } catch (SocketTimeoutException e) {
            responseModel.setServermsg("No internet connection/cannot connect to server");
            response = "timeout";
            responseData = "";
        } catch (Exception e) {
            responseModel.setServermsg("No internet connection/cannot connect to server");
            response = "";
            responseData = "";
        }

        responseModel.setResponseCode("" + responseCode);
        responseModel.setResponse(response);
        responseModel.setResponseData(responseData);
        return responseModel;
    }

    /**
     * To make post request using json object
     *
     * @param urlpath      -->url path
     * @param jsonObject   -->json object
     * @param mHeadersData --> null if no header
     * @return Responsemodel
     */
    static Responsemodel makePostRequestWithJsonRefreshToken(String urlpath, JSONObject jsonObject, HashMap<String, String> mHeadersData, String serverType) {
        Responsemodel responseModel = new Responsemodel();
        String response = "";
        String responseData = "";
        int responseCode = 0;
        URL url1;
        try {
            url1 = new URL(urlpath);
            HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
            conn.setReadTimeout(180000);
            conn.setConnectTimeout(180000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            if (serverType.equalsIgnoreCase("WCP")) {
                String encoding = Base64.encodeToString(basicAuth.getBytes(), Base64.DEFAULT);
                conn.setRequestProperty("Authorization", "Basic " + encoding);
            }

            if (mHeadersData != null) {
                Set mapSet = (Set) mHeadersData.entrySet();
                Iterator mapIterator = mapSet.iterator();
                while (mapIterator.hasNext()) {
                    Map.Entry mapEntry = (Map.Entry) mapIterator.next();
                    String keyValue = (String) mapEntry.getKey();
                    String value = (String) mapEntry.getValue();
                    conn.setRequestProperty(keyValue, value);
                }
            }

            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(jsonObject.toString());

            writer.flush();
            writer.close();
            os.close();

            try {
                // Will throw IOException if server responds with 401.
                responseCode = conn.getResponseCode();
            } catch (IOException e) {
                // Will return 401, because now connection has the correct internal state.
                responseCode = conn.getResponseCode();
            }

            if (serverType.equalsIgnoreCase("Response")) {
                String line;
                BufferedReader br;
                try {
                    br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                } catch (IOException e) {
                    br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                }
                while ((line = br.readLine()) != null) {
                    response += line;
                    responseData += line;
                }
            } else {
                if (responseCode == HttpsURLConnection.HTTP_OK) {
                    String line;
                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    while ((line = br.readLine()) != null) {
                        response += line;
                        responseData += line;
                    }
                } else if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
                    response = "session expired";
                    responseData = "";
                } else {
                    response = "http_not_ok";
                    responseData = "";
                }
            }
            if (conn.getHeaderField("StatusMessage") != null) {
                responseModel.setServermsg(conn.getHeaderField("StatusMessage"));
            } else if (responseCode != HttpURLConnection.HTTP_OK && conn.getHeaderField("StatusMessage") == null) {
                responseModel.setServermsg("server error");
            } else {
                responseModel.setServermsg("success");
            }
        } catch (SocketTimeoutException e) {
            responseModel.setServermsg("No internet connection/cannot connect to server");
            response = "timeout";
            responseData = "";
        } catch (Exception e) {
            responseModel.setServermsg("No internet connection/cannot connect to server");
            response = "";
            responseData = "";
        }

        responseModel.setResponseCode("" + responseCode);
        responseModel.setResponse(response);
        responseModel.setResponseData(responseData);
        return responseModel;
    }

    /**
     * method to parse hashmap to json
     *
     * @param params --> params of hash map
     * @return String
     */
    private static String getPostDataString(HashMap<String, String> params) {
        return new Gson().toJson(params);
    }


    /**
     * To make post request for form data and files upload
     *
     * @param urlPath  --> url path
     * @param headers  --> null if no header
     * @param formData --> null if no form data
     * @param files    --> null if no files
     * @return web-service response as String
     */
    static Responsemodel postRequestMultipart(String urlPath, HashMap<String, String> headers, HashMap<String, String> formData, HashMap<String, File> files, String serverType) {
        Responsemodel responseModel = new Responsemodel();
        HttpURLConnection httpConn;
        String response = "";
        String responseData = "";
        String LINE_FEED = "\r\n";
        int responseCode = 0;
        try {
            URL url = new URL(urlPath);
            httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setUseCaches(false);
            httpConn.setDoOutput(true); // indicates POST method
            httpConn.setDoInput(true);
            httpConn.setReadTimeout(180000);
            httpConn.setConnectTimeout(180000);
            httpConn.setRequestProperty("Content-Type", "multipart/form-data;");
            httpConn.setRequestProperty("User-Agent", "CodeJava Agent");
            if (serverType.equalsIgnoreCase("WCP")) {
                String encoding = Base64.encodeToString(basicAuth.getBytes(), Base64.DEFAULT);
                httpConn.setRequestProperty("Authorization", "Basic " + encoding);
            }
            OutputStream outputStream = httpConn.getOutputStream();
            PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"), true);

            if (headers != null) {
                Set keys = headers.keySet();
                for (Iterator i = keys.iterator(); i.hasNext(); ) {
                    writer.append(i.next() + ": " + headers.get(i.next())).append(LINE_FEED);
                    writer.flush();
                }
            }
            if (formData != null) {
                Set keys = formData.keySet();
                for (Iterator i = keys.iterator(); i.hasNext(); ) {
                    writer.append(LINE_FEED);
                    writer.append("Content-Disposition: form-data; name=\"" + i.next() + "\"").append(LINE_FEED);
                    writer.append("Content-Type: text/plain; charset=UTF-8").append(LINE_FEED);
                    writer.append(LINE_FEED);
                    writer.append(formData.get(i.next())).append(LINE_FEED);
                    writer.flush();
                }
            }
            if (files != null) {
                Set keys = files.keySet();
                for (Iterator i = keys.iterator(); i.hasNext(); ) {
                    FileInputStream inputStream = null;
                    try {
                        String fileName = files.get(i.next()).getName();
                        writer.append("Content-Disposition: form-data; name=\"" + i.next() + "\"; filename=\"" + fileName + "\"").append(LINE_FEED);
                        writer.append("Content-Type: " + URLConnection.guessContentTypeFromName(fileName)).append(LINE_FEED);
                        writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
                        writer.append(LINE_FEED);
                        writer.flush();

                        inputStream = new FileInputStream(files.get(i.next()));
                        byte[] buffer = new byte[4096];
                        int bytesRead = -1;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                        outputStream.flush();
                        writer.append(LINE_FEED);
                        writer.flush();
                    } catch (IOException e) {
                    }
                    finally {
                        try {
                            if (inputStream != null) {
                                inputStream.close();
                            }
                        } catch (IOException e) {
                        }
                    }
                }
            }
            writer.close();
            // checks server's status code first
            responseCode = httpConn.getResponseCode();
            if (serverType.equalsIgnoreCase("Response")) {
                BufferedReader reader;
                try {
                    reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
                } catch (IOException e) {
                    reader = new BufferedReader(new InputStreamReader(httpConn.getErrorStream()));
                }
                String line;
                while ((line = reader.readLine()) != null) {
                    response += line;
                    responseData += line;
                }
                reader.close();
                httpConn.disconnect();
            } else {
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response += line;
                        responseData += line;
                    }
                    reader.close();
                    httpConn.disconnect();
                } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                    response = "session expired";
                    responseData = "";
                } else {
                    response = "http_not_ok";
                    responseData = "";
                }
            }
            if (httpConn.getHeaderField("StatusMessage") != null) {
                responseModel.setServermsg(httpConn.getHeaderField("StatusMessage"));
            } else if (responseCode != HttpURLConnection.HTTP_OK && httpConn.getHeaderField("StatusMessage") == null) {
                responseModel.setServermsg("server error");
            } else {
                responseModel.setServermsg("success");
            }
        } catch (SocketTimeoutException e) {
            responseModel.setServermsg("No internet connection/cannot connect to server");
            response = "timeout";
            responseData = "";
        } catch (Exception e) {
            responseModel.setServermsg("No internet connection/cannot connect to server");
            response = "";
            responseData = "";
        }
        responseModel.setResponseCode("" + responseCode);
        responseModel.setResponse(response);
        responseModel.setResponseData(responseData);
        return responseModel;
    }


    /**
     * To make DELETE request using hashmap
     *
     * @param url          --> url path
     * @param params       --> Hashmap params
     * @param mHeadersData --> null if no header
     * @param serverType
     * @return Responsemodel
     */

    static Responsemodel deleteRequestsWithHashmap(String url, HashMap<String, String> params, HashMap<String, String> mHeadersData, String serverType) {
        Responsemodel responseModel = new Responsemodel();
        String response = "";
        String responseData = "";
        int responseCode = 0;
        URL url1;
        if (Build.VERSION.SDK_INT >= 21)  {
            try {
                url1 = new URL(url);
                HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
                conn.setReadTimeout(180000);
                conn.setConnectTimeout(180000);
                conn.setRequestMethod("DELETE");
                conn.setDoInput(true);
                conn.setRequestProperty("Content-Type", "application/json");

                if (mHeadersData != null) {
                    Set mapSet = (Set) mHeadersData.entrySet();
                    Iterator mapIterator = mapSet.iterator();
                    while (mapIterator.hasNext()) {
                        Map.Entry mapEntry = (Map.Entry) mapIterator.next();
                        String keyValue = (String) mapEntry.getKey();
                        String value = (String) mapEntry.getValue();
                        conn.setRequestProperty(keyValue, value);
                    }
                }

                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(getPostDataString(params));

                writer.flush();
                writer.close();
                os.close();
                try {
                    // Will throw IOException if server responds with 401.
                    responseCode = conn.getResponseCode();
                } catch (IOException e) {
                    // Will return 401, because now connection has the correct internal state.
                    responseCode = conn.getResponseCode();
                }

                InputStream is = null;
                if (responseCode >= 200 && responseCode < 400) {
                    is = conn.getInputStream();
                } else {
                    is = conn.getErrorStream();
                }
                String line;
                if (serverType.equalsIgnoreCase("Response")) {
                    BufferedReader br;
                    try {
                        br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    } catch (IOException e) {
                        br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    }
                    while ((line = br.readLine()) != null) {
                        response += line;
                        responseData += line;
                    }
                    br.close();
                    conn.disconnect();
                } else {
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(is));
                        while ((line = br.readLine()) != null) {
                            response += line;
                            responseData += line;
                        }
                        br.close();
                        conn.disconnect();
                    } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                        responseData = "";
                        response = "session expired";
                    } else {
                        responseData = "";
                        response = "http_not_ok";
                    }
                }
                if (conn.getHeaderField("StatusMessage") != null) {
                    responseModel.setServermsg(conn.getHeaderField("StatusMessage"));
                } else {
                    responseModel.setServermsg("success");
                }

            } catch (SocketTimeoutException e) {
                responseData = "";
                responseModel.setServermsg("No internet connection/cannot connect to server");
                response = "timeout";
            } catch (Exception e) {
                responseData = "";
                responseModel.setServermsg("No internet connection/cannot connect to server");
                response = "";
            }
        } else {
            try {
                HttpParams my_httpParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(my_httpParams, 180000);
                HttpConnectionParams.setSoTimeout(my_httpParams, 180000);
                HttpClient httpclient = new DefaultHttpClient(my_httpParams);;
                OwnHttpDelete httppost = new OwnHttpDelete(url);

                if (mHeadersData != null) {
                    Set mapSet = (Set) mHeadersData.entrySet();
                    Iterator mapIterator = mapSet.iterator();
                    while (mapIterator.hasNext()) {
                        Map.Entry mapEntry = (Map.Entry) mapIterator.next();
                        String keyValue = (String) mapEntry.getKey();
                        String value = (String) mapEntry.getValue();
                        httppost.addHeader(keyValue, value);
                    }
                }
                httppost.addHeader("Content-Type", "application/json");

                StringEntity params1 = new StringEntity(getPostDataString(params));
                httppost.setEntity(params1);


                //Execute and get the response.
                HttpResponse response1 = httpclient.execute(httppost);
                responseCode = response1.getStatusLine().getStatusCode();

                if (serverType.equalsIgnoreCase("Response")) {
                    HttpEntity entity = response1.getEntity();
                    String line;
                    if (entity != null) {
                        InputStream instream = entity.getContent();
                        try {
                            // do something useful
                            BufferedReader br = new BufferedReader(new InputStreamReader(instream));
                            while ((line = br.readLine()) != null) {
                                responseData += line;
                                response += line;
                            }
                            br.close();
                        } finally {
                            instream.close();
                        }
                    }
                }
                else
                {
                    if (responseCode == HttpURLConnection.HTTP_OK)  {
                        HttpEntity entity = response1.getEntity();
                        String line;
                        if (entity != null) {
                            InputStream instream = entity.getContent();
                            try {
                                // do something useful
                                BufferedReader br = new BufferedReader(new InputStreamReader(instream));
                                while ((line = br.readLine()) != null) {
                                    responseData += line;
                                    response += line;
                                }
                                br.close();
                            } finally {
                                instream.close();
                            }
                        }
                    } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                        responseData = "";
                        response = "session expired";
                    } else {
                        responseData = "";
                        response = "http_not_ok";
                    }
                }

                if (response1.getFirstHeader("StatusMessage") != null) {
                    responseModel.setServermsg(response1.getFirstHeader("StatusMessage").getValue());
                } else {
                    responseModel.setServermsg("success");
                }

            }
            catch (ConnectTimeoutException e) {
            }
            catch (SocketTimeoutException e) {
            }
            catch (Exception e) {
            }
        }
        if (responseCode == HttpURLConnection.HTTP_FORBIDDEN) {
            responseCode = HttpURLConnection.HTTP_UNAUTHORIZED;
        }
        responseModel.setResponseCode("" + responseCode);
        responseModel.setResponse(response);
        responseModel.setResponseData(responseData);
        return responseModel;
    }

    static Responsemodel makeDeleteRequestWithJson(String urlpath, JSONObject jsonObject, HashMap<String, String> mHeadersData, String serverType) {
        Responsemodel responseModel = new Responsemodel();
        String response = "";
        String responseData = "";
        int responseCode = 0;
        URL url1;
        if (Build.VERSION.SDK_INT >= 21) {
            try {
                url1 = new URL(urlpath);
                HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
                conn.setReadTimeout(180000);
                conn.setConnectTimeout(180000);
                conn.setRequestMethod("DELETE");
                conn.setDoInput(true);
                conn.setRequestProperty("Content-Type", "application/json");
                if (serverType.equalsIgnoreCase("WCP")) {
                    String encoding = Base64.encodeToString(basicAuth.getBytes(), Base64.DEFAULT);
                    conn.setRequestProperty("Authorization", "Basic " + encoding);
                }

                if (mHeadersData != null) {
                    Set mapSet = (Set) mHeadersData.entrySet();
                    Iterator mapIterator = mapSet.iterator();
                    while (mapIterator.hasNext()) {
                        Map.Entry mapEntry = (Map.Entry) mapIterator.next();
                        String keyValue = (String) mapEntry.getKey();
                        String value = (String) mapEntry.getValue();
                        conn.setRequestProperty(keyValue, value);
                    }
                }

                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(jsonObject.toString());

                writer.flush();
                writer.close();
                os.close();

                try {
                    // Will throw IOException if server responds with 401.
                    responseCode = conn.getResponseCode();
                } catch (IOException e) {
                    // Will return 401, because now connection has the correct internal state.
                    responseCode = conn.getResponseCode();
                }
                if (serverType.equalsIgnoreCase("Response")) {
                    String line;
                    BufferedReader br;
                    try {
                        br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    } catch (IOException e) {
                        br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    }
                    while ((line = br.readLine()) != null) {
                        response += line;
                        responseData += line;
                    }
                } else {
                    if (responseCode == HttpsURLConnection.HTTP_OK) {
                        String line;
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        while ((line = br.readLine()) != null) {
                            response += line;
                            responseData += line;
                        }
                    } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                        response = "session expired";
                        responseData = "";
                    } else {
                        responseData = "";
                        response = "http_not_ok";
                    }
                }
                if (conn.getHeaderField("StatusMessage") != null) {
                    responseModel.setServermsg(conn.getHeaderField("StatusMessage"));
                } else if (responseCode != HttpURLConnection.HTTP_OK && conn.getHeaderField("StatusMessage") == null) {
                    responseModel.setServermsg("server error");
                } else {
                    responseModel.setServermsg("success");
                }
            } catch (SocketTimeoutException e) {
                responseModel.setServermsg("No internet connection/cannot connect to server");
                responseData = "";
                response = "timeout";
            } catch (Exception e) {
                responseModel.setServermsg("No internet connection/cannot connect to server");
                responseData = "";
                response = "";
            }
        } else {
            try {
                HttpParams my_httpParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(my_httpParams, 180000);
                HttpConnectionParams.setSoTimeout(my_httpParams, 180000);
                HttpClient httpclient = new DefaultHttpClient(my_httpParams);;
                OwnHttpDelete httppost = new OwnHttpDelete(urlpath);

                if (mHeadersData != null) {
                    Set mapSet = (Set) mHeadersData.entrySet();
                    Iterator mapIterator = mapSet.iterator();
                    while (mapIterator.hasNext()) {
                        Map.Entry mapEntry = (Map.Entry) mapIterator.next();
                        String keyValue = (String) mapEntry.getKey();
                        String value = (String) mapEntry.getValue();
                        httppost.addHeader(keyValue, value);
                    }
                }
                httppost.addHeader("Content-Type", "application/json");
                StringEntity params1 = new StringEntity(jsonObject.toString());
                httppost.setEntity(params1);


                //Execute and get the response.
                HttpResponse response1 = httpclient.execute(httppost);
                responseCode = response1.getStatusLine().getStatusCode();

                if (serverType.equalsIgnoreCase("Response")) {
                    HttpEntity entity = response1.getEntity();
                    String line;
                    if (entity != null) {
                        InputStream instream = entity.getContent();
                        try {
                            // do something useful
                            BufferedReader br = new BufferedReader(new InputStreamReader(instream));
                            while ((line = br.readLine()) != null) {
                                responseData += line;
                                response += line;
                            }
                            br.close();
                        } finally {
                            instream.close();
                        }
                    }
                }
                else
                {
                    if (responseCode == HttpURLConnection.HTTP_OK)  {
                        HttpEntity entity = response1.getEntity();
                        String line;
                        if (entity != null) {
                            InputStream instream = entity.getContent();
                            try {
                                // do something useful
                                BufferedReader br = new BufferedReader(new InputStreamReader(instream));
                                while ((line = br.readLine()) != null) {
                                    responseData += line;
                                    response += line;
                                }
                                br.close();
                            } finally {
                                instream.close();
                            }
                        }
                    } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                        responseData = "";
                        response = "session expired";
                    } else {
                        responseData = "";
                        response = "http_not_ok";
                    }
                }

                if (response1.getFirstHeader("StatusMessage") != null) {
                    responseModel.setServermsg(response1.getFirstHeader("StatusMessage").getValue());
                } else {
                    responseModel.setServermsg("success");
                }

            }
            catch (ConnectTimeoutException e) {
            }
            catch (SocketTimeoutException e) {
            }
            catch (Exception e) {
            }
        }

        responseModel.setResponseCode("" + responseCode);
        responseModel.setResponse(response);
        responseModel.setResponseData(responseData);
        return responseModel;
    }

    static Responsemodel makeDeleteRequestWithJsonArray(String urlpath, JSONArray jsonArray, HashMap<String, String> mHeadersData, String serverType) {
        Responsemodel responseModel = new Responsemodel();
        String response = "";
        String responseData = "";
        int responseCode = 0;
        URL url1;
        if (Build.VERSION.SDK_INT >= 21) {
            try {
                url1 = new URL(urlpath);
                HttpURLConnection conn = (HttpURLConnection) url1.openConnection();
                conn.setReadTimeout(180000);
                conn.setConnectTimeout(180000);
                conn.setRequestMethod("DELETE");
                conn.setDoInput(true);
                conn.setRequestProperty("Content-Type", "application/json");
                if (serverType.equalsIgnoreCase("WCP")) {
                    String encoding = Base64.encodeToString(basicAuth.getBytes(), Base64.DEFAULT);
                    conn.setRequestProperty("Authorization", "Basic " + encoding);
                }

                if (mHeadersData != null) {
                    Set mapSet = (Set) mHeadersData.entrySet();
                    Iterator mapIterator = mapSet.iterator();
                    while (mapIterator.hasNext()) {
                        Map.Entry mapEntry = (Map.Entry) mapIterator.next();
                        String keyValue = (String) mapEntry.getKey();
                        String value = (String) mapEntry.getValue();
                        conn.setRequestProperty(keyValue, value);
                    }
                }

                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(jsonArray.toString());

                writer.flush();
                writer.close();
                os.close();

                try {
                    // Will throw IOException if server responds with 401.
                    responseCode = conn.getResponseCode();
                } catch (IOException e) {
                    // Will return 401, because now connection has the correct internal state.
                    responseCode = conn.getResponseCode();
                }
                if (serverType.equalsIgnoreCase("Response")) {
                    String line;
                    BufferedReader br;
                    try {
                        br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    } catch (IOException e) {
                        br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
                    }
                    while ((line = br.readLine()) != null) {
                        response += line;
                        responseData += line;
                    }
                } else {
                    if (responseCode == HttpsURLConnection.HTTP_OK) {
                        String line;
                        BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                        while ((line = br.readLine()) != null) {
                            response += line;
                            responseData += line;
                        }
                    } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                        response = "session expired";
                        responseData = "";
                    } else {
                        responseData = "";
                        response = "http_not_ok";
                    }
                }
                if (conn.getHeaderField("StatusMessage") != null) {
                    responseModel.setServermsg(conn.getHeaderField("StatusMessage"));
                } else if (responseCode != HttpURLConnection.HTTP_OK && conn.getHeaderField("StatusMessage") == null) {
                    responseModel.setServermsg("server error");
                } else {
                    responseModel.setServermsg("success");
                }
            } catch (SocketTimeoutException e) {
                responseModel.setServermsg("No internet connection/cannot connect to server");
                responseData = "";
                response = "timeout";
            } catch (Exception e) {
                responseModel.setServermsg("No internet connection/cannot connect to server");
                responseData = "";
                response = "";
            }
        } else {
            try {
                HttpParams my_httpParams = new BasicHttpParams();
                HttpConnectionParams.setConnectionTimeout(my_httpParams, 180000);
                HttpConnectionParams.setSoTimeout(my_httpParams, 180000);
                HttpClient httpclient = new DefaultHttpClient(my_httpParams);;
                OwnHttpDelete httppost = new OwnHttpDelete(urlpath);

                if (mHeadersData != null) {
                    Set mapSet = (Set) mHeadersData.entrySet();
                    Iterator mapIterator = mapSet.iterator();
                    while (mapIterator.hasNext()) {
                        Map.Entry mapEntry = (Map.Entry) mapIterator.next();
                        String keyValue = (String) mapEntry.getKey();
                        String value = (String) mapEntry.getValue();
                        httppost.addHeader(keyValue, value);
                    }
                }
                httppost.addHeader("Content-Type", "application/json");
                StringEntity params1 = new StringEntity(jsonArray.toString());
                httppost.setEntity(params1);


                //Execute and get the response.
                HttpResponse response1 = httpclient.execute(httppost);
                responseCode = response1.getStatusLine().getStatusCode();

                if (serverType.equalsIgnoreCase("Response")) {
                    HttpEntity entity = response1.getEntity();
                    String line;
                    if (entity != null) {
                        InputStream instream = entity.getContent();
                        try {
                            // do something useful
                            BufferedReader br = new BufferedReader(new InputStreamReader(instream));
                            while ((line = br.readLine()) != null) {
                                responseData += line;
                                response += line;
                            }
                            br.close();
                        } finally {
                            instream.close();
                        }
                    }
                }
                else
                {
                    if (responseCode == HttpURLConnection.HTTP_OK)  {
                        HttpEntity entity = response1.getEntity();
                        String line;
                        if (entity != null) {
                            InputStream instream = entity.getContent();
                            try {
                                // do something useful
                                BufferedReader br = new BufferedReader(new InputStreamReader(instream));
                                while ((line = br.readLine()) != null) {
                                    responseData += line;
                                    response += line;
                                }
                                br.close();
                            } finally {
                                instream.close();
                            }
                        }
                    } else if (responseCode == HttpURLConnection.HTTP_UNAUTHORIZED) {
                        responseData = "";
                        response = "session expired";
                    } else {
                        responseData = "";
                        response = "http_not_ok";
                    }
                }

                if (response1.getFirstHeader("StatusMessage") != null) {
                    responseModel.setServermsg(response1.getFirstHeader("StatusMessage").getValue());
                } else {
                    responseModel.setServermsg("success");
                }

            }
            catch (ConnectTimeoutException e) {
            }
            catch (SocketTimeoutException e) {
            }
            catch (Exception e) {
            }
        }

        responseModel.setResponseCode("" + responseCode);
        responseModel.setResponse(response);
        responseModel.setResponseData(responseData);
        return responseModel;
    }

    private static class OwnHttpDelete extends HttpPost {
        public static final String METHOD_NAME = "DELETE";

        public OwnHttpDelete() {
            super();
        }

        public OwnHttpDelete(URI uri) {
            super(uri);
        }

        public OwnHttpDelete(String uri) {
            super(uri);
        }

        public String getMethod() {
            return METHOD_NAME;
        }
    }

}
