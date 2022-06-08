package com.ice.framework.util.json;

import java.util.List;

/**
 * @author : tjq
 * @since : 2022/1/20 9:56
 */
public interface IJson {

    /**
     * 对象转JSON
     *
     * @param tResponse 返回字符串
     * @param <T>       传入对象
     * @return
     */
    <T> String getJson(T tResponse);

    /**
     * 数组转JSON
     *
     * @param ts  传入数组
     * @param <T> 泛型T
     * @return
     */
    <T> String listToJson(List<T> ts);


    /**
     * JSON转对象
     *
     * @param pojo
     * @param tclass
     * @param <T>
     * @return
     */
    <T> T getObject(String pojo, Class<T> tclass);


    /**
     * JSON转数组
     *
     * @param jsonString
     * @param clazz
     * @param <T>
     * @return
     */
    <T> List<T> jsonToList(String jsonString, Class<T> clazz);


    /**
     * 对象转对象
     *
     * @param source 源对象
     * @param target 目标对象
     */
    void convert(Object source, Object target);


    /**
     * 数组拷贝
     *
     * @param sourceList
     * @param targetClass
     * @param <T>
     * @return
     */
    <T> List<T> copyList(List<?> sourceList, Class<T> targetClass);

}
