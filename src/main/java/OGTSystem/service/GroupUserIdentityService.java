package OGTSystem.service;

import OGTSystem.entity.GroupInfoEntity;
import OGTSystem.entity.GroupUserIdentityEntity;
import OGTSystem.repository.GroupInfoRepository;
import OGTSystem.repository.GroupUserIdentityRepository;
import OGTSystem.vo.GroupInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GroupUserIdentityService {
    @Autowired
    GroupUserIdentityRepository groupuseridentityrepository;

    public int createGroupUserIdentity(GroupUserIdentityEntity groupuseridentityentity){
        return groupuseridentityrepository.createGroupUserIdentity(groupuseridentityentity);
    }

    public List<GroupUserIdentityEntity> getUserIdentity(GroupUserIdentityEntity groupuseridentityentity){
        return groupuseridentityrepository.getUserIdentity(groupuseridentityentity);
    }


}