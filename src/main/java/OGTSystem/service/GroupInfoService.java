package OGTSystem.service;

import OGTSystem.controller.GroupController;
import OGTSystem.entity.*;
import OGTSystem.interFuck.OssService;
import OGTSystem.repository.GroupInfoRepository;
import OGTSystem.util.UserToken;
import OGTSystem.vo.GroupCreateVo;
import OGTSystem.vo.GroupInfoVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
public class GroupInfoService {
    @Autowired
    GroupInfoRepository repository;

    @Autowired
    UserInfoService userinfoservice;

    @Autowired
    UserAuthService userauthservice;

    @Autowired
    OssService ossservice;

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
                groupinfoentity.setGroupAvatar(groupinfovo.getGroupAvatar());
                groupinfoentity.setGroupAvatarOrigin(groupinfovo.getGroupAvatarOrigin());
                groupinfoentity.setGroupIntroduction(groupinfovo.getGroupIntroduction());
                repository.createGroup(groupinfoentity);



                // 一旦创建成功，则把创建者加入群聊关系数据库，然后在特殊权限用户表记录这个群里拥有特殊权限的用户，并返回这个用户加入的所有群聊列表
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
                    groupinfoV.setGroupAvatarOrigin(groupInfoE.getGroupAvatarOrigin());
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

    public GroupInfoEntity getByGroupIdSafe(GroupInfoSearchEntity groupinfosearchentity){

        if (userauthservice.userAuthCheck(groupinfosearchentity.getUserId(), groupinfosearchentity.getToken())) {
            // check
            if ( "".equals(groupinfosearchentity.getGroupId()) ){
                return null;
            } else {
                List<GroupInfoEntity> groupInfoList = repository.getByGroupId(groupinfosearchentity.getGroupId());
                if (groupInfoList.size() > 0) {
                    return groupInfoList.get(0);
                } else {
                    return null;
                }
            }
        } else {
            return null;
        }
    }

    public List<GroupInfoEntity> getHotGroup(){
        return repository.getHotGroup();
    }

    public Map<String, String> uploadGroupAvatar(MultipartFile file, MultipartFile fileOrigin, String userId, String token){

        System.out.println("userId [" + userId + "]  -  token [" + token + "]");

        if (userauthservice.userAuthCheck(userId, token)) {
            Map<String, String> result = new HashMap<>();

            //获取小上传文件 inputStream
            InputStream inputStream = null;
            try {
                inputStream = file.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 小头像上传到阿里云oss
            result.put("small", ossservice.uploadFileAvatar(inputStream, file.getOriginalFilename()));

            //获取大上传文件 inputStreamOrigin
            InputStream inputStreamOrigin = null;
            try {
                inputStreamOrigin = fileOrigin.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 头像原图上传到阿里云oss
            result.put("full", ossservice.uploadFileAvatar(inputStreamOrigin, fileOrigin.getOriginalFilename()));

            return result;
        } else {
            System.out.println("有人可能正在利用群聊头像上传接口进行攻击！");
            return null;
        }
    }


}