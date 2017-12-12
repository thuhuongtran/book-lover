package com.vimensa.apis.handlers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vimensa.apis.responses._BaseResponse;
import com.vimensa.core.dao.LoginSession;
import com.vimensa.core.dao.UserData;
import com.vimensa.core.service.UserService;
import com.vimensa.core.serviceimp.UserServiceImp;
import com.vimensa.core.utils.Utils;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class LoginHandler extends BaseApiHandler {
    private static Logger logger = LoggerFactory.getLogger(LoginHandler.class.getName());
    private UserService userService = new UserServiceImp();

    @Override
    public _BaseResponse handle(HttpServerRequest request) throws Exception {
        String email = request.getFormAttribute("e");
        String password = request.getFormAttribute("p");
        String appId = request.getFormAttribute("ai");
        password = Utils.sha256(password);
        UserData userData = new UserData();
        userData.setEmail(email);
        userData.setPassword(password);
        LoginSession loginSession = new LoginSession();
        loginSession.setAppId(appId);
        _BaseResponse response = userService.login(userData, loginSession);
        return response;
    }

    @Override
    public _BaseResponse hanler(HttpServerRequest request, String body) throws Exception {
        JsonObject object;
        UserData userData = new UserData();
        LoginSession loginSession = null;
        _BaseResponse response;
        try {
            object = (JsonObject) new JsonParser().parse(body);
            userData = new UserData();
            userData.setEmail(object.get("email").getAsString());
            String password = object.get("password").getAsString();
            password = Utils.sha256(password);
            userData.setPassword(password);
            loginSession = new LoginSession();
            loginSession.setAppId(object.get("appid").getAsString());
        } catch (Exception e) {
            userData=new UserData();
        }
        response = userService.login(userData, loginSession);
        return response;
    }
}
