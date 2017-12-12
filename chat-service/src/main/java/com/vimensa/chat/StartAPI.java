package com.vimensa.chat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "com.vimensa")
public class StartAPI {
    private static Logger logger = LoggerFactory.getLogger(StartAPI.class);
    public static void main(String[] args) {
        SpringApplication.run(StartAPI.class,args);
        logger.info("spring boot has started.");
    }
}
