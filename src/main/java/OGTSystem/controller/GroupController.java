package OGTSystem.controller;

import OGTSystem.entity.GroupInfoEntity;
import OGTSystem.entity.GroupRelationshipEntity;
import OGTSystem.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
    public int createGroup(
            @RequestBody GroupRelationshipEntity grouprelationshipentity
    ){
        return grouprelationshipservice.createGroupRelationship(grouprelationshipentity);
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


}
