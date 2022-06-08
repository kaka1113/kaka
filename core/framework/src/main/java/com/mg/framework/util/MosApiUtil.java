package com.mg.framework.util;

import com.alibaba.fastjson.JSONObject;
import com.mg.framework.response.ResponseModel;
import com.mg.framework.response.ResponseUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;

/**
 * @author HB
 * @since 2021/11/11
 */
@Slf4j
@Component
public class MosApiUtil {

    private static final String DEFAULT_MOS_API_URL_KEY = "mos.api.url";
    private static String DEFAULT_MOS_API_URL;
    @Autowired
    private Environment environment;

    /**
     * 如果application.yml配置了mos.api.url,可以用此方法调用中台get接口
     *
     * @param urlGet url后缀
     * @return ResponseModel
     */
    public static ResponseModel sendGet(String urlGet) {
        if (StringUtils.isEmpty(DEFAULT_MOS_API_URL)) {
            ResponseUtil.fail("variable[mos.api.url] is not set!");
        }
        return sendGet(DEFAULT_MOS_API_URL, urlGet);
    }

    /**
     * 如果application.yml配置了mos.api.url,可以用此方法调用中台post接口
     *
     * @param urlPost url后缀
     * @return ResponseModel
     */
    public static ResponseModel sendPost(String urlPost, Object param) {
        if (StringUtils.isEmpty(DEFAULT_MOS_API_URL)) {
            ResponseUtil.fail("variable[mos.api.url] is not set!");
        }
        return sendPost(DEFAULT_MOS_API_URL, urlPost, param);
    }

    /**
     * 调用中台get接口
     *
     * @param mosApiUrl 中台接口路径
     * @param urlGet       url后缀
     * @return ResponseModel
     */
    public static ResponseModel sendGet(String mosApiUrl, String urlGet) {
        String url = mosApiUrl + urlGet;
        String getResult = null;
        try {
            getResult = HttpUtil.sendGet(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JSONObject.parseObject(getResult, ResponseModel.class);
    }

    /**
     * 调用中台post接口
     *
     * @param mosApiUrl 中台接口路径
     * @param urlPost      url后缀
     * @return ResponseModel
     */
    public static ResponseModel sendPost(String mosApiUrl, String urlPost, Object param) {
        String url = mosApiUrl + urlPost;
        String res = null;
        try {
            res = HttpUtil.sendPost(url, param);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return JSONObject.parseObject(res, ResponseModel.class);
    }

    @PostConstruct
    public void init() {
        DEFAULT_MOS_API_URL = environment.getProperty(DEFAULT_MOS_API_URL_KEY);
        log.info("DEFAULT_MOS_API_URL : {}", DEFAULT_MOS_API_URL);
    }
}
