Spring Cloud Bus 采用了 Spring 的事件驱动模型。

* 事件驱动模型

  Spring 的事件驱动模型中包含了三个基本概念：事件、事件监听者和事件发布者。

  * 事件

    Spring 中定义了事件的抽象类 ApplicationEvent，它继承自 JDK 的 EventObject 类。

  * 事件监听者

    Spring 中定义了事件监听者的接口 ApplicationListener，它继承自 JDK 的 EventListener 接口，同时 ApplicationListener 接口限定了 ApplicationEvent 子类作为该接口中 onApplicationEvent(E event) 函数的参数。所以，每一个 ApplicationListener 都是针对某个 ApplicationEvent 子类的监听和处理者。

  * 事件发布者

    Spring 中定义了 ApplicationEventPublisher 和 ApplicationEventMulticaster（在 Spring 的默认实现为 SimpleApplicationEventMulticaster） 两个接口用来发布事件。其中 ApplicationEventPublisher  接口定义了发布事件的函数 publishEvent(ApplicationEvent event) 和 publishEvent(Object event)；而 ApplicationEventMulticaster 接口中定义了对 ApplicationListener 的维护操作以及将 ApplicationEvent 多播给可用 ApplicationListener 的操作。

* Spring Cloud Bus 事件

  * RemoteApplicationEvent（抽象类）
  * RefreshRemoteApplicationEvent（该事件用于远程刷新应用的配置信息）
  * AckRemoteApplicationEvent（该事件用于告知某个事件消息已经被接受，通过该消息我们可以监控各个事件消息的响应）
  * EnvironmentChangeRemoteApplicationEvent（该事件用于动态更新消息总线上每个节点的 Spring 环境属性）
  * SentApplicationEvent

* Spring Cloud Bus 事件监听器

  * RefreshListener（针对 RefreshRemoteApplicationEvent）
  * EnvironmentChangeListener（针对 EnvironmentChangeRemoteApplicationEvent）

* Spring Cloud Bus 事件跟踪

  * TraceListener（针对 AckRemoteApplicationEvent 和 SentApplicationEvent）

* Spring Cloud Bus 事件发布

  @StreamListener(SpringCloudBusClient.INPUT) acceptRemote(RemoteApplicationEvent event)

  通过 acceptRemote 方法来监听消息代理的输入通道，并根据事件类型和配置内容来确定是否要发布事件。

* Spring Cloud Bus 控制端点

  * RefreshBusEndpoint

    在请求处理部分直接调用了父类中的 publish 函数将 RefreshRemoteApplicationEvent 事件发布出来，实现在总线上发布消息的功能。

  * EnvironmentBusEndpoint

    触发环境参数更新的消息总线控制。