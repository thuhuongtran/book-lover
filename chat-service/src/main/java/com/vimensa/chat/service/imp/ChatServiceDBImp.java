package com.vimensa.chat.service.imp;

import com.vimensa.chat.StartAPI;
import com.vimensa.chat.config.ChatServiceConnect;
import com.vimensa.chat.dao.Message;
import com.vimensa.chat.dao.Room;
import com.vimensa.chat.model.CreateRoom;
import com.vimensa.chat.service.ChatServiceDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Vector;


public class ChatServiceDBImp implements ChatServiceDB {
    private static Logger logger = LoggerFactory.getLogger(StartAPI.class);

    @Override
    public void addMsgToDB(Message msg) throws SQLException {
        Connection connection = ChatServiceConnect.getConnection();
        String query = "INSERT INTO Messages(`msg`,`create_at`,`msg_type`,`room_link`\n" +
                ",`room_name`,`user_id`,`usernick`,`user_avatar`)" +
                "VALUES\n" +
                "(?,?,?,?,?,?,?,?)";
        PreparedStatement st = connection.prepareStatement(query);
        st.setString(1, msg.getMsg());
        st.setString(2, msg.getCreateAt());
        st.setInt(3, msg.getType());
        st.setString(4, msg.getRoomLink());
        st.setString(5, msg.getRoomName());
        st.setString(6, msg.getUserID());
        st.setString(7, msg.getUserNick());
        st.setString(8, msg.getUserAvatar());

        st.executeUpdate();

        connection.close();

    }

    @Override
    public void addNewRoomToDB(CreateRoom room, String time) throws SQLException {
        Connection con = ChatServiceConnect.getConnection();
        String query = "INSERT INTO chat_room(`room_link`,`room_name`,`create_at`,`creator`)\n" +
                "\tVALUES (?,?,?,?)";
        PreparedStatement st = con.prepareStatement(query);
        st.setString(1, room.getRoomLink());
        st.setString(2, room.getRoomName());
        st.setString(3, time);
        st.setString(4, room.getSenderID());
        st.executeUpdate();
        con.close();

    }

    /*
     * get 50 old message from current in msg table on db
     * */
    @Override
    public Vector<Message> getOldMsgs(ResultSet rs, int loadCount) throws SQLException {
        Vector<Message> msgVector = new Vector<Message>();
        if (loadCount < 0) loadCount = 0;
        int count = 0;
        while (rs.next() && count < 50) {
            if (count == loadCount * 50) {
                Message msg = new Message();
                msg.setMsgID(rs.getString("msg_id"));
                msg.setMsg(rs.getString("msg"));
                msg.setCreateAt(rs.getString("create_at"));
                msg.setType(rs.getInt("msg_type"));
                msg.setRoomLink(rs.getString("room_link"));
                msg.setRoomName(rs.getString("room_name"));
                msg.setUserID(rs.getString("user_id"));
                msg.setUserNick(rs.getString("usernick"));
                msg.setUserAvatar(rs.getString("user_avatar"));
                msgVector.add(msg);

            }
            count++;
        }
        return msgVector;
    }

    @Override
    public ResultSet getResultOfMsg(String roomLink) throws SQLException {
        Connection con = ChatServiceConnect.getConnection();
        String query = "SELECT * FROM Messages WHERE `room_link`=?";
        PreparedStatement st = con.prepareStatement(query);
        st.setString(1, roomLink);
        ResultSet rs = st.executeQuery();
        con.close();
        return rs;
    }

    @Override
    public String getFileName(String msgTime, String roomLink) throws SQLException {
        String fileLink = null;
        Connection con = ChatServiceConnect.getConnection();
        String query = "SELECT `msg` FROM Messages WHERE `room_link`='" + roomLink + "' \n" +
                "AND `create_at`='" + msgTime + "'";
        PreparedStatement st = con.prepareStatement(query);
        ResultSet rs = st.executeQuery();
        con.close();
        while (rs.next()) {
            fileLink = rs.getString("msg");
        }
        return fileLink;
    }

