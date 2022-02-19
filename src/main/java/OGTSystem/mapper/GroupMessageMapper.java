package OGTSystem.mapper;

import OGTSystem.entity.GroupMessageEntity;
import OGTSystem.entity.P2PMessageEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GroupMessageMapper {
    List<GroupMessageEntity> getMessagesByGroupId (String groupId);
    List<GroupMessageEntity> getMessagesByGroupMessageEntity (GroupMessageEntity groupmessageentity);
    int createGroupMessage(GroupMessageEntity groupmessageentity);
    int setGroupMessage(GroupMessageEntity groupmessageentity);

}
