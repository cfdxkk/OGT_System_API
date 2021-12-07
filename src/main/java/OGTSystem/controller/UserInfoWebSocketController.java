package OGTSystem.controller;

import org.springframework.stereotype.Controller;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArraySet;

@Controller
@ServerEndpoint(value = "/websocket/{id}")
public class UserInfoWebSocketController {

    //在线连接数,应该把它设计成线程安全的
    private static int onlineCount = 0;

    //concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
    //虽然@Component默认是单例模式的，但springboot还是会为每个websocket连接初始化一个bean，所以可以用一个静态set保存起来。
    public static CopyOnWriteArraySet<UserInfoWebSocketController> websocketServerSet
            = new CopyOnWriteArraySet<>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    //会话窗口的ID标识
    private String id = "";


    /**
     * 链接成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("id") String id) {
        System.out.println("onOpen >> 链接成功");
        this.session = session;
        //将当前websocket对象存入到Set集合中
        websocketServerSet.add(this);
        //在线人数+1
        addOnlineCount();
        System.out.println("有新窗口开始监听：" + id + ", 当前在线人数为：" + getOnlineCount());
        this.id = id;
        try {
            sendMessage(session,"有新窗口开始监听：" + id + ", 当前在线人数为：" + getOnlineCount());
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }


    /**
     * 链接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        System.out.println("onClose >> 链接关闭");
        //移除当前Websocket对象
        websocketServerSet.remove(this);
        //在内线人数-1
        subOnLineCount();
        System.out.println("链接关闭，当前在线人数：" + getOnlineCount());
    }


    /**
     * 收到客户端消息后调用的方法
     *
     * @param message
     * @param session
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("push 接收到窗口：" + id + " 的信息：" + message);

        //发送信息
        for (UserInfoWebSocketController endpoint : websocketServerSet) {
            try {
                endpoint.sendMessage(session,"pull 接收到窗口：" + id + " 的信息：" + message);
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
