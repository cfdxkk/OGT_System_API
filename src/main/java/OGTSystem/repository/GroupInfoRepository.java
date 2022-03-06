package OGTSystem.repository;

import OGTSystem.entity.GroupInfoEditEntity;
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

    public List<GroupInfoEntity> getHotGroup(){
        return mapper.selectHotGroup();
    }

    public int createGroup(GroupInfoEntity groupinfoentity){
        return mapper.createGroup(groupinfoentity);
    }

    public int setHotGroup(String groupId) { return mapper.setHotGroup(groupId); }

    public int unSetHotGroup(String groupId) { return mapper.unSetHotGroup(groupId); }

    public int editGroupInfo(GroupInfoEditEntity groupinfoeditentity) { return mapper.editGroupInfo(groupinfoeditentity); }

    public int removeByGroupId(String groupId) { return mapper.removeByGroupId(groupId); }
}
