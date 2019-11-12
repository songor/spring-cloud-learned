# Spring Cloud Eureka

### 致谢

《Spring Cloud 微服务实战》

### Eureka 详解

* 服务治理机制

  * 服务提供者

    * 服务注册

      “服务提供者”在启动的时候会通过发送 REST 请求的方式将自己注册到 Eureka Server 上，同时带上了自身服务的一些元数据信息。Eureka Server 接收到这个 REST 请求之后，将元数据信息存储在一个双层结构 Map 中，其中第一层的 key 是服务名，第二层的 key 是具体服务的实例名。在服务注册时，需要确认一下 eureka.client.register-with-eureka=true 参数是否正确，该值默认为 true。若设置为 false，将不会启动注册操作。

    * 服务同步

      由于服务注册中心之间因互相注册为服务，当服务提供者发送注册请求到一个服务注册中心时，它会将该请求转发给集群中相连的其他注册中心，从而实现注册中心之间的服务同步。

    * 服务续约

      在注册完服务之后，服务提供者会维护一个心跳用来持续告诉 Eureka Server “我还活着”，以防止 Eureka Server 的“剔除任务”将该服务实例从服务列表中排除出去。

      eureka.instance.lease-renewal-interval-in-seconds=30 参数用于定义服务续约任务的调用间隔时间

      eureka.instance.lease-expiration-duration-in-seconds=90 参数用于定义服务失效的时间

  * 服务消费者

    * 获取服务

      当我们启动服务消费者的时候，它会发送一个 REST 请求给服务注册中心，来获取上面注册的服务清单。

      获取服务是服务消费者的基础，所以必须确保 eureka.client.fetch-registry=true，该值默认为 true。若希望修改缓存清单的更新时间，可以通过 eureka.client.registry-fetch-interval-seconds=30 参数进行修改。

    * 服务调用

      服务消费者在获取服务清单后，通过服务名可以获得具体提供服务的实例名和该实例的元数据信息。因为有这些服务实例的详细信息，所以客户端可以根据自己的需要决定具体调用哪个实例，在 Ribbon 中会默认采用轮询的方式进行调用，从而实现客户端的负载均衡。

    * 服务下线

      在客户端程序中，当服务实例进行正常的关闭操作时，它会触发一个服务下线的 REST 请求给 Eureka Server，告诉服务注册中心“我要下线了”。服务端在接收到请求之后，将该服务状态置为 DOWN，并把该下线事件广播出去。

  * 服务注册中心

    * 失效剔除

      有些时候，我们的服务实例并不一定会正常下线，可能由于内存溢出、网络故障等原因使得服务不能正常工作，而服务注册中心并未收到“服务下线”的请求。为了从服务列表中将这些无法提供服务的实例剔除，Eureka Server 在启动的时候会创建一个定时任务，默认每隔 60 秒将当前清单中超时（默认为 90 秒）没有续约的服务剔除出去。

    * 自我保护

      Eureka Server 在运行期间，会统计心跳失败的比例在 15 分钟之内是否低于 85%，如果出现低于的情况，Eureka Server 会将当前的实例注册信息保护起来，让这些实例不会过期，尽可能保护这些注册信息。但是，在这段保护期内实例若出现问题，那么客户端很容易拿到实际已经不存在的服务实例，会出现调用失败的情况，所以客户端必须要有容错机制，比如可以使用请求重试、断路器等机制。

      由于本地调试很容易触发注册中心的保护机制，这会使得注册中心维护的服务实例不那么准确。所以，我们在本地进行开发的时候，可以使用 eureka.server.enable-self-preservation=false 参数来关闭保护机制，以确保注册中心将不可用的实例正确剔除。

* 源码分析

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