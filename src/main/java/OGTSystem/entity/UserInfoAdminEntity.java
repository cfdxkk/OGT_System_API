package OGTSystem.entity;

import lombok.Data;

@Data
public class UserInfoAdminEntity {
    // 被查询者的信息
    private String userId;
    private Long userNo;
    private String username;

    // 管理员ID
    private String adminUserId;
    // 管理员Token
    private String token;

}
