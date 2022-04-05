package OGTSystem.vo;

import lombok.Data;


@Data
public class GroupInfoVo {

    // id 群号
    private String groupId;
    // no 自增数字编号
    private Long groupNo;
    // 群名
    private String groupName;
    // 群聊简介
    private String groupIntroduction;
    // 群头像
    private String groupAvatar;
    // 群头像
    private String groupAvatarOrigin;
    // 群主ID
    private String groupCreator;
    // token
    private String token;

}
