package com.vimensa.apis.handlers;

import com.vimensa.apis.responses.SimpleResponse;
import com.vimensa.apis.responses._BaseResponse;
import com.vimensa.config.ErrorCode;
import com.vimensa.core.serviceimp.UserServiceImp;
import io.vertx.core.http.HttpServerRequest;

public class ResetPasswordHandler extends BaseApiHandler {
    @Override
    public _BaseResponse handle(HttpServerRequest request) throws Exception {
        _BaseResponse response=null;
        UserServiceImp userServiceImp=new UserServiceImp();
        try{
            String token=request.getParam("token");
            response=userServiceImp.resetPassword(token);
        }catch (Exception e){
            response=new SimpleResponse();
            response.setError(ErrorCode.INVALID_PARAMS);
        }
        return response;
    }

    @Override
    public _BaseResponse hanler(HttpServerRequest request, String body) throws Exception {
        return null;
    }
}
