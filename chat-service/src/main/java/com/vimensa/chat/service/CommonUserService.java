package com.vimensa.chat.service;

import com.vimensa.chat.dao.User;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Vector;

public interface CommonUserService {
    //get list of all users
    public Vector<User> getAllUsers() throws IOException, SQLException;

}
