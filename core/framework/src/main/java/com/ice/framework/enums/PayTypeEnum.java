package com.ice.framework.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;

/**
 * @author zyk
 * @since 2022/3/8
 */
public enum PayTypeEnum {
    /**
     * 微信支付
     */
    WX_PAY(1, "微信支付"),
    /**
     * 支付宝支付
     */
    ALI_PAY(2, "支付宝支付"),
    /**
     * 线下支付
     */
    OFFLINE_PAY(3, "线下支付"),
    /**
     * 银行卡支付
     */
    CARD(4, "银行卡支付"),
    /**
     * 其他
     */
    OTHER(7, "其他");

    @EnumValue
    private final int code;

    private final String name;

    PayTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
