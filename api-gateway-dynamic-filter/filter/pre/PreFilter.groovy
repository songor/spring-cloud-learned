import com.netflix.zuul.ZuulFilter
import com.netflix.zuul.context.RequestContext
import com.netflix.zuul.exception.ZuulException
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import javax.servlet.http.HttpServletRequest

class PreFilter extends ZuulFilter {

    Logger log = LoggerFactory.getLogger(PreFilter.class)

    @Override
    String filterType() {
        return "pre"
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
    Object run() throws ZuulException {
        HttpServletRequest request = RequestContext.getCurrentContext().getRequest()
        log.info("pre filter: send {} request to {}", request.getMethod(), request.getRequestURL().toString())
        return null
    }

}