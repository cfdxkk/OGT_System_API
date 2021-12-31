package OGTSystem.repository;

import OGTSystem.entity.UserInfoEntity;
import OGTSystem.entity.WsServerInfoEntity;
import OGTSystem.mapper.UserOftenEditMapper;
import OGTSystem.mapper.WsServerInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class WsServerInfoRepository {

    @Autowired
    WsServerInfoMapper mapper;

    public List<WsServerInfoEntity> getByServerNO(Long serverNO){
        return mapper.selectByServerNO(serverNO);
    }

    public List<WsServerInfoEntity> getWsServerInfo(WsServerInfoEntity wsserverinfoentity){
        return mapper.selectWsServerInfo(wsserverinfoentity);
    }

    public int createWsServer (WsServerInfoEntity wsserverinfoentity){
        return mapper.createWsServer(wsserverinfoentity);
    }

    public int setServerConnectNumber(WsServerInfoEntity wsserverinfoentity){
        return mapper.setServerConnectNumber(wsserverinfoentity);
    }
}
