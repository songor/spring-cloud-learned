# spring-cloud-learned

### 致谢

《Spring Cloud 微服务实战》

### Spring Cloud Eureka

* Region、Zone

  * 源码

    EndpointUtils.getServiceUrlsMapFromConfig()

    EndpointUtils.getRegion()

    EurekaClientConfigBean.getAvailabilityZones()

  * 注释

    一个微服务应用只可以属于一个 Region，如果不特别配置，默认为 default。若我们要自己设置，可以通过 eureka.client.region 属性来定义。

    当我们没有特别为 Region 配置 Zone 的时候，将默认采用 defaultZone，这也是我们之前配置参数 eureka.client.serviceUrl.defaultZone 的由来。若要为应用指定 Zone，可以通过 eureka.client.availability-zones 属性来进行设置。Zone 能够设置多个，并且通过逗号分隔来配置。由此，我们可以判断 Region 和 Zone 是一对多的关系。

* serviceUrls

  * 源码

    EurekaClientConfigBean.getEurekaServerServiceUrls()

  * 注释

    eureka.client.serviceUrl.defaultZone 属性可以配置多个，并且需要通过逗号分隔。

    当我们在微服务应用中使用 Ribbon 来实现服务调用时，对于 Zone 的设置可以在负载均衡时实现区域亲和特性：Ribbon 的默认策略会优先访问同客户端处于一个 Zone 中的服务端实例，只有当同一个 Zone 中没有可用服务端实例的时候才会访问其他 Zone 中的实例。

* 服务注册

  * 源码

    DiscoveryClient.initScheduledTasks()

    if (clientConfig.shouldRegisterWithEureka())

  * 注释

    我们能看到发起注册请求的时候，传入了一个 com.netflix.appinfo.InstanceInfo 对象，该对象就是注册时客户端给服务端的服务的元数据。

* 服务获取与服务续约

  * 源码

    DiscoveryClient.initScheduledTasks()

    if (clientConfig.shouldFetchRegistry())

    if (clientConfig.shouldRegisterWithEureka()) { scheduler.schedule() }

  * 注释

    “服务获取”任务相对于“服务续约”和“服务注册”任务更为独立。“服务续约”与“服务注册”在同一个 if 逻辑中，这个不难理解，服务注册到 Eureka Server 后，自然需要一个心跳（HeartbeatThread）去续约，防止被剔除，所以它们肯定是成对出现的。

* 服务注册中心处理

  * 源码

    InstanceRegistry.register() { publishEvent(new EurekaInstanceRegisteredEvent()); super.register(); }

  * 注释

    注册中心存储了两层 Map 结构（ConcurrentHashMap），第一层的 key 存储服务名：InstanceInfo 中的 appName 属性，第二层的 key 存储实例名：InstanceInfo 中的 instanceId 属性。