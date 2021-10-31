package OGTSystem.controller;

import OGTSystem.entity.userInfoEntity;
import OGTSystem.service.userInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/userinfo")
public class userInfoController {

    @Autowired
    userInfoService service;

    @GetMapping("")
    @ResponseStatus(HttpStatus.OK)
    public List<userInfoEntity> getByUUID(
            @RequestParam(name = "uuid",required = false) String uuid
    ){
        List<userInfoEntity> userInfo = service.getByUUID(uuid);
        return userInfo;
    }

}
