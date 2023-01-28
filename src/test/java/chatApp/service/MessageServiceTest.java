package chatApp.service;

import chatApp.entities.Message;
import chatApp.entities.User;
import chatApp.repository.MessageRepository;
import chatApp.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.sql.SQLDataException;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(SpringExtension.class)
@SpringBootTest
class MessageServiceTest {

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
        this.userSender.setPassword("Aa12345");
        authService.login(this.userSender);
        User userTwo = User.createUser("elisamer", "seselevtion@gmail.com", "Aa12345");
        this.userReceiver  = User.dbUser(authService.addUser(userTwo));
        Message mainMsg = new Message("samerelishai@gmail.com", "hello main content", "main", "0");
        this.mainMessage = Message.MainChatMessageFactory(messageService.addMessageToMainChat(mainMsg));
        Message privateMsg = new Message("samerelishai@gmail.com", "hello elisamer content", "seselevtion@gmail.com", userSender.getId() + "E" + userReceiver.getId());
        this.privateMessage = Message.PrivateChatMessageFactory(messageService.addMessageToPrivateChat(privateMsg));
    }


    @AfterEach
    public void deleteAllTables(){
        userRepository.deleteAll();
        messageRepository.deleteAll();
    }

    @Test
    void getPrivateRoomMessages_checkRoomIdMessagesAreFromTheSameRoom_true() {
        List<Message> messages = messageService.getPrivateRoomMessages(userSender.getEmail(), userRepository.findByEmail(userReceiver.getEmail()).getId());
        assertTrue(messages.stream().allMatch(message -> Objects.equals(message.getRoomId(), privateMessage.getRoomId())));
    }

    @Test
    void addMessageToMainChat_roomIdNull_throwIllegalArgument() {
        privateMessage.setRoomId(null);
        assertThrows(IllegalArgumentException.class, () -> messageService.addMessageToMainChat(privateMessage));
    }

    @Test
    void getPrivateRoomMessages_roomIdExistsInTheOppositeWay_NotEquals() {
        List<Message> messages = messageService.getPrivateRoomMessages(userReceiver.getEmail(), userRepository.findByEmail(userReceiver.getEmail()).getId());
        assertNotEquals(messages, messageRepository.findByRoomId(userReceiver.getId() + "E" + userRepository.findByEmail(userReceiver.getEmail()).getId()));
    }

    @Test
    void getPrivateRoomMessages_roomIdDosentExits_equals() {
        User newUser = User.createUser("new", "new123@gmail.com", "Aa12345");
        User dbuser = User.dbUser(authService.addUser(newUser));
        List<Message> messages = messageService.getPrivateRoomMessages(userReceiver.getEmail(), dbuser.getId());
        assertEquals(messages.get(0).getContent(), "New Private Chat Room");
    }

    @Test
    void getPrivateRoomMessages_receiverIdDosentExists_throwsIllegalArgument() {
        assertThrows(IllegalArgumentException.class, () -> messageService.getPrivateRoomMessages(userSender.getEmail(), 5000L));
    }


    @Test
    void addMessageToPrivateChat_roomIdEqualsToRetrievedMessageRoomId_equals() {
        privateMessage.setContent("check");
        Message message = messageService.addMessageToPrivateChat(privateMessage);
        assertEquals(message.getRoomId(), messageRepository.findByContent("check").get(0).getRoomId());
    }

    @Test
    void downloadPrivateRoomMessages_checkIfRoomIdExists_notNull() {
        assertNotNull(messageService.downloadPrivateRoomMessages(privateMessage.getRoomId()));
    }

    @Test
    void addMessageToMainChat_checkIfTheReceiverIsMainChat_equals(){
        Message newMessage = new Message("samerelishai@gmail.com", "hello main content", null, "0");
        Message message = messageService.addMessageToMainChat(newMessage);
        assertEquals(message.getReceiver(), "main");
    }

    @Test
    void addMessageToMainChat_userIsMuted_throwsIllegealArgumentException(){
        userSender.setMute(true);
        userRepository.save(userSender);
        Message newMessage = new Message("samerelishai@gmail.com", "hello main content", null, "0");
        assertThrows(IllegalArgumentException.class, () ->{messageService.addMessageToMainChat(newMessage);} );
    }


    @Test
    void getMainRoomMessages_getOneMessage_equals() {
        List<Message> messages = messageService.getMainRoomMessages(1);
        assertEquals(messages.size(), 1);
    }

    @Test
    void getMainRoomMessages_getMinusOneMessage_throwsIllegalException() {
        assertThrows(IllegalArgumentException.class, () -> messageService.getMainRoomMessages(-1));
    }


    @Test
    void getMainRoomMessagesByTime_checkMessagesByTimeNotFromMainChatRoom_equalsZero(){
        List<Message> messages = messageService.getMainRoomMessagesByTime(mainMessage.getIssueDateEpoch() - 1);
        assertEquals(0, messages.stream().filter(msg -> !Objects.equals(msg.getRoomId(), "0")).count());
    }
}