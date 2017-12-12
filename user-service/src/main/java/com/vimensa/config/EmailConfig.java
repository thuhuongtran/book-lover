package com.vimensa.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class EmailConfig {
    public static String EMAIL_CONFIG_FILE = "commonconfig/email.properties";
    public static String EMAIL = "";
    public static String PASSWORD = "";
    public static String EMAIL_NAME = "";

    public static void init() throws IOException {
        Properties prop = new Properties();
        InputStream input = new FileInputStream(EMAIL_CONFIG_FILE);
        prop.load(input);
        EMAIL = prop.getProperty("email");
        PASSWORD = prop.getProperty("password");
        EMAIL_NAME = prop.getProperty("email_name");
    }
}
