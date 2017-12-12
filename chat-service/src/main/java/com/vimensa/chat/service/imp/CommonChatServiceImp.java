package com.vimensa.chat.service.imp;

import com.vimensa.chat.dao.Message;
import com.vimensa.chat.service.ChatServiceDB;
import com.vimensa.chat.service.CommonChatService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Vector;

public class CommonChatServiceImp implements CommonChatService {
    /*
     * how to check exactly nearest msgs
     * */
    @Override
    public Vector<Message> getOldMsgs(String roomLink, int loadCount) throws IOException, SQLException {

        // firstly check whether msgs are on hazel
        //ChatServiceHazel serviceHazel = new ChatServiceHazelImp();
        //Vector<Message> msgVector = serviceHazel.getOldMsgs(roomLink, serviceHazel.getRoomHazel());

        //if(msgVector==null||msgVector.size()<=5){
        // if history of msg on hazel is null or too small then call from DB
        ChatServiceDB serviceDB = new ChatServiceDBImp();
        Vector<Message> msgVector = serviceDB.getOldMsgs(serviceDB.getResultOfMsg(roomLink),loadCount);
        //}
        return msgVector;
    }
    // split roomLink str into list of userID
    @Override
    public String[] getListUserID(String roomLink) {
        String[] userIDLi = roomLink.split("_");
         return userIDLi;
    }

    @Override
    public void addUserIDsInRoom(String[] userIDLi, String roomLink) throws IOException, SQLException {
        ChatServiceDB serviceDB = new ChatServiceDBImp();
        for(int i=0;i<userIDLi.length;i++){
            serviceDB.addUserInRoom(userIDLi[i],roomLink);
        }
    }

    @Override
    public boolean isPermittedInRoom(String userID, String roomLink) throws IOException, SQLException {
        ChatServiceDB serviceDB = new ChatServiceDBImp();
        Vector<String> userIDLi = serviceDB.getUserIDLiByRoomLink(roomLink);
        return userIDLi.contains(userID);
    }
}
