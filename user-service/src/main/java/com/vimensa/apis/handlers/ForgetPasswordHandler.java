package com.vimensa.apis.handlers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vimensa.apis.responses.SimpleResponse;
import com.vimensa.apis.responses._BaseResponse;
import com.vimensa.config.ErrorCode;
import com.vimensa.core.serviceimp.UserServiceImp;
import io.vertx.core.http.HttpServerRequest;

public class ForgetPasswordHandler extends BaseApiHandler {
    @Override
    public _BaseResponse handle(HttpServerRequest request) throws Exception {
        return null;
    }

    @Override
    public _BaseResponse hanler(HttpServerRequest request, String body) throws Exception {
        UserServiceImp userServiceImp = new UserServiceImp();
        _BaseResponse response = null;
        try {
            JsonObject object = (JsonObject) new JsonParser().parse(body);
            String email = object.get("email").getAsString();
            response=userServiceImp.forgetPassword(email);
        } catch (Exception e) {
            response = new SimpleResponse();
            response.setError(ErrorCode.INVALID_PARAMS);
        }
        return response;
    }
}
