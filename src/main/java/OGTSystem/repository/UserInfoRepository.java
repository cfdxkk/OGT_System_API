package OGTSystem.repository;

import OGTSystem.entity.UserInfoEntity;
import OGTSystem.mapper.UserOftenEditMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserInfoRepository {

    @Autowired
    UserOftenEditMapper mapper;

    public List<UserInfoEntity> getByUUID(String uuid){

        return mapper.selectByUUId(uuid);
    }
}
