package OGTSystem.entity;

import lombok.Data;


@Data
public class GroupUserIdentityEditEntity {

    // id 群号
    private String groupId;
    // 群特权用户ID
    private String groupAdminId;
    // 用户特权类别
    private int groupAdminType;


    // 编辑者ID
    private String userId;
    // token
    private String token;

}
