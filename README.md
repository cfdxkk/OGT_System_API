## OGT (Order Game Time - 预定游戏时间)
OGT系统是用来与好友预定游戏时间，或由队长来策划并发布活动的一套系统，是我在东软的毕设。
***
此存储库为 OGT_System 的后端API，由 `Java -> Spring-boot` 编写。

在编写本系统的后期，因为要赶毕设的进度，产生了大量 _不优雅_ 的代码；同时我自己也意识到了 Java 语言的局限性，所以这个项目未来会由 `Node.js` 进行重构;

系统功能类似于 `IM系统`，数据都基于消息，不同于一般的网站使用的http请求，我们的消息通信将会使用 `Websocket` 协议，因为这个协议是 `全双工` 的。
### API
注意：除了正常的api外，当前版本也提供了几个用于调试的api，系统中可能存在废弃的API没在下文中列出
1. **[调试] [危险]** `/user/info`   获取数据库中用户的所有信息(包括敏感信息)，**请在生产环境中删除**，请求类型：`GET`
    * 支持的的参数有(URL载荷)：
        * `uuid` 根据用户的ID筛选这个用户的所有信息
        * `空` 获取全部用户的全部信息
2. `/user/register`   用户注册，请求类型：`POST`
    * 支持的参数有(JavaScript对象/JSON)：
        * `username` + `password`  将会注册这个用户，返回注册结果 (username不允许重复)
3. `/user/login`   用户登录，请求类型：`POST`
    * 支持的参数有(JavaScript对象/JSON)：
        * `username` + `password`  如果是注册用户且密码正确，则会返回token，如果不是则返回用户名或密码错误
4. **[调试] [危险]** `/user/testUserMap`   系统执行 for 循环向一个 HashMap 中插入 100000000 个数，并在控制台打印执行时间，**请在生产环境中删除**，请求类型：`GET`
    * 支持的参数有：
        * 无   返回字符串"right"
5. `/user/tokenCheck`   检查用户的 token 是否合法，请求类型：`POST`
    * 支持的参数有(JavaScript对象/JSON)：
        * `UUID` + `userToken`  返回布尔类型结果
6. `/user/avatar`   上传用户头像，请求类型：`POST`
    * 支持的参数有(JavaScript对象/JSON)：
        * `file` + `fileOrigin` + `userId` + `token`  验证用户token，如果正确则上传头像至阿里oss并返回头像图和头像原图的 URL
7. `/user/editUserInfo`   修改用户信息，请求类型：`POST`
    * 支持的参数有(JavaScript对象/JSON)：
        * 详见 `UserInfoEditEntity.java`  返回布尔型，标识是否编辑成功


8. `/group/create`   创建群聊，请求类型：`POST`
    * 支持的参数有(JavaScript对象/JSON)：
        * 详见 `GroupInfoVo.java`
9. `/group/search`   搜索群聊，请求类型：`GET`
   * 支持的参数有(JavaScript对象/JSON)：
       * `groupName`  返回搜索到的群聊信息列表
10. `/group/join`   加入群聊，请求类型：`POST`
    * 支持的参数有(JavaScript对象/JSON)：
        * 详见 `GroupRelationshipEntity.java`  返回加入的群聊的信息
11. `/group`   用户加入的群聊列表，请求类型：`GET`
    * 支持的参数有(JavaScript对象/JSON)：
        * `userId`  返回这个用户加入的群聊的信息列表
12. `/group/message`   向群聊中的其他用户发送消息，整合了全双工消息和离线消息，请求类型：`GET`
    * 支持的参数有(JavaScript对象/JSON)：
        * 详见 `GroupMessageVo.java`  返回布尔型，标识消息发送结果
13. `/group/offlineMessage`   获取自己的离线消息，请求类型：`POST`
    * 支持的参数有(JavaScript对象/JSON)：
        * 详见 `GroupMessageVo.java`  返回 `HashMap<群ID, 群聊中的消息列表>`
14. `/group/event`   获取群聊中的活动事件，请求类型：`POST`
    * 支持的参数有(JavaScript对象/JSON)：
        * 详见 `GroupEventGetVo.java`  返回一个群聊中的事件列表
15. `/group/hotGroupList`   获取热门群聊列表，请求类型：`GET`
    * 支持的参数有(JavaScript对象/JSON)：
        * 无  返回一个群聊中的事件列表
16. `/group/getGroupUsersByGroupId`   获取一个群聊中的用户列表，请求类型：`POST`
    * 支持的参数有(JavaScript对象/JSON)：
        * 详见 `GroupRelationshipSearchEntity.java`  返回一个群聊中的用户信息列表
17. `/group/getGroupIdentity`   获取一个用户在群聊中的身份类型，请求类型：`POST`
    * 支持的参数有(JavaScript对象/JSON)：
        * 详见 `GroupUserIdentityEntity.java`  返回一个用户在群聊中的身份类，1=群主，2=管理员
18. `/group/getGroupInfoByGroupId`   根据群聊ID获取群聊信息，请求类型：`POST`
    * 支持的参数有(JavaScript对象/JSON)：
        * 详见 `GroupInfoSearchEntity.java`  返回群聊信息
