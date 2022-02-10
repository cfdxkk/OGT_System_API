package OGTSystem.entity;

import lombok.Data;


@Data
public class GroupInfoEntity {

    // id  群号
    private Long id;
    // 群名
    private String groupName;
    // 群聊简介
    private String groupIntroduction;

}
