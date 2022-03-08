package OGTSystem.mapper;

import OGTSystem.entity.GroupInfoEntity;
import OGTSystem.entity.GroupRelationshipEditEntity;
import OGTSystem.entity.GroupRelationshipEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GroupRelationshipMapper {

    List<GroupRelationshipEntity> selectGroupRelationshipByUUID(String userId);
    List<GroupRelationshipEntity> selectGroupRelationshipByGroupId(String groupId);
    List<GroupRelationshipEntity> selectGroupRelationshipByGroupRelationshipEntity(GroupRelationshipEntity entity);

    int createGroupRelationship(GroupRelationshipEntity group);

    int removeByGroupId(String groupId);
    int removeByEditEntity(GroupRelationshipEditEntity entity);

}
