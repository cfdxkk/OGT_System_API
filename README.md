# OGT (Order Game Time - 预定游戏时间)
OGT系统是用来与好友预定游戏时间，或由队长来策划并发布活动的一套系统
此存储库为OGT_System的后端API，由 `Java / Spring-boot` 编写
按计划，系统底层将实现一个 `IM系统`，换句话说，我们的所有数据都是基于会话的，不同于一般的网站(Ajax)，我们的通信将会使用 `Websocket` 协议，因为这个协议是 `全双工` 的。

# 启动方法
1. 根据自己的实际情况修改 `src/main/resources/application.yml` 文件中的 `url` `username` 和 `password` 项如果使用的不是MySQL，则需要修改`driver-class-name` 等字段，当然，你也可以修改其他属性让本系统以更贴合你的需求的方式运行
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
