package com.vimensa.test;

import com.vimensa.core.utils.Utils;

public class Test {
    /*
     * lack handlers: - update user_bl - set location
     *                - update user_bl - set phone
     * */
    public static void main(String []args){
        System.out.println("Password: "+ Utils.sha256("123456"));
    }
}
