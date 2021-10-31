package OGTSystem.service;

import OGTSystem.repository.userInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import OGTSystem.entity.userInfoEntity;

import java.util.List;

@Service
public class userInfoService {

    @Autowired
    userInfoRepository repository;

    public List<userInfoEntity> getByUUID(String uuid){
        return repository.getByUUID(uuid);
    }



}
