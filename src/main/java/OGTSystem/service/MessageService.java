package OGTSystem.service;

import OGTSystem.entity.UserInfoEntity;
import OGTSystem.entity.WsServerInfoEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@Service
public class MessageService {

    // 创建线程安全的 UserInfoService 对象
    private static UserInfoService userinfoservice;
    @Autowired
    public void setUserauthservice(UserInfoService userinfoservice){
        MessageService.userinfoservice = userinfoservice;
    }

    @Autowired
    WsP2PMessageConcurrencyAndConvergenceService wsp2pmessageconcurrencyandconvergenceservice = new WsP2PMessageConcurrencyAndConvergenceService();

    public String sentMessage2User(
            String uuidFrom, String uuidTo, Long uunoTo, String token, String messageType, String message
//            ,WsServerInfoService wsserverinfoservice,
//            UserInfoService userinfoservice,
//            MessageNoFriendService messagenofriendservice,
//            RedisTemplate<String, String> redistemplate,
//            int messageSentFlag,
//            HashMap<String,String> wsServerAddressMap
    ) throws IOException {

        long startTime=System.currentTimeMillis();   //获取开始时间

        System.out.println("消息Service服务器收到消息了");
        System.out.println("uuidFrom is: " + uuidFrom);
        System.out.println("uuidTo is: " + uuidTo);
        System.out.println("uunoTo is: " + uunoTo);
        System.out.println("token is: " + token);
        System.out.println("messageType is: " + messageType);
        System.out.println("message is: " + message);



        String messageSentStatus = "";

        // 检查token合法性


        // 危险字符串过滤
        // 消息问题check 攻击、注入 etc...
        // do Something...


        // AI消息鉴定
        // do Something...


        // 根据uuidFrom获取uunoFrom
        List<UserInfoEntity> userinfo =  userinfoservice.getByUUID(uuidFrom);
        Long uunoFrom = null;
        if (userinfo.size()>0){
            uunoFrom = userinfo.get(0).getUUNO();
        } else {
            System.out.println("最终确认消息发送失败 :( - 通过uuidFrom未能找到uunofrom");
            messageSentStatus = "最终确认消息发送失败";
        }

        // 判断是P2P消息还是群聊消息

        // 验证这条消息是否需要好友关系才能发送
        // do Something...

        // 发送P2P消息
        messageSentStatus = wsp2pmessageconcurrencyandconvergenceservice.sentMessage2User(uuidFrom, uuidTo, uunoTo, token, messageType, message, uunoFrom);



        // ————————————————————————————————————————






        return messageSentStatus;



    }
}
