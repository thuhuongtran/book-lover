package com.vimensa.chat.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class UserServiceConnect {
    private static final String HIRAKI_CONFIG_FILE = "config/userSQL.properties";
    private static HikariDataSource dataSource;
    public static void init() throws IOException {
        InputStream input = new FileInputStream(HIRAKI_CONFIG_FILE);
        Properties prop = new Properties();
        prop.load(input);
        HikariConfig config = new HikariConfig(prop);
        config.setMaximumPoolSize(20);
        dataSource = new HikariDataSource(config);
    }
    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
