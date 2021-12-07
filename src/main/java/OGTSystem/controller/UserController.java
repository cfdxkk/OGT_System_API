package OGTSystem.controller;

import OGTSystem.entity.UserInfoEntity;
import OGTSystem.service.UserCreateService;
import OGTSystem.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserInfoService userinfoservice;

    @Autowired
    UserCreateService usercreateservice;

    @CrossOrigin
    @GetMapping("/info")
    @ResponseStatus(HttpStatus.OK)
    public List<UserInfoEntity> getByUUID(
            @RequestParam(name = "uuid",required = false) String uuid
    ){
        List<UserInfoEntity> userInfo = userinfoservice.getByUUID(uuid);
        return userInfo;
    }

    @CrossOrigin
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.OK)
    public String createUser(
            @RequestParam(name = "username",required = false) String username,
            @RequestParam(name = "password",required = false) String password
    ){

        System.out.println("username is: "+username);
        System.out.println("password is: "+password);

        String status = "500 something wrong";
        if ((username != null && !"".equals(username)) && (password != null && !"".equals(password))){
            int flag = usercreateservice.createUser(username, password);
            if (flag != 0){
                status = "200 okey!";
            } else {
                status = "500 insert error";
            }
        }

        return status;
    }

    @CrossOrigin
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    public String userLogin(
            @RequestParam(name = "username",required = false) String username,
            @RequestParam(name = "password",required = false) String password
    ){
        String token = "error password or unregister user";

        System.out.println("username is: " + username);
        System.out.println("password is: " + password);
        UserInfoEntity userinfoentity = new UserInfoEntity();
        userinfoentity.setUsername(username);

        List<UserInfoEntity> userInfo = userinfoservice.getByUserInfoEntity(userinfoentity);
        if (userInfo.get(0).getPassword().equals(password) ){
            token = "right token";
        }

        return token;
    }

}
