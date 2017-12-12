package com.vimensa.apis.handlers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vimensa.apis.responses._BaseResponse;
import com.vimensa.core.dao.LoginSession;
import com.vimensa.core.dao.UserData;
import com.vimensa.core.service.UserService;
import com.vimensa.core.serviceimp.UserServiceImp;
import io.vertx.core.http.HttpServerRequest;

public class UpdateUserHandler extends BaseApiHandler {
    private UserService userService = new UserServiceImp();
    @Override
    public _BaseResponse handle(HttpServerRequest request) throws Exception {
        return null;
    }
    @Override
    public _BaseResponse hanler(HttpServerRequest request, String body) throws Exception {
        _BaseResponse response = null;
        UserData userData = new UserData();
        LoginSession loginSession = new LoginSession();
        JsonObject object = (JsonObject) new JsonParser().parse(body);
        loginSession.setAccessToken(request.getHeader("at"));
        loginSession.setAppId(object.get("appid").getAsString());
        userData.setDescription(object.get("desc").getAsString());
        userData.setUsername(object.get("un").getAsString());
        userData.setRole(object.get("roles").getAsInt());
        userData.setLinkFB(object.get("linkfb").getAsString());
        userData.setLinkIns(object.get("linkins").getAsString());
        userData.setLinkWS(object.get("linkwb").getAsString());

        response = userService.updateUserInfo(userData,loginSession);

        return response;
    }
}
