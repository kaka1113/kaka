package com.ice.framework.enums;

import com.ice.framework.util.ObjectUtils;

/**
 * @author tjq
 * @since 2022/5/18 8:58
 */
public enum ProjectEnum {

    MG("MG", "MG中台服务"),
    MOS("MOS", "MOS总部服务"),

    ;

    private String key;
    private String value;

    ProjectEnum(String key, String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public static ProjectEnum keyOf(String key) {
        if (ObjectUtils.isEmpty(key)) {
            return null;
        }
        ProjectEnum[] values = ProjectEnum.values();
        for (ProjectEnum item : values) {
            if (item.getKey().equalsIgnoreCase(key)) {
                return item;
            }
        }
        return null;
    }

}
