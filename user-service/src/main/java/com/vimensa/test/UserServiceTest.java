package com.vimensa.test;

import com.mongodb.DB;
import com.mongodb.client.MongoCursor;
import com.vimensa.core.dao.User;
import com.vimensa.core.pools.MongoPool;
import org.jongo.Jongo;

import java.io.IOException;

public class UserServiceTest {
    public static void main(String[] args) {

//        JsonObject jsonObject = new JsonObject();
//        System.out.println("add user ");
//        System.out.println(HttpUtils.httpGetRequest("http://localhost:8083/user/add?uid=2&un=VietUng&avatar=avatarVietUngIT"));
//        UserData userData = new UserData();
//        userData.setUsername("Nguyễn Cương");
//        userData.setDescription("Description_NguyenCuong");
//        userData.setAvatar("Avatar_NguyenCuong");
//        userData.setCoverImg("Cover_Image_NguyenCuong");
//
//        LoginSession loginSession = new LoginSession();
//        loginSession.setAppId("ns");
//        loginSession.setAccessToken("abc");
//
//        UserService userService = new UserServiceImp();
//        try {
//            System.out.println("start update");
//            userService.updateUserInfo(userData,loginSession);
//            System.out.println("end update");
//        } catch (SQLException e) {
//            System.out.println("update fail");
//            e.printStackTrace();
//        }
        try {
            MongoPool.init();
        } catch (IOException e) {
            e.printStackTrace();
        }
        DB db = MongoPool.getDBJongo();
        Jongo jongo = new Jongo(db);
        org.jongo.MongoCollection cculogUser = jongo.getCollection("users");
        org.jongo.MongoCursor<User> cursorUser = cculogUser.find().as(User.class);
        while(cursorUser.hasNext()){
            User user = cursorUser.next();
            System.out.println(user.getUserid());
            System.out.println(user.getUsername());
            System.out.println("-----------------");
        }
    }
}
