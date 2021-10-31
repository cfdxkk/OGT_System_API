package OGTSystem.mapper;

import OGTSystem.entity.userInfoEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface userOftenEditMapper {

    List<userInfoEntity> selectByUUId(String uuid);

}
