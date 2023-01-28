package chatApp.controller;

import chatApp.customEntities.CustomResponse;
import chatApp.customEntities.UserDTO;
import chatApp.entities.User;
import chatApp.repository.UserRepository;
import chatApp.service.AuthService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static chatApp.utilities.messages.ExceptionMessages.*;
import static chatApp.utilities.Utility.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class AuthControllerTest {

    @Autowired
    AuthService authService;

    @Autowired
    AuthController authController;

    @Autowired
    private UserRepository userRepo;

    User user;

    @BeforeEach
    void newUser(){
        this.user = User.createUser("abcd", "abcd123@gmail.com", "abcdABCD123");
    }

    @AfterEach
    void delete(){ userRepo.deleteAll();}

    @Test
    void createUser_checkValidEmail_inValidEmail() {
            user.setEmail("k");
            ResponseEntity<CustomResponse<UserDTO>> user1 = authController.registerUser(user);
            assertEquals(invalidEmailMessage, user1.getBody().getMessage());
    }
    @Test
    void createUser_checkValidName_inValidName() {
            user.setName("k09");
            ResponseEntity<CustomResponse<UserDTO>> user1 = authController.registerUser(user);
            assertEquals(invalidNameMessage, user1.getBody().getMessage());
    }
    @Test
    void createUser_checkValidPassword_inValidPassword() {
        user.setPassword("jks87k09");
        ResponseEntity<CustomResponse<UserDTO>> user1 = authController.registerUser(user);
        assertEquals(invalidPasswordMessage, user1.getBody().getMessage());
    }
    @Test
    void createUser_checkCreateUser_ok() {
        ResponseEntity<CustomResponse<UserDTO>> user1 = authController.registerUser(user);
        user.setPassword("abcdABCD123");
        assertEquals(HttpStatus.OK, user1.getStatusCode());
    }
    @Test
    void createUser_checkCreateUser_badRequest() {
            User user1 = User.createUser("abcdCopy", "abcd123@gmail.com", "abcdABCD123Copy");
            authController.registerUser(user);
            assertEquals(HttpStatus.BAD_REQUEST, authController.registerUser(user1).getStatusCode());
            userRepo.delete(user1);
    }
    @Test
    void login_checkValidEmail_inValidEmail() {
            user.setEmail("k");
            ResponseEntity<CustomResponse<UserDTO>> user1 = authController.login(user);
            assertEquals(invalidEmailMessage, user1.getBody().getMessage());
    }
    @Test
    void login_checkValidPassword_inValidPassword() {
            user.setPassword("jks87k09");
            ResponseEntity<CustomResponse<UserDTO>> user1 = authController.login(user);
            assertEquals(invalidPasswordMessage, user1.getBody().getMessage());
    }
    @Test
    void login_checkLoginUser_ok() {
        authController.registerUser(user);
        user.setPassword("abcdABCD123");
        assertEquals(HttpStatus.OK, authController.login(user).getStatusCode());
    }
    @Test
    void login_checkLoginUser__badRequest() {
        user.setPassword("abcdABCD123");
        assertEquals(HttpStatus.BAD_REQUEST, authController.login(user).getStatusCode());
    }
    @Test
    void loginAsGuest_checkValidName_inValidName() {
            user.setName("k09");
            assertEquals(invalidNameMessage,authController.loginAsGuest(user).getBody().getMessage());
    }

    @Test
    void loginAsGuest_checkLoginGuest_ok() {
            assertEquals(HttpStatus.OK, authController.loginAsGuest(user).getStatusCode());
    }
    @Test
    void loginAsGuest_checkLoginGuest_badRequest() {
            User user1 = User.createUser("abcd", "abcd123@copygmail.com", "abcdABCD123Copy");
            authController.loginAsGuest(user);
            assertEquals(HttpStatus.BAD_REQUEST, authController.loginAsGuest(user1).getStatusCode());
            userRepo.delete(user1);
    }
    @Test
    void verifyEmail_ok(){
        user.setVerifyCode(randomString());
        user.setIssueDate(LocalDate.now());
        user.setNickname(user.getEmail());
        userRepo.save(user);
        assertEquals(HttpStatus.OK, authController.verifyEmail(user).getStatusCode());
    }
    @Test
    void verifyEmail_badRequest(){
        user.setEnabled(true);
        assertEquals(HttpStatus.BAD_REQUEST, authController.verifyEmail(user).getStatusCode());
    }











//    assertEquals(userRepo.findByEmail(user.getEmail()), authService.addGuest(user));








//    @Test
//    void createUser_insertUserInDB_saveUserInDB() throws SQLDataException {
//        User user = User.registerUser("bbb", "bbb222@gmail.com", "bbbBBB222");
//        User user1 = authService.addUser(user);
//        assertEquals(user, userRepo.findByEmail(user1.getEmail()));
//        userRepo.delete(user1);
//    }
//    @Test
//    void login_existingUser_tokenNotNull() {
//        ResponseEntity<CustomResponse<UserDTO>> user1 = authController.login(user);
//        assertNotNull(user1.getBody().getHeaders());
//    }
//
//    @Test
//    void login_existingUser_emailNotExist() {
//        user.setEmail("aaa111@gmail.com");
//        assertThrows(SQLDataException.class, () ->{authService.login(user);}  );
//    }
//
//    @Test
//    void loginAsGuest_checkName_addPrefixGuest() throws SQLDataException {
//        user.setName("a");
//        User user1 = authService.addGuest(user);
//        assertEquals(user.getName(), user1.getName());
//        userRepo.delete(user1);
//    }
//    @Test
//    void loginAsGuest_checkEmail_addEmailGuest() throws SQLDataException {
//        user.setName("b");
//        User user1 = authService.addGuest(user);
//        assertEquals(user.getEmail() ,user1.getEmail());
//        userRepo.delete(user1);
//    }
//
//    @Test
//    void verifyEmail_ok() throws SQLDataException {
//
//        User user1 = authService.addUser(user);
//        ResponseEntity<CustomResponse<UserDTO>> response = authController.verifyEmail(user1);
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        userRepo.delete(user1);
//    }
//    @Test
//    void verifyEmail_badRequest() throws SQLDataException {
//        User user1 = authService.addUser(user);
//        ResponseEntity<CustomResponse<UserDTO>> response = authController.verifyEmail(user1);
//        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
//        userRepo.delete(user1);
//    }=================================================================================
    //=================================================================================
//    @Test
//    void createUser_validateUserInput_isValid() {
//        assertTrue(isValidEmail(user.getEmail()) && isValidName(user.getName())
//                && isValidPassword(user.getPassword()));
//    }
//
//    @Test
//    void createUser_validateEmailInput_emailNotValid() {
//        user.setEmail("shaigmail.com");
//        assertFalse(isValidEmail(user.getEmail()));
//    }
//
//    @Test
//    void createUser_validatePasswordInput_passwordNotValid() {
//        user.setPassword("shai1234");
//        assertFalse(isValidEmail(user.getPassword()));
//    }
//
//    @Test
//    void createUser_validateNameInput_nameNotValid() {
//        user.setName("shai1234");
//        assertFalse(isValidEmail(user.getName()));
//    }
//
//    @Test
//    void createUser_validateEmailInput_emailIsNull() {
//        user.setEmail(null);
//        assertNull(user.getEmail());
//    }
//
//    @Test
//    void createUser_validatePasswordInput_passwordIsNull() {
//        user.setPassword(null);
//        assertNull(user.getPassword());
//    }
//
//    @Test
//    void createUser_validateNameInput_nameIsNull() {
//        user.setName(null);
//        assertNull(user.getName());
//    }
}