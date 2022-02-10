package OGTSystem.controller;

import OGTSystem.entity.GroupInfoEntity;
import OGTSystem.entity.UserAuthEntity;
import OGTSystem.entity.UserInfoEntity;
import OGTSystem.service.UserAuthService;
import OGTSystem.service.UserCreateService;
import OGTSystem.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

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

    @CrossOrigin
    @PostMapping("/create")
    @ResponseStatus(HttpStatus.OK)
    public String createGroup(
            @RequestBody GroupInfoEntity groupinfoentity
            ){
        System.out.println("groupName is: " + groupinfoentity.getGroupName());
        System.out.println("groupIntroduction is: " + groupinfoentity.getGroupIntroduction());

        return "create";
    }
}
