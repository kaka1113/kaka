package com.mg.framework.jwt;

import cn.hutool.json.JSONObject;
import cn.hutool.jwt.JWT;
import cn.hutool.jwt.JWTUtil;
import com.mg.framework.exception.MgException;
import com.mg.framework.model.CompanySysUserModel;
import com.mg.framework.model.CustomerModel;
import com.mg.framework.model.ResultModel;
import com.mg.framework.model.SmartBUserModel;
import com.mg.framework.response.ResponseErrorCodeEnum;
import com.mg.framework.util.HttpUtil;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Map;

/**
 * @author hubo
 * @since 2022/2/14
 */
@Component
public class JwtUtil {

    /**
     * 创建一个32-byte的密匙
     */
    private static final byte[] SECRET = "HBBigPersonHBTaoistPriestHBRealP".getBytes();

    private static final String mosBaseUrlKey = "mos.base-url";

    private static String mosBaseUrl;

    @Resource
    private Environment environment;

    @PostConstruct
    public void init() {
        mosBaseUrl = environment.getProperty(mosBaseUrlKey);
    }

    /**
     * 生成token
     *
     * @param payloadMap
     * @return
     */
    public static String creatToken(Map<String, Object> payloadMap) {
        return JWTUtil.createToken(payloadMap, SECRET);
    }

    /**
     * 验证是否有效的token，并且返回企业用户模型对象
     *
     * @param token
     * @return
     */
    public static CompanySysUserModel validCompanySysUser(String token) {
        JWT jwt = JWTUtil.parseToken(token);
        boolean verifyKey = jwt.setKey(SECRET).verify();
        if (!verifyKey) {
            throw new MgException(ResponseErrorCodeEnum.API_ERROR.getCode(), "无效的token");
        }
        JSONObject jsonObject = jwt.getPayload().getClaimsJson();
        CompanySysUserModel companySysUserModel = com.alibaba.fastjson.JSONObject.parseObject(jsonObject.toString(), CompanySysUserModel.class);
        return companySysUserModel;
    }

    public static SmartBUserModel validSmartBUser(String token) {
        JWT jwt = JWTUtil.parseToken(token);
        boolean verifyKey = jwt.setKey(SECRET).verify();
        if (!verifyKey) {
            throw new MgException(ResponseErrorCodeEnum.API_ERROR.getCode(), "无效的token");
        }
        JSONObject jsonObject = jwt.getPayload().getClaimsJson();
        SmartBUserModel smartBUserModel = com.alibaba.fastjson.JSONObject.parseObject(jsonObject.toString(), SmartBUserModel.class);
        return smartBUserModel;
    }

    public static CustomerModel validCustomerUser(String token) {
        String url = mosBaseUrl + "/mos/2c-mw/customer/parsingCToken";
        String resultStr = "";
        try {
            resultStr = HttpUtil.sendGet(url, "C-Token", token);
        } catch (Exception e) {
            e.printStackTrace();
        }
        ResultModel resultModel = com.alibaba.fastjson.JSONObject.parseObject(resultStr, ResultModel.class);
        if (resultModel.getCode() == -1) {
            throw new MgException(resultModel.getCode(), "token解析失败" + resultModel.getMessage());
        }
        CustomerModel customerModel = com.alibaba.fastjson.JSONObject.parseObject(resultModel.getData().toString(), CustomerModel.class);
        return customerModel;
    }
}
