package OGTSystem.mapper;

import OGTSystem.entity.UserAuthEntity;
import OGTSystem.entity.UserInfoEntity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserAuthMapper {
    int createUserAuth(UserAuthEntity userauthentity);
}
