package OGTSystem.repository;

import OGTSystem.entity.userInfoEntity;
import OGTSystem.mapper.userOftenEditMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class userInfoRepository {

    @Autowired
    userOftenEditMapper mapper;

    public List<userInfoEntity> getByUUID(String uuid){

        return mapper.selectByUUId(uuid);
    }
}
