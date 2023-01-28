package chatApp.controller;

import chatApp.customEntities.CustomResponse;
import chatApp.customEntities.UserDTO;
import chatApp.entities.Message;
import chatApp.entities.User;
import chatApp.repository.MessageRepository;
import chatApp.repository.UserRepository;
import chatApp.service.AuthService;
import chatApp.service.MessageService;
import chatApp.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.SQLDataException;
import java.util.List;

import static chatApp.utilities.messages.ExceptionMessages.*;
import static chatApp.utilities.messages.SuccessMessages.*;
import static org.junit.jupiter.api.Assertions.*;
import static chatApp.utilities.Utility.*;


@ExtendWith(SpringExtension.class)
@SpringBootTest
class ChatControllerTest {
    @Autowired
    private ChatController chatController;
    @Autowired
    private AuthService authService;
    @Autowired
    private UserService userService;
    @Autowired
    private MessageService messageService;
    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private UserRepository userRepository;

    Message mainMessage;
    Message privateMessage;
    User userSender;
    User userReceiver;

    @BeforeEach
    void newMessage() {
        User userOne = User.createUser("shai", "samerelishai@gmail.com", "Aa12345");
        this.userSender = User.dbUser(authService.addUser(userOne));
        userSender.setPassword("Aa12345");
        authService.login(this.userSender);
        User userTwo = User.createUser("elisamer", "seselevtion@gmail.com", "Aa12345");
        this.userReceiver = User.dbUser(authService.addUser(userTwo));
        userReceiver.setPassword("Aa12345");
        Message mainMsg = new Message("samerelishai@gmail.com", "hello main content", mainRoomReceiverName, mainRoomId);
        this.mainMessage = Message.MainChatMessageFactory(messageService.addMessageToMainChat(mainMsg));
        Message privateMsg = new Message("samerelishai@gmail.com", "hello elisamer content", "seselevtion@gmail.com", userSender.getId() + separator + userReceiver.getId());
        this.privateMessage = Message.PrivateChatMessageFactory(messageService.addMessageToPrivateChat(privateMsg));
    }

    @AfterEach
    public void deleteAllTables(){
        userRepository.deleteAll();
        messageRepository.deleteAll();
    }

    @Test
    void sendMainPlainMessage_contentResponseEqualsContentFromClient_equals() {
        ResponseEntity<CustomResponse<Message>> responseMessage = chatController.sendMainPlainMessage(mainMessage);
        assertEquals(responseMessage.getBody().getResponse().getContent(), mainMessage.getContent());
    }

    @Test
    void sendMainPlainMessage_contentInDatabaseEqualsContentFromClient_equals() {
        ResponseEntity<CustomResponse<Message>> responseMessage = chatController.sendMainPlainMessage(mainMessage);
        assertEquals(responseMessage.getBody().getResponse().getContent(), messageRepository.findByContent(responseMessage.getBody().getResponse().getContent()).get(0).getContent());
    }

    @Test
    void sendMainPlainMessage_senderResponseEqualsSenderResponse_equals() {
        ResponseEntity<CustomResponse<Message>> responseMessage = chatController.sendMainPlainMessage(mainMessage);
        assertEquals(responseMessage.getBody().getResponse().getSender(), mainMessage.getSender());
    }

    @Test
    void sendMainPlainMessage_senderInDatabaseEqualsSenderResponse_equals() {
        ResponseEntity<CustomResponse<Message>> responseMessage = chatController.sendMainPlainMessage(mainMessage);
        assertEquals(responseMessage.getBody().getResponse().getSender(), messageRepository.findBySenderAndContent(responseMessage.getBody().getResponse().getSender(), responseMessage.getBody().getResponse().getContent()).get(0).getSender());
    }

    @Test
    void sendMainPlainMessage_roomIdIsZero_equals() {
        ResponseEntity<CustomResponse<Message>> responseMessage = chatController.sendMainPlainMessage(mainMessage);
        assertEquals(responseMessage.getBody().getResponse().getRoomId(), "0");
    }

    @Test
    void sendMainPlainMessage_userIsMuted_badRequest() {
        userSender.setMute(true);
        userRepository.deleteAll();
        User.dbUser(userRepository.save(userSender));
        ResponseEntity<CustomResponse<Message>> responseMessage = chatController.sendMainPlainMessage(mainMessage);
        assertEquals(userIsMutedMessage , responseMessage.getBody().getMessage());
    }


    @Test
    void sendPrivatePlainMessage_contentInDatabaseEqualsContentFromClient_equals() {
        ResponseEntity<CustomResponse<Message>> responseMessage = chatController.sendPrivatePlainMessage(privateMessage);
        assertEquals(responseMessage.getBody().getResponse().getContent(), privateMessage.getContent());
    }

