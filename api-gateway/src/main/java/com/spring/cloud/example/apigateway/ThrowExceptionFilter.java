package com.spring.cloud.example.apigateway;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.exception.ZuulException;
import lombok.extern.java.Log;

@Log
public class ThrowExceptionFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return "post";
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        log.info("pre filter throw RuntimeException");
        throw new RuntimeException("An error occurred");
    }

}
