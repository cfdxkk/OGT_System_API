package OGTSystem.repository;

import OGTSystem.entity.MessageNoFriendEntity;
import OGTSystem.entity.MessageNoGroupEntity;
import OGTSystem.mapper.MessageNoFriendMapper;
import OGTSystem.mapper.MessageNoGroupMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MessageNoGroupRepository {

    @Autowired
    MessageNoGroupMapper messagenogroupmapper;

    public List<MessageNoGroupEntity> getMessageNoByGroupId(String groupId){
        return messagenogroupmapper.getMessageNoByGroupId(groupId);
    }

    public int createGroupMessageNo(MessageNoGroupEntity messagenogroupentity){
        return messagenogroupmapper.createGroupMessageNo(messagenogroupentity);
    }

    public int setGroupMessageNo(MessageNoGroupEntity messagenogroupentity){
        return messagenogroupmapper.setGroupMessageNo(messagenogroupentity);
    }
}
