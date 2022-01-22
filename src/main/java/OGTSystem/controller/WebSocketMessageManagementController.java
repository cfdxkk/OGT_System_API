package OGTSystem.controller;

import OGTSystem.entity.MessagePushEntity;
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
            @RequestBody MessagePushEntity messagepushentity
    ) throws IOException {


        // 发送消息
        return messageservice.sentMessage2User(
                messagepushentity.getUuidfrom(),
                messagepushentity.getUuidto(),
                messagepushentity.getUunoto(),
                messagepushentity.getToken(),
                messagepushentity.getMessagetype(),
                messagepushentity.getMessage()
        );
    }
}



