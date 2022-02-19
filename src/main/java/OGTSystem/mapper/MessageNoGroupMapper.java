package OGTSystem.mapper;

import OGTSystem.entity.MessageNoFriendEntity;
import OGTSystem.entity.MessageNoGroupEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageNoGroupMapper {
    List<MessageNoGroupEntity> getMessageNoByGroupId (String groupId);
    int createGroupMessageNo(MessageNoGroupEntity messagenogroupentity);
    int setGroupMessageNo(MessageNoGroupEntity messagenogroupentity);

}
