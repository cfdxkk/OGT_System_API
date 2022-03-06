package OGTSystem.entity;

import lombok.Data;

import java.util.Date;

@Data
public class UserSafeInfoEntity {
    // user_often_edit table
    // 唯一用户ID
    private String userId;
    // 自增唯一用户编号
    private Long userNo;
    // 用户昵称
    private String username;
    // 用户头像
    private String userAvatar;
    // 用户个人简介
    private String userInfo;
    // 用户ws连接状态
    private String wsStatus;  // 通常
    private String wsAndroidPhone;  // 安卓手机
    private String wsIosPhone;  // iphone
    private String wsAndroidPad;  // 安卓平板
    private String wsIosPad;  // ipad
    private String wsWindows;  // 微软windows系统
    private String wsMacos;  // 苹果MacOs
    private String wsWebBrowser;  // 网络浏览器
    private String wsLinux;  //  linux
    // 用户ws地址
    private String userWsServer;

    private String userAdminFlag;
    private String userBanFlag;

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
