## OGT (Order Game Time - 预定游戏时间)
OGT系统是用来与好友预定游戏时间，或由队长来策划并发布活动的一套系统
此存储库为OGT_System的后端API，由 `Java → Spring-boot` 编写
系统底层实现一个 `IM系统`，所有数据都是基于消息的，不同于一般的网站(Ajax)，我们的通信将会使用 `Websocket` 协议，因为这个协议是 `全双工` 的。
### API
注意：除了正常的api外，当前版本也提供了几个api接口用于调试
1. `localhost:8080/user/info`   获取数据库中用户的所有信息
     * 请求类型：`GET` - 支持的的属性有：
          * `uuid` 根据用户的ID筛选这个用户的所有信息
          * `空` 获取全部用户的全部信息
2. `localhost:8080/user/register`

### 启动方法
1. 根据自己的实际情况修改 `src/main/resources/application.yml` 文件中的 `url` `username` 和 `password` 项（如果使用的不是MySQL，则需要修改`driver-class-name` 等字段，当然，你也可以修改其他属性让本系统以更贴合你的需求的方式运行）
2. 打开终端并定位到正确的目录 (pom.xml所在的目录)
3. 输入下方命令检查maven是否正确安装，如果没安装清自行安装maven
```
mvn -v
```
4. 安装java依赖
```
mvn install
```
5. 运行Spring-boot项目
```
mvn spring-boot:run
```

done!
