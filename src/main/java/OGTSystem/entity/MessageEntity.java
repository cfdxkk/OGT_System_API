package OGTSystem.entity;

import lombok.Data;

@Data
public class MessageEntity {

    // 消息来源ID
    private String uuidfrom;
    // 消息目标ID
    private String uuidto;
    // 消息目标NO
    private Long uunoto;
    // 来源用户token
    private String token;
    // 消息类型
    private String messagetype;
    //消息
    private String message;

}
