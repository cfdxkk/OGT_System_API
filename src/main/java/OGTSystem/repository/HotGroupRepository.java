package OGTSystem.repository;

import OGTSystem.entity.GroupEventEntity;
import OGTSystem.entity.HotGroupEntity;
import OGTSystem.mapper.GroupEventMapper;
import OGTSystem.mapper.HotGroupMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class HotGroupRepository {

    @Autowired
    HotGroupMapper mapper;

    public List<HotGroupEntity> getHotGroup(String groupId){

        return mapper.selectHotGroup(groupId);
    }

    public List<HotGroupEntity> getAllHotGroup(String groupId){

        return mapper.selectAllHotGroup(groupId);
    }

    public int createHotGroup(HotGroupEntity hotgroupentity){
        return mapper.createHotGroup(hotgroupentity);
    }

    public int deleteHotGroup(HotGroupEntity hotgroupentity){
        return mapper.deleteHotGroup(hotgroupentity);
    }

}
