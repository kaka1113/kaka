package com.mg.framework.util.json;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * @author : tjq
 * @since : 2022/1/20 9:47
 */
public class FastJsonUtil extends AbstractJson {
    private static final Logger log = LoggerFactory.getLogger(FastJsonUtil.class);

    /**
     * POJO 转 JSON
     */
    @Override
    public <T> String getJson(T tResponse) {
        return JSON.toJSONString(tResponse);
    }

    @Override
    public void convert(Object source, Object target) {
        super.convert(source, target);
    }


    /**
     * List<T> 转 json 保存到数据库
     */
    @Override
    public <T> String listToJson(List<T> ts) {
        String jsons = JSON.toJSONString(ts);
        return jsons;
    }

    /**
     * json 转 List<T>
     */
    @Override
    public <T> List<T> jsonToList(String jsonString, Class<T> clazz) {
        List<T> ts = (List<T>) JSONArray.parseArray(jsonString, clazz);
        return ts;
    }

    /**
     * JSON 转 POJO
     */
    @Override
    public <T> T getObject(String pojo, Class<T> tclass) {
        try {
            return JSONObject.parseObject(pojo, tclass);
        } catch (Exception e) {
            log.error(tclass + "转 JSON 失败");
        }
        return null;
    }

    @Override
    public <T> List<T> copyList(List<?> sourceList, Class<T> targetClass) {
        return super.copyList(sourceList, targetClass);
    }

}
