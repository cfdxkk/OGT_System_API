package OGTSystem.entity;

import lombok.Data;
import java.util.Date;

@Data
public class UserInfoEntity {
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

}
