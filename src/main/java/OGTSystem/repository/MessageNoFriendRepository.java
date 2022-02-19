package OGTSystem.repository;

import OGTSystem.entity.MessageNoFriendEntity;
import OGTSystem.mapper.MessageNoFriendMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class MessageNoFriendRepository {

    @Autowired
    MessageNoFriendMapper messagenofriendmapper;

    public List<MessageNoFriendEntity> getMessageNoByFriendString(String friend){
        return messagenofriendmapper.getMessageNoByFriendString(friend);
    }

    public int createMessageNo(MessageNoFriendEntity messagenoentity){
        return messagenofriendmapper.createMessageNo(messagenoentity);
    }

    public int setMessageNo(MessageNoFriendEntity messagenoentity){
        return messagenofriendmapper.setMessageNo(messagenoentity);
    }
}
