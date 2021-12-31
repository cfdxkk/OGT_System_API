package OGTSystem.mapper;

import OGTSystem.entity.UserInfoEntity;
import OGTSystem.entity.WsServerInfoEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WsServerInfoMapper {

    List<WsServerInfoEntity> selectByServerNO(Long serverNO);
    List<WsServerInfoEntity> selectWsServerInfo(WsServerInfoEntity wsserverinfoentity);
    int createWsServer(WsServerInfoEntity wsserverinfoentity);
    int setServerConnectNumber(WsServerInfoEntity wsserverinfoentity);




}
