package com.vimensa.core.serviceimp;

import com.vimensa.apis.responses.LoginSessionResponse;
import com.vimensa.config.ErrorCode;
import com.vimensa.core.dao.LoginSession;
import com.vimensa.core.dao.UserData;
import com.vimensa.core.pools.HikariPool;
import com.vimensa.core.service.LoginSessionService;
import com.vimensa.core.utils.Utils;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class LoginSessionServiceImp implements LoginSessionService {
    public static long TIME_TOKEN=86400000*7;
    @Override
    public LoginSession createToken(UserData userData, LoginSession loginSession) throws SQLException {
        Connection connection = HikariPool.getConnection();
        LoginSession session = null;
        try {
            String query = "INSERT INTO token(userId,appId,accessToken,timeCreated) values(?,?,?,?)";
            PreparedStatement st=connection.prepareStatement(query);
            st.setInt(1, userData.getId());
            st.setString(2, loginSession.getAppId());
            st.setString(3, loginSession.getAccessToken());
            st.setTimestamp(4, new Timestamp(loginSession.getTimeCreated().getTimeInMillis()));
            if (st.executeUpdate() > 0) {
                session = new LoginSession();
                session = loginSession;
            }
            st.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            connection.close();
        }
        return session;
    }
    //------------------------add condition into query select appID

    @Override
    public LoginSessionResponse checkToken(LoginSession loginSession) throws SQLException {
        Connection connection = HikariPool.getConnection();
        LoginSession session = null;
        LoginSessionResponse response = new LoginSessionResponse();
        PreparedStatement st = null;
        ResultSet resultSet = null;
        UserData userData = null;
        try {
            String query = "SELECT * FROM token,account_login where accessToken=? " +
                    "and account_login.id=token.userId and token.appId='bl'";
            st = connection.prepareStatement(query);
            st.setString(1, loginSession.getAccessToken());
            resultSet = st.executeQuery();
            if (resultSet!=null&&resultSet.next()) {
                loginSession.setUserId(resultSet.getInt("userId"));
                Calendar now = Calendar.getInstance();
                Calendar time = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd hh:mm:ss");
                time.setTime(sdf.parse(resultSet.getString("timeCreated")));
                if (now.getTimeInMillis() - time.getTimeInMillis() > TIME_TOKEN) {
                    session = new LoginSession();
                    session.setAccessToken(Utils.createAccessToken(loginSession.getAppId()).getAccessToken());
                    session.setTimeCreated(now);
                    String store = "CALL update_access_token(?,?,?,?,?)";
                    CallableStatement callableStatement = connection.prepareCall(store);
                    callableStatement.setString(1, loginSession.getAccessToken());
                    callableStatement.setString(2, session.getAccessToken());
                    callableStatement.setString(3, loginSession.getAppId());
                    callableStatement.setTimestamp(4, new Timestamp(session.getTimeCreated().getTimeInMillis()));
                    callableStatement.registerOutParameter(5, Types.INTEGER);
                    callableStatement.executeUpdate();
                    if (callableStatement.getInt(5) == 2) {
                        session = null;
                        response.setError(ErrorCode.SYSTEM_ERROR);
                    } else {
                        response.setError(ErrorCode.UPDATE_TOKEN);
                        session.setUserId(resultSet.getInt("userId"));
                        session.setUsername(resultSet.getString("username"));
                        response.setLoginSession(session);
                    }
                    callableStatement.close();
                } else {
                    session = loginSession;
                    session.setUserId(resultSet.getInt("userId"));
                    session.setUsername(resultSet.getString("username"));
                    response.setLoginSession(session);
                    response.setError(ErrorCode.ACTIVE_TOKEN);
                }
            } else {
                response.setError(ErrorCode.INVALID_TOKEN);
            }
        } catch (Exception e) {
            response.setError(ErrorCode.SYSTEM_ERROR);
        } finally {
            if(st!=null)st.close();
            if(resultSet!=null)resultSet.close();
            if(connection!=null)connection.close();
        }
        return response;
    }

    @Override
    public int userOfToken(Connection connection, LoginSession loginSession) throws SQLException {
        PreparedStatement st = null;
        ResultSet resultSet = null;
        UserData userData = null;
        int id=0;
        try {
            String query = "SELECT * FROM token,account_login where accessToken=? and account_login.id=token.userId";
            st = connection.prepareStatement(query);
            st.setString(1, loginSession.getAccessToken());
            resultSet = st.executeQuery();
            if (resultSet.next()) {
                loginSession.setUserId(resultSet.getInt("userId"));
                Calendar now = Calendar.getInstance();
                Calendar time = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd hh:mm:ss");
                time.setTime(sdf.parse(resultSet.getString("timeCreated")));
                if (now.getTimeInMillis() - time.getTimeInMillis() > TIME_TOKEN) {
                    LoginSession session = new LoginSession();
                    session.setAccessToken(Utils.createAccessToken(loginSession.getAppId()).getAccessToken());
                    session.setTimeCreated(now);
                    String store = "CALL update_access_token(?,?,?,?,?)";
                    CallableStatement callableStatement = connection.prepareCall(store);
                    callableStatement.setString(1, loginSession.getAccessToken());
                    callableStatement.setString(2, session.getAccessToken());
                    callableStatement.setString(3, loginSession.getAppId());
                    callableStatement.setTimestamp(4, new Timestamp(session.getTimeCreated().getTimeInMillis()));
                    callableStatement.registerOutParameter(5, Types.INTEGER);
                    callableStatement.executeUpdate();
                    if (callableStatement.getInt(5) == 2) {
                        id=0;
                    } else {
                        id=resultSet.getInt("userId");
                    }
                    callableStatement.close();
                } else {
                    id=resultSet.getInt("userId");
                }
            }
        } catch (Exception e) {
        } finally {
            if(st!=null)st.close();
            if(resultSet!=null)resultSet.close();
        }
        return id;
    }


}