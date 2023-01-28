package chatApp.service;

import static chatApp.utilities.messages.ExceptionMessages.*;
import static chatApp.utilities.Utility.*;
import static chatApp.utilities.messages.LoggerMessages.*;

import chatApp.entities.Message;
import chatApp.entities.User;
import chatApp.entities.UserStatuses;
import chatApp.entities.UserType;
import chatApp.repository.MessageRepository;
import chatApp.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.NestedRuntimeException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin
@Service
public class UserService {
    private static final Logger logger = LogManager.getLogger(UserService.class.getName());

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MessageRepository messageRepository;


    /**
     * Update user : check if data is valid syntax & the user exist in DB, update user data in DB
     *
     * @param user  - the user's data
     * @return user with updated data
     * @throws IllegalArgumentException when the Update user failed
     */
    public User updateUser(User user, String userEmail) {
        try {
            String oldEmail = emptyString;
            String oldNickname = emptyString;
            logger.debug(checkIfExistsAlready);
            User dbUser = User.dbUser(userRepository.findByEmail(userEmail));
            logger.info(update);
            if (!user.getEmail().equals(emptyString)) {
                oldEmail = dbUser.getEmail();
                if (dbUser.getNickname().equals(dbUser.getEmail())) {
                    oldNickname = dbUser.getEmail();
                    dbUser.setNickname(user.getEmail());
                }
                dbUser.setEmail(user.getEmail());
                logger.info(updateEmail);
            }
            if (user.getNickname() != null && !user.getNickname().equals(emptyString)) {
                oldNickname = dbUser.getNickname();
                dbUser.setNickname(user.getNickname());
                logger.info(updateNickname);
            }
            if (user.getName() != null && !user.getName().equals(emptyString)) {
                dbUser.setName(user.getName());
                logger.info(updateName);

            }
            if (user.getPassword() != null && !user.getPassword().equals(emptyString)) {
                dbUser.setPassword(encrypt(user.getPassword()));
                logger.info(updatePassword);
            }
            if (user.getDateOfBirth() != null) {
                if (user.getDateOfBirth().isAfter(LocalDate.now())) {
                    throw new IllegalArgumentException(updateUserFailedMessage + invalidDateMessage);
                }
                dbUser.setDateOfBirth(user.getDateOfBirth());
                dbUser.setAge(calcAge(user.getDateOfBirth()));
                logger.info(updateDateOfBirthAndAge);
            }
            if (user.getPhoto() != null && !user.getPhoto().equals(emptyString)) {
                dbUser.setPhoto(user.getPhoto());
                logger.info(updatePhoto);
            }
            if (user.getDescription() != null && !user.getDescription().equals(emptyString)) {
                dbUser.setDescription(user.getDescription());
                logger.info(updateDescription);
            }
            logger.info(saveInDB);
            User returnUser =  User.dbUser(userRepository.save(dbUser));
            if(!oldEmail.equals(emptyString)){
                updateUserMessages(oldEmail, user.getEmail());
            }
            if(!oldNickname.equals(emptyString)){
                if(!user.getNickname().equals(emptyString)) {
                    updateUserMessages(oldNickname, user.getNickname());
                }
            }
            return returnUser;
        } catch (NestedRuntimeException e){
                throw new IllegalArgumentException(emailExistsInSystemMessage(user.getEmail()));
        }
        catch (RuntimeException e) {
            logger.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Logout user : delete token & change status to offline, if the user is guest delete him from the DB
     *
     * @param userEmail - the userEmail gets by the token
     * @return user with offline status
     * @throws IllegalArgumentException when the logout user failed
     */
    public User logoutUser(String userEmail) {
        try {
            logger.info(userLogout);
            User user = userRepository.findByEmail(userEmail);
            if (user == null) {
                logger.error(emailNotExistsMessage);
                throw new IllegalArgumentException(emailNotExistsMessage);
            }
            User dbUser = User.dbUser(user);
            logger.debug(deleteGuest);
            if (dbUser.getType().equals(UserType.GUEST) && dbUser.getEmail().contains(systemEmail)) {
                userRepository.delete(dbUser);
                return dbUser;
            }
            dbUser.setUserStatus(UserStatuses.OFFLINE);
            return userRepository.save(dbUser);
        } catch (RuntimeException e) {
            logger.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Update Mute/unmute Users : check token session not expired & the user exist in DB, update user mute/unmute status in DB
     *
     * @param adminEmail - the userEmail from the token hash
     * @param userToMuteId    - the id of the user
     * @return user with mute/unmute status
     * @throws IllegalArgumentException when the update mute/unmute user failed
     */
    public User updateMuteUnmuteUser(Long userToMuteId, String adminEmail) {
        try {
            logger.info(beforeMuteUnmute);
            if (userRepository.findByEmail(adminEmail).getType() != UserType.ADMIN) {
                logger.error(notAdminUser);
                throw new IllegalArgumentException(notAdminUser);
            }
            if (!userRepository.findById(userToMuteId).isPresent()) {
                throw new IllegalArgumentException(emailNotExistsMessage(adminEmail));
            }
            User dbUser = User.dbUser(userRepository.findById(userToMuteId).get());
            dbUser.setMute(!dbUser.isMute());
            logger.info(toggledMute);
            return userRepository.save(dbUser);
        } catch (RuntimeException e) {
            logger.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Update away/online Users : check token session not expired & the user exist in DB, update user away/online status in DB
     *
     * @param userEmail  - the userEmail from the token hash
     * @param status - the away/online status of the user
     * @return user with away/online status
     * @throws IllegalArgumentException when the update away/online status user failed
     */
    public User updateStatusUser(String userEmail, String status) {
        try {
            logger.info(beforeUpdateStatus);
            User user = userRepository.findByEmail(userEmail);
            if (user == null) {
                throw new IllegalArgumentException(emailNotExistsMessage(userEmail));
            }
            User dbUser = User.dbUser(user);
            if (status.equals(UserStatuses.AWAY.name().toLowerCase())) {
                logger.info(UserStatuses.AWAY.name().toLowerCase());
                dbUser.setUserStatus(UserStatuses.AWAY);
            } else if (status.equals(UserStatuses.ONLINE.name().toLowerCase())) {
                logger.info(UserStatuses.ONLINE.name().toLowerCase());
                dbUser.setUserStatus(UserStatuses.ONLINE);
            }
            return userRepository.save(dbUser);
        } catch (RuntimeException e) {
            logger.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Get all users: get all users from DB
     *
     * @return all the users sorted by theirs types [ADMIN(0), REGISTERED(1), GUEST(2)] from DB
     */
    public List<User> getAllUsers() {
        logger.info(getAllUsers);
        return userRepository.findAll().stream().filter(currUser -> currUser.getUserStatus() != UserStatuses.OFFLINE).sorted(Comparator.comparing(User::getType)).collect(Collectors.toList());
    }


    /**
     * Update user nickname messages by sender and receiver
     * @param oldNickname - previous user email
     * @param newNickname - new user email
     */
    public void updateUserMessages(String oldNickname, String newNickname) {
        logger.info(updatingNewNicknameInOldMessages);
        List<Message> senderMessages = messageRepository.findBySender(oldNickname);
        List<Message> newSenderMessages = senderMessages.stream().filter(message -> message.getSender().equals(oldNickname)).collect(Collectors.toList());
        newSenderMessages.forEach(message -> message.setSender(newNickname));
        newSenderMessages.forEach(message -> messageRepository.save(message));

        List<Message> receiverMessages = messageRepository.findByReceiver(oldNickname);
        List<Message> newReceiverMessages = receiverMessages.stream().filter(message -> message.getReceiver().equals(oldNickname)).collect(Collectors.toList());
        newReceiverMessages.forEach(message -> message.setReceiver(newNickname));
        newReceiverMessages.forEach(message -> messageRepository.save(message));
    }
}

