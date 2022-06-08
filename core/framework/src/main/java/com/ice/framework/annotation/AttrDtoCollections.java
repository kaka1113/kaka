package com.ice.framework.annotation;

import java.lang.annotation.*;

/**
 * @author hubo
 * @since 2020/2/25
 */
@Inherited
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface AttrDtoCollections {
    public Class value();
}
