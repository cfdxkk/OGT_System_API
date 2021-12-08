package OGTSystem.entity;

import lombok.Data;

import java.math.BigInteger;

@Data
public class UserAuthEntity {

    // 唯一用户ID
    private String UUID;
    // 自增唯一用户编号
    private BigInteger UUNO;
    // 用户token
    private String userToken;
    // 用户违规次数
    private int userAttackTime;

}
