package com.vimensa.apis.handlers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vimensa.apis.responses._BaseResponse;
import com.vimensa.core.dao.UserData;
import com.vimensa.core.service.UserService;
import com.vimensa.core.serviceimp.UserServiceImp;
import com.vimensa.core.utils.Utils;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

public class RegisterHandler extends BaseApiHandler {
    private static Logger logger = LoggerFactory.getLogger(LoginHandler.class.getName());
    private UserService userService = new UserServiceImp();
    @Override
    public _BaseResponse handle(HttpServerRequest request) throws Exception {
        String username=request.getFormAttribute("un");
        String email = request.getFormAttribute("e");
        String password = request.getFormAttribute("p");
        password = Utils.sha256(password);
        UserData userData = new UserData();
        userData.setUsername(username);
        userData.setEmail(email);
        userData.setPassword(password);
        _BaseResponse response = userService.register(userData);
        return response;
    }

    @Override
    public _BaseResponse hanler(HttpServerRequest request, String body) throws Exception {
        JsonObject object;
        UserData userData = new UserData();
        _BaseResponse response;
        try {
            object = (JsonObject) new JsonParser().parse(body);
            userData = new UserData();
            userData.setUsername(object.get("un").getAsString());
            userData.setEmail(object.get("email").getAsString());
            String password = object.get("password").getAsString();
            password = Utils.sha256(password);
            userData.setPassword(password);
        } catch (Exception e) {
            userData=null;
        } finally {
            response = userService.register(userData);
        }
        return response;
    }
}
