package com.mg.framework.util.json;

import java.util.List;

/**
 * @author : tjq
 * @since : 2022/1/20 9:54
 */
public class JacksonUtil extends AbstractJson {
    @Override
    public <T> T getObject(String pojo, Class<T> tclass) {
        return super.getObject(pojo,tclass);
    }

    @Override
    public <T> String listToJson(List<T> ts) {
        return super.listToJson(ts);
    }

    @Override
    public <T> List<T> jsonToList(String jsonString, Class<T> clazz) {
        return super.jsonToList(jsonString,clazz);
    }

    @Override
    public <T> String getJson(T tResponse) {
        return super.getJson(tResponse);
    }

    @Override
    public void convert(Object source, Object target) {
        super.convert(source,target);
    }

    @Override
    public <T> List<T> copyList(List<?> sourceList, Class<T> targetClass) {
        return super.copyList(sourceList, targetClass);
    }
}
