package com.vimensa.chat.service;

import com.hazelcast.core.IMap;
import com.vimensa.chat.dao.User;

import java.util.Vector;

public interface UserServiceHazel {
    // get list of all user from hazel
    public Vector<User> getAllUserHz(IMap<String,User> userIMap);
    //get userIMap
    public IMap<String, User> getUserIMap();
}
