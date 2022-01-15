package OGTSystem.entity;

import lombok.Data;

import java.util.Date;

@Data
public class P2PMessageEntity implements Comparable<P2PMessageEntity> {

    // 消息ID
    private Long messageId;
    // 消息顺序号
    private Long chatMessageNo;
    // 消息来源UUID
    private String messageFrom;
    // 消息目标UUID
    private String messageTarget;
    // 长消息flag
    private int longMessageFlag;
    // 消息类型
    private String messageType;
    // 发送成功flag
    private int sendFlag;
    // 已读flag
    private int readFlag;
    // 违规flag
    private int violationFlag;
    // 消息体
    private String message;
    // 消息发送日期
    private Date sentDate;
    // 消息编辑日期
    private Date editDate;
    // 消息编辑者
    private String messageEditorID;


    @Override
    public int compareTo(P2PMessageEntity p2pmessageentity) {//重写比较方法

        // 比较消息顺序号大小
        if (getChatMessageNo() > p2pmessageentity.getChatMessageNo()){
            return 1;
        } else if (getChatMessageNo() < p2pmessageentity.getChatMessageNo()){
            return -1;
        }
        return 0;
    }
}
