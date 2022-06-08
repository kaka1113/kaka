package com.mg.framework.lock;

import com.mg.framework.util.MyStringUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.CodeSignature;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Author: qiang.su
 * Date: 2021/5/26
 * Msg: 分布式注解解析入参值到lock key
 */
public class AspectDisLockKeyParser {

    /**
     * 解析缓存的key
     *
     * @param proceedingJoinPoint 切面
     * @param rawKey              : a:b:{user.id}:{sku.id}
     * @return String
     * @throws IllegalAccessException 异常
     */
    public static String parse(ProceedingJoinPoint proceedingJoinPoint, String rawKey) throws IllegalAccessException {
        String realKey = rawKey;
        // 解析实际参数的key
        List<String> fromPatter = MyStringUtil.getFromPatter(rawKey, "\\{.*?\\}");

        for (String key :
                fromPatter) {
            String cleanKey = key.replace("{", "").replace("}", "");
            StringTokenizer stringTokenizer = new StringTokenizer(cleanKey, ".");

            Map<String, Object> nameAndValue = getNameAndValue(proceedingJoinPoint);
            Object actualKey = null;

            while (stringTokenizer.hasMoreTokens()) {
                if (actualKey == null) {
                    actualKey = nameAndValue.get(stringTokenizer.nextToken());
                } else {
                    actualKey = getPropValue(actualKey, stringTokenizer.nextToken());
                }
            }

            String replaced = actualKey.toString();
            realKey = realKey.replace(key, replaced);
        }

        return realKey;
    }

    public static void main(String[] args) {
        List<String> fromPatter = MyStringUtil.getFromPatter("rawKey:{1}:{2}", "\\{.*?\\}");
        for (String key :
                fromPatter) {
            System.out.println(key);
        }
    }


    /**
     * 获取参数Map集合
     *
     * @param joinPoint 切面
     * @return Map<String, Object>
     */
    private static Map<String, Object> getNameAndValue(ProceedingJoinPoint joinPoint) {
        Object[] paramValues = joinPoint.getArgs();
        String[] paramNames = ((CodeSignature) joinPoint.getSignature()).getParameterNames();
        Map<String, Object> param = new HashMap<>(paramNames.length);

        for (int i = 0; i < paramNames.length; i++) {
            param.put(paramNames[i], paramValues[i]);
        }
        return param;
    }

    /**
     * 获取指定参数名的参数值
     *
     * @param obj
     * @param propName
     * @return
     * @throws IllegalAccessException
     */
    public static Object getPropValue(Object obj, String propName) throws IllegalAccessException {
        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field f : fields) {
            if (f.getName().equals(propName)) {
                //在反射时能访问私有变量
                f.setAccessible(true);
                return f.get(obj);
            }
        }
        return recursionSuperPropValue(obj, propName);
    }

    private static Object recursionSuperPropValue(Object obj, String propName) throws IllegalAccessException {
        Class<?> superclass = obj.getClass().getSuperclass();
        if (null != superclass) {
            Field[] fields = superclass.getDeclaredFields();
            for (Field f : fields) {
                if (f.getName().equals(propName)) {
                    //在反射时能访问私有变量
                    f.setAccessible(true);
                    return f.get(obj);
                }
            }
            return recursionSuperPropValue(superclass, propName);
        } else {
            return null;
        }
    }

}
