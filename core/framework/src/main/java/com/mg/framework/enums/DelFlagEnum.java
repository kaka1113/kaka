package com.mg.framework.enums;

/**
 * @author hubo
 * @since 2020/2/25
 */
public enum DelFlagEnum {

    /**
     * 未删除
     */
    FLAG_0(0, "未删除"),
    /**
     * 已删除
     */
    FLAG_1(1, "已删除");

    private Integer key;
    private String value;

    DelFlagEnum(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

    public Integer getKey() {
        return key;
    }

    public void setKey(Integer key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
