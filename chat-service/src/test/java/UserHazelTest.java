import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.vimensa.chat.dao.User;
import com.vimensa.chat.service.UserServiceHazel;
import com.vimensa.chat.service.imp.UserServiceHazelImp;

import java.util.Vector;

public class UserHazelTest {
    private static UserServiceHazel service;
    public static void main(String[] args) {
        /*
        HazelcastInstance instance = HazelcastClient.newHazelcastClient();
        IMap<String, User> userIMap = instance.getMap("users");
        User user = new User();
        user.setUserID(32);
        user.setUserNick("jane-nick");
        user.setUserName("jane");
        user.setUserAvatar("jane-ava");
        userIMap.put(String.valueOf(user.getUserID()), user);

        User user1 = new User();
        user1.setUserID(454);
        user1.setUserNick("bill-nick");
        user1.setUserName("bill");
        user1.setUserAvatar("bill-ava");
        userIMap.put(String.valueOf(user1.getUserID()), user1);

        User user2 = new User();
        user2.setUserID(864);
        user2.setUserNick("jack-nick");
        user2.setUserName("jack");
        user2.setUserAvatar("jack-ava");
        userIMap.put(String.valueOf(user2.getUserID()), user2);
        */
        service = new UserServiceHazelImp();
        Vector<User> userLi=service.getAllUserHz(service.getUserIMap());
        for(User user:userLi){
            System.out.println(user.getUserID()+" "+user.getUserNick()+" "+user.getUserName()+" "+user.getUserAvatar());
        }
    }
}
