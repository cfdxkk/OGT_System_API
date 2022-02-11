package OGTSystem.service;

import OGTSystem.entity.GroupInfoEntity;
import OGTSystem.entity.GroupRelationshipEntity;
import OGTSystem.repository.GroupInfoRepository;
import OGTSystem.repository.GroupRelationshipRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class GroupRelationshipService {
    @Autowired
    GroupRelationshipRepository repository;

    @Autowired
    UserInfoService userinfoservice;

    public List<GroupRelationshipEntity> getGroupRelationshipByUUID(String uuid){
        return repository.getGroupRelationshipByUUID(uuid);
    }

    public int createGroupRelationship(GroupRelationshipEntity grouprelationshipentity){

        System.out.println("UUID is: " + grouprelationshipentity.getUserId());
        List<GroupRelationshipEntity> groupRelationships = repository.getGroupRelationshipByUUID(grouprelationshipentity.getUserId());
        for(GroupRelationshipEntity groupRelationship : groupRelationships){
            if ("".equals(grouprelationshipentity.getGroupId())){
                return 0;
            } else {
                if (grouprelationshipentity.getGroupId().equals(groupRelationship.getGroupId())){
                    return 0;
                }
            }
        }
        return repository.createGroupRelationship(grouprelationshipentity);
    }


}