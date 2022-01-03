package OGTSystem.service;

import OGTSystem.entity.UserInfoEntity;
import OGTSystem.entity.WsServerInfoEntity;
import OGTSystem.repository.UserInfoRepository;
import OGTSystem.repository.WsServerInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WsServerInfoService {

    @Autowired
    WsServerInfoRepository repository;

    public List<WsServerInfoEntity> getByServerNO(Long serverNO){
        return repository.getByServerNO(serverNO);
    }

    public boolean wsServerConnectNumberPlusOne(String serverAddress){
        boolean status = false;

        try{
            WsServerInfoEntity wsserverinfoentity = new WsServerInfoEntity();
            wsserverinfoentity.setServerAddress(serverAddress);
            List<WsServerInfoEntity> serverinfo = repository.getWsServerInfo(wsserverinfoentity);
            if (serverinfo.isEmpty()){
                wsserverinfoentity.setConnectNumbers(1L);
                repository.createWsServer(wsserverinfoentity);
            } else {
                Long connectNumber = serverinfo.get(0).getConnectNumbers();
                Long newConnectNumber = connectNumber + 1;
                wsserverinfoentity.setConnectNumbers(newConnectNumber);
                repository.setServerConnectNumber(wsserverinfoentity);
            }
            status = true;
        } catch (Exception e){
            System.out.println(e);
        }

        return status;
    }

    public boolean wsServerConnectNumberSubOne(String serverAddress){
        boolean status = false;

        try{
            WsServerInfoEntity wsserverinfoentity = new WsServerInfoEntity();
            wsserverinfoentity.setServerAddress(serverAddress);
            List<WsServerInfoEntity> serverinfo = repository.getWsServerInfo(wsserverinfoentity);
            Long connectNumber = serverinfo.get(0).getConnectNumbers();
            if (serverinfo.isEmpty()){
                System.out.println("发生致命错误，断开WebSocket服务器时找不到服务器");
                System.out.println("可能是服务器地址有误");
            } else if (connectNumber == 0){
                System.out.println("发生致命错误，断开WebSocket服务器时找不到服务器");
                System.out.println("可能是该用户连接的服务器显示连接数为0");
            } else {
                Long newConnectNumber = connectNumber - 1;
                wsserverinfoentity.setConnectNumbers(newConnectNumber);
                repository.setServerConnectNumber(wsserverinfoentity);
            }
            status = true;
        } catch (Exception e){
            System.out.println(e);
        }

        return status;
    }

    public List<WsServerInfoEntity> getAllServerAddress(){

        WsServerInfoEntity wsserverinfoentity = new WsServerInfoEntity();
        return repository.getWsServerInfo(wsserverinfoentity);

    }

}
