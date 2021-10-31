package OGTSystem.entity;

import java.util.Date;

public class userInfoEntity {
    // user_often_edit table
    // 唯一用户ID
    private String UUID;
    // 用户昵称
    private String username;
    // 密码
    private String password;
    // 用户隐私权限
    private String privacyAuth;
    // 用户头像
    private String userAvatar;
    // 用户个人简介
    private String userInfo;

    // user_not_often_edit table
    // 用户邮箱
    private String userEmail;
    // 用户性别
    private String userSex;
    // 用户生日
    private Date userBirthday;
    // 用户住址
    private String userLocation;


    public String getUUID() {
        return UUID;
    }

    public void setUUID(String UUID) {
        this.UUID = UUID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPrivacyAuth() {
        return privacyAuth;
    }

    public void setPrivacyAuth(String privacyAuth) {
        this.privacyAuth = privacyAuth;
    }

    public String getUserAvatar() {
        return userAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.userAvatar = userAvatar;
    }

    public String getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(String userInfo) {
        this.userInfo = userInfo;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserSex() {
        return userSex;
    }

    public void setUserSex(String userSex) {
        this.userSex = userSex;
    }

    public Date getUserBirthday() {
        return userBirthday;
    }

    public void setUserBirthday(Date userBirthday) {
        this.userBirthday = userBirthday;
    }

    public String getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(String userLocation) {
        this.userLocation = userLocation;
    }
}
