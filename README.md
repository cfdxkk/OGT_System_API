## OGT (Order Game Time - 预定游戏时间)
OGT系统是用来与好友预定游戏时间，或由队长来策划并发布活动的一套系统，是我在东软的毕设
***
此存储库为 OGT_System 的后端API，由 `Java -> Spring-boot` 编写，未来有由 `Node.js` 重构的计划;
</br>
系统底层类似于 `IM系统`，所有数据都基于消息，不同于一般的网站使用的http请求(Ajax)，我们的消息通信将会使用 `Websocket` 协议，因为这个协议是 `全双工` 的。
### API
注意：除了正常的api外，当前版本也提供了几个用于调试的api
1. [调试] `localhost:8080/user/info`   获取数据库中用户的所有信息，请求类型：`GET`
    * 支持的的参数有(URL载荷)：
        * `uuid` 根据用户的ID筛选这个用户的所有信息
        * `空` 获取全部用户的全部信息
2. `/user/register`   用户注册，请求类型：`POST`
    * 支持的参数有(JavaScript对象/JSON)：
        * `username` + `password`  将会注册这个用户，返回注册结果 (username不允许重复)
3. `/user/login`   用户登录，请求类型：`POST`
    * 支持的参数有(JavaScript对象/JSON)：
        * `username` + `password`  如果是注册用户且密码正确，则会返回token，如果不是则返回用户名或密码错误
4.  `/websocket/{username}/{token}`  开启全双工通信，请求类型：`GET → WebSocket`
    * 支持的参数有(URL内含式)
        * `username` + `token`  传递username和token，用于打开一个带token的WebSocket会话 (后端接收到token后会进行验证)
5.  `/message/messagefilterandcluster`  用于全双工通信的发送方向服务器发送消息，请求类型：`POST`
    * 支持的参数有(JavaScript对象/JSON):
        * `uuidfrom` + `uuidto` + `uunoto` + `token` + `messagetype` + `message`  向 `uuidto` 所指向用户推送一条 `messagetype` 类型的消息
        * `uuidfrom` + `uuidto` + `token` + `messagetype` + `message`  向 `uuidto` 所指向用户推送一条 `messagetype` 类型的消息
        * `uuidfrom` + `uunoto` + `token` + `messagetype` + `message`  向 `uunoto` 所指向用户推送一条 `messagetype` 类型的消息
          

### 启动方法
1. 根据自己的实际情况修改连接的数据库，方法：修改在 `src/main/resources/application.yml` 文件中的 `url` `username` 和 `password`参数（如果使用的不是MySQL，则需要修改`driver-class-name` 参数，当然，你也可以修改其他属性让本系统以更贴合你的实际需求的方式运行）
2. 打开终端并定位到正确的目录 (pom.xml所在的目录)
3. 输入下方命令检查Maven是否正确安装，如果没安装清自行安装[maven](https://github.com/apache/maven)
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
