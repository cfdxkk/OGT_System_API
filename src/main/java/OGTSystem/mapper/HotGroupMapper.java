package OGTSystem.mapper;

import OGTSystem.entity.GroupEventEntity;
import OGTSystem.entity.HotGroupEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface HotGroupMapper {

    List<HotGroupEntity> selectHotGroup(String groupId);
    List<HotGroupEntity> selectAllHotGroup(String groupId);
    int createHotGroup(HotGroupEntity group);
    int deleteHotGroup(HotGroupEntity group);

}
