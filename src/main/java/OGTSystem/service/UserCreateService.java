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

    public int createUser(String username, String password){

        String uuid = "";
        uuid = UUID.randomUUID().toString().replaceAll("-", "");;
        System.out.println("uuid is: " + uuid);
        if ( uuid == ""){
            return 0;
        }
        UserInfoEntity userinfoentity = new UserInfoEntity();
        userinfoentity.setUUID(uuid);
        userinfoentity.setUsername(username);
        userinfoentity.setPassword(password);
        UserAuthEntity userauthentity = new UserAuthEntity();
        UserToken usertoken = new UserToken();
        userauthentity.setUUID(uuid);
        userauthentity.setUserToken(usertoken.gen());
        return repository.createUser(userinfoentity,userauthentity);
    }
}