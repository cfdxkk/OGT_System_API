package OGTSystem.service;

import OGTSystem.entity.P2PMessageEntity;
import OGTSystem.entity.UserInfoEntity;
import OGTSystem.entity.WsServerInfoEntity;
import OGTSystem.repository.P2PMessageRepository;
import lombok.Data;
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
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Service
public class WsP2PMessageConcurrencyAndConvergenceService implements FutureCallback<HttpResponse> {


    // 创建线程安全的 WsServerInfoService 对象
    private static WsServerInfoService wsserverinfoservice;
    @Autowired
    public void setWsserverinfoservice(WsServerInfoService wsserverinfoservice){
        WsP2PMessageConcurrencyAndConvergenceService.wsserverinfoservice = wsserverinfoservice;
    }

    // 创建线程安全的 UserInfoService 对象
    private static UserInfoService userinfoservice;
    @Autowired
    public void setUserauthservice(UserInfoService userinfoservice){
        WsP2PMessageConcurrencyAndConvergenceService.userinfoservice = userinfoservice;
    }

    // 创建线程安全的 MessageNoFriendService 对象
    private static MessageNoFriendService messagenofriendservice;
    @Autowired
    public void setMessagenofriendservice(MessageNoFriendService messagenofriendservice){
        WsP2PMessageConcurrencyAndConvergenceService.messagenofriendservice = messagenofriendservice;
    }

    // 创建线程安全的 RedisTemplate 对象
    private static RedisTemplate<String, String> redistemplate;
    @Autowired
    public void setRedistemplate(RedisTemplate<String, String> redistemplate){
        WsP2PMessageConcurrencyAndConvergenceService.redistemplate = redistemplate;
    }

    // 创建线程安全的 P2PMessageRepository 对象
    private static P2PMessageRepository p2pmessagerepository;
    @Autowired
    public void setP2pmessagerepository(P2PMessageRepository p2pmessagerepository){
        WsP2PMessageConcurrencyAndConvergenceService.p2pmessagerepository = p2pmessagerepository;
    }

    // 创建线程安全的 P2PMessageService 对象
    private static P2PMessageService p2pmessageservice;
    @Autowired
    public void setP2pmessageservice(P2PMessageService p2pmessageservice){
        WsP2PMessageConcurrencyAndConvergenceService.p2pmessageservice = p2pmessageservice;
    }


    // 消息结果flag
    private int messageSentFlag;

    // 服务器列表map
    private HashMap<String,String> wsServerAddressMap = new HashMap<String,String>();

