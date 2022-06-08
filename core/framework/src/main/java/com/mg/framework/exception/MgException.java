package com.mg.framework.exception;

import lombok.Getter;
import lombok.Setter;

/**
 * @author hubo
 * @since 2021/8/19
 */
@Setter
@Getter
public class MgException extends RuntimeException {

    /**
     * 错误编码
     */
    private Integer errCode;
    /**
     * 错误信息
     */
    private String errMsg;

    public MgException(int errCode, String errMsg) {
        this.errCode = errCode;
        this.errMsg = errMsg;
    }
}
