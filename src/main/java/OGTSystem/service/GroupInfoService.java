package OGTSystem.service;

import OGTSystem.entity.GroupInfoEntity;
import OGTSystem.entity.UserAuthEntity;
import OGTSystem.entity.UserInfoEntity;
import OGTSystem.repository.GroupInfoRepository;
import OGTSystem.util.UserToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GroupInfoService {
    @Autowired
    GroupInfoRepository repository;

    @Autowired
    UserInfoService userinfoservice;

    public String createGroup(GroupInfoEntity groupinfoentity){


        // uuid gen
        String groupId = "";
        groupId = UUID.randomUUID().toString().replaceAll("-", "");


        // check
        if ( "".equals(groupId) ){
            return "false";
        } else {
            groupinfoentity.setGroupId(groupId);
            repository.createGroup(groupinfoentity);
            return groupId;
        }
    }

    public List<GroupInfoEntity> getByGroupName(String groupName){
        // check
        if ( "".equals(groupName) ){
            return null;
        } else {
            groupName = "%" + groupName + "%";
            return repository.getByGroupName(groupName);
        }
    }

    public List<GroupInfoEntity> getByGroupId(String groupId){
        // check
        if ( "".equals(groupId) ){
            return null;
        } else {
            return repository.getByGroupId(groupId);
        }
    }


}