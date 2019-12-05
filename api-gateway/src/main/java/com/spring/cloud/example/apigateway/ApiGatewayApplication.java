package com.spring.cloud.example.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.netflix.zuul.filters.discovery.PatternServiceRouteMapper;
import org.springframework.context.annotation.Bean;

/**
 * API 网关：
 * 它作为系统的统一入口，屏蔽了系统内部各个微服务的细节
 * 它可以与服务治理框架结合，实现自动化的服务实例维护以及负载均衡的路由转发
 * 它可以实现接口权限校验与微服务业务逻辑的解耦
 * 通过服务网关中的过滤器，在各生命周期中去校验请求的内容，将原本在对外服务层做的校验前移，保证了微服务的无状态性，
 * 同时降低了微服务的测试难度，让服务本身更集中关注业务逻辑的处理
 * <p>
 * Zuul：
 * 它包含了对请求的路由和过滤两个功能，其中路由功能负责将外部请求转发到具体的微服务实例上，是实现外部访问统一入口的基础；
 * 而过滤器功能则负责对请求的处理过程进行干预，是实现请求校验、服务聚合等功能的基础。
 * 路由功能在真正运行时，它的路由映射和请求转发都是由几个不同的过滤器完成的。其中，路由映射主要通过 pre 类型的过滤器完成，
 * 它将请求路径与配置的路由规则进行匹配，以找到需要转发的目标地址；而请求转发的部分则是由 route 类型的过滤器来完成，
 * 对 pre 类型过滤器获得的路由地址进行转发。
 * <p>
 * Zuul：
 * 它包含了对请求的路由和过滤两个功能，其中路由功能负责将外部请求转发到具体的微服务实例上，是实现外部访问统一入口的基础；
 * 而过滤器功能则负责对请求的处理过程进行干预，是实现请求校验、服务聚合等功能的基础。
 * 路由功能在真正运行时，它的路由映射和请求转发都是由几个不同的过滤器完成的。其中，路由映射主要通过 pre 类型的过滤器完成，
 * 它将请求路径与配置的路由规则进行匹配，以找到需要转发的目标地址；而请求转发的部分则是由 route 类型的过滤器来完成，
 * 对 pre 类型过滤器获得的路由地址进行转发。
 * <p>
 * Zuul：
 * 它包含了对请求的路由和过滤两个功能，其中路由功能负责将外部请求转发到具体的微服务实例上，是实现外部访问统一入口的基础；
 * 而过滤器功能则负责对请求的处理过程进行干预，是实现请求校验、服务聚合等功能的基础。
 * 路由功能在真正运行时，它的路由映射和请求转发都是由几个不同的过滤器完成的。其中，路由映射主要通过 pre 类型的过滤器完成，
 * 它将请求路径与配置的路由规则进行匹配，以找到需要转发的目标地址；而请求转发的部分则是由 route 类型的过滤器来完成，
 * 对 pre 类型过滤器获得的路由地址进行转发。
 */

/**
 * Zuul：
 * 它包含了对请求的路由和过滤两个功能，其中路由功能负责将外部请求转发到具体的微服务实例上，是实现外部访问统一入口的基础；
 * 而过滤器功能则负责对请求的处理过程进行干预，是实现请求校验、服务聚合等功能的基础。
 * 路由功能在真正运行时，它的路由映射和请求转发都是由几个不同的过滤器完成的。其中，路由映射主要通过 pre 类型的过滤器完成，
 * 它将请求路径与配置的路由规则进行匹配，以找到需要转发的目标地址；而请求转发的部分则是由 route 类型的过滤器来完成，
 * 对 pre 类型过滤器获得的路由地址进行转发。
 */

/**
 * 核心过滤器：
 * <b>pre 过滤器</b>
 * （1）ServletDetectionFilter
 * 该过滤器总是会被执行，主要用来检测当前请求是通过 Spring 的 DispatcherServlet 处理运行的，还是通过 ZuulServlet 来处理运行的。
 * 它的检测结果会以布尔类型保存在当前请求上下文的 isDispatcherServletRequest 中。
 * （2）Servlet30WrapperFilter
 * 将原始的 HttpServletRequest 包装成 Servlet30RequestWrapper 对象。
 * （3）FormBodyWrapperFilter
 * Content-Type 为 application/x-www-form-urlencoded 的请求；Content-Type 为 multipart/form-data 并且是由 Spring 的 DispatcherServlet 处理的请求。
 * 将符合要求的请求体包装成 FormBodyRequestWrapper 对象。
 * （4）DebugFilter
 * 根据配置参数 zuul.debug.request 和请求中的 debug 参数决定是否执行过滤器中的操作。
 * 将当前请求上下文中的 debugRouting 和 debugRequest 参数设置为 true。
 * （5）PreDecorationFilter
 * 判断当前请求上下文中是否存在 forward.to 和 serviceId 参数，如果都不存在，那么它就会执行具体过滤器的操作。
 * 为当前请求做一些预处理。
 * <b>route 过滤器</b>
 * （1）RibbonRoutingFilter
 * 对请求上下文中存在 serviceId 参数的请求进行处理，即只对通过 serviceId 配置路由规则的请求生效。
 * （2）SimpleHostRoutingFilter
 * 对请求上下文中存在 routeHost 参数的请求进行处理，即只对通过 url 配置路由规则的请求生效。
 * （3）SendForwardFilter
 * 对请求上下文中存在 forward.to 参数的请求进行处理，即用来处理路由规则中的 forward 本地跳转配置。
 * <b>post 过滤器</b>
 * （1）SendResponseFilter
 * context.getThrowable() == null && (!context.getZuulResponseHeaders().isEmpty() || context.getResponseDataStream() != null || context.getResponseBody() != null);
 * 利用请求上下文的响应信息来组织需要发送回客户端的响应内容。
 * <b>error 过滤器</b>
 * （1）SendErrorFilter
 * 组织一个 forward 到 /error 端点的请求来获取错误响应
 * ctx.getThrowable() != null && !ctx.getBoolean("sendErrorFilter.ran", false);
 * request.setAttribute("javax.servlet.error.status_code", exception.getStatusCode());
 * request.setAttribute("javax.servlet.error.exception", exception.getThrowable());
 * request.setAttribute("javax.servlet.error.message", exception.getErrorCause());
 */
@EnableEurekaClient
@EnableZuulProxy
@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    @Bean
    public AccessTokenFilter accessTokenFilter() {
        return new AccessTokenFilter();
    }

    /**
     * 自定义路由映射规则
     * 为不同版本的微服务应用生成以版本号作为路由前缀定义的路由规则，通过这样具有版本号前缀的 URL 路径，
     * 我们就可以很容易地通过路径表达式来归类和管理这些具有版本信息的微服务了
     * 例如：helloservice-v1 -> /v1/helloservice/**
     */
    @Bean
    public PatternServiceRouteMapper patternServiceRouteMapper() {
        return new PatternServiceRouteMapper(
                "(?<name>^.+)-(?<version>v.+$)",
                "${version}/${name}");
    }

    @Bean
    public ThrowExceptionFilter throwExceptionFilter() {
        return new ThrowExceptionFilter();
    }

    @Bean
    public DefaultErrorAttributes customErrorAttributes() {
        return new CustomErrorAttributes();
    }

}
