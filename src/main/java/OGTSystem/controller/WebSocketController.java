package OGTSystem.controller;

import OGTSystem.service.UserAuthService;
import OGTSystem.service.UserInfoService;
import OGTSystem.service.WsServerInfoService;
import OGTSystem.util.LocalhostTrueIpAddressInitializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.net.InetAddress;
import java.util.concurrent.CopyOnWriteArraySet;

@Controller
@ServerEndpoint(value = "/websocket/{username}/{uuid}/{token}")
public class WebSocketController {

    // 创建线程安全的 UserAuthService 对象
    private static UserAuthService userauthservice;
    @Autowired
    public void setUserauthservice(UserAuthService userauthservice){
        WebSocketController.userauthservice = userauthservice;
    }

    // 创建线程安全的U UserInfoService 对象
    private static UserInfoService userinfoservice;
    @Autowired
    public void setUserauthservice(UserInfoService userinfoservice){
        WebSocketController.userinfoservice = userinfoservice;
    }

    // 创建线程安全的 WsServerInfoService 对象
    private static WsServerInfoService wsserverinfoservice;
    @Autowired
    public void setUserauthservice(WsServerInfoService wsserverinfoservice){
        WebSocketController.wsserverinfoservice = wsserverinfoservice;
    }



    //在线连接数,应该把它设计成线程安全的
    private static int onlineCount = 0;

    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    //虽然@Component默认是单例模式的，但springboot还是会为每个websocket连接初始化一个bean，所以可以用一个静态set保存起来。
    public static CopyOnWriteArraySet<WebSocketController> websocketServerSet
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
            @PathParam("token") String token,
            @PathParam("terminaltype") String terminalType
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

            // 获取当前服务器的IP地址
            String localhostIpAddress = "";
            localhostIpAddress = LocalhostTrueIpAddressInitializer.LOCAL_HOST_TRUE_IP_ADDRESS;

            System.out.println("获取到当前服务器的IP地址为: " + localhostIpAddress);
            //在当前用户信息表中，修改该用户连接的ws服务器连接状态记录为正在连接: 1，并记录用户连接的ws服务器地址
            userinfoservice.setUserWebSocketServiceInfo(uuid,terminalType,"1",localhostIpAddress);

            //在服务器记录中给服务器在线人数+1
            wsserverinfoservice.wsServerConnectNumberPlusOne(localhostIpAddress);


            //服务器内部计数器的在线人数+1
            addOnlineCount();

            System.out.println("有新窗口开始监听：" + username + ", 用户token是： " + token + ", 当前在线人数为：" + getOnlineCount());

//            try {
//                sendMessage(session,"有新窗口开始监听：" + username + ", 用户token是： " + token + ", 当前在线人数为：" + getOnlineCount());
//            } catch (Exception e) {
//                System.out.println(e);
//            }
        } else {
            System.out.println("注意：可能有人正在利用websocket接口DDoS攻击服务器！ UUID为"+uuid);
        }
    }


    /**
     * 链接关闭调用的方法
     */
    @OnClose
    public void onClose(
            @PathParam("terminaltype") String terminalType
    ) {
        System.out.println("onClose >> 链接关闭");



        // 获取当前服务器的IP地址
        String localhostIpAddress = "";
        localhostIpAddress = LocalhostTrueIpAddressInitializer.LOCAL_HOST_TRUE_IP_ADDRESS;

        //在当前用户信息表中，修改该用户连接的ws服务器连接状态记录为断开连接: 0，并记录用户连接的ws服务器地址
        userinfoservice.setUserWebSocketServiceInfo(uuid,terminalType, "0",localhostIpAddress);

        // 在服务器记录中给服务器在线人数+1
        wsserverinfoservice.wsServerConnectNumberSubOne(localhostIpAddress);

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
    public void onMessage(String message) {
        System.out.println("push 接收到窗口：" + username + " 的信息：" + message);
        //发送信息
        for (WebSocketController endpoint : websocketServerSet) {
            try {
                endpoint.sendMessage(endpoint.session,"pull 接收到窗口：" + endpoint.username + " 的信息：" + message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
     *
     * 遍历set，找到目标用户是否连接在当前服务器set中，如果在则发送消息并return成功，如果不在则return不在，如果发送失败就return失败
     *
     * @param uuidFrom 消息来源
     * @param uuidTo 消息目标
     * @param messageType 消息类型
     * @param message 消息体
     * @param messageNo 消息顺序号
     * @param messageInfo 预留字段
     * @return
     */
    public String sendMessage2User(String uuidFrom, String uuidTo, String messageType, String message, Long messageNo, String... messageInfo){

        int connectNumber = websocketServerSet.size();
        int count = 0;

        for (WebSocketController websocketServer : websocketServerSet){

            count++;
            if (websocketServer.uuid.equals(uuidTo) || websocketServer.uuid == uuidTo){
                System.out.println("匹配到目标用户: " + websocketServer.uuid);
                try {
                    websocketServer.sendMessage(websocketServer.session,"接收到用户: " + uuidFrom + " 发给你的 " + messageType + " 类信息: [" + message + "] 消息顺序号为: " + messageNo);
                    return "消息发送成功";
                } catch (Exception e) {
                    e.printStackTrace();
                    return "消息发送失败";
                }
            }
            if (count == connectNumber){
                return "在本服务器上未找到目标用户";
            }
        }

        return "未知错误，发送失败";

    }


    /**
     *
     * 遍历set，找到目标用户是否连接在当前服务器set中，如果在则发送消息并return成功，如果不在则return不在，如果发送失败就return失败
     *
     * @param groupIdFrom
     * @param uuidFrom
     * @param uuidTo
     * @param messageIDInGroup
     * @param messageType
     * @param message
     * @param messageInfo
     * @return
     */
    public String sendMessage2Group(
            String groupIdFrom,
            String uuidFrom,
            String usernameFrom,
            String uuidTo,
            String messageIDInGroup,
            String messageType,
            String message,
            String... messageInfo
    ){

        int connectNumber = websocketServerSet.size();
        int count = 0;

        for (WebSocketController websocketServer : websocketServerSet){

            count++;
            if (websocketServer.uuid.equals(uuidTo)){
                System.out.println("匹配到目标用户: " + websocketServer.uuid);
                try {
                    String safeMessage = message.replace(" >clip_:< ", "");
//                    websocketServer.sendMessage(websocketServer.session,"接收到: " + groupIdFrom + "群的用户: " + uuidFrom + " 发给你的 " + messageType + " 类信息: [" + message + "] 消息顺序号为: " + messageIDInGroup);
                    System.out.println("messag is: " + safeMessage);
                    websocketServer.sendMessage(websocketServer.session, groupIdFrom + " >clip_:< " + uuidFrom + " >clip_:< " + usernameFrom + " >clip_:< " + messageType + " >clip_:< " + safeMessage + " >clip_:< " + messageIDInGroup);

                    return "消息发送成功";
                } catch (Exception e) {
                    e.printStackTrace();
                    return "消息发送失败";
                }
            }
            if (count == connectNumber){
                return "在本服务器上未找到目标用户";
            }
        }

        return "未知错误，发送失败";

    }

    /**
     * 推送消息
     *
     * @param message
     */
    private void sendMessage(Session session,String message) throws IOException {
        try {
            System.out.println("消息已经回送");
            session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            System.out.println("发送消息出错：{}"+ e.getMessage());
            e.printStackTrace();
        }
    }

    private void subOnLineCount() {
        WebSocketController.onlineCount--;
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    private void addOnlineCount() {
        WebSocketController.onlineCount++;
    }

}
