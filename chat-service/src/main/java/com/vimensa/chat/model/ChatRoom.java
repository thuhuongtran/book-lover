package com.vimensa.chat.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vimensa.chat.dao.Message;

import java.io.Serializable;
import java.util.List;
import java.util.Vector;

public class ChatRoom implements Serializable{
    private String roomLink;
    private Vector<Message> msgVector;
    private static int error=ErrorCode.UNPASS;

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public ChatRoom(String roomLink, Vector<Message> msgVector) {
        this.roomLink = roomLink;
        this.msgVector = msgVector;
    }

    public ChatRoom() {
    }

    public String getRoomLink() {
        return roomLink;
    }

    public void setRoomLink(String roomLink) {
        this.roomLink = roomLink;
    }

    public Vector<Message> getMsgVector() {
        return msgVector;
    }

    public void setMsgVector(Vector<Message> msgVector) {
        this.msgVector = msgVector;
    }

    // add Message in MsgVector
    public void addMsgInVector(Message msg){
        this.msgVector.add(msg);
    }

    // list of chat room to json string
    public String listToJsonString(List<ChatRoom> roomLi){
        JsonArray jsonArray = new JsonArray();
        for(ChatRoom room : roomLi){
            JsonObject json = new JsonObject();
            json.addProperty("roomLink", room.getRoomLink());
            //json.addProperty("roomName",room.getMsg().getRoomName());
            //json.addProperty("nick",room.getMsg().getUserNick());
            jsonArray.add(json.toString());
        }
        return jsonArray.toString();
    }

    public String toJsonString(){
        JsonArray jsonArray = new JsonArray();
        if(getError()==ErrorCode.SUCCESS){
            for(Message msg : getMsgVector()){
                JsonObject json = new JsonObject();
                json.addProperty("nick",msg.getUserNick());
                json.addProperty("avatar",msg.getUserAvatar());
                json.addProperty("userID", msg.getUserID());
                json.addProperty("roomLink",msg.getRoomLink());
                json.addProperty("roomName",msg.getRoomName());
                json.addProperty("msgTime",msg.getCreateAt());
                json.addProperty("msgType",msg.getType());
                json.addProperty("msg",msg.getMsg());
                jsonArray.add(json);
            }
        }
        JsonObject json = new JsonObject();
        json.addProperty("e", getError());
        json.addProperty("roomLink", getRoomLink());
        jsonArray.add(json);

        return jsonArray.toString();
    }
}
