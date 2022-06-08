package com.mg.framework.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * @author : tjq
 * @version V1.0
 * @Project: mos-framework
 * @Package com.gateon.mos.framework.util
 * @Description: 判断字符串、对象、集合、map是否为空
 * @since Date : 2021年06月15日 10:12
 */
public class ObjectUtils {


    /**
     * 判断object是否为空,集合会校验size
     */
    public static boolean isNull(Object... objs) {
        for (Object obj : objs) {
            if (ObjectUtils.isEmpty(obj)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 判断object是否不为空,集合会校验size
     */
    public static boolean isNotNull(Object... obj) {
        return !ObjectUtils.isNull(obj);
    }

    /**
     * 对象非空判断
     */
    public static boolean isNotEmpty(Object obj) {
        return !ObjectUtils.isEmpty(obj);
    }

    /**
     * 对象空判断
     */
    public static boolean isEmpty(Object obj) {
        if (obj == null) {
            return true;
        }
        if (obj.getClass().isArray()) {
            return Array.getLength(obj) == 0;
        }
        if (obj instanceof CharSequence) {
            return ((CharSequence) obj).length() == 0;
        }
        if (obj instanceof Collection) {
            return ((Collection<?>) obj).isEmpty();
        }
        if (obj instanceof Map) {
            return ((Map<?, ?>) obj).isEmpty();
        }
        // else
        return false;
    }

    /**
     * 将对象转为字符串（解决localDateTime事件序列化带T的情况）
     *
     * @param object
     * @return
     * @throws JsonProcessingException
     */
    public static String toJsonString(Object object) throws JsonProcessingException {
        JavaTimeModule module = new JavaTimeModule();
        // 序列化器
        module.addSerializer(LocalDateTime.class,
                new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        // 反序列化器
        // 这里添加的是自定义的反序列化器
        module.addDeserializer(LocalDateTime.class,
                new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(module);
        return mapper.writeValueAsString(object);
    }

    public static boolean equals(Object object1, Object object2) {
        if (object1 == object2) {
            return true;
        } else {
            return object1 != null && object2 != null && object1.equals(object2);
        }
    }

    public static boolean notEqual(Object object1, Object object2) {
        return !equals(object1, object2);
    }

    /**
     * 数组转字符串
     *
     * @param list
     * @param separator
     * @return
     */
    public String join(List list, char separator) {
        return StringUtils.join(list.toArray(), separator);
    }

    /**
     * 驼峰转下滑线
     *
     * 参考hutools
     * @param str StrUtil.toUnderlineCase(name)
     * @return str
     */
    public static String toUnderlineCase(CharSequence str) {
        return toSymbolCase(str, '_');
    }


    public static String toSymbolCase(CharSequence str, char symbol) {
        if (str == null) {
            return null;
        } else {
            int length = str.length();
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < length; ++i) {
                char c = str.charAt(i);
                if (Character.isUpperCase(c)) {
                    Character preChar = i > 0 ? str.charAt(i - 1) : null;
                    Character nextChar = i < str.length() - 1 ? str.charAt(i + 1) : null;
                    if (null != preChar) {
                        if (symbol == preChar) {
                            if (null == nextChar || Character.isLowerCase(nextChar)) {
                                c = Character.toLowerCase(c);
                            }
                        } else if (Character.isLowerCase(preChar)) {
                            sb.append(symbol);
                            if (null == nextChar || Character.isLowerCase(nextChar) || isNumber(nextChar)) {
                                c = Character.toLowerCase(c);
                            }
                        } else if (null != nextChar && Character.isLowerCase(nextChar)) {
                            sb.append(symbol);
                            c = Character.toLowerCase(c);
                        }
                    } else if (null == nextChar || Character.isLowerCase(nextChar)) {
                        c = Character.toLowerCase(c);
                    }
                }

                sb.append(c);
            }

            return sb.toString();
        }
    }


    public static boolean isNumber(char ch) {
        return ch >= '0' && ch <= '9';
    }
}
