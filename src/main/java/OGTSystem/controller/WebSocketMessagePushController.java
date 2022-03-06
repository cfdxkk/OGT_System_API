package OGTSystem.controller;

import OGTSystem.entity.UserInfoEntity;
import OGTSystem.service.UserAuthService;
import OGTSystem.service.UserInfoService;
import OGTSystem.util.LocalhostTrueIpAddressInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wsserver")
@Scope(value = "prototype")   // 提供线程安全，每次访问controller都会创建一个新容器
public class WebSocketMessagePushController {

    // 创建线程安全的 UserAuthService 对象
    private static UserAuthService userauthservice;
    @Autowired
    public void setUserauthservice(UserAuthService userauthservice){
        WebSocketMessagePushController.userauthservice = userauthservice;
    }

    // 创建线程安全的 UserInfoService 对象
    private static UserInfoService userinfoservice;
    @Autowired
    public void setUserauthservice(UserInfoService userinfoservice){
        WebSocketMessagePushController.userinfoservice = userinfoservice;
    }

    @CrossOrigin
    @PostMapping("/sentmessage2user")
    @ResponseStatus(HttpStatus.OK)
    public String sentMessage2User(
            @RequestParam(name = "uuidfrom",required = false) String uuidFrom,
            @RequestParam(name = "uuidto",required = false) String uuidTo,
            @RequestParam(name = "uunoto",required = false) Long uunoTo,
            @RequestParam(name = "token",required = false) String token,
            @RequestParam(name = "messageno",required = false) Long messageNo,
            @RequestParam(name = "messagetype",required = false) String messageType,
            @RequestParam(name = "message",required = false) String message
    ){

        // 判断传参是否异常
        if ("".equals(uuidFrom) || ("".equals(uuidTo) && uunoTo == null) || "".equals(token) || "".equals(messageType) || "".equals(message)){
            // 获取当前服务器的IP地址
            String localhostIpAddress = "";
            localhostIpAddress = LocalhostTrueIpAddressInitializer.LOCAL_HOST_TRUE_IP_ADDRESS;
            System.out.println("消息发送失败[传输的参数不足]" + localhostIpAddress);
            return "消息发送失败[传输的参数不足]-" + localhostIpAddress;
        } else {
            // 检查来源用户是否是合法用户
            if (userauthservice.userAuthCheck(uuidFrom,token)){
                // 如果只传过来uunoTo的情况下，会根据uuno获取uuid
                if ("".equals(uuidTo)){
                    UserInfoEntity userinfoentity = new UserInfoEntity();
                    userinfoentity.setUserNo(uunoTo);
                    List<UserInfoEntity> userInfo = userinfoservice.getByUserInfoEntity(userinfoentity);
                    if (userInfo.size()>0){
                        uuidTo = userInfo.get(0).getUserId();
                    } else {
                        // 获取当前服务器的IP地址
                        String localhostIpAddress = "";
                        localhostIpAddress = LocalhostTrueIpAddressInitializer.LOCAL_HOST_TRUE_IP_ADDRESS;
                        System.out.println("消息发送失败[用户NO错误]" + localhostIpAddress);
                        return "消息发送失败[用户NO错误]-" + localhostIpAddress;
                    }
                }



                // new ws server
                WebSocketController wsServer = new WebSocketController();

                // 发送消息并获得发送结果
                String messageSentEventResult = wsServer.sendMessage2User(uuidFrom,uuidTo,messageType,message,messageNo);

                // 判断，如果消息发送成功则返回true
                if ("消息发送成功".equals(messageSentEventResult)){

                    // 消息顺序号更新(redis/持久层)
                    // do Something...

                    // 获取当前服务器的IP地址
                    String localhostIpAddress = "";
                    localhostIpAddress = LocalhostTrueIpAddressInitializer.LOCAL_HOST_TRUE_IP_ADDRESS;

                    return "消息发送成功-" + localhostIpAddress;
                }

                // 持久层消息记录存储
                // do Something...
            }
        }

        System.out.println("消息发送失败[未知原因]");

        // 获取当前服务器的IP地址
        String localhostIpAddress = "";
        localhostIpAddress = LocalhostTrueIpAddressInitializer.LOCAL_HOST_TRUE_IP_ADDRESS;

        return "消息发送失败[未知原因]-" + localhostIpAddress;
    }





}