    @Override
    public Message getMsgByTimeAndRoomLink(String msgTime, String roomLink) throws SQLException {
        Message msg = new Message();
        Connection con = ChatServiceConnect.getConnection();
        String query = "SELECT * FROM Messages WHERE `room_link`='" + roomLink + "' and `create_at`='" + msgTime + "'";
        PreparedStatement st = con.prepareStatement(query);
        ResultSet rs = st.executeQuery();
        con.close();
        while (rs.next()) {
            msg.setMsgID(rs.getString("msg_id"));
            msg.setMsg(rs.getString("msg"));
            msg.setCreateAt(rs.getString("create_at"));
            msg.setType(rs.getInt("msg_type"));
            msg.setRoomLink(rs.getString("room_link"));
            msg.setRoomName(rs.getString("room_name"));
            msg.setUserID(rs.getString("user_id"));
            msg.setUserNick(rs.getString("usernick"));
            msg.setUserAvatar(rs.getString("user_avatar"));
        }
        return msg;
    }

    @Override
    public void updateTxtMsg(String roomLink, String msgTime, String editMsg) throws SQLException {
        Connection con = ChatServiceConnect.getConnection();
        String query = "UPDATE Messages SET `msg` = '" + editMsg + "'\n" +
                " WHERE `room_link`='" + roomLink + "' AND `create_at`='" + msgTime + "';";
        PreparedStatement st = con.prepareStatement(query);
        st.executeUpdate();
        con.close();
    }

    @Override
    public void deleteTxtMsg(String roomLink, String msgTime) throws SQLException {
        Connection con = ChatServiceConnect.getConnection();
        String query = "DELETE FROM Messages WHERE `room_link`='" + roomLink + "' AND `create_at`='" + msgTime + "'";
        PreparedStatement st = con.prepareStatement(query);
        st.executeUpdate();
        con.close();
    }

    @Override
    public void addUserInRoom(String userID, String roomLink) throws SQLException {
        Connection con = ChatServiceConnect.getConnection();
        String query = "INSERT INTO room_user (`userID`,`roomlink`) VALUES (?,?)";
        PreparedStatement st = con.prepareStatement(query);
        st.setString(1, userID);
        st.setString(2, roomLink);
        st.executeUpdate();
        con.close();
    }

    @Override
    public Vector<String> getUserIDLiByRoomLink(String roomLink) throws SQLException {
        Connection con = ChatServiceConnect.getConnection();
        String query = "SELECT `userID` FROM room_user WHERE `roomlink`='" + roomLink + "'";
        PreparedStatement st = con.prepareStatement(query);
        ResultSet rs = st.executeQuery();
        con.close();
        Vector<String> userIDLi = new Vector<>();
        while (rs.next()) {
            String userID = rs.getString("userID");
            userIDLi.addElement(userID);
        }
        return userIDLi;
    }

    @Override
    public String getCreatorOfRoom(String roomLink) throws SQLException {
        String creator = "";
        Connection con = ChatServiceConnect.getConnection();
        String query = "select `creator` from chat_room where `room_link`='" + roomLink + "'";
        PreparedStatement st = con.prepareStatement(query);
        ResultSet rs = st.executeQuery();
        con.close();
        while (rs.next()) {
            creator = rs.getString("creator");
        }
        return creator;
    }

    @Override
    public void deleteUserFromRoom(String userID, String roomLink) throws SQLException {
        Connection con = ChatServiceConnect.getConnection();
        String query = "delete from room_user where `userID`='" + userID + "' and `roomlink`='" + roomLink + "'";
        PreparedStatement st = con.prepareStatement(query);
        st.executeUpdate();
        con.close();
    }

    @Override
    public Vector<Room> getRoomLiByUserID(String userID) throws SQLException {
        Connection con = ChatServiceConnect.getConnection();
        String query = "select room_user.`roomlink`,chat_room.`room_name`,chat_room.`create_at`,chat_room.`creator`\n" +
                " from room_user inner join chat_room on room_user.`roomlink`=chat_room.`room_link`\n" +
                " where room_user.`userID`='"+userID+"'";
        PreparedStatement st = con.prepareStatement(query);
        ResultSet rs = st.executeQuery();
        con.close();

        Vector<Room> roomLi = new Vector<>();
        while(rs.next()){
            Room room = new Room();
            room.setRoomLink(rs.getString("roomlink"));
            room.setRoomName(rs.getString("room_name"));
            room.setCreateAt(rs.getString("create_at"));
            room.setCreator(rs.getString("creator"));
            roomLi.addElement(room);
        }
        return roomLi;
    }

}
