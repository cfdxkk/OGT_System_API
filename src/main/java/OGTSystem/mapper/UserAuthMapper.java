package OGTSystem.mapper;

import OGTSystem.entity.UserAuthEntity;
import OGTSystem.entity.UserInfoEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserAuthMapper {
    int createUserAuth(UserAuthEntity userauthentity);
    List<UserAuthEntity> getUserTokenByUserInfoEntity (UserInfoEntity userinfoentity);
}
