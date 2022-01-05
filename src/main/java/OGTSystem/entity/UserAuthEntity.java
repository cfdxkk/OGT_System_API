package OGTSystem.entity;

import lombok.Data;


@Data
public class UserAuthEntity {

    // 唯一用户ID
    private String UUID;
    // 自增唯一用户编号
    private Long UUNO;
    // 用户token
    private String userToken;
    // 用户违规次数
    private int userAttackTime;

}
