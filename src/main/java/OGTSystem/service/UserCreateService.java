package OGTSystem.service;

import OGTSystem.entity.UserAuthEntity;
import OGTSystem.entity.UserInfoEntity;
import OGTSystem.repository.UserCreateRepository;
import OGTSystem.repository.UserInfoRepository;
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

    @Autowired
    UserInfoRepository userinforepository;

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

        userinfoentity.setUserId(uuid);
        userinfoentity.setUsername(username);
        userinfoentity.setPassword(password);

        userauthentity.setUUID(uuid);
        userauthentity.setUserToken(token);

        userinforepository.createUserInUserNotOftenEdit(uuid);

        return repository.createUser(userinfoentity,userauthentity);
    }
}