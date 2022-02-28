package OGTSystem.service;

import OGTSystem.controller.GroupController;
import OGTSystem.entity.*;
import OGTSystem.repository.GroupInfoRepository;
import OGTSystem.util.UserToken;
import OGTSystem.vo.GroupCreateVo;
import OGTSystem.vo.GroupInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class GroupInfoService {
    @Autowired
    GroupInfoRepository repository;

    @Autowired
    UserInfoService userinfoservice;

    @Autowired
    UserAuthService userauthservice;

    // 创建线程安全的GroupRelationshipService对象
//    private static GroupRelationshipService grouprelationshipservice;
//    @Autowired
//    public void setGrouprelationshipservice(GroupRelationshipService grouprelationshipservice){
//        GroupInfoService.grouprelationshipservice = grouprelationshipservice;
//    }
//
//    // 创建线程安全的GroupInfoService对象
//    private static GroupInfoService groupinfoservice;
//    @Autowired
//    public void setGroupinfoservice(GroupInfoService groupinfoservice){
//        GroupInfoService.groupinfoservice = groupinfoservice;
//    }

    @Autowired
    GroupRelationshipService grouprelationshipservice;

    @Autowired
    GroupInfoService groupinfoservice;

    @Autowired
    GroupUserIdentityService groupuseridentityservice;

    public GroupCreateVo createGroup(GroupInfoVo groupinfovo){

        if (userauthservice.userAuthCheck(groupinfovo.getGroupCreator(), groupinfovo.getToken())){
            // uuid gen
            String groupId = "";
            groupId = UUID.randomUUID().toString().replaceAll("-", "");


            // check
            if ( "".equals(groupId) ){
                System.out.println("生成群ID时出错");
                return null;
            } else {

                GroupInfoEntity groupinfoentity = new GroupInfoEntity();
                groupinfoentity.setGroupId(groupId);
                groupinfoentity.setGroupCreator(groupinfovo.getGroupCreator());
                groupinfoentity.setGroupName(groupinfovo.getGroupName());
                groupinfoentity.setGroupIntroduction(groupinfovo.getGroupIntroduction());
                repository.createGroup(groupinfoentity);


                // 一旦创建成功，则把创建者加入群聊关系数据库，人后在特殊权限用户表记录这个群里拥有特殊权限的用户，并返回这个用户加入的所有群聊列表
                List<GroupInfoVo> groupVoList = new ArrayList<GroupInfoVo>();

                GroupRelationshipEntity grouprelationshipentity = new GroupRelationshipEntity();
                grouprelationshipentity.setGroupId(groupId);
                grouprelationshipentity.setUserId(groupinfovo.getGroupCreator());
                // join
                grouprelationshipservice.createGroupRelationship(grouprelationshipentity);  // 加入这个群聊
                // 获取最新的群关系
                List<GroupRelationshipEntity> groupRelationshipList = grouprelationshipservice.getGroupRelationshipByUUID(grouprelationshipentity.getUserId());
                // 遍历群关系获取最新的群列表，然后塞到list里
                for(GroupRelationshipEntity groupRelationship : groupRelationshipList) {
                    // entity2Vo

                    GroupInfoEntity groupInfoE = groupinfoservice.getByGroupId(groupRelationship.getGroupId()).get(0);
                    GroupInfoVo groupinfoV = new GroupInfoVo();
                    groupinfoV.setGroupId(groupInfoE.getGroupId());
                    groupinfoV.setGroupNo(groupInfoE.getGroupNo());
                    groupinfoV.setGroupName(groupInfoE.getGroupName());
                    groupinfoV.setGroupIntroduction(groupInfoE.getGroupIntroduction());
                    groupinfoV.setGroupAvatar(groupInfoE.getGroupAvatar());
                    groupinfoV.setGroupCreator(groupInfoE.getGroupCreator());

                    groupVoList.add(groupinfoV);
                }

                GroupUserIdentityEntity groupuseridentityentity = new GroupUserIdentityEntity();
                groupuseridentityentity.setGroupId(groupId);
                groupuseridentityentity.setGroupAdminId(groupinfovo.getGroupCreator());
                groupuseridentityentity.setGroupAdminType(1);  // 1 代表群主
                groupuseridentityservice.createGroupUserIdentity(groupuseridentityentity);  // 记录这个群里拥有特殊权限的用户


                GroupCreateVo groupcreatevo = new GroupCreateVo();
                groupcreatevo.setGroupId(groupId);
                groupcreatevo.setGroupList(groupVoList);


                return groupcreatevo;
            }
        } else {

            System.out.println("创建群聊时，用户安全验证未通过");
            return null;
        }
    }

    public List<GroupInfoEntity> getByGroupName(String groupName){
        // check
        if ( "".equals(groupName) ){
            return null;
        } else {
            groupName = "%" + groupName + "%";
            return repository.getByGroupName(groupName);
        }
    }

    public List<GroupInfoEntity> getByGroupId(String groupId){
        // check
        if ( "".equals(groupId) ){
            return null;
        } else {
            return repository.getByGroupId(groupId);
        }
    }


}