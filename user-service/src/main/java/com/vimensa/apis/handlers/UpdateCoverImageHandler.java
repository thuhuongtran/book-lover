package com.vimensa.apis.handlers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vimensa.apis.responses._BaseResponse;
import com.vimensa.config.ErrorCode;
import com.vimensa.core.dao.LoginSession;
import com.vimensa.core.service.UserService;
import com.vimensa.core.serviceimp.UserServiceImp;
import io.vertx.core.http.HttpServerRequest;

public class UpdateCoverImageHandler extends BaseApiHandler {
    @Override
    public _BaseResponse handle(HttpServerRequest request) throws Exception {
        return null;
    }

    @Override
    public _BaseResponse hanler(HttpServerRequest request, String body) throws Exception {
        UserService userService = new UserServiceImp();
        _BaseResponse response = null;
        LoginSession loginSession = new LoginSession();
        JsonObject object = (JsonObject) new JsonParser().parse(body);
        loginSession.setAccessToken(request.getHeader("at"));
        loginSession.setAppId(object.get("appid").getAsString());
        String coverimg = object.get("cimg").getAsString();

        if(coverimg!=null && coverimg.length()> 0 && loginSession.getAccessToken()!=null && loginSession.getAccessToken().length()>0 && loginSession.getAppId()!=null && loginSession.getAppId().length()>0){
            response = userService.updateCoverImage(coverimg,loginSession);
        }else{
            response.setError(ErrorCode.INVALID_PARAMS);
        }
        return response;
    }
}
