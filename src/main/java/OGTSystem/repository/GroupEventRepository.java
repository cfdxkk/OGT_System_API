package OGTSystem.repository;

import OGTSystem.entity.GroupEventEditEntity;
import OGTSystem.entity.GroupEventEntity;
import OGTSystem.entity.GroupInfoEntity;
import OGTSystem.mapper.GroupEventMapper;
import OGTSystem.mapper.GroupInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GroupEventRepository {

    @Autowired
    GroupEventMapper mapper;

    public List<GroupEventEntity> getEventsByGroupId(String groupId){
        return mapper.selectEventsByGroupId(groupId);
    }

    public int createGroupEvent(GroupEventEntity groupevententity){
        return mapper.createGroupEvent(groupevententity);
    }

    public int editGroupEvent(GroupEventEditEntity groupeventeditentity) { return mapper.editGroupEvent(groupeventeditentity); }
    public int removeByEventId(String eventId) { return mapper.removeByEventId(eventId); }

}
