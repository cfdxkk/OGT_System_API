package OGTSystem.vo;

import lombok.Data;

import java.util.List;


@Data
public class GroupCreateVo {

    // id 群号
    private String groupId;
    // no 自增数字编号
    private List<GroupInfoVo> groupList;

}
