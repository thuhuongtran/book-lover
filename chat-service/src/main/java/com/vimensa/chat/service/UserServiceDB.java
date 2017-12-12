package com.vimensa.chat.service;

import com.vimensa.chat.dao.User;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;

public interface UserServiceDB {
    // get all users from db
    public Vector<User> getAllUser(ResultSet rs);

    // get resultSet of users
    public ResultSet getUserResultSet() throws SQLException, IOException;
}
