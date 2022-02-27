package OGTSystem.controller;

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
@RequestMapping("/user")
@Scope(value = "prototype")   // 提供线程安全，每次访问controller都会创建一个新容器
public class UserController {

    // 创建线程安全的UserAuthService对象
    private static UserAuthService userauthservice;
    @Autowired
    public void setUserauthservice(UserAuthService userauthservice){
        UserController.userauthservice = userauthservice;
    }

    // 创建线程安全的 UserInfoService 对象
    private static UserInfoService userinfoservice;
    @Autowired
    public void setUserauthservice(UserInfoService userinfoservice){
        UserController.userinfoservice = userinfoservice;
    }

    // 创建线程安全的 UserCreateService对象
    private static UserCreateService usercreateservice;
    @Autowired
    public void setUsercreateservice(UserCreateService usercreateservice){
        UserController.usercreateservice = usercreateservice;
    }

    @CrossOrigin
    @GetMapping("/info")
    @ResponseStatus(HttpStatus.OK)
    public List<UserInfoEntity> getByUUID(
            @RequestParam(name = "uuid",required = false) String uuid
    ){
        List<UserInfoEntity> userInfo = userinfoservice.getByUUID(uuid);
        return userInfo;
    }

    @CrossOrigin
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.OK)
    public String createUser(
            @RequestBody UserInfoEntity userinfoentity
    ){

        System.out.println("username is: "+userinfoentity.getUsername());
        System.out.println("password is: "+userinfoentity.getPassword());

        String status = "500 something wrong";
        if ((userinfoentity.getUsername() != null && !"".equals(userinfoentity.getUsername())) && (userinfoentity.getPassword() != null && !"".equals(userinfoentity.getPassword()))){
            int flag = usercreateservice.createUser(userinfoentity.getUsername(), userinfoentity.getPassword());
            if (flag != 0){
                status = "200 okey!";
            } else {
                status = "500 insert error";
            }
        }

        return status;
    }

    @CrossOrigin
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public String userLogin(
            @RequestBody UserInfoEntity userinfoentity
    ){
        String token = "error password or unregister user"; // 默认token，密码错误或用户未注册

        List<UserInfoEntity> userinfo = userinfoservice.getByUserInfoEntity(userinfoentity); // 根据用户名获取信息
        if (userinfo.get(0).getPassword().equals(userinfoentity.getPassword()) ){ // 根据获取的用户信息与用户输入的密码比对，看看是否一致
            List<UserAuthEntity> userauthinfo = userauthservice.getUserTokenByUserInfoEntity(userinfo.get(0));
            token = userauthinfo.get(0).getUUID()+"-"+userauthinfo.get(0).getUserToken(); // 从数据库查询这个用户的uuid和token

            System.out.println("token is: " + token);
        }
        return token;
    }


    @CrossOrigin
    @GetMapping("/testUserMap")
    @ResponseStatus(HttpStatus.OK)
    public String testUserMap(
            @RequestParam(name = "uuno",required = false) long uuno
    ){
        System.out.println("uuno is: " + uuno);
        System.out.println("start set");
        HashMap usermap = new HashMap();
        for (long i = 0; i<100000000; i++){
            usermap.put(i,i);
        }
        System.out.println("end set");

        long startTime=System.nanoTime();   //获取开始时间
        System.out.println("start find");
        System.out.println("user is: " + usermap.get(uuno));
        System.out.println("end find");
        long endTime=System.nanoTime(); //获取结束时间
        System.out.println("程序运行时间： "+(endTime-startTime)+"ns");

        return "right";
    }

    @CrossOrigin
    @GetMapping("/httpSentWsMessage")
    @ResponseStatus(HttpStatus.OK)
    public String httpSentWsMessage(
            @RequestParam(name = "message",required = false) String message
    ){
        System.out.println("uuno is: " + message);

        WebSocketController sentWs = new WebSocketController();
        sentWs.onMessage(message);

        return "message is: "+message;
    }

    @CrossOrigin
    @PostMapping("/tokenCheck")
    @ResponseStatus(HttpStatus.OK)
    public Boolean checkUserToken(
            @RequestBody UserAuthEntity userauthentity
    ){
        return userauthservice.userAuthCheck(userauthentity.getUserId(),userauthentity.getUserToken());
    }





}
