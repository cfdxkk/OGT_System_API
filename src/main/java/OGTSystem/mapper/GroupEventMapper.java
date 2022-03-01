package OGTSystem.mapper;

import OGTSystem.entity.GroupEventEntity;
import OGTSystem.entity.GroupInfoEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GroupEventMapper {

    List<GroupEventEntity> selectEventsByGroupId(String groupId);
    int createGroupEvent(GroupEventEntity group);

}
