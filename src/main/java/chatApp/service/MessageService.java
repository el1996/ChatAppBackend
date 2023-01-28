package chatApp.service;

import chatApp.entities.Message;
import chatApp.entities.User;
import chatApp.repository.MessageRepository;
import chatApp.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.ZoneOffset;
import java.util.List;

import static chatApp.utilities.messages.ExceptionMessages.*;
import static chatApp.utilities.Utility.*;
import static chatApp.utilities.messages.LoggerMessages.*;
import static chatApp.utilities.messages.LoggerMessages.checkPrivateRoomMessage;

@CrossOrigin
@Service
public class MessageService {

    private static final Logger logger = LogManager.getLogger(MessageService.class.getName());

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MessageRepository messageRepository;

    /**
     * finding the room id by the userEmail and the receiverId combination
     *
     * @param userEmail  - user email to get the roomId
     * @param receiverId - user id to get the roomId
     * @return list of messages of specific private room
     * @throws IllegalArgumentException
     */
    public List<Message> getPrivateRoomMessages(String userEmail, Long receiverId) {
        try {
            logger.info(getPrivateRoom);
            User senderUser = User.dbUser(userRepository.findByEmail(userEmail));
            User receiverUser = User.dbUser(userRepository.findById(receiverId).get());
            Long senderId = senderUser.getId();
            logger.info(getPrivateRoom + senderUser.getNickname() + " " + receiverUser.getNickname());
            logger.info(checkPrivateRoomMessage(senderId, receiverId));
            List<Message> messageList = messageRepository.findByRoomId(senderId + separator + receiverId);
            if (messageList.isEmpty()) {
                logger.info(checkPrivateRoomMessage(receiverId, senderId));
                messageList = messageRepository.findByRoomId(receiverId + separator + senderId);
                if (messageList.isEmpty()) {
                    logger.info(createPrivateRoomMessage(senderId, receiverId));
                    messageList.add(messageRepository.save(Message.createFirstPrivateRoomMessageFactory(senderUser.getNickname(), receiverUser.getNickname(), senderId , receiverId)));
                }
            }
            return messageList;
        } catch (RuntimeException e) {
            logger.error(privateChatRoomMessagesFailed);
            throw new IllegalArgumentException(privateChatRoomMessagesFailed);
        }
    }

    /**
     * adds message to private chat room to the db
     *
     * @param message - the message`s data
     * @return saved message
     * @throws IllegalArgumentException
     */
    public Message addMessageToPrivateChat(Message message) {
        try {
            logger.info(addMessageToPrivateRoom(message.getRoomId()));
            Message messageFactory = Message.PrivateChatMessageFactory(message);
            return messageRepository.save(messageFactory);
        } catch (RuntimeException e) {
            logger.error(FailedToSendPrivateMessage);
            throw new IllegalArgumentException(FailedToSendPrivateMessage);
        }
    }

    /**
     * downloads private room messages
     *
     * @param roomId - the roomId`s data
     * @return list of messages
     * @throws IllegalArgumentException
     **/
    public List<Message> downloadPrivateRoomMessages(String roomId) {
        try {
            logger.info(downloadPrivateChat);
            return messageRepository.findByRoomId(roomId);
        } catch (RuntimeException e) {
            logger.error(downloadPrivateRoomFailed);
            throw new IllegalArgumentException(downloadPrivateRoomFailed);
        }
    }

    /**
     * adding message to db
     *
     * @param message - the message`s data
     * @return a saved message body
     * @throws IllegalArgumentException
     */
    public Message addMessageToMainChat(Message message) {
        try {
            logger.info(addMessageInMainChat);
            String userNickname = message.getSender();
            User user = User.dbUser(userRepository.findByNickname(userNickname));
            if (user.isMute()) {
                throw new IllegalArgumentException(userIsMutedMessage);
            }
            Message messageFactory = Message.MainChatMessageFactory(message);
            return messageRepository.save(messageFactory);
        } catch (RuntimeException e) {
            logger.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * find all the main chat room messages in the db
     *
     * @param size - the number of returned rows
     * @return list of messages sorted by DESC timestamp
     * @throws IllegalArgumentException
     */
    public List<Message> getMainRoomMessages(int size) {
        try {
            logger.info(getMainChatMessages);
            return messageRepository.findByRoomId(mainRoomId, PageRequest.of(0, size, Sort.Direction.DESC, userIdNameInTable));
        } catch (RuntimeException e) {
            logger.error(mainChatRoomMessagesFailed);
            throw new IllegalArgumentException(mainChatRoomMessagesFailed);
        }
    }

    /**
     * find all the main chat room messages in the db from specific time till now
     *
     * @param time - the time in epoch seconds
     * @return list of messages from that time till now
     */
    public List<Message> getMainRoomMessagesByTime(long time) {
        if(time > 0){
            return messageRepository.findByRoomIdAndIssueDateEpochBetween(mainRoomId, time, getLocalDateTimeNow().toEpochSecond(ZoneOffset.of(zoneOffsetId)));
        }
        else{
            return messageRepository.findByRoomId(mainRoomId);
        }
    }
    //invalidate cache in File FOR TESTS!!!!!!!!!!!!!!!!!!!!!!!!!!!1
}
