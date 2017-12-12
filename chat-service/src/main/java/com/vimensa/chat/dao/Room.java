package com.vimensa.chat.dao;

public class Room {
    private String roomID;
    private String roomLink;
    private String roomName;
    private String createAt;
    private String creator;

    public Room() {
    }

    public Room(String roomID, String roomLink, String roomName, String createAt, String creator) {
        this.roomID = roomID;
        this.roomLink = roomLink;
        this.roomName = roomName;
        this.createAt = createAt;
        this.creator = creator;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
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

    public String getCreateAt() {
        return createAt;
    }

    public void setCreateAt(String createAt) {
        this.createAt = createAt;
    }
}
