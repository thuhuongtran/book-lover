package com.vimensa.test;

import com.vimensa.core.utils.Utils;

public class Test {
    public static void main(String []args){
        System.out.println("Password: "+ Utils.sha256("123456"));
    }
}
