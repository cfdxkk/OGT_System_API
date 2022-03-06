package OGTSystem.mapper;

import OGTSystem.entity.UserInfoAdminEntity;
import OGTSystem.entity.UserInfoEditEntity;
import OGTSystem.entity.UserInfoEntity;
import OGTSystem.entity.UserSafeInfoEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface UserOftenEditMapper {

    List<UserSafeInfoEntity> selectByUUId(String uuid);
    List<UserInfoEntity> getByUserInfoEntity(UserInfoEntity userinfoentity);
    int createUser(UserInfoEntity user);
    int setUserWebSocketServiceInfo(UserInfoEntity userinfoentity);

    int setUserInfoInUserOftenEdit(UserInfoEditEntity userinfoeditentity);

    int setUserBaned(UserInfoAdminEntity entity);
    int setUserUnBaned(UserInfoAdminEntity entity);
    int removeUser(UserInfoAdminEntity entity);
    int removeUserInAuth(UserInfoAdminEntity entity);

}
