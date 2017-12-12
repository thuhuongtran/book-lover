package com.vimensa.apis.responses;

import com.google.gson.JsonObject;
import com.vimensa.core.dao.LoginSession;
import lombok.Data;

@Data
public class LoginSessionResponse extends _BaseResponse {
    private LoginSession loginSession;

    @Override
    public String toJonString() {
        JsonObject json = new JsonObject();
        json.addProperty("e", getError());
        if (loginSession != null) {
            json.addProperty("at", loginSession.getAccessToken());
            json.addProperty("id",loginSession.getUserId());
            json.addProperty("un",loginSession.getUsername());
        }
        return json.toString();
    }
}
