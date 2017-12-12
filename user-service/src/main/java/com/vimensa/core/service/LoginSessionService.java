package com.vimensa.core.service;

import com.vimensa.apis.responses.LoginSessionResponse;
import com.vimensa.core.dao.LoginSession;
import com.vimensa.core.dao.UserData;

import java.sql.Connection;
import java.sql.SQLException;

public interface LoginSessionService {
    public LoginSession createToken(UserData userData, LoginSession loginSession) throws SQLException;

    public LoginSessionResponse checkToken(LoginSession loginSession) throws SQLException;

    public int userOfToken(Connection connection, LoginSession loginSession) throws SQLException;
}
