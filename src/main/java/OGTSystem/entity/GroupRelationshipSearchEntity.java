package OGTSystem.entity;

import lombok.Data;


@Data
public class GroupRelationshipSearchEntity {

    // id
    private String relationshipId;
    // 群ID
    private String groupId;
    // 用户ID
    private String userId;

    // 搜索者ID
    private String searcherId;
    // 搜索者Token
    private String searcherToken;

}
