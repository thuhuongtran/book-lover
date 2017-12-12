package com.vimensa.chat.model;

public class RemoveUser {
    private String userID;
    private String removUserID;
    private String roomLink;
    private int error;

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getRemovUserID() {
        return removUserID;
    }

    public void setRemovUserID(String removUserID) {
        this.removUserID = removUserID;
    }

    public String getRoomLink() {
        return roomLink;
    }

    public void setRoomLink(String roomLink) {
        this.roomLink = roomLink;
    }

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }
}
