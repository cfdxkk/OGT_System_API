package OGTSystem.entity;

import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

@Data
public class UserInfoEntity {
    // user_often_edit table
    // 唯一用户ID
    private String UUID;
    // 自增唯一用户编号
    private BigInteger UUNO;
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
    // 用户ws连接状态
    private String wsStatus;
    // 用户ws地址
    private String userWsServer;

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
