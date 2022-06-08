package com.ice.framework.aop;

import com.ice.framework.annotation.DbType;
import com.ice.framework.component.db.DynamicDataSourceContextHolder;
import groovy.util.logging.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * @author tjq
 * @since 2022/5/17 16:20
 */
@Slf4j
@Aspect
@Component
public class DBTypeAspect {

    private Logger log = LoggerFactory.getLogger(DBTypeAspect.class);

    @Around("@annotation(dbType)")
    public Object setRead(ProceedingJoinPoint joinPoint, DbType dbType) throws Throwable {
        try {
            DynamicDataSourceContextHolder.setDataSourceKey(dbType.dbType());
            log.info("已经切换为{}执行", dbType.dbType());
            return joinPoint.proceed();
        } finally {
            //一方面为了避免内存泄漏，更重要的是避免对后续在本线程上执行的操作产生影响
            DynamicDataSourceContextHolder.clearDataSourceKey();
            log.info("切面数据源{}已经清除", dbType.dbType());
        }
    }

}
