package OGTSystem.controller;

import OGTSystem.entity.GroupInfoEntity;
import OGTSystem.entity.GroupMessageEntity;
import OGTSystem.entity.GroupRelationshipEntity;
import OGTSystem.message.sender.group.AsynchronousGroupMessageSender;
import OGTSystem.service.*;
import OGTSystem.vo.GroupMessageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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



    @CrossOrigin
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.OK)
    public String createGroup(
            @RequestBody GroupInfoEntity groupinfoentity
    ){

        String groupId = groupinfoservice.createGroup(groupinfoentity);
        if ("".equals(groupId)){
            return "false";
        } else {
            return groupId;
        }

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
    public List<GroupInfoEntity> createGroup(
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
    public List<GroupMessageVo> getGroupOfflineMessage(
            @RequestBody GroupMessageVo groupmessagevo
    ){
        return groupmessageservice.getOfflineMessage(groupmessagevo.getUuidTo(),groupmessagevo.getGroupIdFrom(), groupmessagevo.getToken());
    }
}
