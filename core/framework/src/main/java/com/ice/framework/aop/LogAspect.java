package com.ice.framework.aop;

import com.alibaba.fastjson.JSON;
import com.ice.framework.annotation.IgnoreLogAspect;
import com.ice.framework.util.CommonUtil;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author SL
 * @since 2020/4/9
 */
@Aspect
@Component
public class LogAspect {

    private static Logger logger = LoggerFactory.getLogger(LogAspect.class);

    private static String IGNORE = "unknown", POST_METHOD = "POST";
    /**
     * 开始时间
     */
    private long startTimeMillis = 0;
    /**
     * 请求地址
     */
    private String requestUrl = "";
    /**
     * 请求参数
     */
    private String params = "";
    /**
     * 远程地址
     */
    private String remoteAddr = "";
    /**
     * 方法名
     */
    private String methodName = "";

    @Pointcut("@within(com.ice.framework.annotation.LogAspect)")
    public void init() {
    }

    @Before("init()")
    public void doBefore(JoinPoint joinPoint) {
        startTimeMillis = System.currentTimeMillis();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        IgnoreLogAspect ignoreLogAspect = signature.getMethod().getAnnotation(IgnoreLogAspect.class);
        if (ignoreLogAspect != null) {
            String typeName = joinPoint.getSignature().getDeclaringTypeName();
            String name = signature.getMethod().getName();
            logger.info(">>>>>>>>>log忽略打印请求入参,{}.{}>>>>>>>>", typeName, name);
            return;
        }
        try {
            // 接收到请求，记录请求内容
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                    .getRequestAttributes();
            HttpServletRequest request = attributes.getRequest();

            methodName = joinPoint.getSignature().getName();
            requestUrl = request.getRequestURI();
            remoteAddr = CommonUtil.getIpAddr(request);

            String method = request.getMethod();
            params = "";
            if (POST_METHOD.equals(method)) {
                Object[] paramsArray = joinPoint.getArgs();
                params = argsArrayToString(paramsArray);
            } else {
                params = request.getQueryString();
            }

            logger.info("uri= {}; remoteAddr= {}; methodName= {}; params: {}; ", requestUrl, remoteAddr, method, params);
        } catch (Exception e) {
            logger.error("***操作请求日志记录失败doBefore()***", e);
        }
    }

    @AfterReturning(returning = "result", pointcut = "init()")
    public void doAfterReturning(Object result) {
        long endTime = System.currentTimeMillis();
        String responseStauts = result != null ? result.toString().length() > 100 ? result.toString().substring(0, 100) : result.toString() : "";
        logger.info("uri= {}; pro_time= {}ms; {}", requestUrl, endTime - startTimeMillis, responseStauts);
    }

    /**
     * 请求参数拼装
     *
     * @param paramsArray
     * @return
     */
    private String argsArrayToString(Object[] paramsArray) {
        String params = "";
        if (paramsArray != null && paramsArray.length > 0) {
            for (int i = 0; i < paramsArray.length; i++) {
                Object param = paramsArray[i];
                String jsonStr = param != null ? toJSONStr(param) : null;
                params += jsonStr + " ";
            }
        }
        return params.trim();
    }

    private String toJSONStr(Object object) {
        String result = "";
        try {

            result = JSON.toJSONString(object);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return result;
    }
}

