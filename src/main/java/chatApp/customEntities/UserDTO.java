package chatApp.customEntities;

import chatApp.entities.User;
import chatApp.entities.UserStatuses;
import chatApp.entities.UserType;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserDTO {
    private Long id;
    private String name;
    private String email;
    private String photo;
    private LocalDate dateOfBirth;
    private int age;
    private UserStatuses userStatus;
    private UserType userType;
    private boolean isMute;
    private String nickname;
    private String description;

    private UserDTO(){
    }

    /**
     *User DTO: get user and convert him to userDTO
     * @param user - the user
     * @return the userDTO
     */
    public static UserDTO userToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setPhoto(user.getPhoto());
        userDTO.setDateOfBirth(user.getDateOfBirth());
        userDTO.setAge(user.getAge());
        userDTO.setUserStatus(user.getUserStatus());
        userDTO.setUserType(user.getType());
        userDTO.setNickname(user.getNickname());
        userDTO.setDescription((user.getDescription()));
        userDTO.setMute(user.isMute());
        return userDTO;
    }

    /**
     *UserListToUserListDTO: get users list and convert them to userDTO list
     * @param users - the users list
     * @return the userDTO list
     */
    public static List<UserDTO> userListToUserListDTO(List<User> users) {
        List<UserDTO> listUsers = new ArrayList<>();
        for (User user: users) {
            UserDTO userDTO = UserDTO.userToUserDTO(user);
            listUsers.add(userDTO);
        }
        return listUsers;
    }


    /**
     *User DTO: get user guest and convert him to userDTO
     * @param user - the guest
     * @return the guest as userDTO
     */
    public static UserDTO userGuestToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getId());
        userDTO.setName(user.getName());
        userDTO.setEmail(user.getEmail());
        userDTO.setUserStatus(user.getUserStatus());
        userDTO.setUserType(user.getType());
        userDTO.setNickname(user.getNickname());
        userDTO.setMute(user.isMute());
        return userDTO;
    }

    private void setId(Long id) {
        this.id = id;
    }

    private void setName(String name) {
        this.name = name;
    }

    private void setEmail(String email) {
        this.email = email;
    }

    private void setPhoto(String photo) {
        this.photo = photo;
    }

    private void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    private void setAge(int age) {
        this.age = age;
    }

    private void setUserStatus(UserStatuses userStatus) {
        this.userStatus = userStatus;
    }

    private void setUserType(UserType userType) {
        this.userType = userType;
    }

    private void setMute(boolean mute) {
        isMute = mute;
    }

    private void setNickname(String nickname) {
        this.nickname = nickname;
    }

    private void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoto() {
        return photo;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public int getAge() {
        return age;
    }

    public UserStatuses getUserStatus() {
        return userStatus;
    }

    public UserType getUserType() {
        return userType;
    }

    public boolean isMute() {
        return isMute;
    }

    public String getNickname() {
        return nickname;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserDTO userDTO = (UserDTO) o;

        if (age != userDTO.age) return false;
        if (isMute != userDTO.isMute) return false;
        if (!Objects.equals(id, userDTO.id)) return false;
        if (!name.equals(userDTO.name)) return false;
        if (!email.equals(userDTO.email)) return false;
        if (!Objects.equals(photo, userDTO.photo)) return false;
        if (!Objects.equals(dateOfBirth, userDTO.dateOfBirth)) return false;
        if (userStatus != userDTO.userStatus) return false;
        if (userType != userDTO.userType) return false;
        if (!nickname.equals(userDTO.nickname)) return false;
        return Objects.equals(description, userDTO.description);
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + name.hashCode();
        result = 31 * result + email.hashCode();
        result = 31 * result + (photo != null ? photo.hashCode() : 0);
        result = 31 * result + (dateOfBirth != null ? dateOfBirth.hashCode() : 0);
        result = 31 * result + age;
        result = 31 * result + (userStatus != null ? userStatus.hashCode() : 0);
        result = 31 * result + (userType != null ? userType.hashCode() : 0);
        result = 31 * result + (isMute ? 1 : 0);
        result = 31 * result + nickname.hashCode();
        result = 31 * result + (description != null ? description.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "UserDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", photo='" + photo + '\'' +
                ", dateOfBirth=" + dateOfBirth +
                ", age=" + age +
                ", userStatus=" + userStatus +
                ", userType=" + userType +
                ", isMute=" + isMute +
                ", nickname='" + nickname + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
