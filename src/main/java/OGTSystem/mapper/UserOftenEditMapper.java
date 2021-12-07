package OGTSystem.mapper;

import OGTSystem.entity.UserInfoEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface UserOftenEditMapper {

    List<UserInfoEntity> selectByUUId(String uuid);
    int createUser(UserInfoEntity user);

}
