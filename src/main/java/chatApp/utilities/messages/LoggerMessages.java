package chatApp.utilities.messages;

import static chatApp.utilities.Utility.separator;

public class LoggerMessages {

    public static String beforeLoginAsGuest = "Try to login as guest to the system";
    public static String beforeActivateEmail = "Try to activate email";
    public static String beforeSendMessageInMain = "Try to send message in main chat room";
    public static String beforeSendPrivateMessage = "Try to send private message";
    public static String beforeGettingMainRoomMessages =  "Try to get main chat room messages";
    public static String beforeGettingPrivateRoomMessages =  "Try to get private chat room messages";
    public static String beforeDownloadingPrivateRoom =  "Try to download specific private chat room";
    public static String beforeDownloadingMainRoom =  "Try to download main chat room from specific time";
    public static String beforeGettingAllUsers = "Try to get all users to display in the frontend";
    public static String beforeLogout = "User try to logout in the system";
    public static String beforeMuteUnmute = "Try to mute / unmute user";
    public static String beforeUpdateStatus = "Try to changed the status of the user to ONLINE/AWAY";
    public static String checkPassword = "Check if password is correct";
    public static String createToken = "Create token for current user";
    public static String userLogged = "User is logged into the system";
    public static String checkIfExistsAlready = "Check if exist in DB";
    public static String checkIfActivatedEmail = "Check if the user already activated his email";
    public static String guestValid = "The guest receives token,email,password";
    public static String userValid = "Encrypts password user and sends him email to complete the registration";
    public static String saveInDB = "Saving in DB";
    public static String saveInDbWaitToActivate = "User saved as Guest in the system, The system is waiting for activate email to complete the registration";
    public static String getPrivateRoom = "Try to get private room messages between 2 users";
    public static String downloadPrivateChat = "Try to download private chat room messages";
    public static String addMessageInMainChat = "Try to add message to main chat room";
    public static String getMainChatMessages = "Try to get main chat room messages";
    public static String updateEmail = "Email has been updated";
    public static String updateNickname = "Nickname has been updated";
    public static String updateName = "Name has been updated";
    public static String updatePassword = "Password has been updated";
    public static String updateDateOfBirthAndAge = "Date of birth and Age has been updated";
    public static String updatePhoto = "Photo has been updated";
    public static String updateDescription = "Description has been updated";
    public static String update = "Checking fields to update in user profile";
    public static String userLogout = "logging out the user from the system";
    public static String deleteGuest = "check if the user is a guest and delete him from DB, else update his status to offline";
    public static String toggledMute = "Mute or Unmute has been toggled";
    public static String getAllUsers = "Get all users in users table sorted by admin,registered,guest and filtered the offline users";
    public static String updatingNewNicknameInOldMessages = "User has changed his email or nickname , updating all his old messages as sender and as receiver";

    public static String beforeAnAction(String email, String action) {
        return String.format("Try to "+ action + " " + email + " to the system");
    }

    public static String checkPrivateRoomMessage(Long senderId, Long receiverId) {
        return String.format("check if the room " + senderId + separator + receiverId + " exists");
    }

    public static String createPrivateRoomMessage(Long senderId, Long receiverId) {
        return String.format("create if the room " + senderId + separator + receiverId + " exists");
    }

    public static String addMessageToPrivateRoom(String room) {
        return String.format("Try to add a message to a private chat room id: " + room);
    }

}
