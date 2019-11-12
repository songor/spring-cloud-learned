# Spring Cloud Eureka

### 致谢

《Spring Cloud 微服务实战》

### 源码分析

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

### 配置详解

* 服务注册类配置

  org.springframework.cloud.netflix.eureka.EurekaClientConfigBean

  这些配置信息都以 eureka.client 为前缀

  * 指定注册中心（eureka.client.serviceUrl）

    ```java
    private Map<String, String> serviceUrl = new HashMap<>();
    {
    	this.serviceUrl.put("defaultZone", "http://localhost:8761/eureka/");
    }
    ```

  * 其他配置

    | 参数名                                        | 说明                                                         | 默认值 |
    | :-------------------------------------------- | ------------------------------------------------------------ | ------ |
    | enabled                                       | Flag to indicate that the Eureka client is enabled.          | true   |
    | registryFetchIntervalSeconds                  | Indicates how often(in seconds) to fetch the registry information from the eureka server. | 30     |
    | instanceInfoReplicationIntervalSeconds        | Indicates how often(in seconds) to replicate instance changes to be replicated to the eureka server. | 30     |
    | initialInstanceInfoReplicationIntervalSeconds | Indicates how long initially (in seconds) to replicate instance info to the eureka server. | 40     |
    | registerWithEureka                            | Indicates whether or not this instance should register its information with eureka server for discovery by others. In some cases, you do not want your instances to be discovered whereas you just want do discover other instances. | true   |
    | preferSameZoneEureka                          | Indicates whether or not this instance should try to use the eureka server in the same zone for latency and/or other reason. Ideally eureka clients are configured to talk to servers in the same zone. The changes are effective at runtime at the next registry fetch cycle as specified by registryFetchIntervalSeconds. | true   |
    | filterOnlyUpInstances                         | Indicates whether to get the applications after filtering the applications for instances with only InstanceStatus UP states. | true   |
    | fetchRegistry                                 | Indicates whether this client should fetch eureka registry information from eureka server. | true   |

* 服务实例类配置

  org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean

  这些配置信息都以 eureka.instance 为前缀

  * 元数据

    服务实例的元数据是 Eureka 客户端在向服务注册中心发送注册请求时，用来描述自身服务信息的对象，其中包含了一些标准化的元数据，比如服务名称、实例名称、实例 IP、实例端口等用于服务治理的重要信息；以及一些用于负载均衡策略或是其他特殊用途的自定义元数据信息。

    原生 Eureka 对元数据的定义 com.netflix.appinfo.InstanceInfo

    我们可以通过 eureka.instance.\<properties\>=\<value\> 的格式对标准化元数据直接进行配置。而对于自定义元数据，可以通过 eureka.instance.metadataMap.\<key\>=\<value\> 的格式来进行配置。

  * 实例名配置

    实例名，即 InstanceInfo 中的 instanceId 参数，它是区分同一服务中不同实例的唯一标识。在 Netflix Eureka 的原生实现中，实例名采用主机名作为默认值，这样的设置使得在同一主机上无法启动多个相同的服务实例。所以，在 Spring Cloud Eureka 的配置中，针对同一主机中无法启动多实例的情况，对实例名的默认命名做了更为合理的扩展，它采用了如下默认规则：${spring.cloud.client.hostname}:${spring.application.name}:${spring.application.instance_id:${server.port}}}

    对于实例名的命名规则，我们可以通过 eureka.instance.instanceId 参数来进行配置。

    实际上，我们可以直接通过设置 server.port=0 或者使用随机数 server.port=${random.int[10000,19999]} 来让 Tomcat 启动的时候采用随机端口。但是这个时候我们会发现注册到 Eureka Server 的实例名都是相同的，这会使得只有一个服务实例能够正常提供服务（eureka.instance.instance-id=${spring.application.name}:${random.uuid}）。

  * 端点配置

    homePageUrl、statusPageUrl、healthCheckUrl 分别代表了应用主页的 URL、状态页的 URL、健康检查的 URL。其中，状态页和健康检查的 URL 在 Spring Cloud Eureka 中默认使用了 spring-boot-actuator 模块提供的 /info 和 /health 端点。

    使用相对路径来进行配置：eureka.instance.statusPageUrlPath、eureka.instance.healthCheckUrlPath

    由于 Eureka 的服务注册中心默认会以 HTTP 的方式来访问和暴露这些端点，因此当客户端应用以 HTTPS 的方式来暴露服务和监控端点时，相对路径的配置方式就无法满足需求了。所以 Spring Cloud Eureka 还提供了绝对路径的配置参数：eureka.instance.statusPageUrl、eureka.instance.healthCheckUrl

  * 健康检测

    默认情况下，Eureka 中各个服务实例的健康检测并不是通过 spring-boot-actuator 模块的 /health 端点来实现的，而是依靠客户端心跳的方式来保持服务实例的存活。

    默认的心跳实现方式可以有效检查客户端进程是否正常运作，但却无法保证客户端应用能够正常提供服务。由于大多数微服务应用都会有一些其他的外部资源依赖，比如数据库、缓存、消息代理等，如果我们的应用与这些外部资源无法联通的时候，实际上已经不能提供正常的对外服务了，但是因为客户端心跳依然在运行，所以它还是会被服务消费者调用，而这样的调用实际上并不能获得预期的结果。

  * 其他配置

    | 参数名                           | 说明                                                         | 默认值 |
    | -------------------------------- | ------------------------------------------------------------ | ------ |
    | preferIpAddress                  | Flag to say that, when guessing a hostname, the IP address of the server should be used in prference to the hostname reported by the OS. | false  |
    | leaseRenewalIntervalInSeconds    | Indicates how often (in seconds) the eureka client needs to send heartbeats to eureka server to indicate that it is still alive. If the heartbeats are not received for the period specified in leaseExpirationDurationInSeconds, eureka server will remove the instance from its view, there by disallowing traffic to this instance. | 30     |
    | leaseExpirationDurationInSeconds | Indicates the time in seconds that the eureka server waits since it received the  last heartbeat before it can remove this instance from its view and there by disallowing traffic to this instance. Setting this value too long could mean that the traffic could be routed to the instance even though the instance is not alive. Setting this value too small could mean, the instance may be taken out of traffic because of temporary network glitches. This value to be set to at least higher than the value specified in leaseRenewalIntervalInSeconds. | 90     |