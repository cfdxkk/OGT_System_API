package OGTSystem.controller;

import OGTSystem.entity.MessageEntity;
import OGTSystem.entity.UserInfoEntity;
import OGTSystem.entity.WsServerInfoEntity;
import OGTSystem.service.*;
import OGTSystem.util.AsyncHttpRequestCallback;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.EscapedErrors;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;


@RestController
@RequestMapping("/message")
@Scope(value = "prototype")   // 提供线程安全，每次访问controller都会创建一个新容器
public class WebSocketMessageFilterAndClusterController implements FutureCallback<HttpResponse>  {

    // 创建线程安全的 WsServerInfoService 对象
    private static WsServerInfoService wsserverinfoservice;
    @Autowired
    public void setWsserverinfoservice(WsServerInfoService wsserverinfoservice){
        WebSocketMessageFilterAndClusterController.wsserverinfoservice = wsserverinfoservice;
    }

    // 创建线程安全的U UserInfoService 对象
    private static UserInfoService userinfoservice;
    @Autowired
    public void setUserauthservice(UserInfoService userinfoservice){
        WebSocketMessageFilterAndClusterController.userinfoservice = userinfoservice;
    }

    // 创建线程安全的U UserInfoService 对象
    private static MessageNoFriendService messagenofriendservice;
    @Autowired
    public void setUserauthservice(MessageNoFriendService messagenofriendservice){
        WebSocketMessageFilterAndClusterController.messagenofriendservice = messagenofriendservice;
    }

    // 创建线程安全的 RedisTemplate 对象
    private static RedisTemplate<String, String> redistemplate;
    @Autowired
    public void setRedistemplate(RedisTemplate<String, String> redistemplate){
        WebSocketMessageFilterAndClusterController.redistemplate = redistemplate;
    }


    // 消息结果flag
    private int messageSentFlag;

    // 服务器列表map
    private HashMap<String,String> wsServerAddressMap = new HashMap<String,String>();

    @CrossOrigin
    @PostMapping("/messagefilterandcluster")
    @ResponseStatus(HttpStatus.OK)
    public String sentMessage2User(
            @RequestParam(name = "uuidfrom",required = false) String uuidFrom,
            @RequestParam(name = "uuidto",required = false) String uuidTo,
            @RequestParam(name = "uunoto",required = false) Long uunoTo,
            @RequestParam(name = "token",required = false) String token,
            @RequestParam(name = "messagetype",required = false) String messageType,
            @RequestParam(name = "message",required = false) String message
    ) throws IOException {

        long startTime=System.currentTimeMillis();   //获取开始时间

        System.out.println("集群服务器收到消息了");
        System.out.println("uuidFrom is: " + uuidFrom);
        System.out.println("uuidTo is: " + uuidTo);
        System.out.println("uunoTo is: " + uunoTo);
        System.out.println("token is: " + token);
        System.out.println("messageType is: " + messageType);
        System.out.println("message is: " + message);

        // 根据uuidFrom获取uunoFrom
        List<UserInfoEntity> userinfo =  userinfoservice.getByUUID(uuidFrom);
        Long uunoFrom = null;
        if (userinfo.size()>0){
            uunoFrom = userinfo.get(0).getUUNO();
        } else {
            System.out.println("最终确认消息发送失败 :( - 通过uuidFrom未能找到uunofrom");
            return "最终确认消息发送失败";
        }



        // 验证这条消息是否需要好友关系才能发送
        // do Something...

        // 消息问题check 攻击、注入 etc...
        // do Something...

        // 获取/存储 消息顺序号(redis/持久层)
        String messageNo = "";
        messageNo = messagenofriendservice.getAndCheckAndEditMessageNo(uunoFrom,uunoTo,redistemplate);
        System.out.println("其实messageNo已经完成了");
//        messageNo = "0";

        // AI消息鉴定
        // do Something...


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
                System.out.println("看看成功的回调是否好用(前)" + this.messageSentFlag);
                httpclient.execute(httppost, this);
                System.out.println("看看成功的回调是否好用" + this.messageSentFlag);

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

                    messagenofriendservice.messageNoPlusOne(uunoFrom,uunoTo,redistemplate,messageNo);

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
            System.out.println("收到集群服务器的执行结果(Origin): " + messageSentResultOrigin);
            String[] messageArray = messageSentResultOrigin.split("-");
            String messageSentResult = messageArray[0];
            String wsServerAddress = messageArray[1];

            System.out.println("收到集群服务器的执行结果: " + messageSentResult);

            // 一旦结果为成功则改变[messageSentFlag]的值，让上面步骤3.3的死循环跳出，或失败(根据服务器地址表筛查全部服务器都返回消息发送失败)时返回其他值
            if("消息发送成功".equals(messageSentResult)){
                this.messageSentFlag = 1;
                System.out.println("messageSentFlag修改1成功");
            } else {
                // 将失败的结果存放到map中对应的key中
                if (this.wsServerAddressMap.containsKey(wsServerAddress)){
                    this.wsServerAddressMap.put(wsServerAddress,"1");
                }

                // 判断map中的values是否有空的，如果没有空的，则将messageSentFlag改为2，表示发送失败
                if (!this.wsServerAddressMap.containsValue("")){
                    this.messageSentFlag = 2;
                    System.out.println("messageSentFlag修改2成功");
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
        System.out.println("cancelled");
    }

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



