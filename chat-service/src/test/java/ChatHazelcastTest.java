import com.vimensa.chat.dao.Message;
import com.vimensa.chat.service.ChatServiceHazel;
import com.vimensa.chat.service.CommonChatService;
import com.vimensa.chat.service.imp.CommonChatServiceImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Vector;

public class ChatHazelcastTest {
    private static ChatServiceHazel service;
    private static Logger logger = LoggerFactory.getLogger(ChatHazelcastTest.class);

    public static void main(String[] args) throws IOException, SQLException {

/*
        HazelcastInstance instance = HazelcastClient.newHazelcastClient();
        IMap<String, ChatRoom> roomIMap = instance.getMap("roomHistory");
        ChatRoom room = new ChatRoom();
        room.setRoomID(12);
        Message message = new Message();
        message.setRoomName("test_room_12");
        room.setMsg(message);
        roomIMap.put(String.valueOf(room.getRoomID()),room);
        ChatRoom room1 = new ChatRoom();
        room1.setRoomID(2);
        Message message1 = new Message();
        message1.setRoomName("test_room_2");
        room.setMsg(message1);
        roomIMap.put(String.valueOf(room1.getRoomID()),room1);

        HazelcastInstance instance = HazelcastClient.newHazelcastClient();
        IMap<String, ChatRoom> roomIMap = instance.getMap("roomHistory");
        for(Map.Entry<String, ChatRoom> entry: roomIMap.entrySet()){
            System.out.println(entry.getKey()+" "+entry.getValue().getMsg().getRoomName());
        }
*/
/*
        service = new ChatServiceHazelImp();
        IMap<String, ChatRoom> roomIMap=service.getRoomHazel();
        List<ChatRoom> roomList = service.getAllRoom(roomIMap);
        for(ChatRoom room:roomList){
            System.out.println(room.getRoomLink());
        }
  */
/*
        service = new ChatServiceHazelImp();
        IMap<String, ChatRoom> roomIMap = service.getRoomHazel();
        ChatRoom room = new ChatRoom();
        room.setRoomID(25);
        Message message = new Message();
        message.setRoomName("test_room_25");
        room.setMsg(message);
        service.pushMsgToRoomHazel(room,roomIMap);
  */
/*
        service = new ChatServiceHazelImp();
        IMap<String, ChatRoom> roomIMap = service.getRoomHazel();
        ChatRoom room = service.getRoomFromHazel(roomIMap, 25);
        System.out.println(room.getRoomID() + " " + room.getMsg().getRoomName());
        System.out.println("success!");
        */
/*
        service = new ChatServiceHazelImp();
        IMap<String, ChatRoom> roomIMap = service.getRoomHazel();
        Message msg = new Message();
        msg.setRoomLink("room_link_test1");
        msg.setMsg("test push new room hazel");
        ChatRoom room = new ChatRoom(msg.getRoomLink(), msg);
        service.pushNewRoomToHazel(room, roomIMap);
        logger.info("push new room to hazel successfully");
    */
/*
        CommonChatService commonChatService = new CommonChatServiceImp();
        Vector<Message> msgLi = commonChatService.getOldMsgs("link-harry");
        for(Message msg : msgLi){
            System.out.println(msg.getUserNick()+" "+msg.getRoomLink()+" "+msg.getCreateAt());
        }
        */

     /*
        Message msg = new Message();
        msg.setMsg("hello 1st");
        msg.setRoomLink("1st_link");
        Vector<Message> msgVector = new Vector<Message>();
        msgVector.add(msg);
        ChatRoom room = new ChatRoom(msg.getRoomLink(),msgVector);
        service = new ChatServiceHazelImp();
        service.pushNewRoomToHazel(room, service.getRoomHazel());
       */
  /*
        service = new ChatServiceHazelImp();
        IMap<String, ChatRoom> roomIMap=service.getRoomHazel();
        System.out.println(service.getRoomHazel()==null);
        List<ChatRoom> roomList = service.getAllRoom(roomIMap);
        System.out.println(roomList==null);
        for(ChatRoom room:roomList){
            System.out.println(room.getRoomLink());
            for(Message msg_i : room.getMsgVector()){
                System.out.println(msg_i.getMsg());
            }
        }
  */
  /*
 // test put msg on hazel room
        Message msg = new Message();
        msg.setMsg("test second time");
        msg.setRoomLink("1st_link");
        service = new ChatServiceHazelImp();
        IMap<String, ChatRoom> roomIMap = service.getRoomHazel();
        service.pushMsgToRoomHazel(msg,roomIMap);

        // print
        List<ChatRoom> roomList = service.getAllRoom(roomIMap);
        for(ChatRoom room:roomList){
            System.out.println(room.getRoomLink());
            for(Message msg_i : room.getMsgVector()){
                System.out.println(msg_i.getMsg());
            }
        }
*/

        // test get old msgs
        /*
        service = new ChatServiceHazelImp();
        // System.out.println(service.getOldMsgs("1st_link", service.getRoomHazel()));
        ChatRoom room = service.getRoomFromHazel(service.getRoomHazel(), "1st_link");
        //Vector<Message> vt = service.getOldMsgs("1st_link",service.getRoomHazel());
        // System.out.println(vt==null);
        Vector<Message> vt = room.getMsgVector();
        Vector<Message> msgLi = new Vector<Message>();
        System.out.println("1st_link");
        for (int i = vt.size(); i > 0; i--) {
            System.out.println(vt.elementAt(i-1).getMsg());
            msgLi.addElement(vt.elementAt(i-1));
        }
        System.out.println("msgli");
        for(Message msg: msgLi){
            System.out.println(msg.getMsg());
        }
        */
        /*
        // test get room
        service = new ChatServiceHazelImp();
        System.out.println(service.getRoomFromHazel(service.getRoomHazel(),"1st_link")==null);

    */
        /*
        service = new ChatServiceHazelImp();
        Vector<Message> vt = service.getOldMsgs("1st_link",service.getRoomHazel());
        for(Message msg : vt){
            System.out.println(msg.getMsg());
        }
        */
        /*
        service = new ChatServiceHazelImp();
        ChatRoom room = service.getRoomFromHazel(service.getRoomHazel(), "link-harry");
        System.out.println(room.getMsgVector()==null);
        for(Message msg : room.getMsgVector()){
            System.out.println(msg.getMsg());
            System.out.println(msg.getUserNick());
        }*/
    }
}
