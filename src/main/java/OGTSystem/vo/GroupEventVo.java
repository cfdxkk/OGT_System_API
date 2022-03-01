package OGTSystem.vo;

import lombok.Data;

import java.util.List;


@Data
public class GroupEventVo {

    // event id
    private Long eventId;
    // 事件起始日期
    private Long startTime;
    // 事件结束日期
    private Long endTime;
    // 事件颜色
    private String eventColor;
    // 发布事件者用户名
    private String userName;
    // 发布事件者用户头像
    private String userAvatar;
    // 发布事件者在群聊中的角色
    private String userType;
    // 事件标题
    private String eventTitle;
    // 事件体
    private String eventText;
    // 事件图片附件
    private List<String> eventImg;
}
