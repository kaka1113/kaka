package com.mg.framework.response;

/**
 * @author hubo
 * @since 2021/8/19
 */
public enum ResponseErrorCodeEnum {

    /***
     * 错误码和错误信息定义类
     * 1. 错误码定义规则为5位数字
     * 2. 前两位表示业务场景，最后三位表示错误码。例如：10001。10:系统 001:系统未知异常
     * 3. 维护错误码后需要维护错误描述，将他们定义为枚举形式
     * 错误码列表：
     * 10: 系统服务错误
     * 001：参数格式校验
     * ...
     * 11: 运营商企业WEB端服务
     */

    NODATA_EXCEPTION(10000, "未查询到相关数据"),
    UNKNOWN_EXCEPTION(10001, "系统未知异常"),
    VALID_EXCEPTION(10002, "参数格式校验失败"),
    API_ERROR(10003, "接口调用异常"),
    TO_MANY_REQUEST(10004, "请求流量过大"),
    REDIS_LOCK_ERROR(10005, "分布式锁异常"),
    DB_ERROR(10006, "数据库校验异常"),
    MQ_ERROR(10007, "消息队列异常"),
    LOGIN_INVALID(11000, "登录失效，请重新登录");


    private int code;
    private String msg;

    ResponseErrorCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
