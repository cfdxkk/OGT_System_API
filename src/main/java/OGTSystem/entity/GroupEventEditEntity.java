package OGTSystem.entity;

import lombok.Data;

import java.util.Date;


@Data
public class GroupEventEditEntity {

    // event id
    private String eventId;
    // 事件来源群ID
    private String groupIdFrom;
    // 事件来源用户ID
    private String uuidFrom;

    // 事件起始日期
    private Date eventStartDateTime;
    // 事件结束日期
    private Date eventEndDateTime;

    // 长事件flag
    private int langEventFlag;
    // 违规事件flag
    private int violationFlag;

    // 事件颜色
    private String eventColor;
    // 事件标题
    private String eventTitle;
    // 事件体
    private String eventText;

    // 事件发送日期
    private Date sentDate;
    // 事件编辑日期
    private Date editDate;
    // 事件编辑者
    private String eventEditorID;

    //adminId
    private String adminUserId;
    //adminToken
    private String adminToken;

}
