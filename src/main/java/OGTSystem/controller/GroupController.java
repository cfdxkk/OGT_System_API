package OGTSystem.controller;

import OGTSystem.entity.*;
import OGTSystem.interFuck.OssService;
import OGTSystem.message.sender.group.AsynchronousGroupMessageSender;
import OGTSystem.service.*;
import OGTSystem.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/group")
@Scope(value = "prototype")   // 提供线程安全，每次访问controller都会创建一个新容器
public class GroupController {

    // 创建线程安全的UserAuthService对象
    private static UserAuthService userauthservice;
    @Autowired
    public void setUserauthservice(UserAuthService userauthservice){
        GroupController.userauthservice = userauthservice;
    }

    // 创建线程安全的UserAuthService对象
    private static UserInfoService userinfoservice;
    @Autowired
    public void setUserauthservice(UserInfoService userinfoservice){
        GroupController.userinfoservice = userinfoservice;
    }

    // 创建线程安全的 UserCreateService对象
    private static UserCreateService usercreateservice;
    @Autowired
    public void setUsercreateservice(UserCreateService usercreateservice){
        GroupController.usercreateservice = usercreateservice;
    }

    // 创建线程安全的GroupInfoService对象
    private static GroupInfoService groupinfoservice;
    @Autowired
    public void setGroupinfoservice(GroupInfoService groupinfoservice){
        GroupController.groupinfoservice = groupinfoservice;
    }

    // 创建线程安全的GroupRelationshipService对象
    private static GroupRelationshipService grouprelationshipservice;
    @Autowired
    public void setGroupinfoservice(GroupRelationshipService grouprelationshipservice){
        GroupController.grouprelationshipservice = grouprelationshipservice;
    }


    // 创建线程安全的 GroupMessageService 对象
    private static GroupMessageService groupmessageservice;
    @Autowired
    public void setGroupmessageservice(GroupMessageService groupmessageservice){
        GroupController.groupmessageservice = groupmessageservice;
    }

    // 创建线程安全的 GroupUserIdentityService 对象
    private static GroupUserIdentityService groupuseridentityservice;
    @Autowired
    public void setGroupuseridentityservice(GroupUserIdentityService groupuseridentityservice){
        GroupController.groupuseridentityservice = groupuseridentityservice;
    }

