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
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UserServiceTest {

    @Autowired
    UserService userService;

    @Autowired
    AuthService authService;

    @Autowired
    UserRepository userRepository;
    User user;
    User user2;
    @BeforeEach
    void newUser() {
        User userOne = User.createUser("a", "a11222222@gmail.com", "aA12345");
        this.user = User.dbUser(authService.addUser(userOne));
        user.setPassword("aA12345");
        authService.login(this.user);
        User userTwo = User.createUser("a1", "aa11222222@gmail.com", "aA12345");
        this.user2  = User.dbUser(authService.addUser(userTwo));
        user2.setPassword("aA12345");
    }

    @AfterEach
    void deleteUsers() {
        userRepository.deleteAll();
    }

    @Test
    void logoutUser_checkLogoutGuestUser_changeStatusToOffline() {
        user.setUserStatus(UserStatuses.OFFLINE);
        userRepository.save(user);
        user.setPassword("aA12345");
        assertEquals(UserStatuses.OFFLINE, userService.logoutUser(user.getEmail()).getUserStatus());
    }

    @Test
    void updateUser_updateUserName_successfulUpdate() {
        user.setName("tteesstt");
        assertEquals(user.getName(), userService.updateUser(user, user.getEmail()).getName());
    }

    @Test
    void updateUser_updateUserName_failedUpdate() {
        user.setName("@");
        assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUser(user, authService.getKeyEmailsValTokens().get(user.getEmail()));
        });
    }
    @Test
    void updateUser_updateUserDateOfBirth_failedUpdate() {
        user.setDateOfBirth(LocalDate.of(2023,4,20));
        assertThrows(IllegalArgumentException.class, () -> {
            userService.updateUser(user, authService.getKeyEmailsValTokens().get(user.getEmail()));
        });
    }
    @Test
    void updateUser_updateUserDateOfBirth_successfulUpdate() {
        user.setDateOfBirth(LocalDate.of(1995,4,20));
        user.setAge(LocalDate.now().minusYears(user.getDateOfBirth().getYear()).getYear());
        assertEquals(user.getAge(), userService.updateUser(user, user.getEmail()).getAge());
    }
    @Test
    void updateUser_updateUserDescription_successfulUpdate() {
        user.setDescription("king of the kings");
        assertEquals(user.getDescription(), userService.updateUser(user, user.getEmail()).getDescription());
    }
    @Test
    void updateUser_updateUserPhoto_successfulUpdate() {
        user.setPhoto("https://www.realmadrid.com/StaticFiles/RealMadridResponsive/images/static/og-image.png");
        assertEquals(user.getPhoto(),userService.updateUser(user, user.getEmail()).getPhoto());
    }
    @Test
    void updateStatusUser_updateStatusUserOnline_successfulUpdate() {
        assertEquals(UserStatuses.ONLINE, userService.updateStatusUser(user.getEmail(), "online").getUserStatus());
    }
    @Test
    void updateStatusUser_updateStatusUserAway_successfulUpdate() {
        User u = userService.updateStatusUser(user.getEmail(), "away");
        assertEquals(UserStatuses.AWAY,u.getUserStatus());
    }
    @Test
    void updateStatusUser_updateStatusUserEmailNull_throwNewIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.updateStatusUser(null, "away");
        });
    }
    @Test
    void getAllUsers_getAllUsersNotOffline_listOfUsers() {
        List<User> l = userService.getAllUsers();
        List<User> newl = new ArrayList<>();
        newl.add(user);
        assertEquals(newl.get(0).getId(),l.get(0).getId());
    }
    @Test
    void updateMuteUnMuteUser_updateMuteUserToUnMute_successfulUpdate() {
        user.setType(UserType.ADMIN);
        userRepository.save(user);
        User u = userService.updateMuteUnmuteUser(user2.getId(), user.getEmail());
        assertEquals(!user2.isMute(),u.isMute());
    }
    @Test
    void updateMuteUnMuteUser_updateMuteUserToUnMuteNotAdmin_failedUpdate() {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.updateMuteUnmuteUser(user2.getId(), authService.getKeyEmailsValTokens().get(user.getEmail()));
        });
    }
    @Test
    void updateMuteUnMuteUser_updateMuteUserToUnMuteNullEmail_failedUpdate() {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.updateMuteUnmuteUser(user2.getId(), null);
        });
    }
    @Test
    void updateMuteUnMuteUser_updateMuteUserToUnMuteNullId_failedUpdate() {
        assertThrows(IllegalArgumentException.class, () -> {
            userService.updateMuteUnmuteUser(80000000000000000L, authService.getKeyEmailsValTokens().get(user.getEmail()));
        });
    }
}