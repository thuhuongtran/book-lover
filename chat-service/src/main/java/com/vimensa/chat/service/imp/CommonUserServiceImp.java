package com.vimensa.chat.service.imp;

import com.vimensa.chat.dao.User;
import com.vimensa.chat.service.CommonUserService;
import com.vimensa.chat.service.UserServiceDB;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

public class CommonUserServiceImp implements CommonUserService {
    @Override
    public Vector<User> getAllUsers() throws IOException, SQLException {
        //UserServiceHazel userServiceHz = new UserServiceHazelImp();
        UserServiceDB userServiceDB = new UserServiceDBImp();
        //Vector<User> userLi = userServiceHz.getAllUserHz(userServiceHz.getUserIMap());
        //if(userLi==null||userLi.size()<500){ // add more condition ||userLi.size()<500
        Vector<User> userLi = userServiceDB.getAllUser(userServiceDB.getUserResultSet());
        //}
        return userLi;
    }


}
