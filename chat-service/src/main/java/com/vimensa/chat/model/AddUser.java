package com.vimensa.chat.model;

public class AddUser {
    private String roomLink;
    private String userIDLi;
    private String userID;
    private int error;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getError() {
        return error;
    }

    public String getUserIDLi() {
        return userIDLi;
    }

    public void setUserIDLi(String userIDLi) {
        this.userIDLi = userIDLi;
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


}
