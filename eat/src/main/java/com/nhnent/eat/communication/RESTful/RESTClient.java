package com.nhnent.eat.communication.RESTful;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.nhnent.eat.entity.ScenarioUnit;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.nhnent.eat.common.CommonDefine.EmptyString;
import static com.nhnent.eat.common.JsonUtil.getValueOfVar;


/**
 *  REST Client to call REST execute and response.
 */
public class RESTClient {


    private final Logger logger = LoggerFactory.getLogger(getClass());


    /**
     * Call REST requests
     *
     * @param scenarioUnit scenarioUnit which contains REST data
     * @return REST response
     */
    public String requestRestCall(ScenarioUnit scenarioUnit) {

        String method;
        String url;
        String jsonBody;
        String resultJson;

        JsonParser jp = new JsonParser();
        JsonElement je = jp.parse(scenarioUnit.json);

        JsonElement jeMethod = getValueOfVar(je, "Method");
        if (jeMethod != null) {
            method = jeMethod.getAsString();
        } else {
            method = EmptyString;
        }

        JsonElement jeURL = getValueOfVar(je, "Url");
        if (jeURL != null) {
            url = jeURL.getAsString();
        } else {
            url = EmptyString;
        }

        if (method.equals("GET")) {
            resultJson = this.get(url);

            logger.info("<-------RESTful Request [GET]------->");
            logger.info("Url : " + url);

            return resultJson;
        } else if (method.equals("post")) {
            JsonElement jeBody = getValueOfVar(je, "Body");
            if (jeBody != null) {
                jsonBody = jeBody.toString();
            } else {
                jsonBody = EmptyString;
            }

            logger.info("<-------RESTful Request [post]------->");
            logger.info("Url : " + url);
            logger.info("jsonBody : " + jsonBody);

            resultJson = this.post(url, jsonBody);

            return resultJson;
        }


        return null;
    }

    /**
     * REST GET Request
     *
     * @param requestURL execute url
     * @return response
     */
    private String get(String requestURL) {

        if (requestURL == null) return null;

        StringBuilder resultJson = new StringBuilder();

        try {
            URL url = new URL(requestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            // 200 == OK
            BufferedReader br;
            String output;
            if (conn.getResponseCode() != 200) {
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            } else {

                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            }


            while ((output = br.readLine()) != null) {
                resultJson.append(output);
            }

            conn.disconnect();

        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }


        return resultJson.toString();
    }


    /**
     * REST post Request
     *
     * @param requestURL execute url
     * @param jsonBody   execute body
     * @return response
     */
    public String post(String requestURL, String jsonBody) {
        if (requestURL == null) return null;

        StringBuilder resultJson = new StringBuilder();

        URL url;
        HttpURLConnection conn;

        try {
            url = new URL(requestURL);
            conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("post");
            conn.setRequestProperty("Content-Type", "application/json");

            conn.setDoOutput(true);

            OutputStream os = conn.getOutputStream();
            os.write(jsonBody.getBytes());
            os.flush();


            BufferedReader br;
            String output;

            if (conn.getResponseCode() != 200) {
                br = new BufferedReader(new InputStreamReader(conn.getErrorStream()));
            } else {

                br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            }

            while ((output = br.readLine()) != null) {
                resultJson.append(output);
            }

            conn.disconnect();
        } catch (IOException e) {

            logger.error(ExceptionUtils.getStackTrace(e));
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
        }


        return resultJson.toString();
    }


    public RESTClient() {

    }
}
