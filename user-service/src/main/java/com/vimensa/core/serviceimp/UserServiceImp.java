package com.vimensa.core.serviceimp;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.vimensa.apis.responses.LoginSessionResponse;
import com.vimensa.apis.responses.SimpleResponse;
import com.vimensa.apis.responses.UserResponse;
import com.vimensa.config.APIConfig;
import com.vimensa.config.ErrorCode;
import com.vimensa.core.dao.LoginSession;
import com.vimensa.core.dao.UserData;
import com.vimensa.core.pools.HikariPool;
import com.vimensa.core.pools.MongoPool;
import com.vimensa.core.service.LoginSessionService;
import com.vimensa.core.service.UserService;
import com.vimensa.core.utils.Utils;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;

import static java.sql.Types.INTEGER;

public class UserServiceImp implements UserService {
    private static Logger logger = LoggerFactory.getLogger(UserServiceImp.class.getName());

    @Override
    public UserResponse login(UserData userData, LoginSession loginSession) throws SQLException {
        Connection connection = HikariPool.getConnection();
        UserResponse response = new UserResponse();
        logger.debug("LOGIN : {}", userData.getEmail());
        if (userData.getEmail() == null || userData.getPassword() == null || loginSession.getAppId() == null) {
            response.setError(ErrorCode.INVALID_PARAMS);
        } else {
            PreparedStatement st = null;
            ResultSet rs = null;
            try {
                String query = "SELECT * from account_login where email=? and socialId IS NULL";
                st = connection.prepareStatement(query);
                st.setString(1, userData.getEmail());
                rs = st.executeQuery();
                if (rs.next()) {
                    String password = rs.getString("password");
                    if (!password.equals(userData.getPassword())) {
                        response.setError(ErrorCode.INVALID_PASSWORD);
                    } else {
                        boolean lock = rs.getBoolean("locked");
                        if (!lock) {

                            Calendar now = Calendar.getInstance();
                            long timestamp = now.getTimeInMillis();
                            userData.setId(rs.getInt("id"));
                            userData.setUsername(rs.getString("username"));
                            userData.setEmail(rs.getString("email"));
                            userData.setRole(rs.getInt("role"));
                            userData.setAvatar(rs.getString("avatar"));
                            userData.setSocicalId(rs.getString("socialId"));
                            userData.setSocialType(rs.getString("socialType"));
                            loginSession.setAccessToken(Utils.createAccessToken(loginSession.getAppId()).getAccessToken());
                            loginSession.setTimeCreated(now);

                            LoginSessionService loginSessionService = new LoginSessionServiceImp();
                            if (loginSessionService.createToken(userData, loginSession) != null) {
                                response.setUserData(userData);
                                response.setLoginSession(loginSession);
                            } else {
                                response.setError(ErrorCode.SYSTEM_ERROR);
                            }

                        } else {
                            response.setError(ErrorCode.LOCKED_ACCOUNT);
                        }
                    }
                } else {
                    response.setError(ErrorCode.UNEXIST_EMAIL);
                }
            } catch (Exception e) {
                response.setError(ErrorCode.SYSTEM_ERROR);
                logger.debug("LOGIN Error: {}", e.getMessage());
            } finally {
                if (st != null) st.close();
                if (rs != null) rs.close();
                if (connection != null) connection.close();
            }
        }
        return response;
    }
//-----------------------------------------done - not checked yet
    @Override
    public UserResponse register(UserData userData) throws SQLException {
        Connection connection = HikariPool.getConnection();
        UserResponse response = new UserResponse();
        logger.debug("REGISTER : {} - {} - {}", userData.getUsername(), userData.getEmail());
        if (userData.getUsername() == null || userData.getEmail() == null || userData.getPassword() == null) {
            response.setError(ErrorCode.INVALID_PARAMS);
        } else {
            try {
                UserData user = getUserByEmail(userData);
                if (user != null)
                    response.setError(ErrorCode.EMAIL_USED);
                else {

                    Calendar now = Calendar.getInstance();
                    userData.setTimeCreated(now);
                    long timestamp = now.getTimeInMillis();

                    String query = "Insert into account_login(username,email,password,timeCreated) values(?,?,?,?)";
                    PreparedStatement st = connection.prepareStatement(query);
                    st.setString(1, userData.getUsername());
                    st.setString(2, userData.getEmail());
                    st.setString(3, userData.getPassword());
                    st.setTimestamp(4, new Timestamp(timestamp));
                    if (st.executeUpdate() > 0) {
                        String getquery = "SELECT * from account_login where email=? and socialId IS NULL";
                        PreparedStatement statement = connection.prepareStatement(getquery);
                        statement.setString(1, userData.getEmail());
                        ResultSet rs = statement.executeQuery();
                        if (rs.next()) {
                            ObjectId idCmt = new ObjectId();
                            String idComment = idCmt.toHexString();
                            MongoDatabase database = MongoPool.getDB();

                            //Insert user  to mongo
                            MongoCollection collectionComments = database.getCollection("users");
                            Document doc = new Document();
                            doc.append("_id", idComment);
                            doc.append("userid", rs.getInt("id"));
                            doc.append("username", rs.getString("username"));
                            doc.append("avatar", rs.getString("avatar"));
                            collectionComments.insertOne(doc);
                            // insert user into sql db
                            String insertquery = "INSERT INTO user_bl (`id_user`,`username`,`nickname`,`email`,`password`)" +
                                    " VALUES (?,?,?,?,?)";
                            PreparedStatement state = connection.prepareStatement(insertquery);
                            state.setInt(1, rs.getInt("id"));
                            state.setString(2,rs.getString("username"));
                            state.setString(3,rs.getString("username"));
                            state.setString(4,userData.getEmail());
                            state.setString(5,userData.getPassword());
                            state.executeQuery();
                        }


                        int id = rs.getInt(1);
                        userData.setId(id);
                        LoginSession loginSession = new LoginSession();
                        loginSession.setAppId("ns");
                        loginSession.setAccessToken(Utils.createAccessToken(loginSession.getAppId()).getAccessToken());
                        loginSession.setTimeCreated(now);
                        LoginSessionServiceImp loginSessionServiceImp = new LoginSessionServiceImp();
                        if (loginSessionServiceImp.createToken(userData, loginSession) != null) {
                            response.setLoginSession(loginSession);
                            response.setUserData(userData);
                            response.setError(ErrorCode.SUCCESS);
                        }else{
                            response.setError(ErrorCode.SYSTEM_ERROR);
                            return response;
                        }
                        statement.close();
                        st.close();
                    } else {
                        response.setError(ErrorCode.SYSTEM_ERROR);
                        return response;
                    }
                }
            } catch (Exception e) {
                response.setError(ErrorCode.SYSTEM_ERROR);
                logger.debug("REGISTER Error: {}", e.getMessage());
            } finally {
                connection.close();
            }
        }
        return response;
    }

