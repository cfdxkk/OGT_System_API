package OGTSystem.service;

import OGTSystem.repository.UserInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import OGTSystem.entity.UserInfoEntity;

import java.util.List;

@Service
public class UserInfoService {

    @Autowired
    UserInfoRepository repository;

    public List<UserInfoEntity> getByUUID(String uuid){
        return repository.getByUUID(uuid);
    }

    public List<UserInfoEntity> getByUserInfoEntity (UserInfoEntity userinfoentity){
        return repository.getByUserInfoEntity(userinfoentity);
    }
}
