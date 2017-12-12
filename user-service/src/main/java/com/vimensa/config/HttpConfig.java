package com.vimensa.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class HttpConfig {
    public static String HTTP_CONFIG_FILE = "commonconfig/http.properties";
    public static String URL = "";

    public static void init() throws IOException {
        Properties prop = new Properties();
        InputStream input = new FileInputStream(HTTP_CONFIG_FILE);
        prop.load(input);
        URL = prop.getProperty("url");
    }
}
