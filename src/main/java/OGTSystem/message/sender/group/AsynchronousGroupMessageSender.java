package OGTSystem.message.sender.group;

import OGTSystem.entity.WsServerInfoEntity;
import OGTSystem.service.WsServerInfoService;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@Service
public class AsynchronousGroupMessageSender implements FutureCallback<HttpResponse> {

    // 创建线程安全的 WsServerInfoService 对象
    private static WsServerInfoService wsserverinfoservice;
    @Autowired
    public void setWsserverinfoservice(WsServerInfoService wsserverinfoservice){
        AsynchronousGroupMessageSender.wsserverinfoservice = wsserverinfoservice;
    }

    // 服务器列表map
    private HashMap<String,String> wsServerAddressMap = new HashMap<String,String>();
    // 判断发送状态的flag
    private int messageSentFlag = 0;

    public boolean sentMessageToGroupUser (
            String groupIdFrom, String uuidFrom, String uuidTo, String messageIDInGroup, String token, String messageType, String message
        ){

        System.out.println("-------------groupIdFrom is: " + groupIdFrom);

        // 1.获取ws服务器列表，把结果装在一个map里HashMap<服务器地址, 消息发送状态>
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

            for (String wsServerAddress : this.wsServerAddressMap.keySet()) {

                // 3.1 拼接请求参数
                String messageUrl = "http://" + wsServerAddress + ":8888/AsynchronousMessage/group";
                URIBuilder urlbuilder = new URIBuilder(messageUrl);

                urlbuilder.setParameter("groupIdFrom", groupIdFrom);
                urlbuilder.setParameter("uuidFrom", uuidFrom);
                urlbuilder.setParameter("uuidTo", uuidTo);
                urlbuilder.setParameter("messageIDInGroup", messageIDInGroup);
                urlbuilder.setParameter("token", token);
                urlbuilder.setParameter("messageType", messageType);
                urlbuilder.setParameter("message", message);

                HttpPost httppost = new HttpPost(urlbuilder.build());

                // 3.2 发起请求，不阻塞，马上返回
                // 我想让callback方法给this类中的一个状态flag[messageSentFlag]赋值，然后我再检测状态flag即可得知消息发送是否成功
                httpclient.execute(httppost, this);

            }



            int count = 0;

            while (true){

                if (count>10000){

                    System.out.println("最终确认消息发送失败 :( - 循环次数过10000次，启动保护程序自动退出");

                    // flag 归0
                    this.messageSentFlag = 0;
                    return false;
                } else if (this.messageSentFlag == 1){

                    System.out.println("最终确认消息发送成功 :)");

                    // flag 归0
                    this.messageSentFlag = 0;
                    return true;
                } else if(this.messageSentFlag == 2){

                    System.out.println("最终确认消息发送失败 :( - 全部机器完成了连接检索，但没找到符合的目标用户或消息发送失败");

                    // flag 归0
                    this.messageSentFlag = 0;
                    return false;
                } else if (this.messageSentFlag == 3){

                    System.out.println("最终确认消息发送失败 :( - 系统超时");

                    // flag 归0
                    this.messageSentFlag = 0;
                    return false;
                }
                // 休息5毫秒后再进行下一次循环
                Thread.sleep(5);
                count++;
            }



        } catch (Exception e) {
            System.out.println("oops in AsynchronousGroupMessage.sentMessageToUser -> 01");
            System.out.println(e);
        }
        return false;
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
