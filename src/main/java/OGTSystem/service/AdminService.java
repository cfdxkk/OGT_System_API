package OGTSystem.service;

import OGTSystem.entity.*;
import OGTSystem.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminService {
//    @Autowired
//    GroupInfoRepository repository;

    @Autowired
    UserInfoService userinfoservice;

    @Autowired
    UserInfoRepository userinforepository;

    @Autowired
    UserAuthService userauthservice;

    @Autowired
    GroupRelationshipService grouprelationshipservice;

    @Autowired
    AdminService groupinfoservice;

    @Autowired
    GroupUserIdentityService groupuseridentityservice;

    @Autowired
    HotGroupRepository hotgrouprepository;

    @Autowired
    GroupInfoRepository groupinforepository;

    @Autowired
    GroupRelationshipRepository grouprelationshiprepository;

    @Autowired
    GroupEventRepository groupeventrepository;

    public boolean adminAuthCheck(String userId, String token){
        if (userauthservice.userAuthCheck(userId,token)) {
            List<UserSafeInfoEntity> userInfo = userinfoservice.getByUUID(userId);
            if (userInfo.size() > 0) {
                String userAdminFlag = userInfo.get(0).getUserAdminFlag();
                if ("1".equals(userAdminFlag)){
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    public boolean setHotGroup(HotGroupEntity hotgroupentity) {
        if (this.adminAuthCheck(hotgroupentity.getUserId(), hotgroupentity.getToken())){
            groupinforepository.setHotGroup(hotgroupentity.getGroupId());
            return true;
        } else {
            return false;
        }
    }

    public boolean removeHotGroup(HotGroupEntity hotgroupentity) {
        if (this.adminAuthCheck(hotgroupentity.getUserId(), hotgroupentity.getToken())){
            groupinforepository.unSetHotGroup(hotgroupentity.getGroupId());
            return true;
        } else {
            return false;
        }
    }

    public List<UserInfoEntity> getUserInfoAndAuth(UserInfoAdminEntity userinfoadminhentity) {
        if (this.adminAuthCheck(userinfoadminhentity.getAdminUserId(), userinfoadminhentity.getToken())){
            UserInfoEntity userinfoentity = new UserInfoEntity();
            userinfoentity.setUserId(userinfoadminhentity.getUserId());
            userinfoentity.setUserNo(userinfoadminhentity.getUserNo());
            userinfoentity.setUsername(userinfoadminhentity.getUsername());
            return userinforepository.getByUserInfoEntity(userinfoentity);
        } else {
            return null;
        }
    }

    public void setUserBaned(UserInfoAdminEntity userinfoadminhentity) {
        if (this.adminAuthCheck(userinfoadminhentity.getAdminUserId(), userinfoadminhentity.getToken())){
            userinforepository.setUserBaned(userinfoadminhentity);
        }
    }

    public void setUserUnBaned(UserInfoAdminEntity userinfoadminhentity) {
        if (this.adminAuthCheck(userinfoadminhentity.getAdminUserId(), userinfoadminhentity.getToken())) {
            userinforepository.setUserUnBaned(userinfoadminhentity);
        }
    }

    public void removeUser(UserInfoAdminEntity userinfoadminhentity) {

        if (this.adminAuthCheck(userinfoadminhentity.getAdminUserId(), userinfoadminhentity.getToken())) {
            userinforepository.removeUser(userinfoadminhentity);
            userinforepository.removeUserInAuth(userinfoadminhentity);
        }
    }

    public void editUserInfo(UserInfoEditEntity userinfoeditentity) {
        if (this.adminAuthCheck(userinfoeditentity.getAdminUserId(), userinfoeditentity.getAdminToken())) {
            if(
                    "".equals(userinfoeditentity.getPassword())
                            || "".equals(userinfoeditentity.getUserId())
                            || "".equals(userinfoeditentity.getUsername())
            ){
                return;
            }
            userinforepository.setUserInfoInUserNotOftenEdit(userinfoeditentity);
            userinforepository.setUserInfoInUserOftenEdit(userinfoeditentity);
        }
    }

    public void editGroupInfo(GroupInfoEditEntity groupinfoeditentity) {
        if (this.adminAuthCheck(groupinfoeditentity.getAdminUserId(), groupinfoeditentity.getAdminToken())) {
            groupinforepository.editGroupInfo(groupinfoeditentity);
        }
    }

    public void removeByGroupId(GroupInfoEditEntity groupinfoeditentity) {
        if (this.adminAuthCheck(groupinfoeditentity.getAdminUserId(), groupinfoeditentity.getAdminToken())) {
            if( groupinfoeditentity.getGroupId() != null && !("".equals(groupinfoeditentity.getGroupId())) )
            groupinforepository.removeByGroupId(groupinfoeditentity.getGroupId());
            grouprelationshiprepository.removeByGroupId(groupinfoeditentity.getGroupId());
        }
    }

    public void editGroupEvent(GroupEventEditEntity groupeventeditentity) {
        if (this.adminAuthCheck(groupeventeditentity.getAdminUserId(), groupeventeditentity.getAdminToken())) {
            if( groupeventeditentity.getEventId() != null && !("".equals(groupeventeditentity.getEventId())) ){
                groupeventrepository.editGroupEvent(groupeventeditentity);
            }
        }
    }


    public void removeByEventId(GroupEventEditEntity groupeventeditentity) {
        if (this.adminAuthCheck(groupeventeditentity.getAdminUserId(), groupeventeditentity.getAdminToken())) {
            if( groupeventeditentity.getEventId() != null && !("".equals(groupeventeditentity.getEventId())) ){
                groupeventrepository.removeByEventId(groupeventeditentity.getEventId());
            }
        }
    }

}