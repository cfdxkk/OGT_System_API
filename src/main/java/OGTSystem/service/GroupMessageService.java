package OGTSystem.service;

import OGTSystem.controller.GroupController;
import OGTSystem.controller.WebSocketMessagePullController;
import OGTSystem.entity.*;
import OGTSystem.message.recipient.AsynchronousMessageRecipient;
import OGTSystem.message.sender.group.AsynchronousGroupMessageSender;
import OGTSystem.repository.GroupEventRepository;
import OGTSystem.repository.GroupMessageRepository;
import OGTSystem.repository.GroupRelationshipRepository;
import OGTSystem.vo.GroupEventVo;
import OGTSystem.vo.GroupMessageVo;
import OGTSystem.vo.P2PMessageVo;
import com.sun.tools.javac.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import java.util.*;

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


    // 创建线程安全的 UserAuthService 对象
    private static UserAuthService userauthservice;
    @Autowired
    public void setUserauthservice(UserAuthService userauthservice){
        GroupMessageService.userauthservice = userauthservice;
    }

    // 创建线程安全的 GroupRelationshipService 对象
    private static GroupRelationshipService grouprelationshipservice;
    @Autowired
    public void setGrouprelationshipservice(GroupRelationshipService grouprelationshipservice){
        GroupMessageService.grouprelationshipservice = grouprelationshipservice;
    }

    // 创建线程安全的 UserInfoService 对象
    private static UserInfoService userinfoservice;
    @Autowired
    public void setUserauthservice(UserInfoService userinfoservice){
        GroupMessageService.userinfoservice = userinfoservice;
    }

    // 创建线程安全的 GroupEventRepository 对象
    private static GroupEventRepository groupeventrepository;
    @Autowired
    public void setGroupeventrepository(GroupEventRepository groupeventrepository){
        GroupMessageService.groupeventrepository = groupeventrepository;
    }

    // 创建线程安全的 GroupUserIdentityService 对象
    private static GroupUserIdentityService groupuseridentityservice;
    @Autowired
    public void setGroupuseridentityservice(GroupUserIdentityService groupuseridentityservice){
        GroupMessageService.groupuseridentityservice = groupuseridentityservice;
    }











    public boolean MessageToGroupUser (GroupMessageVo groupmessagevo) {

        System.out.println("checkTime?");

        // 0. 验证用户是否有权利发这条消息
        if (grouprelationshipservice.checkUserInGroup(groupmessagevo.getUuidFrom(), groupmessagevo.getGroupIdFrom())) {
            // 1. 检查消息完整性

            // 2. 检查消息是否违规,并且过滤消息
            // 如果是普通消息(type == 1)，长度超过295则截取
            if("1".equals(groupmessagevo.getMessageType())) {
                groupmessagevo.setMessage(groupmessagevo.getMessage().length() > 295 ? groupmessagevo.getMessage().substring(0, 290) + "..." : groupmessagevo.getMessage());
            }

            // 3. 获得消息顺序号
            String oldGroupMessageNo = messagenogroupservice.getAndCheckAndEditMessageNo(groupmessagevo.getGroupIdFrom(), redistemplate);
            long newGroupMessageNo = (Long.parseLong(oldGroupMessageNo))+1;

            // 这里理论上可以通过userList获取到这些用户的在线情况 -> select * from xxx where userid in ['foo', 'bar', 'baz']
            // 然后只给在线用户发送消息

            groupmessagevo.setMessageNoInGroup(Long.toString(newGroupMessageNo));
            groupmessagevo.setSentDate(new Date().getTime());

            // 4. 获取发送者用户信息
            List<UserSafeInfoEntity> userInfo = userinfoservice.getByUUID(groupmessagevo.getUuidFrom());
            if (userInfo.size() > 0) {
                // 4.1 获取发送者用户名
                String usernameFrom = userInfo.get(0).getUsername();
                groupmessagevo.setUsernameFrom(usernameFrom);

                // 5. 根据groupIdFrom获取群用户列表
                List<GroupRelationshipEntity> userList = grouprelationshiprepository.getGroupRelationshipByGroupId(groupmessagevo.getGroupIdFrom());


                // 6. 遍历userList
                for(GroupRelationshipEntity user: userList) {

                    // 6.1 和发送者uuid相同，跳过
                    if (user.getUserId().equals(groupmessagevo.getUuidFrom())){
                        continue;
                    }

                    System.out.println("22222222222222222 times?" + user.getUserId());

                    // 6.2 向user推送消息并获取消息发送状态
                    boolean groupMessageSentStatus = asynchronousgroupmessagesender.sentMessageToGroupUser(
                            groupmessagevo.getGroupIdFrom(),
                            groupmessagevo.getUuidFrom(),
                            groupmessagevo.getUsernameFrom(),
                            user.getUserId(),
                            Long.toString(newGroupMessageNo),
                            groupmessagevo.getToken(),
                            groupmessagevo.getMessageType(),
                            groupmessagevo.getMessage()
                    );
                    System.out.println("向" + user.getUserId() + "的消息" + groupmessagevo.getMessage() + "发送结果为" + groupMessageSentStatus);
                    // 2.2.1 如果消息发送失败
                    if (!groupMessageSentStatus){
                        // 在redis层存储离线消息记录
                        this.saveOfflineGroupMessageToRedis(groupmessagevo, user.getUserId(), redistemplate);
                    }

                }
            } else {
                System.out.println("消息发送失败[获取用户名失败]");
                return false;
            }



            // 6. 在持久层存储离线消息记录
            if("1".equals(groupmessagevo.getMessageType())) {
                // 6.1 如果是普通消息，就往消息表存储
                this.saveGroupMessage2Mysql(groupmessagevo);
            } else {
                // 6.2 如果是事件，就往事件表存
                GroupEventEntity groupevententity = new GroupEventEntity();
                String[] eventStringArray = groupmessagevo.getMessage().split(" >c10y_:< ");
                groupevententity.setGroupIdFrom(groupmessagevo.getGroupIdFrom());
                groupevententity.setUuidFrom(groupmessagevo.getUuidFrom());
                groupevententity.setEventStartDateTime(new Date(Long.parseLong(eventStringArray[0])));
                groupevententity.setEventEndDateTime(new Date(Long.parseLong(eventStringArray[1])));
                if (eventStringArray[3].length() > 800){
                    groupevententity.setLangEventFlag(1);
                    groupevententity.setEventText(eventStringArray[3].substring(0, 800) + "..."  );
                } else {
                    groupevententity.setLangEventFlag(0);
                    groupevententity.setEventText(eventStringArray[3]);
                }
                groupevententity.setEventColor(eventStringArray[4]);
                groupevententity.setEventTitle(eventStringArray[2]);
                groupevententity.setSentDate(new Date());

                this.saveGroupEvent2Mysql(groupevententity);

            }

            // 7. 群消息顺序号 + 1
            messagenogroupservice.messageNoPlusOne(groupmessagevo.getGroupIdFrom(), redistemplate, oldGroupMessageNo);

            return true;
        } else {
            System.out.println("用户已经不在群聊中");
            return false;
        }
    }


    private boolean saveOfflineGroupMessageToRedis(GroupMessageVo groupmessagevo, String uuidTo, RedisTemplate<String, String> redistemplate){
        long now = groupmessagevo.getSentDate();

        String groupMessageRedisKey = "GM>" + groupmessagevo.getGroupIdFrom() + ">" + uuidTo;
        String messageBody = groupmessagevo.getGroupIdFrom() + " : " + groupmessagevo.getUuidFrom() + " : " + groupmessagevo.getUsernameFrom() + " : " + uuidTo + " : " + groupmessagevo.getMessageNoInGroup() + " : " + groupmessagevo.getMessageType() + " : " + groupmessagevo.getLangMessageFlag() + " : " + groupmessagevo.getViolationFlag() + " : " + groupmessagevo.getMessage() + " : " + now;

        System.out.println("offlineMessageKey" + groupMessageRedisKey);
        System.out.println("offlineMessageBody" + messageBody);

        redistemplate.opsForZSet().add(groupMessageRedisKey, messageBody,new Long(groupmessagevo.getMessageNoInGroup()));

        return true;
    }

    private void saveGroupMessage2Mysql(GroupMessageVo groupmessagevo){
        groupmessagerepository.createMessage(this.groupMessageVo2Entity(groupmessagevo));
    }

    private void saveGroupEvent2Mysql(GroupEventEntity groupevententity){
        groupeventrepository.createGroupEvent(groupevententity);
    }



    public HashMap<String, List<GroupMessageVo>> getOfflineMessage(String userId, String token){

        if (userauthservice.userAuthCheck(userId,token)){  // 验证用户是否是合规

            // 获取这个用户加入的所有群聊信息
            List<GroupRelationshipEntity> groupInfoList = grouprelationshipservice.getGroupRelationshipByUUID(userId);

            HashMap<String, List<GroupMessageVo>> userofflinemessagelistmap = new HashMap<>();

            if(groupInfoList != null && groupInfoList.size() != 0) {
                // 遍历用户群聊信息，获得群号，然后根据群号获取这个用户在各个群的离线消息
                for(GroupRelationshipEntity groupinfo : groupInfoList){

                    List<GroupMessageVo> offlineGroupMessagesList = new ArrayList<GroupMessageVo>();
                    // 获取这个用户在这个群的离线消息列表
                    Set<ZSetOperations.TypedTuple<String>> offlineMessagesSet = redistemplate.opsForZSet().rangeWithScores("GM>" + groupinfo.getGroupId() + ">" + userId,0,-1);

                    if (offlineMessagesSet != null && offlineMessagesSet.size() != 0) {
                        // 遍历离线消息列表，获取每一条消息(从set转为list)
                        for (ZSetOperations.TypedTuple<String> message : offlineMessagesSet){

                            String messageString = message.getValue();
                            // 切割字符串
                            if (messageString != null && !("".equals(messageString)) ){
                                String[] messageStringArray = messageString.split(" : ");
                                if(this.checkFullArray(messageStringArray)){
                                    GroupMessageVo groupmessagevo = new GroupMessageVo();

                                    groupmessagevo.setGroupIdFrom(messageStringArray[0]);
                                    groupmessagevo.setUuidFrom(messageStringArray[1]);
                                    groupmessagevo.setUsernameFrom(messageStringArray[2]);
                                    groupmessagevo.setUuidTo(messageStringArray[3]);
                                    groupmessagevo.setMessageNoInGroup(messageStringArray[4]);
                                    groupmessagevo.setMessageType(messageStringArray[5]);
                                    groupmessagevo.setLangMessageFlag(Integer.parseInt(messageStringArray[6]));
                                    groupmessagevo.setViolationFlag(Integer.parseInt(messageStringArray[7]));
                                    groupmessagevo.setMessage(messageStringArray[8]);
                                    groupmessagevo.setSentDate(Long.getLong(messageStringArray[9]));

                                    offlineGroupMessagesList.add(groupmessagevo);
                                } else {
                                    System.out.println("在获取[" + groupinfo.getGroupId() + "]群聊的离线消息时字符串切割出错");
                                    offlineGroupMessagesList.add(null);
                                }
                            }
                        }
                        // 获取到消息后就清空这个redis缓存了
                        redistemplate.opsForZSet().removeRange("GM>" + groupinfo.getGroupId() + ">" + userId,0,1000000);
                        // 把转为list的消息存放到所有群离线消息的集合中
                        userofflinemessagelistmap.put(groupinfo.getGroupId(), offlineGroupMessagesList);
                    } else {
                        System.out.println("在获取[" + groupinfo.getGroupId() + "]群聊的离线消息时获取set为空或出错");
                    }
                }
                return userofflinemessagelistmap;
            } else {
                System.out.println("用户没有加入任何群聊或在获取用户加入的群聊时出错");
                return null;
            }
        } else {
            // 用户违规 +1
            System.out.println("在获取离线消息时token验证未通过");
            return null;
        }
    }

    private boolean checkFullArray(String[] stringArr) {
        if(stringArr == null || stringArr.length == 0)
            return false;
                for (String a : stringArr)
                    if (a == null || "".equals(a))
                        return false;
                            return true;
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

    public List<GroupEventVo> getEvent(String groupId, String userId, String token) {

        if (userauthservice.userAuthCheck(userId,token)){  // 验证用户安全性

            List<GroupEventEntity> groupEvents = groupeventrepository.getEventsByGroupId(groupId);
            List<GroupEventVo> groupeventsvo = new ArrayList<GroupEventVo>();
            for(GroupEventEntity groupevent : groupEvents) {
                GroupEventVo groupeventvo = new GroupEventVo();

                groupeventvo.setEventId(Long.parseLong(groupevent.getEventId()));
                groupeventvo.setStartTime(groupevent.getEventStartDateTime().getTime());
                groupeventvo.setEndTime(groupevent.getEventEndDateTime().getTime());
                groupeventvo.setEventColor(groupevent.getEventColor());

                UserSafeInfoEntity userinfo = userinfoservice.getByUUID(groupevent.getUuidFrom()).get(0);
                groupeventvo.setUserName(userinfo.getUsername());
                groupeventvo.setUserAvatar(userinfo.getUserAvatar());

                GroupUserIdentityEntity groupuseridentityentitiy = new GroupUserIdentityEntity();
                groupuseridentityentitiy.setGroupId(groupevent.getGroupIdFrom());
                groupuseridentityentitiy.setGroupAdminId(groupevent.getUuidFrom());
                List<GroupUserIdentityEntity> useridentity = groupuseridentityservice.getUserIdentity(groupuseridentityentitiy);
                if(useridentity.size() > 0) {
                    groupeventvo.setUserType(useridentity.get(0).getGroupAdminType());
                } else {
                    groupeventvo.setUserType(0);
                }

                groupeventvo.setEventTitle(groupevent.getEventTitle());
                groupeventvo.setEventText(groupevent.getEventText());

                groupeventvo.setEventImg(new ArrayList<>());

                groupeventsvo.add(groupeventvo);
            }

            return groupeventsvo;

        } else {

            // 记录用户违规操作

            return null;
        }
    }



}
