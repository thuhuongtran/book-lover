package com.vimensa.chat.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vimensa.chat.dao.Room;

import java.util.Vector;

public class RoomList {
    Vector<Room> roomLi;
    private String userID;
    private int error;

    public RoomList() {
    }

    public RoomList(Vector<Room> roomLi, String userID) {
        this.roomLi = roomLi;
        this.userID = userID;
    }

    public Vector<Room> getRoomLi() {
        return roomLi;
    }

    public void setRoomLi(Vector<Room> roomLi) {
        this.roomLi = roomLi;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public String roomLiToJsonString() {
        JsonArray jsonArray = new JsonArray();
        if (getError() == ErrorCode.SUCCESS) {
            for (Room room : getRoomLi()) {
                JsonObject json = new JsonObject();
                json.addProperty("roomLink", room.getRoomLink());
                json.addProperty("roomName", room.getRoomName());
                json.addProperty("createAt", room.getCreateAt());
                json.addProperty("creator", room.getCreator());
                jsonArray.add(json);
            }
        }
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("e",getError());
        jsonObject.addProperty("userID",getUserID());
        jsonArray.add(jsonObject);
        return jsonArray.toString();
    }
}
