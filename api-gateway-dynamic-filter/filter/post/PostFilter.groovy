import com.netflix.zuul.ZuulFilter
import com.netflix.zuul.context.RequestContext
import com.netflix.zuul.exception.ZuulException
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.servlet.http.HttpServletResponse

class PostFilter extends ZuulFilter {

    Logger log = LoggerFactory.getLogger(PostFilter.class)

    @Override
    String filterType() {
        return "post"
    }

    @Override
    int filterOrder() {
        return 0
    }

    @Override
    boolean shouldFilter() {
        return true
    }

    @Override
    Object run() {
        log.info("post filter: receive response")
        HttpServletResponse response = RequestContext.getCurrentContext().getResponse()
        response.getOutputStream().print("--Dynamic--")
        response.flushBuffer()
    }

}