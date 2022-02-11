package OGTSystem.mapper;

import OGTSystem.entity.GroupInfoEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GroupInfoMapper {

    List<GroupInfoEntity> selectByGroupId(String groupId);
    List<GroupInfoEntity> selectByGroupName(String groupName);
    int createGroup(GroupInfoEntity group);

}
