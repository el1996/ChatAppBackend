package chatApp.service;

import chatApp.entities.User;
import chatApp.entities.UserStatuses;
import chatApp.entities.UserType;
import chatApp.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;

import static chatApp.utilities.Utility.randomString;
import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(SpringExtension.class)
@SpringBootTest
class AuthServiceTest {

    @Autowired
    AuthService authService;
    @Autowired
    private UserRepository userRepo;

    User user;

    @BeforeEach
    void newUser(){
        this.user = User.createUser("abcd", "abcd1234567@gmail.com", "abcdABCD123");
    }

    @AfterEach
    void delete(){ userRepo.deleteAll();}

    @Test
    void addUser_insertUserInDB_saveUserInDB()  {
        User registerUser = User.dbUser(authService.addUser(user));
        assertEquals(registerUser, userRepo.findByEmail(registerUser.getEmail()));
    }
    @Test
    void login_insertUserInDB_IllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->{authService.login(user);}  );
    }
    @Test
    void login_checkStatus_statusOnline()  {
        authService.addUser(user);
        user.setPassword("abcdABCD123");
        authService.login(user);
        User dbuser = userRepo.findByEmail(user.getEmail());
        assertNotEquals(UserStatuses.OFFLINE, dbuser.getUserStatus().toString());
    }
    @Test
    void addUser_checkEmailExists_IllegalArgumentException()  {
        User user1 = User.createUser("abcdCopy", "abcd1234567@gmail.com", "abcdABCD123Copy");
        authService.addUser(user1);
        assertThrows(IllegalArgumentException.class, () ->{authService.addUser(user);} );
        userRepo.delete(user1);
    }
    @Test
    void addGuest_checkNameExists_IllegalArgumentException()  {
        User user1 = user.createUser("abcd", "abcd123@copygmail.com", "abcdABCD123Copy");
        authService.addGuest(user1);
        assertThrows(IllegalArgumentException.class, () ->{authService.addGuest(user);} );
        userRepo.delete(user1);

    }
    @Test
    void verifyEmail_checkUerExists_IllegalArgumentException()  {
        assertThrows(IllegalArgumentException.class,()->{authService.verifyEmail(user);});
    }
    @Test
    void verifyEmail_checkEnabled_IllegalArgumentException1()  {
        user.setEnabled(true);
        assertThrows(IllegalArgumentException.class,()->{ authService.verifyEmail(authService.addUser(user));});
    }
    @Test
    void verifyEmail_checkIfPassDay_IllegalArgumentException()  {
        User user1 = User.createUser("abcde", "abc486@comail.com", "abcdABCD1234");
        user1.setIssueDate(LocalDate.now().minusDays(5));
        userRepo.save(user1);
        assertThrows(IllegalArgumentException.class, () ->{authService.verifyEmail(user1);}  );
        userRepo.delete(user1);
    }
    @Test
    void verifyEmail_checkVerifyCode_IllegalArgumentException() {
        User user1 = authService.addUser(user);
        user.setVerifyCode(user1.getVerifyCode() + "aa");
       // user.setIssueDate(LocalDate.now());
        assertThrows(IllegalArgumentException.class, () ->{authService.verifyEmail(user);}  );
        userRepo.delete(user1);
    }
    @Test
    void verifyEmail_checkType_registeredType()  {
        user.setVerifyCode(randomString());
        user.setIssueDate(LocalDate.now());
        assertEquals(UserType.REGISTERED, authService.verifyEmail(authService.addUser(user)).getType());
    }
}