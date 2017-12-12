package com.vimensa.chat.service.imp;

import com.vimensa.chat.config.UserServiceConnect;
import com.vimensa.chat.dao.User;
import com.vimensa.chat.service.UserServiceDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public class UserServiceDBImp implements UserServiceDB {
    private static Logger logger = LoggerFactory.getLogger(UserServiceDBImp.class);

    @Override
    public Vector<User> getAllUser(ResultSet rs) {
        Vector<User> userLi = new Vector<User>();
        try {
            while (rs.next()){
                User user = new User();
                user.setUserID(rs.getInt("id"));
                user.setUserName(rs.getString("username"));
                user.setUserNick(rs.getString("nickname"));
                user.setUserAvatar(rs.getString("avatar"));
                userLi.addElement(user);
            }
        } catch (SQLException e) {
            logger.info(UserServiceDBImp.class.getName()+" resultset exception");
            e.printStackTrace();
        }
        return userLi;
    }

    @Override
    public ResultSet getUserResultSet() throws SQLException, IOException {
        UserServiceConnect.init();
        Connection con = UserServiceConnect.getConnection();
        String query = "SELECT `id`,`username`,`nickname`,`avatar` FROM user";
        PreparedStatement st = con.prepareStatement(query);
        ResultSet rs = st.executeQuery();
        con.close();
        return rs;
    }
}
