package com.vimensa.apis;

import com.vimensa.apis.vertx.VertxHttpServer;
import com.vimensa.config.APIConfig;
import com.vimensa.config.EmailConfig;
import com.vimensa.config.HttpConfig;
import com.vimensa.core.pools.HikariPool;
import com.vimensa.core.pools.MongoPool;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class APILauncher {
    private static Logger logger = LoggerFactory.getLogger(APILauncher.class.getName());
    public static String sessionID = "";


    public static void main(String[] args) {
        runVertx();
    }

    public static void runVertx() {
        try {
            PropertyConfigurator.configure("commonconfig/log4j.properties");
            logger.info("Logger is initialed");
//            TaskScheduler.instance();
            APIConfig.init();
            logger.info("APIConfig is initialed");
//            HazelcastClientFactory.start();
//            logger.info("HazelcastClientFactory is initialed");
            HikariPool.init();
            logger.info("HikariPool is initialed");
            HttpConfig.init();
            EmailConfig.init();
            MongoPool.init();
//            logger.info("MongoPool is initialed");
//            LogTask.init();
            int procs = Runtime.getRuntime().availableProcessors();

            Vertx vertx = Vertx.vertx();
            vertx.deployVerticle(VertxHttpServer.class.getName(),
                    new DeploymentOptions().setInstances(procs * 2), event -> {
                        if (event.succeeded()) {
                            logger.debug("Your Vert.x application is started!");
//                            System.out.println("Your Vert.x application is started!");
                        } else {
                            logger.error("Unable to start your application", event.cause());
//                            System.out.println("Unable to start your application"+ event.cause());
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Error:"+e.toString());
        }
    }
}
