package com.vimensa.apis.handlers;

import com.vimensa.apis.responses.SimpleResponse;
import com.vimensa.apis.responses._BaseResponse;
import com.vimensa.config.ErrorCode;
import com.vimensa.core.dao.LoginSession;
import com.vimensa.core.service.UserService;
import com.vimensa.core.serviceimp.UserServiceImp;
import io.vertx.core.http.HttpServerRequest;

public class UserInfoHandler extends BaseApiHandler {
    private UserService userService = new UserServiceImp();
    @Override
    public _BaseResponse handle(HttpServerRequest request) throws Exception {
        _BaseResponse response = null;
        String access_token = null,app_id = null;
        int uid =  Integer.parseInt(request.getParam("uid").trim());
        access_token = request.getHeader("at");
        app_id = request.getParam("appid").trim();


        if (access_token != null && app_id !=null && access_token.length() > 0 && app_id.length() >0) {
            LoginSession loginSession = new LoginSession();
            loginSession.setAccessToken(access_token);
            loginSession.setAppId(app_id);
            response = userService.getUserInfo(loginSession,uid);
        } else {
            response = new SimpleResponse(ErrorCode.INVALID_PARAMS);
        }

        return response;
    }

    @Override
    public _BaseResponse hanler(HttpServerRequest request, String body) throws Exception {
        return null;
    }
}
