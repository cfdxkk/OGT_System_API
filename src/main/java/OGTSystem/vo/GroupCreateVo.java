package OGTSystem.vo;

import lombok.Data;

import java.util.List;


@Data
public class GroupCreateVo {

    // id 群号
    private String groupId;
    // 群聊列表
    private List<GroupInfoVo> groupList;

}
