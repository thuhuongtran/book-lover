package com.vimensa.core.dao;

import com.google.gson.JsonObject;
import lombok.Data;

@Data
public class User {
    private String id;
    private int userid;
    private String username;
    private String avatar;
    private int status;

    public JsonObject toJsonObject(){
        JsonObject json=new JsonObject();
        JsonObject jsonuser = new JsonObject();
        json.addProperty("uid", getUserid());
        json.addProperty("un", getUsername());
        json.addProperty("avatar", getAvatar());
        return json;
    }
}
