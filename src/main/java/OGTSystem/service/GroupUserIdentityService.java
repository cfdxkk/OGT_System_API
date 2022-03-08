package OGTSystem.service;

import OGTSystem.entity.GroupInfoEntity;
import OGTSystem.entity.GroupUserIdentityEditEntity;
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

    @Autowired
    UserAuthService userauthservice;

    public int createGroupUserIdentity(GroupUserIdentityEntity groupuseridentityentity){
        return groupuseridentityrepository.createGroupUserIdentity(groupuseridentityentity);
    }

    public List<GroupUserIdentityEntity> getUserIdentity(GroupUserIdentityEntity groupuseridentityentity){
        return groupuseridentityrepository.getUserIdentity(groupuseridentityentity);
    }

    public int removeUserIdentity(GroupUserIdentityEditEntity groupuseridentityeditentity) {
        return groupuseridentityrepository.removeUserIdentity(groupuseridentityeditentity);
    }


    public int removeGroupAllIdentity(String groupId) {
        return groupuseridentityrepository.removeGroupAllIdentity(groupId);
    }


    public int getUserIdentityType (GroupUserIdentityEntity groupuseridentityentity){
        if (userauthservice.userAuthCheck(groupuseridentityentity.getGroupAdminId(), groupuseridentityentity.getToken())) {
            if ("".equals(groupuseridentityentity.getGroupId()) || "".equals(groupuseridentityentity.getGroupAdminId())) {
                return 0;
            } else {
                List<GroupUserIdentityEntity> identity = groupuseridentityrepository.getUserIdentity(groupuseridentityentity);
                if (identity.size() > 0) {
                    return identity.get(0).getGroupAdminType();
                } else {
                    return 0;
                }
            }
        } else {
            return 0;
        }
    }

    public boolean checkUserGroupLeader(String groupId, String userId) {
        if ("".equals(groupId) || "".equals(userId) || groupId == null || userId == null) {
            return false;
        } else {
            GroupUserIdentityEntity groupuseridentityentity = new GroupUserIdentityEntity();
            groupuseridentityentity.setGroupId(groupId);
            groupuseridentityentity.setGroupAdminId(userId);

            List<GroupUserIdentityEntity> groupIdentityList = this.getUserIdentity(groupuseridentityentity);
            if (groupIdentityList.size() > 0) {
                if (groupIdentityList.get(0).getGroupAdminType() == 1) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }

    public boolean checkUserGroupAdmin(String groupId, String userId) {
        if ("".equals(groupId) || "".equals(userId) || groupId == null || userId == null) {
            return false;
        } else {
            GroupUserIdentityEntity groupuseridentityentity = new GroupUserIdentityEntity();
            groupuseridentityentity.setGroupId(groupId);
            groupuseridentityentity.setGroupAdminId(userId);

            List<GroupUserIdentityEntity> groupIdentityList = this.getUserIdentity(groupuseridentityentity);
            if (groupIdentityList.size() > 0) {
                if (groupIdentityList.get(0).getGroupAdminType() == 2) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }
    }
    public void addNewAdmin(GroupUserIdentityEditEntity groupuseridentityeditentity) {
        if (userauthservice.userAuthCheck(groupuseridentityeditentity.getUserId(), groupuseridentityeditentity.getToken())) {
            if(groupuseridentityeditentity.getUserId().equals(groupuseridentityeditentity.getGroupAdminId())) {
                System.out.println("笑死, [" + groupuseridentityeditentity.getUserId() + "] 正试图将一个群主设为管理员");
            } else {
                if (this.checkUserGroupLeader(groupuseridentityeditentity.getGroupId(), groupuseridentityeditentity.getUserId())) {
                    GroupUserIdentityEntity groupuseridentityentity = new GroupUserIdentityEntity();
                    groupuseridentityentity.setGroupAdminType(2);
                    groupuseridentityentity.setGroupId(groupuseridentityeditentity.getGroupId());
                    groupuseridentityentity.setGroupAdminId(groupuseridentityeditentity.getGroupAdminId());
                    groupuseridentityrepository.createGroupUserIdentity(groupuseridentityentity);
                }
            }
        }
    }

    public void removeGroupAdmin(GroupUserIdentityEditEntity groupuseridentityeditentity) {
        if (userauthservice.userAuthCheck(groupuseridentityeditentity.getUserId(), groupuseridentityeditentity.getToken())) {
            if(groupuseridentityeditentity.getUserId().equals(groupuseridentityeditentity.getGroupAdminId())) {
                System.out.println("笑死, [" + groupuseridentityeditentity.getUserId() + "] 正试图将一个群主移除管理员身份");
            } else {
                if(this.checkUserGroupLeader(groupuseridentityeditentity.getGroupId(), groupuseridentityeditentity.getUserId())) {
                    groupuseridentityrepository.removeUserIdentity(groupuseridentityeditentity);
                }
            }
        }
    }
}