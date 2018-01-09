package com.vimensa.chat.controller;

import com.vimensa.chat.config.ChatServiceConnect;
import com.vimensa.chat.dao.User;
import com.vimensa.chat.model.*;
import com.vimensa.chat.security.Authentication;
import com.vimensa.chat.service.ChatServiceDB;
import com.vimensa.chat.service.CommonChatService;
import com.vimensa.chat.service.CommonUserService;
import com.vimensa.chat.service.imp.ChatServiceDBImp;
import com.vimensa.chat.service.imp.CommonChatServiceImp;
import com.vimensa.chat.service.imp.CommonUserServiceImp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Vector;

@RestController
public class MainController {
    private static Logger logger = LoggerFactory.getLogger(MainController.class);

    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)
    public String checkToken(HttpSession session,
                             @RequestHeader(value = "at") String token, HttpServletResponse response) {
        // call authentication to call /token from user-service
        Authen authen = Authentication.checkToken(token, session);
        // if active account then return to list of chat-box
        // else return to login page-get
        if (authen.getStatus() == ErrorCode.ACTIVE_TOKEN || authen.getStatus() == ErrorCode.UPDATE_TOKEN) {
            try {
                response.sendRedirect("/getRoomLi");
                authen.setStatus(ErrorCode.SUCCESS);
                logger.info(MainController.class.getName() + " /---get list of room");
            } catch (IOException e) {
                authen.setStatus(ErrorCode.INTERNAL_EXCEPTION);
                e.printStackTrace();
                logger.info(MainController.class.getName() + " /---io exception in response redirect");
            }
        } else {
            try {
                response.sendRedirect("/login");
                authen.setStatus(ErrorCode.UNAUTHORIZED);
                logger.info(MainController.class.getName() + " redirect to login");
            } catch (IOException e) {
                authen.setStatus(ErrorCode.INTERNAL_EXCEPTION);
                logger.info(MainController.class.getName() + " /---io exception in response redirect");
                e.printStackTrace();
            }
        }
        return "e:" + authen.getStatus();

    }

    @GetMapping("/login")
    public void login() {
    }

    /*
     * find accounts - to add members in group
     * call each time user type a letter
     * 1. load list of all users on hazel or DB
     *  limit search results by match each letter from left ro right
     *  return userNick - ava - userID
     *
     * */
    @RequestMapping(value = "/findUserAccount", method = RequestMethod.POST)
    public String findUserAccount(@RequestBody FindUser reqWord, @RequestHeader(value = "at") String token,
                                  HttpSession session, HttpServletResponse response) {

        FindUser findUser = new FindUser();
        if (session.getAttribute("userID") == null) {
            // send to login
            try {
                response.sendRedirect("/login");
                findUser.setError(ErrorCode.UNAUTHORIZED);
                logger.info(MainController.class.getName() + " findUserAccount redirect to login");
            } catch (IOException e) {
                findUser.setError(ErrorCode.INTERNAL_EXCEPTION);
                logger.info(MainController.class.getName() + " /findUserAccount io exception in response redirect");
                e.printStackTrace();
            }
            return "e:" + findUser.getError();

        } else {
            String word = reqWord.getWord(); // get input word
            // get list of users from hazel or DB
            CommonUserService userService = new CommonUserServiceImp();
            Vector<User> userLi = null;
            try {
                ChatServiceConnect.init(); // init
                userLi = userService.getAllUsers();
                findUser.setError(ErrorCode.SUCCESS);
                logger.info(MainController.class.getName() + " findUserAccount get userLi successfully");
            } catch (IOException e) {
                findUser.setError(ErrorCode.INTERNAL_EXCEPTION);
                logger.info(MainController.class.getName() + "findUserAccount io exception");
                e.printStackTrace();
            } catch (SQLException e) {
                findUser.setError(ErrorCode.INTERNAL_EXCEPTION);
                logger.info(MainController.class.getName() + "findUserAccount sq1 exception");
                e.printStackTrace();
            }
            findUser = new FindUser(word, userLi);
            return findUser.toJsonString(findUser.findUsers());
        }

    }

    /*
     * add user in group
     * added user is allowed to see history of chat room
     * return: send a link to access in group chat
     *         send back a list of old msgs in group
     *         security: permit to access in group
     * param: roomLink, list of userIDs in form userID1_userID2_..., userID
     * only members in room have permission to add other user in room chat
     * write permission on session - maybe
     * */
    @RequestMapping(value = "/addUserInGroup", method = RequestMethod.POST)
    public String addUserInGroup(@RequestBody AddUser addUser, HttpSession session, HttpServletResponse response) {
        AddUser respAddUser = new AddUser();
        if (session.getAttribute("userID") == null) {
            // send to login
            try {
                response.sendRedirect("/login");
                respAddUser.setError(ErrorCode.UNAUTHORIZED);
                logger.info(MainController.class.getName() + " addUserInGroup redirect to login");
            } catch (IOException e) {
                respAddUser.setError(ErrorCode.INTERNAL_EXCEPTION);
                logger.info(MainController.class.getName() + " /addUserInGroup io exception in response redirect");
                e.printStackTrace();
            }

        } else {
            CommonChatService chatService = new CommonChatServiceImp();
            // check null param
            if (addUser.getRoomLink() == null || addUser.getRoomLink().equals("")
                    || addUser.getUserIDLi() == null || addUser.getUserIDLi().equals("")) {
                respAddUser.setError(ErrorCode.NULL_REQUEST_PARAM);
                logger.info(MainController.class.getName() + " null request param");
            } else {
                // check permissions in room - whether a member in room chat
                // check on db - get userID by roomLink from room_user table
                try {
                    ChatServiceConnect.init(); // init
                    boolean checkPermission = chatService.isPermittedInRoom(addUser.getUserID(), addUser.getRoomLink());
                    if (!checkPermission) {
                        respAddUser.setError(ErrorCode.DENY_ROOM_PERMISSION);
                        logger.info(MainController.class.getName() + " deny permission in room chat");
                    } else {
                        // write on db
                        try {
                            chatService.addUserIDsInRoom(chatService.getListUserID(addUser.getUserIDLi()), addUser.getRoomLink());
                            respAddUser.setError(ErrorCode.SUCCESS);
                            logger.info(MainController.class.getName() + " add more users in room_user table successfully");
                        } catch (IOException e) {
                            respAddUser.setError(ErrorCode.INTERNAL_EXCEPTION);
                            logger.info(MainController.class.getName() + " add more user in exist room io exception");
                            e.printStackTrace();
                        } catch (SQLException e) {
                            respAddUser.setError(ErrorCode.INTERNAL_EXCEPTION);
                            logger.info(MainController.class.getName() + " add more user in exist room sql exception");
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    respAddUser.setError(ErrorCode.INTERNAL_EXCEPTION);
                    logger.info(MainController.class.getName() + " check permission fail due to io exception");
                    e.printStackTrace();
                } catch (SQLException e) {
                    respAddUser.setError(ErrorCode.INTERNAL_EXCEPTION);
                    logger.info(MainController.class.getName() + " check permission fail due to sql exception");
                    e.printStackTrace();
                }

            }

        }
        return "e:" + respAddUser.getError();

    }

    /*
     * remove one user from a group
     * only room chat creator has this permission
     * find room creator in chat_room table
     * param: userID, removUserID, roomLink
     * */
    @RequestMapping(value = "/removeUserFromRoom", method = RequestMethod.POST)
    public String removeUserFromRoom(@RequestBody RemoveUser removUser, HttpServletResponse response, HttpSession session) {
        RemoveUser respRemovUser = new RemoveUser();
        if (session.getAttribute("userID") == null) {
            // send to login
            try {
                response.sendRedirect("/login");
                respRemovUser.setError(ErrorCode.UNAUTHORIZED);
                logger.info(MainController.class.getName() + " removeUserFromRoom redirect to login");
            } catch (IOException e) {
                respRemovUser.setError(ErrorCode.INTERNAL_EXCEPTION);
                logger.info(MainController.class.getName() + " /removeUserFromRoom io exception in response redirect");
                e.printStackTrace();
            }

        } else {
            ChatServiceDB serviceDB = new ChatServiceDBImp();
            // check null param
            if (removUser.getRoomLink() == null || removUser.getRoomLink().equals("")
                    || removUser.getRemovUserID() == null || removUser.getRemovUserID().equals("")
                    || removUser.getUserID() == null || removUser.getUserID().equals("")) {
                respRemovUser.setError(ErrorCode.NULL_REQUEST_PARAM);
                logger.info(MainController.class.getName() + " null request param");
            } else {
                // check permissions in room - whether user is the creator of the room chat
                // check search for creator from db
                try {
                    ChatServiceConnect.init(); // init
                    String creator = serviceDB.getCreatorOfRoom(removUser.getRoomLink());
                    if (creator == null || !creator.equals(removUser.getUserID())) {
                        respRemovUser.setError(ErrorCode.DENY_ROOM_PERMISSION);
                        logger.info(MainController.class.getName() + " not creator of this room");
                    } else {
                        // write on db
                        try {
                            serviceDB.deleteUserFromRoom(removUser.getRemovUserID(), removUser.getRoomLink()); // whole sql - no row affected
                            respRemovUser.setError(ErrorCode.SUCCESS);
                            logger.info(MainController.class.getName() + " remove user from room_user table successfully");
                        } catch (SQLException e) {
                            respRemovUser.setError(ErrorCode.INTERNAL_EXCEPTION);
                            logger.info(MainController.class.getName() + " remove user from room sql exception");
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    respRemovUser.setError(ErrorCode.INTERNAL_EXCEPTION);
                    logger.info(MainController.class.getName() + " check permission fail due to io exception");
                    e.printStackTrace();
                } catch (SQLException e) {
                    respRemovUser.setError(ErrorCode.INTERNAL_EXCEPTION);
                    logger.info(MainController.class.getName() + " check permission fail due to sql exception");
                    e.printStackTrace();
                }

            }
        }
        return "e:" + respRemovUser.getError();
    }

    /*
     * user is out of room chat
     * check permission - whether user is in room - userID = userID-db
     * remove userID from room_user table
     * param: userID,roomLink
     * */
    @RequestMapping(value = "/userOut", method = RequestMethod.POST)
    public String userOut(@RequestBody RemoveUser userOut, HttpSession session, HttpServletResponse response) {
        RemoveUser respUserOut = new RemoveUser();
        if (session.getAttribute("userID") == null) {
            // send to login
            try {
                response.sendRedirect("/login");
                respUserOut.setError(ErrorCode.UNAUTHORIZED);
                logger.info(MainController.class.getName() + " userOut redirect to login");
            } catch (IOException e) {
                respUserOut.setError(ErrorCode.INTERNAL_EXCEPTION);
                logger.info(MainController.class.getName() + " /userOut io exception in response redirect");
                e.printStackTrace();
            }

        } else {
            ChatServiceDB serviceDB = new ChatServiceDBImp();
            CommonChatService chatService = new CommonChatServiceImp();
            // check null param
            if (userOut.getRoomLink() == null || userOut.getRoomLink().equals("")
                    || userOut.getUserID() == null || userOut.getUserID().equals("")) {
                respUserOut.setError(ErrorCode.NULL_REQUEST_PARAM);
                logger.info(MainController.class.getName() + " null request param");
            } else {
                // check permissions - whether user is in room chat and userID db equals to userID on session
                try {
                    ChatServiceConnect.init(); // init
                    boolean checkPermission = chatService.isPermittedInRoom(userOut.getUserID(), userOut.getRoomLink());
                    if (session.getAttribute("userID") == null || !checkPermission
                            || !session.getAttribute("userID").equals(userOut.getUserID())) {
                        respUserOut.setError(ErrorCode.DENY_ROOM_PERMISSION);
                        logger.info(MainController.class.getName() + " deny of getting out of chat room permission.");
                    } else {
                        // write on db
                        try {
                            serviceDB.deleteUserFromRoom(userOut.getRemovUserID(), userOut.getRoomLink()); // whole sql - no row affected
                            respUserOut.setError(ErrorCode.SUCCESS);
                            logger.info(MainController.class.getName() + " remove user from room_user table successfully");
                        } catch (SQLException e) {
                            respUserOut.setError(ErrorCode.INTERNAL_EXCEPTION);
                            logger.info(MainController.class.getName() + " remove user from room sql exception");
                            e.printStackTrace();
                        }
                    }
                } catch (IOException e) {
                    respUserOut.setError(ErrorCode.INTERNAL_EXCEPTION);
                    logger.info(MainController.class.getName() + " check permission fail due to io exception");
                    e.printStackTrace();
                } catch (SQLException e) {
                    respUserOut.setError(ErrorCode.INTERNAL_EXCEPTION);
                    logger.info(MainController.class.getName() + " check permission fail due to sql exception");
                    e.printStackTrace();
                }

            }
        }
        return "e:" + respUserOut.getError();
    }

    /*
     * get list of rooms of user by userId - room_user table
     * param: userID- get from session
     * */
    @RequestMapping(value = "/getRoomLi", method = RequestMethod.GET)
    public String getRoomLi(HttpSession session, HttpServletResponse response) {
        RoomList manageRoom = new RoomList();
        if (session.getAttribute("userID") == null) {
            // send to login
            try {
                response.sendRedirect("/login");
                manageRoom.setError(ErrorCode.UNAUTHORIZED);
                logger.info(MainController.class.getName() + " getRoomLi redirect to login");
            } catch (IOException e) {
                manageRoom.setError(ErrorCode.INTERNAL_EXCEPTION);
                logger.info(MainController.class.getName() + " /getRoomLi io exception in response redirect");
                e.printStackTrace();
            }
            return "e: " + manageRoom.getError();

        } else {
            String userID = (String) session.getAttribute("userID");
            manageRoom.setUserID(userID);
            if (userID == null) { ///////////////-------------HERE
                manageRoom.setError(ErrorCode.UNAUTHORIZED);
                logger.info(MainController.class.getName() + " user is out of session");
            } else {
                try {
                    ChatServiceConnect.init();
                    ChatServiceDB serviceDB = new ChatServiceDBImp();
                    manageRoom.setRoomLi(serviceDB.getRoomLiByUserID(userID));
                    manageRoom.setError(ErrorCode.SUCCESS);
                    logger.info(MainController.class.getName() + " getRoomLi get rooms from db successfully");
                } catch (IOException e) {
                    manageRoom.setError(ErrorCode.INTERNAL_EXCEPTION);
                    logger.info(MainController.class.getName() + " getRoomLi io exception");
                    e.printStackTrace();
                } catch (SQLException e) {
                    manageRoom.setError(ErrorCode.INTERNAL_EXCEPTION);
                    logger.info(MainController.class.getName() + " getRoomLi sql exception");
                    e.printStackTrace();
                }
            }

            return manageRoom.roomLiToJsonString();
        }
    }

    //---------- security + file upload - download size exception- duplicate- just to set unique in table
    /**
     * @RequestMapping(value = "/test-hazelcast",method = RequestMethod.GET)
     * public String getRoomOnHazel(){
     * ChatServiceHazel chatService = new ChatServiceHazelImp();
     * IMap<String, ChatRoom> roomIMap = chatService.getRoomHazel();
     * List<ChatRoom> roomLi = chatService.getAllRoom(roomIMap);
     * ChatRoom room = new ChatRoom();
     * return room.listToJsonString(roomLi);
     * }
     */

    /**
     @RequestMapping(value = "/test-download", method = RequestMethod.GET)
     public String testDownloadFile(HttpServletResponse response, HttpServletRequest request) {
     // test get application path
     ServletContext context = request.getServletContext();
     String appPath = context.getRealPath("");
     logger.info("app path: " + appPath);
     return appPath;
     }
      * */

    /**
     * test download file
     *
     * @RequestMapping(value = {"","/"},method = RequestMethod.GET)
     * public String welcome(){
     * return "welcome";
     * }
     * @RequestMapping(value = "/test-downloadFile",method = RequestMethod.GET)
     * public void downloadFile(@RequestParam(value = "roomLink")String roomLink,
     * @RequestParam(value = "msgTime")String msgTime,
     * HttpServletResponse response){
     * // get file link from db then get file from folder
     * ChatServiceDB serviceDB = new ChatServiceDBImp();
     * String fileName = "";
     * try {
     * fileName = serviceDB.getFileName(msgTime, roomLink);
     * } catch (IOException e) {
     * logger.info(ChatServiceDBImp.class.getName() + " io exception");
     * e.printStackTrace();
     * } catch (SQLException e) {
     * logger.info(ChatServiceDBImp.class.getName() + " sql exception");
     * e.printStackTrace();
     * }
     * <p>
     * // check if fileName is empty
     * if(fileName==null||fileName.equals("")){
     * // set error
     * logger.info(ChatController.class.getName() + " fail to get file link from server");
     * }
     * else if (fileName!=null||!fileName.equals("")) {
     * // return file to client
     * FileProcess fileProcess = new FileProcess();
     * //get file
     * File returnFile = fileProcess.getFile(fileName);
     * logger.info(ChatController.class.getName() + " get file from server successfully.");
     * // response file to client
     * String mimeType= URLConnection.guessContentTypeFromName(fileName);
     * response.setContentType(mimeType);
     * response.setHeader("Content-Disposition", String.format("inline; filename="+fileName+""));
     * response.setContentLength((int) returnFile.length());
     * InputStream inputStream = null;
     * try {
     * inputStream = new BufferedInputStream(new FileInputStream(returnFile));
     * // copy byte to destination - output stream- close both streams
     * FileCopyUtils.copy(inputStream, response.getOutputStream());
     * logger.info(ChatController.class.getName()+" response file to client successfully.");
     * //inputStream.close(); //------
     * } catch (FileNotFoundException e) {
     * logger.info(ChatController.class.getName()+" input stream - file not found exc");
     * e.printStackTrace();
     * } catch (IOException e) {
     * logger.info(ChatController.class.getName()+" response stream fail.");
     * e.printStackTrace();
     * }
     * }
     * }
     */


}
