package com.vimensa.apis.vertx;

import com.vimensa.apis.handlers.BaseApiHandler;
import com.vimensa.apis.responses.SimpleResponse;
import com.vimensa.apis.responses._BaseResponse;
import com.vimensa.config.APIConfig;
import com.vimensa.config.ErrorCode;
import io.netty.util.AsciiString;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;

public class VertxHttpServer extends AbstractVerticle implements Handler<HttpServerRequest> {
    static Logger logger = LoggerFactory.getLogger(VertxHttpServer.class.getName());
    private HttpServer server;
//    private static final CharSequence RESPONSE_TYPE_JSON = new AsciiString("application/json");
    private static final CharSequence RESPONSE_TYPE_UTF8=new AsciiString("application/json;charset=UTF-8");
    @Override
    public void start() {
        int port = APIConfig.PORT;
        server = vertx.createHttpServer();
        server.requestHandler(VertxHttpServer.this).listen(port);
        logger.debug("start on port {}", port);
    }

    @Override
    public void handle(HttpServerRequest request) {
        _BaseResponse response = null;
        BaseApiHandler handler = APIConfig.getHandler(request.path());
        try {
            if (handler != null) {
                switch (handler.getMethod()) {
                    case "GET":
                        handleGet(handler, request);
                        break;
                    case "POST":
                        handlePOST(handler, request);
                        break;
                    case "PUT":
                        handlePOST(handler, request);
                        break;
                }
            } else {
                response = new SimpleResponse(ErrorCode.HANDLER_NOT_FOUND);
                makeHttpResponse(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            response = new SimpleResponse(ErrorCode.SYSTEM_ERROR);
            makeHttpResponse(request, response);
        }
    }

    private void handleGet(BaseApiHandler handler, HttpServerRequest request) throws Exception {
        _BaseResponse response = null;
        if (handler.isPublic()) {
            response = handler.handle(request);
        } else {
            String nickname = request.getParam("n");
            String accessToken = request.getParam("at");
            response = handlePrivateRequest(handler, request, nickname, accessToken);
        }
        makeHttpResponse(request, response);
    }

    private void handlePOST(BaseApiHandler handler, HttpServerRequest request) throws Exception {
        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        request.setExpectMultipart(true);
        final String[] body = {""};
        Buffer fullRequestBody = Buffer.buffer();
        request.handler(new Handler<Buffer>() {
            @Override
            public void handle(Buffer buffer) {
                fullRequestBody.appendBuffer(buffer);
            }
        });
        request.endHandler(req -> {
            _BaseResponse response = null;
            body[0]=fullRequestBody.toString();
            try {
                if (handler.isPublic()) {
//                    response = handler.handle(request);
                    response = handler.hanler(request, body[0]);
                } else {
                    String nickname = request.formAttributes().get("n");
                    String accessToken = request.formAttributes().get("at");
                    response = handlePrivateRequest(handler, request, nickname, accessToken);
                }
            } catch (Exception e) {
                e.printStackTrace();
                response = new SimpleResponse(ErrorCode.SYSTEM_ERROR);
            }
            makeHttpResponse(request, response);
        });
    }



    private _BaseResponse handlePrivateRequest(BaseApiHandler handler, HttpServerRequest request, String nickname, String accessToken) throws Exception {
        _BaseResponse response = null;
        if (nickname != null && accessToken != null) {
            boolean securityCheck = handler.filterSecutiry(nickname, accessToken);
            if (securityCheck) {
                response = handler.handle(request);
            } else {
                response = new SimpleResponse(ErrorCode.NOT_AUTHORISED);
            }
        } else {
            response = new SimpleResponse(ErrorCode.INVALID_PARAMS);
        }
        return response;
    }

    public void allowAccessControlOrigin(HttpServerRequest request) {
        request.response().putHeader("Access-Control-Allow-Origin", "*");
        request.response().putHeader("Access-Control-Allow-Credentials", "true");
        request.response().putHeader("Access-Control-Allow-Methods", "POST, GET, HEAD, OPTIONS");
        request.response().putHeader("Access-Control-Allow-Headers",
                "Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers");
    }

    private void makeHttpResponse(HttpServerRequest request, _BaseResponse response) {
        allowAccessControlOrigin(request);
        String content = response.toJonString();
        CharSequence contentLength = new AsciiString(String.valueOf(content.length()));
        Buffer contentBuffer = Buffer.buffer(content);
        request.response().putHeader("Content-Type",RESPONSE_TYPE_UTF8)
                .putHeader("CONTENT_LENGTH", contentLength).end(contentBuffer);
    }

    @Override
    public void stop() {
        if (server != null) server.close();
    }
}
