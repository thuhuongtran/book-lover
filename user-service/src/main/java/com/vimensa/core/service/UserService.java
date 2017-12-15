package com.vimensa.core.service;

import com.vimensa.apis.responses.SimpleResponse;
import com.vimensa.apis.responses.UserResponse;
import com.vimensa.core.dao.LoginSession;
import com.vimensa.core.dao.UserData;

import java.sql.SQLException;

public interface UserService {
    public UserResponse login(UserData userData, LoginSession loginSession) throws SQLException;

    public UserResponse register(UserData userData) throws SQLException;

    public UserData getUserByEmail(UserData userData) throws SQLException;

    public UserResponse registerSocial(UserData userData, LoginSession loginSession) throws SQLException;

    public UserResponse registerGoogleSocial(UserData userData, LoginSession loginSession) throws SQLException;

    public UserResponse updateUserInfo(UserData userData, LoginSession loginSession) throws SQLException;

    public SimpleResponse updateAvatar(String avatar, LoginSession loginSession) throws SQLException;

    public SimpleResponse updateCoverImage(String coverimg, LoginSession loginSession) throws SQLException;

    public UserResponse getUserInfo(LoginSession loginSession, int uid) throws SQLException;

    public SimpleResponse forgetPassword(String email);

    public SimpleResponse resetPassword(String token);

    public SimpleResponse changePassword(String email, String old_password, String new_password);

    public SimpleResponse choiceRole(String token, int role);
}