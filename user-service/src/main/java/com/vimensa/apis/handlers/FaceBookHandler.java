package com.vimensa.apis.handlers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.vimensa.apis.responses.SimpleResponse;
import com.vimensa.apis.responses._BaseResponse;
import com.vimensa.config.ErrorCode;
import com.vimensa.core.dao.LoginSession;
import com.vimensa.core.dao.UserData;
import com.vimensa.core.service.UserService;
import com.vimensa.core.serviceimp.UserServiceImp;
import com.vimensa.core.utils.Utils;
import io.vertx.core.http.HttpServerRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FaceBookHandler extends BaseApiHandler {
    private static Logger logger = LoggerFactory.getLogger(FaceBookHandler.class.getName());
    private UserService userService = new UserServiceImp();
    @Override
    public _BaseResponse handle(HttpServerRequest request) throws Exception {
//        _BaseResponse response = null;
//        String access_token = null, social_type = null, app_id = null;
//        access_token = request.getFormAttribute("access_token").trim();
//        social_type = request.getFormAttribute("st").trim();
//        app_id = request.getFormAttribute("appid").trim();
//
//
//        if (social_type != null && access_token != null && app_id !=null && social_type.length() > 0 && access_token.length() > 0 && app_id.length() >0) {
//            UserData userData = Utils.getInfoUserFacebook(access_token);
//            LoginSession loginSession = Utils.createAccessToken(app_id);
//            response = userService.registerSocial(userData,loginSession);
//        } else {
//            response = new SimpleResponse(ErrorCode.INVALID_PARAMS);
//        }

        return null;
    }

    @Override
    public _BaseResponse hanler(HttpServerRequest request, String body) throws Exception {
        _BaseResponse response = null;
        JsonObject object = (JsonObject) new JsonParser().parse(body);
        String access_token = object.get("access_token").getAsString();
        String social_type = object.get("st").getAsString();
        String app_id = object.get("appid").getAsString();
        logger.debug("{} login by Facebook",access_token);

        if (social_type != null && access_token != null && app_id !=null && social_type.length() > 0 && access_token.length() > 0 && app_id.length() >0) {
            UserData userData = Utils.getInfoUserFacebook(access_token);
            LoginSession loginSession = Utils.createAccessToken(app_id);
            response = userService.registerSocial(userData,loginSession);
        } else {
            response = new SimpleResponse(ErrorCode.INVALID_PARAMS);
        }
        logger.debug("Response login by Facebook : \n{}",response);

        return response;
    }
}
