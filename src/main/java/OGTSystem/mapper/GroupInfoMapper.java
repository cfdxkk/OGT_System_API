package OGTSystem.mapper;

import OGTSystem.entity.GroupInfoEditEntity;
import OGTSystem.entity.GroupInfoEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GroupInfoMapper {

    List<GroupInfoEntity> selectByGroupId(String groupId);
    List<GroupInfoEntity> selectByGroupName(String groupName);
    List<GroupInfoEntity> selectHotGroup();
    int createGroup(GroupInfoEntity group);
    int setHotGroup(String groupId);
    int unSetHotGroup(String groupId);

    int editGroupInfo(GroupInfoEditEntity entity);

    int removeByGroupId(String groupId);

}
