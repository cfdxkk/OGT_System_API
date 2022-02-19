package OGTSystem.service;

import OGTSystem.entity.MessageNoFriendEntity;
import OGTSystem.repository.MessageNoFriendRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageNoFriendService {

    @Autowired
    MessageNoFriendRepository messagenofriendrepository;

    @Autowired
    UserInfoService userinfoservice;

    public List<MessageNoFriendEntity> getMessageNoByFriendString(String friend){
        return messagenofriendrepository.getMessageNoByFriendString(friend);
    }

    public int createMessageNo(MessageNoFriendEntity messagenoentity){
        return messagenofriendrepository.createMessageNo(messagenoentity);
    }

    public int setMessageNo(MessageNoFriendEntity messagenoentity){
        return messagenofriendrepository.setMessageNo(messagenoentity);
    }

    public String getAndCheckAndEditMessageNo(Long uunoFrom, Long uunoTo, RedisTemplate<String, String> redistemplate){

        // 获取/存储 消息顺序号(redis/持久层)
        // 比较uunoFrom和uunoTo的大小，将他们大的设在后，小的在前组成一个新字符串
        String messageNoKey = "";
        if(uunoFrom > uunoTo){
            messageNoKey = uunoTo + "-" + uunoFrom;
        } else {
            messageNoKey = uunoFrom + "-" + uunoTo;
        }
        System.out.println("两个人的消息记录key为: " + messageNoKey);

        // 根据拼接成的新字符串找到对应两个人的消息NO
        String messageNo = "";
        String messageNoRedisTemp = redistemplate.opsForValue().get(messageNoKey);  // 从redis层获取消息number
        System.out.println("messageNoRedisTemp is: " + messageNoRedisTemp);
        // 如果找不到(表明两个人第一次通讯或者很久都没有通讯)
        if ("".equals(messageNoRedisTemp) || messageNoRedisTemp == null){
            // 去持久层找
            List<MessageNoFriendEntity> messageNosMysqlTemp = this.getMessageNoByFriendString(messageNoKey);
            if (messageNosMysqlTemp.size() == 0){  // 如果持久层找不到
                System.out.println("持久层没找到，开始在持久层和redis层新建");
                // 持久层新建
                MessageNoFriendEntity messagenoentity = new MessageNoFriendEntity();
                messagenoentity.setFriend(messageNoKey);
                messagenoentity.setMessageNo(0L);
                this.createMessageNo(messagenoentity);
                // redis层新建
                redistemplate.opsForValue().set(messageNoKey,"0");
                // 赋值messageNo 为 0
                messageNo = "0";

            } else {  // 持久层找到了
                System.out.println("持久层找到了，开始在redis层新建持久层找到的数据");
                // 根据持久层的结果，把这两个人的messageNo存储到redis里
                String messageNoMysqlTemp =  messageNosMysqlTemp.get(0).getMessageNo().toString();
                redistemplate.opsForValue().set(messageNoKey,messageNoMysqlTemp);
                // 赋值messageNo
                messageNo = messageNoMysqlTemp;
            }
        } else {  // redis 里找到了messageNo

            System.out.println("redis层找到了，开始更新持久层数据");
            // 更新持久层数据与redis层统一
            MessageNoFriendEntity messagenoentity = new MessageNoFriendEntity();
            messagenoentity.setFriend(messageNoKey);
            messagenoentity.setMessageNo(new Long(messageNoRedisTemp));
            this.setMessageNo(messagenoentity);

            // 赋值messageNo
            messageNo = messageNoRedisTemp;
        }
        System.out.println("获得到的 messageNo 为 [" + messageNo + "]");
        return messageNo;
    }

    public String messageNoPlusOne(Long uunoFrom, Long uunoTo, RedisTemplate<String, String> redistemplate, String messageNo){

        // redis和持久层的消息no都加1

        // 比较uunoFrom和uunoTo的大小，将他们大的设在后，小的在前组成一个新字符串
        String messageNoKey = "";
        if(uunoFrom > uunoTo){
            messageNoKey = uunoTo + "-" + uunoFrom;
        } else {
            messageNoKey = uunoFrom + "-" + uunoTo;
        }
        System.out.println("两个人的消息记录key为: " + messageNoKey);

        Long newMessageNo = (new Long(messageNo))+1;

        // 持久层+1
        MessageNoFriendEntity messagenoentity = new MessageNoFriendEntity();
        messagenoentity.setFriend(messageNoKey);
        messagenoentity.setMessageNo(newMessageNo);
        this.setMessageNo(messagenoentity);

        // redis层+1
        redistemplate.opsForValue().set(messageNoKey,newMessageNo.toString());

        return "新的消息NO是: [" + newMessageNo + "]";
    }
}