    @Override
    public UserData getUserByEmail(UserData userData) throws SQLException {
        Connection connection = HikariPool.getConnection();
        UserData user = null;
        try {
            String query = "SELECT * from account_login where email=? and socialType IS NULL";
            PreparedStatement st = connection.prepareStatement(query);
            st.setString(1, userData.getEmail());
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                user = new UserData();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setAvatar(rs.getString("avatar"));
                user.setSocicalId(rs.getString("socialId"));
                user.setSocialType(rs.getString("socialType"));
                user.setLocked(rs.getInt("locked"));
            }
            st.close();
        } catch (Exception e) {

        } finally {
            connection.close();
        }
        return user;
    }

    @Override
    public UserResponse registerGoogleSocial(UserData userData, LoginSession loginSession) throws SQLException {
        Connection connection = HikariPool.getConnection();
        UserResponse response = new UserResponse();
        logger.debug("registerGoogleSocial : {} - {} - {} - {} - {} - {}", loginSession.getAccessToken(), userData.getUsername(), userData.getEmail(), userData.getAvatar(), userData.getSocicalId(), userData.getSocialType());
        try {
            Calendar now = Calendar.getInstance();
            userData.setTimeCreated(now);
            long timestamp = now.getTimeInMillis();
            String query = "CALL create_user_data_google_social(?,?,?,?,?,?,?,?,?,?)";
            CallableStatement st = connection.prepareCall(query);
            st.setString(1, userData.getUsername());
            st.setString(2, userData.getEmail());
            st.setString(3, userData.getAvatar());
            st.setString(4, userData.getSocicalId());
            st.setString(5, userData.getSocialType());
            st.setTimestamp(6, new Timestamp(timestamp));
            st.setString(7, loginSession.getAccessToken());
            st.setString(8, loginSession.getAppId());
            st.setTimestamp(9, new Timestamp(loginSession.getTimeCreated().getTimeInMillis()));
            st.registerOutParameter(10, INTEGER);
            ResultSet rs = st.executeQuery();
            int checkUsername = st.getInt(10);

            if (checkUsername == 0) {
                response.setError(ErrorCode.DUPLICATE_EMAIL);
            } else {
                if (rs.next()) {

                    if (checkUsername == 2) {
                        userData.setFirstReg(true);
                        ObjectId idCmt = new ObjectId();
                        String idComment = idCmt.toHexString();
                        MongoDatabase database = MongoPool.getDB();

                        //Insert user  to mongo
                        MongoCollection collectionComments = database.getCollection("users");
                        Document doc = new Document();
                        doc.append("_id", idComment);
                        doc.append("username", rs.getString("username"));
                        doc.append("userid", rs.getInt("id"));
                        doc.append("avatar", rs.getString("avatar"));
                        collectionComments.insertOne(doc);

                    } else if (checkUsername == 1) {
                        userData.setFirstReg(false);
                    }
                    userData.setId(rs.getInt("id"));
                    response.setUserData(userData);
                    response.setLoginSession(loginSession);
                }
            }

            st.close();
        } catch (Exception e) {
            response.setError(ErrorCode.SYSTEM_ERROR);
            logger.debug("registerGoogleSocial Error: {}", e.getMessage());
        } finally {
            connection.close();
        }
        return response;
    }
