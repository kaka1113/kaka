package com.ice.framework.component.tracing;

import com.ice.framework.util.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;


public class TracingContextIntercept extends HandlerInterceptorAdapter {

    private Logger logger = LoggerFactory.getLogger(TracingContextIntercept.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String tracingXid = request.getHeader(TracingContext.TRACING_XID);
        if (tracingXid == null) {
            tracingXid = UUID.randomUUID().toString().replace("-", "");
            logger.info("链路id生成：{}", tracingXid);
        }
        MDC.put(TracingContext.TRACING_XID, tracingXid);

        //添加强制打印请求响应变量
        String forceLog = request.getHeader(TracingContext.FORCE_LOG);
        if (ObjectUtils.isNotEmpty(forceLog)) {
            MDC.put(TracingContext.FORCE_LOG, forceLog);
        }

        logger.info("请求地址：[{}],forceLog:{} 绑定：[{}] to RootContext", request.getRequestURL(),forceLog, tracingXid);
        return super.preHandle(request, response, handler);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String tracingXid = MDC.get(TracingContext.TRACING_XID);
        if (ObjectUtils.isNotEmpty(tracingXid)) {
            MDC.remove(TracingContext.TRACING_XID);
        }

        //清除feign失败的服务名称
        String serviceName = MDC.get(TracingContext.SERVICE_NAME);
        if (ObjectUtils.isNotEmpty(serviceName)) {
            MDC.remove(TracingContext.SERVICE_NAME);
        }

        //清除强制打印请求响应变量
        String forceLog = MDC.get(TracingContext.FORCE_LOG);
        if (ObjectUtils.isNotEmpty(forceLog)) {
            MDC.remove(TracingContext.FORCE_LOG);
        }

        logger.info("执行后 TRACING_XID:{}", tracingXid);
        super.afterCompletion(request, response, handler, ex);
    }
}
