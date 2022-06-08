package com.ice.framework.exception;

import com.ice.framework.response.ResponseErrorCodeEnum;
import com.ice.framework.response.ResponseModel;
import com.ice.framework.response.ResponseUtil;
import com.ice.framework.util.SnowflakeUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLSyntaxErrorException;
import java.util.Map;
import java.util.TreeMap;

/**
 * @author hubo
 * @since 2020/8/19
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理参数校验异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public ResponseModel handleValidException(MethodArgumentNotValidException e) {
        long errorId = SnowflakeUtil.id();
        log.error("参数校验异常：{}，异常ID：{}，异常类型：{}", e.getMessage(), errorId, e.getClass());
        BindingResult bindingResult = e.getBindingResult();
        Map<String, String> errorMap = new TreeMap<>();
        bindingResult.getFieldErrors().forEach((fieldError) -> {
            errorMap.put(fieldError.getField(), fieldError.getDefaultMessage());
        });
        return ResponseUtil.fail(ResponseErrorCodeEnum.VALID_EXCEPTION.getCode(), "（" + errorId + "）" + ResponseErrorCodeEnum.VALID_EXCEPTION.getMsg(), errorMap);
    }

    /**
     * 数据库参数校验错误
     * @param e
     * @return
     */
    @ExceptionHandler(value = SQLSyntaxErrorException.class)
    public ResponseModel sqlException(SQLSyntaxErrorException e) {
        long errorId = SnowflakeUtil.id();
        log.error("：{}，异常ID：{}，异常类型：{}", e.getMessage(), errorId, e.getClass());

        String errorMsg = "（" + errorId + "）" + ResponseErrorCodeEnum.DB_ERROR.getMsg();
        log.error(errorMsg, e.getCause());
        return ResponseUtil.fail(ResponseErrorCodeEnum.DB_ERROR.getCode(), errorMsg);
    }

    /**
     * 处理系统异常
     *
     * @param throwable
     * @return
     */
    @ExceptionHandler(value = Throwable.class)
    public ResponseModel handleException(Throwable throwable) {
        long errorId = SnowflakeUtil.id();
        String errorMsg = "（" + errorId + "）" + ResponseErrorCodeEnum.UNKNOWN_EXCEPTION.getMsg();
        log.error(errorMsg, throwable);
        return ResponseUtil.fail(ResponseErrorCodeEnum.UNKNOWN_EXCEPTION.getCode(), errorMsg);
    }

    /**
     * 处理自定义异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(value = MgException.class)
    public ResponseModel handleCustomException(MgException e) {
        return ResponseUtil.fail(e.getErrCode(), e.getErrMsg());
    }


}
