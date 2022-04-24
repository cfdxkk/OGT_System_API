package OGTSystem.service;

import OGTSystem.entity.UserInfoEditEntity;
import OGTSystem.entity.UserSafeInfoEntity;
import OGTSystem.entity.UserSafeInfoGroupEntity;
import OGTSystem.interFuck.OssService;
import OGTSystem.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import OGTSystem.entity.UserInfoEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserInfoService {

    @Autowired
    UserInfoRepository repository;

    @Autowired
    UserAuthService userauthservice;

    @Autowired
    OssService ossservice;

    public List<UserSafeInfoEntity> getByUUID(String uuid){
        return repository.getByUUID(uuid);
    }

    public List<UserSafeInfoGroupEntity> getByUUID2(String uuid){
        return repository.getByUUID2(uuid);
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


    public Map<String, String> uploadUserAvatar(MultipartFile file, MultipartFile fileOrigin, String userId, String token){

        if (userauthservice.userAuthCheck(userId, token)) {
            Map<String, String> result = new HashMap<>();

            //获取小上传文件 inputStream
            InputStream inputStream = null;
            try {
                inputStream = file.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 小头像上传到阿里云oss
            result.put("small", ossservice.uploadFileAvatar(inputStream, file.getOriginalFilename()));

            //获取大上传文件 inputStreamOrigin
            InputStream inputStreamOrigin = null;
            try {
                inputStreamOrigin = fileOrigin.getInputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
            // 头像原图上传到阿里云oss
            result.put("full", ossservice.uploadFileAvatar(inputStreamOrigin, fileOrigin.getOriginalFilename()));

            return result;
        } else {
            System.out.println("有人可能正在利用用户头像上传接口进行攻击！");
            return null;
        }
    }


    public boolean editUserInfo(UserInfoEditEntity userinfoeditentity) {
        // 验证编辑者账户
        if (userauthservice.userAuthCheck(userinfoeditentity.getAdminUserId(), userinfoeditentity.getAdminToken())) {

            // 把被编辑的用户设为编辑者Id
            userinfoeditentity.setUserId(userinfoeditentity.getAdminUserId());

            // 防止篡改
            userinfoeditentity.setUserNo(null);
            userinfoeditentity.setUsername(null);
            userinfoeditentity.setPassword(null);
            userinfoeditentity.setUserAdminFlag(null);
            userinfoeditentity.setUserBanFlag(null);

            repository.setUserInfoInUserNotOftenEdit(userinfoeditentity);
            repository.setUserInfoInUserOftenEdit(userinfoeditentity);
            return true;
        } else {
            return false;
        }
    }



}
