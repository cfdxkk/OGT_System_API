package OGTSystem.service;

import OGTSystem.entity.P2PMessageEntity;
import OGTSystem.repository.P2PMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class P2PMessageService {

    @Autowired
    P2PMessageRepository p2pmessagerepository;

    /**
     *
     * 向redis和mysql插入离线消息记录
     *
     * @param messageNo
     * @param uuidFrom
     * @param uuidTo
     * @param message
     * @param messageType
     * @param redistemplate
     */
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
                this.addNewMessageToRedisBySortedSet(messageNo,uuidFrom,uuidTo,message,messageType,0,redistemplate);
            }

            // 插入这条消息数据
            p2pmessagerepository.createMessage(p2pmessageentity);

    }


    /**
     *
     * 插入持久层消息记录
     *
     * @param messageNo
     * @param uuidFrom
     * @param uuidTo
     * @param message
     * @param messageType
     */
    public void setP2PmessageOnlyToMySQL(
            String messageNo,
            String uuidFrom,
            String uuidTo,
            String message,
            String messageType
    ){
        P2PMessageEntity p2pmessageentity = new P2PMessageEntity();
        p2pmessageentity.setChatMessageNo(new Long(messageNo));
        p2pmessageentity.setMessageFrom(uuidFrom);
        p2pmessageentity.setMessageTarget(uuidTo);
        p2pmessageentity.setMessageType(messageType);
        p2pmessageentity.setSendFlag(1);
        p2pmessageentity.setReadFlag(0);
        p2pmessageentity.setViolationFlag(0); // 违规flag
        p2pmessageentity.setSentDate(new Date());
        p2pmessageentity.setEditDate(new Date());
        p2pmessageentity.setMessageEditorID(uuidFrom);
        if (message.length()>300){
            p2pmessageentity.setLongMessageFlag(1);
            // 其实这个地方的message字段应该存长消息的地址
            p2pmessageentity.setMessage(message.substring(0,295)+"...");
        } else {
            p2pmessageentity.setLongMessageFlag(0);
            p2pmessageentity.setMessage(message);
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
        long now = new Date().getTime();
        String messageBody = message + " : " + uuidFrom + " : " + uuidTo + " : " + messageType + " : " + messageNo + " : " + longMessageFlag + " : " + now;
        redistemplate.opsForZSet().add("P2PM-"+uuidTo,messageBody,new Long(messageNo));
    }
}
