package com.vimensa.core.utils;

import com.google.gson.JsonObject;
import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerRequest;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtils {
    public static String requestJson(String sUrl) throws IOException {
        StringBuilder res = new StringBuilder();
        URL url = new URL(sUrl);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");
        if (conn.getResponseCode() != 200) {
            return res.toString();
        }
        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        String output;
        while ((output = br.readLine()) != null) {
            res.append(output);
        }
        conn.disconnect();
        return res.toString();
    }
    public static String httpGetRequest(String url){
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet request = new HttpGet(url);
        try {
            request.addHeader("content-type", "application/x-www-form-urlencoded");
            CloseableHttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();
            String json = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
            return json;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
    public static String httpPostRequest(String url,String token, JsonObject params){
        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpPost request = new HttpPost(url);
        try {
            StringEntity requestEntity = new StringEntity(params.toString());
            request.addHeader("content-type", "application/x-www-form-urlencoded");
            request.addHeader("at", token);
            request.setEntity(requestEntity);
            CloseableHttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();
            String json = EntityUtils.toString(entity);
            EntityUtils.consume(entity);
            return json;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getPostParams(HttpServerRequest request){
        MultiMap form = request.formAttributes();
        return "";
    }
}
