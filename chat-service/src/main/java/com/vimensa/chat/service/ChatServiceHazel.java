package com.vimensa.chat.service;

import com.hazelcast.core.IMap;
import com.vimensa.chat.dao.Message;
import com.vimensa.chat.model.ChatRoom;

import java.util.List;
import java.util.Vector;

public interface ChatServiceHazel {
    // push message to hazelcast - roomHistory
    public void pushMsgToRoomHazel(Message msg, IMap<String, ChatRoom> roomMap);

    // get hazelcast instance
    public IMap<String, ChatRoom> getRoomHazel();

    public ChatRoom getRoomFromHazel(IMap<String, ChatRoom> roomIMap, String roomLink);

    //get all room in hazelcast
    public List<ChatRoom> getAllRoom(IMap<String, ChatRoom> roomIMap);

    //put new room to hazelcast
    public void pushNewRoomToHazel(ChatRoom room, IMap<String, ChatRoom> roomIMap);

    // get list of 50 msgs from hazel through roomLink
    public Vector<Message> getOldMsgs(String roomLink, IMap<String, ChatRoom> roomIMap);


}
