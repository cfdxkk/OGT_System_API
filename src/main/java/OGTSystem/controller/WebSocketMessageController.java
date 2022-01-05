package OGTSystem.controller;

import OGTSystem.entity.UserInfoEntity;
import OGTSystem.service.UserAuthService;
import OGTSystem.service.UserCreateService;
import OGTSystem.service.UserInfoService;
import OGTSystem.util.LocalhostTrueIpAddressInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/wsserver")
@Scope(value = "prototype")   // 提供线程安全，每次访问controller都会创建一个新容器
public class WebSocketMessageController {

    // 创建线程安全的 UserAuthService 对象
    private static UserAuthService userauthservice;
    @Autowired
    public void setUserauthservice(UserAuthService userauthservice){
        WebSocketMessageController.userauthservice = userauthservice;
    }

    // 创建线程安全的 UserInfoService 对象
    private static UserInfoService userinfoservice;
    @Autowired
    public void setUserauthservice(UserInfoService userinfoservice){
        WebSocketMessageController.userinfoservice = userinfoservice;
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
        System.out.println("uuidFrom is: " + uuidFrom);
        System.out.println("uuidTo is: " + uuidTo);
        System.out.println("uunoTo is: " + uunoTo);
        System.out.println("token is: " + token);
        System.out.println("messageNo is: " + messageNo);
        System.out.println("messageType is: " + messageType);
        System.out.println("message is: " + message);



        // 判断传参是否异常
        if ("".equals(uuidFrom) || ("".equals(uuidTo) && uunoTo == null) || "".equals(token) || "".equals(messageType) || "".equals(message)){
            System.out.println("消息发送失败[传输的参数不足]");

            // 获取当前服务器的IP地址
            String localhostIpAddress = "";
            localhostIpAddress = LocalhostTrueIpAddressInitializer.LOCAL_HOST_TRUE_IP_ADDRESS;

            return "消息发送失败[传输的参数不足]-" + localhostIpAddress;
        } else {
            System.out.println("通过消息完整性验证");

            // 检查来源用户是否是合法用户
            if (userauthservice.userAuthCheck(uuidFrom,token)){
                System.out.println("通过用户安全性验证");

                // 如果只传过来uunoTo的情况下，会根据uuno获取uuid
                if ("".equals(uuidTo)){
                    System.out.println("用户ID开始转换");
                    UserInfoEntity userinfoentity = new UserInfoEntity();
                    userinfoentity.setUUNO(uunoTo);
                    List<UserInfoEntity> userInfo = userinfoservice.getByUserInfoEntity(userinfoentity);
                    if (userInfo.size()>0){
                        uuidTo = userInfo.get(0).getUUID();
                        System.out.println("用户ID转换完成，获得的用户ID为: " + uuidTo);
                    } else {
                        System.out.println("消息发送失败[用户NO错误]");

                        // 获取当前服务器的IP地址
                        String localhostIpAddress = "";
                        localhostIpAddress = LocalhostTrueIpAddressInitializer.LOCAL_HOST_TRUE_IP_ADDRESS;

                        return "消息发送失败[用户NO错误]-" + localhostIpAddress;
                    }
                }



                // new ws server
                WebSocketController wsServer = new WebSocketController();

                // 发送消息并获得发送结果
                String messageSentEventResult = wsServer.sendMessage2User(uuidFrom,uuidTo,messageType,message,messageNo);
                System.out.println("消息发送结果为: " + messageSentEventResult);

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
