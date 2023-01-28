package chatApp.entities;

import chatApp.utilities.Utility;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

import static chatApp.utilities.Utility.*;
import static chatApp.utilities.Utility.randomString;

@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false, length = 20)
    private String name;
    @Column(nullable = false, unique = true, length = 45)
    private String email;
    @Column(nullable = false, length = 64)
    private String password;
    @Column()
    private String photo;
    @Column()
    private LocalDate dateOfBirth;
    @Column()
    private int age;
    @Column()
    private UserType type;
    @Column(name = "user_status")
    private UserStatuses userStatus;
    @Column(name = "enabled")
    private boolean enabled;
    @Column(name = "verification_code")
    private String verifyCode;
    @Column(name = "issue_date")
    private LocalDate issueDate;
    @Column(name = "is_mute")
    private boolean isMute;

    @Column(nullable = false, unique = true, length = 25)
    private String nickname;

    @Column(length = 200)
    private String description;

    User() {
    }


    public static User dbUser(User user) {
        User currUser = new User();
        currUser.setName(user.getName());
        currUser.setEmail(user.getEmail());
        currUser.setPassword(user.getPassword());
        currUser.setEnabled(user.isEnabled());
        currUser.setMute(user.isMute());
        currUser.setUserStatus(user.getUserStatus());
        currUser.setNickname(user.getNickname());
        currUser.setType(user.getType());
        currUser.setAge(user.getAge());
        currUser.setDescription(user.getDescription());
        currUser.setPhoto(user.getPhoto());
        currUser.setIssueDate(user.getIssueDate());
        currUser.setDateOfBirth(user.getDateOfBirth());
        currUser.setId(user.getId());
        currUser.setVerifyCode(user.getVerifyCode());
        return currUser;
    }
    public static User guestUser(User user) {
        User currUser = new User();
        currUser.setName(guestPrefix + user.getName());
        currUser.setEmail(user.getName() + systemEmail);
        currUser.setPassword(Utility.randomString());
        currUser.setEnabled(user.isEnabled());
        currUser.setMute(user.isMute());
        currUser.setUserStatus(UserStatuses.ONLINE);
        currUser.setNickname(user.getName());
        currUser.setType(UserType.GUEST);
        currUser.setIssueDate(user.getIssueDate());
        return currUser;
    }
    public static User registeredUser(User user) {
        User currUser = new User();
        currUser.setName(user.getName());
        currUser.setEmail(user.getEmail());
        currUser.setPassword(encrypt((user.getPassword())));
        currUser.setEnabled(user.isEnabled());
        currUser.setMute(user.isMute());
        currUser.setUserStatus(UserStatuses.OFFLINE);
        currUser.setNickname(user.getEmail());
        currUser.setType(UserType.GUEST);
        currUser.setPhoto("https://t3.ftcdn.net/jpg/03/46/83/96/360_F_346839683_6nAPzbhpSkIpb8pmAwufkC7c5eD7wYws.jpg");
        currUser.setIssueDate(LocalDate.now());
        currUser.setVerifyCode(randomString());
        return currUser;
    }
    public static void verifyUser(User user) {
        user.setEnabled(true);
        user.setType(UserType.REGISTERED);
        user.setVerifyCode(null);
    }

    public static User createUser(String name, String email, String password) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setNickname(email);
        user.setPassword(password);
        user.setEnabled(false);
        user.setMute(false);
        return user;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public UserType getType() {
        return type;
    }

    public void setType(UserType type) {
        this.type = type;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getVerifyCode() {
        return verifyCode;
    }

    public void setVerifyCode(String verifyCode) {
        this.verifyCode = verifyCode;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public UserStatuses getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(UserStatuses userStatus) {
        this.userStatus = userStatus;
    }

    public boolean isMute() {
        return isMute;
    }

    public void setMute(boolean mute) {
        isMute = mute;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (age != user.age) return false;
        if (enabled != user.enabled) return false;
        if (isMute != user.isMute) return false;
        if (!Objects.equals(id, user.id)) return false;
        if (!Objects.equals(name, user.name)) return false;
        if (!Objects.equals(email, user.email)) return false;
        if (!Objects.equals(password, user.password)) return false;
        if (!Objects.equals(photo, user.photo)) return false;
        if (!Objects.equals(dateOfBirth, user.dateOfBirth)) return false;
        if (type != user.type) return false;
        if (userStatus != user.userStatus) return false;
        if (!Objects.equals(verifyCode, user.verifyCode)) return false;
        return Objects.equals(issueDate, user.issueDate);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        result = 31 * result + (password != null ? password.hashCode() : 0);
        result = 31 * result + (photo != null ? photo.hashCode() : 0);
        result = 31 * result + (dateOfBirth != null ? dateOfBirth.hashCode() : 0);
        result = 31 * result + age;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (userStatus != null ? userStatus.hashCode() : 0);
        result = 31 * result + (enabled ? 1 : 0);
        result = 31 * result + (verifyCode != null ? verifyCode.hashCode() : 0);
        result = 31 * result + (issueDate != null ? issueDate.hashCode() : 0);
        result = 31 * result + (isMute ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", photo='" + photo + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", age=" + age +
                ", type=" + type +
                ", userStatus=" + userStatus +
                ", enabled=" + enabled +
                ", verifyCode='" + verifyCode + '\'' +
                ", issueDate=" + issueDate +
                ", isMute=" + isMute +
                ", nickname='" + nickname + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
