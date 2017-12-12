package com.vimensa.apis.handlers;

import com.google.gson.JsonArray;
import com.vimensa.apis.responses._BaseResponse;
import com.vimensa.core.dao.LoginSession;
import com.vimensa.core.dao.Roles;
import com.vimensa.core.utils.Utils;
import io.vertx.core.http.HttpServerRequest;
import lombok.Data;

@Data
public abstract class BaseApiHandler {
    protected String path = "";
    protected boolean isPublic = false;
    protected String method;
    protected int[] roles = {Roles.SUPER_ADMIN};

    public abstract _BaseResponse handle(HttpServerRequest request) throws Exception;
    public abstract _BaseResponse hanler(HttpServerRequest request, String body) throws Exception;

    public  boolean filterSecutiry(String nickname, String accessToken){
        LoginSession session = Utils.getLoginSession(nickname, accessToken);
        if(session != null) {
            int role = session.getRole();
            return checkRole(role);
        }
        return false;
    }

    private boolean checkRole(int role){
        for(int i = 0; i < roles.length; i++){
            if(roles[i] == role){
                return true;
            }
        }
        return (roles.length == 0);
    }

    public void initRoles(JsonArray roleArray) {
        roles = new int[roleArray.size()];
        for(int i = 0; i < roles.length; i++){
            roles[i] = roleArray.get(i).getAsInt();
        }
    }
}