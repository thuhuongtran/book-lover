package com.vimensa.chat.model;

import com.vimensa.chat.dao.Message;

import java.util.Calendar;

public class CreateRoom {
    private String senderID;
    private String senderNick;
    private String senderAvatar;
    private String roomLink;
    private String roomName;
    private String msg;
    private int msgType;
    public CreateRoom() {
    }

    public CreateRoom(String senderID, String senderNick, String senderAvatar,
                      String roomLink, String roomName, String msg, int msgType) {
        this.senderID = senderID;
        this.senderNick = senderNick;
        this.senderAvatar = senderAvatar;
        this.roomLink = roomLink;
        this.roomName = roomName;
        this.msg = msg;
        this.msgType = msgType;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public String getSenderAvatar() {
        return senderAvatar;
    }

    public void setSenderAvatar(String senderAvatar) {
        this.senderAvatar = senderAvatar;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getSenderNick() {
        return senderNick;
    }

    public void setSenderNick(String senderNick) {
        this.senderNick = senderNick;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getRoomLink() {
        return roomLink;
    }

    public void setRoomLink(String roomLink) {
        this.roomLink = roomLink;
    }

    /*
        * convert createRoom to Message obj
        * */
    public Message toMessageObj(){
        Message msg = new Message();
        msg.setUserID(getSenderID());
        msg.setUserNick(getSenderNick());
        msg.setUserAvatar(getSenderAvatar());
        msg.setRoomLink(getRoomLink());
        msg.setRoomName(getRoomName());
        msg.setType(getMsgType());
        msg.setMsg(getMsg());
        Calendar cal = Calendar.getInstance();
        msg.setCreateAt(String.valueOf(cal.getTimeInMillis()));
        return  msg;

    }
}
