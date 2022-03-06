package OGTSystem.service;

import OGTSystem.entity.UserSafeInfoEntity;
import OGTSystem.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import OGTSystem.entity.UserInfoEntity;

import java.util.List;

@Service
public class UserInfoService {

    @Autowired
    UserInfoRepository repository;

    public List<UserSafeInfoEntity> getByUUID(String uuid){
        return repository.getByUUID(uuid);
    }

    public List<UserInfoEntity> getByUserInfoEntity (UserInfoEntity userinfoentity){
        return repository.getByUserInfoEntity(userinfoentity);
    }

    // 设置用户在线情况
    public int setUserWebSocketServiceInfo (String UUID, String terminalType, String status, String userWsServer ){

        UserInfoEntity userinfoentity = new UserInfoEntity();
        userinfoentity.setUserId(UUID);
        userinfoentity.setWsStatus(status);
        userinfoentity.setUserWsServer(userWsServer);

        if ( terminalType != null ){
            switch (terminalType){
                case "android_phone": userinfoentity.setWsAndroidPhone(status); break;
                case "ios_phone": userinfoentity.setWsIosPhone(status); break;
                case "android_pad": userinfoentity.setWsAndroidPad(status); break;
                case "ios_pad": userinfoentity.setWsIosPad(status); break;
                case "windows": userinfoentity.setWsWindows(status); break;
                case "macos": userinfoentity.setWsMacos(status); break;
                case "web_browser": userinfoentity.setWsWebBrowser(status); break;
                case "linux": userinfoentity.setWsLinux(status); break;
            }
        }

        return repository.setUserWebSocketServiceInfo(userinfoentity);
    }
}