    @CrossOrigin
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.OK)
    public GroupCreateVo createGroup(
            @RequestBody GroupInfoVo groupinfovo
    ){
        return groupinfoservice.createGroup(groupinfovo);
    }

    @CrossOrigin
    @GetMapping("/search")
    @ResponseStatus(HttpStatus.OK)
    public List<GroupInfoEntity> searchGroup(
            @RequestParam(name = "groupName",required = false) String groupName
    ){
        return groupinfoservice.getByGroupName(groupName);
    }


    @CrossOrigin
    @PostMapping("/join")
    @ResponseStatus(HttpStatus.OK)
    public List<GroupInfoEntity> joinGroup(
            @RequestBody GroupRelationshipEntity grouprelationshipentity
    ){

        List<GroupInfoEntity> groupList = new ArrayList<GroupInfoEntity>();

        if( !("".equals(grouprelationshipentity.getUserId())) ){
            // join
            grouprelationshipservice.createGroupRelationship(grouprelationshipentity);
            // 获取最新的群关系
            List<GroupRelationshipEntity> groupRelationshipList = grouprelationshipservice.getGroupRelationshipByUUID(grouprelationshipentity.getUserId());
            // 遍历群关系获取最新的群列表
            for(GroupRelationshipEntity groupRelationship : groupRelationshipList) {
                groupList.add(groupinfoservice.getByGroupId(groupRelationship.getGroupId()).get(0));
            }
        }
        return groupList;
    }

    @CrossOrigin
    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public List<GroupInfoEntity> getGroupsList(
            @RequestParam(name = "userId",required = false) String userId
    ){
        List<GroupRelationshipEntity> groupRelationshipList = grouprelationshipservice.getGroupRelationshipByUUID(userId);
        List<GroupInfoEntity> groupInfoList = new ArrayList<GroupInfoEntity>();
        for(GroupRelationshipEntity groupRelationship : groupRelationshipList) {
            groupInfoList.add(groupinfoservice.getByGroupId(groupRelationship.getGroupId()).get(0));
        }
        return groupInfoList;
    }

    @CrossOrigin
    @PostMapping("/message")
    @ResponseStatus(HttpStatus.OK)
    public boolean sentGroupMessage(
            @RequestBody GroupMessageVo groupmessagevo
    ){
        return groupmessageservice.MessageToGroupUser(groupmessagevo);
    }

    @CrossOrigin
    @PostMapping("/offlineMessage")
    @ResponseStatus(HttpStatus.OK)
    public HashMap<String, List<GroupMessageVo>> getGroupOfflineMessage(
            @RequestBody GroupMessageVo groupmessagevo
    ){
        return groupmessageservice.getOfflineMessage(groupmessagevo.getUuidTo(), groupmessagevo.getToken());
    }

    @CrossOrigin
    @PostMapping("/event")
    @ResponseStatus(HttpStatus.OK)
    public List<GroupEventVo> getGroupEvent(
            @RequestBody GroupEventGetVo groupeventgetvo
    ){
        return groupmessageservice.getEvent(groupeventgetvo.getGroupId(), groupeventgetvo.getUserId(), groupeventgetvo.getToken());
    }

    @CrossOrigin
    @GetMapping("/hotGroupList")
    @ResponseStatus(HttpStatus.OK)
    public List<GroupInfoEntity> getHotGroupList(
    ){
        return groupinfoservice.getHotGroup();
    }


    @CrossOrigin
    @PostMapping("/getGroupUsersByGroupId")
    @ResponseStatus(HttpStatus.OK)
    public List<UserSafeInfoGroupEntity> getGroupRelationshipByGroupId(
            @RequestBody GroupRelationshipSearchEntity grouprelationshipsearchentity
    ){
        return grouprelationshipservice.getGroupUsersByGroupId(grouprelationshipsearchentity);
    }


    @CrossOrigin
    @PostMapping("/getGroupIdentity")
    @ResponseStatus(HttpStatus.OK)
    public int checkGroupController(
            @RequestBody GroupUserIdentityEntity groupuseridentityentity
    ){
        return groupuseridentityservice.getUserIdentityType(groupuseridentityentity);
    }

    @CrossOrigin
    @PostMapping("/getGroupInfoByGroupId")
    @ResponseStatus(HttpStatus.OK)
    public GroupInfoEntity getGroupInfoByGroupId(
            @RequestBody GroupInfoSearchEntity groupinfosearchentity
    ){
        return groupinfoservice.getByGroupIdSafe(groupinfosearchentity);
    }

    @CrossOrigin
    @PostMapping("/removeUserFromGroup")
    @ResponseStatus(HttpStatus.OK)
    public void removeUserFromGroup(
            @RequestBody GroupRelationshipEditEntity grouprelationshipeditentity
    ){
        grouprelationshipservice.removeByEditEntity(grouprelationshipeditentity);
    }

    @CrossOrigin
    @PostMapping("/addNewAdmin")
    @ResponseStatus(HttpStatus.OK)
    public void addNewAdmin(
            @RequestBody GroupUserIdentityEditEntity groupuseridentityeditentity
    ){
        groupuseridentityservice.addNewAdmin(groupuseridentityeditentity);
    }

    @CrossOrigin
    @PostMapping("/removeGroupAdmin")
    @ResponseStatus(HttpStatus.OK)
    public void removeGroupAdmin(
            @RequestBody GroupUserIdentityEditEntity groupuseridentityeditentity
    ){
        groupuseridentityservice.removeGroupAdmin(groupuseridentityeditentity);
    }


    @CrossOrigin
    @PostMapping("/exitGroup")
    @ResponseStatus(HttpStatus.OK)
    public boolean exitGroup(
            @RequestBody GroupRelationshipEditEntity grouprelationshipeditentity
    ){
        return grouprelationshipservice.exitGroup(grouprelationshipeditentity);
    }


    @CrossOrigin
    @PostMapping("/deleteGroup")
    @ResponseStatus(HttpStatus.OK)
    public boolean deleteGroup(
            @RequestBody GroupRelationshipEditEntity grouprelationshipeditentity
    ){
        return grouprelationshipservice.deleteGroup(grouprelationshipeditentity);
    }

    @CrossOrigin
    @PostMapping("/avatar")
    @ResponseStatus(HttpStatus.OK)
    public Map<String, String> updateAvatar(
            @RequestParam("file") MultipartFile file,
            @RequestParam("fileOrigin") MultipartFile fileOrigin,
            @RequestParam("userId") String userId,
            @RequestParam("token") String token
    ){
        return groupinfoservice.uploadGroupAvatar(file, fileOrigin, userId, token);
    }




}
