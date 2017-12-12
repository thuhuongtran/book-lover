package com.vimensa.chat.security;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vimensa.chat.config.HttpConfig;
import com.vimensa.chat.model.Authen;
import com.vimensa.chat.model.ErrorCode;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;


public class Authentication {
    public static Authen checkToken(String token) {

        CloseableHttpClient httpclient = HttpClients.createDefault();
        HttpGet request = new HttpGet(HttpConfig.AUTHEN_URL);
        Authen at = null;
        try {
            request.addHeader("content-type", "application/x-www-form-urlencoded");
            request.addHeader("at", token);
            request.addHeader("ai", "bl");
            CloseableHttpResponse response = httpclient.execute(request);
            HttpEntity entity = response.getEntity();
            String json = EntityUtils.toString(entity,"UTF-8");
            EntityUtils.consume(entity);
            JsonObject result = (JsonObject) new JsonParser().parse(json);
            int status = result.get("e").getAsInt();
            at = new Authen();
            if (status == ErrorCode.ACTIVE_TOKEN) {
                at.setUserId(result.get("id").getAsInt());
                //at.setUsername(result.get("un").getAsString());
                at.setStatus(ErrorCode.ACTIVE_TOKEN);
            } else if (status == ErrorCode.UPDATE_TOKEN) {
                at.setUserId(result.get("id").getAsInt());
                at.setToken(result.get("at").getAsString());
                //at.setUsername(result.get("un").getAsString());
                at.setStatus(ErrorCode.UPDATE_TOKEN);
            } else if (status == ErrorCode.INVALID_TOKEN) {
                at.setStatus(ErrorCode.INVALID_TOKEN);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return at;
    }
}
