package com.mg.framework.config;

import com.alibaba.csp.sentinel.adapter.spring.webmvc.callback.BlockExceptionHandler;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.mg.framework.response.ResponseErrorCodeEnum;
import com.mg.framework.response.ResponseModel;
import org.springframework.context.annotation.Configuration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author hubo
 * @since 2021/12/23
 */
@Configuration
public class SentinelConfig implements BlockExceptionHandler {

    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, BlockException e) throws Exception {
        ResponseModel responseModel = new ResponseModel();
        responseModel.setSuccess(false);
        responseModel.setErrCode(ResponseErrorCodeEnum.TO_MANY_REQUEST.getCode());
        responseModel.setMessage(ResponseErrorCodeEnum.TO_MANY_REQUEST.getMsg());
        httpServletResponse.setCharacterEncoding("UTF-8");
        httpServletResponse.setContentType("application/json");
        httpServletResponse.getWriter().write(JSON.toJSONString(responseModel));
    }
}

