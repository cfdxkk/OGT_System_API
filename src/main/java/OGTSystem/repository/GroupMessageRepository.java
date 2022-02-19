package OGTSystem.repository;

import OGTSystem.entity.GroupMessageEntity;
import OGTSystem.entity.P2PMessageEntity;
import OGTSystem.mapper.GroupMessageMapper;
import OGTSystem.mapper.P2PMessageMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GroupMessageRepository {

    @Autowired
    GroupMessageMapper groupmessagemapper;

    public List<GroupMessageEntity> getMessagesByMessageFromUUID(String groupId){
        return groupmessagemapper.getMessagesByGroupId(groupId);
    }

    public List<GroupMessageEntity> getMessagesByP2PMessageEntity(GroupMessageEntity groupmessageentity){
        return groupmessagemapper.getMessagesByGroupMessageEntity(groupmessageentity);
    }

    public int createMessage(GroupMessageEntity groupmessageentity){
        return groupmessagemapper.createGroupMessage(groupmessageentity);
    }

    public int setMessage(GroupMessageEntity groupmessageentity){
        return groupmessagemapper.setGroupMessage(groupmessageentity);
    }
}
