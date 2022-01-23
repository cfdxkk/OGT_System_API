package OGTSystem.controller;

import OGTSystem.entity.P2PMessageEntity;
import OGTSystem.entity.UserInfoEntity;
import OGTSystem.service.UserAuthService;
import OGTSystem.service.UserInfoService;
import OGTSystem.service.WsP2PMessageConcurrencyAndConvergenceService;
import OGTSystem.util.LocalhostTrueIpAddressInitializer;
import OGTSystem.vo.P2PMessageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.EscapedErrors;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/messagepull")
@Scope(value = "prototype")   // 提供线程安全，每次访问controller都会创建一个新容器
public class WebSocketMessagePullController {

    // 创建线程安全的 UserAuthService 对象
    private static UserAuthService userauthservice;
    @Autowired
    public void setUserauthservice(UserAuthService userauthservice){
        WebSocketMessagePullController.userauthservice = userauthservice;
    }

    // 创建线程安全的 UserInfoService 对象
    private static UserInfoService userinfoservice;
    @Autowired
    public void setUserauthservice(UserInfoService userinfoservice){
        WebSocketMessagePullController.userinfoservice = userinfoservice;
    }

    // 创建线程安全的 RedisTemplate 对象
    private static RedisTemplate<String, String> redistemplate;
    @Autowired
    public void setRedistemplate(RedisTemplate<String, String> redistemplate){
        WebSocketMessagePullController.redistemplate = redistemplate;
    }

    @CrossOrigin
    @GetMapping("/getofflinemessage")
    @ResponseStatus(HttpStatus.OK)
    public List<P2PMessageVo> getOfflineMessageByUUID(
            @RequestParam(name = "uuid",required = false) String uuid
    ){

        List<P2PMessageVo> offlineMessagesList = new ArrayList<P2PMessageVo>();
        Set<TypedTuple<String>> offlineMessagesSet = redistemplate.opsForZSet().rangeWithScores("P2PM-"+uuid,0,-1);
        for (TypedTuple<String> message : offlineMessagesSet){
            System.out.println("message set is:" + message.getValue());
            String messageString = message.getValue();
            // 切割字符串
            String[] messageStringArray = messageString.split(" : ");
            if (messageStringArray[0]!=null && messageStringArray[1]!=null && messageStringArray[2]!=null && messageStringArray[3]!=null && messageStringArray[4]!=null && messageStringArray[5]!=null){
                P2PMessageVo p2pmessagevo = new P2PMessageVo();
                p2pmessagevo.setMessage(messageStringArray[0]);
                p2pmessagevo.setMessageFrom(messageStringArray[1]);
                p2pmessagevo.setMessageTarget(messageStringArray[2]);
                p2pmessagevo.setMessageType(messageStringArray[3]);
                p2pmessagevo.setChatMessageNo(Long.valueOf(messageStringArray[4]));
                p2pmessagevo.setLongMessageFlag(Integer.parseInt(messageStringArray[5]));
                p2pmessagevo.setSentDate(Long.valueOf(messageStringArray[6]));
                offlineMessagesList.add(p2pmessagevo);
            }
        }
        // 获取到消息后就清空这个redis缓存了
        redistemplate.opsForZSet().removeRange("P2PM-"+uuid,0,1000000);
        return offlineMessagesList;
    }





}
