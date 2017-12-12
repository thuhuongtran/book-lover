package com.vimensa.chat.dao;

import com.google.gson.JsonObject;
import com.vimensa.chat.model.ErrorCode;

import java.io.Serializable;

public class Message implements Serializable {
    private static int error;
    private String msgID;
    private String roomLink;
    private String userID;
    private String userNick;
    private String userAvatar;
    private String msg;
    private String createAt;
    private String roomName;
    private int type;

    private int loadCount; // for loading old message

    public int getLoadCount() {
        return loadCount;
    }

    public void setLoadCount(int loadCount) {
        this.loadCount = loadCount;
    }

    public Message(String msgID, String roomLink, String userID, String userNick, String userAvatar,
                   String msg, String createAt, String roomName, int type) {
        this.msgID = msgID;
        this.roomLink = roomLink;
        this.userID = userID;
        this.userNick = userNick;
        this.userAvatar = userAvatar;
        this.msg = msg;
        this.createAt = createAt;
        this.roomName = roomName;
        this.type = type;
    }

    public Message() {
    }

    public Message(String roomLink, String userID, String userNick, String userAvatar,
                   String msg, String createAt, String roomName, int type) {
        this.roomLink = roomLink;
        this.userID = userID;
        this.userNick = userNick;
        this.userAvatar = userAvatar;
        this.msg = msg;
        this.createAt = createAt;
        this.roomName = roomName;
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public String getMsgID() {
        return msgID;
    }

    public void setMsgID(String msgID) {
        this.msgID = msgID;
    }

    public String getRoomLink() {
        return roomLink;
    }

    public void setRoomLink(String roomLink) {
        this.roomLink = roomLink;
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

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String toJsonString() {
        JsonObject json = new JsonObject();
        if (getError() == ErrorCode.SUCCESS) {
            json.addProperty("nick", getUserNick());
            json.addProperty("avatar", getUserAvatar());
            json.addProperty("userID", getUserID());
            json.addProperty("roomLink", getRoomLink());
            json.addProperty("roomName", getRoomName());
            json.addProperty("msgTime", getCreateAt());
            json.addProperty("msgType", getType());
            json.addProperty("msg", getMsg());
        }
        json.addProperty("e", getError());
        return json.toString();
    }
    public String editMsgtoJsonString() {
        JsonObject json = new JsonObject();
        if (getError() == ErrorCode.SUCCESS) {
            json.addProperty("userID", getUserID());
            json.addProperty("roomLink", getRoomLink());
            json.addProperty("msgTime", getCreateAt());
            json.addProperty("msgType", getType());
            json.addProperty("msg", getMsg());
        }
        json.addProperty("e", getError());
        return json.toString();
    }
    public String deleteMsgtoJsonString() {
        JsonObject json = new JsonObject();
        if (getError() == ErrorCode.SUCCESS) {
        json.addProperty("userID", getUserID());
        json.addProperty("roomLink", getRoomLink());
        json.addProperty("msgTime", getCreateAt());
        json.addProperty("msgType", getType());
        }
        json.addProperty("e", getError());
        return json.toString();
    }

}
