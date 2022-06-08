package com.ice.ice.company.web.mvc;


import com.ice.framework.exception.MgException;
import com.ice.framework.jwt.JwtUtil;
import com.ice.framework.model.CompanySysUserModel;
import com.ice.framework.response.ResponseErrorCodeEnum;
import com.mg.sys.api.constants.SysConstant;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * @author sqw
 * @since 2022/06/08
 */
@Configuration
@ResponseBody
public class UserModelArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return CompanySysUserModel.class.equals(methodParameter.getParameterType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        String token = webRequest.getHeader(SysConstant.COMPANY_WEB_TOKEN_KEY);
        if (StringUtils.isBlank(token)) {
            return null;
        }
        return validCompanySysUser(token);

    }

    private Object validCompanySysUser(String token) {
        CompanySysUserModel companySysUserModel = JwtUtil.validCompanySysUser(token);
        if (null == companySysUserModel) {
            throw new MgException(ResponseErrorCodeEnum.API_ERROR.getCode(), "token解析失败");
        } else if (null == companySysUserModel.getCompanyId()) {
            throw new MgException(ResponseErrorCodeEnum.API_ERROR.getCode(), "运营商企业ID为空");
        }
        return companySysUserModel;
    }

}
