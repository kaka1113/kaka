/*
 * Copyright (C) 2017 Zhejiang BYCDAO Technology CO.,LTD.
 * All rights reserved.
 * Official Web Site: http://www.bycdao.com.
 * Developer Web Site: http://open.bycdao.com.
 */

package com.mg.framework.response;

import com.mg.framework.exception.MgException;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.MDC;

/**
 * @author hubo
 * @since 2021/8/19
 */
@Setter
@Getter
public class ResponseModel<T> {

    @ApiModelProperty(value = "是否成功 true - 成功    false - 失败")
    private Boolean success = true;

    @ApiModelProperty(value = "错误编码")
    private Integer errCode;

    @ApiModelProperty(value = "返回信息")
    private String message;

    @ApiModelProperty(value = "返回对象")
    private T data;

    @ApiModelProperty(value = "服务名称")
    private String serviceName;

    @ApiModelProperty(value = "链路id")
    private String tracingXid;

    /**
     * @param flag 这个参数必须要有，否则aop拦截会出问题
     * @return
     */
    public T getDataThrowException(boolean flag) {
        if (flag && this.success == Boolean.FALSE) {
            throw new MgException(this.getErrCode(), this.getMessage());
        }
        return data;
    }

    public String getTracingXid() {
        if (ObjectUtils.isNotEmpty(MDC.getMDCAdapter())) {
            return MDC.get("TRACING_XID");
        }
        return tracingXid;
    }

}
