package OGTSystem.service;

import OGTSystem.entity.GroupMessageEntity;
import OGTSystem.entity.GroupRelationshipEntity;
import OGTSystem.message.sender.group.AsynchronousGroupMessageSender;
import OGTSystem.repository.GroupMessageRepository;
import OGTSystem.repository.GroupRelationshipRepository;
import OGTSystem.vo.GroupMessageVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class GroupMessageService {




    // 创建线程安全的 getGroupRelationshipByGroupId 对象
    private static GroupRelationshipRepository grouprelationshiprepository;
    @Autowired
    public void setGrouprelationshiprepository(GroupRelationshipRepository grouprelationshiprepository){
        GroupMessageService.grouprelationshiprepository = grouprelationshiprepository;
    }

    // 创建线程安全的 AsynchronousGroupMessageSender 对象
    private static AsynchronousGroupMessageSender asynchronousgroupmessagesender;
    @Autowired
    public void setAsynchronousgroupmessagesender(AsynchronousGroupMessageSender asynchronousgroupmessagesender){
        GroupMessageService.asynchronousgroupmessagesender = asynchronousgroupmessagesender;
    }

    // 创建线程安全的 MessageNoGroupService 对象
    private static MessageNoGroupService messagenogroupservice;
    @Autowired
    public void setMessagenogroupservice(MessageNoGroupService messagenogroupservice){
        GroupMessageService.messagenogroupservice = messagenogroupservice;
    }

    // 创建线程安全的 RedisTemplate 对象
    private static RedisTemplate<String, String> redistemplate;
    @Autowired
    public void setRedistemplate(RedisTemplate<String, String> redistemplate){
        GroupMessageService.redistemplate = redistemplate;
    }

    // 创建线程安全的 GroupMessageRepository 对象
    private static GroupMessageRepository groupmessagerepository;
    @Autowired
    public void setGroupmessagerepository(GroupMessageRepository groupmessagerepository){
        GroupMessageService.groupmessagerepository = groupmessagerepository;
    }






    public boolean MessageToGroupUser (GroupMessageVo groupmessagevo) {

        // 1. 检查消息完整性

        // 2. 检查消息是否违规

        // 3. 获得消息顺序号
        String oldGroupMessageNo = messagenogroupservice.getAndCheckAndEditMessageNo(groupmessagevo.getGroupIdFrom(), redistemplate);
        long newGroupMessageNo = (Long.parseLong(oldGroupMessageNo))+1;


        // 4. 根据groupIdFrom获取群用户列表
        List<GroupRelationshipEntity> userList = grouprelationshiprepository.getGroupRelationshipByGroupId(groupmessagevo.getGroupIdFrom());

        // 这里理论上可以通过userList获取到这些用户的在线情况 -> select * from xxx where userid in ['foo', 'bar', 'baz']
        // 然后只给在线用户发送消息


        groupmessagevo.setMessageNoInGroup(Long.toString(newGroupMessageNo));
        groupmessagevo.setSentDate(new Date().getTime());


        // 5. 遍历userList
        for(GroupRelationshipEntity user: userList) {

            // 和发送者uuid相同，跳过
            if (user.getUserId().equals(groupmessagevo.getUuidFrom())){
                continue;
            }


            // 5.1 向user推送消息并获取消息发送状态
            boolean groupMessageSentStatus = asynchronousgroupmessagesender.sentMessageToGroupUser(
                    groupmessagevo.getGroupIdFrom(),
                    groupmessagevo.getUuidFrom(),
                    user.getUserId(),
                    Long.toString(newGroupMessageNo),
                    groupmessagevo.getToken(),
                    groupmessagevo.getMessageType(),
                    groupmessagevo.getMessage()
                );

            // 5.1.1 如果消息发送失败
            if (!groupMessageSentStatus){
                // 在redis层存储离线消息记录
                this.saveOfflineGroupMessageToRedis(groupmessagevo, user.getUserId(), redistemplate);
            }

        }

        // 6. 在持久层存储离线消息记录
        this.saveOfflineGroupMessageToMysql(groupmessagevo);

        // 7. 群消息顺序号 + 1
        messagenogroupservice.messageNoPlusOne(groupmessagevo.getGroupIdFrom(), redistemplate, oldGroupMessageNo);

        return true;
    }


    private void saveOfflineGroupMessageToRedis(GroupMessageVo groupmessagevo, String uuidTo, RedisTemplate<String, String> redistemplate){
        long now = groupmessagevo.getSentDate();
        String groupMessageRedisKey = "GM>" + groupmessagevo.getGroupIdFrom() + ">" + uuidTo;


        String messageBody = groupmessagevo.getGroupIdFrom() + " : " + groupmessagevo.getUuidFrom() + " : " + uuidTo + " : " + groupmessagevo.getMessageNoInGroup() + " : " + groupmessagevo.getMessageType() + " : " + groupmessagevo.getLangMessageFlag() + " : " + groupmessagevo.getViolationFlag() + " : " + groupmessagevo.getMessage() + " : " + now;
        redistemplate.opsForZSet().add(groupMessageRedisKey, messageBody,new Long(groupmessagevo.getMessageNoInGroup()));

    }

    private void saveOfflineGroupMessageToMysql(GroupMessageVo groupmessagevo){
        groupmessagerepository.createMessage(this.groupMessageVo2Entity(groupmessagevo));
    }

    private GroupMessageEntity groupMessageVo2Entity(GroupMessageVo groupmessagevo) {
        GroupMessageEntity entity = new GroupMessageEntity();
        entity.setGroupIdFrom(groupmessagevo.getGroupIdFrom());
        entity.setUuidFrom(groupmessagevo.getUuidFrom());
        entity.setMessageNoInGroup(groupmessagevo.getMessageNoInGroup());
        entity.setToken(groupmessagevo.getToken());
        entity.setMessageType(groupmessagevo.getMessageType());
        entity.setLangMessageFlag(groupmessagevo.getLangMessageFlag());
        entity.setViolationFlag(groupmessagevo.getViolationFlag());
        entity.setMessage(groupmessagevo.getMessage());
        entity.setSentDate(new Date(groupmessagevo.getSentDate() == null ? 0 : groupmessagevo.getSentDate()));
        entity.setEditDate(new Date(groupmessagevo.getEditDate() == null ? 0 : groupmessagevo.getEditDate()));
        entity.setMessageEditorID(groupmessagevo.getMessageEditorID());

        return entity;
    }
}
