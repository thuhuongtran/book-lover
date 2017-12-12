package com.vimensa.test;

import io.vertx.core.*;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;

public class TestUltil extends AbstractVerticle implements Handler<HttpServerRequest>{
    HttpServer httpServer;
    @Override
    public void start(Future<Void> startFuture) throws Exception {
        super.start(startFuture);
        httpServer = vertx.createHttpServer();
        httpServer.requestHandler(this);
        httpServer.listen(8080);
    }

    public static void main(String []arg) {
        int procs = Runtime.getRuntime().availableProcessors();
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(TestUltil.class.getName(),
                new DeploymentOptions().setInstances(procs * 2), event -> {
                    if (event.succeeded()) {
//                            logger.debug("Your Vert.x application is started!");
                        System.out.println("Your Vert.x application is started!");
                    } else {
//                            logger.error("Unable to start your application", event.cause());
                        System.out.println("Unable to start your application"+ event.cause());
                    }
                });
    }

    @Override
    public void handle(HttpServerRequest request) {
        System.out.println("incoming request!");

        Buffer fullRequestBody = Buffer.buffer();
        if(request.method() == HttpMethod.POST){

            request.handler(new Handler<Buffer>() {
                @Override
                public void handle(Buffer buffer) {
                    fullRequestBody.appendBuffer(buffer);
                }
            });
            request.endHandler(new Handler<Void>() {
                @Override
                public void handle(Void aVoid) {
                    System.out.println(fullRequestBody.toString());
                }
            });
        }
    }
}
