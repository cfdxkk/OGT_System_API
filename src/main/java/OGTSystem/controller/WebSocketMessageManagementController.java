package OGTSystem.controller;

import OGTSystem.entity.UserInfoEntity;
import OGTSystem.service.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/message")
@Scope(value = "prototype")   // 提供线程安全，每次访问controller都会创建一个新容器
public class WebSocketMessageManagementController {

    @Autowired
    MessageService messageservice = new MessageService();

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

        System.out.println("消息Controller服务器收到消息了");
        System.out.println("uuidFrom is: " + uuidFrom);
        System.out.println("uuidTo is: " + uuidTo);
        System.out.println("uunoTo is: " + uunoTo);
        System.out.println("token is: " + token);
        System.out.println("messageType is: " + messageType);
        System.out.println("message is: " + message);

        // 发送消息
        return messageservice.sentMessage2User(uuidFrom, uuidTo, uunoTo, token, messageType, message);
    }
}



