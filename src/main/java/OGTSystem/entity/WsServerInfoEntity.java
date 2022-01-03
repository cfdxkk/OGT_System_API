package OGTSystem.entity;

import lombok.Data;

import java.math.BigInteger;
import java.util.Date;

@Data
public class WsServerInfoEntity {

    // 服务器ID
    private Long serverNO;
    // 服务器在线人数
    private Long connectNumbers;
    // 服务器地址
    private String serverAddress;

}
