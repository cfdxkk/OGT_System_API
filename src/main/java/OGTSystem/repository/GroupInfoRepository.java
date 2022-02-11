package OGTSystem.repository;

import OGTSystem.entity.GroupInfoEntity;
import OGTSystem.mapper.GroupInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GroupInfoRepository {

    @Autowired
    GroupInfoMapper mapper;

    public List<GroupInfoEntity> getByGroupId(String groupId){
        return mapper.selectByGroupId(groupId);
    }

    public List<GroupInfoEntity> getByGroupName(String groupName){
        return mapper.selectByGroupName(groupName);
    }

    public int createGroup(GroupInfoEntity groupinfoentity){
        return mapper.createGroup(groupinfoentity);
    }

}
