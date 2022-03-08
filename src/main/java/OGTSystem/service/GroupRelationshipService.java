package OGTSystem.service;

import OGTSystem.entity.*;
import OGTSystem.repository.GroupInfoRepository;
import OGTSystem.repository.GroupRelationshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class GroupRelationshipService {
    @Autowired
    GroupRelationshipRepository repository;

    @Autowired
    UserInfoService userinfoservice;

    @Autowired
    UserAuthService userauthservice;

    @Autowired
    GroupUserIdentityService groupuseridentityservice;

    @Autowired
    GroupInfoRepository groupinforepository;



    public List<GroupRelationshipEntity> getGroupRelationshipByUUID(String uuid){
        return repository.getGroupRelationshipByUUID(uuid);
    }

    public int createGroupRelationship(GroupRelationshipEntity grouprelationshipentity){

        System.out.println("UUID is: " + grouprelationshipentity.getUserId());
        List<GroupRelationshipEntity> groupRelationships = repository.getGroupRelationshipByUUID(grouprelationshipentity.getUserId());
        for(GroupRelationshipEntity groupRelationship : groupRelationships){
            if ("".equals(grouprelationshipentity.getGroupId())){
                return 0;
            } else {
                if (grouprelationshipentity.getGroupId().equals(groupRelationship.getGroupId())){
                    return 0;
                }
            }
        }
        return repository.createGroupRelationship(grouprelationshipentity);
    }


    public List<UserSafeInfoGroupEntity> getGroupUsersByGroupId (GroupRelationshipSearchEntity grouprelationshipsearchentity) {

        if(userauthservice.userAuthCheck(grouprelationshipsearchentity.getSearcherId(), grouprelationshipsearchentity.getSearcherToken())) {
            List<GroupRelationshipEntity> groupRelationships = repository.getGroupRelationshipByGroupId(grouprelationshipsearchentity.getGroupId());

            List<UserSafeInfoGroupEntity> usersInGroup = new ArrayList<>();
            for(GroupRelationshipEntity relationships : groupRelationships) {
                List<UserSafeInfoGroupEntity> userInfos = userinfoservice.getByUUID2(relationships.getUserId());
                if (userInfos.size() > 0) {
                    UserSafeInfoGroupEntity userInfo = userInfos.get(0);

                    GroupUserIdentityEntity groupuseridentityentity = new GroupUserIdentityEntity();
                    groupuseridentityentity.setGroupId(grouprelationshipsearchentity.getGroupId());
                    groupuseridentityentity.setGroupAdminId(userInfo.getUserId());

                    List<GroupUserIdentityEntity> groupIdentityList = groupuseridentityservice.getUserIdentity(groupuseridentityentity);

                    if (groupIdentityList.size() > 0) {
                        userInfo.setUserIdentityInGroup(groupIdentityList.get(0).getGroupAdminType());
                    }

                    usersInGroup.add(userInfo);
                }
            }
            return usersInGroup;
        } else {
            System.out.println("在获取群聊中的用户信息时，用户验证未通过");
            return null;
        }
    }

    public void removeByEditEntity(GroupRelationshipEditEntity grouprelationshipeditentity) {
        if(userauthservice.userAuthCheck(grouprelationshipeditentity.getAdminUserId(), grouprelationshipeditentity.getAdminToken())) {
            if (
                    groupuseridentityservice.checkUserGroupLeader(grouprelationshipeditentity.getGroupId(), grouprelationshipeditentity.getAdminUserId())
                            || groupuseridentityservice.checkUserGroupAdmin(grouprelationshipeditentity.getGroupId(), grouprelationshipeditentity.getAdminUserId())
            )
                repository.removeByEditEntity(grouprelationshipeditentity);
        }

    }

    public boolean checkUserInGroup(String userId, String groupId) {
        if ("".equals(userId) || "".equals(groupId)) {
            return false;
        } else {
            GroupRelationshipEntity entity = new GroupRelationshipEntity();
            entity.setUserId(userId);
            entity.setGroupId(groupId);
            List<GroupRelationshipEntity> relationship = repository.getGroupRelationshipByGroupRelationshipEntity(entity);
            if (relationship.size() > 0) {
                return true;
            } else {
                return false;
            }
        }
    }

    public boolean exitGroup(GroupRelationshipEditEntity grouprelationshipeditentity) {
        if (userauthservice.userAuthCheck(grouprelationshipeditentity.getAdminUserId(), grouprelationshipeditentity.getAdminToken())) {

            repository.removeByEditEntity(grouprelationshipeditentity);

            GroupUserIdentityEditEntity groupuseridentityeditentity = new GroupUserIdentityEditEntity();
            groupuseridentityeditentity.setGroupId(grouprelationshipeditentity.getGroupId());
            groupuseridentityeditentity.setGroupAdminId(grouprelationshipeditentity.getAdminUserId());
            groupuseridentityservice.removeUserIdentity(groupuseridentityeditentity);

            return true;

        } else {
            System.out.println("非法用户");
            return false;
        }
    }

    public boolean deleteGroup(GroupRelationshipEditEntity grouprelationshipeditentity) {
        if (userauthservice.userAuthCheck(grouprelationshipeditentity.getAdminUserId(), grouprelationshipeditentity.getAdminToken())) {
            if(groupuseridentityservice.checkUserGroupLeader(grouprelationshipeditentity.getGroupId(), grouprelationshipeditentity.getAdminUserId())) {
                groupinforepository.removeByGroupId(grouprelationshipeditentity.getGroupId());
                repository.removeByGroupId(grouprelationshipeditentity.getGroupId());
                groupuseridentityservice.removeGroupAllIdentity(grouprelationshipeditentity.getGroupId());

                return true;
            } else {
                System.out.println("用户并非群主");
                return false;
            }
        } else {
            System.out.println("非法用户");
            return false;
        }
    }


}