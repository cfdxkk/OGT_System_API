package OGTSystem.entity;

import lombok.Data;


@Data
public class MessageNoEntity {

    // id
    private Long id;
    // 两个人的ID索引
    private String friend;
    // 消息No
    private Long messageNo;

}
