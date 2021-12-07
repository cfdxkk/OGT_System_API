package OGTSystem.service;

import OGTSystem.entity.UserAuthEntity;
import OGTSystem.entity.UserInfoEntity;
import OGTSystem.repository.UserCreateRepository;
import OGTSystem.util.UserToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserCreateService {
    @Autowired
    UserCreateRepository repository;

    @Autowired
    UserInfoService userinfoservice;

    public int createUser(String username, String password){

        UserInfoEntity userinfoentity = new UserInfoEntity();
        UserAuthEntity userauthentity = new UserAuthEntity();

        // uuid gen
        String uuid = "";
        uuid = UUID.randomUUID().toString().replaceAll("-", "");

        // token gen
        UserToken usertoken = new UserToken();
        String token = usertoken.gen();

        // check
        if ( uuid == "" || token == ""){
            return 0;
        }

        userinfoentity.setUUID(uuid);
        userinfoentity.setUsername(username);
        userinfoentity.setPassword(password);

        userauthentity.setUUID(uuid);
        userauthentity.setUserToken(token);

        return repository.createUser(userinfoentity,userauthentity);
    }
}