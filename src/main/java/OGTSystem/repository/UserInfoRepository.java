package OGTSystem.repository;

import OGTSystem.entity.UserInfoAdminEntity;
import OGTSystem.entity.UserInfoEditEntity;
import OGTSystem.entity.UserInfoEntity;
import OGTSystem.entity.UserSafeInfoEntity;
import OGTSystem.mapper.UserNotOftenEditMapper;
import OGTSystem.mapper.UserOftenEditMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserInfoRepository {

    @Autowired
    UserOftenEditMapper userofteneditmapper;

    @Autowired
    UserNotOftenEditMapper usernotofteneditmapper;

    public List<UserSafeInfoEntity> getByUUID(String uuid){
        return userofteneditmapper.selectByUUId(uuid);
    }

    public List<UserInfoEntity> getByUserInfoEntity(UserInfoEntity userinfoentity){
        return userofteneditmapper.getByUserInfoEntity(userinfoentity);
    }

    public int setUserWebSocketServiceInfo(UserInfoEntity userinfoentity){
        return userofteneditmapper.setUserWebSocketServiceInfo(userinfoentity);
    }


    public int setUserBaned(UserInfoAdminEntity entity) { return userofteneditmapper.setUserBaned(entity); }
    public int setUserUnBaned(UserInfoAdminEntity entity) { return userofteneditmapper.setUserUnBaned(entity); }
    public int removeUser(UserInfoAdminEntity entity) { return userofteneditmapper.removeUser(entity); }
    public int removeUserInAuth(UserInfoAdminEntity entity) { return userofteneditmapper.removeUserInAuth(entity); }

    public int setUserInfoInUserNotOftenEdit(UserInfoEditEntity userinfoeditentity) {
        return usernotofteneditmapper.setUserInfoInUserNotOftenEdit(userinfoeditentity);
    }
    public int setUserInfoInUserOftenEdit(UserInfoEditEntity userinfoeditentity) {
        return userofteneditmapper.setUserInfoInUserOftenEdit(userinfoeditentity);
    }

    public int createUserInUserNotOftenEdit(String userid) { return usernotofteneditmapper.createUserInUserNotOftenEdit(userid); }

}
