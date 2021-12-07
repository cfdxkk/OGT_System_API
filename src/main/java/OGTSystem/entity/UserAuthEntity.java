package OGTSystem.entity;

import lombok.Data;

@Data
public class UserAuthEntity {

    // 唯一用户ID
    private String UUID;
    // 用户token
    private String userToken;
    // 用户违规次数
    private String userAttackTime;

}
