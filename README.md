# spring-cloud-learned

### Spring Cloud Eureka

* 构建高可用的服务注册中心集群

  * hosts 

    127.0.0.1 peer1

    127.0.0.1 peer2

  * eureka-server

    --spring.profiles.active=peer1

    --spring.profiles.active=peer2

  * hello-service

    java -jar 启动多个实例

  * ribbon-consumer

    java -jar 启动单个实例，Postman 发送请求 http://localhost:9000/ribbon-consumer

* 构建高可用的跨区域容错集群

  * hosts

    127.0.0.1 eureka-server-hk-tko

    127.0.0.1 eureka-server-hk-skm

  * eureka-server

    --spring.profiles.active=tko

    --spring.profiles.active=skm

  * hello-service

    --spring.profiles.active=tko

    --spring.profiles.active=skm

    java -jar 启动多个实例

  * ribbon-consumer

    --spring.profiles.active=tko
    
    java -jar 启动单个实例，Postman 发送请求 http://localhost:9000/ribbon-consumer

### Spring Cloud Config

* URL

  * Config Client 端创建 RESTful 接口来返回配置中心的 from 属性

    GET http://localhost:7002/from

  * 查看加密功能状态

    GET http://localhost:7001/encrypt/status

    Basic Auth: Username - user, Password - 02795da9-8f86-4b22-aeec-7351a8bde965

  * 加密

    POST http://localhost:7001/encrypt

    Body: (raw) 02795da9-8f86-4b22-aeec-7351a8bde965

  * 动态刷新配置

    POST http://localhost:7002/actuator/refresh

### Spring Cloud Hystrix

* Hystrix Dashboard

  http://localhost:2001/hystrix

* 单体应用监控

  在 Hystrix Dashboard 的首页输入 http://localhost:9000/actuator/hystrix.stream

* 默认的集群监控

  在 Hystrix Dashboard 的首页输入 http://localhost:8989/turbine.stream

* 指定的集群监控

  在 Hystrix Dashboard 的首页输入 http://localhost:8989/turbine.stream?cluster=default