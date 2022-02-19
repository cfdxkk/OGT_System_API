package OGTSystem.mapper;

import OGTSystem.entity.GroupInfoEntity;
import OGTSystem.entity.GroupRelationshipEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GroupRelationshipMapper {

    List<GroupRelationshipEntity> selectGroupRelationshipByUUID(String userId);
    List<GroupRelationshipEntity> selectGroupRelationshipByGroupId(String groupId);
    int createGroupRelationship(GroupRelationshipEntity group);

}
