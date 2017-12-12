package com.vimensa.chat.service;

import com.vimensa.chat.dao.Message;
import com.vimensa.chat.dao.Room;
import com.vimensa.chat.model.CreateRoom;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public interface ChatServiceDB {
    //push msg to db
    public void addMsgToDB(Message msg) throws SQLException;
    // write new room on DB
    public void addNewRoomToDB(CreateRoom room, String time) throws SQLException;
    // get old msg through roomLink
    public Vector<Message> getOldMsgs(ResultSet rs, int loadCount) throws SQLException;

    //get resultSet of msg
    public ResultSet getResultOfMsg(String roomLink) throws SQLException;

    //get file link from db by msgTime and roomLink
    public String getFileName(String msgTime, String roomLink) throws SQLException;

    // get msg obj from db by roomLink and msgTime
    public Message getMsgByTimeAndRoomLink(String msgTime, String roomLink) throws  SQLException;

    // update msg where roomLink and createTime
    public void updateTxtMsg(String roomLink, String msgTime, String msg) throws SQLException;

    // delete msg by msgTime and roomLink
    public void deleteTxtMsg(String roomLink, String msgTime) throws SQLException;

    // write on table room_user
    public void addUserInRoom(String userID, String roomLink) throws SQLException;

    // get list of userIDs by roomLink in
    public Vector<String> getUserIDLiByRoomLink(String roomLink) throws SQLException;

    // get creator of chat room
    public String getCreatorOfRoom(String roomLink) throws SQLException;

    // delete user in room_user by roomLink and userID
    public void deleteUserFromRoom(String userID, String roomLink) throws SQLException;

    // get list of rooms by userId
    public Vector<Room> getRoomLiByUserID(String userID) throws SQLException;
}
