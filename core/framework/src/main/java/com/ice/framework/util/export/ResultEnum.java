package com.ice.framework.util.export;

import java.util.HashMap;
import java.util.Map;

/**
 * @author hubo
 * @since 2020/2/25
 */
public enum ResultEnum {

    /**
     * 操作成功
     */
    SUCCESS("0", "操作成功"),
    /**
     * 操作失败
     */
    FAILURE("-1", "操作失败");

    private String key;
    private String desc;

    ResultEnum(String key, String desc) {
        this.key = key;
        this.desc = desc;
    }

    public static Map<String, String> resultEnum;

    static {
        if (null == resultEnum || resultEnum.isEmpty()) {
            resultEnum = new HashMap<String, String>();
            for (ResultEnum enumT : ResultEnum.values()) {
                resultEnum.put(enumT.getKey(), enumT.getDesc());
            }
        }
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
