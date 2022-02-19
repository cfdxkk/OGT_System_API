package OGTSystem.vo;

import lombok.Data;

import java.util.Date;


@Data
public class GroupMessageVo {

    // message id
    private String messageId;
    // 消息来源群号
    private String groupIdFrom;
    // 消息来源用户号
    private String uuidFrom;
    // 消息目标用户号
    private String uuidTo;
    // 群聊级别的消息顺序号
    private String messageNoInGroup;
    // 发送者token
    private String token;
    // 消息类型
    private String messageType;
    // 长消息flag
    private int langMessageFlag;
    // 违规消息flag
    private int violationFlag;
    // 消息
    private String message;
    // 消息发送日期
    private Long sentDate;
    // 消息编辑日期
    private Long editDate;
    // 消息编辑者
    private String messageEditorID;

}
