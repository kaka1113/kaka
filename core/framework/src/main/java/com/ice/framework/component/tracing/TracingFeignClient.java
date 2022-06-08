package com.ice.framework.component.tracing;

import com.ice.framework.util.ObjectUtils;
import feign.Client;
import feign.Request;
import feign.Response;
import org.slf4j.MDC;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

public class TracingFeignClient implements Client {

    private final Client delegate;

    private final BeanFactory beanFactory;

    private static final int MAP_SIZE = 16;

    public TracingFeignClient(BeanFactory beanFactory, Client delegate) {
        this.delegate = delegate;
        this.beanFactory = beanFactory;
    }

    @Override
    public Response execute(Request request, Request.Options options) throws IOException {
        Request modifiedRequest = getModifyRequest(request);
        return this.delegate.execute(modifiedRequest, options);
    }

    private Request getModifyRequest(Request request) {

        //链路传递
        String tracingXid = MDC.get(TracingContext.TRACING_XID);

        if (StringUtils.isEmpty(tracingXid)) {
            return request;
        }

        Map<String, Collection<String>> headers = new HashMap<>(MAP_SIZE);
        headers.putAll(request.headers());

        List<String> tracingXidList = new ArrayList<>();
        tracingXidList.add(tracingXid);
        headers.put(TracingContext.TRACING_XID, tracingXidList);

        //强制打印日志传递
        String forceLog = MDC.get(TracingContext.FORCE_LOG);
        if (ObjectUtils.isNotEmpty(forceLog)) {
            List<String> forceLogList = new ArrayList<>();
            tracingXidList.add(forceLog);
            headers.put(TracingContext.FORCE_LOG, forceLogList);
        }

        return Request.create(request.method(), request.url(), headers, request.body(),
                request.charset());
    }
}
