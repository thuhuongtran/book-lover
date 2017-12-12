package com.vimensa.chat.service.imp;

import com.hazelcast.client.HazelcastClient;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.vimensa.chat.dao.User;
import com.vimensa.chat.service.UserServiceHazel;

import java.util.Map;
import java.util.Vector;


public class UserServiceHazelImp implements UserServiceHazel {
    //get list of user from hazel
    @Override
    public Vector<User> getAllUserHz(IMap<String,User> userIMap) {
        Vector<User> userLi = new Vector<User>();
        for(Map.Entry<String,User> entry : userIMap.entrySet()){
            userLi.addElement(entry.getValue());
        }
        return userLi;
    }

    @Override
    public IMap<String, User> getUserIMap() {
        HazelcastInstance instance = HazelcastClient.newHazelcastClient();
        return instance.getMap("users");
    }
}
