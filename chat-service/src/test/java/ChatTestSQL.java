import com.vimensa.chat.service.ChatServiceDB;
import com.vimensa.chat.service.imp.ChatServiceDBImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.SQLException;

public class ChatTestSQL {
    private static Logger logger = LoggerFactory.getLogger(ChatTestSQL.class);
    public static void main(String[] args) throws IOException, SQLException {
        //test push msg to db
        /*
        ChatServiceDB serviceDB = new ChatServiceDBImp();
        Message msg = new Message();
        msg.setMsg("msg test2");
        msg.setCreateAt("time test2");
        msg.setRoomName("room-test2");
        msg.setRoomLink("36");
        msg.setType(1);
        msg.setUserID("6");
        msg.setUserNick("nick-test2");
        msg.setUserAvatar("avatar-test2");

        ChatRoom room = new ChatRoom(msg.getRoomLink(), msg);
        serviceDB.addNewRoomToDB(room);
        logger.info("push room to db successfully.");
        */
        /*
        // test get list of message
        ChatServiceDB serviceDB = new ChatServiceDBImp();
        serviceDB.getOldMsgs(serviceDB.getResultOfMsg("link_test1"));
        logger.info("get old msg successfully");
        */
        ChatServiceDB serviceDB = new ChatServiceDBImp();
        //System.out.println(serviceDB.getFileName("1512213947442", "74_89"));
        serviceDB.updateTxtMsg("link_test1","time1","hello test1");
        System.out.println("success");
    }
}
