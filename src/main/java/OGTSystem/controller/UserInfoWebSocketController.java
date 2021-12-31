package OGTSystem.controller;

import OGTSystem.service.UserAuthService;
import OGTSystem.service.UserInfoService;
import OGTSystem.service.WsServerInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.annotation.Resource;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.CopyOnWriteArraySet;

@Controller
@ServerEndpoint(value = "/websocket/{username}/{uuid}/{token}")
public class UserInfoWebSocketController {

    // 创建UserAuthService对象
    private static UserAuthService userauthservice;
    @Autowired
    public void setUserauthservice(UserAuthService userauthservice){
        UserInfoWebSocketController.userauthservice = userauthservice;
    }

    // 创建UserAuthService对象
    private static UserInfoService userinfoservice;
    @Autowired
    public void setUserauthservice(UserInfoService userinfoservice){
        UserInfoWebSocketController.userinfoservice = userinfoservice;
    }

    // 创建WsServerInfoService对象
    private static WsServerInfoService wsserverinfoservice;
    @Autowired
    public void setUserauthservice(WsServerInfoService wsserverinfoservice){
        UserInfoWebSocketController.wsserverinfoservice = wsserverinfoservice;
    }



    //在线连接数,应该把它设计成线程安全的
    private static int onlineCount = 0;

    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    //虽然@Component默认是单例模式的，但springboot还是会为每个websocket连接初始化一个bean，所以可以用一个静态set保存起来。
    public static CopyOnWriteArraySet<UserInfoWebSocketController> websocketServerSet
            = new CopyOnWriteArraySet<>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    //会话窗口的username标识
    private String username = "";

    //会话窗口的uuid标识
    private String uuid = "";


    /**
     * 链接成功调用的方法
     */
    @OnOpen
    public void onOpen(
            Session session,
            @PathParam("username") String username,
            @PathParam("uuid") String uuid,
            @PathParam("token") String token
    ) {
        System.out.println("-----------------------------------------");
        System.out.println("username is: "+username);
        System.out.println("uuid is: "+uuid);
        System.out.println("token is: "+ token);
        System.out.println("-----------------------------------------");

        if(userauthservice.userAuthCheck(uuid,token)){
            System.out.println("onOpen >> 链接成功");

            this.session = session;
            this.username = username;
            this.uuid = uuid;

            //将当前websocket对象存入到Set集合中
            websocketServerSet.add(this);

            // 获取当前服务器的IP地址,组合成服务器URL
            String serverUrl = "";
            try {
                InetAddress inetaddress = InetAddress.getLocalHost();
                String localhostIpAddress = inetaddress.getHostAddress();
                serverUrl = "ws://"+localhostIpAddress+":8080/websocket";

            } catch (Exception e){
                System.out.println(e);
            }

            //在当前用户信息表中，修改该用户连接的ws服务器连接状态记录为正在连接: 1，并记录用户连接的ws服务器地址
            userinfoservice.setUserWebSocketServiceInfo(uuid,"1",serverUrl);

            //在服务器记录中给服务器在线人数+1
            wsserverinfoservice.wsServerConnectNumberPlusOne(serverUrl);


            //服务器内部计数器的在线人数+1
            addOnlineCount();

            System.out.println("有新窗口开始监听：" + username + ", 用户token是： " + token + ", 当前在线人数为：" + getOnlineCount());

            try {
                sendMessage(session,"有新窗口开始监听：" + username + ", 用户token是： " + token + ", 当前在线人数为：" + getOnlineCount());
            } catch (Exception e) {
                System.out.println(e);
            }
        } else {
            System.out.println("注意：可能有人正在利用websocket接口DDoS攻击服务器！ UUID为"+uuid);
        }
    }


    /**
     * 链接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        System.out.println("onClose >> 链接关闭");



        // 获取当前服务器的IP地址,组合成服务器URL
        String serverUrl = "";
        try {
            InetAddress inetaddress = InetAddress.getLocalHost();
            String localhostIpAddress = inetaddress.getHostAddress();
            serverUrl = "ws://"+localhostIpAddress+":8080/websocket";

        } catch (Exception e){
            System.out.println(e);
        }

        //在当前用户信息表中，修改该用户连接的ws服务器连接状态记录为断开连接: 0，并记录用户连接的ws服务器地址
        userinfoservice.setUserWebSocketServiceInfo(uuid,"0",serverUrl);

        // 在服务器记录中给服务器在线人数+1
        wsserverinfoservice.wsServerConnectNumberSubOne(serverUrl);

        // 移除当前Websocket对象
        websocketServerSet.remove(this);

        // 服务器内部计数器的在线人数-1
        subOnLineCount();
        System.out.println("链接关闭，当前在线人数：" + getOnlineCount());
    }


//    /**
//     * 收到客户端消息后调用的方法
//     *
//     * @param message
//     * @param session
//     */
//    @OnMessage
//    public void onMessage(String message, Session session) {
//        System.out.println("push 接收到窗口：" + username + " 的信息：" + message);
//
//        //发送信息
//        for (UserInfoWebSocketController endpoint : websocketServerSet) {
//            try {
//                endpoint.sendMessage(endpoint.session,"pull 接收到窗口：" + endpoint.username + " 的信息：" + message);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    }

    @OnMessage
    public String onMessage(String message) {
        System.out.println("push 接收到窗口：" + username + " 的信息：" + message);

        //发送信息
        for (UserInfoWebSocketController endpoint : websocketServerSet) {
            try {
                endpoint.sendMessage(endpoint.session,"pull 接收到窗口：" + endpoint.username + " 的信息：" + message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "message sanded";
    }

    /**
     *
     * 出错时调用的方法
     *
     * @param session
     * @param e
     */
    @OnError
    public void onError(Session session, Throwable e) {
        e.printStackTrace();
    }


    /**
     * 推送消息
     *
     * @param message
     */
    private void sendMessage(Session session,String message) throws IOException {
        System.out.println("消息已经回送");
        try {
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            System.out.println("发送消息出错：{}"+ e.getMessage());
            e.printStackTrace();
        }
    }

    private void subOnLineCount() {
        UserInfoWebSocketController.onlineCount--;
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    private void addOnlineCount() {
        UserInfoWebSocketController.onlineCount++;
    }

}
