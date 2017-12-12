package com.vimensa.chat.service.imp;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.vimensa.chat.dao.Message;
import com.vimensa.chat.model.ChatRoom;
import com.vimensa.chat.service.ChatServiceHazel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Vector;

@Service
public class ChatServiceHazelImp implements ChatServiceHazel {

    @Override
    public void pushMsgToRoomHazel(Message msg, IMap<String, ChatRoom> roomIMap) {
        ChatRoom room = new ChatRoom();
        room.setRoomLink(msg.getRoomLink());
        for(Map.Entry<String, ChatRoom> entry : roomIMap.entrySet()){
            if(entry.getKey().equals(msg.getRoomLink())){
                room = entry.getValue();
                room.getMsgVector().add(msg);
            }
        }
        roomIMap.put(msg.getRoomLink(),room);
    }

    /*
    * get IMap room
    * */
    @Override
    public IMap<String, ChatRoom> getRoomHazel() {
        HazelcastInstance instance = HazelcastClient.newHazelcastClient();
        return instance.getMap("roomHistory");
    }
    /*
    * get room obj from hazelcast throughout roomLink
    * */
    public ChatRoom getRoomFromHazel(IMap<String, ChatRoom> roomIMap, String roomLink){
        ChatRoom room = null;
        for(Map.Entry<String, ChatRoom> entry : roomIMap.entrySet()){
            if(entry.getKey().equals(roomLink)){
                room = entry.getValue();
            }
        }
        return room;
    }

    @Override
    public List<ChatRoom> getAllRoom(IMap<String, ChatRoom> roomIMap) {
        List<ChatRoom> roomList = null;
        for(Map.Entry<String, ChatRoom> entry : roomIMap.entrySet()){
            roomList = (List<ChatRoom>) roomIMap.values();
        }
        return roomList;
    }

    @Override
    public void pushNewRoomToHazel(ChatRoom room, IMap<String, ChatRoom> roomIMap) {
        roomIMap.put(room.getRoomLink(), room);
    }

    // return list of 50 nearest msgs
    @Override
    public Vector<Message> getOldMsgs(String roomLink, IMap<String, ChatRoom> roomIMap) {
        Vector<Message> vt = getRoomFromHazel(roomIMap, roomLink).getMsgVector(); // get room chat
        Vector<Message> msgLi = new Vector<Message>();
        int count = 0;
        for(int i=vt.size()-1;i>=0;i--){
            msgLi.add(vt.get(i));
            count++;
            if(count==50) break;
        }
        return msgLi;
    }
}
