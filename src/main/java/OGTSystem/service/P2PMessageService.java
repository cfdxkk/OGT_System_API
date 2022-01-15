package OGTSystem.service;

import OGTSystem.entity.MessageNoEntity;
import OGTSystem.entity.P2PMessageEntity;
import OGTSystem.entity.UserInfoEntity;
import OGTSystem.repository.MessageNoFriendRepository;
import OGTSystem.repository.P2PMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

@Service
public class P2PMessageService {

    @Autowired
    P2PMessageRepository p2pmessagerepository;

    public void setP2Pmessage(
            String messageNo,
            String uuidFrom,
            String uuidTo,
            String message,
            String messageType,
            RedisTemplate<String, String> redistemplate
    ){


            P2PMessageEntity p2pmessageentity = new P2PMessageEntity();
            p2pmessageentity.setChatMessageNo(new Long(messageNo));
            p2pmessageentity.setMessageFrom(uuidFrom);
            p2pmessageentity.setMessageTarget(uuidTo);
            p2pmessageentity.setMessageType(messageType);
            p2pmessageentity.setSendFlag(0);
            p2pmessageentity.setReadFlag(0);
            p2pmessageentity.setViolationFlag(0); // 违规flag
            p2pmessageentity.setSentDate(new Date());
            p2pmessageentity.setEditDate(new Date());
            p2pmessageentity.setMessageEditorID(uuidFrom);
            if (message.length()>300){
                p2pmessageentity.setLongMessageFlag(1);

                // 其实这个地方的message字段应该存长消息的地址
                p2pmessageentity.setMessage(message.substring(0,295)+"...");

                // 向redis层存储消息
                // 其实这个地方的message字段应该存长消息的地址
                this.addNewMessageToRedisBySortedSet(messageNo,uuidFrom,uuidTo,message.substring(0,295)+"...",messageType,1,redistemplate);


            } else {
                p2pmessageentity.setLongMessageFlag(0);
                p2pmessageentity.setMessage(message);

                // 向redis层存储消息
                // 其实这个地方的message字段应该存长消息的地址
                this.addNewMessageToRedisBySortedSet(messageNo,uuidFrom,uuidTo,message,messageType,1,redistemplate);
            }

            // 插入这条消息数据
            p2pmessagerepository.createMessage(p2pmessageentity);

    }

    private void addNewMessageToRedisBySortedSet(
            String messageNo,
            String uuidFrom,
            String uuidTo,
            String message,
            String messageType,
            int longMessageFlag,
            RedisTemplate<String, String> redistemplate
    ){
        String messageBody = message + " : " + uuidFrom + " : " + uuidTo + " : " + messageType + " : " + messageNo + " : " + longMessageFlag;
        redistemplate.opsForZSet().add("P2PM-"+uuidTo,messageBody,new Double(messageNo));
    }
}
