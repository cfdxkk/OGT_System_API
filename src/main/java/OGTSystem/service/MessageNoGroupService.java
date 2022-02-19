package OGTSystem.service;

import OGTSystem.entity.MessageNoFriendEntity;
import OGTSystem.entity.MessageNoGroupEntity;
import OGTSystem.repository.MessageNoFriendRepository;
import OGTSystem.repository.MessageNoGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageNoGroupService {

    @Autowired
    MessageNoGroupRepository messagenogrouprepository;

    @Autowired
    UserInfoService userinfoservice;

    public List<MessageNoGroupEntity> getMessageNoByGroupId(String friend){
        return messagenogrouprepository.getMessageNoByGroupId(friend);
    }

    public int createGroupMessageNo(MessageNoGroupEntity messagenogroupentity){
        return messagenogrouprepository.createGroupMessageNo(messagenogroupentity);
    }

    public int setGroupMessageNo(MessageNoGroupEntity messagenogroupentity){
        return messagenogrouprepository.setGroupMessageNo(messagenogroupentity);
    }

    public String getAndCheckAndEditMessageNo(String groupId, RedisTemplate<String, String> redistemplate){

        // 获取/存储 群聊消息顺序号(redis/持久层)

        // 在groupId前拼接一个 g_
        String savableGroupId = "g_" + groupId;


        // 根据拼接成的新字符串找到群聊消息No
        String groupMessageNo = "";
        String groupMessageNoRedisTemp = redistemplate.opsForValue().get(savableGroupId);  // 从redis层获取消息No

        // 如果redis层找不到(表明是群聊第一条消息或该群很久都没有消息)
        if ("".equals(groupMessageNoRedisTemp) || groupMessageNoRedisTemp == null){
            // 去持久层找
            List<MessageNoGroupEntity> groupMessageNosMysqlTemp = this.getMessageNoByGroupId(groupId);

            if (groupMessageNosMysqlTemp.size() == 0){  // 如果持久层也找不到(表明这是群聊第一条消息)
                // 持久层新建
                MessageNoGroupEntity messagenogroupentity = new MessageNoGroupEntity();
                messagenogroupentity.setGroupId(groupId);
                messagenogroupentity.setMessageNo(0L);
                this.createGroupMessageNo(messagenogroupentity);
                // redis层新建
                redistemplate.opsForValue().set(savableGroupId,"0");
                // 赋值messageNo 为 0
                groupMessageNo = "0";

            } else {  // 持久层找到了 (表明是群很久都没有消息)
                // 根据持久层的结果，把这两个人的messageNo存储到redis里
                String messageNoMysqlTemp =  groupMessageNosMysqlTemp.get(0).getMessageNo().toString();
                redistemplate.opsForValue().set(savableGroupId,messageNoMysqlTemp);  // 更新redis数据与持久层统一
                // 赋值messageNo
                groupMessageNo = messageNoMysqlTemp;
            }
        } else {  // redis 里找到了messageNo

            System.out.println("redis层找到了，开始更新持久层数据");
            // 更新持久层数据与redis层统一
            MessageNoGroupEntity messagenogroupentity = new MessageNoGroupEntity();
            messagenogroupentity.setGroupId(groupId);
            messagenogroupentity.setMessageNo(new Long(groupMessageNoRedisTemp));
            this.setGroupMessageNo(messagenogroupentity);

            // 赋值messageNo
            groupMessageNo = groupMessageNoRedisTemp;
        }
        return groupMessageNo;
    }

    public String messageNoPlusOne(String groupId, RedisTemplate<String, String> redistemplate, String groupMessageNo){

        // redis和持久层的消息no都加1

        // 在groupId前拼接一个 g_
        String savableGroupId = "g_" + groupId;

        Long newGroupMessageNo = (new Long(groupMessageNo))+1;

        // 持久层+1
        MessageNoGroupEntity messagenogroupentity = new MessageNoGroupEntity();
        messagenogroupentity.setGroupId(groupId);
        messagenogroupentity.setMessageNo(newGroupMessageNo);
        this.setGroupMessageNo(messagenogroupentity);

        // redis层+1
        redistemplate.opsForValue().set(savableGroupId,newGroupMessageNo.toString());

        return "新的消息NO是: [" + newGroupMessageNo + "]";
    }
}
