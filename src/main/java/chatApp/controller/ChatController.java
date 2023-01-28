package chatApp.controller;

import chatApp.customEntities.CustomResponse;
import chatApp.customEntities.UserDTO;
import chatApp.entities.Message;
import chatApp.service.MessageService;
import chatApp.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.*;


import java.util.List;

import static chatApp.utilities.messages.LoggerMessages.*;
import static chatApp.utilities.messages.SuccessMessages.*;
import static chatApp.utilities.Utility.*;

@RestController
@CrossOrigin
@RequestMapping("/chat")
public class ChatController {
    private static final Logger logger = LogManager.getLogger(ChatController.class.getName());

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;

    /**
     * sends the message to the addMessageToMainChat method in the messageService
     *
     * @param message - the message's data
     * @return a saved message
     */
    @MessageMapping("/plain")
    @SendTo("/topic/mainChat")
    public ResponseEntity<CustomResponse<Message>> sendMainPlainMessage(Message message) {
        CustomResponse<Message> response = new CustomResponse<>(null, emptyString);
        try {
            logger.info(beforeSendMessageInMain);
            Message mainMessage = messageService.addMessageToMainChat(message);
            response.setResponse(mainMessage);
            response.setMessage(mainMessageSentSuccessfully);
            logger.info(mainMessageSentSuccessfully);
            return ResponseEntity.ok().body(response);
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            response.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * sends the message to the addMessageToPrivateChat method in the messageService
     *
     * @param message - the message's data
     * @return a saved message
     */
    @MessageMapping("/plain/privatechat/{roomId}")
    @SendTo("/topic/privatechat/{roomId}")
    public ResponseEntity<CustomResponse<Message>> sendPrivatePlainMessage(Message message) {
        CustomResponse<Message> response = new CustomResponse<>(null, emptyString);
        try {
            logger.info(beforeSendPrivateMessage);
            Message privateMessage = messageService.addMessageToPrivateChat(message);
            response.setResponse(privateMessage);
            response.setMessage(privateMessageSentSuccessfully);
            logger.info(privateMessageSentSuccessfully);
            return ResponseEntity.ok().body(response);
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            response.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * calling the getAllUsers method in the userService
     *
     * @return list of all users
     */
    @RequestMapping(value = "/getusers", method = RequestMethod.GET)
    public ResponseEntity<CustomResponse<List<UserDTO>>> getAllUsers() {
        logger.info(beforeGettingAllUsers);
        CustomResponse<List<UserDTO>> response = new CustomResponse<>(UserDTO.userListToUserListDTO(userService.getAllUsers()), listOfAllUsersSuccessfulMessage);
        logger.info(listOfAllUsersSuccessfulMessage);
        return ResponseEntity.ok().body(response);
    }

    /**
     * sends the senderEmail, receiverId to the getPrivateRoomMessages method in the messageService
     *
     * @param senderEmail - the Email of the sender
     * @param receiverId  - the id of the receiver
     * @return list of messages of private chat room
     */
    @RequestMapping(value = "/privatechatroom", method = RequestMethod.GET)
    public ResponseEntity<CustomResponse<List<Message>>> getPrivateRoom(@RequestParam("sender") String senderEmail, @RequestParam("receiver") Long receiverId) {
        CustomResponse<List<Message>> response = new CustomResponse<>(null, emptyString);
        try {
            logger.info(beforeGettingPrivateRoomMessages);
            List<Message> privateRoomMessages = messageService.getPrivateRoomMessages(senderEmail, receiverId);
            response.setResponse(privateRoomMessages);
            response.setMessage(privateChatRoomMessagesSentSuccessfully);
            logger.info(privateChatRoomMessagesSentSuccessfully);
            return ResponseEntity.ok().body(response);
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            response.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * sends the size to the getMainRoomMessages method in the messageService
     *
     * @param size - the number of returned messages
     * @return list of messages of main chat room
     */
    @RequestMapping(value = "/mainchatroom", method = RequestMethod.GET)
    public ResponseEntity<CustomResponse<List<Message>>> getMainRoom(@RequestParam("size") int size) {
        CustomResponse<List<Message>> response = new CustomResponse<>(null, emptyString);
        try {
            logger.info(beforeGettingMainRoomMessages);
            List<Message> mainRoomMessages = messageService.getMainRoomMessages(size);
            response.setResponse(mainRoomMessages);
            response.setMessage(mainChatRoomMessagesSentSuccessfully);
            logger.info(mainChatRoomMessagesSentSuccessfully);
            return ResponseEntity.ok().body(response);
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            response.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * sends the roomId to the downloadPrivateRoomMessages method in the messageService
     *
     * @param roomId - the room id
     * @return list of messages of specific private chat room
     */
    @RequestMapping(value = "/downloadprivatechatroom", method = RequestMethod.GET)
    public ResponseEntity<CustomResponse<List<Message>>> downloadPrivateRoom(@RequestParam("roomId") String roomId) {
        CustomResponse<List<Message>> response = new CustomResponse<>(null, emptyString);
        try {
            logger.info(beforeDownloadingPrivateRoom);
            List<Message> downloadPrivateRoom = messageService.downloadPrivateRoomMessages(roomId);
            response.setResponse(downloadPrivateRoom);
            response.setMessage(downloadPrivateRoomSentSuccessfully);
            logger.info(downloadPrivateRoomSentSuccessfully);
            return ResponseEntity.ok().body(response);
        } catch (IllegalArgumentException e) {
            logger.error(e.getMessage());
            response.setMessage(e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * sends the time in epoch-seconds to the downloadMainRoom method in the messageService
     *
     * @param time - the LocalDateTime.now() in epoch seconds
     * @return list of messages of specific main chat room from that time till now
     */
    @RequestMapping(value = "/downloadmainchatroom", method = RequestMethod.GET)
    public ResponseEntity<CustomResponse<List<Message>>> downloadMainRoom(@RequestParam("time") long time) {
        logger.info(beforeDownloadingMainRoom);
        CustomResponse<List<Message>> response = new CustomResponse<>(messageService.getMainRoomMessagesByTime(time), downloadMainRoomSentSuccessfully);
        logger.info(downloadMainRoomSentSuccessfully);
        return ResponseEntity.ok().body(response);
    }
}