package OGTSystem.service;

import OGTSystem.entity.UserAuthEntity;
import OGTSystem.entity.UserInfoEntity;
import OGTSystem.repository.UserAuthInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserAuthService {

    @Autowired
    UserAuthInfoRepository userauthinforepository;

    public List<UserAuthEntity> getUserTokenByUserInfoEntity(UserInfoEntity userinfoentity){
        return userauthinforepository.getUserTokenByUserInfoEntity(userinfoentity);
    }
}
