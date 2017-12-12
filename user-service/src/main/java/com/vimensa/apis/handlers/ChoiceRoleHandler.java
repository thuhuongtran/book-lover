package com.vimensa.apis.handlers;

import com.vimensa.apis.responses.SimpleResponse;
import com.vimensa.apis.responses._BaseResponse;
import com.vimensa.config.ErrorCode;
import com.vimensa.core.serviceimp.UserServiceImp;
import io.vertx.core.http.HttpServerRequest;

public class ChoiceRoleHandler extends BaseApiHandler {
    @Override
    public _BaseResponse handle(HttpServerRequest request) throws Exception {
        SimpleResponse response=null;
        if(request.getParam("role").equals("1") || request.getParam("role").equals("2") || request.getParam("role").equals("3")){
            UserServiceImp imp=new UserServiceImp();
            response=imp.choiceRole(request.getHeader("at"),Integer.parseInt(request.getParam("role")));
        }else{
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