//--------------------------------------------------done - not checked yet
    @Override
    public UserResponse updateUserInfo(UserData userData, LoginSession loginSession) throws SQLException {
        Connection connection = HikariPool.getConnection();
        UserResponse response = new UserResponse();
        logger.debug("updateUserInfo: {} - {} - {} - {} - {} - {} - {}", loginSession.getAccessToken(), userData.getUsername(),
                userData.getDescription(), userData.getRole(), userData.getLinkFB(), userData.getLinkIns(), userData.getLinkWS());

        //checktoken
        LoginSessionService loginSessionService = new LoginSessionServiceImp();
        LoginSessionResponse loginSessionResponse = loginSessionService.checkToken(loginSession);
        if (loginSessionResponse.getError() == ErrorCode.ACTIVE_TOKEN || loginSessionResponse.getError() == ErrorCode.UPDATE_TOKEN) {
            if (loginSessionResponse.getError() == ErrorCode.UPDATE_TOKEN) {
                loginSession.setAccessToken(loginSessionResponse.getLoginSession().getAccessToken());
            }
            try {
                String query = "CALL update_user_info(?,?,?,?,?,?,?,?)";
                CallableStatement st = connection.prepareCall(query);
                st.setString(1, loginSession.getAccessToken());
                st.setString(2, loginSession.getAppId());
                st.setString(3, userData.getUsername());
                st.setString(4, userData.getDescription());
                st.setInt(5, userData.getRole());
                st.setString(6, userData.getLinkFB());
                st.setString(7, userData.getLinkIns());
                st.setString(8, userData.getLinkWS());
                ResultSet rs = st.executeQuery();
                if (rs.next()) {
                    MongoDatabase database = MongoPool.getDB();

                    //Insert user  to mongo
                    MongoCollection collectionComments = database.getCollection("users");
                    Document filter = new Document("userid", rs.getInt("id"));
                    collectionComments.updateOne(filter, new Document("$set", new Document("username", rs.getString("username"))));

                    userData.setId(rs.getInt("id"));
                    userData.setEmail(rs.getString("email"));
                    userData.setAvatar(rs.getString("avatar"));
                    userData.setCoverImg(rs.getString("coverimg"));
                    userData.setSocicalId(rs.getString("socialId"));
                    userData.setSocialType(rs.getString("socialType"));
                }

                response.setUserData(userData);
                response.setLoginSession(loginSession);
                st.close();
            } catch (Exception e) {
                response.setError(ErrorCode.SYSTEM_ERROR);
                logger.debug("updateUserInfo Error: {}", e.getMessage());
            } finally {
                connection.close();
            }

        } else if (loginSessionResponse.getError() == ErrorCode.INVALID_TOKEN) {
            response.setError(ErrorCode.INVALID_TOKEN);
        } else {
            response.setError(ErrorCode.SYSTEM_ERROR);
        }


        return response;
    }

    @Override
    public SimpleResponse updateAvatar(String avatar, LoginSession loginSession) throws SQLException {
        Connection connection = HikariPool.getConnection();
        SimpleResponse response = new SimpleResponse();
        logger.debug("updateCoverImage avatar: {}", avatar);

        LoginSessionService loginSessionService = new LoginSessionServiceImp();
        LoginSessionResponse loginSessionResponse = loginSessionService.checkToken(loginSession);
        if (loginSessionResponse.getError() == ErrorCode.ACTIVE_TOKEN || loginSessionResponse.getError() == ErrorCode.UPDATE_TOKEN) {
            if (loginSessionResponse.getError() == ErrorCode.UPDATE_TOKEN) {
                loginSession.setAccessToken(loginSessionResponse.getLoginSession().getAccessToken());
            }
            try {
                String query = "CALL update_avatar(?,?,?)";
                CallableStatement st = connection.prepareCall(query);
                st.setString(1, loginSession.getAccessToken());
                st.setString(2, loginSession.getAppId());
                st.setString(3, avatar);
                ResultSet rs = st.executeQuery();
                if (rs.next()) {

                    MongoDatabase database = MongoPool.getDB();

                    //Insert user  to mongo
                    MongoCollection collectionComments = database.getCollection("users");
                    Document filter = new Document("userid", rs.getInt("id"));
                    collectionComments.updateOne(filter, new Document("$set", new Document("avatar", rs.getString("avatar"))));

                }
                response.setError(ErrorCode.SUCCESS);
                st.close();
            } catch (Exception e) {
                response.setError(ErrorCode.SYSTEM_ERROR);
                logger.debug("updateCoverImage error: {}", e.getMessage());
            } finally {
                connection.close();
            }
        } else if (loginSessionResponse.getError() == ErrorCode.INVALID_TOKEN) {
            response.setError(ErrorCode.INVALID_TOKEN);
        } else {
            response.setError(ErrorCode.SYSTEM_ERROR);

        }
        return response;
    }

    //----------------------------------------------update cover img -done- not checked yet

    @Override
    public SimpleResponse updateCoverImage(String coverimg, LoginSession loginSession) throws SQLException {
        Connection connection = HikariPool.getConnection();
        SimpleResponse response = new SimpleResponse();
        logger.debug("updateCoverImage coverimg: {}", coverimg);

        LoginSessionService loginSessionService = new LoginSessionServiceImp();
        LoginSessionResponse loginSessionResponse = loginSessionService.checkToken(loginSession);
        if (loginSessionResponse.getError() == ErrorCode.ACTIVE_TOKEN || loginSessionResponse.getError() == ErrorCode.UPDATE_TOKEN) {
            if (loginSessionResponse.getError() == ErrorCode.UPDATE_TOKEN) {
                loginSession.setAccessToken(loginSessionResponse.getLoginSession().getAccessToken());
            }
            try {
                String query = "CALL update_cover_image(?,?,?)";
                CallableStatement st = connection.prepareCall(query);
                st.setString(1, loginSession.getAccessToken());
                st.setString(2, loginSession.getAppId());
                st.setString(3, coverimg);
                st.executeUpdate();
                response.setError(ErrorCode.SUCCESS);
                st.close();
            } catch (Exception e) {
                response.setError(ErrorCode.SYSTEM_ERROR);
                logger.debug("updateCoverImage Error: {}", e.getMessage());
            } finally {
                connection.close();
            }
        } else if (loginSessionResponse.getError() == ErrorCode.INVALID_TOKEN) {
            response.setError(ErrorCode.INVALID_TOKEN);
        } else {
            response.setError(ErrorCode.SYSTEM_ERROR);
        }
        return response;
    }
