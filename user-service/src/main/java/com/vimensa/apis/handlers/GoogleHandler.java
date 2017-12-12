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

public class GoogleHandler extends BaseApiHandler {
    private static Logger logger = LoggerFactory.getLogger(GoogleHandler.class.getName());
    private UserService userService = new UserServiceImp();
    @Override
    public _BaseResponse handle(HttpServerRequest request) throws Exception {
//        _BaseResponse response = null;
//        String avatar = null;
//        System.out.println("path: "+request.path().toString());
//        String username = request.getFormAttribute("un").trim();
//        String email = request.getFormAttribute("email").trim();
//        avatar = request.getFormAttribute("avatar").trim();
//        String socialid = request.getFormAttribute("sid").trim();
//        String socialtype = request.getFormAttribute("st").trim();
//        String app_id = request.getFormAttribute("appid").trim();
//		//todo
//
//        if (username != null && email !=null && socialid!=null && socialtype!=null && app_id!=null
//                && username.length() > 0 && email.length() > 0 && socialid.length() > 0 && socialtype.length() > 0 && app_id.length() >0) {
//            UserData userData = new UserData();
//            userData.setUsername(username);
//            userData.setEmail(email);
//            userData.setAvatar(avatar);
//            userData.setSocicalId(socialid);
//            userData.setSocialType(socialtype);
//            LoginSession loginSession = Utils.createAccessToken(app_id);
//            response = userService.registerGoogleSocial(userData,loginSession);
//        } else {
//            response = new SimpleResponse(ErrorCode.INVALID_PARAMS);
//        }

        return null;
    }

    @Override
    public _BaseResponse hanler(HttpServerRequest request, String body) throws Exception {

        _BaseResponse response = null;
        JsonObject object = (JsonObject) new JsonParser().parse(body);

        String username = object.get("un").getAsString();
        String email = object.get("email").getAsString();
        String avatar = object.get("avatar").getAsString();
        String socialid = object.get("sid").getAsString();
        String socialtype = object.get("st").getAsString();
        String app_id = object.get("appid").getAsString();
        logger.debug("{} login by Google",username);

        if (username != null && email !=null && socialid!=null && socialtype!=null && app_id!=null
                && username.length() > 0 && email.length() > 0 && socialid.length() > 0 && socialtype.length() > 0 && app_id.length() >0) {
            UserData userData = new UserData();
            userData.setUsername(username);
            userData.setEmail(email);
            userData.setAvatar(avatar);
            userData.setSocicalId(socialid);
            userData.setSocialType(socialtype);
            LoginSession loginSession = Utils.createAccessToken(app_id);
            response = userService.registerGoogleSocial(userData,loginSession);

        } else {
            response = new SimpleResponse(ErrorCode.INVALID_PARAMS);
        }
        logger.debug("Response login by Google : \n{}",response);
        return response;
    }
}
