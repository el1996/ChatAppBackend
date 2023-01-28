package chatApp.utilities;

import chatApp.customEntities.CustomResponse;
import chatApp.customEntities.UserDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Pattern;

import static chatApp.utilities.messages.ExceptionMessages.*;
import static chatApp.utilities.messages.ExceptionMessages.invalidPasswordMessage;

public class Utility {
    private static final Logger logger = LogManager.getLogger(Utility.class.getName());
    public static String separator = "E";
    public static String userIdNameInTable = "id";
    public static String systemEmail = "@chatappsystem.com";
    public static String emptyString = "";
    public static String zoneOffsetId = "Z";
    public static String mainRoomReceiverName = "main";
    public static String mainRoomId = "0";
    public static String guestPrefix = "Guest-";
    public static String innerSystemEmail = "seselevtion@gmail.com";
    public static String emailContent = "Chat App Verification Code";
    public static String firstPrivateMessage = "New Private Chat Room";
    public static List<String> permissionPathsForAll = new ArrayList<>(List.of("/sign", "ws", "/mainchatroom", "/downloadmainchatroom", "chat/getusers"));
    public static List<String> permissionPathsForGuest = new ArrayList<>(List.of("/logout", "update/status", "chat/mainchatroom", "chat/downloadmainchatroom", "/topic", "/app", "/plain"));
    public static List<String> noPermissionsPathsForRegistered = new ArrayList<>(List.of("update/mute"));

    /**
     * Is valid password : check if The length of the password > 6 & At least one capital letter
     *
     * @param password - the password
     * @return true if valid password else false
     */
    public static boolean isValidPassword(String password) {
        logger.debug("Check valid password");
        if (password != null) {
            return password.matches(".*[A-Z].*") && password.length() >= 6;
        }
        return false;
    }

    /**
     * Is valid name : check if only letters in name
     *
     * @param name - the name
     * @return true if valid name else false
     */
    public static boolean isValidName(String name) {
        logger.debug("Check valid name");
        if (name != null) {
            return name.matches("^[ A-Za-z]+$");
        }
        return false;
    }

    /**
     * Is valid email: check if syntax of email is valid
     *
     * @param emailAddress - the user email
     * @return true if valid emailAddress else false
     */
    public static boolean isValidEmail(String emailAddress) {
        logger.debug("Check valid email");
        if (emailAddress != null) {
            String regexPattern = "^(.+)@(\\S+)$";

            return Pattern.compile(regexPattern)
                    .matcher(emailAddress)
                    .matches();
        }
        return false;
    }

    /**
     * Random string: generate random string
     *
     * @return true if valid emailAddress else false
     */
    public static String randomString() {
        UUID randomUUID = UUID.randomUUID();
        return randomUUID.toString().replaceAll("_", "");
    }

    /**
     * encrypt: encrypt string
     *
     * @param stringToEncrypt - the string to encrypt
     * @return the value encrypted
     */
    public static String encrypt(String stringToEncrypt) {
        BCryptPasswordEncoder bEncoder = new BCryptPasswordEncoder();
        return bEncoder.encode(stringToEncrypt);
    }

    /**
     * Calculate Age : calculate the age of the user
     * @param dateOfBirth
     * @return the age of the user
     */
    public static int calcAge(LocalDate dateOfBirth) {
        return LocalDate.now().minusYears(dateOfBirth.getYear()).getYear();
    }

    /**
     * Calculate LocalDateTime : calculate the current date and time
     *
     * @return the current date and time
     */
    public static LocalDateTime getLocalDateTimeNow() {
        return LocalDateTime.now();
    }

    /**
     * Check if email is valid : validates email and check if email dosen't contain "@chatappsystem"
     * @param email - user email, response - CustomResponse<UserDTO> to edit.
     * @param response - CustomResponse<UserDTO> to edit from controller.
     * @return the response edited and wrapped in optional, send it back to controller.
     */
    public static Optional<CustomResponse<UserDTO>> checkValidEmail(String email, CustomResponse<UserDTO> response){
        if (!email.equals(emptyString) && !isValidEmail(email)){
            logger.error(invalidEmailMessage);
            response.setMessage(invalidEmailMessage);
            return Optional.of(response);
        }

        if(email.contains(systemEmail)){
            logger.error(invalidRegistrationEmailMessage);
            response.setMessage(invalidRegistrationEmailMessage);
            return Optional.of(response);
        }
        return Optional.empty();
    }

    /**
     * Check if name is valid : validates name
     * @param name - user name
     * @param response - CustomResponse<UserDTO> to edit from controller.
     * @return the response edited and wrapped in optional, send it back to controller.
     */
    public static Optional<CustomResponse<UserDTO>> checkValidName(String name, CustomResponse<UserDTO> response){
        if (!name.equals(emptyString) && !isValidName(name)) {
            logger.error(invalidNameMessage);
            response.setMessage(invalidNameMessage);
            return Optional.of(response);
        }
        return Optional.empty();
    }

    /**
     * Check if email is valid : validates email
     * @param password - user password
     * @param response - CustomResponse<UserDTO> to edit from controller.
     * @return the response edited and wrapped in optional, send it back to controller.
     */
    public static Optional<CustomResponse<UserDTO>> checkValidPassword(String password, CustomResponse<UserDTO> response){
        if (!password.equals(emptyString) && !isValidPassword(password)) {
            logger.error(invalidPasswordMessage);
            response.setMessage(invalidPasswordMessage);
            return Optional.of(response);
        }
        return Optional.empty();
    }

}
