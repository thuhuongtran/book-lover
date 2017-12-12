package com.vimensa.apis.handlers;

import com.vimensa.apis.responses._BaseResponse;
import com.vimensa.core.dao.LoginSession;
import com.vimensa.core.service.LoginSessionService;
import com.vimensa.core.serviceimp.LoginSessionServiceImp;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class CheckAccessTokenHandler extends BaseApiHandler {
    private static Logger logger = LoggerFactory.getLogger(LoginHandler.class.getName());
    private LoginSessionService loginSessionService=new LoginSessionServiceImp();
    @Override
    public _BaseResponse handle(HttpServerRequest request) throws Exception {
        String accessToken=request.getHeader("at");
        String ai=request.getHeader("ai");
        LoginSession loginSession=new LoginSession();
        loginSession.setAccessToken(accessToken);
        loginSession.setAppId(ai);
        _BaseResponse response=loginSessionService.checkToken(loginSession);
        return response;
    }


    @Override
    public _BaseResponse hanler(HttpServerRequest request, String body) throws Exception {
        return null;
    }
}
