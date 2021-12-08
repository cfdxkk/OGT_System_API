package OGTSystem.repository;

import OGTSystem.entity.UserAuthEntity;
import OGTSystem.entity.UserInfoEntity;
import OGTSystem.mapper.UserAuthMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserAuthInfoRepository {

    @Autowired
    UserAuthMapper userauthmapper;

    public List<UserAuthEntity> getUserTokenByUserInfoEntity(UserInfoEntity userinfoentity){
        return userauthmapper.getUserTokenByUserInfoEntity(userinfoentity);
    }
}
