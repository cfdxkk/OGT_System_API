package OGTSystem.message.recipient;

import OGTSystem.controller.WebSocketController;
import OGTSystem.controller.WebSocketMessagePushController;
import OGTSystem.entity.UserInfoEntity;
import OGTSystem.service.UserAuthService;
import OGTSystem.util.LocalhostTrueIpAddressInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/AsynchronousMessage")
@Scope(value = "prototype")   // 提供线程安全，每次访问controller都会创建一个新容器
public class AsynchronousMessageRecipient {


    // 创建线程安全的 UserAuthService 对象
    private static UserAuthService userauthservice;
    @Autowired
    public void setUserauthservice(UserAuthService userauthservice){
        AsynchronousMessageRecipient.userauthservice = userauthservice;
    }


    @CrossOrigin
    @PostMapping("/group")
    @ResponseStatus(HttpStatus.OK)
    public String sentMessage2User(
            @RequestParam(name = "groupIdFrom",required = false) String groupIdFrom,
            @RequestParam(name = "uuidFrom",required = false) String uuidFrom,
            @RequestParam(name = "uuidTo",required = false) String uuidTo,
            @RequestParam(name = "messageIDInGroup",required = false) String messageIDInGroup,
            @RequestParam(name = "token",required = false) String token,
            @RequestParam(name = "messageType",required = false) String messageType,
            @RequestParam(name = "message",required = false) String message
    ){

        System.out.println("=================groupIdFrom is: " + groupIdFrom);
        System.out.println("User check: " + userauthservice.userAuthCheck(uuidFrom,token));
        // 获取当前服务器的IP地址
        String localhostIpAddress = LocalhostTrueIpAddressInitializer.LOCAL_HOST_TRUE_IP_ADDRESS;

        // 判断传参是否异常
        if ("".equals(groupIdFrom) || "".equals(uuidFrom) || "".equals(uuidTo) || "".equals(messageIDInGroup) || "".equals(token) || "".equals(messageType) || "".equals(message)){
            System.out.println("消息发送失败[传输的参数不足]" + localhostIpAddress);
            return "消息发送失败[传输的参数不足]-" + localhostIpAddress;
        } else {
            // 检查来源用户是否是合法用户
            if (userauthservice.userAuthCheck(uuidFrom,token)){

                // new ws server
                WebSocketController wsServer = new WebSocketController();

                // 发送消息并获得发送结果
                String messageSentEventResult = wsServer.sendMessage2Group(groupIdFrom,uuidFrom,uuidTo,messageIDInGroup,messageType,message);

                // 判断，如果消息发送成功则返回
                if ("消息发送成功".equals(messageSentEventResult)){
                    return "消息发送成功-" + localhostIpAddress;
                }
            } else {

                // 记录用户违规次数

                System.out.println("消息发送失败[用户校验未通过]");
                return "消息发送失败[用户校验未通过]-" + localhostIpAddress;
            }
        }

        System.out.println("消息发送失败[未知原因]");
        return "消息发送失败[未知原因]-" + localhostIpAddress;
    }

}
