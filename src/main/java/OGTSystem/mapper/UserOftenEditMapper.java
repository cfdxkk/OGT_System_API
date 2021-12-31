package OGTSystem.mapper;

import OGTSystem.entity.UserInfoEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface UserOftenEditMapper {

    List<UserInfoEntity> selectByUUId(String uuid);
    List<UserInfoEntity> getByUserInfoEntity(UserInfoEntity userinfoentity);
    int createUser(UserInfoEntity user);
    int setUserWebSocketServiceInfo(UserInfoEntity userinfoentity);

}
