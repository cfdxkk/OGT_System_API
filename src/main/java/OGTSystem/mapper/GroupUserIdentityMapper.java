package OGTSystem.mapper;

import OGTSystem.entity.GroupInfoEntity;
import OGTSystem.entity.GroupUserIdentityEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface GroupUserIdentityMapper {

    int insertGroupUserIdentity(GroupUserIdentityEntity group);

}
