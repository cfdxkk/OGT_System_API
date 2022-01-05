package OGTSystem.repository;

import OGTSystem.entity.MessageNoEntity;
import OGTSystem.entity.UserAuthEntity;
import OGTSystem.entity.UserInfoEntity;
import OGTSystem.mapper.MessageNoFriendMapper;
import OGTSystem.mapper.UserAuthMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MessageNoFriendRepository {

    @Autowired
    MessageNoFriendMapper messagenofriendmapper;

    public List<MessageNoEntity> getMessageNoByFriendString(String friend){
        return messagenofriendmapper.getMessageNoByFriendString(friend);
    }

    public int createMessageNo(MessageNoEntity messagenoentity){
        return messagenofriendmapper.createMessageNo(messagenoentity);
    }

    public int setMessageNo(MessageNoEntity messagenoentity){
        return messagenofriendmapper.setMessageNo(messagenoentity);
    }
}
