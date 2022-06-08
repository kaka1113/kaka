package com.ice.framework.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * @author sqw
 * @since 2022/03/02
 */
public enum RoleEnum {

    /**
     * 运营商业务员
     */
    OPERATOR_SALESMAN("OPERATOR_SALESMAN"),
    /**
     * 联合创始人
     */
    CO_FOUNDER("CO_FOUNDER"),
    /**
     * 事业合伙人
     */
    BUSINESS_PARTNER("BUSINESS_PARTNER"),
    /**
     * 创业合伙人
     */
    VENTURE_PARTNER("VENTURE_PARTNER"),
    /**
     * 授权店
     */
    AUTHORIZED_STORE("AUTHORIZED_STORE"),
    /**
     * 特许经营店
     */
    FRANCHISE_STORE("FRANCHISE_STORE"),
    /**
     * 服务商
     */
    SERVICE_PROVIDER("SERVICE_PROVIDER"),
    /**
     * 服务商子账号
     */
    SERVICE_PROVIDER_SUB("SERVICE_PROVIDER_SUB"),
    /**
     * 授权店员工
     */
    AUTHORIZED_STORE_STAFF("AUTHORIZED_STORE_STAFF");

    @EnumValue
    private String name;

    RoleEnum(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