    @Test
    void sendPrivatePlainMessage_senderInDatabaseEqualsSenderFromClient_equals() {
        ResponseEntity<CustomResponse<Message>> responseMessage = chatController.sendPrivatePlainMessage(privateMessage);
        assertEquals(responseMessage.getBody().getResponse().getSender(), privateMessage.getSender());
    }

    @Test
    void sendPrivatePlainMessage_receiverInDatabaseEqualsReceiverFromClient_equals() {
        ResponseEntity<CustomResponse<Message>> responseMessage = chatController.sendPrivatePlainMessage(privateMessage);
        assertEquals(responseMessage.getBody().getResponse().getReceiver(), privateMessage.getReceiver());
    }

    @Test
    void sendPrivatePlainMessage_roomIdIs1E2_equals() {
        ResponseEntity<CustomResponse<Message>> responseMessage = chatController.sendPrivatePlainMessage(privateMessage);
        assertEquals(responseMessage.getBody().getResponse().getRoomId(), privateMessage.getRoomId());
    }

    @Test
    void sendPrivatePlainMessage_roomIdNull_badRequestResponse() {
        privateMessage.setRoomId(null);
        ResponseEntity<CustomResponse<Message>> responseMessage = chatController.sendPrivatePlainMessage(privateMessage);
        assertEquals(HttpStatus.BAD_REQUEST, responseMessage.getStatusCode());
    }

    @Test
    void sendPrivatePlainMessage_roomIdIsNotZero_equals() {
        ResponseEntity<CustomResponse<Message>> responseMessage = chatController.sendPrivatePlainMessage(privateMessage);
        assertNotEquals(responseMessage.getBody().getResponse().getRoomId(), "0");
    }


    @Test
    void getAllUsers_checkIfEmpty_notEmpty() {
        ResponseEntity<CustomResponse<List<UserDTO>>> responseUsers = chatController.getAllUsers();
        assertFalse(responseUsers.getBody().getResponse().isEmpty());
    }

    @Test
    void getAllUsers__badRequestResponse() {
        ResponseEntity<CustomResponse<List<UserDTO>>> responseUsers = chatController.getAllUsers();
        assertFalse(responseUsers.getBody().getResponse().isEmpty());
    }
    @Test
    void getAllUsers_checkUserInDatabaseEqualsUserResponse_equals() {
        ResponseEntity<CustomResponse<List<UserDTO>>> responseUsers = chatController.getAllUsers();
        assertTrue(responseUsers.getBody().getResponse().get(0).equals(UserDTO.userToUserDTO(userRepository.findByEmail(userSender.getEmail()))));
    }

    @Test
    void getAllUsers_checkIfUserDTOInFirstIndexIsUserSenderDTO_equals() {
        ResponseEntity<CustomResponse<List<UserDTO>>> responseUsers = chatController.getAllUsers();
        assertEquals(responseUsers.getBody().getMessage(), listOfAllUsersSuccessfulMessage);
    }

    @Test
    void getPrivateRoom_checkIfRoomIdResponseEqualsPrivateMessageId_equals() {
        ResponseEntity<CustomResponse<List<Message>>> responseMessages = chatController.getPrivateRoom(userSender.getEmail(), userReceiver.getId());
        assertEquals(responseMessages.getBody().getResponse().get(0).getRoomId(), privateMessage.getRoomId());
    }

    @Test
    void getMainRoom_checkIfOneMessageReturnedToClient_true() {
        ResponseEntity<CustomResponse<List<Message>>> responseMessages = chatController.getMainRoom(1);
        assertEquals(1, responseMessages.getBody().getResponse().size());
    }

    @Test
    void downloadPrivateRoom_messageContentInDatabaseEqualsContentFromClient_equals() {
        ResponseEntity<CustomResponse<List<Message>>> responseMessages = chatController.downloadPrivateRoom(userSender.getId() + "E" + userReceiver.getId());
        assertEquals(responseMessages.getBody().getResponse().get(0).getContent(), privateMessage.getContent());
    }

    @Test
    void downloadPrivateRoom_roomIdInDatabaseEqualsRoomIdFromClient_equals() {
        ResponseEntity<CustomResponse<List<Message>>> responseMessages = chatController.downloadPrivateRoom(userSender.getId() + "E" + userReceiver.getId());
        assertEquals(responseMessages.getBody().getResponse().get(0).getRoomId(), privateMessage.getRoomId());
    }

    @Test
    void downloadMainRoom_roomIdInDatabaseEqualsRoomIdFromClient_equals() {
        ResponseEntity<CustomResponse<List<Message>>> responseMessages = chatController.downloadMainRoom(mainMessage.getIssueDateEpoch() - 1);
        assertEquals(responseMessages.getBody().getResponse().get(0).getRoomId(), mainMessage.getRoomId());
    }

}