package chatApp.service;

import chatApp.utilities.EmailUtilityFacade;
import chatApp.utilities.Utility;
import chatApp.entities.User;
import chatApp.entities.UserStatuses;
import chatApp.entities.UserType;
import chatApp.repository.UserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import static chatApp.utilities.messages.ExceptionMessages.*;
import static chatApp.utilities.Utility.*;
import static chatApp.utilities.messages.LoggerMessages.*;

import org.springframework.web.bind.annotation.CrossOrigin;


@CrossOrigin
@Service
public class AuthService {

    private static final Logger logger = LogManager.getLogger(AuthService.class.getName());
    @Autowired
    private UserRepository userRepository;


    private Map<String, String> keyTokensValEmails;
    private Map<String, String> keyEmailsValTokens;

    /**
     * AuthService constructor
     * Initializes keyTokensValEmails new Map
     * Initializes keyEmailsValTokens new Map
     */
    AuthService() {
        this.keyTokensValEmails = getTokensInstance();
        this.keyEmailsValTokens = getEmailsInstance();
    }

    /**
     * Adds a user crypt password to the database if the user`s email exist in the db
     *
     * @param user - the user's data
     * @return a saved user
     * @throws IllegalArgumentException when the provided email not exists in the database
     */
    public User login(User user) {
        try {
            logger.debug(checkIfExistsAlready);
            if (userRepository.findByEmail(user.getEmail()) == null) {
                logger.error(loginFailedMessage);
                throw new IllegalArgumentException(loginFailedMessage);
            }
            User dbUser = User.dbUser(userRepository.findByEmail(user.getEmail()));

            logger.debug(checkPassword);
            BCryptPasswordEncoder bEncoder = new BCryptPasswordEncoder();
            if (!bEncoder.matches(user.getPassword(), dbUser.getPassword())) {
                logger.error(loginFailedMessage);
                throw new IllegalArgumentException(loginFailedMessage);
            }
            logger.info(createToken);
            logger.info(userLogged);
            String sessionToken = randomString();
            keyTokensValEmails.put(sessionToken, dbUser.getEmail());
            keyEmailsValTokens.put(dbUser.getEmail(), sessionToken);
            dbUser.setUserStatus(UserStatuses.ONLINE);
            return userRepository.save(dbUser);
        } catch (RuntimeException e) {
            logger.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Adds a user to the database if it has a unique name
     *
     * @param user - the user's data
     * @return a saved user
     * @throws IllegalArgumentException when the provided name exists in the database
     */
    public User addGuest(User user) {
        try {
            logger.debug(checkIfExistsAlready);
            if (!userRepository.findByName(guestPrefix + user.getName()).isEmpty()) {
                logger.error(guestNameExistsMessage(user.getName()));
                throw new IllegalArgumentException(guestNameExistsMessage(user.getName()));
            }
            logger.info(guestValid);
            User guestUser = User.guestUser(user);
            String sessionToken = randomString();
            keyTokensValEmails.put(sessionToken, guestUser.getEmail());
            keyEmailsValTokens.put(guestUser.getEmail(), sessionToken);
            logger.info(saveInDB);
            return userRepository.save(guestUser);
        } catch (RuntimeException e) {
            logger.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Adds a user to the database if it has a unique email
     *
     * @param user - the user's data
     * @return a saved user
     * @throws IllegalArgumentException when the provided email already exists
     */
    public User addUser(User user) {
        try {
            logger.debug(checkIfExistsAlready);
            if (userRepository.findByEmail(user.getEmail()) != null) {
                logger.error(emailExistsInSystemMessage(user.getEmail()));
                throw new IllegalArgumentException(emailExistsInSystemMessage(user.getEmail()));
            }
            logger.info(userValid);
            User registeredUser = User.registeredUser(user);
            logger.info(saveInDbWaitToActivate);
            return userRepository.save(registeredUser);
        } catch (RuntimeException e) {
            logger.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Adds a user to the database with userType field
     *
     * @param user - the user's data
     * @return a saved user
     * @throws IllegalArgumentException when the provided email not exists
     * @throws IllegalArgumentException when the provided user activation is true
     * @throws IllegalArgumentException when the provided token Expired
     */
    public User verifyEmail(User user) {
        try {
            logger.debug(checkIfExistsAlready);
            if (userRepository.findByEmail(user.getEmail()) == null) {
                logger.error(emailNotExistsMessage(user.getEmail()));
                throw new IllegalArgumentException(emailNotExistsMessage(user.getEmail()));
            }

            User dbUser = User.dbUser(userRepository.findByEmail(user.getEmail()));

            logger.debug(checkIfActivatedEmail);
            if (dbUser.isEnabled()) {
                logger.error(emailAlreadyActivatedMessage(user.getEmail()));
                throw new IllegalArgumentException(emailAlreadyActivatedMessage(user.getEmail()));
            } else if (LocalDate.now().isAfter(dbUser.getIssueDate().plusDays(1))) {
                //update verification code in DB to a new code and send it to user mail.
                logger.error(emailIssueTokenPassedMessage(user.getIssueDate().toString()));
                throw new IllegalArgumentException(emailIssueTokenPassedMessage(user.getIssueDate().toString()));
            } else if (!dbUser.getVerifyCode().equals(user.getVerifyCode())) {
                logger.error(verificationCodeNotMatch);
                throw new IllegalArgumentException(verificationCodeNotMatch);
            }

            User.verifyUser(dbUser);
            logger.info(saveInDB);
            return userRepository.save(dbUser);
        } catch (RuntimeException e) {
            logger.error(e.getMessage());
            throw new IllegalArgumentException(e.getMessage());
        }
    }


    /**
     * Initializes the keyTokensValEmails if the keyTokensValEmails is null
     */
    Map<String, String> getTokensInstance() {
        if (this.keyTokensValEmails == null)
            this.keyTokensValEmails = new HashMap<>();
        return this.keyTokensValEmails;
    }

    /**
     * Initializes the keyEmailsValTokens if the keyEmailsValTokens is null
     */
    Map<String, String> getEmailsInstance() {
        if (this.keyEmailsValTokens == null)
            this.keyEmailsValTokens = new HashMap<>();
        return this.keyEmailsValTokens;
    }

    /**
     * gets the KeyTokensValEmails Map
     */
    public Map<String, String> getKeyTokensValEmails() {
        return this.keyTokensValEmails;
    }

    /**
     * gets the KeyEmailsValTokens Map
     */
    public Map<String, String> getKeyEmailsValTokens() {
        return this.keyEmailsValTokens;
    }

}
