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