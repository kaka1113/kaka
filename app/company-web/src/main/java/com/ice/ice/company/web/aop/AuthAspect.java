package com.ice.ice.company.web.aop;

import com.ice.framework.jwt.JwtUtil;
import com.ice.framework.model.CompanySysUserModel;
import com.ice.framework.response.ResponseModel;
import com.ice.framework.response.ResponseUtil;
import com.mg.sys.api.constants.SysConstant;
import com.mg.sys.api.feign.CompanySysMenuFeign;
import com.mg.sys.api.request.sys.CompanySysMenuIsAccessUriRequest;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * @author sqw
 * @since 2022/06/08
 */
@Aspect
@Component
@Order(0)
public class AuthAspect {

    @Autowired
    private CompanySysMenuFeign companySysMenuFeign;

    /**
     * 登录 访问路径
     */
    private static final String IGNORED_LOGIN = "/login";

    /**
     * 定义切点
     */
    @Pointcut("execution(public * com.mg.mg.company.web.controller.*.*.*(..))")
    public void privilege() {
    }

    /**
     * 权限环绕通知
     *
     * @param joinPoint
     * @throws Throwable
     */
    @ResponseBody
    @Around("privilege()")
    public Object isAccessUriMethod(ProceedingJoinPoint joinPoint) throws Throwable {
        // 获取请求内容
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder
                .getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        String uri = request.getRequestURI();
        //登录直接放行
        if (uri.contains(IGNORED_LOGIN)) {
            return joinPoint.proceed();
        }
        String token = request.getHeader(SysConstant.COMPANY_WEB_TOKEN_KEY);
        if (StringUtils.isBlank(token)) {
            return ResponseUtil.fail("token不能为空");
        }
        CompanySysUserModel companySysUserModel = JwtUtil.validCompanySysUser(token);
        if (null == companySysUserModel || null == companySysUserModel.getId()) {
            return ResponseUtil.fail("token解析失败");
        }
        //校验该token是否可访问该资源
        CompanySysMenuIsAccessUriRequest isAccessUriRequest = new CompanySysMenuIsAccessUriRequest();
        isAccessUriRequest.setUserId(companySysUserModel.getId());
        isAccessUriRequest.setUri(uri);
        ResponseModel responseModel = companySysMenuFeign.isAccessUri(isAccessUriRequest);
        if (responseModel.getSuccess()) {
            return joinPoint.proceed();
        } else {
            return responseModel;
        }
    }

}
