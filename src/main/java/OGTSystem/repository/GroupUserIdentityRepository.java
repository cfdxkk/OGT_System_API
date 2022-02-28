package OGTSystem.repository;

import OGTSystem.entity.GroupInfoEntity;
import OGTSystem.entity.GroupUserIdentityEntity;
import OGTSystem.mapper.GroupInfoMapper;
import OGTSystem.mapper.GroupUserIdentityMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class GroupUserIdentityRepository {

    @Autowired
    GroupUserIdentityMapper mapper;


    public int createGroupUserIdentity(GroupUserIdentityEntity groupuseridentityentity){
        return mapper.insertGroupUserIdentity(groupuseridentityentity);
    }

}
