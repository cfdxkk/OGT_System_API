package OGTSystem.service;

import OGTSystem.entity.UserInfoEntity;
import OGTSystem.entity.UserSafeInfoEntity;
import OGTSystem.entity.WsServerInfoEntity;
import OGTSystem.util.OgtSystemInitializer;
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
    public void setUserinfoservice(UserInfoService userinfoservice){
        MessageService.userinfoservice = userinfoservice;
    }

    // 创建线程安全的 UserInfoService 对象
    private static UserAuthService userauthservice;
    @Autowired
    public void setUserauthservice(UserAuthService userauthservice){
        MessageService.userauthservice = userauthservice;
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

        long startTime = System.currentTimeMillis();   //获取开始时间


        // 发送状态
        String messageSentStatus = "";

        // 彩蛋: 通过在消息头部添加username的方式可以向某个用户抄送消息
        // 比如说正常的消息是: 你好
        // 当你写作:  cfdxkk -> 你好 : type
        // 就会向cfdxkk抄送一个 type 类型的消息 你好
        // 因为username和type不允许空格
        // 所以可以通过过滤 第一个“ -> ” 前的字符方式获得username，可以通过过滤最后一个“ : ”后的字符获得type; 这样可以防止滥用

        // 检查token合法性
        // 检查来源用户是否是合法用户
        if (userauthservice.userAuthCheck(uuidFrom,token)) {


            // 危险字符串过滤
            // 消息问题check 攻击、注入 etc...
            // do Something...


            // AI消息鉴定
            // do Something...


            // 判断是P2P消息还是群聊消息



            // P2P部分开始 ————————————————————————————————————————


            // 如果只传过来uunoTo的情况下，会根据uunoTo获取uuidTo
            if ("".equals(uuidTo)) {
                UserInfoEntity userinfoentity = new UserInfoEntity();
                userinfoentity.setUserNo(uunoTo);
                List<UserInfoEntity> userInfo = userinfoservice.getByUserInfoEntity(userinfoentity);
                if (userInfo.size() > 0) {
                    uuidTo = userInfo.get(0).getUserId();
                } else {
                    System.out.println("最终确认消息发送失败 :( - 用户NO错误");
                    messageSentStatus = "最终确认消息发送失败";
                }
            }

            // 根据uuidFrom获取uunoFrom
            List<UserSafeInfoEntity> userinfo1 =  userinfoservice.getByUUID(uuidFrom);
            Long uunoFrom = null;
            if (userinfo1.size()>0){
                uunoFrom = userinfo1.get(0).getUserNo();
            } else {
                System.out.println("最终确认消息发送失败 :( - 通过uuidFrom未能找到uunofrom");
                messageSentStatus = "最终确认消息发送失败";
            }

            // 根据uuidTo获取uunoTo
            List<UserSafeInfoEntity> userinfo2 =  userinfoservice.getByUUID(uuidTo);
            Long trueUunoTo = null;
            if (userinfo2.size()>0){
                trueUunoTo = userinfo2.get(0).getUserNo();
            } else {
                System.out.println("最终确认消息发送失败 :( - 通过uuidFrom未能找到uunofrom");
                messageSentStatus = "最终确认消息发送失败";
            }

            // 验证这条消息是否需要好友关系才能发送
            // do Something...

            // 发送P2P消息
            messageSentStatus = wsp2pmessageconcurrencyandconvergenceservice.sentMessage2User(uuidFrom, uuidTo, trueUunoTo, token, messageType, message, uunoFrom);

            // P2P部分结束 ————————————————————————————————————————






            // 群聊部分开始 ————————————————————————————————————————

            // 发送群聊消息

            // 群聊部分结束 ————————————————————————————————————————






        } else {
            // 非法用户
            System.out.println("最终确认消息发送失败 :( - 非法用户");
            messageSentStatus = "最终确认消息发送失败";
        }


        return messageSentStatus;



    }
}
