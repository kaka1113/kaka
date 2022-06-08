package com.ice.framework.aop;

import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import com.ice.framework.model.ApiEntity;
import com.ice.framework.model.FrameworkRockMqConstant;
import com.ice.framework.response.ResponseModel;
import com.ice.framework.component.tracing.TracingContext;
import com.ice.framework.util.ObjectUtils;
import com.ice.framework.component.mq.RocketMqSendUtil;
import com.ice.framework.util.json.JsonFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * User: tjq
 * Date: 2021/9/1 14:55
 * Description: 异常传递及日志打印
 */
@Aspect
@Component
public class AfterReturnAspect {
    private Logger logger = LoggerFactory.getLogger(AfterReturnAspect.class);

    @Resource
    private RocketMqSendUtil rocketMqSendUtil;

    @Value("${spring.application.name:null}")
    private String serviceName;

    @Pointcut("@annotation(org.springframework.web.bind.annotation.PostMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.GetMapping)  || " +
            "@annotation(org.springframework.web.bind.annotation.PutMapping)  || " +
            "@annotation(org.springframework.web.bind.annotation.RequestMapping)")
    public void resp() {
    }

    @AfterReturning(returning = "response", pointcut = "resp()")
    public void doAfterReturning(JoinPoint joinPoint, Object response) {
        Signature signature = joinPoint.getSignature();
        String methodName = signature.getName();
        String className = joinPoint.getSignature().getDeclaringType().getName();
        String forceLog = MDC.get(TracingContext.FORCE_LOG);
        //获取开发者信息
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        ApiOperationSupport apiOperationSupport = methodSignature.getMethod().getAnnotation(ApiOperationSupport.class);
        if (ObjectUtils.isNotEmpty(response) && response instanceof ResponseModel) {
            Object[] args = joinPoint.getArgs();
            String params = argsArrayToString(args);
            //1.有强制打印请求响应日志时
            //2.当响应model为正常响应时,避免与下面异常打印日志重复
            if (ObjectUtils.isNotEmpty(forceLog) && ((ResponseModel) response).getSuccess() == Boolean.TRUE) {
                logger.warn("请求类名：{},请求方法：{}，xid:{},响应Msg：{},请求参数：{}", className, methodName, ((ResponseModel) response).getTracingXid(),
                        ((ResponseModel) response).getMessage(), params);
            }
            //异常打印日志
            if (((ResponseModel) response).getSuccess() == Boolean.FALSE) {
                logger.warn("请求类名：{},请求方法：{}，xid:{},异常Msg：{},请求参数：{}", className, methodName, ((ResponseModel) response).getTracingXid(), ((ResponseModel) response).getMessage(), params);
                //新增接口调用失败通知开发者功能
                if (ObjectUtils.isNotEmpty(apiOperationSupport) && ObjectUtils.isNotEmpty(apiOperationSupport.author())) {
                    String author = apiOperationSupport.author();
                    String message = ((ResponseModel) response).getMessage();
                    ApiEntity apiEntity = ApiEntity.builder().author(author).order(apiOperationSupport.order()).serviceName(serviceName).className(className).
                            methodName(methodName).params(ObjectUtils.isNotEmpty(params) ? params : "无参数").errorMsg(ObjectUtils.isNotEmpty(message) ? message : "无消息")
                            .tracingXid(((ResponseModel) response).getTracingXid()).build();
                    logger.info("异常消息投递：{}", JsonFactory.jsonTools().getJson(apiEntity));
                    rocketMqSendUtil.sendDelayMsg(FrameworkRockMqConstant.MG_FRAMEWORK_SERVICE_REQUEST_ERROR_LOG_TOPIC,
                            JsonFactory.jsonTools().getJson(apiEntity), 2);
                }
            }
        }
    }


    private String argsArrayToString(Object[] paramsArray) {
        String params = "";
        try {
            if (paramsArray != null && paramsArray.length > 0) {
                for (int i = 0; i < paramsArray.length; i++) {
                    Object param = paramsArray[i];
                    String jsonStr = param != null ? toJSONStr(param) : null;
                    params += jsonStr + " ";
                }
            }
        } catch (Exception ex) {
            logger.warn(ex.getMessage());
        }
        return params.trim();
    }

    private String toJSONStr(Object object) {
        String result = "";
        try {
            result = JsonFactory.jsonTools().getJson(object);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return result;
    }
}
