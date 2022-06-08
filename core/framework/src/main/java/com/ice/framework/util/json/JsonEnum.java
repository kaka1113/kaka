package com.ice.framework.util.json;

import com.baomidou.mybatisplus.annotation.EnumValue;
import lombok.Getter;

/**
 * @author : tjq
 * @since : 2022/1/20 9:45
 */
@Getter
public enum JsonEnum {

    FAST("FAST_JSON", 10),
    SPRING("SPRING_BEAN_JSON", 20),
    JACKSON("JACKSON", 30),
    FRAMEWORK("FRAMEWORK_JSON", 40),
    ;

    private String name;

    @EnumValue
    private Integer code;

    JsonEnum(String name, Integer code) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static JsonEnum codeOf(Integer status) {
        JsonEnum[] values = JsonEnum.values();
        for (JsonEnum item : values) {
            if (item.getCode().equals(status)) {
                return item;
            }
        }
        return null;
    }
}
