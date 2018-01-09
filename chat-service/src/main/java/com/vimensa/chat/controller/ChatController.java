package com.vimensa.chat.controller;

import com.vimensa.chat.StartAPI;
import com.vimensa.chat.config.ChatServiceConnect;
import com.vimensa.chat.dao.Message;
import com.vimensa.chat.model.*;
import com.vimensa.chat.service.ChatServiceDB;
import com.vimensa.chat.service.CommonChatService;
import com.vimensa.chat.service.FileProcess;
import com.vimensa.chat.service.imp.ChatServiceDBImp;
import com.vimensa.chat.service.imp.CommonChatServiceImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Vector;

@RestController
@RequestMapping(value = {"/chat"})
public class ChatController {
    private final Logger logger = LoggerFactory.getLogger(StartAPI.class);

    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    public ChatController(SimpMessagingTemplate simpMessagingTemplate) {
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    //private static Message test_msg;
    /*
     * send Message to an exist room
     * ---check null param--check security
     * */
    @RequestMapping(value = "/sendMsg", method = RequestMethod.POST)
    @MessageMapping("/chat/sendMsg/{roomLink}")
    //@SendTo("/room/{roomLink}")
    public String sendMessage(@DestinationVariable(value = "roomLink") String roomLink,
                              @RequestBody Message msg, @Payload Message payloadMsg,
                              HttpSession session, HttpServletResponse response) {
        if (session.getAttribute("userID") == null) {
            // send to login
            Response rsp = new Response();
            try {
                response.sendRedirect("/login");
                rsp.setError(ErrorCode.UNAUTHORIZED);
                logger.info(MainController.class.getName() + " sendMsg redirect to login");
            } catch (IOException e) {
                rsp.setError(ErrorCode.INTERNAL_EXCEPTION);
                logger.info(MainController.class.getName() + " /sendMsg io exception in response redirect");
                e.printStackTrace();
            }
            return "e: " + rsp.getError();

        } else {

            // get time in milis
            Calendar cal = Calendar.getInstance();
            msg.setCreateAt(String.valueOf(cal.getTimeInMillis()));
            msg.setType(MessageType.TEXT_MSG);

            // payload msg room destination
            roomLink = msg.getRoomLink();
            payloadMsg = msg;
            this.simpMessagingTemplate.convertAndSend("/room/" + roomLink, payloadMsg);
            // push to DB
            ChatServiceDB chatServiceDB = new ChatServiceDBImp();
            try {
                ChatServiceConnect.init(); // init chat-db-connection
                chatServiceDB.addMsgToDB(msg);
                msg.setError(ErrorCode.SUCCESS);
                logger.info(ChatController.class.getName() + " add msg to db successfully.");
            } catch (SQLException e) {
                msg.setError(ErrorCode.INTERNAL_EXCEPTION);
                logger.info(ChatController.class.getName() + " sendMsg - sql exception");
                e.printStackTrace();
            } catch (IOException e) {
                msg.setError(ErrorCode.INTERNAL_EXCEPTION);
                logger.info(ChatController.class.getName() + " sendMsg io exception");
                e.printStackTrace();
            }

            return msg.toJsonString();
        }
        /*test_msg = msg;
        return "/chat/room/"+roomID;*/
    }
    /*@RequestMapping(value = "/room/58", method = RequestMethod.GET)
    public String testChatRoom(){

        return test_msg.getUserNick()+" "+ test_msg.getMsg();
    }*/

    /*
     * just text
     *  create a new chat room
     * clients must send roomlink to server in form "senderID_addUserID1_addUserID2_addUserID3_..."
     * if clients do not set roomName then default roomName = userNick1+userNick2+...
     * afterall, send msg like /sendMsg request
     *
     * ---check null param--check security
     * param: roomLink, msg, senderID, senderAvatar, senderNick, roomName, msgType
     * */
    @RequestMapping(value = "/createRoom", method = RequestMethod.POST)
    @MessageMapping("/chat/createRoom/{roomLink}")
    //@SendTo("/room/{roomID}")
    public String createNewChatRoom(@DestinationVariable(value = "roomLink") String roomLink,
                                    @RequestBody CreateRoom createRoom,
                                    @Payload Message payloadMsg,
                                    HttpServletResponse response, HttpSession session) {
        if (session.getAttribute("userID") == null) {
            // send to login
            Response rsp = new Response();
            try {
                response.sendRedirect("/login");
                rsp.setError(ErrorCode.UNAUTHORIZED);
                logger.info(MainController.class.getName() + " createRoom redirect to login");
            } catch (IOException e) {
                rsp.setError(ErrorCode.INTERNAL_EXCEPTION);
                logger.info(MainController.class.getName() + " /createRoom io exception in response redirect");
                e.printStackTrace();
            }
            return "e: " + rsp.getError();

        } else {

            // covert creatRoom to message obj
            Message msg = createRoom.toMessageObj();
            msg.setCreateAt(String.valueOf(Calendar.getInstance().getTimeInMillis()));// get time im milis
            logger.info(ChatController.class.getName() + " convert createRoom to Message obj successfully!");// check null params

            // payload msg to chat room
            roomLink = createRoom.getRoomLink();
            payloadMsg = msg;
            this.simpMessagingTemplate.convertAndSend("/room/" + roomLink, payloadMsg);

            try {
                ChatServiceConnect.init(); // init chat-db-connect
                // write new room on DB
                ChatServiceDB serviceDB = new ChatServiceDBImp();
                serviceDB.addNewRoomToDB(createRoom, msg.getCreateAt());
                logger.info(ChatController.class.getName() + " write new room on DB successfully.");

                //write msg on db
                serviceDB.addMsgToDB(msg);
                logger.info(ChatController.class.getName() + " write msg on DB successfully");

                // write on table room_user
                // get all userId from roomLink form
                CommonChatService chatService = new CommonChatServiceImp();
                String[] userLi = chatService.getListUserID(roomLink);
                // write into table room_user
                chatService.addUserIDsInRoom(userLi, roomLink);
                msg.setError(ErrorCode.SUCCESS);
                logger.info(ChatController.class.getName() + " add userID list in room successfully");

            } catch (SQLException e) {
                msg.setError(ErrorCode.INTERNAL_EXCEPTION);
                logger.info(ChatController.class.getName() + " create new room - sqlexception");
                e.printStackTrace();
            } catch (IOException e) {
                msg.setError(ErrorCode.INTERNAL_EXCEPTION);
                logger.info(ChatController.class.getName() + " create new room io exception");
                e.printStackTrace();
            }

            return msg.toJsonString();
        }
    }

    /*
     * start a new room with sending a file
     * param: roomLink, file, senderId, senderAvatar, senderNick, roomName, msgType
     * */
    @RequestMapping(value = "/createRoomWithFile", method = RequestMethod.POST)
    @MessageMapping("/chat/createRoomWithFile/{roomLink}")
//@SendTo("/room/{roomID}")
    public String createNewRoomWithSendingFile(@DestinationVariable(value = "roomLink") String des_roomLink,
                                               @Payload Message payloadMsg,
                                               @RequestParam(name = "roomLink") String roomLink,
                                               @RequestParam(name = "file") MultipartFile file,
                                               @RequestParam(name = "senderID") String senderID,
                                               @RequestParam(name = "senderAva") String senderAva,
                                               @RequestParam(name = "senderNick") String senderNick,
                                               @RequestParam(name = "roomName") String roomName,
                                               @RequestParam(name = "msgType") String type,
                                               HttpSession session, HttpServletResponse response) {
        if (session.getAttribute("userID") == null) {
            // send to login
            Response rsp = new Response();
            try {
                response.sendRedirect("/login");
                rsp.setError(ErrorCode.UNAUTHORIZED);
                logger.info(MainController.class.getName() + " createRoomWithFile redirect to login");
            } catch (IOException e) {
                rsp.setError(ErrorCode.INTERNAL_EXCEPTION);
                logger.info(MainController.class.getName() + " /createRoomWithFile io exception in response redirect");
                e.printStackTrace();
            }
            return "e: " + rsp.getError();

        } else {

            Message msg = new Message();
            // check null param
            if (roomLink == null || roomLink.equals("") || file == null || senderID == null || senderID.equals("")
                    || senderAva == null || senderAva.equals("") || senderNick == null || senderNick.equals("")
                    || roomName == null || roomName.equals("") || type == null || !type.equals("2")) {
                // return error
                msg.setError(ErrorCode.NULL_REQUEST_PARAM);
                logger.info(ChatController.class.getName() + " null request param");
            } else {
                int msgType = Integer.parseInt(type);
                //write file in set directory then get file name
                FileProcess fileProcess = new FileProcess();
                String fileName = fileProcess.processFile(file);
                // get time
                String createAt = String.valueOf(Calendar.getInstance().getTimeInMillis());
                msg = new Message(roomLink, senderID, senderNick, senderAva, fileName, createAt, roomName, msgType);

                // payload msg to chat room
                msg = new Message(roomLink, senderID, senderNick, senderAva, fileName, createAt, roomName, msgType);
                des_roomLink = roomLink;
                payloadMsg = msg;
                this.simpMessagingTemplate.convertAndSend("/room/" + des_roomLink, payloadMsg);

                try {
                    ChatServiceConnect.init(); // init
                    //push msg to db
                    ChatServiceDB chatServiceDB = new ChatServiceDBImp();
                    chatServiceDB.addMsgToDB(msg);
                    logger.info(ChatController.class.getName() + " add msg to db successfully.");

                    // write new room on DB
                    CreateRoom createRoom = new CreateRoom(senderID, senderNick, senderAva, roomLink, roomName, fileName, msgType);
                    ChatServiceDB serviceDB = new ChatServiceDBImp();
                    serviceDB.addNewRoomToDB(createRoom, msg.getCreateAt());
                    logger.info(ChatController.class.getName() + " write new room on DB successfully.");

                    // write on table room_user
                    // get all userId from roomLink form
                    CommonChatService chatService = new CommonChatServiceImp();
                    String[] userLi = chatService.getListUserID(roomLink);
                    // write into table room_user
                    chatService.addUserIDsInRoom(userLi, roomLink);
                    msg.setError(ErrorCode.SUCCESS);
                    logger.info(ChatController.class.getName() + " add userID list in room successfully");
                } catch (IOException e) {
                    msg.setError(ErrorCode.INTERNAL_EXCEPTION);
                    logger.info(ChatController.class.getName() + " add userID in room io exception");
                    e.printStackTrace();
                } catch (SQLException e) {
                    msg.setError(ErrorCode.INTERNAL_EXCEPTION);
                    logger.info(ChatController.class.getName() + " add userID in room sql exception");
                    e.printStackTrace();
                }
            }
            return msg.toJsonString();
        }
    }

    /*
    1. get chat room from hazel if null --> create new one
    * load old messages - history of chat room
    * return 50 messages each time
    * playload all msgs to roomLink
    * param: roomLink, loadCount
    * */
    @RequestMapping(value = "/loadChatHistory", method = RequestMethod.POST)
    @MessageMapping(value = "/chat/loadOldMsgs/{roomLink}")
    public String loadOldMsgs(@DestinationVariable(value = "roomLink") String des_roomLink,
                              @RequestBody Message msgRoomLink, HttpServletResponse response,
                              @Payload ChatRoom payload_msgLi,HttpSession session) {
        if (session.getAttribute("userID") == null) {
            // send to login
            Response rsp = new Response();
            try {
                response.sendRedirect("/login");
                rsp.setError(ErrorCode.UNAUTHORIZED);
                logger.info(MainController.class.getName() + " loadChatHistory redirect to login");
            } catch (IOException e) {
                rsp.setError(ErrorCode.INTERNAL_EXCEPTION);
                logger.info(MainController.class.getName() + " /loadChatHistory io exception in response redirect");
                e.printStackTrace();
            }
            return "e: " + rsp.getError();

        } else {
            String roomLink = msgRoomLink.getRoomLink();
            int loadCount = msgRoomLink.getLoadCount();
            // check roomLink is null
            if (roomLink != null || !roomLink.equals("")) {
                // get 50 msgs
                CommonChatService commonChatService = new CommonChatServiceImp();
                ChatRoom room = new ChatRoom();
                try {
                    ChatServiceConnect.init(); // init
                    Vector<Message> msgLi = commonChatService.getOldMsgs(roomLink, loadCount);
                    room.setMsgVector(msgLi);
                } catch (IOException e) {
                    room.setError(ErrorCode.INTERNAL_EXCEPTION);
                    logger.info(ChatController.class.getName() + " loadChatHistory io exception" + e.getMessage());
                    e.printStackTrace();
                } catch (SQLException e) {
                    room.setError(ErrorCode.INTERNAL_EXCEPTION);
                    logger.info(ChatController.class.getName() + " loadChatHistory sql exception" + e.getMessage());
                    e.printStackTrace();
                }
                logger.info("get 50 old msg from db successfully");
                // payload old msgs
                des_roomLink = roomLink;
                payload_msgLi = room;
                payload_msgLi.setRoomLink(roomLink);
                this.simpMessagingTemplate.convertAndSend("/room/" + des_roomLink, payload_msgLi);

                room.setError(ErrorCode.SUCCESS);
                return room.toJsonString();
            } else {
                ChatRoom room = new ChatRoom();
                room.setError(ErrorCode.INTERNAL_EXCEPTION);
                return room.toJsonString();
            }
        }

    }

    /*
     * send file
     * 1. get msg from request
     * 2. payload msg to room - set msg = filename
     * 3. push msg to db
     *
     * notice: client just show file symbol
     * if client want to download files then get file from server, send to clients
     *
     * */
    @RequestMapping(value = "/sendFile", method = RequestMethod.POST)
    @MessageMapping(value = "/chat/sendFile/{roomLink}")
    public String sendFile(@DestinationVariable(value = "roomLink") String des_roomLink,
                           @RequestParam(name = "file") MultipartFile file,
                           @RequestParam(name = "senderID") String senderID,
                           @RequestParam(name = "senderNick") String senderNick,
                           @RequestParam(name = "senderAvatar") String senderAva,
                           @RequestParam(name = "roomLink") String roomLink,
                           @RequestParam(name = "roomName") String roomName,
                           @Payload Message payloadMsg,
                           HttpSession session, HttpServletResponse response) {
        if (session.getAttribute("userID") == null) {
            // send to login
            Response rsp = new Response();
            try {
                response.sendRedirect("/login");
                rsp.setError(ErrorCode.UNAUTHORIZED);
                logger.info(MainController.class.getName() + " sendFile redirect to login");
            } catch (IOException e) {
                rsp.setError(ErrorCode.INTERNAL_EXCEPTION);
                logger.info(MainController.class.getName() + " /sendFile io exception in response redirect");
                e.printStackTrace();
            }
            return "e: " + rsp.getError();

        } else {
            //write file in set directory then get file name
            FileProcess fileProcess = new FileProcess();
            String fileName = fileProcess.processFile(file);
            // get time
            String createAt = String.valueOf(Calendar.getInstance().getTimeInMillis());
            Message msg = new Message(roomLink, senderID, senderNick, senderAva, fileName, createAt, roomName, MessageType.FILE_MSG);

            // payload msg to chat room
            des_roomLink = roomLink;
            payloadMsg = msg;
            this.simpMessagingTemplate.convertAndSend("/room/" + des_roomLink, payloadMsg);

            try {
                ChatServiceConnect.init();//init
                //push msg to db
                ChatServiceDB chatServiceDB = new ChatServiceDBImp();
                chatServiceDB.addMsgToDB(msg);
                msg.setError(ErrorCode.SUCCESS);
                logger.info(ChatController.class.getName() + " add msg to db successfully.");
            } catch (SQLException e) {
                msg.setError(ErrorCode.INTERNAL_EXCEPTION);
                logger.info(ChatController.class.getName() + " sendFile sql exception");
                e.printStackTrace();
            } catch (IOException e) {
                msg.setError(ErrorCode.INTERNAL_EXCEPTION);
                logger.info(ChatController.class.getName() + " sendFile io exception");
                e.printStackTrace();
            }

            return msg.toJsonString();
        }
    }

    /*
     * get file from server then send to client
     * this event happens whenever client click on one file
     * search file link from db by msgTime and roomLink
     * */
    @RequestMapping(value = "/downloadFile", method = RequestMethod.POST)
    public String downloadFile(@RequestBody GetFile getFile, HttpServletResponse response, HttpSession session) {
        if (session.getAttribute("userID") == null) {
            // send to login
            Response rsp = new Response();
            try {
                response.sendRedirect("/login");
                rsp.setError(ErrorCode.UNAUTHORIZED);
                logger.info(MainController.class.getName() + " downloadFile redirect to login");
            } catch (IOException e) {
                rsp.setError(ErrorCode.INTERNAL_EXCEPTION);
                logger.info(MainController.class.getName() + " /downloadFile io exception in response redirect");
                e.printStackTrace();
            }
            return "e: " + rsp.getError();

        } else {
            // get file link from db then get file from folder
            String msgTime = getFile.getMsgTime();
            String roomLink = getFile.getRoomLink();
            ChatServiceDB serviceDB = new ChatServiceDBImp();
            String fileName = "";
            try {
                ChatServiceConnect.init(); // init
                fileName = serviceDB.getFileName(msgTime, roomLink);
            } catch (IOException e) {
                getFile.setError(ErrorCode.FILE_NOT_FOUND);
                logger.info(ChatServiceDBImp.class.getName() + " io exception");
                e.printStackTrace();
            } catch (SQLException e) {
                getFile.setError(ErrorCode.INTERNAL_EXCEPTION);
                logger.info(ChatServiceDBImp.class.getName() + " sql exception");
                e.printStackTrace();
            }

            // check if fileName is empty
            if (fileName == null || fileName.equals("")) {
                // set error
                getFile.setError(ErrorCode.FILE_NOT_FOUND);
                logger.info(ChatController.class.getName() + " fail to get file link from server");
            } else if (fileName != null || !fileName.equals("")) {
                // return file to client
                FileProcess fileProcess = new FileProcess();
                //get file
                File returnFile = fileProcess.getFile(fileName);
                logger.info(ChatController.class.getName() + " get file from server successfully.");
                // response file to client
                String mimeType = URLConnection.guessContentTypeFromName(fileName);
                response.setContentType(mimeType);
                response.setHeader("Content-Disposition", String.format("inline; filename=" + fileName + ""));
                response.setContentLength((int) returnFile.length());
                InputStream inputStream = null;
                try {
                    inputStream = new BufferedInputStream(new FileInputStream(returnFile));
                    // copy byte to destination - output stream- close both streams
                    FileCopyUtils.copy(inputStream, response.getOutputStream());
                    getFile.setError(ErrorCode.SUCCESS);
                    logger.info(ChatController.class.getName() + " response file to client successfully.");
                    //inputStream.close(); //------
                } catch (FileNotFoundException e) {
                    getFile.setError(ErrorCode.INTERNAL_EXCEPTION);
                    logger.info(ChatController.class.getName() + " input stream - file not found exc");
                    e.printStackTrace();
                } catch (IOException e) {
                    getFile.setError(ErrorCode.INTERNAL_EXCEPTION);
                    logger.info(ChatController.class.getName() + " response stream fail.");
                    e.printStackTrace();
                }
            }
            return getFile.toJsonString();
        }
    }

    /*
     * edit text messages
     * must send to server type of msg
     * edit on db
     * then edit on chat room view - frontend
     * param: msgTime, roomLink, msgType, msg, userID
     * */

    @RequestMapping(value = "/editTxtMsg", method = RequestMethod.POST)
    public String editMsg(@RequestBody Message msg, HttpSession session, HttpServletResponse response) {
        if (session.getAttribute("userID") == null) {
            // send to login
            Response rsp = new Response();
            try {
                response.sendRedirect("/login");
                rsp.setError(ErrorCode.UNAUTHORIZED);
                logger.info(MainController.class.getName() + " editTxtMsg redirect to login");
            } catch (IOException e) {
                rsp.setError(ErrorCode.INTERNAL_EXCEPTION);
                logger.info(MainController.class.getName() + " /editTxtMsg io exception in response redirect");
                e.printStackTrace();
            }
            return "e: " + rsp.getError();

        } else {
            Message respMsg = new Message();
            String msgTime = msg.getCreateAt();
            String roomLink = msg.getRoomLink();
            String editMsg = msg.getMsg();
            String userID = msg.getUserID();
            int msgType = msg.getType();
            // check whether param if null
            if (msgTime == null || roomLink == null || editMsg == null || msgTime.equals("") || roomLink.equals("") || editMsg.equals("")
                    || userID == null || userID.equals("")) {
                // send error
                respMsg.setError(ErrorCode.NULL_REQUEST_PARAM);
                logger.info(ChatController.class.getName() + " null request param");
            } else {
                // check whether msgType is txt
                if (msgType != MessageType.TEXT_MSG) {
                    // send error
                    respMsg.setError(ErrorCode.NOT_TXT_MSG);
                    logger.info(ChatController.class.getName() + " msg type: not txt msg");
                } else if (msgType == MessageType.TEXT_MSG) {
                    ChatServiceDB serviceDB = new ChatServiceDBImp();
                    try {
                        ChatServiceConnect.init();//init
                        // check whether userID on session equals to userID of msg
                        if (session.getAttribute("userID") == null || !session.getAttribute("userID").equals(userID)) {
                            respMsg.setError(ErrorCode.UNAUTHORIZED);
                            logger.info(ChatController.class.getName() + " deny permissions of editing txt msg");
                        } else {
                            // edit msg on db
                            serviceDB.updateTxtMsg(roomLink, msgTime, editMsg);
                            respMsg.setError(ErrorCode.SUCCESS);
                            logger.info(ChatController.class.getName() + " edit msg in db successfully.");
                        }

                    } catch (IOException e) {
                        respMsg.setError(ErrorCode.INTERNAL_EXCEPTION);
                        logger.info(ChatController.class.getName() + " editTxtMsg: io exception");
                        e.printStackTrace();
                    } catch (SQLException e) {
                        respMsg.setError(ErrorCode.INTERNAL_EXCEPTION);
                        logger.info(ChatController.class.getName() + " editTxtMsg: sql exception");
                        e.printStackTrace();
                    }
                }
            }
            respMsg = msg;
            return respMsg.editMsgtoJsonString();
        }
    }

    /*
     * delete msg - just delete txt msg
     * param: msgTime, roomLink, msgType, userID
     * frontend must update chat room view
     * */
    @RequestMapping(value = "/deleteTxtMsg", method = RequestMethod.POST)
    public String deleteMsg(@RequestBody Message msg, HttpSession session, HttpServletResponse response) {
        if (session.getAttribute("userID") == null) {
            // send to login
            Response rsp = new Response();
            try {
                response.sendRedirect("/login");
                rsp.setError(ErrorCode.UNAUTHORIZED);
                logger.info(MainController.class.getName() + " deleteTxtMsg redirect to login");
            } catch (IOException e) {
                rsp.setError(ErrorCode.INTERNAL_EXCEPTION);
                logger.info(MainController.class.getName() + " /deleteTxtMsg io exception in response redirect");
                e.printStackTrace();
            }
            return "e: " + rsp.getError();

        } else {
            Message respMsg = new Message();
            String msgTime = msg.getCreateAt();
            String roomLink = msg.getRoomLink();
            String userID = msg.getUserID();
            int msgType = msg.getType();
            // check whether param if null
            if (msgTime == null || roomLink == null || msgTime.equals("") || roomLink.equals("")
                    || userID == null || userID.equals("")) {
                // send error
                respMsg.setError(ErrorCode.NULL_REQUEST_PARAM);
                logger.info(ChatController.class.getName() + " null request param");
            } else {
                // check whether msgType is txt
                if (msgType != MessageType.TEXT_MSG) {
                    // send error
                    respMsg.setError(ErrorCode.NOT_TXT_MSG);
                    logger.info(ChatController.class.getName() + " msg type: not txt msg");
                } else if (msgType == MessageType.TEXT_MSG) {
                    ChatServiceDB serviceDB = new ChatServiceDBImp();
                    try {
                        ChatServiceConnect.init(); // init
                        // check whether userID on session equals to userID of msg
                        if (session.getAttribute("userID") == null || !session.getAttribute("userID").equals(userID)) {
                            respMsg.setError(ErrorCode.UNAUTHORIZED);
                            logger.info(ChatController.class.getName() + " deny permissions of deleting txt msg");
                        } else {
                            // delete msg on db
                            serviceDB.deleteTxtMsg(roomLink, msgTime);
                            respMsg.setError(ErrorCode.SUCCESS);
                            logger.info(ChatController.class.getName() + " delete msg in db successfully.");
                        }

                    } catch (IOException e) {
                        respMsg.setError(ErrorCode.INTERNAL_EXCEPTION);
                        logger.info(ChatController.class.getName() + " deleteTxtMsg: io exception");
                        e.printStackTrace();
                    } catch (SQLException e) {
                        respMsg.setError(ErrorCode.INTERNAL_EXCEPTION);
                        logger.info(ChatController.class.getName() + " deleteTxtMsg: sql exception");
                        e.printStackTrace();
                    }
                }
            }
            respMsg = msg;
            return respMsg.deleteMsgtoJsonString();
        }
    }
}
