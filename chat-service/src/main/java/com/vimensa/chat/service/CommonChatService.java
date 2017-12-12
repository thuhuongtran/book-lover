package com.vimensa.chat.service;

import com.vimensa.chat.dao.Message;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Vector;

public interface CommonChatService {
    // get 50 msg from hazel or DB
    public Vector<Message> getOldMsgs(String roomLink, int loadCount) throws IOException, SQLException;

    // process roomLink form - return to list of userID
    public String[] getListUserID(String roomLink);

    // write each userID correspond to roomlink
    public void addUserIDsInRoom(String[] userIDLi, String roomLink) throws IOException, SQLException;
    // check user permission in room chat
    public boolean isPermittedInRoom(String userID, String roomLink) throws IOException, SQLException;
}