//------------------------------------------------done - not checked yet
    @Override
    public UserResponse getUserInfo(LoginSession loginSession, int uid) throws SQLException {
        Connection connection = HikariPool.getConnection();
        UserResponse response = new UserResponse();
        UserData userData = new UserData();

        LoginSessionService loginSessionService = new LoginSessionServiceImp();
        LoginSessionResponse loginSessionResponse = loginSessionService.checkToken(loginSession);
        if (loginSessionResponse.getError() == ErrorCode.ACTIVE_TOKEN || loginSessionResponse.getError() == ErrorCode.UPDATE_TOKEN) {
            if (loginSessionResponse.getError() == ErrorCode.UPDATE_TOKEN) {
                loginSession.setAccessToken(loginSessionResponse.getLoginSession().getAccessToken());
            }
            try {
                String query = "CALL get_user_info(?)";
                CallableStatement st = connection.prepareCall(query);
                st.setInt(1, uid);

                ResultSet rs = st.executeQuery();
                if (rs.next()) {
                    userData.setId(rs.getInt("id"));
                    userData.setEmail(rs.getString("email"));
                    userData.setAvatar(rs.getString("avatar"));
                    userData.setCoverImg(rs.getString("coverimg"));
                    userData.setRole(rs.getInt("roles"));
                    userData.setLinkFB(rs.getString("linkFaceBook"));
                    userData.setLinkIns(rs.getString("linkIns"));
                    userData.setLinkWS(rs.getString("linkWS"));
                    userData.setSocicalId(rs.getString("socialId"));
                    userData.setSocialType(rs.getString("socialType"));
                }

                MongoDatabase database = MongoPool.getDB();
                MongoCollection collectionFollow = database.getCollection("follows");
                ArrayList<Document> arrayList = new ArrayList<>();
                arrayList.add(new Document("uidFollow", uid));
                arrayList.add(new Document("userid", loginSessionResponse.getLoginSession().getUserId()));
                boolean check = false;
                FindIterable<Document> findIterable = collectionFollow.find(new Document("$and", arrayList));
                if (findIterable != null) {
                    for (Document doc : findIterable) {
                        check = true;
                        break;
                    }
                }
                userData.setIsFollow(check);

                response.setUserData(userData);
                response.setLoginSession(loginSession);
                st.close();
            } catch (Exception e) {
                response.setError(ErrorCode.SYSTEM_ERROR);
                logger.debug("getUserInfo Error: {}", e.getMessage());
            } finally {
                connection.close();
            }
        } else if (loginSessionResponse.getError() == ErrorCode.INVALID_TOKEN) {
            response.setError(ErrorCode.INVALID_TOKEN);
        } else {
            response.setError(ErrorCode.SYSTEM_ERROR);
        }
        return response;
    }

    @Override
    public UserResponse registerSocial(UserData userData, LoginSession loginSession) throws SQLException {
        Connection connection = HikariPool.getConnection();
        UserResponse response = new UserResponse();
        logger.debug("registerSocial: {} - {} - {} - {} - {} - {}", loginSession.getAccessToken(), userData.getUsername(), userData.getEmail(), userData.getAvatar(), userData.getSocicalId(), userData.getSocialType());
        try {
            Calendar now = Calendar.getInstance();
            userData.setTimeCreated(now);
            long timestamp = now.getTimeInMillis();
            String query = "CALL create_user_data_facebook_social(?,?,?,?,?,?,?,?,?,?)";
            CallableStatement st = connection.prepareCall(query);
            st.setString(1, userData.getUsername());
            st.setString(2, userData.getEmail());
            st.setString(3, userData.getAvatar());
            st.setString(4, userData.getSocicalId());
            st.setString(5, userData.getSocialType());
            st.setTimestamp(6, new Timestamp(timestamp));
            st.setString(7, loginSession.getAccessToken());
            st.setString(8, loginSession.getAppId());
            st.setTimestamp(9, new Timestamp(loginSession.getTimeCreated().getTimeInMillis()));
            st.registerOutParameter(10, INTEGER);
            ResultSet rs = st.executeQuery();

            int isFirstReg = st.getInt(10);

            if (rs.next()) {
                //Insert user  to mongo
                if (isFirstReg == 1) {
                    userData.setFirstReg(true);

                    ObjectId idCmt = new ObjectId();
                    String idComment = idCmt.toHexString();
                    MongoDatabase database = MongoPool.getDB();
                    MongoCollection collectionComments = database.getCollection("users");
                    Document doc = new Document();
                    doc.append("_id", idComment);
                    doc.append("userid", rs.getInt("id"));
                    doc.append("username", rs.getString("username"));
                    doc.append("avatar", rs.getString("avatar"));
                    collectionComments.insertOne(doc);
                } else {
                    userData.setFirstReg(false);
                }
                userData.setId(rs.getInt("id"));
            }

            response.setUserData(userData);
            response.setLoginSession(loginSession);
            st.close();
        } catch (Exception e) {
            response.setError(ErrorCode.SYSTEM_ERROR);
            logger.debug("registerSocial FB : {}", e.getMessage());
        } finally {
            connection.close();
        }
        return response;
    }

    @Override
    public SimpleResponse forgetPassword(String email) {
        SimpleResponse response = new SimpleResponse();
        Connection connection = null;
        PreparedStatement stCheck = null, stInsert = null;
        ResultSet rs = null;
        try {
            connection = HikariPool.getConnection();
            stCheck = connection.prepareStatement("SELECT * FROM account_login where email=? and socialType IS NULL");
            stCheck.setString(1, email);
            rs = stCheck.executeQuery();
            if (rs.next()) {
                String token = Utils.createTokenResetPassword(email);
                stInsert = connection.prepareStatement("INSERT INTO reset_password(email,token) values(?,?)");
                stInsert.setString(1, email);
                stInsert.setString(2, token);
                stInsert.execute();
                String message = "Để reset password cho tài khoản, hãy click vào link bên dưới \n" +
                        APIConfig.MODE + ":" + APIConfig.PORT + "/resetpassword?token=" + token;
                Utils.sendEmail(email, "Reset password", message);
                response.setError(ErrorCode.SUCCESS);
            } else {
                response.setError(ErrorCode.UNEXIST_EMAIL);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.setError(ErrorCode.SUCCESS);
            logger.error("Forgetpassword : {}", e.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            logger.error("Forgetpassword : {}", e.toString());
        } catch (MessagingException e) {
            e.printStackTrace();
            logger.error("Forgetpassword : {}", e.toString());
        } finally {
            if (stInsert != null) try {
                stInsert.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (stCheck != null) try {
                stCheck.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (rs != null) try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (connection != null) try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return response;
    }

    @Override
    public SimpleResponse resetPassword(String token) {
        SimpleResponse response = new SimpleResponse();
        Connection connection = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            connection = HikariPool.getConnection();
            st = connection.prepareStatement("SELECT * FROM reset_password where token=? and status=0");
            st.setString(1, token);
            rs = st.executeQuery();
            if (rs.next()) {
                String email = rs.getString("email");
                String newPassword = Utils.randomAlphaNumeric(10);
                st = connection.prepareStatement("UPDATE account_login set password=? where email=? and socialType IS NULL");
                st.setString(1, Utils.sha256(newPassword));
                st.setString(2, email);
                st.execute();

                st = connection.prepareStatement("UPDATE reset_password set status=1 where email=? and token=?");
                st.setString(1, email);
                st.setString(2, token);
                st.execute();
                String message = "Mật khẩu mới cho tài khoản của bạn là: " + newPassword;
                Utils.sendEmail(email, "New password", message);
                response.setError(ErrorCode.SUCCESS);
            } else
                response.setError(ErrorCode.INVALID_TOKEN);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setError(ErrorCode.SYSTEM_ERROR);
            logger.error("Resetpassword : {}", e.toString());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            logger.error("Resetpassword : {}", e.toString());
        } catch (MessagingException e) {
            e.printStackTrace();
            logger.error("Resetpassword : {}", e.toString());
        } finally {
            if (st != null) try {
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (rs != null) try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (connection != null) try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return response;
    }

    @Override
    public SimpleResponse changePassword(String email, String old_password, String new_password) {
        SimpleResponse response = new SimpleResponse();
        Connection connection = null;
        PreparedStatement st = null;
        ResultSet rs = null;
        try {
            connection = HikariPool.getConnection();
            st = connection.prepareStatement("SELECT * FROM account_login where email=? and socialType IS NULL");
            st.setString(1, email);
            rs = st.executeQuery();
            if (rs.next()) {
                if (rs.getString("password").equals(Utils.sha256(old_password))) {
                    st = connection.prepareStatement("UPDATE account_login set password=? where email=? and socialType IS NULL");
                    st.setString(1, Utils.sha256(new_password));
                    st.setString(2, email);
                    st.execute();
                    response.setError(ErrorCode.SUCCESS);
                } else {
                    response.setError(ErrorCode.INVALID_PASSWORD);
                }
            } else
                response.setError(ErrorCode.UNEXIST_EMAIL);
        } catch (SQLException e) {
            e.printStackTrace();
            response.setError(ErrorCode.SYSTEM_ERROR);
        } finally {
            if (rs != null) try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (st != null) try {
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            if (connection != null) try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return response;
    }

    @Override
    public SimpleResponse choiceRole(String token, int role) {
        SimpleResponse response=new SimpleResponse();
        Connection connection=null;
        PreparedStatement st=null;
        ResultSet rs=null;
        try {
            connection= HikariPool.getConnection();
            LoginSessionServiceImp loginSessionServiceImp=new LoginSessionServiceImp();
            LoginSession loginSession=new LoginSession();
            loginSession.setAccessToken(token);
            loginSession.setAppId("ns");
            int id=loginSessionServiceImp.userOfToken(connection,loginSession);
            if(id==0){
                response.setError(ErrorCode.INVALID_TOKEN);
            }else{
                st=connection.prepareStatement("UPDATE account_login set role=? where id=?");
                st.setInt(1,role);
                st.setInt(2,id);
                st.execute();
                response.setError(ErrorCode.SUCCESS);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.setError(ErrorCode.SYSTEM_ERROR);
        }
        return response;
    }

}