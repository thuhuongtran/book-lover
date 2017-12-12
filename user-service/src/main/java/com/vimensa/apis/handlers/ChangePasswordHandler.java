package com.vimensa.apis.handlers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vimensa.apis.responses.SimpleResponse;
import com.vimensa.apis.responses._BaseResponse;
import com.vimensa.config.ErrorCode;
import com.vimensa.core.serviceimp.UserServiceImp;
import io.vertx.core.http.HttpServerRequest;

public class ChangePasswordHandler extends BaseApiHandler {
    @Override
    public _BaseResponse handle(HttpServerRequest request) throws Exception {
        return null;
    }

    @Override
    public _BaseResponse hanler(HttpServerRequest request, String body) throws Exception {
        _BaseResponse response=new SimpleResponse();
        UserServiceImp userServiceImp=new UserServiceImp();
        try{
            JsonObject object= (JsonObject) new JsonParser().parse(body);
            String email=object.get("email").getAsString();
            String old_password=object.get("old_password").getAsString();
            String new_password=object.get("new_password").getAsString();
            response=userServiceImp.changePassword(email,old_password,new_password);
        }catch (Exception e){
            response.setError(ErrorCode.INVALID_PARAMS);
        }
        return response;
    }
}
