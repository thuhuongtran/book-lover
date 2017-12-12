package com.vimensa.chat.model;

import com.google.gson.JsonObject;

public class GetFile {
    private String userID;
    private String userNick;
    private String userAva;
    private String roomLink;
    private String roomName;
    private String msgTime;
    private static int error;

    public String getMsgTime() {
        return msgTime;
    }

    public void setMsgTime(String msgTime) {
        this.msgTime = msgTime;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserNick() {
        return userNick;
    }

    public void setUserNick(String userNick) {
        this.userNick = userNick;
    }

    public String getUserAva() {
        return userAva;
    }

    public void setUserAva(String userAva) {
        this.userAva = userAva;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public String getRoomLink() {
        return roomLink;
    }

    public void setRoomLink(String roomLink) {
        this.roomLink = roomLink;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    // to json string
    public String toJsonString() {
        JsonObject json = new JsonObject();
        json.addProperty("userID",getUserID());
        json.addProperty("userNick",getUserNick());
        json.addProperty("userAva",getUserAva());
        json.addProperty("roomLink",getRoomLink());
        json.addProperty("roomName",getRoomName());
        json.addProperty("msgTime",getMsgTime());
        json.addProperty("e",getError());
        return json.toString();
    }
}
