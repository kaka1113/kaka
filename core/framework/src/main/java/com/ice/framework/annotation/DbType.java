package com.ice.framework.annotation;

import com.ice.framework.component.db.DataSourceKey;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author tjq
 * @since 2022/5/17 16:18
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DbType {

    DataSourceKey dbType() default DataSourceKey.SLAVER;
}
