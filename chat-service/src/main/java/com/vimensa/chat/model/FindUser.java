package com.vimensa.chat.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vimensa.chat.dao.User;

import java.util.List;
import java.util.Vector;

public class FindUser {
    private String word;
    private Vector<User> userLi;
    private int error;

    public int getError() {
        return error;
    }

    public void setError(int error) {
        this.error = error;
    }

    public FindUser() {
    }

    public FindUser(String word, Vector<User> userLi) {
        this.word = word;
        this.userLi = userLi;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public Vector<User> getUserLi() {
        return userLi;
    }

    public void setUserLi(Vector<User> userLi) {
        this.userLi = userLi;
    }

    /*
     *find users whose userNick match the word
    */
    public List<User> findUsers(){
        //List<User> userLi = ordUserLiByNick();
        List<User> userLi = getUserLi();
        List<User> findUserLi = new Vector<>();
        for(User u : userLi){
            if(u.getUserNick().startsWith(word)){
                findUserLi.add(u);
            }
        }
        return findUserLi;
    }
    public String toJsonString(List<User> usersFound){
        JsonArray jsonArray = new JsonArray();
        for(User u : usersFound){
            JsonObject json = new JsonObject();
            json.addProperty("userID",u.getUserID());
            json.addProperty("userNick",u.getUserNick());
            json.addProperty("userName",u.getUserName());
            json.addProperty("userAvatar",u.getUserAvatar());
            jsonArray.add(json);
        }
        return jsonArray.toString();
    }
    public String errToJsonString(){
        JsonObject json = new JsonObject();
        json.addProperty("e",getError());
        return json.toString();
    }
    /*
    * order userLi by userNick
    public List<User> ordUserLiByNick(){
        List<User> allUsers = getUserLi();
        allUsers.sort(new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                return o1.getUserNick().compareTo(o2.getUserNick());
            }
        });
        return allUsers;
    }
    * */
}
