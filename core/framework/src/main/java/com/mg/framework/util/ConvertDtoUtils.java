package com.mg.framework.util;


import com.mg.framework.annotation.AttrDtoCollections;
import com.mg.framework.annotation.AttrDtoConvertSkip;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author hubo
 * @since 2020/2/25
 */
public class ConvertDtoUtils {

    /**
     * 单个entity copy到dto
     *
     * @param source
     * @param target
     * @throws Exception
     */
    public static void convert(Object source, Object target) {
        try {
            Class sourceClazz = queryClazz(source);
            Class targetClazz = queryClazz(target);
            //1.获取各种的字段 放到map
            Field[] sourceFields = sourceClazz.getDeclaredFields();
            Field[] targetFields = targetClazz.getDeclaredFields();
            //对target父类对象做处理
            Field[] targetSupperFields = new Field[0];
            for (Class<?> clazz = targetClazz.getSuperclass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
                targetSupperFields = clazz.getDeclaredFields();
                if (targetSupperFields.length > 0) {
                    targetFields = (Field[]) ArrayUtils.addAll(targetFields, targetSupperFields);
                }
            }

            if (sourceFields.length == 0 || targetFields.length == 0) {
                return;
            }

            Map<String, Field> sourceFieldMap = new HashMap<String, Field>(256);
            for (int i = 0; i < sourceFields.length; i++) {
                sourceFieldMap.put(sourceFields[i].getName(), sourceFields[i]);
            }
            Map<String, Field> targetFieldMap = new HashMap<String, Field>(256);
            for (int i = 0; i < targetFields.length; i++) {
                targetFieldMap.put(targetFields[i].getName(), targetFields[i]);
            }

            //*处理带自定义注解的属性的copy
            List<String> skiped = new ArrayList<String>();

            doAnnotationConvert(skiped, sourceFieldMap, targetFieldMap, source, target);

            //处理常规属性copy
            String[] skipedAttrs = skiped.toArray(new String[]{});
            BeanUtils.copyProperties(source, target, skipedAttrs);
        } catch (Exception e) {
            throw new RuntimeException("ConvertDtoUtils.convert Error!", e);
        }
    }

    /**
     * 主要是被cglib代理对象的处理
     * class com.web.call.entity.CallVisitInfo$$EnhancerByCGLIB$$54985de
     *
     * @return
     */
    private static Class queryClazz(Object obj) throws Exception {
        String className = obj.getClass().getName();
        String enhancerByCgLib = "EnhancerByCGLIB";
        if (className.contains(enhancerByCgLib)) {
            className = className.substring(0, className.indexOf("$$EnhancerByCGLIB"));
            return Class.forName(className);
        } else {
            return obj.getClass();
        }

    }

    private static void doAnnotationConvert(List<String> skiped, Map<String, Field> sourceFieldMap,
                                            Map<String, Field> targetFieldMap, Object source, Object target) throws Exception {
        Class sourceClazz = queryClazz(source);
        //遍历source字段
        Set<Map.Entry<String, Field>> sourceFieldentries = sourceFieldMap.entrySet();

        for (Map.Entry<String, Field> sourceFieldKv :
                sourceFieldentries) {
            String sourceFiledName = sourceFieldKv.getKey();
            Field sourceFiled = sourceFieldKv.getValue();
            if (!sourceFiled.isAccessible()) {
                sourceFiled.setAccessible(true);
            }
            //如果有这些注解 就skip
            AttrDtoConvertSkip skipAnno = sourceFiled.getAnnotation(AttrDtoConvertSkip.class);
            if (null != skipAnno) {
                skiped.add(sourceFiledName);
            }

            //解析list类型的参数，转换成Dto
            AttrDtoCollections collectionDtoAnno = sourceFiled.getAnnotation(AttrDtoCollections.class);
            if (null == collectionDtoAnno) {
                continue;
            }
            //带@AttrDtoCollections注解的 ，下面会处理，不在beanutils copy给target
            skiped.add(sourceFiledName);

            Class dtoClazz = collectionDtoAnno.value();

            //1.判断 获取标记注解的字段名对应target里的同名字段的字段类型 如果存在 或者 source的字段类型和target的不一样 报错
            Class sourceFiledType = sourceFiled.getType();
            Field targetFiled = targetFieldMap.get(sourceFiledName);
            //可以允许不对应，不处理
            if (null == targetFiled) {
                continue;
            }
            if (!targetFiled.isAccessible()) {
                targetFiled.setAccessible(true);
            }
            Class targetFiledType = targetFiled.getType();


            //获取source该对象的值
            //mybatis懒加载 用sourceFiled取不到数据。需要先调get方法。
            dealLazyLoadField(source, sourceClazz, sourceFiledName);
            Object souceFieldvalue = sourceFiled.get(source);
            if (null == souceFieldvalue) {
                continue;
            }
            //如果是Type extend collection的子类（一般一对多）
            if (Collection.class.isAssignableFrom(sourceFiledType)) {

                if (sourceFiledType != List.class) {
                    throw new RuntimeException("ConvertDtoUtils.convert Error. @AttrDtoCollections 对应的属性如果是Collection子类，必须是List类型！类：" + sourceClazz.getName()
                            + " , 属性：" + sourceFiledName);
                }
                if (sourceFiledType != targetFiledType) {
                    throw new RuntimeException("ConvertDtoUtils.convert Error. @AttrDtoCollections 对应的属性，两边数据类型必须一致！类：" + sourceClazz.getName()
                            + " , 属性：" + sourceFiledName);
                }

                List souceFieldvalueC = (List) souceFieldvalue;
                if (souceFieldvalueC.size() == 0) {
                    continue;
                }

                //初始化给target的字段的赋值
                List targetList = new ArrayList();

                for (Object souceItem :
                        souceFieldvalueC) {
                    Object targetDtoValue = dtoClazz.newInstance();
                    convert(souceItem, targetDtoValue);
                    targetList.add(targetDtoValue);

                }
                targetFiled.set(target, targetList);

            } else {
                //属性值是单一类（一般一对一 或者 多对一）
                Object targetDtoValue = dtoClazz.newInstance();
                try {
                    convert(souceFieldvalue, targetDtoValue);
                    targetFiled.set(target, targetDtoValue);
                } catch (Exception e) {
                    throw new RuntimeException("ConvertDtoUtils.convert Error. @AttrDtoCollections 对应的属性处理时发生异常！类：" + sourceClazz.getName()
                            + " , 属性：" + sourceFiledName, e);
                }
            }
        }
    }


    /**
     * 将list封装的entity 转换成 list<dto>
     *
     * @param entityList
     * @param dtoClazz
     */
    public static List convertList(List entityList, Class dtoClazz) {
        List dtoList = new ArrayList();
        try {
            for (Object entity :
                    entityList) {
                Object dtoObj = dtoClazz.newInstance();
                convert(entity, dtoObj);
                dtoList.add(dtoObj);
            }
        } catch (Exception e) {
            throw new RuntimeException("entity转换dto失败！", e);
        }
        return dtoList;
    }

    private static void dealLazyLoadField(Object source, Class sourceClazz, String sourceFiledName) throws Exception {
        Method getMethod = sourceClazz.getDeclaredMethod("get" + sourceFiledName.toUpperCase().substring(0, 1) + sourceFiledName.substring(1, sourceFiledName.length()));
        getMethod.invoke(source);
    }

}
