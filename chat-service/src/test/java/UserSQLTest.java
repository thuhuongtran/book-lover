import com.vimensa.chat.dao.User;
import com.vimensa.chat.model.FindUser;
import com.vimensa.chat.service.UserServiceDB;
import com.vimensa.chat.service.imp.UserServiceDBImp;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

public class UserSQLTest {
    private static UserServiceDB serviceDB ;
    public static void main(String[] args) throws IOException, SQLException {
        /*
        // get user from db
        serviceDB = new UserServiceDBImp();
        Vector<User> userLi = serviceDB.getAllUser(serviceDB.getUserResultSet());
        for(User user : userLi){
            System.out.println(user.getUserID()+" "+user.getUserNick()+" "+user.getUserAvatar()+" "+user.getUserName());
        */
        Vector<User> users = new Vector<>();
        User user = new User();
        user.setUserNick("huaga");

        User user1 = new User();
        user1.setUserNick("amny");

        User user2 = new User();
        user2.setUserNick("uhaf");

        User user3 = new User();
        user3.setUserNick("oahfa");

        User user4 = new User();
        user4.setUserNick("obgdd");

        User user5= new User();
        user5.setUserNick("bhaf");

        User user6 = new User();
        user6.setUserNick("bagc");

        User user7 = new User();
        user7.setUserNick("tsfaf");

        User user8 = new User();
        user8.setUserNick("ihatat");

        User user9 = new User();
        user9.setUserNick("betssd");


        users.addElement(user);
        users.addElement(user1);
        users.addElement(user2);
        users.addElement(user3);
        users.addElement(user4);
        users.addElement(user5);
        users.addElement(user6);
        users.addElement(user7);
        users.addElement(user8);
        users.addElement(user9);

        FindUser findUser = new FindUser();
        findUser.setUserLi(users);
        /*
        List<User> userLi = findUser.ordUserLiByNick();
        for(User u: userLi){
            System.out.println(u.getUserNick());
        }
        */
        // find users
        System.out.println("find users");
        findUser.setWord("ba");
        List<User> findU = findUser.findUsers();
        for(User u : findU){
            System.out.println(u.getUserNick());
        }

    }
}