    public String sentMessage2User(
            String uuidFrom, String uuidTo, Long uunoTo, String token, String messageType, String message, Long uunoFrom
    ) throws IOException {

        long startTime=System.currentTimeMillis();   //获取开始时间


        // 判断是P2P消息还是群聊消息


        // 获取/存储 消息顺序号(redis/持久层)
        String messageNo = "";
        messageNo = messagenofriendservice.getAndCheckAndEditMessageNo(uunoFrom,uunoTo,redistemplate);

        // 检测对方是否在线...
        UserInfoEntity userinfoentity = new UserInfoEntity();
        userinfoentity.setUUNO(uunoTo);
        List<UserInfoEntity> userWsStatus = userinfoservice.getByUserInfoEntity(userinfoentity);

        if ( userWsStatus.size() == 0 || "0".equals(userWsStatus.get(0).getWsStatus())){  // 如果不在线，则在redis和持久层存储离线消息


            // 在redis和持久层存储离线消息
            p2pmessageservice.setP2Pmessage(messageNo,uuidFrom,uuidTo,message,messageType,redistemplate);

            // 消息顺序号+1
            messagenofriendservice.messageNoPlusOne(uunoFrom,uunoTo,redistemplate,messageNo);

            System.out.println("最终确认消息发送失败 :( - 对方未在线");
            return "最终确认消息发送失败";
        } else {
            // 获取ws服务器列表，把结果装在一个map里HashMap<服务器地址, 消息发送状态>
            List<WsServerInfoEntity> wsServerInfoList = wsserverinfoservice.getAllServerAddress();
            for (WsServerInfoEntity wsServerInfo :wsServerInfoList){
                String wsServerAddress = wsServerInfo.getServerAddress();
                this.wsServerAddressMap.put(wsServerAddress,"");
            }

            // 2.创建异步httpclient对象
            CloseableHttpAsyncClient httpclient = HttpAsyncClients.custom().build();

            // 3.发起调用
            try {
                // 3.0启动
                httpclient.start();

                for(String wsServerAddress: this.wsServerAddressMap.keySet()){

                    // 3.1 拼接请求参数
                    String messageUrl = "http://" + wsServerAddress + ":8888/wsserver/sentmessage2user";
                    URIBuilder urlbuilder = new URIBuilder(messageUrl);
                    urlbuilder.setParameter("uuidfrom",uuidFrom);
                    urlbuilder.setParameter("uuidto",uuidTo);
                    urlbuilder.setParameter("uunoto", uunoTo.toString());
                    urlbuilder.setParameter("token", token);
                    urlbuilder.setParameter("messageno", messageNo);
                    urlbuilder.setParameter("messagetype", messageType);
                    urlbuilder.setParameter("message", message);

                    HttpPost httppost = new HttpPost(urlbuilder.build());

                    // 3.2 发起请求，不阻塞，马上返回
                    // 我想让callback方法给this类中的一个状态flag[messageSentFlag]赋值，然后我再检测状态flag即可得知消息发送是否成功
                    httpclient.execute(httppost, this);

                    // 消息顺序号+1
                    messagenofriendservice.messageNoPlusOne(uunoFrom,uunoTo,redistemplate,messageNo);

                }

                Long count = 1L;
                while (true){

                    System.out.println("messageSentFlag NO.[" + count + "]is: " + this.messageSentFlag);

                    if (count>10000000){
                        return "最终确认消息发送失败 :( - 循环次数过多，计数器超时";
                    }

                    if (this.messageSentFlag == 1){
                        // flag 归0
                        this.messageSentFlag = 0;

                        System.out.println("最终确认消息发送成功 :)");
                        long endTime=System.currentTimeMillis(); //获取结束时间
                        System.out.println("程序运行时间： "+(endTime-startTime)+"ms");
                        return "最终确认消息发送成功";
                    } else if(this.messageSentFlag == 2){
                        // flag 归0
                        this.messageSentFlag = 0;
                        System.out.println("最终确认消息发送失败 :( - 全部机器完成了连接检索，但没找到符合的目标用户或消息发送失败");
                        long endTime=System.currentTimeMillis(); //获取结束时间
                        System.out.println("程序运行时间： "+(endTime-startTime)+"ms");
                        return "最终确认消息发送失败";
                    } else if (this.messageSentFlag == 3){
                        // flag 归0
                        this.messageSentFlag = 0;
                        System.out.println("最终确认消息发送失败 :( - 系统超时");
                        long endTime=System.currentTimeMillis(); //获取结束时间
                        System.out.println("程序运行时间： "+(endTime-startTime)+"ms");
                        return "最终确认消息发送失败";
                    }
                    Thread.sleep(5);
                    count++;
                }

            } catch (Exception e){
                System.out.println(e);

            } finally {
                httpclient.close();
            }
        }





        long endTime=System.currentTimeMillis(); //获取结束时间
        System.out.println("程序运行时间： "+(endTime-startTime)+"ms");
        System.out.println("消息发送失败[未知原因]");
        return "消息发送失败[未知原因]";
    }

    public void failed(final Exception ex) {
        System.out.println(ex.getLocalizedMessage());
    }

    public void completed(final HttpResponse response) {

        try {
            String messageSentResultOrigin = EntityUtils.toString(response.getEntity());

            // 处理执行结果  原格式: [执行结果]-[执行任务的服务器地址]
            String[] messageArray = messageSentResultOrigin.split("-");
            String messageSentResult = messageArray[0];
            String wsServerAddress = messageArray[1];


            // 一旦结果为成功则改变[messageSentFlag]的值，让上面步骤3.3的循环跳出，
            if("消息发送成功".equals(messageSentResult)){
                this.messageSentFlag = 1;
            } else { // 如果失败就根据服务器地址向map中对应的执行结果栏输入失败  (下面会根据服务器地址map筛查,当全部服务器地址对应的结果都是发送失败后，将发送flag设为2，3.3的循环会跳出并向上游返回发送失败)
                // 将失败的结果存放到map中对应的key中
                if (this.wsServerAddressMap.containsKey(wsServerAddress)){
                    this.wsServerAddressMap.put(wsServerAddress,"1");
                }

                // 判断map中的values是否有空的，如果没有空的，则将messageSentFlag改为2，表示发送失败
                if (!this.wsServerAddressMap.containsValue("")){
                    this.messageSentFlag = 2;
                }

                this.setTimeout(() -> this.messageSentFlag = 3, 10000);

            }
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cancelled() {
        // do something
        System.out.println("cancelled");
    }

    // 一旦结果为成功则改变[messageSentFlag]的值，让上面步骤3.3的循环跳出
    public static void setTimeout(Runnable runnable, int delay){
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                runnable.run();
            }
            catch (Exception e){
                System.err.println(e);
            }
        }).start();
    }
}
