package com.vimensa.apis.responses;

import com.google.gson.JsonObject;
import com.vimensa.core.dao.LoginSession;
import com.vimensa.core.dao.UserData;
import lombok.Data;

@Data
public class UserResponse extends _BaseResponse {
    private UserData userData;
    private LoginSession loginSession;

    @Override
    public String toJonString() {
        JsonObject json = new JsonObject();
        json.addProperty("e", getError());
        JsonObject userInfo = new JsonObject();
        if (userData != null) {
            userInfo.addProperty("uid", userData.getId());
            userInfo.addProperty("un", userData.getUsername());
            userInfo.addProperty("email", userData.getEmail());
            userInfo.addProperty("avatar", userData.getAvatar());
            userInfo.addProperty("coverImg",userData.getCoverImg());
            userInfo.addProperty("description",userData.getDescription());
            userInfo.addProperty("role",userData.getRole());
            userInfo.addProperty("linkFb",userData.getLinkFB());
            userInfo.addProperty("linkIns",userData.getLinkIns());
            userInfo.addProperty("linkWs",userData.getLinkWS());
            userInfo.addProperty("socialId", userData.getSocicalId());
            userInfo.addProperty("socialType", userData.getSocialType());
            userInfo.addProperty("firstReg",userData.getFirstReg());
            userInfo.addProperty("isFollow",userData.getIsFollow());
            json.add("uInfo", userInfo);
        }
        if (loginSession != null) {
            json.addProperty("at", loginSession.getAccessToken());
        }
        return json.toString();
    }
}
