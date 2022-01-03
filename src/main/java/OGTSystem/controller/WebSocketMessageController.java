package OGTSystem.controller;

import OGTSystem.entity.UserInfoEntity;
import OGTSystem.service.UserAuthService;
import OGTSystem.service.UserCreateService;
import OGTSystem.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.math.BigInteger;
import java.util.List;

@RestController
@RequestMapping("/wsserver")
@Scope(value = "prototype")   // 提供线程安全，每次访问controller都会创建一个新容器
public class WebSocketMessageController {

    // 创建线程安全的UserAuthService对象
    private static UserAuthService userauthservice;
    @Autowired
    public void setUserauthservice(UserAuthService userauthservice){
        WebSocketMessageController.userauthservice = userauthservice;
    }

    // 创建线程安全的UserAuthService对象
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
            @RequestParam(name = "uunoto",required = false) BigInteger uunoTo,
            @RequestParam(name = "token",required = false) String token,
            @RequestParam(name = "messagetype",required = false) String messageType,
            @RequestParam(name = "message",required = false) String message
    ){
        System.out.println("uuidFrom is: " + uuidFrom);
        System.out.println("uuidTo is: " + uuidTo);
        System.out.println("uunoTo is: " + uunoTo);
        System.out.println("token is: " + token);
        System.out.println("messageType is: " + messageType);
        System.out.println("message is: " + message);

        System.out.println("这地方不应该是true么"+"".equals(uuidTo));

        // 判断传参是否异常
        if ("".equals(uuidFrom) || ("".equals(uuidTo) && uunoTo == null) || "".equals(token) || "".equals(messageType) || "".equals(message)){
            System.out.println("消息发送失败[传输的参数不足]");
            return "消息发送失败[传输的参数不足]";
        } else {
            System.out.println("通过消息完整性验证");

            // 检查来源用户是否是合法用户
            if (userauthservice.userAuthCheck(uuidFrom,token)){
                System.out.println("通过用户安全性验证");

                // 如果只传过来uunoTo的情况下，会根据uuno获取uuid
                if ("".equals(uuidTo) && !("".equals(uunoTo))){
                    System.out.println("用户ID开始转换");
                    UserInfoEntity userinfoentity = new UserInfoEntity();
                    userinfoentity.setUUNO(uunoTo);
                    List<UserInfoEntity> userInfo = userinfoservice.getByUserInfoEntity(userinfoentity);
                    if (userInfo.size()>0){
                        uuidTo = userInfo.get(0).getUUID();
                        System.out.println("用户ID转换完成，获得的用户ID为: " + uuidTo);
                    } else {
                        System.out.println("消息发送失败[用户NO错误]");
                        return "消息发送失败[用户NO错误]";
                    }
                }

                // 验证这条消息是否需要好友关系才能发送
                // do Something...

                // 消息问题check 攻击、注入 etc...
                // do Something...

                // 获取消息顺序号(redis/持久层)
                // do Something...
                double messageNo = 0d;

                // AI消息鉴定
                // do Something...

                // new ws server
                WebSocketController wsServer = new WebSocketController();

                // 发送消息并获得发送结果
                String messageSentEventResult = wsServer.sendMessage2User(uuidFrom,uuidTo,messageType,message,messageNo);
                System.out.println("消息发送结果为: " + messageSentEventResult);
                // 如果发送结果是 "在本服务器上未找到目标用户"，则表明目标用户没连接到本服务器
                if ("在本服务器上未找到目标用户".equals(messageSentEventResult)){
                    return "消息发送失败[未找到目标用户]";
                }

                // 判断，如果消息发送成功则返回true
                if ("消息发送成功".equals(messageSentEventResult)){

                    // 消息顺序号更新(redis/持久层)
                    // do Something...

                    // 这个地方需要返回当前服务器地址，稍后需要补充
                    return "消息发送成功";
                }

                // 持久层消息记录存储
                // do Something...
            }
        }

        System.out.println("消息发送失败[未知原因]");
        return "消息发送失败[未知原因]";
    }





}
