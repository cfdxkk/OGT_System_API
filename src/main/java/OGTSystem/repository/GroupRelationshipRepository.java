package OGTSystem.repository;

import OGTSystem.entity.GroupInfoEntity;
import OGTSystem.entity.GroupRelationshipEditEntity;
import OGTSystem.entity.GroupRelationshipEntity;
import OGTSystem.mapper.GroupInfoMapper;
import OGTSystem.mapper.GroupRelationshipMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GroupRelationshipRepository {

    @Autowired
    GroupRelationshipMapper mapper;

    public List<GroupRelationshipEntity> getGroupRelationshipByUUID(String uuid){
        return mapper.selectGroupRelationshipByUUID(uuid);
    }

    public List<GroupRelationshipEntity> getGroupRelationshipByGroupId(String groupId){
        return mapper.selectGroupRelationshipByGroupId(groupId);
    }

    public List<GroupRelationshipEntity> getGroupRelationshipByGroupRelationshipEntity(GroupRelationshipEntity entity){
        return mapper.selectGroupRelationshipByGroupRelationshipEntity(entity);
    }



    public int createGroupRelationship(GroupRelationshipEntity grouprelationshipentity){
        return mapper.createGroupRelationship(grouprelationshipentity);
    }

    public int removeByGroupId(String groupId) {
        return mapper.removeByGroupId(groupId);
    }

    public int removeByEditEntity(GroupRelationshipEditEntity grouprelationshipeditentity) { return mapper.removeByEditEntity(grouprelationshipeditentity); }

}
