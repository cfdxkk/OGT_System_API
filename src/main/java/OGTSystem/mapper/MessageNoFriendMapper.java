package OGTSystem.mapper;

import OGTSystem.entity.MessageNoEntity;
import OGTSystem.entity.UserAuthEntity;
import OGTSystem.entity.UserInfoEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface MessageNoFriendMapper {
    List<MessageNoEntity> getMessageNoByFriendString (String friend);
    int createMessageNo(MessageNoEntity messagenoentity);
    int setMessageNo(MessageNoEntity messagenoentity);

}
