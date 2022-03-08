package OGTSystem.entity;

import lombok.Data;


@Data
public class GroupRelationshipEditEntity {

    // id
    private String relationshipId;
    // 群ID
    private String groupId;
    // 用户ID
    private String userId;

    // 编辑者ID
    private String adminUserId;
    // 编辑者Token
    private String adminToken;

}
