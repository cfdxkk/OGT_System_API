package OGTSystem.mapper;

import OGTSystem.entity.MessageNoFriendEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageNoFriendMapper {
    List<MessageNoFriendEntity> getMessageNoByFriendString (String friend);
    int createMessageNo(MessageNoFriendEntity messagenoentity);
    int setMessageNo(MessageNoFriendEntity messagenoentity);

}
