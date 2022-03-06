package OGTSystem.mapper;

import OGTSystem.entity.UserInfoAdminEntity;
import OGTSystem.entity.UserInfoEditEntity;
import OGTSystem.entity.UserInfoEntity;
import OGTSystem.entity.UserSafeInfoEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserNotOftenEditMapper {

    int createUserInUserNotOftenEdit(String userId);
    int setUserInfoInUserNotOftenEdit(UserInfoEditEntity userinfoeditentity);


}
