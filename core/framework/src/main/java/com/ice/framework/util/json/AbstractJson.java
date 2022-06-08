package com.ice.framework.util.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.BeanUtils;

import java.util.List;

/**
 * @author : tjq
 * @since : 2022/1/20 11:24
 */
public abstract class AbstractJson implements IJson {


    @Override
    public <T> T getObject(String pojo, Class<T> tclass) {
        return JSON.parseObject(pojo, tclass);
    }

    @Override
    public void convert(Object source, Object target) {
        BeanUtils.copyProperties(source, target);
    }

    @Override
    public <T> String getJson(T tResponse) {
        return JSONObject.toJSONString(tResponse);
    }

    @Override
    public <T> List<T> jsonToList(String jsonString, Class<T> clazz) {
        return JSON.parseArray(jsonString, clazz);
    }

    @Override
    public <T> String listToJson(List<T> ts) {
        return JSON.toJSONString(ts);
    }

    @Override
    public <T> List<T> copyList(List<?> sourceList, Class<T> targetClass) {
        return JSON.parseArray(JSON.toJSONString(sourceList), targetClass);
    }
}
