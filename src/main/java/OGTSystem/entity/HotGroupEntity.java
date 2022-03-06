package OGTSystem.entity;

import lombok.Data;


@Data
public class HotGroupEntity {

    // id 群号
    private String groupId;
    // 删除flag
    private int deleteFlag;
    // usrId
    private String userId;
    // token
    private String token;

}
