package com.vimensa.core.dao;

import lombok.Data;

import java.io.Serializable;
import java.util.Calendar;

@Data
public class LoginSession implements Serializable {
    public static final long serialVersionUID = 1L;
    private int userId;
    private String username;
    private String appId;
    private String accessToken;
    private int role;
    private Calendar timeCreated;
}
