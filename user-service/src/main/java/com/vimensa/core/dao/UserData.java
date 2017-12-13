package com.vimensa.core.dao;

import com.google.gson.JsonObject;
import lombok.Data;

import java.io.Serializable;
import java.util.Calendar;

@Data
public class UserData implements Serializable {
    private int id;
    private String username;
    private String password;
    private String email;
    private String avatar;
    private String nickName;
    private String phone;
    private String location;
    private String socicalId;
    private String socialType;
    private int locked;
    private String description;
    private String coverImg;
    private int role;
    private String linkFB;
    private String linkIns;
    private String linkWS;
    private Boolean firstReg = false;
    private Calendar timeCreated;
    private Boolean isFollow = false;
    public String toJsonString(){
        return toJsonObject().toString();
    }
    public JsonObject toJsonObject(){
        JsonObject json=new JsonObject();
        json.addProperty("uid", getId());
        json.addProperty("username",getUsername());
        json.addProperty("email",getEmail());
        json.addProperty("avatar",getAvatar());
        json.addProperty("coverImg",getCoverImg());
        json.addProperty("description",getDescription());
        json.addProperty("role",getRole());
        json.addProperty("linkFB",getLinkFB());
        json.addProperty("linkIns",getLinkIns());
        json.addProperty("linkWS",getLinkWS());
        json.addProperty("socialId",getSocicalId());
        json.addProperty("socialType",getSocialType());
        json.addProperty("firstReg",getFirstReg());
        json.addProperty("isFollow",getIsFollow());
        return json;
    }
}
