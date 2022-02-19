package OGTSystem.entity;

import lombok.Data;


@Data
public class MessageNoGroupEntity {

    // id
    private Long id;
    // 群ID索引
    private String groupId;
    // 消息No
    private Long messageNo;

}