19. `/group/removeUserFromGroup`   从一个群聊中移除一个用户，仅限该群的群主和管理员使用，请求类型：`POST`
    * 支持的参数有(JavaScript对象/JSON)：
        * 详见 `GroupRelationshipEditEntity.java`  返回 void
20. `/group/addNewAdmin`   将一个用户设为某个群的管理员，仅限该群的群主使用，请求类型：`POST`
    * 支持的参数有(JavaScript对象/JSON)：
        * 详见 `GroupUserIdentityEditEntity.java`  返回 void
21. `/group/removeGroupAdmin`   将某个群的一个管理员撤职，仅限该群的群主使用，请求类型：`POST`
    * 支持的参数有(JavaScript对象/JSON)：
        * 详见 `GroupUserIdentityEditEntity.java`  返回 void
22. `/group/exitGroup`   退出群聊，仅限该群的普通成员使用，请求类型：`POST`
    * 支持的参数有(JavaScript对象/JSON)：
        * 详见 `GroupRelationshipEditEntity.java`  返回布尔类型，标识是否退群成功
23. `/group/deleteGroup`   解散群聊，仅限该群的群主使用，请求类型：`POST`
    * 支持的参数有(JavaScript对象/JSON)：
        * 详见 `GroupRelationshipEditEntity.java`  返回布尔类型，标识是否解散成功
24. `/group/avatar`   上传群聊头像，请求类型：`POST`
    * 支持的参数有(JavaScript对象/JSON)：
        * `file` + `fileOrigin` + `userId` + `token`  验证用户token，如果正确则上传头像至阿里oss并返回头像图和头像原图的 URL


25. `/admin/setHotGroup`   设为热门群聊，仅限系统管理员使用，请求类型：`POST`
    * 支持的参数有(JavaScript对象/JSON)：
        * 详见 `HotGroupEntity.java`  返回布尔类型，标识是否设置成功
26. `/admin/removeHotGroup`   取消热门群聊，仅限系统管理员使用，请求类型：`POST`
    * 支持的参数有(JavaScript对象/JSON)：
        * 详见 `HotGroupEntity.java`  返回布尔类型，标识是否取消成功
27. `/admin/getUserInfo`   获取用户信息(包含密码)，仅限系统管理员使用，请求类型：`POST`
    * 支持的参数有(JavaScript对象/JSON)：
        * 详见 `UserInfoAdminEntity.java`  返回包含敏感信息的用户信息
28. `/admin/setUserBaned`   封禁用户，仅限系统管理员使用，请求类型：`POST`
    * 支持的参数有(JavaScript对象/JSON)：
        * 详见 `UserInfoAdminEntity.java`  返回 void
29. `/admin/setUserUnBaned`   解除封禁用户，仅限系统管理员使用，请求类型：`POST`
    * 支持的参数有(JavaScript对象/JSON)：
        * 详见 `UserInfoAdminEntity.java`  返回 void
30. `/admin/removeUser`   删除用户，仅限系统管理员使用，请求类型：`POST`
    * 支持的参数有(JavaScript对象/JSON)：
        * 详见 `UserInfoAdminEntity.java`  返回 void
31. `/admin/editGroupInfo`   编辑群聊信息，仅限系统管理员使用，请求类型：`POST`
    * 支持的参数有(JavaScript对象/JSON)：
        * 详见 `GroupInfoEditEntity.java`  返回 void
32. `/admin/removeByGroupId`   删除群聊，仅限系统管理员使用，请求类型：`POST`
    * 支持的参数有(JavaScript对象/JSON)：
        * 详见 `GroupInfoEditEntity.java`  返回 void
33. `/admin/editGroupEvent`   编辑群聊中的事件，仅限系统管理员使用，请求类型：`POST`
    * 支持的参数有(JavaScript对象/JSON)：
        * 详见 `GroupEventEditEntity.java`  返回 void
34. `/admin/removeByEventId`   删除群聊中的事件，仅限系统管理员使用，请求类型：`POST`
    * 支持的参数有(JavaScript对象/JSON)：
        * 详见 `GroupEventEditEntity.java`  返回 void


35. **[这是一个WebSocket API]** `/websocket/{username}/{token}`  开启全双工通信，请求类型：`GET → WebSocket`， URL类似于: ws://10.10.10.10/xxx
    * 支持的参数有(URL内含式)
        * `username` + `token`  传递username和token，用于打开一个带token的WebSocket会话 (后端接收到token后会进行验证)
          

### 启动方法
1. 根据自己的实际情况修改连接的数据库/阿里云accessKey/百度云apiKey、secretKey (方法：复制 `src/main/resources/application.yml.template` 为 `src/main/resources/application.yml`，然后修改文件中的数据库、redis、ali oss 和 baidu cloud 的配置项为你自己的值 )
2. 打开终端并定位到正确的目录 (pom.xml所在的目录)
3. 在终端中输入下方命令检查Maven是否正确安装，如果没安装清自行安装[maven](https://github.com/apache/maven)
```
mvn -v
```
4. 使用Maven安装java依赖
```
mvn install
```
5. 运行Spring-boot项目
```
mvn spring-boot:run
```

done!
