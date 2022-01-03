package OGTSystem.controller;

import OGTSystem.entity.MessageEntity;
import OGTSystem.entity.UserInfoEntity;
import OGTSystem.entity.WsServerInfoEntity;
import OGTSystem.service.UserAuthService;
import OGTSystem.service.UserCreateService;
import OGTSystem.service.UserInfoService;
import OGTSystem.service.WsServerInfoService;
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
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.EscapedErrors;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.math.BigInteger;
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

    private int messageSentFlag;

    @CrossOrigin
    @PostMapping("/messagefilterandcluster")
    @ResponseStatus(HttpStatus.OK)
    public String sentMessage2User(
            @RequestParam(name = "uuidfrom",required = false) String uuidFrom,
            @RequestParam(name = "uuidto",required = false) String uuidTo,
            @RequestParam(name = "uunoto",required = false) BigInteger uunoTo,
            @RequestParam(name = "token",required = false) String token,
            @RequestParam(name = "messagetype",required = false) String messageType,
            @RequestParam(name = "message",required = false) String message
    ) throws IOException {
        System.out.println("集群服务器收到消息了");
        System.out.println("uuidFrom is: " + uuidFrom);
        System.out.println("uuidTo is: " + uuidTo);
        System.out.println("uunoTo is: " + uunoTo);
        System.out.println("token is: " + token);
        System.out.println("messageType is: " + messageType);
        System.out.println("message is: " + message);


        // 获取ws服务器列表，把结果装在一个map里HashMap<服务器地址, 消息发送状态>
        List<WsServerInfoEntity> wsServerInfoList = wsserverinfoservice.getAllServerAddress();
        for (WsServerInfoEntity wsServerInfo :wsServerInfoList){
            String wsServerAddress = wsServerInfo.getServerAddress();
            String wsMessagePostAddress = "http://"+wsServerAddress+":8080/wsserver/sentmessage2user";

        }

        // 2.创建异步httpclient对象
        CloseableHttpAsyncClient httpclient = HttpAsyncClients.custom().build();

        // 3.发起调用
        try {
            // 3.0启动
            httpclient.start();

            // 3.1请求参数
            String messageUrl = "http://127.0.0.1:8080/wsserver/sentmessage2user";
            URIBuilder urlbuilder = new URIBuilder(messageUrl);
            urlbuilder.setParameter("uuidfrom",uuidFrom);
            urlbuilder.setParameter("uuidto",uuidTo);
            urlbuilder.setParameter("uunoto", uunoTo.toString());
            urlbuilder.setParameter("token", token);
            urlbuilder.setParameter("messagetype", messageType);
            urlbuilder.setParameter("message", message);
            HttpPost httppost = new HttpPost(urlbuilder.build());

            // 3.2发起请求，不阻塞，马上返回
            // 我想让callback方法给this类中的一个状态flag[messageSentFlag]赋值，然后我再检测状态flag即可得知消息发送是否成功
            System.out.println("看看成功的回调是否好用(前)" + this.messageSentFlag);
            httpclient.execute(httppost, this);

            // 3.3休眠10s,避免请求执行完成前，关闭了链接
            // 最终应该实现为不停循环判断[messageSentFlag]，一旦flag为真则表示消息发送成功(跳出循环)，一旦为其他值则进行其他操作 etc...
//            Thread.sleep(10000);
            System.out.println("看看成功的回调是否好用" + this.messageSentFlag);

            while (true){

                System.out.println("messageSentFlag is: " + this.messageSentFlag);
                if (this.messageSentFlag == 1){
                    // flag 归0
                    this.messageSentFlag = 0;
                    System.out.println("最终确认消息发送成功！！！！");
                    return "最终确认消息发送成功";
                }
                Thread.sleep(50);
//                else if(false){
//                    return "最终确认消息发送失败";
//                }
            }


        } catch (Exception e){
            System.out.println(e);

        } finally {
            httpclient.close();
        }




        System.out.println("消息发送失败[未知原因]");
        return "消息发送失败[未知原因]";
    }





    public void failed(final Exception ex) {
        System.out.println(ex.getLocalizedMessage());
    }

    public void completed(final HttpResponse response) {

        try {
            System.out.println("收到集群服务器的执行结果: " + EntityUtils.toString(response.getEntity()));

            // 一旦结果为成功则改变[messageSentFlag]的值，让上面步骤3.3的死循环跳出，或失败(根据服务器地址表筛查全部服务器都返回消息发送失败)时返回其他值
            this.messageSentFlag = 1;
            System.out.println("messageSentFlag修改成功");


        } catch (ParseException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void cancelled() {
        System.out.println("cancelled");
    }





}



