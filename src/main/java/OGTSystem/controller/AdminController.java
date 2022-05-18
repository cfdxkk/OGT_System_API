package OGTSystem.controller;

import OGTSystem.entity.*;
import OGTSystem.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin")
@Scope(value = "prototype")   // 提供线程安全，每次访问controller都会创建一个新容器
public class AdminController {


    // 创建线程安全的 AdminService 对象
    private static AdminService adminservice;
    @Autowired
    public void setGroupmessageservice(AdminService adminservice){
        AdminController.adminservice = adminservice;
    }



//    @CrossOrigin
//    @PostMapping("/adminLogin")
//    @ResponseStatus(HttpStatus.OK)
//    public String searchGroup(
//            @RequestBody UserInfoEntity userinfoentity
//    ){
//        return adminservice.adminLogin(userinfoentity);
//    }


    @CrossOrigin
    @PostMapping("/setHotGroup")
    @ResponseStatus(HttpStatus.OK)
    public boolean setHotGroup(
            @RequestBody HotGroupEntity hotgroupentity
    ){
        return adminservice.setHotGroup(hotgroupentity);
    }

    @CrossOrigin
    @PostMapping("/removeHotGroup")
    @ResponseStatus(HttpStatus.OK)
    public boolean removeHotGroup(
            @RequestBody HotGroupEntity hotgroupentity
    ){
        return adminservice.removeHotGroup(hotgroupentity);
    }


    @CrossOrigin
    @PostMapping("/getUserInfo")
    @ResponseStatus(HttpStatus.OK)
    public List<UserInfoEntity> getUserInfoAndAuth(
            @RequestBody UserInfoAdminEntity userinfoadminentity
    ){
        return adminservice.getUserInfoAndAuth(userinfoadminentity);
    }

    @CrossOrigin
    @PostMapping("/setUserBaned")
    @ResponseStatus(HttpStatus.OK)
    public void setUserBaned(
            @RequestBody UserInfoAdminEntity userinfoadminentity
    ){
        adminservice.setUserBaned(userinfoadminentity);
    }


    @CrossOrigin
    @PostMapping("/setUserUnBaned")
    @ResponseStatus(HttpStatus.OK)
    public void setUserUnBaned(
            @RequestBody UserInfoAdminEntity userinfoadminentity
    ){
        adminservice.setUserUnBaned(userinfoadminentity);
    }

    @CrossOrigin
    @PostMapping("/removeUser")
    @ResponseStatus(HttpStatus.OK)
    public void removeUser(
            @RequestBody UserInfoAdminEntity userinfoadminentity
    ){
        adminservice.removeUser(userinfoadminentity);
    }


        @CrossOrigin
        @PostMapping("/editUserInfo")
        @ResponseStatus(HttpStatus.OK)
        public void editUserInfo(
                @RequestBody UserInfoEditEntity userinfoeditentity
        ){
            adminservice.editUserInfo(userinfoeditentity);
        }


    @CrossOrigin
    @PostMapping("/editGroupInfo")
    @ResponseStatus(HttpStatus.OK)
    public void editGroupInfo(
            @RequestBody GroupInfoEditEntity groupinfoeditentity
    ){
        adminservice.editGroupInfo(groupinfoeditentity);
    }


    @CrossOrigin
    @PostMapping("/removeByGroupId")
    @ResponseStatus(HttpStatus.OK)
    public void removeByGroupId(
            @RequestBody GroupInfoEditEntity groupinfoeditentity
    ){
        adminservice.removeByGroupId(groupinfoeditentity);
    }

    @CrossOrigin
    @PostMapping("/editGroupEvent")
    @ResponseStatus(HttpStatus.OK)
    public void editGroupEvent(
            @RequestBody GroupEventEditEntity groupeventeditentity
    ){
        adminservice.editGroupEvent(groupeventeditentity);
    }


    @CrossOrigin
    @PostMapping("/removeByEventId")
    @ResponseStatus(HttpStatus.OK)
    public void removeByEventId(
            @RequestBody GroupEventEditEntity groupeventeditentity
    ){
        adminservice.removeByEventId(groupeventeditentity);
    }







}
