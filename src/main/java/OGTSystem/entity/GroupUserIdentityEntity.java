package OGTSystem.entity;

import lombok.Data;


@Data
public class GroupUserIdentityEntity {

    // id 群号
    private String groupId;
    // 群特权用户ID
    private String groupAdminId;
    // 用户特权类别
    private int groupAdminType;

}
