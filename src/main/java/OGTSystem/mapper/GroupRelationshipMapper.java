package OGTSystem.mapper;

import OGTSystem.entity.GroupInfoEntity;
import OGTSystem.entity.GroupRelationshipEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GroupRelationshipMapper {

    List<GroupRelationshipEntity> selectGroupRelationshipByUUID(String UUID);
    int createGroupRelationship(GroupRelationshipEntity group);

}
