package com.vimensa.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vimensa.apis.handlers.BaseApiHandler;
import com.vimensa.core.utils.Utils;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class APIConfig {
    public static int PORT = 0;
    public static String MODE = "live";
    private static String API_CONFIG_FILE = "commonconfig/apis.json";
    private static Map<String, BaseApiHandler> handlers = new HashMap<String, BaseApiHandler>();
    public static BaseApiHandler getHandler(String path){
        return handlers.get(path);
    }
    public static JsonObject config;
    public static void init() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        initJson();
        PORT = config.get("port").getAsInt();
        MODE = config.get("mode").getAsString();
        JsonArray arr = config.getAsJsonArray("api");
        for(int i = 0; i < arr.size(); i++){
            JsonObject obj = arr.get(i).getAsJsonObject();
            String path = obj.get("path").getAsString();
            String handlerClassPath = obj.get("handler").getAsString();
            boolean isPublic = obj.get("isPublic").getAsBoolean();
            JsonArray roleArray = obj.get("roles").getAsJsonArray();
            String method = obj.get("method").getAsString();
            BaseApiHandler handler = (BaseApiHandler) Class.forName(handlerClassPath).newInstance();
            handler.setPath(path);
            handler.setPublic(isPublic);
            handler.initRoles(roleArray);
            handler.setMethod(method);
            handlers.put(handler.getPath(), handler);
        }
    }

    private static void initJson(){
        File file = new File(API_CONFIG_FILE);
        StringBuffer contents = new StringBuffer();
        BufferedReader reader = null;
        try {
            Reader r = new InputStreamReader( new FileInputStream(file),"UTF-8");
            reader = new BufferedReader(r);
            String text = null;
            while ((text = reader.readLine()) != null) {
                contents.append(text).append(System.getProperty("line.separator"));
            }
            config = Utils.toJsonObject(contents.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
