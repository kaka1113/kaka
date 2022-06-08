package com.ice.framework.enums;

/**
 * @author hubo
 * @since 2020/2/25
 */
public enum WhetherEnum {

    /**
     * 否
     */
    WHETHER_0(0, "否"),
    /**
     * 是
     */
    WHETHER_1(1, "是");

    private Integer key;
    private String value;

    WhetherEnum(Integer key, String value) {
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
